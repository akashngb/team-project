package data_access;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import entity.User;
import entity.UserFactory;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.mindrot.jbcrypt.BCrypt;
import use_case.change_password.ChangePasswordUserDataAccessInterface;
import use_case.login.LoginUserDataAccessInterface;
import use_case.logout.LogoutUserDataAccessInterface;
import use_case.signup.SignupUserDataAccessInterface;

import java.util.UUID;

/**
 * MongoDB implementation of the user data access object.
 * Stores users in MongoDB with hashed passwords for security.
 */
public class MongoDBUserDataAccessObject implements SignupUserDataAccessInterface,
                                                     LoginUserDataAccessInterface,
                                                     ChangePasswordUserDataAccessInterface,
                                                     LogoutUserDataAccessInterface {

    private static final String USERNAME_FIELD = "username";
    private static final String PASSWORD_FIELD = "password";

    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> usersCollection;
    private final UserFactory userFactory;
    private String currentUsername;

    /**
     * Creates a MongoDB data access object using X.509 certificate authentication.
     *
     * @param connectionString MongoDB connection string from MongoDBConfig (ignored, kept for interface compatibility)
     * @param databaseName the database name
     * @param collectionName the collection name for users
     * @param userFactory factory for creating user objects
     */
    public MongoDBUserDataAccessObject(String connectionString,
                                       String databaseName,
                                       String collectionName,
                                       UserFactory userFactory) {
        this.userFactory = userFactory;
        // Use simple username/password authentication (no certificates!)
        this.mongoClient = SimpleMongoDBConfig.createMongoClient();
        this.database = mongoClient.getDatabase(databaseName);
        this.usersCollection = database.getCollection(collectionName);

        // Create index on username for faster lookups
        try {
            this.usersCollection.createIndex(new Document(USERNAME_FIELD, 1));
        } catch (MongoException ex) {
            // Index might already exist, that's okay
        }
    }

    @Override
    public User get(String username) {
        try {
            Bson filter = Filters.eq(USERNAME_FIELD, username);
            Document userDoc = usersCollection.find(filter).first();

            if (userDoc == null) {
                throw new RuntimeException("User not found: " + username);
            }

            // Get the hashed password (stored in DB)
            String hashedPassword = userDoc.getString(PASSWORD_FIELD);

            // IMPORTANT: We return the hashed password here.
            // The LoginInteractor will need to be updated to use verifyPassword()
            // instead of direct password comparison.
            // For now, we return a User with hashed password - you'll need to
            // modify LoginInteractor to handle this properly.
            return userFactory.create(username, hashedPassword);

        } catch (MongoException ex) {
            throw new RuntimeException("Error retrieving user from MongoDB: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void setCurrentUsername(String name) {
        this.currentUsername = name;
    }

    @Override
    public String getCurrentUsername() {
        return currentUsername;
    }

    @Override
    public boolean existsByName(String username) {
        try {
            Bson filter = Filters.eq(USERNAME_FIELD, username);
            long count = usersCollection.countDocuments(filter);
            return count > 0;
        } catch (com.mongodb.MongoSecurityException ex) {
            String errorMsg = "\n" +
                "═══════════════════════════════════════════════════════════════\n" +
                "  ❌ MONGODB AUTHENTICATION FAILED!\n" +
                "═══════════════════════════════════════════════════════════════\n" +
                "\n" +
                "Your MongoDB credentials are incorrect or expired.\n" +
                "\n" +
                "🔧 QUICK FIX:\n" +
                "1. Check MongoDB Atlas (https://cloud.mongodb.com/)\n" +
                "   - Verify user exists in 'Database Access'\n" +
                "   - Verify your IP is whitelisted in 'Network Access'\n" +
                "\n" +
                "2. Update environment variables in PowerShell:\n" +
                "   [System.Environment]::SetEnvironmentVariable('MONGODB_USERNAME', 'YourUsername', 'User')\n" +
                "   [System.Environment]::SetEnvironmentVariable('MONGODB_PASSWORD', 'YourPassword', 'User')\n" +
                "\n" +
                "3. ⚠️  RESTART IntelliJ IDEA after changing variables!\n" +
                "\n" +
                "📖 See MONGODB_AUTH_FIX.md for detailed instructions\n" +
                "═══════════════════════════════════════════════════════════════\n";
            throw new RuntimeException(errorMsg, ex);
        } catch (MongoException ex) {
            throw new RuntimeException("Error checking if user exists: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void save(User user) {
        try {
            if (existsByName(user.getName())) {
                throw new RuntimeException("User already exists: " + user.getName());
            }

            // Hash the password before storing (IMPORTANT for security!)
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

            Document userDoc = new Document()
                    .append(USERNAME_FIELD, user.getName())
                    .append(PASSWORD_FIELD, hashedPassword);

            usersCollection.insertOne(userDoc);

        } catch (MongoException ex) {
            throw new RuntimeException("Error saving user to MongoDB: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void changePassword(User user) {
        try {
            // Hash the new password
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

            Bson filter = Filters.eq(USERNAME_FIELD, user.getName());
            Bson update = Updates.set(PASSWORD_FIELD, hashedPassword);

            usersCollection.updateOne(filter, update);

        } catch (MongoException ex) {
            throw new RuntimeException("Error updating password: " + ex.getMessage(), ex);
        }
    }

    /**
     * Verifies if a plain-text password matches the stored hashed password.
     * This should be used during login.
     *
     * @param username the username
     * @param plainPassword the plain-text password to verify
     * @return true if password matches, false otherwise
     */
    public boolean verifyPassword(String username, String plainPassword) {
        try {
            Bson filter = Filters.eq(USERNAME_FIELD, username);
            Document userDoc = usersCollection.find(filter).first();

            if (userDoc == null) {
                return false;
            }

            String hashedPassword = userDoc.getString(PASSWORD_FIELD);
            return BCrypt.checkpw(plainPassword, hashedPassword);

        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Closes the MongoDB connection.
     * Should be called when the application shuts down.
     */
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}

