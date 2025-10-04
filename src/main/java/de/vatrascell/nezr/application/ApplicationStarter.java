package de.vatrascell.nezr.application;

import javafx.application.Application;
import javafx.application.Platform;
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
        context.publishEvent(new StageReadyEvent(primaryStage));
    }

    @Override
    public void stop() {
        this.context.close();
        Platform.exit();
    }
}
