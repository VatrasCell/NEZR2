package de.vatrascell.nezr.application;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;

@SpringBootApplication(exclude = JmxAutoConfiguration.class)
public class  Main {

    public static void main(String[] args) {
        Application.launch(ApplicationStarter.class, args);
    }
}
