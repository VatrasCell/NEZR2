package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import location.LocationService;
import login.LoginService;
import model.SceneName;

import java.io.IOException;

import static application.GlobalFuncs.getURL;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        if (GlobalVars.DEV_MODE) {
            primaryStage.setTitle("NEZR FX DEVMODE");
        } else {
            primaryStage.setTitle("NEZR FX");
        }
        primaryStage.setFullScreen(false);

        ScreenController.setPrimaryStage(primaryStage);

        LoginService.login("usr", "Q#DQ8Ka&9Vq6`;)s");
        GlobalVars.locations = LocationService.getLocations();

        ScreenController.addScreen(SceneName.LOGIN, FXMLLoader.load(getURL(SceneName.LOGIN_PATH)));
        ScreenController.addScreen(SceneName.LOCATION, FXMLLoader.load(getURL(SceneName.LOCATION_PATH)));
        ScreenController.activate(SceneName.LOCATION);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
