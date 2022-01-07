package de.vatrascell.nezr.model.tableObject;

import de.vatrascell.nezr.admin.AdminController;
import de.vatrascell.nezr.model.Questionnaire;
import javafx.scene.control.Button;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class QuestionnaireTableObject extends Questionnaire {

    private Button edit;
    private Button copy;
    private Button rename;
    private Button sqlExport;
    private Button xlsExport;
    private Button delete;

    @Autowired
    public QuestionnaireTableObject(AdminController adminController) {
        edit = adminController.initEditButton(this);
        copy = adminController.initCopyButton(this);
        rename = adminController.initRenameButton(this);
        sqlExport = adminController.initSqlExportButton(this);
        xlsExport = adminController.initXlsExportButton(this);
        delete = adminController.initDeleteButton(this);
    }
}
