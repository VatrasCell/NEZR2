package de.vatrascell.nezr.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
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

    private long id;
    private LocalDateTime creationDate;
    private String name;
    private String location;
    private BooleanProperty isActive = new SimpleBooleanProperty();
    private BooleanProperty isFinal = new SimpleBooleanProperty();

    public Questionnaire(long id, LocalDateTime creationDate, String name, String location, boolean isActive, boolean isFinal) {
        this.id = id;
        this.creationDate = creationDate;
        this.name = name;
        this.location = location;
        this.isActive.set(isActive);
        this.isFinal.set(isFinal);
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
}
