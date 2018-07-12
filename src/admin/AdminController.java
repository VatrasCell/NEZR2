package admin;

import java.io.IOException;
import java.util.Vector;

import application.GlobalVars;
import application.ScreenController;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import model.Fragebogen;
import questionList.QuestionListController;

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
	private TableColumn<Fragebogen, Boolean> activCol = new TableColumn<>("Aktiv");
	@FXML
	private TableColumn<Fragebogen, Boolean> finalCol = new TableColumn<>("Final");
	@FXML
	private TableColumn<Fragebogen, String> actionCol = new TableColumn<>("Bearbeiten");
	@FXML
	private TableColumn<Fragebogen, String> copCol = new TableColumn<>("Kopieren");
	@FXML
	private TableColumn<Fragebogen, String> delCol = new TableColumn<>("Löschen");
	
	/**
	 * The constructor (is called before the initialize()-method).
	 */
	public AdminController() {
		getData();
	}
	
	private void getData() {
		fragebogen = AdminService.getFragebogen(GlobalVars.standort);
		//System.out.println(fragebogen.toString());
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
		
		nameCol.setCellValueFactory(new PropertyValueFactory<Fragebogen, String>("name"));
		dateCol.setCellValueFactory(new PropertyValueFactory<Fragebogen, String>("date"));
		//activCol.setCellValueFactory(new PropertyValueFactory<Fragebogen, Boolean>("activ"));
		activCol.setCellValueFactory(cellData -> {
            Fragebogen cellValue = cellData.getValue();
            ObservableBooleanValue property = cellValue.isActiv();

            // Add listener to handler change
            property.addListener((observable, oldValue, newValue) -> {
            	cellValue.setActiv(newValue);
            	if(newValue) {
            		AdminService.updateFragebogen(cellValue);
            	} else {
            		AdminService.disableFragebogen(cellValue);
            	}          	
            	});

            return property;
        });
		activCol.setCellFactory(CheckBoxTableCell.forTableColumn(activCol));	
		//finalCol.setCellValueFactory(new PropertyValueFactory<Fragebogen, Boolean>("final"));
		finalCol.setCellValueFactory(cellData -> {

            Fragebogen cellValue = cellData.getValue();
            ObservableBooleanValue property = cellValue.isFinal();

            // Add listener to handler change
            property.addListener((observable, oldValue, newValue) -> cellValue.setFinal(newValue));

            return property;
        });
		finalCol.setCellFactory(CheckBoxTableCell.forTableColumn(finalCol));
		
		actionCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        Callback<TableColumn<Fragebogen, String>, TableCell<Fragebogen, String>> cellFactory
                = //
                new Callback<TableColumn<Fragebogen, String>, TableCell<Fragebogen, String>>() {
            @Override
            public TableCell<Fragebogen, String> call(final TableColumn<Fragebogen, String> param) {
                final TableCell<Fragebogen, String> cell = new TableCell<Fragebogen, String>() {

                    final Button btn = new Button("EDIT");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                            	try {
                            		QuestionListController.fragebogen = getTableView().getItems().get(getIndex());
									ScreenController.addScreen(model.Scene.QuestionList.scene(), 
											new Scene(FXMLLoader.load(getClass().getResource("../questionList/QuestionListView.fxml"))));
									ScreenController.activate(model.Scene.QuestionList.scene());
								} catch (IOException e) {
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
        tbl_fragebogen.getColumns().add(actionCol);
        
        copCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        Callback<TableColumn<Fragebogen, String>, TableCell<Fragebogen, String>> cellFactoryCop
                = //
                new Callback<TableColumn<Fragebogen, String>, TableCell<Fragebogen, String>>() {
            @Override
            public TableCell<Fragebogen, String> call(final TableColumn<Fragebogen, String> param) {
                final TableCell<Fragebogen, String> cell = new TableCell<Fragebogen, String>() {

                    final Button btn = new Button("COP");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                            	Fragebogen fragebogen = getTableView().getItems().get(getIndex());
                            	if(AdminService.copyFragebogen(fragebogen)) {
                            		getData();
                            		initialize();
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

        copCol.setCellFactory(cellFactoryCop);
        tbl_fragebogen.getColumns().add(copCol);
        
        delCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        Callback<TableColumn<Fragebogen, String>, TableCell<Fragebogen, String>> cellFactoryDel
                = //
                new Callback<TableColumn<Fragebogen, String>, TableCell<Fragebogen, String>>() {
            @Override
            public TableCell<Fragebogen, String> call(final TableColumn<Fragebogen, String> param) {
                final TableCell<Fragebogen, String> cell = new TableCell<Fragebogen, String>() {

                    final Button btn = new Button("DEL");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                            	//Fragebogen person = getTableView().getItems().get(getIndex());
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
        tbl_fragebogen.getColumns().add(delCol);

	}
	
	@FXML
	private void logout() {
		ScreenController.activate(model.Scene.Start.scene());
	}
	
	@FXML
	private void exit() {
		System.exit(0);
	}

}
