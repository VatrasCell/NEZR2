package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) throws IOException {
		if(GlobalVars.DEVMODE) {
			primaryStage.setTitle("NEZR FX DEVMODE");
		} else {
			primaryStage.setTitle("NEZR FX");
		}
		primaryStage.setFullScreen(false);
		
		ScreenController.setRootScene(primaryStage);
		
		ScreenController.addScreen("login", new Scene(FXMLLoader.load(getClass().getResource("../login/LoginView.fxml"))));
		if(!GlobalVars.DEVMODE) {
			ScreenController.activate("login");
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
