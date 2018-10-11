package login;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import location.LocationService;

import java.io.IOException;

import application.GlobalVars;
import application.ScreenController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;

public class LoginController {
	
	public static boolean toAdmin = false;
	
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
	private void initialize() throws IOException {
		if(GlobalVars.DEVMODE) {
			username.setText("root");
			password.setText("1234");
			ip.setText("1234");
			port.setText("1234");
			login(new ActionEvent());
		}
	}
	
	@FXML
	private void login(ActionEvent event) throws IOException {
		//System.out.println("doLogin");
		if(!username.getText().equals("") && !password.getText().equals("")) {
			if(LoginService.testDB(username.getText(), password.getText(), ip.getText(), Integer.parseInt(port.getText()), false)) {
				//System.out.println("logged in");
				if(toAdmin) {
					ScreenController.addScreen("admin", new Scene(FXMLLoader.load(getClass().getResource("../admin/AdminView.fxml"))));
					ScreenController.activate("admin");
				} else {
					GlobalVars.standorte = LocationService.getStandort();
					ScreenController.addScreen("location", new Scene(FXMLLoader.load(getClass().getResource("../location/LocationView.fxml"))));
					ScreenController.activate("location");
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
