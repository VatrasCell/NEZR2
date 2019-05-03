package gratitude;

import application.ScreenController;
import javafx.fxml.FXML;

public class GratitudeController {
	
	@FXML
	private void exit() {
		ScreenController.activate(model.Scene.START);
	}

}
