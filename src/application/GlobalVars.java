package application;

import java.util.Vector;

import model.Frage;
import model.Fragebogen;

public class GlobalVars {
	public static final boolean DEVMODE = true;
	public static final int fragen = 3;
	public static final int proZeile = 3;
	public static String standort;
	public static Fragebogen activFragebogen = null;
	public static int page = 0;
	public static int countPanel;
	public static Vector<Vector<Frage>> fragenJePanel;
	public static boolean everythingIsAwesome = true;
}
