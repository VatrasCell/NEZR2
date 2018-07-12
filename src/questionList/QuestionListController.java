package questionList;
import java.io.IOException;
import java.util.Vector;

import application.ScreenController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import model.Frage;
import model.Fragebogen;
import question.QuestionController;
import survey.SurveyService;

public class QuestionListController {
	public static Fragebogen fragebogen;
	private Vector<Frage> fragen;
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
		fragen = SurveyService.getFragen(fragebogen);
		Vector<Frage> ueberschriften = QuestionListService.getUeberschriften(fragebogen);
		for(int i = 0; i < ueberschriften.size(); i++) {
			int pos = ueberschriften.get(i).getPosition();
			for(int j = 0; j < fragen.size(); j++) {
				if(fragen.get(j).getPosition() == pos) {
					fragen.add(j, ueberschriften.get(i));
					break;
				}
			}
		}
		//System.out.println(fragebogen.toString());
		for(Frage f : fragen) {
			data.add(f);
		}
	}
	
	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		tbl_fragen.setItems(data);
		
		nameCol.setCellValueFactory(new PropertyValueFactory<Frage, String>("frage"));
		katCol.setCellValueFactory(new PropertyValueFactory<Frage, String>("kategorie"));
		posCol.setCellValueFactory(new PropertyValueFactory<Frage, Integer>("Position"));
		artCol.setCellValueFactory(new PropertyValueFactory<Frage, String>("art"));
		
		actionCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        Callback<TableColumn<Frage, String>, TableCell<Frage, String>> cellFactory
                = //
                new Callback<TableColumn<Frage, String>, TableCell<Frage, String>>() {
            @Override
            public TableCell<Frage, String> call(final TableColumn<Frage, String> param) {
                final TableCell<Frage, String> cell = new TableCell<Frage, String>() {

                    final Button btn = new Button("EDIT");

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
									ScreenController.addScreen(model.Scene.Question.scene(), 
											new Scene(FXMLLoader.load(getClass().getResource("../question/QuestionView.fxml"))));
											ScreenController.activate(model.Scene.Question.scene());
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
                return cell;
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
                final TableCell<Frage, String> cell = new TableCell<Frage, String>() {

                    final Button btn = new Button("DEL");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                            	Frage frage = getTableView().getItems().get(getIndex());
                            	QuestionListService.deleteFrage(frage);
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };

        delCol.setCellFactory(cellFactoryDel);
        tbl_fragen.getColumns().add(delCol);

	}
	
	@FXML
	private void save() {
		ScreenController.activate(model.Scene.Admin.scene());
	}
	
	@FXML
	private void newQuestion() {
		
	}
}
