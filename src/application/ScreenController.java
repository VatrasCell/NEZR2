package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import login.LoginController;
import model.Fragebogen;
import questionList.QuestionListController;

public class ScreenController {
	private static HashMap<String, Pane> screenMap = new HashMap<>();
	private static Stage main;

	private static DoubleProperty fontSize = new SimpleDoubleProperty(0);

	private static List<Map<String, Double>> nodeSizes = new ArrayList<>();

	private static ArrayList<Node> nodes;

	private static final double FACTOR = 1.1;

	private static final String styleSheet = "application.css";

	public static void setRootScene(Stage main) {
		ScreenController.main = main;

//		ScreenController.main.widthProperty().addListener((obs, oldVal, newVal) -> {
//			if (!((Double) oldVal).isNaN()) {
//				resizeNodesWidth((double) newVal);
//			}
//		});

//		ScreenController.main.heightProperty().addListener((obs, oldVal, newVal) -> {
//			if (!((Double) oldVal).isNaN()) {
//				resizeNodesHeight((double) newVal);
//			}
//		});
	}

	public static void addScreen(String name, Pane pane) {
		pane.getStylesheets().add(ScreenController.class.getResource(styleSheet).toExternalForm());
		screenMap.put(name, pane);
	}

	public static void removeScreen(String name) {
		screenMap.remove(name);
	}

	public static void activate(String name) {
		if (main.getScene() == null) {
			if (GlobalVars.DEVMODE) {
				main.setScene(new Scene(screenMap.get("location")));
			} else {
				main.setScene(new Scene(screenMap.get("login")));
			}
		}
		Pane scene = screenMap.get(name);
		fontSize.bind(scene.widthProperty().add(scene.heightProperty()).divide(85));
		scene.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString(), ";"));
		main.getScene().setRoot(scene);
		//nodes = getAllNodes(scene);
		main.show();
		//setListMap();
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

	private static ArrayList<Node> getAllNodes(Parent root) {
		ArrayList<Node> nodes = new ArrayList<Node>();
		addAllDescendents(root, nodes);
		return nodes;
	}

	private static void addAllDescendents(Parent parent, ArrayList<Node> nodes) {
		for (Node node : parent.getChildrenUnmodifiable()) {
			if (node instanceof Control || node instanceof ImageView) {
				nodes.add(node);
			}
			if (node instanceof Parent && !(node instanceof TableView)) {
				addAllDescendents((Parent) node, nodes);
			}

		}
	}

	public static void setListMap() {
		nodeSizes = new ArrayList<>();
		nodes.forEach(node -> {
			if (node instanceof Parent) {
				if (node instanceof Control) {
					Control control = (Control) node;
					Map<String, Double> map = new LinkedHashMap<>();
					map.put("nodeWidth", control.getWidth());
					map.put("stageWidth", main.getWidth());
					map.put("nodeHeight", control.getHeight());
					map.put("stageHeight", main.getHeight());
					nodeSizes.add(map);
				}
			}
			if (node instanceof ImageView) {
				ImageView imageView = (ImageView) node;
				Map<String, Double> map = new LinkedHashMap<>();
				map.put("nodeWidth", imageView.getFitWidth());
				map.put("stageWidth", main.getWidth());
				map.put("nodeHeight", imageView.getFitHeight());
				map.put("stageHeight", main.getHeight());
				nodeSizes.add(map);
			}
		});

		System.out.println(nodeSizes.toString());
	}

	private static void resizeNodesWidth(Double width) {
		for (int i = 0; i < nodes.size(); ++i) {
			Node node = nodes.get(i);
			double value = nodeSizes.get(i).get("nodeWidth") / nodeSizes.get(i).get("stageWidth") * width * FACTOR;
			// if(node instanceof Parent) {
			// Parent parent = (Parent) node;
			// if (parent.getChildrenUnmodifiable().size() == 1) {
			// if (node instanceof Control) {
			// Control control = (Control)node;
			// //System.out.println("get: " + control.getPrefWidth());
			// control.setPrefWidth(value);
			// }
			// }
			// }
			if (node instanceof ImageView) {
				ImageView imageView = (ImageView) node;
				imageView.setFitWidth(value / FACTOR);
			}
		}
	}

	private static void resizeNodesHeight(Double heigth) {

		for (int i = 0; i < nodes.size(); ++i) {
			Node node = nodes.get(i);
			double value = nodeSizes.get(i).get("nodeHeight") / nodeSizes.get(i).get("stageHeight") * heigth;
			// if(node instanceof Parent) {
			// Parent parent = (Parent) node;
			// if (parent.getChildrenUnmodifiable().size() == 1) {
			// if(node instanceof Control) {
			// Control control = (Control)node;
			// control.setPrefHeight(value);
			// }
			// }
			// }
			if (node instanceof ImageView) {
				ImageView imageView = (ImageView) node;
				imageView.setFitHeight(value);
			}
		}
	}
}