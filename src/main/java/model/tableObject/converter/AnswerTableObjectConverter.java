package model.tableObject.converter;

import model.AnswerOption;
import model.tableObject.AnswerOptionTableObject;

import java.util.List;
import java.util.stream.Collectors;

public class AnswerTableObjectConverter {

    public static AnswerOptionTableObject convert(AnswerOption answerOption) {
        AnswerOptionTableObject tableObject = new AnswerOptionTableObject();
        tableObject.setId(answerOption.getId());
        tableObject.setValue(answerOption.getValue());

        return tableObject;
    }

    public static List<AnswerOptionTableObject> convert(List<AnswerOption> answerOptions) {
        return answerOptions.stream().map(AnswerTableObjectConverter::convert).collect(Collectors.toList());
    }
}
