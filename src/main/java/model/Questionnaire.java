package model;

import admin.AdminController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.control.Button;

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
    private Button edit;
    private Button copy;
    private Button rename;
    private Button sqlExport;
    private Button xlsExport;
    private Button delete;

    public Questionnaire() {
        edit = AdminController.initEditButton(this);
        copy = AdminController.initCopyButton(this);
        rename = AdminController.initRenameButton(this);
        sqlExport = AdminController.initSqlExportButton(this);
        xlsExport = AdminController.initXlsExportButton(this);
        delete = AdminController.initDeleteButton(this);
    }

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

    @SuppressWarnings("unused")
    public Button getEdit() {
        return edit;
    }

    @SuppressWarnings("unused")
    public void setEdit(Button edit) {
        this.edit = edit;
    }

    @SuppressWarnings("unused")
    public Button getCopy() {
        return copy;
    }

    @SuppressWarnings("unused")
    public void setCopy(Button copy) {
        this.copy = copy;
    }

    @SuppressWarnings("unused")
    public Button getRename() {
        return rename;
    }

    @SuppressWarnings("unused")
    public void setRename(Button rename) {
        this.rename = rename;
    }

    @SuppressWarnings("unused")
    public Button getSqlExport() {
        return sqlExport;
    }

    @SuppressWarnings("unused")
    public void setSqlExport(Button sqlExport) {
        this.sqlExport = sqlExport;
    }

    @SuppressWarnings("unused")
    public Button getXlsExport() {
        return xlsExport;
    }

    @SuppressWarnings("unused")
    public void setXlsExport(Button xlsExport) {
        this.xlsExport = xlsExport;
    }

    @SuppressWarnings("unused")
    public Button getDelete() {
        return delete;
    }

    @SuppressWarnings("unused")
    public void setDelete(Button delete) {
        this.delete = delete;
    }

    @Override
    public String toString() {
        return "Questionnaire [id=" + id + ", date=" + date + ", name=" + name + ", ort=" + location + ", activ=" + isActive
                + ", isFinal=" + isFinal + "]";
    }

}
