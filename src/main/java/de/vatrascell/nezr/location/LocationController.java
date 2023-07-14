package de.vatrascell.nezr.location;

import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import de.vatrascell.nezr.application.GlobalVars;
import de.vatrascell.nezr.application.controller.PoiLayer;
import de.vatrascell.nezr.application.controller.ScreenController;
import de.vatrascell.nezr.login.LoginService;
import de.vatrascell.nezr.model.Location;
import de.vatrascell.nezr.start.StartController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
    private final PoiLayer poiLayer;

    @FXML
    private ChoiceBox<Location> choiceBox;
    @FXML
    private MapView mapView;
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

        //TODO use other lib
        intMapView();
    }

    @FXML
    private void next() {
        GlobalVars.location = choiceBox.getValue();
        screenController.activate(StartController.class);
    }

    private void intMapView() {



        List<MapPoint> mapPoints = locationService.getLocations().stream().map(location -> {
            MapPoint mapPoint = new MapPoint(location.getCoordinates().getLatitude(), location.getCoordinates().getLongitude());
            /*MapLabel labelClick = new MapLabel(location.getName(), 10, -10).setVisible(true);
            mapPoint.attachLabel(labelClick);*/
            poiLayer.addPoint(mapPoint, new Circle(7, Color.RED));
            return mapPoint;
        }).toList();


        /*mapView.setMapType(MapType.OSM);
        mapView.initialize(Configuration.builder()
                .interactive(true)
                .projection(GlobalVars.projection)
                .showZoomControls(true)
                .build());*/
        mapView.setCenter(new MapPoint(51.206387, 10.2787497));
        mapView.setZoom(6);
        mapView.addLayer(poiLayer);

        /*mapView.addEventHandler(MarkerEvent.MARKER_CLICKED, event -> {
            event.consume();
            choiceBox.getSelectionModel().select(new Location(event.getMarker().getMapLabel().get().getText()));

            //TODO add info box with location info
            //mapView.setCenter(event.getMarker().getPosition());
            //System.out.println("Event: marker clicked: " + event.getMarker());
        });

        mapView.initializedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                mapPoints.forEach(marker -> mapView.addMarker(marker));
            }
        });*/
    }
}
