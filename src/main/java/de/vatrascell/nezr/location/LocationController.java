package de.vatrascell.nezr.location;

import de.vatrascell.nezr.application.GlobalVars;
import de.vatrascell.nezr.application.controller.ScreenController;
import de.vatrascell.nezr.login.LoginService;
import de.vatrascell.nezr.start.StartController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static de.vatrascell.nezr.model.SceneName.LOCATION_PATH;

//TODO add map with locations
@Component
@FxmlView(LOCATION_PATH)
public class LocationController {

    private final LocationService locationService;
    private final LoginService loginService;
    private final ScreenController screenController;

    @FXML
    private ChoiceBox<String> choiceBox;
    private ObservableList<String> choiceBoxData = FXCollections.observableArrayList();

    /**
     * The constructor (is called before the initialize()-method).
     */
    @Autowired
    public LocationController(LocationService locationService, LoginService loginService, ScreenController screenController) {
        this.locationService = locationService;
        this.loginService = loginService;
        this.screenController = screenController;
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        loginService.login("usr", "Q#DQ8Ka&9Vq6`;)s");
        choiceBoxData.addAll(locationService.getLocations());
        choiceBoxData = choiceBoxData.stream().distinct().collect(Collectors.toCollection(FXCollections::observableArrayList));
        // Init ComboBox items.
        choiceBox.setItems(choiceBoxData);
        choiceBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void next() {
        GlobalVars.location = choiceBox.getValue();
        screenController.activate(StartController.class);
    }
}
