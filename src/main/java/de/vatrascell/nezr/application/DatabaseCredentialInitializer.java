package de.vatrascell.nezr.application;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DatabaseCredentialInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        System.out.println("=== Database Credential Initializer Starting ===");

        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        String dbUrl = environment.getProperty("spring.datasource.url",
                "jdbc:h2:./db/nezr_v8;MODE=MySQL");

        System.out.println("Database URL: " + dbUrl);

        // Extract database file path from URL
        String dbName = extractDbName(dbUrl);
        String dbFile = dbName + ".mv.db";
        String dbCreds = dbName + ".creds";
        System.out.println("Database file path: " + dbFile);
        File databaseFile = new File(dbFile);

        System.out.println("Database file exists: " + databaseFile.exists());

        // Generate credentials if database doesn't exist
        if (!databaseFile.exists()) {
            System.out.println("Database file does not exist, generating new credentials");
            String username = generateUsername();
            String password = generatePassword();

            // Add to environment
            addCredentialsToEnvironment(environment, username, password);

            // Save credentials for future use
            saveCredentials(dbCreds, username, password);

            System.out.println("Generated new database credentials:");
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);
        } else {
            System.out.println("Database file exists, checking for existing credentials");
            // Load existing credentials
            System.out.println("Credentials file path: " + dbCreds);
            System.out.println("Credentials file exists: " + new File(dbCreds).exists());

            if (new File(dbCreds).exists()) {
                System.out.println("Loading existing credentials from file");
                String[] credentials = loadCredentials(dbCreds);
                addCredentialsToEnvironment(environment, credentials[0], credentials[1]);
                
                System.out.println("Loaded existing database credentials:");
                System.out.println("Username: " + credentials[0]);
                System.out.println("Password: " + credentials[1]);
            } else {
                System.out.println("Using default database credentials");
                // Use default credentials if no saved credentials found
                addCredentialsToEnvironment(environment, "usr", "Q#DQ8Ka&9Vq6`;)s");
                System.out.println("Using default database credentials");
            }
        }

        System.out.println("=== Database Credential Initializer Completed ===");
    }

    private void addCredentialsToEnvironment(ConfigurableEnvironment environment, String username, String password) {
        Map<String, Object> map = new HashMap<>();
        map.put("spring.datasource.username", username);
        map.put("spring.datasource.password", password);

        MapPropertySource propertySource = new MapPropertySource(
                "databaseCredentials", map);

        MutablePropertySources propertySources = environment.getPropertySources();
        if (propertySources.contains("applicationConfig: [classpath:/application.yml]")) {
            propertySources.addBefore("applicationConfig: [classpath:/application.yml]", propertySource);
        } else {
            propertySources.addFirst(propertySource);
        }
    }

    private String extractDbName(String dbUrl) {
        // Remove jdbc:h2: prefix and any connection parameters
        String path = dbUrl.replace("jdbc:h2:", "");
        if (path.contains(";")) {
            path = path.split(";")[0];
        }
        return path;
    }

    private String generateUsername() {
        return UUID.randomUUID().toString();
    }

    private String generatePassword() {
        return UUID.randomUUID().toString();
    }

    private void saveCredentials(String filePath, String username, String password) {
        System.out.println("Saving credential to " + filePath);
        try {
            Path path = Paths.get(filePath);
            String content = username + ":" + password;
            Files.write(path, content.getBytes());
        } catch (IOException e) {
            System.err.println("Failed to save database credentials: " + e.getMessage());
        }
    }

    private String[] loadCredentials(String filePath) {
        System.out.println("Loading credential from " + filePath);
        try {
            Path path = Paths.get(filePath);
            String content = new String(Files.readAllBytes(path));
            return content.split(":");
        } catch (IOException e) {
            System.err.println("Failed to load database credentials: " + e.getMessage());
            return new String[]{"usr", "Q#DQ8Ka&9Vq6`;)s"};
        }
    }
}