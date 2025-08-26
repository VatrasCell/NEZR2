package de.vatrascell.nezr.login;

import de.vatrascell.nezr.application.Main;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest(classes = Main.class)
@ActiveProfiles("test")
@Log4j2
class LoginServiceTest {

    private static MockedStatic<DriverManager> driverManagerMockedStatic;
    private final String URL = String.format("jdbc:h2:%s\\nezr_v8;MODE=MySQL", new File("db")
            .getAbsolutePath());
    @Mock
    private Connection connection;
    @InjectMocks
    private LoginService loginService;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        driverManagerMockedStatic = mockStatic(DriverManager.class);
    }

    @Test
    void testLoginSuccess() throws SQLException {
        // Given
        String user = "testUser";
        String pwd = "testPassword";

        // Mock successful connection
        driverManagerMockedStatic.when(() -> DriverManager.getConnection(URL, user, pwd)).thenReturn(connection);

        // When
        boolean result = loginService.login(user, pwd);

        // Then
        assertThat(result).isTrue();

        // Verify that the DriverManager.getConnection method was called
        driverManagerMockedStatic.verify(() -> DriverManager.getConnection(URL, user, pwd));
    }

    @Test
    void testLoginFailure() {
        // Given
        String user = "invalidUser";
        String pwd = "invalidPassword";

        // Mock connection failure
        driverManagerMockedStatic.when(() -> DriverManager.getConnection(URL, user, pwd))
                .thenThrow(new RuntimeException("Connection failed"));

        // When
        boolean result = loginService.login(user, pwd);

        // Then
        assertThat(result).isFalse();

        // Verify that the DriverManager.getConnection method was called
        driverManagerMockedStatic.verify(() -> DriverManager.getConnection(URL, user, pwd));
    }

    @Test
    void testLoginWithNullCredentials() {
        // Given
        String user = null;
        String pwd = null;

        // Mock connection failure due to null credentials
        driverManagerMockedStatic.when(() -> DriverManager.getConnection(URL, user, pwd))
                .thenThrow(new RuntimeException("Connection failed due to null credentials"));

        // When
        boolean result = loginService.login(user, pwd);

        // Then
        assertThat(result).isFalse();

        // Verify that the DriverManager.getConnection method was called
        driverManagerMockedStatic.verify(() -> DriverManager.getConnection(URL, user, pwd));
    }

    @Test
    void testLoginWithEmptyCredentials() {
        // Given
        String user = "";
        String pwd = "";

        // Mock connection failure due to empty credentials
        driverManagerMockedStatic.when(() -> DriverManager.getConnection(URL, user, pwd))
                .thenThrow(new RuntimeException("Connection failed due to empty credentials"));

        // When
        boolean result = loginService.login(user, pwd);

        // Then
        assertThat(result).isFalse();

        // Verify that the DriverManager.getConnection method was called
        driverManagerMockedStatic.verify(() -> DriverManager.getConnection(URL, user, pwd));
    }

    @AfterEach
    void tearDown() {
        if (driverManagerMockedStatic != null) {
            driverManagerMockedStatic.close();
        }
    }
}
