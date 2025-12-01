package data_access;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
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
import use_case.leaderboard.LeaderBoardDataAccessInterface;
import use_case.login.LoginUserDataAccessInterface;
import use_case.logout.LogoutUserDataAccessInterface;
import use_case.signup.SignupUserDataAccessInterface;

import java.util.*;

/**
 * MongoDB implementation of the user data access object.
 * Stores users in MongoDB with hashed passwords for security.
 */
public class MongoDBUserDataAccessObject implements SignupUserDataAccessInterface,
                                                     LoginUserDataAccessInterface,
                                                     ChangePasswordUserDataAccessInterface,
                                                     LogoutUserDataAccessInterface,
                                                     LeaderBoardDataAccessInterface {

    private static final String USERNAME_FIELD = "username";
    private static final String PASSWORD_FIELD = "password";
    private static final String HIGHSCORES_FIELD = "highscores";

    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> usersCollection;
    private final UserFactory userFactory;
    private String currentUsername;

    /**
     * Creates a MongoDB data access object using simple username/password authentication.
     *
     * @param databaseName the database name
     * @param collectionName the collection name for users
     * @param userFactory factory for creating user objects
     */
    public MongoDBUserDataAccessObject(String databaseName,
                                       String collectionName,
                                       UserFactory userFactory) {
        this.userFactory = userFactory;
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
                                throw new RuntimeException("User not found");
            }

            // Get the hashed password (stored in DB)
            String hashedPassword = userDoc.getString(PASSWORD_FIELD);

            // IMPORTANT: We return the hashed password here.
            // The LoginInteractor will need to be updated to use verifyPassword()
            // instead of direct password comparison.
            // For now, we return a User with hashed password
            return userFactory.create(username, hashedPassword, getHighscoresFromDB(userDoc));

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
            throw new RuntimeException("Authentication failed when checking if user exists: " + ex.getMessage(), ex);
        } catch (MongoException ex) {
            throw new RuntimeException("Error checking if user exists: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void save(User user) {
        try {
            if (existsByName(user.getName())) {
                                throw new RuntimeException("Registration failed: username is unavailable.");
            }

            // Hash the password before storing (IMPORTANT for security!)
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

            Document userDoc = new Document()
                    .append(USERNAME_FIELD, user.getName())
                    .append(PASSWORD_FIELD, hashedPassword)
                    .append(HIGHSCORES_FIELD, user.getHighscores());

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

    @Override
    public void changeHighscore(String username, String gameName, Integer score) {
        try {
            // Retrieve the user to update highscores
            User user = get(username);
            Map<String, Integer> highscores = user.getHighscores();
            highscores.put(gameName, score);


            // Update the highscores in the database
            Bson filter = Filters.eq(USERNAME_FIELD, user.getName());
            Bson update = Updates.set(HIGHSCORES_FIELD, highscores);

            usersCollection.updateOne(filter, update);

        } catch (MongoException ex) {
            throw new RuntimeException("Error updating highscore: " + ex.getMessage(), ex);
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
        } catch (IllegalArgumentException ex) {
            // This exception is thrown by BCrypt if the hashed password format is invalid
            System.err.println("Password verification failed due to invalid password format: " + ex.getMessage());
            return false;
        } catch (Exception ex) {
            // Log unexpected exceptions and rethrow as runtime exception
            System.err.println("Unexpected error during password verification for user '" + username + "': " + ex.getMessage());
            throw new RuntimeException("Unexpected error during password verification", ex);
        }
    }

    @Override
    public Map<String, Integer> getHighscoresByName(String username) {
        try {
            Bson filter = Filters.eq(USERNAME_FIELD, username);
            Document userDoc = usersCollection.find(filter).first();

            if (userDoc == null) {
                throw new RuntimeException("User not found");
            }

            return getHighscoresFromDB(userDoc);

        } catch (MongoException ex) {
            throw new RuntimeException("Error retrieving highscores: " + ex.getMessage(), ex);
        }
    }

    private Map<String, Integer> getHighscoresFromDB(Document userDoc) {
        Map<String, Object> rawMap = userDoc.get(HIGHSCORES_FIELD, Map.class);
        HashMap<String, Integer> highscores = new HashMap<>();

        if (rawMap != null) {
            for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof Integer) {
                    highscores.put(entry.getKey(), (Integer) value);
                }
            }
            return highscores;
        }

        return new HashMap<>();
    }

    @Override
    public List<User> getTopUsersForGame(String gameName, int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than 0");
        } else if (limit > 100) {
            limit = 100;           // Cap limit to 100 to prevent excessive data retrieval
        }
        try {
            // Create a field path for sorting (e.g., "highscores.BLOCKBLAST")
            String sortField = HIGHSCORES_FIELD + "." + gameName;

            // Query: Sort by the game's score in descending order with a limit
            List<Document> topUserDocs = usersCollection
                    .find(Filters.exists(sortField)) // Only users who have played this game
                    .sort(new Document(sortField, -1)) // -1 for descending order
                    .limit(limit)
                    .into(new ArrayList<>());

            List<User> topUsers = new ArrayList<>();

            for (Document userDoc : topUserDocs) {
                String username = userDoc.getString(USERNAME_FIELD);
                String placeholderPassword = "REDACTED";  // Use placeholder instead of empty string

                topUsers.add(userFactory.create(username, placeholderPassword, getHighscoresFromDB(userDoc)));
            }

            return topUsers;

        } catch (MongoException ex) {
            throw new RuntimeException("Error fetching leaderboard: " + ex.getMessage(), ex);
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

