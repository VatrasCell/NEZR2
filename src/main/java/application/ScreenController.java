package application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import message.MessageId;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import static application.GlobalFuncs.getURL;

public class ScreenController {
    private static HashMap<String, URL> screenUrlMap = new HashMap<>();
    private static HashMap<String, Pane> screenPaneMap = new HashMap<>();
    private static Stage primaryStage;

    public static final String STYLESHEET = "style/application.css";

    public static void setPrimaryStage(Stage primaryStage) {
        ScreenController.primaryStage = primaryStage;
    }

    public static void addScreen(String name, URL url) {
        screenUrlMap.put(name, url);
    }

    public static void addScreen(String name, Pane pane) {
        screenPaneMap.put(name, pane);
    }

    public static void activate(String name) throws IOException {
        Pane root;
        if (screenUrlMap.containsKey(name)) {
            root = FXMLLoader.load(screenUrlMap.get(name));
        } else if (screenPaneMap.containsKey(name)) {
            root = screenPaneMap.get(name);
        } else {
            NotificationController.createErrorMessage(MessageId.TITLE_UNDEFINED, MessageId.MESSAGE_UNDEFINED_ERROR);
            return;
        }

        if (primaryStage.getScene() == null) {
            primaryStage.setScene(new Scene(root));
        }

        FXMLLoader.load(screenUrlMap.get(name));

        root.getStylesheets().add(getURL(STYLESHEET).toExternalForm());
        primaryStage.getScene().setRoot(root);
        primaryStage.show();
        System.out.println("activate " + name);
    }
}