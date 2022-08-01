package de.vatrascell.nezr.application.controller;

import de.vatrascell.nezr.application.StageReadyEvent;
import de.vatrascell.nezr.location.LocationController;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.HashMap;

import static de.vatrascell.nezr.application.util.ResourceUtil.getURL;

@Controller
public class ScreenController implements ApplicationListener<StageReadyEvent> {
    private static final HashMap<String, URL> SCREEN_URL_MAP = new HashMap<>();
    private static final HashMap<String, Pane> SCREEN_PANE_MAP = new HashMap<>();

    public static final String STYLESHEET = "style/application.css";
    private final FxWeaver fxWeaver;
    private Stage stage;

    public ScreenController(ConfigurableApplicationContext applicationContext) {
        fxWeaver = applicationContext.getBean(FxWeaver.class);
    }

    @Override
    public void onApplicationEvent(StageReadyEvent stageReadyEvent) {
        this.stage = (Stage) stageReadyEvent.getSource();
        activate(LocationController.class);
    }

    /*public static void setPrimaryStage(Stage primaryStage) {
        ScreenController.primaryStage = primaryStage;
    }

    public static void addScreen(String name, URL url) {
        SCREEN_URL_MAP.put(name, url);
    }

    public static void addScreen(String name, Pane pane) {
        pane.getStylesheets().add(getURL(STYLESHEET).toExternalForm());
        SCREEN_PANE_MAP.put(name, pane);
    }*/

    public void activate(Class<?> name) {
        Parent root = fxWeaver.loadView(name);

        if (stage.getScene() == null) {
            stage.setScene(new Scene(root));
        } else {
            stage.getScene().setRoot(root);
        }

        stage.getScene().getRoot().getStylesheets().add(getURL(STYLESHEET).toExternalForm());

        stage.show();
        System.out.println("activate " + name);
    }

    /*
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
    }*/
}