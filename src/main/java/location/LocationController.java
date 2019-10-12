package location;

import application.GlobalVars;
import application.ScreenController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;

import java.io.IOException;

import static application.GlobalFuncs.getURL;

public class LocationController {
	
	@FXML
	private ChoiceBox<String> choiceBox;
	private ObservableList<String> choiceBoxData = FXCollections.observableArrayList();
	
	/**
	 * The constructor (is called before the initialize()-method).
	 */
	public LocationController() {
		// Create some sample data for the ComboBox and ListView.
        choiceBoxData.addAll(GlobalVars.standorte);
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
		GlobalVars.standort = choiceBox.getValue();
		try {
			ScreenController.addScreen("start", FXMLLoader.load(getURL("view/StartView.fxml")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		ScreenController.activate("start");
	}
}
