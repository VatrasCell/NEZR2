package location;

import java.io.IOException;

import application.GlobalVars;
import application.ScreenController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;

public class LocationController {
	
	@FXML
	private ChoiceBox<String> choiceBox;
	private ObservableList<String> choiceBoxData = FXCollections.observableArrayList();
	
	/**
	 * The constructor (is called before the initialize()-method).
	 */
	public LocationController() {
		// Create some sample data for the ComboBox and ListView.
		for (String standort : GlobalVars.standorte) {
			choiceBoxData.add(standort);
		}
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
			ScreenController.addScreen("start", new Scene(FXMLLoader.load(getClass().getResource("../start/StartView.fxml"))));
		} catch (IOException e) {
			e.printStackTrace();
		}
		ScreenController.activate("start");
	}
}
