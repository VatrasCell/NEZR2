package model.tableObject.converter;

import model.Question;
import model.tableObject.QuestionTableObject;

import java.util.List;
import java.util.stream.Collectors;

public class QuestionTableObjectConverter {

    public static QuestionTableObject convert(Question question) {
        QuestionTableObject tableObject = new QuestionTableObject();
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

    public static List<QuestionTableObject> convert(List<Question> questions) {
        return questions.stream().map(QuestionTableObjectConverter::convert).collect(Collectors.toList());
    }
}
