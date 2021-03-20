package login;

import application.GlobalVars;
import application.NotificationController;
import application.ScreenController;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import message.MessageId;
import model.SceneName;

import java.io.IOException;

import static application.GlobalFuncs.getURL;

public class LoginController {

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private void initialize() {
    }

    @FXML
    private void login() throws IOException {
        if (!username.getText().equals("") && !password.getText().equals("")) {
            if (LoginService.login(username.getText(), password.getText())) {
                ScreenController.addScreen(SceneName.ADMIN, getURL(SceneName.ADMIN_PATH));
                reset();
                ScreenController.activate(SceneName.ADMIN);
            } else {
                NotificationController.createErrorMessage(MessageId.TITLE_LOGIN, MessageId.MESSAGE_LOGIN_WRONG_DATA);
            }
        } else {
            NotificationController.createErrorMessage(MessageId.TITLE_LOGIN, MessageId.MESSAGE_LOGIN_WRONG_DATA);
        }
    }

    public static void devLogin() throws IOException {
        if (GlobalVars.DEV_MODE) {
            if (LoginService.login("root", "1234")) {
                ScreenController.addScreen(SceneName.ADMIN, getURL(SceneName.ADMIN_PATH));
                ScreenController.activate(SceneName.ADMIN);
            } else {
                NotificationController.createErrorMessage(MessageId.TITLE_LOGIN, MessageId.MESSAGE_LOGIN_WRONG_DATA);
            }
        }
    }

    @FXML
    private void exit() throws IOException {
        reset();
        ScreenController.activate(SceneName.START);
    }

    private void reset() {
        username.setText("");
        password.setText("");
    }
}
