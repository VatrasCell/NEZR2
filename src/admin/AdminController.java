package admin;

import java.util.Vector;

import application.GlobalVars;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Fragebogen;

public class AdminController {
	
	private Vector<Fragebogen> fragebogen;
	private ObservableList<Fragebogen> data = FXCollections.observableArrayList();
	
	@FXML
	private TableView<Fragebogen> tbl_fragebogen;
	
	@FXML
	private TableColumn<Fragebogen, String> nameCol;
	@FXML
	private TableColumn<Fragebogen, String> dateCol;
	@FXML
	private TableColumn<Fragebogen, Boolean> activCol;
	@FXML
	private TableColumn<Fragebogen, Boolean> finalCol;
	
	/**
	 * The constructor (is called before the initialize()-method).
	 */
	public AdminController() {
		fragebogen = AdminService.getFragebogen(GlobalVars.standort);
		System.out.println(fragebogen.toString());
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
		System.out.println(data.toString());
		tbl_fragebogen.setItems(data);
		
		/*nameCol.setCellValueFactory(new PropertyValueFactory<Fragebogen, String>("name"));
		dateCol.setCellValueFactory(new PropertyValueFactory<Fragebogen, String>("date"));
		activCol.setCellValueFactory(new PropertyValueFactory<Fragebogen, Boolean>("activ"));
		finalCol.setCellValueFactory(new PropertyValueFactory<Fragebogen, Boolean>("final"));*/
	}

}
