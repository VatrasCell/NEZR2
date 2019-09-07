package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javafx.scene.image.Image;
import model.Frage;
import model.Fragebogen;

public class GlobalVars {
	public static final boolean DEVMODE = true;
	public static final boolean IGNORE_CHECK = true;
	public static int fragen = 3;
	public static final int proZeile = 3;
	public static String standort;
	public static Fragebogen activFragebogen = null;
	public static int page = 0;
	public static int countPanel;
	public static List<ArrayList<Frage>> fragenJePanel = new ArrayList<ArrayList<Frage>>();
	public static boolean everythingIsAwesome = true;
	public static Vector<String> standorte;
	
	public static final Image img_del = new Image(GlobalVars.class.getClassLoader().getResourceAsStream("images/icons/delete_2.png"));
	public static final Image img_cop = new Image(GlobalVars.class.getClassLoader().getResourceAsStream("images/icons/copy_2.png"));
	public static final Image img_edt = new Image(GlobalVars.class.getClassLoader().getResourceAsStream("images/icons/edit_2.png"));
	public static final Image img_ren = new Image(GlobalVars.class.getClassLoader().getResourceAsStream("images/icons/rename.png"));
	public static final Image img_sql = new Image(GlobalVars.class.getClassLoader().getResourceAsStream("images/icons/sql_2.png"));
	public static final Image img_xls = new Image(GlobalVars.class.getClassLoader().getResourceAsStream("images/icons/xls_2.png"));
}
