package de.vatrascell.nezr.application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import de.vatrascell.nezr.message.MessageId;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import static de.vatrascell.nezr.application.GlobalFuncs.getURL;

public class ScreenController {
    private static final HashMap<String, URL> SCREEN_URL_MAP = new HashMap<>();
    private static final HashMap<String, Pane> SCREEN_PANE_MAP = new HashMap<>();
    private static Stage primaryStage;

    public static final String STYLESHEET = "style/application.css";

    public static void setPrimaryStage(Stage primaryStage) {
        ScreenController.primaryStage = primaryStage;
    }

    public static void addScreen(String name, URL url) {
        SCREEN_URL_MAP.put(name, url);
    }

    public static void addScreen(String name, Pane pane) {
        pane.getStylesheets().add(getURL(STYLESHEET).toExternalForm());
        SCREEN_PANE_MAP.put(name, pane);
    }

    public static void activate(String name) throws IOException {
        Pane root;
        if (SCREEN_URL_MAP.containsKey(name)) {
            root = FXMLLoader.load(SCREEN_URL_MAP.get(name));
            root.getStylesheets().add(getURL(STYLESHEET).toExternalForm());
        } else if (SCREEN_PANE_MAP.containsKey(name)) {
            root = SCREEN_PANE_MAP.get(name);
        } else {
            NotificationController.createErrorMessage(MessageId.TITLE_UNDEFINED, MessageId.MESSAGE_UNDEFINED_ERROR);
            return;
        }

        if (primaryStage.getScene() == null) {
            primaryStage.setScene(new Scene(root));
        } else {
            primaryStage.getScene().setRoot(root);
        }

        //primaryStage.getScene().getRoot().getStylesheets().add(getURL(STYLESHEET).toExternalForm());

        primaryStage.show();
        System.out.println("activate " + name);
    }
}