package de.vatrascell.nezr.location;

import de.vatrascell.nezr.application.GlobalVars;
import de.vatrascell.nezr.application.controller.ScreenController;
import de.vatrascell.nezr.login.LoginService;
import de.vatrascell.nezr.model.Location;
import de.vatrascell.nezr.start.StartController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import lombok.RequiredArgsConstructor;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static de.vatrascell.nezr.model.SceneName.LOCATION_PATH;

@Component
@FxmlView(LOCATION_PATH)
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;
    private final LoginService loginService;
    private final ScreenController screenController;

    @FXML
    private ChoiceBox<Location> choiceBox;
    private ObservableList<Location> choiceBoxData = FXCollections.observableArrayList();

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
/*
        //TODO use other lib
        intMapView();*/
    }

    @FXML
    private void next() {
        GlobalVars.location = choiceBox.getValue();
        screenController.activate(StartController.class);
    }

    private void intMapView() {

    }
}
