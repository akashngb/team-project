package data_access;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * Simple MongoDB configuration using username/password authentication.
 * Much simpler than X.509 certificates!
 */
public class SimpleMongoDBConfig {

    /**
     * MongoDB Atlas connection string using ENVIRONMENT VARIABLES for security.
     *
     * NEVER hardcode passwords in code that goes to GitHub!
     *
     * To set up:
     * 1. Set environment variables (see README for instructions):
     *    - MONGODB_USERNAME: Your MongoDB Atlas username
     *    - MONGODB_PASSWORD: Your MongoDB Atlas password
     *
     * 2. For your team: Each person sets their own environment variables
     *
     * 3. For deployment: Set environment variables in your hosting service
     */
    private static final String MONGODB_USERNAME = System.getenv("MONGODB_USERNAME");
    private static final String MONGODB_PASSWORD = System.getenv("MONGODB_PASSWORD");
    private static final String MONGODB_CLUSTER = "maindata.gskv9ec.mongodb.net";

    public static final String CONNECTION_STRING = String.format(
        "mongodb+srv://%s:%s@%s/?retryWrites=true&w=majority&appName=MainData",
        MONGODB_USERNAME != null ? MONGODB_USERNAME : "your-username",
        MONGODB_PASSWORD != null ? MONGODB_PASSWORD : "your-password",
        MONGODB_CLUSTER
    );

    public static final String DATABASE_NAME = "user_accounts";
    public static final String USERS_COLLECTION = "users";

    /**
     * Creates a simple MongoClient with username/password authentication.
     * No certificates, no complexity!
     */
    public static MongoClient createMongoClient() {
        // Check if environment variables are set
        if (MONGODB_USERNAME == null || MONGODB_PASSWORD == null) {
            String errorMsg = "\n" +
                "═══════════════════════════════════════════════════════════════\n" +
                "  ❌ MONGODB ENVIRONMENT VARIABLES NOT SET!\n" +
                "═══════════════════════════════════════════════════════════════\n" +
                "\n" +
                "Environment variables are missing:\n" +
                "  MONGODB_USERNAME: " + (MONGODB_USERNAME == null ? "❌ NOT SET" : "✓ Set") + "\n" +
                "  MONGODB_PASSWORD: " + (MONGODB_PASSWORD == null ? "❌ NOT SET" : "✓ Set") + "\n" +
                "\n" +
                "🔧 QUICK FIX (Windows PowerShell):\n" +
                "─────────────────────────────────────────────────────────────\n" +
                "[System.Environment]::SetEnvironmentVariable('MONGODB_USERNAME', 'your-username', 'User')\n" +
                "[System.Environment]::SetEnvironmentVariable('MONGODB_PASSWORD', 'your-password', 'User')\n" +
                "\n" +
                "⚠️  IMPORTANT: After setting variables, RESTART IntelliJ IDEA!\n" +
                "\n" +
                "📖 For detailed instructions, see: MONGODB_SETUP.md\n" +
                "═══════════════════════════════════════════════════════════════\n";

            throw new IllegalStateException(errorMsg);
        }

        try {
            ServerApi serverApi = ServerApi.builder()
                    .version(ServerApiVersion.V1)
                    .build();

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(CONNECTION_STRING))
                    .serverApi(serverApi)
                    .build();

            return MongoClients.create(settings);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create MongoDB client: " + e.getMessage(), e);
        }
    }

    private SimpleMongoDBConfig() {
        // Utility class
    }
}

