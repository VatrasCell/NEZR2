package de.vatrascell.nezr.application.controller;

import de.vatrascell.nezr.application.PropertyLoader;
import de.vatrascell.nezr.message.DialogId;
import javafx.scene.control.Dialog;

public class DialogMessageController {

    private static final PropertyLoader propertyLoader;

    static {
        propertyLoader = new PropertyLoader("dialog");
    }

    public static String getDialogMessage(DialogId message) {
        return propertyLoader.getPropertyValue(message);
    }

    public static void setMessage(Dialog<?> dialog, DialogId title) {
        setMessage(dialog, title, null, null);
    }

    public static void setMessage(Dialog<?> dialog, DialogId title, DialogId contentText) {
        setMessage(dialog, title, null, contentText);
    }

    public static void setMessage(Dialog<?> dialog, DialogId title, DialogId headerText, DialogId contentText) {
        dialog.setTitle(propertyLoader.getPropertyValue(title));
        if (headerText != null) {
            dialog.setHeaderText(propertyLoader.getPropertyValue(headerText));
        } else {
            dialog.setHeaderText("");
        }

        if (headerText != null) {
            dialog.setContentText(propertyLoader.getPropertyValue(contentText));
        } else {
            dialog.setContentText("");
        }
    }
}
