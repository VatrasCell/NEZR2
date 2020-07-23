package application;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import login.LoginController;
import model.Fragebogen;
import questionList.QuestionListController;

import java.util.HashMap;

import static application.GlobalFuncs.getURL;

public class ScreenController {
	private static HashMap<String, Pane> screenMap = new HashMap<>();
	private static Stage main;

	public static final String styleSheet = "style/application.css";

	public static void setRootScene(Stage main) {
		ScreenController.main = main;
	}

	public static void addScreen(String name, Pane pane) {
		pane.getStylesheets().add(getURL(styleSheet).toExternalForm());
		screenMap.put(name, pane);
	}

	public static void activate(String name) {
		if (main.getScene() == null) {
			main.setScene(new Scene(screenMap.get(name)));
		}

		Pane scene = screenMap.get(name);
		main.getScene().setRoot(scene);
		main.show();
		System.out.println("activate " + name);
	}

	public static <T> void activate(String name, String key, T value) {
		setParameter(name + "." + key, value);
		activate(name);
	}

	private static <T> void setParameter(String key, T value) {
		switch (key) {
		case "login.toAdmin":
			LoginController.toAdmin = (Boolean) value;
			break;
		case "questionList.fragebogen":
			QuestionListController.fragebogen = (Fragebogen) value;

		default:
			break;
		}
	}
}