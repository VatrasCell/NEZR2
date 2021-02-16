package application;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.HashMap;

import static application.GlobalFuncs.getURL;

public class ScreenController {
    private static HashMap<String, Pane> screenMap = new HashMap<>();
    private static Stage primaryStage;

    public static final String styleSheet = "style/application.css";

    public static void setPrimaryStage(Stage primaryStage) {
        ScreenController.primaryStage = primaryStage;
    }

    public static void addScreen(String name, Pane pane) {
        pane.getStylesheets().add(getURL(styleSheet).toExternalForm());
        screenMap.put(name, pane);
    }

    public static void activate(String name) {
        if (primaryStage.getScene() == null) {
            primaryStage.setScene(new Scene(screenMap.get(name)));
        }

        Pane root = screenMap.get(name);
        primaryStage.getScene().setRoot(root);
        primaryStage.show();
        System.out.println("activate " + name);
    }
}