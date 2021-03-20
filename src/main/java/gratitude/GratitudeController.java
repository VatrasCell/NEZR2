package gratitude;

import application.ScreenController;
import javafx.fxml.FXML;
import model.SceneName;

import java.io.IOException;

public class GratitudeController {

    @FXML
    private void exit() throws IOException {
        ScreenController.activate(SceneName.START);
    }

}
