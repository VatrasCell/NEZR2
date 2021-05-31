package model.tableObject;

import javafx.scene.control.Button;
import model.Question;
import question.QuestionController;
import questionList.QuestionListController;

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
