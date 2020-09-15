package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import model.SceneName;

import java.io.IOException;

import static application.GlobalFuncs.getURL;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) throws IOException {
		if(GlobalVars.DEV_MODE) {
			primaryStage.setTitle("NEZR FX DEVMODE");
		} else {
			primaryStage.setTitle("NEZR FX");
		}
		primaryStage.setFullScreen(false);
		
		ScreenController.setPrimaryStage(primaryStage);
		
		ScreenController.addScreen(SceneName.LOGIN, FXMLLoader.load(getURL(SceneName.LOGIN_PATH)));
		
		if(!GlobalVars.DEV_MODE) {
			ScreenController.activate(SceneName.LOGIN);
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
