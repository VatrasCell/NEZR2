package admin;

import java.util.Vector;

import application.GlobalVars;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import model.Fragebogen;

public class AdminController {
	
	private Vector<Fragebogen> fragebogen;
	private ObservableList<Fragebogen> data = FXCollections.observableArrayList();
	
	@FXML
	private TableView<Fragebogen> tbl_fragebogen;
	
	/**
	 * The constructor (is called before the initialize()-method).
	 */
	public AdminController() {
		fragebogen = AdminService.getFragebogen(GlobalVars.standort);
		for(Fragebogen f : fragebogen) {
			data.add(f);
		}
	}
	
	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		tbl_fragebogen.setItems(data);
	}

}
