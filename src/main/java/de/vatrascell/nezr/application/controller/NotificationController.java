package de.vatrascell.nezr.application.controller;

import de.vatrascell.nezr.application.PropertyLoader;
import de.vatrascell.nezr.message.MessageId;
import org.controlsfx.control.Notifications;

public class NotificationController {

    private static final PropertyLoader propertyLoader;

    static {
        propertyLoader = new PropertyLoader("message");
    }

    public static void createErrorMessage(MessageId title, MessageId message) {
        Notifications.create()
                .title(propertyLoader.getPropertyValue(title))
                .text(propertyLoader.getPropertyValue(message))
                .showError();
    }

    public static void createMessage(MessageId title, MessageId message) {
        Notifications.create()
                .title(propertyLoader.getPropertyValue(title))
                .text(propertyLoader.getPropertyValue(message))
                .show();
    }

    public static void createMessage(MessageId title, MessageId message, Object... args) {
        Notifications.create()
                .title(propertyLoader.getPropertyValue(title))
                .text(String.format(propertyLoader.getPropertyValue(message), args))
                .show();
    }

    public static void createErrorMessage(MessageId title, MessageId message, Object... args) {
        Notifications.create()
                .title(propertyLoader.getPropertyValue(title))
                .text(String.format(propertyLoader.getPropertyValue(message), args))
                .showError();
    }
}
