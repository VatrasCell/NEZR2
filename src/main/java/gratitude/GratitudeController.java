package gratitude;

import application.ScreenController;
import javafx.fxml.FXML;
import model.SceneName;

public class GratitudeController {

    @FXML
    private void exit() {
        ScreenController.activate(SceneName.START);
    }

}
