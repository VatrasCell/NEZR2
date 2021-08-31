package de.vatrascell.nezr.export.model.converter;

import de.vatrascell.nezr.export.model.ExcelQuestionModel;
import de.vatrascell.nezr.model.AnswerOption;
import de.vatrascell.nezr.model.Question;
import de.vatrascell.nezr.model.QuestionType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExcelQuestionModelConverter {

    public static ExcelQuestionModel convert(Question question, int startPosition) {
        ExcelQuestionModel model = new ExcelQuestionModel();
        model.setQuestion(question);
        List<String> answerOptions = new ArrayList<>();
        if (question.getQuestionType().equals(QuestionType.SHORT_ANSWER) || question.getFlags().isSingleLine()) {
            answerOptions.add(question.getQuestion());
        } else {
            answerOptions.addAll(question.getAnswerOptions().stream().map(AnswerOption::getValue).collect(Collectors.toList()));
        }
        model.setAnswerOptions(answerOptions);
        model.setFistCellPosition(startPosition);
        model.setLastCellPosition(startPosition + answerOptions.size() - 1);

        return model;
    }

    public static List<ExcelQuestionModel> convert(List<Question> questions, int startPosition) {
        List<ExcelQuestionModel> excelQuestionModels = new ArrayList<>();
        for (Question question : questions) {
            ExcelQuestionModel excelQuestionModel = convert(question, startPosition);
            startPosition = excelQuestionModel.getLastCellPosition() + 1;
            excelQuestionModels.add(excelQuestionModel);
        }
        return excelQuestionModels;
    }
}
