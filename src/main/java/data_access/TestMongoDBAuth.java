package data_access;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Simple test to verify MongoDB authentication.
 * Run this BEFORE starting the main application to check if credentials work.
 */
public class TestMongoDBAuth {

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("  MongoDB Authentication Test");
        System.out.println("═══════════════════════════════════════════════════════════════\n");

        // Check environment variables
        String username = System.getenv("MONGODB_USERNAME");
        String password = System.getenv("MONGODB_PASSWORD");

        System.out.println("Environment Variables:");
        System.out.println("  MONGODB_USERNAME: " + (username != null ? username : "❌ NOT SET"));
        System.out.println("  MONGODB_PASSWORD: " + (password != null ? "✓ Set (hidden)" : "❌ NOT SET"));
        System.out.println();

        if (username == null || password == null) {
            System.err.println("❌ ERROR: Environment variables not set!");
            System.err.println("\nSet them in PowerShell:");
            System.err.println("  [System.Environment]::SetEnvironmentVariable('MONGODB_USERNAME', 'YourUsername', 'User')");
            System.err.println("  [System.Environment]::SetEnvironmentVariable('MONGODB_PASSWORD', 'YourPassword', 'User')");
            System.err.println("\n⚠️  Remember to RESTART IntelliJ after setting variables!");
            System.exit(1);
        }

        // Try to connect
        System.out.println("Attempting to connect to MongoDB Atlas...");
        System.out.println("Username: " + username);
        System.out.println();

        try {
            MongoClient mongoClient = SimpleMongoDBConfig.createMongoClient();
            System.out.println("✓ MongoClient created successfully");

            MongoDatabase database = mongoClient.getDatabase(SimpleMongoDBConfig.DATABASE_NAME);
            System.out.println("✓ Connected to database: " + SimpleMongoDBConfig.DATABASE_NAME);

            // Try a simple operation
            Document ping = database.runCommand(new Document("ping", 1));
            System.out.println("✓ Ping successful: " + ping.toJson());

            System.out.println();
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("  ✅ SUCCESS! MongoDB connection is working!");
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("\nYou can now run MainMongoDB.java");

            mongoClient.close();

        } catch (com.mongodb.MongoSecurityException e) {
            System.err.println();
            System.err.println("═══════════════════════════════════════════════════════════════");
            System.err.println("  ❌ AUTHENTICATION FAILED!");
            System.err.println("═══════════════════════════════════════════════════════════════");
            System.err.println();
            System.err.println("Your credentials are incorrect. Please check:");
            System.err.println();
            System.err.println("1. MongoDB Atlas (https://cloud.mongodb.com/)");
            System.err.println("   → Database Access");
            System.err.println("   → Verify user '" + username + "' exists");
            System.err.println("   → Reset password if needed");
            System.err.println();
            System.err.println("2. Update environment variables with correct credentials:");
            System.err.println("   [System.Environment]::SetEnvironmentVariable('MONGODB_USERNAME', '" + username + "', 'User')");
            System.err.println("   [System.Environment]::SetEnvironmentVariable('MONGODB_PASSWORD', 'YourCorrectPassword', 'User')");
            System.err.println();
            System.err.println("3. ⚠️  RESTART IntelliJ IDEA!");
            System.err.println();
            System.err.println("4. Check Network Access in MongoDB Atlas");
            System.err.println("   → Ensure your IP is whitelisted (or use 0.0.0.0/0 for testing)");
            System.err.println();
            System.err.println("📖 See MONGODB_AUTH_FIX.md for detailed instructions");
            System.err.println("═══════════════════════════════════════════════════════════════");

            e.printStackTrace();
            System.exit(1);

        } catch (Exception e) {
            System.err.println();
            System.err.println("═══════════════════════════════════════════════════════════════");
            System.err.println("  ❌ CONNECTION ERROR!");
            System.err.println("═══════════════════════════════════════════════════════════════");
            System.err.println();
            System.err.println("Error: " + e.getMessage());
            System.err.println();
            System.err.println("Possible causes:");
            System.err.println("  • Network connection issues");
            System.err.println("  • MongoDB Atlas cluster is paused or deleted");
            System.err.println("  • Firewall blocking connection");
            System.err.println();
            e.printStackTrace();
            System.exit(1);
        }
    }
}

