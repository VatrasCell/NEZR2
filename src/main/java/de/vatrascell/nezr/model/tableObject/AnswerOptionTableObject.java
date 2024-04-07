package de.vatrascell.nezr.model.tableObject;

import de.vatrascell.nezr.model.AnswerOption;
import de.vatrascell.nezr.question.QuestionController;
import javafx.scene.control.Button;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class AnswerOptionTableObject extends AnswerOption {

    private Button edit;
    private Button delete;

    @Autowired
    public AnswerOptionTableObject(QuestionController questionController) {
        edit = questionController.initEditButton(this);
        delete = questionController.initDeleteButton(this);
    }
}
