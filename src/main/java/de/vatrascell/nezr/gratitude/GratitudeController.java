package de.vatrascell.nezr.gratitude;

import de.vatrascell.nezr.application.controller.ScreenController;
import de.vatrascell.nezr.start.StartController;
import javafx.fxml.FXML;
import lombok.AllArgsConstructor;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import static de.vatrascell.nezr.model.SceneName.GRATITUDE_PATH;

@Component
@FxmlView(GRATITUDE_PATH)
@AllArgsConstructor
public class GratitudeController {

    private final ScreenController screenController;

    @FXML
    private void exit() {
        screenController.activate(StartController.class);
    }

}
