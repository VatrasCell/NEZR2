package de.vatrascell.nezr.validation;

import de.vatrascell.nezr.application.ScreenController;
import de.vatrascell.nezr.model.SceneName;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class ValidationController {


    @FXML
    private Button btn_new;

    /**
     * The constructor (is called before the initialize()-method).
     */
    public ValidationController() {

    }

    /**
     * Initializes the controller class. This method is automatically called after
     * the fxml file has been loaded.
     */
    @FXML
    private void initialize() {

    }

    @FXML
    private void createNew() {

    }

    @FXML
    private void save() throws IOException {

    }

    @FXML
    private void exit() throws IOException {
        ScreenController.activate(SceneName.QUESTION);
    }
}
