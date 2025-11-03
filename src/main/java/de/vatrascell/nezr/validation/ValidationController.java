package de.vatrascell.nezr.validation;

import de.vatrascell.nezr.application.controller.ScreenController;
import de.vatrascell.nezr.question.QuestionController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static de.vatrascell.nezr.model.SceneName.VALIDATION_PATH;

@Component
@FxmlView(VALIDATION_PATH)
public class ValidationController {

    public static de.vatrascell.nezr.model.Validation validation;

    @FXML
    private Button btn_new;

    @FXML
    private ToggleGroup toggleGroup1;

    @FXML
    private RadioButton radioNumbers;

    @FXML
    private RadioButton radioLetters;

    @FXML
    private RadioButton radioAlphanumeric;

    @FXML
    private RadioButton radioAllChars;

    @FXML
    private RadioButton radioRegex;

    @FXML
    private CheckBox checkHasLength;

    @FXML
    private TextField textMinLength;

    @FXML
    private TextField textMaxLength;

    @FXML
    private TextField textExactLength;

    @FXML
    private TextField textRegex;

    private final ScreenController screenController;

    @Autowired
    public ValidationController(ScreenController screenController) {
        this.screenController = screenController;
    }

    /**
     * Initializes the controller class. This method is automatically called after
     * the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        if (validation != null) {
            // Set radio buttons based on validation flags
            if (validation.isNumbers()) {
                radioNumbers.setSelected(true);
            } else if (validation.isLetters()) {
                radioLetters.setSelected(true);
            } else if (validation.isAlphanumeric()) {
                radioAlphanumeric.setSelected(true);
            } else if (validation.isAllChars()) {
                radioAllChars.setSelected(true);
            } else if (validation.isRegex()) {
                radioRegex.setSelected(true);
            }

            // Set checkbox and text fields
            checkHasLength.setSelected(validation.isHasLength());
            if (validation.getMinLength() > 0) {
                textMinLength.setText(String.valueOf(validation.getMinLength()));
            }
            if (validation.getMaxLength() > 0) {
                textMaxLength.setText(String.valueOf(validation.getMaxLength()));
            }
            if (validation.getLength() > 0) {
                textExactLength.setText(String.valueOf(validation.getLength()));
            }
            if (validation.getRegex() != null) {
                textRegex.setText(validation.getRegex());
            }
        }
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
