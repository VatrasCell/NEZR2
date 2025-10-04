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

        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        String dbUrl = environment.getProperty("spring.datasource.url");

        if (dbUrl == null || dbUrl.isEmpty()) {
            throw new IllegalStateException("spring.datasource.url is empty");
        }

        // Extract database file path from URL
        String dbName = extractDbName(dbUrl);
        String dbFile = dbName + ".mv.db";
        String dbCreds = dbName + ".creds";
        File databaseFile = new File(dbFile);

        // Generate credentials if database doesn't exist
        if (!databaseFile.exists()) {
            String username = generateUsername();
            String password = generatePassword();

            // Add to environment
            addCredentialsToEnvironment(environment, username, password);

            // Save credentials for future use
            saveCredentials(dbCreds, username, password);

        } else {
            // Load existing credentials
            if (new File(dbCreds).exists()) {
                String[] credentials = loadCredentials(dbCreds);
                addCredentialsToEnvironment(environment, credentials[0], credentials[1]);
            } else {
                throw new IllegalStateException("Credential not found");
            }
        }
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
        try {
            Path path = Paths.get(filePath);
            String content = username + ":" + password;
            Files.write(path, content.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save database credentials: " + e.getMessage());
        }
    }

    private String[] loadCredentials(String filePath) {
        try {
            Path path = Paths.get(filePath);
            String content = new String(Files.readAllBytes(path));
            return content.split(":");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database credentials: " + e.getMessage());
        }
    }
}