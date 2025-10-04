package de.vatrascell.nezr.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class DatabaseCredentialGenerator implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String dbUrl = environment.getProperty("spring.datasource.url",
                "jdbc:h2:./db/nezr_v8;MODE=MySQL");

        // Extract database file path from URL
        String dbFile = extractDbFilePath(dbUrl);
        File databaseFile = new File(dbFile);

        // Generate credentials if database doesn't exist
        if (!databaseFile.exists()) {
            String username = generateUsername();
            String password = generatePassword();

            // Add to environment
            Map<String, Object> map = new HashMap<>();
            map.put("spring.datasource.username", username);
            map.put("spring.datasource.password", password);

            MapPropertySource propertySource = new MapPropertySource(
                    "databaseCredentials", map);
            environment.getPropertySources().addFirst(propertySource);

            // Save credentials for future use
            saveCredentials(dbFile + ".creds", username, password);

            System.out.println("Generated new database credentials:");
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);
        } else {
            // Load existing credentials
            String credFile = dbFile + ".creds";
            if (new File(credFile).exists()) {
                String[] credentials = loadCredentials(credFile);
                Map<String, Object> map = new HashMap<>();
                map.put("spring.datasource.username", credentials[0]);
                map.put("spring.datasource.password", credentials[1]);

                MapPropertySource propertySource = new MapPropertySource(
                        "databaseCredentials", map);
                environment.getPropertySources().addFirst(propertySource);

                System.out.println("Loaded existing database credentials:");
                System.out.println("Username: " + credentials[0]);
                System.out.println("Password: " + credentials[1]);
            } else {
                // Use default credentials if no saved credentials found
                Map<String, Object> map = new HashMap<>();
                map.put("spring.datasource.username", "usr");
                map.put("spring.datasource.password", "Q#DQ8Ka&9Vq6`;)s");

                MapPropertySource propertySource = new MapPropertySource(
                        "databaseCredentials", map);
                environment.getPropertySources().addFirst(propertySource);

            }
        }
    }

    private String extractDbFilePath(String dbUrl) {
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
            System.err.println("Failed to save database credentials: " + e.getMessage());
        }
    }

    private String[] loadCredentials(String filePath) {
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