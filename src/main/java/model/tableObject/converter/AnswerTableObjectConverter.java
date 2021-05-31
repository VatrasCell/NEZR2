package model.tableObject.converter;

import model.Answer;
import model.tableObject.AnswerTableObject;

import java.util.List;
import java.util.stream.Collectors;

public class AnswerTableObjectConverter {

    public static AnswerTableObject convert(Answer answer) {
        AnswerTableObject tableObject = new AnswerTableObject();
        tableObject.setId(answer.getId());
        tableObject.setValue(answer.getValue());

        return tableObject;
    }

    public static List<AnswerTableObject> convert(List<Answer> answers) {
        return answers.stream().map(AnswerTableObjectConverter::convert).collect(Collectors.toList());
    }
}
