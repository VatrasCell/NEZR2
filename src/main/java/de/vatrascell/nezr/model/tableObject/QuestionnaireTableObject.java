package de.vatrascell.nezr.model.tableObject;

import de.vatrascell.nezr.admin.AdminController;
import javafx.scene.control.Button;
import de.vatrascell.nezr.model.Questionnaire;

public class QuestionnaireTableObject extends Questionnaire {

    private Button edit;
    private Button copy;
    private Button rename;
    private Button sqlExport;
    private Button xlsExport;
    private Button delete;

    public QuestionnaireTableObject() {
        edit = AdminController.initEditButton(this);
        copy = AdminController.initCopyButton(this);
        rename = AdminController.initRenameButton(this);
        sqlExport = AdminController.initSqlExportButton(this);
        xlsExport = AdminController.initXlsExportButton(this);
        delete = AdminController.initDeleteButton(this);
    }

    public Button getEdit() {
        return edit;
    }

    public void setEdit(Button edit) {
        this.edit = edit;
    }

    public Button getCopy() {
        return copy;
    }

    public void setCopy(Button copy) {
        this.copy = copy;
    }

    public Button getRename() {
        return rename;
    }

    public void setRename(Button rename) {
        this.rename = rename;
    }

    public Button getSqlExport() {
        return sqlExport;
    }

    public void setSqlExport(Button sqlExport) {
        this.sqlExport = sqlExport;
    }

    public Button getXlsExport() {
        return xlsExport;
    }

    public void setXlsExport(Button xlsExport) {
        this.xlsExport = xlsExport;
    }

    public Button getDelete() {
        return delete;
    }

    public void setDelete(Button delete) {
        this.delete = delete;
    }
}
