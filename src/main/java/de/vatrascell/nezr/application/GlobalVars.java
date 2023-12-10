package de.vatrascell.nezr.application;

import de.vatrascell.nezr.model.Location;
import de.vatrascell.nezr.model.Question;
import de.vatrascell.nezr.model.Questionnaire;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

import static de.vatrascell.nezr.application.util.ResourceUtil.getInputStream;

public class GlobalVars {
    public static final boolean DEV_MODE = true;
    public static final int PER_COLUMN = 3;
    public static Location location;
    public static Questionnaire activeQuestionnaire = null;
    public static List<ArrayList<Question>> questionsPerPanel = new ArrayList<>();
    public static List<Location> locations;

    //public static Projection projection;

    public static final Image IMG_DEL = new Image(getInputStream("images/icons/delete_2.png"));
    public static final Image IMG_COP = new Image(getInputStream("images/icons/copy_2.png"));
    public static final Image IMG_EDT = new Image(getInputStream("images/icons/edit_2.png"));
    public static final Image IMG_REN = new Image(getInputStream("images/icons/rename.png"));
    public static final Image IMG_SQL = new Image(getInputStream("images/icons/sql_2.png"));
    public static final Image IMG_XLS = new Image(getInputStream("images/icons/xls_2.png"));
}
