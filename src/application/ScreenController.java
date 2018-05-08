package application;

import java.util.HashMap;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class ScreenController {
	private static HashMap<String, Scene> screenMap = new HashMap<>();
	private static Stage main;

	public static void setRootScene(Stage main) {
		ScreenController.main = main;
	}

	public static void addScreen(String name, Scene pane) {
		screenMap.put(name, pane);
	}

	public static void removeScreen(String name) {
		screenMap.remove(name);
	}

	public static void activate(String name) {
		main.setScene(screenMap.get(name));
		main.show();
	}
}