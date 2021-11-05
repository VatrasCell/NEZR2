package de.vatrascell.nezr.model.tableObject;

import de.vatrascell.nezr.model.Question;
import de.vatrascell.nezr.questionList.QuestionListController;
import javafx.scene.control.Button;

public class QuestionTableObject extends Question {

    private Button edit;
    private Button delete;

    public QuestionTableObject() {
        edit = QuestionListController.initEditButton(this);
        delete = QuestionListController.initDeleteButton(this);
    }

    public Button getEdit() {
        return edit;
    }

    public void setEdit(Button edit) {
        this.edit = edit;
    }

    public Button getDelete() {
        return delete;
    }

    public void setDelete(Button delete) {
        this.delete = delete;
    }
}
