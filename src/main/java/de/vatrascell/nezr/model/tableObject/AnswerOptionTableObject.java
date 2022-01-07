package de.vatrascell.nezr.model.tableObject;

import de.vatrascell.nezr.model.AnswerOption;
import de.vatrascell.nezr.question.QuestionController;
import javafx.scene.control.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AnswerOptionTableObject extends AnswerOption {

    private Button edit;
    private Button delete;

    @Autowired
    public AnswerOptionTableObject(QuestionController questionController) {
        edit = questionController.initEditButton(this);
        delete = questionController.initDeleteButton(this);
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
