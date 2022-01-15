package de.vatrascell.nezr.start;

import de.vatrascell.nezr.application.GlobalVars;
import de.vatrascell.nezr.application.controller.ScreenController;
import de.vatrascell.nezr.application.svg.SvgImageLoader;
import de.vatrascell.nezr.login.LoginController;
import de.vatrascell.nezr.survey.SurveyController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static de.vatrascell.nezr.application.GlobalFuncs.getURL;
import static de.vatrascell.nezr.model.SceneName.START_PATH;

@Component
@FxmlView(START_PATH)
public class StartController {

    @FXML
    Label questionnaireLabel;
    @FXML
    Label warningLabel;
    @FXML
    Pane pane;
    @FXML
    Button startButton;
    @FXML
    GridPane gridPane;
    @FXML
    ImageView imageView;

    private static final StringProperty questionnaireText = new SimpleStringProperty();
    private static final StringProperty questionnaireWarn = new SimpleStringProperty();

    private final ScreenController screenController;
    private final LoginController loginController;

    /**
     * The constructor (is called before the initialize()-method).
     */
    @Autowired
    public StartController(StartService startService, ScreenController screenController, LoginController loginController) {

        this.screenController = screenController;
        this.loginController = loginController;
        GlobalVars.activeQuestionnaire = startService.getActiveQuestionnaire();
    }

    /**
     * Initializes the controller class. This method is automatically called after
     * the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        System.out.println("initialize StartController");
        String value;
        switch (GlobalVars.location) {
            case "R\u00FCgen":
                value = "images/svg/logo-naturerbe-zentrum-ruegen.svg";
                break;
            case "Bayerischer Wald":
                value = "images/svg/logo-baumwipfelpfad-bayerische-wald.svg";
                break;
            case "Saarschleife":
                value = "images/svg/logo-baumwipfelpfad-saarschleife.svg";
                break;
            case "Schwarzwald":
                value = "images/svg/logo-baumwipfelpfad-schwarzwald.svg";
                break;
            case "Usedom":
                value = "images/svg/logo-baumwipfelpfad-usedom.svg";
                break;
            case "Elsass":
                value = "images/svg/logo-baumwipfelpfad-elsass.svg";
                break;
            case "Salzkammergut":
                value = "images/svg/logo-baumwipfelpfad-salzkammergut.svg";
                break;
            default: //Bachledka, Krkonoše, Lipno, Pohorje
                value = "images/svg/baumwipfelpfade-logo.svg";
                break;
        }

        try {
            imageView.fitHeightProperty().bind(gridPane.heightProperty().multiply(0.55));
            imageView.fitWidthProperty().bind(gridPane.widthProperty().multiply(0.7));
            BufferedImage image = SvgImageLoader.loadSvg(getURL(value), 500);
            imageView.setImage(SwingFXUtils.toFXImage(image, null));
        } catch (IOException e) {
            e.printStackTrace();
        }

        pane.setStyle("-fx-background-repeat: no-repeat;" +
                "-fx-background-attachment: fixed;" +
                "-fx-background-size: 20% auto;" +
                "-fx-background-position: 103% 5%;");

        setStartText();
        questionnaireLabel.textProperty().bind(questionnaireText);
        warningLabel.textProperty().bind(questionnaireWarn);
        warningLabel.setStyle("-fx-text-fill: #c90000;");
    }

    public void setStartText() {
        if (GlobalVars.activeQuestionnaire != null) {
            questionnaireText.set(String.format("Fragebogen: %s", GlobalVars.activeQuestionnaire.getName()));
            if (!GlobalVars.activeQuestionnaire.getLocation().equals(GlobalVars.location)) {
                questionnaireWarn.set("Fragebogen ist nicht für diesen Standort optimiert");
            } else {
                questionnaireWarn.set("");
            }
        } else {
            questionnaireText.set("");
            questionnaireWarn.set("kein Fragebogen ausgewählt");
        }
    }

    @FXML
    private void adminLogin() {
        if (GlobalVars.DEV_MODE) {
            loginController.devLogin();
        } else {
            screenController.activate(LoginController.class);
        }
    }

    @FXML
    private void next() {
        screenController.activate(SurveyController.class);
    }
}
