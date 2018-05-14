package survey;

import application.GlobalVars;
import application.ScreenController;
import javafx.fxml.FXML;

public class SurveyController {
	
	@FXML
	private void next() {
		if(GlobalVars.page < GlobalVars.countPanel - 1) {
			GlobalVars.page++;
			ScreenController.activate("survey_" + GlobalVars.page);
		} else {
			System.out.println("do Save");
		}
	}
	
	@FXML
	private void pre() {
		if(GlobalVars.page > 0) {
			GlobalVars.page--;
			ScreenController.activate("survey_" + GlobalVars.page);
		} else {
			System.out.println("still page 1");
		}
	}
}
