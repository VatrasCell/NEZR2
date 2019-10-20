package admin;

import application.GlobalVars;
import application.ScreenController;
import export.ExportController;
import javafx.application.Platform;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.Pair;
import model.Fragebogen;
import org.controlsfx.control.Notifications;
import questionList.QuestionListController;
import start.StartController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import static application.GlobalFuncs.getURL;
import static application.ScreenController.styleSheet;

public class AdminController {

	private ArrayList<Fragebogen> fragebogen;
	private ObservableList<Fragebogen> data = FXCollections.observableArrayList();
	
	
	@FXML
	private Button btn_loc;
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
	private TableColumn<Fragebogen, String> renCol = new TableColumn<>("Umbenennen");
	@FXML
	private TableColumn<Fragebogen, String> sqlCol = new TableColumn<>("SQL Export");
	@FXML
	private TableColumn<Fragebogen, String> xlsCol = new TableColumn<>("XLS Export");
	@FXML
	private TableColumn<Fragebogen, String> delCol = new TableColumn<>("Löschen");
	
	/**
	 * The constructor (is called before the initialize()-method).
	 */
	public AdminController() {
		getData();
		
	}
	
	private void getData() {
		data.clear();
		fragebogen = AdminService.getFragebogen(GlobalVars.standort);
		//System.out.println(fragebogen.toString());
		data.addAll(Objects.requireNonNull(fragebogen));
	}
	
	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
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
            		AdminService.activateFragebogen(cellValue);
            		GlobalVars.activFragebogen = cellValue;
            	} else {
            		AdminService.disableFragebogen(cellValue);
            		GlobalVars.activFragebogen = null;
            	}
            	StartController.setStartText();
        		getData();
            	tbl_fragebogen.refresh();
            	});

            return property;
        });
		activCol.setCellFactory(CheckBoxTableCell.forTableColumn(activCol));	
		//finalCol.setCellValueFactory(new PropertyValueFactory<Fragebogen, Boolean>("final"));
		finalCol.setCellValueFactory(cellData -> {

            Fragebogen cellValue = cellData.getValue();
            ObservableBooleanValue property = cellValue.isFinal();

            // Add listener to handler change
            property.addListener((observable, oldValue, newValue) -> {
            	if(newValue) {
            		AdminService.setFinal(cellValue);
            	} else {
            		AdminService.setUnFinal(cellValue);
            	}
            	cellValue.setFinal(newValue);
            });

            return property;
        });
		finalCol.setCellFactory(CheckBoxTableCell.forTableColumn(finalCol));
		
		actionCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        Callback<TableColumn<Fragebogen, String>, TableCell<Fragebogen, String>> cellFactory
                = //
                new Callback<TableColumn<Fragebogen, String>, TableCell<Fragebogen, String>>() {
            @Override
            public TableCell<Fragebogen, String> call(final TableColumn<Fragebogen, String> param) {
            	ImageView imgView = new ImageView(GlobalVars.IMG_EDT);
            	imgView.setFitHeight(30);
        		imgView.setFitWidth(30);
        		Button btn = new Button("", imgView);
                btn.setPadding(Insets.EMPTY);
				return new TableCell<Fragebogen, String>() {

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
									ScreenController.addScreen(model.Scene.QUESTIONLIST,
											FXMLLoader.load(getURL("view/QuestionListView.fxml")));
									ScreenController.activate(model.Scene.QUESTIONLIST);
								} catch (IOException e) {
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
        tbl_fragebogen.getColumns().add(actionCol);
        
        copCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        Callback<TableColumn<Fragebogen, String>, TableCell<Fragebogen, String>> cellFactoryCop
                = //
                new Callback<TableColumn<Fragebogen, String>, TableCell<Fragebogen, String>>() {
            @Override
            public TableCell<Fragebogen, String> call(final TableColumn<Fragebogen, String> param) {
            	ImageView imgView = new ImageView(GlobalVars.IMG_COP);
            	imgView.setFitHeight(30);
        		imgView.setFitWidth(30);
				return new TableCell<Fragebogen, String>() {

					final Button btn = new Button("", imgView);

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
							setText(null);
						} else {
							btn.setOnAction(event -> {
								Fragebogen fragebogen1 = getTableView().getItems().get(getIndex());

								ChoiceDialog<String> dialog = new ChoiceDialog<>(GlobalVars.standort, GlobalVars.standorte);
								dialog.setTitle("Fragebogen kopieren");
								dialog.setHeaderText("Fragebogen kopieren");
								dialog.setContentText("Standort wählen:");
								DialogPane dialogPane = dialog.getDialogPane();
								dialogPane.getStylesheets().add(getURL(styleSheet).toExternalForm());

								Optional<String> result = dialog.showAndWait();
								result.ifPresent(ort -> {
										if(AdminService.copyFragebogen(fragebogen1, ort)) {
											getData();
											tbl_fragebogen.refresh();
											Notifications.create().title("Fragebogen kopieren").text("Der Fragebogen \"" + fragebogen1.getName() + "\" wurde erfolgreich\nnach \"" + ort  + "\" kopiert.").show();
										} else {
											Notifications.create().title("Excel Export").text("Ein Fehler ist aufgetreten.").showError();
										}
									}
								);

							});
							setGraphic(btn);
							setText(null);
						}
					}
				};
            }
        };

        copCol.setCellFactory(cellFactoryCop);
        tbl_fragebogen.getColumns().add(copCol);
        
        renCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        Callback<TableColumn<Fragebogen, String>, TableCell<Fragebogen, String>> cellFactoryRen
                = //
                new Callback<TableColumn<Fragebogen, String>, TableCell<Fragebogen, String>>() {
            @Override
            public TableCell<Fragebogen, String> call(final TableColumn<Fragebogen, String> param) {
            	ImageView imgView = new ImageView(GlobalVars.IMG_REN);
            	imgView.setFitHeight(30);
        		imgView.setFitWidth(30);
				return new TableCell<Fragebogen, String>() {
					final Button btn = new Button("", imgView);

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
							setText(null);
						} else {
							btn.setOnAction(event -> {
								Fragebogen fragebogen1 = getTableView().getItems().get(getIndex());
								TextInputDialog dialog = new TextInputDialog("");
								dialog.setTitle("Fragebogen umbenennen");
								dialog.setContentText("neuer Name:");
								DialogPane dialogPane = dialog.getDialogPane();
								dialogPane.getStylesheets().add(
								   getURL("style/application.css").toExternalForm());

								Optional<String> result = dialog.showAndWait();
								result.ifPresent(fragebogen1::setName);

								if(AdminService.renameFragebogen(fragebogen1)) {
									getData();
									tbl_fragebogen.refresh();
								}
							});
							setGraphic(btn);
							setText(null);
						}
					}
				};
            }
        };

        renCol.setCellFactory(cellFactoryRen);
        tbl_fragebogen.getColumns().add(renCol);
        
        sqlCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        Callback<TableColumn<Fragebogen, String>, TableCell<Fragebogen, String>> cellFactorySql
                = //
                new Callback<TableColumn<Fragebogen, String>, TableCell<Fragebogen, String>>() {
            @Override
            public TableCell<Fragebogen, String> call(final TableColumn<Fragebogen, String> param) {
            	ImageView imgView = new ImageView(GlobalVars.IMG_SQL);
            	imgView.setFitHeight(30);
        		imgView.setFitWidth(30);
				return new TableCell<Fragebogen, String>() {
					final Button btn = new Button("", imgView);

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
							setText(null);
						} else {
							btn.setOnAction(event -> {
								Fragebogen fragebogen1 = getTableView().getItems().get(getIndex());

							});
							setGraphic(btn);
							setText(null);
						}
					}
				};
            }
        };

        sqlCol.setCellFactory(cellFactorySql);
        tbl_fragebogen.getColumns().add(sqlCol);
        
        xlsCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        Callback<TableColumn<Fragebogen, String>, TableCell<Fragebogen, String>> cellFactoryXls
                = //
                new Callback<TableColumn<Fragebogen, String>, TableCell<Fragebogen, String>>() {
            @Override
            public TableCell<Fragebogen, String> call(final TableColumn<Fragebogen, String> param) {
            	ImageView imgView = new ImageView(GlobalVars.IMG_XLS);
            	imgView.setFitHeight(30);
        		imgView.setFitWidth(30);
				return new TableCell<Fragebogen, String>() {
					final Button btn = new Button("", imgView);

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
							setText(null);
						} else {
							btn.setOnAction(event -> {
								Fragebogen fragebogen1 = getTableView().getItems().get(getIndex());
								ExportController exportController = new ExportController();
								Optional<Pair<String, String>> result = getDatePickerDialog();
								result.ifPresent(dates -> {
									//System.out.println("Von=" + usernamePassword.getKey() + ", Bis=" + usernamePassword.getValue());
									if( exportController.excelNeu(fragebogen1.getId() + "_" + fragebogen1.getOrt() + "_" + fragebogen1.getName() + ".xlsx", fragebogen1,
									dates.getKey(), dates.getValue())) {
										Notifications.create().title("Excel Export").text("Export erfolgreich abgeschlossen.").show();
									} else {
										Notifications.create().title("Excel Export").text("Ein Fehler ist aufgetreten.").showError();
									}
								});
							});
							setGraphic(btn);
							setText(null);
						}
					}
				};
            }
        };

        xlsCol.setCellFactory(cellFactoryXls);
        tbl_fragebogen.getColumns().add(xlsCol);
        
        delCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        Callback<TableColumn<Fragebogen, String>, TableCell<Fragebogen, String>> cellFactoryDel
                = //
                new Callback<TableColumn<Fragebogen, String>, TableCell<Fragebogen, String>>() {
            @Override
            public TableCell<Fragebogen, String> call(final TableColumn<Fragebogen, String> param) {
            	ImageView imgView = new ImageView(GlobalVars.IMG_DEL);
            	imgView.setFitHeight(30);
        		imgView.setFitWidth(30);
				return new TableCell<Fragebogen, String>() {
					final Button btn = new Button("", imgView);

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
							setText(null);
						} else {
							btn.setOnAction(event -> {
								Fragebogen fragebogen1 = getTableView().getItems().get(getIndex());
								Alert alert = new Alert(AlertType.CONFIRMATION);
								alert.setTitle("Fragebogen löschen");
								alert.setHeaderText("Wollen Sie den Fragebogen wirklich löschen?");
								alert.setContentText("Fortfahren?");

								DialogPane dialogPane = alert.getDialogPane();
								dialogPane.getStylesheets().add(getURL("style/application.css").toExternalForm());

								Optional<ButtonType> result = alert.showAndWait();
								if (result.isPresent() && result.get() == ButtonType.OK){
									if(AdminService.deleteFragebogen(fragebogen1)) {
										getData();
										tbl_fragebogen.refresh();
										Notifications.create().title("Fragebogen löschen").text("Fragebogen \"" + fragebogen1.getName() + "\" wurde erfolgreich abgeschlossen.").show();
									} else {
										Notifications.create().title("Fragebogen löschen").text("Ein Fehler ist aufgetreten.").showError();
									}
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
        tbl_fragebogen.getColumns().add(delCol);

	}
	
	private Optional<Pair<String, String>> getDatePickerDialog() {
		// Create the custom dialog.
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Excel Export");
		
		DialogPane dialogPane = dialog.getDialogPane();
    	dialogPane.getStylesheets().add(getURL("style/application.css").toExternalForm());

		ButtonType okButtonType = new ButtonType("Export", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		DatePicker von = new DatePicker(LocalDate.now());
		von.setPromptText("von");
		DatePicker bis = new DatePicker(LocalDate.now());
		von.setPromptText("bis");

		grid.add(new Label("Von: "), 0, 0);
		grid.add(von, 1, 0);
		grid.add(new Label("Bis: "), 0, 1);
		grid.add(bis, 1, 1);

		dialog.getDialogPane().setContent(grid);

		Platform.runLater(von::requestFocus);

		dialog.setResultConverter(dialogButton -> {
		    if (dialogButton == okButtonType) {
		        return new Pair<>(von.getValue().toString(), bis.getValue().toString());
		    }
		    return null;
		});

		return dialog.showAndWait();
	}
	
	@FXML
	private void newFragebogen() {
		TextInputDialog dialog = new TextInputDialog("");
    	dialog.setTitle("Fragebogen erstellen");
    	dialog.setContentText("Name:");
    	DialogPane dialogPane = dialog.getDialogPane();
		dialogPane.getStylesheets().add(getURL("style/application.css").toExternalForm());
		
    	Optional<String> result = dialog.showAndWait();
    	result.ifPresent(name -> {
    		if(AdminService.createFragebogen(name) != -1) {
        		getData();
        		tbl_fragebogen.refresh();
        	}
    	});
	}
	
	@FXML
	private void logout() {
		ScreenController.activate(model.Scene.START);
	}
	
	@FXML
	private void changeLocation() {
		ScreenController.activate(model.Scene.LOCATION);
	}
	
	@FXML
	private void exit() {
		System.exit(0);
	}

}
