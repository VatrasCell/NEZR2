package de.vatrascell.nezr.location;

import com.sothawo.mapjfx.Configuration;
import com.sothawo.mapjfx.Coordinate;
import com.sothawo.mapjfx.MapLabel;
import com.sothawo.mapjfx.MapType;
import com.sothawo.mapjfx.MapView;
import com.sothawo.mapjfx.Marker;
import com.sothawo.mapjfx.event.MarkerEvent;
import de.vatrascell.nezr.application.GlobalVars;
import de.vatrascell.nezr.application.controller.LocationLogoController;
import de.vatrascell.nezr.application.controller.ScreenController;
import de.vatrascell.nezr.login.LoginService;
import de.vatrascell.nezr.start.StartController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import lombok.RequiredArgsConstructor;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static de.vatrascell.nezr.model.SceneName.LOCATION_PATH;

@Component
@FxmlView(LOCATION_PATH)
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;
    private final LoginService loginService;
    private final ScreenController screenController;
    private final LocationLogoController locationLogoController;

    @FXML
    private ChoiceBox<String> choiceBox;
    @FXML
    private MapView mapView;
    private ObservableList<String> choiceBoxData = FXCollections.observableArrayList();

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

        intMapView();
    }

    @FXML
    private void next() {
        GlobalVars.location = choiceBox.getValue();
        screenController.activate(StartController.class);
    }

    private void intMapView() {

        List<Marker> markers = locationService.getLocations().stream().map(location -> {
            Marker marker = Marker.createProvided(Marker.Provided.GREEN).setPosition(
                    locationLogoController.getLocationsCoordinates(location)).setVisible(true);
            MapLabel labelClick = new MapLabel(location, 10, -10).setVisible(true);
            marker.attachLabel(labelClick);
            return marker;
        }).collect(Collectors.toList());

        mapView.setMapType(MapType.OSM);
        mapView.initialize(Configuration.builder()
                .interactive(true)
                .projection(GlobalVars.projection)
                .showZoomControls(true)
                .build());
        mapView.setCenter(new Coordinate(51.206387, 10.2787497));
        mapView.setZoom(6);

        mapView.addEventHandler(MarkerEvent.MARKER_CLICKED, event -> {
            event.consume();
            choiceBox.getSelectionModel().select(event.getMarker().getMapLabel().get().getText());

            //TODO add info box with location info
            //mapView.setCenter(event.getMarker().getPosition());
            //System.out.println("Event: marker clicked: " + event.getMarker());
        });

        mapView.initializedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                markers.forEach(marker -> mapView.addMarker(marker));
            }
        });
    }
}
