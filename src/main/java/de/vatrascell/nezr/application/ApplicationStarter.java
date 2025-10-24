package de.vatrascell.nezr.application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

public class ApplicationStarter extends Application {

    private ConfigurableApplicationContext context;

    @Override
    public void init() {

        ApplicationContextInitializer<GenericApplicationContext> initializer = context -> {
            context.registerBean(Application.class, () -> ApplicationStarter.this);
            context.registerBean(Parameters.class, this::getParameters); // for demonstration, not really needed
        };

        var dbCredentialInitializer = new DatabaseCredentialInitializer();

        this.context = new SpringApplicationBuilder()
                .lazyInitialization(true)
                .sources(Main.class)
                .initializers(initializer, dbCredentialInitializer)
                .run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ObservableList<Screen> screens = Screen.getScreens();
        Rectangle2D bounds = screens.get(0).getVisualBounds();
        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        context.publishEvent(new StageReadyEvent(primaryStage));
    }

    @Override
    public void stop() {
        this.context.close();
        Platform.exit();
    }
}
