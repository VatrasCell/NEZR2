package login;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;

public class LoginController {
	
	@FXML
	private TextField username;
	
	@FXML
	private PasswordField password;
	
	@FXML
	private TextField ip;
	
	@FXML
	private TextField port;
	
	@FXML
	private Button btn_login;
	
	@FXML
	private Button btn_exit;
	
	@FXML
	private void login(ActionEvent event) {
		System.out.println("doLogin");
		if(!username.getText().equals("") && !password.getText().equals("")) {
			
		}
	}
	
	@FXML
	private void exit(ActionEvent event) {
		System.out.println("exit");
		System.exit(0);
	}
}
