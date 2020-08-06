package login;

import application.GlobalVars;
import application.ScreenController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import location.LocationService;
import model.SceneName;

import java.io.IOException;

import static application.GlobalFuncs.getURL;

public class LoginController {
	
	public static boolean toAdmin = false;
	
	@FXML
	private TextField username;
	
	@FXML
	private PasswordField password;
	
	@FXML
	private Button btn_login;
	
	@FXML
	private Button btn_exit;
	
	@FXML
	private void initialize() throws IOException {
		if(GlobalVars.DEVMODE) {
			username.setText("root");
			password.setText("1234");
			login(new ActionEvent());
		}
	}
	
	@FXML
	private void login(ActionEvent event) throws IOException {
		//System.out.println("doLogin");
		if(!username.getText().equals("") && !password.getText().equals("")) {
			if(LoginService.testDB(username.getText(), password.getText())) {
				//System.out.println("logged in");
				if(toAdmin) {
					ScreenController.addScreen(SceneName.ADMIN, FXMLLoader.load(getURL(SceneName.ADMIN_PATH)));
					ScreenController.activate(SceneName.ADMIN);
				} else {
					GlobalVars.standorte = LocationService.getStandort();
					ScreenController.addScreen(SceneName.LOCATION, FXMLLoader.load(getURL(SceneName.LOCATION_PATH)));
					ScreenController.activate(SceneName.LOCATION);
				}	
			} else {
				//System.out.println("login wrong values");
			}
		}
	}
	
	@FXML
	private void exit(ActionEvent event) {
		//System.out.println("exit");
		System.exit(0);
	}
}
