package application;

import javafx.scene.image.Image;
import model.Question;
import model.Questionnaire;

import java.util.ArrayList;
import java.util.List;

import static application.GlobalFuncs.getInputStream;

public class GlobalVars {
	public static final boolean DEVMODE = true;
	public static final boolean IGNORE_CHECK = false;
	public static int fragen = 3;
	public static final int proZeile = 3;
	public static String standort;
	public static Questionnaire activQuestionnaire = null;
	public static int page = 0;
	public static int countPanel;
	public static List<ArrayList<Question>> fragenJePanel = new ArrayList<>();
	public static boolean everythingIsAwesome = true;
	public static ArrayList<String> standorte;
	
	public static final Image IMG_DEL = new Image(getInputStream("images/icons/delete_2.png"));
	public static final Image IMG_COP = new Image(getInputStream("images/icons/copy_2.png"));
	public static final Image IMG_EDT = new Image(getInputStream("images/icons/edit_2.png"));
	public static final Image IMG_REN = new Image(getInputStream("images/icons/rename.png"));
	public static final Image IMG_SQL = new Image(getInputStream("images/icons/sql_2.png"));
	public static final Image IMG_XLS = new Image(getInputStream("images/icons/xls_2.png"));
}
