package de.vatrascell.nezr.model.tableObject.converter;

import de.vatrascell.nezr.model.Question;
import de.vatrascell.nezr.model.tableObject.QuestionTableObject;
import de.vatrascell.nezr.questionList.QuestionListController;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class QuestionTableObjectConverter {

    public static QuestionTableObject convert(Question question, QuestionListController questionListController) {
        QuestionTableObject tableObject = new QuestionTableObject(questionListController);
        tableObject.setQuestion(question.getQuestion());
        tableObject.setQuestionId(question.getQuestionId());
        tableObject.setQuestionType(question.getQuestionType());
        tableObject.setCategory(question.getCategory());
        tableObject.setDate(question.getDate());
        tableObject.setFlags(question.getFlags());
        tableObject.setPosition(question.getPosition());
        tableObject.setHeadline(question.getHeadline());
        tableObject.setAnswerOptions(question.getAnswerOptions());
        tableObject.setSubmittedAnswer(question.getSubmittedAnswer());
        tableObject.setQuestionLabel(question.getQuestionLabel());
        tableObject.setScene(question.getScene());
        tableObject.setAnswerCheckBoxes(question.getAnswerCheckBoxes());
        tableObject.setAnswerTextField(question.getAnswerTextField());
        tableObject.setAnswerOptionListView(question.getAnswerOptionListView());
        tableObject.setAnswerTextArea(question.getAnswerTextArea());
        tableObject.setTarget(question.getTarget());
        tableObject.setQuestionnaireId(question.getQuestionnaireId());

        return tableObject;
    }

    public static List<QuestionTableObject> convert(List<Question> questions, QuestionListController questionListController) {
        return questions.stream()
                .map(question -> QuestionTableObjectConverter.convert(question, questionListController))
                .collect(Collectors.toList());
    }
}
