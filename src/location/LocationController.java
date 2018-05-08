package location;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
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
		for (String standort : LocationService.getStandort()) {
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
}
