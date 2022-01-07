package de.vatrascell.nezr.validation;

import de.vatrascell.nezr.application.controller.ScreenController;
import de.vatrascell.nezr.question.QuestionController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import static de.vatrascell.nezr.model.SceneName.VALIDATION_PATH;

@Component
@FxmlView(VALIDATION_PATH)
public class ValidationController {

    @FXML
    private Button btn_new;

    private final ScreenController screenController;

    @Autowired
    @Lazy
    public ValidationController(ScreenController screenController) {
        this.screenController = screenController;
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
    private void save() {

    }

    @FXML
    private void exit() {
        screenController.activate(QuestionController.class);
    }
}
