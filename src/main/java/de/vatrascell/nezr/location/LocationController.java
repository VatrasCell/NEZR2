package de.vatrascell.nezr.location;

import de.vatrascell.nezr.application.GlobalVars;
import de.vatrascell.nezr.application.NotificationController;
import de.vatrascell.nezr.application.ScreenController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import de.vatrascell.nezr.message.MessageId;
import de.vatrascell.nezr.model.SceneName;

import java.io.IOException;

import static de.vatrascell.nezr.application.GlobalFuncs.getURL;

//TODO add map with locations
public class LocationController {

    @FXML
    private ChoiceBox<String> choiceBox;
    private ObservableList<String> choiceBoxData = FXCollections.observableArrayList();

    /**
     * The constructor (is called before the initialize()-method).
     */
    public LocationController() {
        // Create some sample data for the ComboBox and ListView.
        choiceBoxData.addAll(GlobalVars.locations);
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {

        // Init ComboBox items.
        choiceBox.setItems(choiceBoxData);
        choiceBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void next() {
        GlobalVars.location = choiceBox.getValue();
        try {
            ScreenController.addScreen(SceneName.START, getURL(SceneName.START_PATH));
            ScreenController.activate(SceneName.START);
        } catch (IOException e) {
            e.printStackTrace();
            NotificationController.createErrorMessage(MessageId.TITLE_UNDEFINED, MessageId.MESSAGE_UNDEFINED_ERROR);
        } catch (NullPointerException e) {
            NotificationController.createErrorMessage(MessageId.TITLE_UNDEFINED, MessageId.MESSAGE_UNDEFINED_ERROR);
        }
    }
}
