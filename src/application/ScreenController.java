package application;

import java.util.HashMap;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import login.LoginController;
import model.Fragebogen;
import questionList.QuestionListController;

public class ScreenController {
	private static HashMap<String, Pane> screenMap = new HashMap<>();
	private static Stage main;
	
	private static DoubleProperty fontSize = new SimpleDoubleProperty(0);
	
	private static final String styleSheet = "application.css";

	public static void setRootScene(Stage main) {
		ScreenController.main = main;
	}

	public static void addScreen(String name, Pane pane) {
		pane.getStylesheets().add(ScreenController.class.getResource(styleSheet).toExternalForm());
		screenMap.put(name, pane);
	}

	public static void removeScreen(String name) {
		screenMap.remove(name);
	}

	public static void activate(String name) {
		if(main.getScene() == null) {
			if(GlobalVars.DEVMODE) {
				main.setScene(new Scene(screenMap.get("location")));
			} else {
				main.setScene(new Scene(screenMap.get("login")));
			}
		}	
		Pane scene = screenMap.get(name);
		fontSize.bind(scene.widthProperty().add(scene.heightProperty()).divide(85));
		scene.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString(), ";"));
		main.getScene().setRoot(scene);
		main.show();
		System.out.println("activate " + name);
	}
	
	public static <T> void activate(String name, String key, T value) {
		setParameter(name + "." + key, value);
		activate(name);
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