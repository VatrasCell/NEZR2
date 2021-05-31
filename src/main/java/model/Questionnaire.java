package model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;

public class Questionnaire {

    public static String ID = "id";
    public static String DATE = "date";
    public static String NAME = "name";
    public static String LOCATION = "location";
    public static String IS_ACTIVE = "isActive";
    public static String IS_FINAL = "isFinal";
    public static String EDIT = "edit";
    public static String COPY = "copy";
    public static String RENAME = "rename";
    public static String SQL_EXPORT = "sqlExport";
    public static String XLS_EXPORT = "xlsExport";
    public static String DELETE = "delete";

    private int id;
    private String date;
    private String name;
    private String location;
    private BooleanProperty isActive = new SimpleBooleanProperty();
    private BooleanProperty isFinal = new SimpleBooleanProperty();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ObservableBooleanValue isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive.set(isActive);
    }

    public ObservableBooleanValue isFinal() {
        return isFinal;
    }

    public void setFinal(boolean isFinal) {
        this.isFinal.set(isFinal);
    }

    @Override
    public String toString() {
        return "Questionnaire [id=" + id + ", date=" + date + ", name=" + name + ", ort=" + location + ", activ=" + isActive
                + ", isFinal=" + isFinal + "]";
    }

}
