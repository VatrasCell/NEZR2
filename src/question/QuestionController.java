package question;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.controlsfx.control.Notifications;

import application.GlobalVars;
import application.ScreenController;
import flag.FlagList;
import flag.SymbolType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import model.Frage;
import model.Fragebogen;
import survey.SurveyService;
import flag.Number;
import flag.QuestionType;
import flag.React;

public class QuestionController {
	public static Fragebogen fragebogen;
	public static Frage frage;
	private Vector<String> antworten;
	private ObservableList<Antwort> data = FXCollections.observableArrayList();
	
	@FXML
	private Label lblQuestion;

	@FXML
	private TextField textFieldFE;
	@FXML
	private TextField textFieldZahl;

	@FXML
	private ChoiceBox<Integer> posChoice;
	@FXML
	private ChoiceBox<String> katChoice;
	@FXML
	private ChoiceBox<String> artChoice;
	@FXML
	private ChoiceBox<String> zahlChoice;

	//@FXML
	//private ImageView imageView;

	@FXML
	private CheckBox chckbxMultipleChoice;
	@FXML
	private CheckBox chckbxListe;
	@FXML
	private CheckBox chckbxTextArea;
	@FXML
	private CheckBox chckbxPflichtfrage;
	@FXML
	private CheckBox chckbxJaNein;
	@FXML
	private CheckBox chckbxUeberschrift;
	@FXML
	private CheckBox chckbxX;
	@FXML
	private CheckBox chckbxZahl;
	
	@FXML
	private Button btnSave;

	@FXML
	private TableView<Antwort> tbl_antworten;
	@FXML
	private TableColumn<Antwort, String> nrCol;
	@FXML
	private TableColumn<Antwort, String> antCol;
	@FXML
	private TableColumn<Antwort, String> actionCol = new TableColumn<>("Bearbeiten");
	// @FXML
	// private TableColumn<Fragebogen, String> copCol = new
	// TableColumn<>("Kopieren");
	@FXML
	private TableColumn<Antwort, String> delCol = new TableColumn<>("Löschen");

	/**
	 * The constructor (is called before the initialize()-method).
	 */
	public QuestionController() {
		// fuer die Generierung der Antwortentabelle
		antworten = QuestionService.getAntworten(frage);
		for (int i = 0; i < antworten.size(); ++i) {
			Antwort antwort = new Antwort();
			antwort.setNr(i + 1);
			antwort.setAntwort(antworten.get(i));
			data.add(antwort);
		}
	}

	/**
	 * Initializes the controller class. This method is automatically called after
	 * the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		tbl_antworten.setItems(data);

		nrCol.setCellValueFactory(new PropertyValueFactory<Antwort, String>("nr"));
		antCol.setCellValueFactory(new PropertyValueFactory<Antwort, String>("antwort"));

		actionCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

		Callback<TableColumn<Antwort, String>, TableCell<Antwort, String>> cellFactory = //
				new Callback<TableColumn<Antwort, String>, TableCell<Antwort, String>>() {
					@Override
					public TableCell<Antwort, String> call(final TableColumn<Antwort, String> param) {
						final TableCell<Antwort, String> cell = new TableCell<Antwort, String>() {

							final Button btn = new Button("EDIT");

							@Override
							public void updateItem(String item, boolean empty) {
								super.updateItem(item, empty);
								if (empty) {
									setGraphic(null);
									setText(null);
								} else {
									btn.setOnAction(event -> {
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
		tbl_antworten.getColumns().add(actionCol);

		delCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
		Callback<TableColumn<Antwort, String>, TableCell<Antwort, String>> cellFactoryDel = //
				new Callback<TableColumn<Antwort, String>, TableCell<Antwort, String>>() {
					@Override
					public TableCell<Antwort, String> call(final TableColumn<Antwort, String> param) {
						final TableCell<Antwort, String> cell = new TableCell<Antwort, String>() {

							final Button btn = new Button("DEL");

							@Override
							public void updateItem(String item, boolean empty) {
								super.updateItem(item, empty);
								if (empty) {
									setGraphic(null);
									setText(null);
								} else {
									btn.setOnAction(event -> {
										 Antwort antwort = getTableView().getItems().get(getIndex());
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
		tbl_antworten.getColumns().add(delCol);
		
		fillScene();
	}

	private void fillScene() {
		String string;
		Pattern MY_PATTERN = Pattern.compile("#\\[[0-9]+\\]");
		Matcher m = MY_PATTERN.matcher(frage.getFrage());
		if (m.find()) {
			string = frage.getFrage().substring(0, m.start());
		} else {
			string = frage.getFrage();
		}
		textFieldFE.setText(string);
		List<Integer> range = IntStream.range(1, QuestionService.getCountPosition(fragebogen) + 2).boxed()
				.collect(Collectors.toList());
		ObservableList<Integer> list = FXCollections.observableArrayList(range);
		posChoice.setItems(list);
		posChoice.getSelectionModel().select(frage.getPosition() - 1);

		ObservableList<String> listKat = FXCollections.observableArrayList(QuestionService.getKategorie());
		katChoice.setItems(listKat);
		katChoice.getSelectionModel().select(frage.getKategorie());

		ObservableList<String> listArt = FXCollections.observableArrayList("Bewertungsfrage", "Multiple Choice",
				"Freie Frage");
		artChoice.setItems(listArt);

		ObservableList<String> listZahl = FXCollections.observableArrayList("Genau wie die Zahl", "Kleiner gleich Zahl",
				"Größer gleich Zahl");
		zahlChoice.setItems(listZahl);

		if (frage.getArt().equals("MC")) {
			if (frage.getFlags().is(SymbolType.B)) {
				artChoice.getSelectionModel().select("Bewertungsfrage");
			} else {
				artChoice.getSelectionModel().select("Multiple Choice");
			}
		} else {
			artChoice.getSelectionModel().select("Freie Frage");
		}

		if (frage.getFlags().is(SymbolType.MC)) {
			chckbxMultipleChoice.setSelected(true);
		} else {
			chckbxMultipleChoice.setSelected(false);
		}

		if (frage.getFlags().is(SymbolType.LIST)) {
			chckbxListe.setSelected(true);
		} else {
			chckbxListe.setSelected(false);
		}

		if (frage.getFlags().is(SymbolType.TEXT)) {
			chckbxTextArea.setSelected(true);
		} else {
			chckbxTextArea.setSelected(false);
		}

		if (frage.getFlags().is(SymbolType.REQUIRED)) {
			chckbxPflichtfrage.setSelected(true);
		} else {
			chckbxPflichtfrage.setSelected(false);
		}

		if (frage.getFlags().is(SymbolType.JN)) {
			chckbxJaNein.setSelected(true);
			//imageView.setVisible(true);
			if (frage.getFlags().is(SymbolType.JNExcel)) {
				chckbxX.setSelected(true);
				// image.showQRImage();
			} else {
				chckbxX.setSelected(false);
				// image.showImage();
			}
		} else {
			//imageView.setVisible(false);
			chckbxJaNein.setSelected(false);
		}
		/*TODO was macht das?!
		if (frage.getFlags().contains("A")) {
			if (frage.getFlags().contains(" ")) {
				int indexSpace = frage.getFlags().indexOf(" ");
				flags = frage.getFlags().substring(0, indexSpace);
				System.out.println(flags);

			}
		}*/
		if (frage.getArt().equals("MC")) {
			if(frage.getAntwort_moeglichkeit().size() > 0) {
				if (frage.getAntwort_moeglichkeit().get(0).equals("#####")) {
					chckbxUeberschrift.setSelected(true);
				} else {
					chckbxUeberschrift.setSelected(false);
				}
			} else {
				chckbxUeberschrift.setSelected(false);
			}		
		}

		// anneSehrNeu
		/*
		 * antworten = new Vector<String>(); for (short i = 0; i <
		 * tableFE.getRowCount(); i++) { antworten.add(tableFE.getValueAt(i,
		 * 1).toString()); }
		 */

		List<Number> numbers = frage.getFlags().getAll(Number.class);

		if (numbers.size() > 0) {
			Number number = numbers.get(0);
			textFieldZahl.setText(number.getDigits() + "");
			chckbxZahl.setSelected(true);
			switch (number.getOperator()) {
			case EQ:
				zahlChoice.getSelectionModel().select("Genau wie die Zahl");
				break;
			case LTE:
				zahlChoice.getSelectionModel().select("Kleiner gleich Zahl");
				break;
			case GTE:
				zahlChoice.getSelectionModel().select("Größer gleich Zahl");
				break;
			}
		} else {
			chckbxZahl.setSelected(false);
		}
	}
	
	@FXML
	private void exit() {
		ScreenController.activate(model.Scene.QuestionList.scene());
	}
	
	@FXML
	private void Save() {
		if (checkFrageDaten()) {
			Frage neueFrage = new Frage();
			
			String string;
			
			String oldFrage = frage.getFrage();
			
			FlagList flags;
			
			if(frage.getFlags().size() > 0) {
				flags = frage.getFlags();
			} else {
				flags = new FlagList();
			}
			
			Pattern MY_PATTERNs = Pattern.compile("#\\[[0-9]+\\]");
			Matcher ms = MY_PATTERNs.matcher(oldFrage);
			if (ms.find()) {
				string = oldFrage.substring(0, ms.start());
			} else {
				string = oldFrage;
			}
			
			if(!string.equals(textFieldFE.getText())) {
				neueFrage.setFrage(QuestionService.duplicateFrage(textFieldFE.getText()));
			} else {
				neueFrage.setFrage(oldFrage);
			}
			oldFrage = "";
			neueFrage.setPosition((int)posChoice.getValue());
			if(frage != null) {
				neueFrage.setFrageID(frage.getFrageID());
			}
			
			if(artChoice.getSelectionModel().getSelectedItem().equals("Multiple Choice") || artChoice.getSelectionModel().getSelectedItem().equals("Bewertungsfrage")) {
				neueFrage.setArt("MC");
			} else if (artChoice.getSelectionModel().getSelectedItem().equals("Freie Frage")) {
				neueFrage.setArt("FF");
			}
			
			String selectedKat = katChoice.getSelectionModel().getSelectedItem().toString();
			if (selectedKat.equals("")) {
				selectedKat = katChoice.getItems().get(0).toString();
			}						
			neueFrage.setKategorie(selectedKat);
			
			
			boolean pflichtfrage = chckbxPflichtfrage.isSelected();                 // +
	        boolean liste = chckbxListe.isSelected();                               // LIST
	        boolean multipleChoice = chckbxMultipleChoice.isSelected();             // *
	        boolean textarea = chckbxTextArea.isSelected();                         // TEXT
	        boolean ja_nein = chckbxJaNein.isSelected();   							// JN
	        boolean x = chckbxX.isSelected();
	        //floNeu
	        boolean isZahl = chckbxZahl.isSelected();									
	        String zahlArt = (String) zahlChoice.getSelectionModel().getSelectedItem();			// INT<= | INT>= | INT==
	        int anzahlZeichen = textFieldZahl.getText().equals("") ? 0 : Integer.parseInt(textFieldZahl.getText());
	        //	
	        
	        List<React> mcList = flags.getAll(React.class);
	        for (React react : mcList) {
	        	if(pflichtfrage) {
					QuestionService.updateFlags(fragebogen, react.getQuestionType().toString(), react.getQuestionId());
				}
		        if (QuestionService.isPflichtfrage(fragebogen, react.getQuestionType().toString(), react.getQuestionId())) {
		        	pflichtfrage = true;
		        }
			}
	        
			if (artChoice.getSelectionModel().getSelectedItem().equals("Freie Frage")) {
				flags = QuestionService.getMoeglicheFlags(flags, pflichtfrage, liste, multipleChoice, textarea, ja_nein, isZahl, false, zahlArt, anzahlZeichen, "ff"); //floNeu
				neueFrage.setFlags(flags);
				QuestionService.saveFreieFrage(fragebogen, neueFrage);					
			}
			if (artChoice.getSelectionModel().getSelectedItem().equals("Multiple Choice")) {
				Vector<Integer> antIds = new Vector<Integer>();
				Vector<Integer> antIdsRaus = new Vector<Integer>();
				Vector<String> ants = new Vector<String>();
				if (ja_nein) {
					ants.add("Ja");
					ants.add("Nein");
				} else {
					for (int i = 0; i < tbl_antworten.getItems().size(); i++) {
						ants.add(tbl_antworten.getItems().get(i).getAntwort());
					}
				}
				
				//anneSehrNeu
				if(lblQuestion.getText().equals("Frage Bearbeiten")) {
					for(int i = 0; i < antworten.size(); i++) {
						for(int j = 0; j < ants.size(); j++) {
							if(!antworten.isEmpty() && antworten.get(i).equals(ants.get(j))) {
								antworten.remove(i);
							}
						}
					}
				}
			
				if(lblQuestion.getText().equals("Frage Bearbeiten")) {
					for(int i = 0; i < antworten.size(); i++){
						int antId = QuestionService.getAntwortID(antworten.get(i));
						antIdsRaus.add(antId);
					}
				}
				
				if(!antIdsRaus.isEmpty()) {
					QuestionService.updateFlags(neueFrage);
					QuestionService.deleteAntworten(antIdsRaus, neueFrage);
				}
				//
				for(int i = 0; i < ants.size(); i++){
					int antId = QuestionService.getAntwortID(ants.get(i));
					antIds.add(antId);
				}
				flags = QuestionService.getMoeglicheFlags(flags, pflichtfrage, liste, multipleChoice, textarea, ja_nein, isZahl, x, zahlArt, anzahlZeichen, "mc"); //floNeu
				neueFrage.setFlags(flags);
				QuestionService.saveMC(fragebogen, neueFrage, antIds);
			}
			if (artChoice.getSelectionModel().getSelectedItem().equals("Bewertungsfrage")) {
				flags = QuestionService.getMoeglicheFlags(flags, pflichtfrage, liste, multipleChoice, textarea, ja_nein, isZahl, false, zahlArt, anzahlZeichen, "bf"); //floNeu
				neueFrage.setFlags(flags);
				QuestionService.saveBewertungsfrage(fragebogen, neueFrage);
			}
		
			ScreenController.activate(model.Scene.QuestionList.scene());
			//pnlFrageErstellenLeeren();
			//makeFragenTabelle(QuestionService.getFragen(fragebogen));
			//cardLayout.show(frame.getContentPane(), "pnlFragenUebersicht");		
		} else {
			/*BalloonTip fehler = new BalloonTip(btnSpeichernFE, "Die Frage ist fehlerhaft und kann deswegen nicht gespeichert werden!");
			fehler.setCloseButton(null);
			TimingUtils.showTimedBalloon(fehler, 3000);
			fehler.setVisible(true);*/
		}
	}
	
	@FXML
	private void setPreview() {
		/* TODO neue Preview Methodik
		if (checkFrageDaten()) {
			Frage neueFrage = new Frage();
			FlagList vflags = flags == null ? "" : flags;
			neueFrage.setFrage(textFieldFE.getText());
			neueFrage.setPosition((int)posChoice.getSelectionModel().getSelectedItem());
			if(frage != null) {
				neueFrage.setFrageID(frage.getFrageID());
			}
			
			String selectedKat = katChoice.getSelectionModel().getSelectedItem().toString();
			if (selectedKat.equals("")) {
				selectedKat = katChoice.getItems().get(0).toString();
			}						
			neueFrage.setKategorie(selectedKat);
			
			boolean pflichtfrage = chckbxPflichtfrage.isSelected();                 // +
	        boolean liste = chckbxListe.isSelected();                               // LIST
	        boolean multipleChoice = chckbxMultipleChoice.isSelected();             // *
	        boolean textarea = chckbxTextArea.isSelected();                        	// TEXT
	        boolean ja_nein = chckbxJaNein.isSelected();   							// JN
	        boolean x = chckbxX.isSelected();										//X
	        //floNeu
	        boolean isZahl = chckbxZahl.isSelected();									
	        String zahlArt = (String) artChoice.getSelectionModel().getSelectedItem();			// INT<= | INT>= | INT==
	        int anzahlZeichen = zahlChoice.getValue() == null ? 0 : Integer.parseInt(zahlChoice.getValue());
	        //
	        if(!chckbxUeberschrift.isSelected()) {

				Pattern MY_PATTERN = Pattern.compile("MC[0-9]+A[0-9]+");
				Matcher mges = MY_PATTERN.matcher(vflags);

				if (mges.find()) {
					Pattern MY_PATTERN1 = Pattern.compile("MC[0-9]+");
					Matcher m1 = MY_PATTERN1.matcher(mges.group(0));
					Pattern MY_PATTERN2 = Pattern.compile("A[0-9]+");
					Matcher m2 = MY_PATTERN2.matcher(mges.group(0));
					if (m1.find() && m2.find()) {
						int zahl = -1;
						try {
							zahl = Integer.parseInt(mges.group(0).substring(2, mges.group(0).indexOf("A")));
						} catch (NumberFormatException en) {
						}
						if (pflichtfrage) {
							QuestionService.updateFlags(fragebogen, "MC", zahl);
						}
						if (QuestionService.isPflichtfrage(fragebogen, "MC", zahl)) {
							pflichtfrage = true;
						}
					}
				}

				Pattern MY_PATTERNFF = Pattern.compile("FF[0-9]+A[0-9]+");
				Matcher mgesFF = MY_PATTERNFF.matcher(vflags);
				if (mgesFF.find()) {
					Pattern MY_PATTERN1 = Pattern.compile("FF[0-9]+");
					Matcher m1 = MY_PATTERN1.matcher(mgesFF.group(0));
					Pattern MY_PATTERN2 = Pattern.compile("A[0-9]+");
					Matcher m2 = MY_PATTERN2.matcher(mgesFF.group(0));
					if (m1.find() && m2.find()) {
						int zahl = -1;
						try {
							zahl = Integer.parseInt(mgesFF.group(0).substring(2, mgesFF.group(0).indexOf("A")));
						} catch (NumberFormatException en) {
						}
						if (pflichtfrage) {
							QuestionService.updateFlags(fragebogen, "FF", zahl);
						}
						if (QuestionService.isPflichtfrage(fragebogen, "FF", zahl)) {
							pflichtfrage = true;
						}
					}

				}
				if (artChoice.getSelectionModel().getSelectedItem().equals("Freie Frage")) {
					vflags += QuestionService.getMoeglicheFlags(pflichtfrage, liste, multipleChoice, textarea,
							ja_nein, isZahl, false, zahlArt, anzahlZeichen, "ff"); // floNeu
					neueFrage.setFlags(vflags);
					neueFrage.setArt("FF");
				} else if (artChoice.getSelectionModel().getSelectedItem().equals("Multiple Choice")) {
					Vector<String> ants = new Vector<String>();
					if (ja_nein) {
						ants.add("Ja");
						ants.add("Nein");
					} else {
						for (Antwort item : tbl_antworten.getItems()) {
						    ants.add(antCol.getCellObservableValue(item).getValue());
						}
					}

					vflags += QuestionService.getMoeglicheFlags(pflichtfrage, liste, multipleChoice, textarea,
							ja_nein, isZahl, x, zahlArt, anzahlZeichen, "mc"); // floNeu
					neueFrage.setFlags(vflags);
					neueFrage.setAntwort_moeglichkeit(ants);
					neueFrage.setArt("MC");
				} else if (artChoice.getSelectionModel().getSelectedItem().equals("Bewertungsfrage")) {
					vflags += QuestionService.getMoeglicheFlags(pflichtfrage, liste, multipleChoice, textarea,
							ja_nein, isZahl, false, zahlArt, anzahlZeichen, "bf"); // floNeu
					neueFrage.setFlags(vflags);
					Vector<String> antwort = new Vector<String>();
					for (int i = 0; i < 11; i++) {
						antwort.add(i + "");
					}
					neueFrage.setAntwort_moeglichkeit(antwort);
					neueFrage.setArt("MC");
				}

				Vector<Frage> allFragen = SurveyService.getFragen(fragebogen);
				Vector<Frage> fragen2 = new Vector<>();
				int pos = neueFrage.getPosition();
				for (int i = 0; i < allFragen.size(); i++) {
					if (allFragen.get(i).getPosition() == pos) {
						fragen2.add(allFragen.get(i));
					}
				}

				boolean setIn = false;
				for (int i = 0; i < fragen2.size(); i++) {
					if (fragen2.get(i).getFrageID() == neueFrage.getFrageID()) {
						if (!fragen2.get(i).getUeberschrift().equals("")) {
							neueFrage.setUeberschrift(fragen2.get(i).getUeberschrift());
						}
						fragen2.set(i, neueFrage);
						setIn = true;
						break;
					}
				}

				if (!setIn) {
					fragen2.add(neueFrage);
				}

				for(Frage frage : fragen2) {
					System.out.println(frage.getFrageID() + " - " + frage.getFrage());
					System.out.println(frage.getArt());
					System.out.println(frage.getFlags());
					System.out.println("-----------------------------------------");
				}

				makeVorschau(fragen2);
				ScreenController.activate("survey_0_preview");
	        } else {
	        	Vector<Frage> allFragen = SurveyService.getFragen(fragebogen);
				Vector<Frage> fragen2 = new Vector<>();
				int pos = neueFrage.getPosition();
				for (int i = 0; i < allFragen.size(); i++) {
					if (allFragen.get(i).getPosition() == pos) {
						allFragen.get(i).setUeberschrift(neueFrage.getFrage());
						fragen2.add(allFragen.get(i));
					}
				}
				
				for(Frage frage : fragen2) {
					System.out.println(frage.getFrageID() + " - " + frage.getFrage());
					System.out.println(frage.getArt());
					System.out.println(frage.getFlags());
					System.out.println("-----------------------------------------");
				}
				
	        	makeVorschau(fragen2);
	        	ScreenController.activate("survey_0_preview");
	        }
		} else {
			System.out.println("Die Frage ist fehlerhaft und es kann deswegen keine Vorschau generiert werden!");
			/*BalloonTip fehler = new BalloonTip(btnVorschauFE, "Die Frage ist fehlerhaft und es kann deswegen keine Vorschau generiert werden!");
			fehler.setCloseButton(null);
			TimingUtils.showTimedBalloon(fehler, 3000);
			fehler.setVisible(true);
		}
		*/
	}
	
	@FXML
	private void createAnswer() {
		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle("Antwort anlegen");
    	dialog.setContentText("Name:");
    	DialogPane dialogPane = dialog.getDialogPane();
		dialogPane.getStylesheets().add(
		   getClass().getResource("../application/application.css").toExternalForm());
		
    	Optional<String> result = dialog.showAndWait();
    	result.ifPresent(name -> {
    		Antwort antwort = new Antwort();
    		antwort.setNr(data.size() + 1);
    		antwort.setAntwort(name);
    		data.add(antwort);
        	Notifications.create().title("Antwort anlegen").text("Antwort \"" + name + "\" wurde erfolgreich angelegt.").show();
    	});
		
	}
	
	@FXML
	private void createCategory() {
		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle("Kategorie anlegen");
    	dialog.setContentText("Name:");
    	DialogPane dialogPane = dialog.getDialogPane();
		dialogPane.getStylesheets().add(
		   getClass().getResource("../application/application.css").toExternalForm());
		
    	Optional<String> result = dialog.showAndWait();
    	result.ifPresent(name -> {
    		if(QuestionService.createKategorie(name)) {
    			ObservableList<String> listKat = FXCollections.observableArrayList(QuestionService.getKategorie());
    			katChoice.setItems(listKat);
    			katChoice.getSelectionModel().select(name);
        		Notifications.create().title("Kategorie anlegen").text("Kategorie \"" + name + "\" wurde erfolgreich angelegt.").show();
    		} else {
    			Notifications.create().title("Kategorie anlegen").text("Ein Fehler ist aufgetreten.").showError();
    		}
    	});
	}
	
	@FXML
	private void react() {
		//TODO
	}
	
	/**
	 * Erstellt das Panel und die dazugehoerigen Elemente fuer die Fragen Vorschau.
	 * @param frage : FrageErstellen
	 */
	private void makeVorschau(Vector<Frage> fragen) {
		try {
		Vector<Vector<Frage>> fragenJePanel = new Vector<Vector<Frage>>();
		/*------------------------- von Julian und Eric --------------------------*/
		
		Vector<Scene> allePanel = new Vector<>();
		
		int countPanel = 0;
		Vector<Integer> anzahl = new Vector<Integer>();
		int temp = 1;
		for(int v = 0; v < fragen.size(); v++) {
			if(v + 1 != fragen.size() && fragen.get(v).getPosition() == fragen.get(v + 1).getPosition()) {
				temp++;
			} else {
				if(temp > GlobalVars.fragen) {
					countPanel += (int)(Math.ceil(temp / GlobalVars.fragen));
					while(temp > GlobalVars.fragen) {
						anzahl.addElement(GlobalVars.fragen);
						temp -= GlobalVars.fragen;
					}
					anzahl.addElement(temp);
				} else {
					anzahl.addElement(temp);
				}
				temp = 1;
			}
		}
		
		
		
		Deque<Frage> stack = new ArrayDeque<Frage>();
		
		for(int v = fragen.size() - 1; v >= 0; v--) {
			stack.push(fragen.get(v));
		}
		
		countPanel += fragen.get(fragen.size() - 1).getPosition();
		int maxCount = countPanel;
		
		
		for(int z = 1; z <= countPanel; z++) {
			Scene scene = new Scene(FXMLLoader.load(getClass().getResource("../survey/SurveyPreView.fxml")));
			
			ProgressBar progressBar = (ProgressBar)scene.lookup("#progressBar");
			progressBar.setProgress((float)z/(float)countPanel);
			
			Label lbl_count = (Label)scene.lookup("#lbl_count");
			lbl_count.setText("Frage " + z + "/" + maxCount);
			
			allePanel.addElement(scene);
		}
		
		for(int z = 0; z < allePanel.size(); z++) {
			
			VBox vBox = (VBox)allePanel.get(z).lookup("#vbox");
			
			boolean isBHeader = false;
			boolean hasUeber = false;
			// setEverythingIsAwesome(true);
			Vector<Frage> frageObj = new Vector<Frage>();

			for(int y = 0; y < anzahl.get(z); y++) {
				frageObj.addElement(stack.pop());
			}
			
			for(int y = 0; y < frageObj.size(); y++) {
				if(!frageObj.get(y).getUeberschrift().equals("") && !hasUeber) {
					
					
					String ueberschrift = frageObj.get(y).getUeberschrift();
					Pattern MY_PATTERNs = Pattern.compile("#\\[[0-9]+\\]");
					Matcher ms = MY_PATTERNs.matcher(ueberschrift);
					if (ms.find()) {
						ueberschrift = ueberschrift.substring(0, ms.start());	
					}
					
					Label lbl_headline = (Label)allePanel.get(z).lookup("#lbl_headline");
					lbl_headline.setText(ueberschrift);
					hasUeber = true;
				}
				
				if(!isBHeader && frageObj.get(y).getFlags().is(SymbolType.B)) {
					
					HBox hBox = new HBox();
					//hBox.setAlignment(Pos.CENTER);
					Label lblEins = new Label("sehr\nschlecht");
					lblEins.setId("lblEins");
					hBox.getChildren().add(lblEins);
					Label lblZehn = new Label("sehr gut");
					lblZehn.setId("lblZehn");
					hBox.getChildren().add(lblZehn);
					Label lblNull = new Label("keine\nAussage");
					lblNull.setId("lblNull");
					hBox.getChildren().add(lblNull);
					
					//TODO: add spacing
					
					vBox.getChildren().add(hBox);
					isBHeader = true;
					
				}
				
				//Fuegt eine Frage ein
				Vector<CheckBox> checkBoxen = new Vector<>();
				boolean isMC = false;
				String frage = frageObj.get(y).getFrage();
				Pattern MY_PATTERNs = Pattern.compile("#\\[[0-9]+\\]");
				Matcher ms = MY_PATTERNs.matcher(frage);
				if (ms.find()) {
					frage = frage.substring(0, ms.start());	
				}
				if(frageObj.get(y).getFlags().is(SymbolType.MC)) {
					isMC = true;
				}
				
				String add = "";			
				
				if(frageObj.get(y).getFlags().is(SymbolType.REQUIRED)) {
					add = " *";
				}
				
				String frageAnzeige;
				if(frage.length() >= 40) {
					int index = frage.indexOf(" ", 15);
					char[] string = frage.toCharArray();
					string[index] = '\n';
					//frageAnzeige = frage.replaceAll("\n", "<br/>");
					frageAnzeige = frage + add;
					
				} else {
					frageAnzeige = frage + add;
				}
				
				Label lblFrage = new Label(frageAnzeige);
				// System.out.println("frageObj.get(y).frageid = " + frageObj.get(y).getFrageID());
				lblFrage.setId("lblFrage_" + frageObj.get(y).getFrageID());
				if (frageObj.get(y).getFlags().hasMCReact()) {
					lblFrage.setVisible(false);
				}
				
				// allePanel.get(z).add(lblFrage, "align center, span, wrap");
				frageObj.get(y).setScene(allePanel.get(z));
				frageObj.get(y).setFrageLabel(lblFrage);
				vBox.getChildren().add(lblFrage);
				
				
				if(frageObj.get(y).getArt() == "FF"){
					
					if(frageObj.get(y).getFlags().is(SymbolType.TEXT)) {
						//F�gt eine Textarea ein
						TextArea textArea = new TextArea(); //anneSuperNeu
						//textArea.setPreferredSize(new Dimension(200, 50));
						//allePanel.get(z).add(textArea, "span, center");
						Vector<TextArea> textAreas = new Vector<TextArea>();
						textAreas.add(textArea);
						frageObj.get(y).setAntwortenTEXT(textAreas);
						vBox.getChildren().add(textArea);
					} else {
						if(frageObj.get(y).getFlags().is(SymbolType.LIST)) {
							//ErrorLog.fehlerBerichtB("ERROR",
							//		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), "Fehler");
						} else {
							//F�gt ein Textfeld ein
							TextField textField = new TextField();
							//textField.setPreferredSize(new Dimension(200, 50));
							
							if (frageObj.get(y).getFlags().hasMCReact()) {
								textField.setVisible(false);
							}
							
							
							//allePanel.get(z).add(textField, "wrap, span, center");
							Vector<TextField> textFields = new Vector<TextField>();
							textFields.add(textField);
							frageObj.get(y).setAntwortenFF(textFields);
							vBox.getChildren().add(textField);
						}
					}
					
				} else if (frageObj.get(y).getArt() == "MC") {
					
					if(frageObj.get(y).getFlags().is(SymbolType.LIST)) {
						//Erstellt eine Liste
						Vector<ListView<String>> antwortenLIST = new Vector<>();
						// scrollPane.getVerticalScrollBar().setUI(new MyScrollBarUI());
						//allePanel.get(z).add(scrollPane, "span, center");
						ListView<String> liste = new ListView<String>();
						liste.setItems(FXCollections.observableArrayList(frageObj.get(y).getAntwort_moeglichkeit()));

						if (frageObj.get(y).getFlags().hasMCReact()) {
							liste.setVisible(false);
						}
						
						antwortenLIST.add(liste);
						vBox.getChildren().add(liste);
						frageObj.get(y).setAntwortenLIST(antwortenLIST);
						continue;
					}
					
					Vector<Integer> anzahlZeile = new Vector<Integer>();
					int intAntworten = frageObj.get(y).getAntwort_moeglichkeit().size();
					do {
						if(intAntworten > GlobalVars.proZeile) {
							anzahlZeile.addElement(GlobalVars.proZeile);
							intAntworten -= GlobalVars.proZeile;
						}
					} while(intAntworten > GlobalVars.proZeile);
					anzahlZeile.addElement(intAntworten);
					
					HBox hBox = new HBox();
					hBox.setAlignment(Pos.CENTER);
					//Uebe die schleife aus, wenn count kleiner ist als die groesse der antwortmoeglichkeiten
					Vector<CheckBox> checkboxs = new Vector<>();
					for(int count3 = 0; count3 < frageObj.get(y).getAntwort_moeglichkeit().size(); count3++){
						
						//Erstellt eine Checkbox
						String antwort = frageObj.get(y).getAntwort_moeglichkeit().get(count3);
						
						String antwortAnzeige = "";
						if(antwort.length() >= 25) {
							int index = antwort.indexOf(" ", 11);
							if(index != -1) {
								char[] string = antwort.toCharArray();
								string[index] = '\n';
								antwortAnzeige = new String(string);
								antwortAnzeige = antwort;
							}
						} else {
							antwortAnzeige = antwort;
						}
						
						CheckBox chckbxSda = new CheckBox(antwortAnzeige);
						//chckbxSda.setFont(new Font("Tahoma", Font.PLAIN, 28));
						//chckbxSda.setForeground(new Color(94, 56, 41));
						
						if (frageObj.get(y).getFlags().hasMCReact()) {
							chckbxSda.setVisible(false);
						}
						checkBoxen.add(chckbxSda);
						if(!isMC) {
							chckbxSda.selectedProperty().addListener(new ChangeListener<Boolean>() {
						        public void changed(ObservableValue<? extends Boolean> ov,
						                Boolean old_val, Boolean new_val) {
						                    //System.out.println("!isMC" + ov + " - " + old_val + " - " + new_val);
						                    if(new_val) {
												for(int i = 0; i < checkBoxen.size(); i++) {
													if(!checkBoxen.get(i).getText().equals(chckbxSda.getText()))
														checkBoxen.get(i).setSelected(false);
										        }
						                    }
						            }
						        });
							/*
							chckbxSda.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								MyCheckbox jCheckBox = (MyCheckbox) e.getSource();
							    boolean selected = jCheckBox.getModel().isSelected();
								for(int i = 0; i < checkBoxen.size(); i++) {
						        	checkBoxen.get(i).setSelected(false);
						        }
								jCheckBox.setSelected(selected);
						        //String text = jCheckBox.getText();
						        
							}
						});*/
						} else {
							chckbxSda.selectedProperty().addListener(new ChangeListener<Boolean>() {
						        public void changed(ObservableValue<? extends Boolean> ov,
						                Boolean old_val, Boolean new_val) {
						                    //System.out.println(ov + " - " + old_val + " - " + new_val);
						            }
						        });
						}
						
						if(frageObj.get(y).getFlags().is(SymbolType.B)) {
							/*
							if(!frageObj.get(y).getFrageLabel().isVisible()) {
								lblNull.setVisible(false);
								lblEins.setVisible(false);
								lblZehn.setVisible(false);
							}*/
							if((count3 == frageObj.get(y).getAntwort_moeglichkeit().size() - 1)) {
								//allePanel.get(z).add(chckbxSda, "");
								hBox.getChildren().add(chckbxSda);
								//JPanel empty = new JPanel();
								//empty.setVisible(false);
								//allePanel.get(z).add(empty, "wrap");
							} else {
								//allePanel.get(z).add(chckbxSda, "");
								hBox.getChildren().add(chckbxSda);
							}
							
						} else {
							if((count3 % (GlobalVars.proZeile)) == (GlobalVars.proZeile - 1)) {
								//allePanel.get(z).add(chckbxSda, "");
								hBox.getChildren().add(chckbxSda);
								/*
								JPanel empty = new JPanel();
								empty.setVisible(false);
								allePanel.get(z).add(empty, "wrap");*/
							} else {
								//allePanel.get(z).add(chckbxSda, "");
								hBox.getChildren().add(chckbxSda);
							}
						}
						checkboxs.addElement(chckbxSda);
						
						/*
						if(count3 == frageObj.get(y).getAntwort_moeglichkeit().size() - 1) {
							for(int v = 0; v < anzahlZeile.size(); v++) {
								while(anzahlZeile.get(v) != (GlobalVars.proZeile)) {
									anzahlZeile.set(v, anzahlZeile.get(v) + 1);
									JPanel empty = new JPanel();
									empty.setVisible(false);
									if(anzahlZeile.get(v) == (GlobalVars.proZeile)) {
										allePanel.get(z).add(empty, "wrap");
									} else {
										allePanel.get(z).add(empty, "");
									}
								}
							}
						}*/	
					}
					vBox.getChildren().add(hBox);
					frageObj.get(y).setAntwortenMC(checkboxs);
					
				} else{
					//Error Nachricht, wenn das obere nicht zutrifft
					//ErrorLog.fehlerBerichtB("ERROR",getClass() + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), "Befragung konnte nicht erstellt/ angezeigt werden!");
				}	
				
			}
			fragenJePanel.addElement(frageObj);
			
		}	
		
		for(int y = 0; y < fragen.size(); y++) {
			
			List<React> reacts = fragen.get(y).getFlags().getAll(React.class);
			List<Number> numbers = fragen.get(y).getFlags().getAll(Number.class);
			for (React react : reacts) {
				fragen.get(y).setTarget(fragen.get(getY(react.getQuestionId(), react.getQuestionType().toString(), fragen)));
				fragen.get(y).setListener(react.getAnswerPos(), react.getQuestionType().toString());
			}
			for (Number number : numbers) {
				fragen.get(y).setListener(0, number.toString());
			}
		}	
		
		for(int i = 0; i < allePanel.size(); ++i) {
			ScreenController.addScreen("survey_" + i + "_preview", allePanel.get(i));
		}
		
		GlobalVars.fragenJePanel = fragenJePanel;
		GlobalVars.countPanel = countPanel;
		
		} catch (Exception e) {
			// ErrorLog.fehlerBerichtB("ERROR",getClass() + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Gibt die Position des "FrageErstellen" Objektes in dem Vector "fragen" 
	 * zurück welche die entsprechende Fragen- ID und Fragenart hat. F�r die Vorschau!
	 * <p>
	 * @param x int: Fragen- ID
	 * @param s String: Fragenart
	 * @param fragen Vector FrageErstellen: alle Fragen
	 * @return Postition im Vector "fragen" als int.
	 */
	private int getY(int x, String s, Vector<Frage> fragen) {
		for(int i = 0; i < fragen.size(); i++) {
			// System.out.println(fragen.get(i).getFrageID()+ " == " + x + " && " + fragen.get(i).getArt() + " == " + s);
			if(x == fragen.get(i).getFrageID() && s.equals(fragen.get(i).getArt())) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Ueberprueft ob alle Bedingungen zum Speichern gegeben sind.
	 */
	public boolean checkFrageDaten() {
		System.out.println(textFieldFE.getText());
		System.out.println(artChoice.getSelectionModel().getSelectedItem());
		if(!textFieldFE.getText().equals("")) {
			if(!artChoice.getSelectionModel().getSelectedItem().equals("-- Art der Frage --")) {
				if(tbl_antworten.getItems().size()<1 && artChoice.getSelectionModel().getSelectedItem().equals("Multiple Choice")) { //anneSehrNeu ? weiß nicht mehr
					btnSave.setDisable(true);
				} else {
					btnSave.setDisable(false);
				}
			} else {
				btnSave.setDisable(true);
			}
		} else {
			btnSave.setDisable(true);
		}
		

		if(!btnSave.isDisabled()) {
			return true;
		} else {
			return false;
		}		
	}

	public class Antwort {
		private int nr;
		private String antwort;

		public int getNr() {
			return nr;
		}

		public void setNr(int nr) {
			this.nr = nr;
		}

		public String getAntwort() {
			return antwort;
		}

		public void setAntwort(String antwort) {
			this.antwort = antwort;
		}

	}
}
