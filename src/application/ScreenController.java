package application;

import java.util.HashMap;

import javafx.scene.Scene;
import javafx.stage.Stage;
import login.LoginController;
import model.Fragebogen;
import questionList.QuestionListController;

public class ScreenController {
	private static HashMap<String, Scene> screenMap = new HashMap<>();
	private static Stage main;
	
	private static final String styleSheet = "application.css";

	public static void setRootScene(Stage main) {
		ScreenController.main = main;
	}

	public static void addScreen(String name, Scene pane) {
		pane.getStylesheets().add(ScreenController.class.getResource(styleSheet).toExternalForm());
		screenMap.put(name, pane);
	}

	public static void removeScreen(String name) {
		screenMap.remove(name);
	}

	public static void activate(String name) {
		Scene oldScene = main.getScene();
		main.setScene(screenMap.get(name));
		main.show();
		if(oldScene != null) {
			main.setMinHeight(oldScene.getHeight() + 15);
			main.setMinWidth(oldScene.getWidth());
		}
	}
	
	public static <T> void activate(String name, String key, T value) {
		main.setScene(screenMap.get(name));
		setParameter(name + "." + key, value);
		main.show();
	}
	
	private static <T> void setParameter(String key, T value) {
		System.out.println(key);
		switch (key) {
		case "login.toAdmin":
			LoginController.toAdmin = (boolean) value;
			break;
		case "questionList.fragebogen":
			QuestionListController.fragebogen = (Fragebogen) value;

		default:
			break;
		}
	}

	/**
	 * @return the main
	 */
	public static Stage getMain() {
		return main;
	}
}