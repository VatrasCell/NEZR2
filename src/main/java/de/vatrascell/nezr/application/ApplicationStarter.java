package de.vatrascell.nezr.application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class ApplicationStarter extends Application {

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() {
        String[] args = getParameters().getRaw().toArray(new String[0]);

        this.applicationContext = new SpringApplicationBuilder()
                .lazyInitialization(true)
                .sources(Main.class)
                .run(args);
    }

    @Override
    public void start(Stage stage) {
        applicationContext.publishEvent(new StageReadyEvent(stage));
    }

    @Override
    public void stop() {
        this.applicationContext.close();
        Platform.exit();
    }

    /*
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

        ScreenController.addScreen(SceneName.LOGIN, getURL(SceneName.LOGIN_PATH));
        ScreenController.addScreen(SceneName.LOCATION, getURL(SceneName.LOCATION_PATH));
        ScreenController.activate(SceneName.LOCATION);
    }*/
}
