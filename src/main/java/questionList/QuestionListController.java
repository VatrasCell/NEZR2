package questionList;

import application.GlobalVars;
import application.ScreenController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import model.Frage;
import model.Fragebogen;
import question.QuestionController;
import survey.SurveyService;

import java.io.IOException;
import java.util.ArrayList;

import static application.GlobalFuncs.getURL;

public class QuestionListController {
	public static Fragebogen fragebogen;
	private ArrayList<Frage> fragen;
	private ObservableList<Frage> data = FXCollections.observableArrayList();
	
	@FXML
	private TableView<Frage> tbl_fragen;
	@FXML
	private TableColumn<Frage, String> nameCol;
	@FXML
	private TableColumn<Frage, String> katCol;
	@FXML
	private TableColumn<Frage, Integer> posCol;
	@FXML
	private TableColumn<Frage, String> artCol;
	@FXML
	private TableColumn<Frage, String> actionCol = new TableColumn<>("Bearbeiten");
	//@FXML
	//private TableColumn<Fragebogen, String> copCol = new TableColumn<>("Kopieren");
	@FXML
	private TableColumn<Frage, String> delCol = new TableColumn<>("Löschen");
	
	/**
	 * The constructor (is called before the initialize()-method).
	 */
	public QuestionListController() {
		getData();
	}
	
	private void getData() {
		data.clear();
		fragen = SurveyService.getFragen(fragebogen);
		ArrayList<Frage> ueberschriften = QuestionListService.getUeberschriften(fragebogen);
		for (Frage frage : ueberschriften) {
			int pos = frage.getPosition();
			for (int j = 0; j < fragen.size(); j++) {
				if (fragen.get(j).getPosition() == pos) {
					fragen.add(j, frage);
					break;
				}
			}
		}
		//System.out.println(fragebogen.toString());
		data.addAll(fragen);
	}
	
	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		tbl_fragen.setItems(data);
		
		nameCol.setCellValueFactory(new PropertyValueFactory<>("frage"));
		katCol.setCellValueFactory(new PropertyValueFactory<>("kategorie"));
		posCol.setCellValueFactory(new PropertyValueFactory<>("Position"));
		artCol.setCellValueFactory(new PropertyValueFactory<>("art"));
		
		actionCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        Callback<TableColumn<Frage, String>, TableCell<Frage, String>> cellFactory
                = //
                new Callback<TableColumn<Frage, String>, TableCell<Frage, String>>() {
            @Override
            public TableCell<Frage, String> call(final TableColumn<Frage, String> param) {
            	ImageView imgView = new ImageView(GlobalVars.IMG_EDT);
            	imgView.setFitHeight(30);
        		imgView.setFitWidth(30);
				return new TableCell<Frage, String>() {

					Button btn = new Button("", imgView);
					//final Button btn = new Button("EDIT");

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
							setText(null);
						} else {
							btn.setOnAction(event -> {
								QuestionController.fragebogen = fragebogen;
								QuestionController.frage = getTableView().getItems().get(getIndex());
								try {
									ScreenController.addScreen(model.Scene.QUESTION,
											FXMLLoader.load(getURL("view/QuestionView.fxml")));
											ScreenController.activate(model.Scene.QUESTION);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							});
							setGraphic(btn);
							setText(null);
						}
					}
				};
            }
        };

        actionCol.setCellFactory(cellFactory);
        tbl_fragen.getColumns().add(actionCol);
        
        
        delCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        Callback<TableColumn<Frage, String>, TableCell<Frage, String>> cellFactoryDel
                = //
                new Callback<TableColumn<Frage, String>, TableCell<Frage, String>>() {
            @Override
            public TableCell<Frage, String> call(final TableColumn<Frage, String> param) {
            	ImageView imgView = new ImageView(GlobalVars.IMG_DEL);
            	imgView.setFitHeight(30);
        		imgView.setFitWidth(30);

				return new TableCell<Frage, String>() {
					Button btn = new Button("", imgView);
					//final Button btn = new Button("DEL");

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
							setText(null);
						} else {
							btn.setOnAction(event -> {
								Frage frage = getTableView().getItems().get(getIndex());
								if(QuestionListService.deleteFrage(frage)) {
									getData();
									tbl_fragen.refresh();
								}
							});
							setGraphic(btn);
							setText(null);
						}
					}
				};
            }
        };

        delCol.setCellFactory(cellFactoryDel);
        tbl_fragen.getColumns().add(delCol);

	}
	
	@FXML
	private void save() {
		ScreenController.activate(model.Scene.ADMIN);
	}
	
	@FXML
	private void newQuestion() {
		QuestionController.fragebogen = fragebogen;
		System.out.println(fragen);
		System.out.println(fragen.size());
		
    	QuestionController.frage = new Frage(fragen.size());
    	try {
			ScreenController.addScreen(model.Scene.QUESTION, 
					FXMLLoader.load(getURL("view/QuestionView.fxml")));
					ScreenController.activate(model.Scene.QUESTION);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
