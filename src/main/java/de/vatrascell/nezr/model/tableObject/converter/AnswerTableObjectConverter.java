package de.vatrascell.nezr.model.tableObject.converter;

import de.vatrascell.nezr.model.AnswerOption;
import de.vatrascell.nezr.model.tableObject.AnswerOptionTableObject;
import de.vatrascell.nezr.question.QuestionController;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class AnswerTableObjectConverter {

    public static AnswerOptionTableObject convert(AnswerOption answerOption, QuestionController questionController) {
        AnswerOptionTableObject tableObject = new AnswerOptionTableObject(questionController);
        tableObject.setId(answerOption.getId());
        tableObject.setValue(answerOption.getValue());

        return tableObject;
    }

    public static List<AnswerOptionTableObject> convert(List<AnswerOption> answerOptions, QuestionController questionController) {
        return answerOptions.stream()
                .map(answerOption -> AnswerTableObjectConverter.convert(answerOption, questionController))
                .collect(Collectors.toList());
    }
}
