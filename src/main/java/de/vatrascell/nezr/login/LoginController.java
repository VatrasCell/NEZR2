package de.vatrascell.nezr.login;

import de.vatrascell.nezr.admin.AdminController;
import de.vatrascell.nezr.application.GlobalVars;
import de.vatrascell.nezr.application.controller.NotificationController;
import de.vatrascell.nezr.application.controller.ScreenController;
import de.vatrascell.nezr.message.MessageId;
import de.vatrascell.nezr.start.StartController;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static de.vatrascell.nezr.model.SceneName.LOGIN_PATH;

@Component
@FxmlView(LOGIN_PATH)
public class LoginController {

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    private final LoginService loginService;
    private final ScreenController screenController;

    @Autowired
    public LoginController(LoginService loginService, ScreenController screenController) {
        this.loginService = loginService;
        this.screenController = screenController;
    }

    @FXML
    private void initialize() {
    }

    @FXML
    private void login() {
        if (!username.getText().equals("") && !password.getText().equals("")) {
            if (loginService.login(username.getText(), password.getText())) {
                reset();
                screenController.activate(AdminController.class);
            } else {
                NotificationController.createErrorMessage(MessageId.TITLE_LOGIN, MessageId.MESSAGE_LOGIN_WRONG_DATA);
            }
        } else {
            NotificationController.createErrorMessage(MessageId.TITLE_LOGIN, MessageId.MESSAGE_LOGIN_WRONG_DATA);
        }
    }

    public void devLogin() {
        if (GlobalVars.DEV_MODE) {
            if (loginService.login("root", "1234")) {
                screenController.activate(AdminController.class);
            } else {
                NotificationController.createErrorMessage(MessageId.TITLE_LOGIN, MessageId.MESSAGE_LOGIN_WRONG_DATA);
            }
        }
    }

    @FXML
    private void exit() {
        reset();
        screenController.activate(StartController.class);
    }

    private void reset() {
        username.setText("");
        password.setText("");
    }
}
