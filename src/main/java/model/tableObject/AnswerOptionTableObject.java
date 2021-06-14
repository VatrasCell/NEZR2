package model.tableObject;

import javafx.scene.control.Button;
import model.AnswerOption;
import question.QuestionController;

public class AnswerOptionTableObject extends AnswerOption {

    private Button edit;
    private Button delete;

    public AnswerOptionTableObject() {
        edit = QuestionController.initEditButton(this);
        delete = QuestionController.initDeleteButton(this);
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
