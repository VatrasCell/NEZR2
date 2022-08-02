package de.vatrascell.nezr.application.controller;

import de.vatrascell.nezr.application.StageReadyEvent;
import de.vatrascell.nezr.location.LocationController;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import static de.vatrascell.nezr.application.util.ResourceUtil.getURL;

@Controller
public class ScreenController implements ApplicationListener<StageReadyEvent> {

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
}