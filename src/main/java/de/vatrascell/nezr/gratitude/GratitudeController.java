package de.vatrascell.nezr.gratitude;

import de.vatrascell.nezr.application.ScreenController;
import de.vatrascell.nezr.model.SceneName;
import javafx.fxml.FXML;

import java.io.IOException;

public class GratitudeController {

    @FXML
    private void exit() throws IOException {
        ScreenController.activate(SceneName.START);
    }

}
