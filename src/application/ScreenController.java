package application;

import java.awt.Dimension;
import java.awt.Toolkit;
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
		main.setScene(screenMap.get(name));
		main.show();
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
	
	public static Dimension getScreenSize() {
		return Toolkit.getDefaultToolkit().getScreenSize();
		// return new Dimension((int)main.getWidth(), (int)main.getHeight());
	}
	
	/**
	 * Gibt die Bildschirmhöhe zur�ck.
	 * <p>
	 * @param d Dimension: die Bildschirmgröße von Toolkit.getDefaultToolkit().getScreenSize().
	 * @return die Höhe des Bildschirmes als int.
	 */
	public static int getScreenHeight(Dimension d) {
		Double heightDouble = d.getHeight();
		Integer heightInt = Integer.valueOf(heightDouble.intValue());
		return heightInt;
	}

	/**
	 * Gibt die Bildschirmbreite zurück.
	 * <p>
	 * @param d die Bildschirmgröße von Toolkit.getDefaultToolkit().getScreenSize().
	 * @return die Breite des Bildschirmes als int.
	 */
	public static int getScreenWidth(Dimension d) {
		Double widthDouble = d.getWidth();
		Integer widthInt = Integer.valueOf(widthDouble.intValue());
		return widthInt;
	}
}