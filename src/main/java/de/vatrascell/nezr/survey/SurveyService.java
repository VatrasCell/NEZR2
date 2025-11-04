package de.vatrascell.nezr.survey;

import de.vatrascell.nezr.answerOption.AnswerOptionMapper;
import de.vatrascell.nezr.application.Database;
import de.vatrascell.nezr.application.GlobalVars;
import de.vatrascell.nezr.model.AnswerOption;
import de.vatrascell.nezr.model.Question;
import de.vatrascell.nezr.model.QuestionType;
import de.vatrascell.nezr.model.SubmittedAnswer;
import de.vatrascell.nezr.model.Survey;
import de.vatrascell.nezr.model.SurveyPage;
import de.vatrascell.nezr.relation.SurveyHasAnswerOptionRelationRepository;
import de.vatrascell.nezr.relation.SurveyHasMultipleChoiceRelationRepository;
import de.vatrascell.nezr.relation.SurveyHasShortAnswerRelationRepository;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SurveyService extends Database {

    private final SurveyRepository surveyRepository;
    private final SurveyHasMultipleChoiceRelationRepository surveyHasMultipleChoiceRelationRepository;
    private final SurveyHasShortAnswerRelationRepository surveyHasShortAnswerRelationRepository;
    private final SurveyHasAnswerOptionRelationRepository surveyHasAnswerOptionRelationRepository;

    private final SurveyMapper surveyMapper;
    private final AnswerOptionMapper answerOptionMapper;

    @Transactional
    public void saveSurvey(int questionnaireId, List<SurveyPage> pages) {
        surveyRepository.createSurvey(questionnaireId);
        Integer surveyId = surveyRepository.findMaxSurveyId();
        if (surveyId == null) {
            throw new RuntimeException("Failed to create survey");
        }

        List<Question> questions = discardSecondDimension(pages);

        for (Question question : questions) {
            if (question.getQuestionType().equals(QuestionType.MULTIPLE_CHOICE)) {
                List<AnswerOption> submittedAnswerOptions = question.getSubmittedAnswer().getSubmittedAnswerOptions();
                var surveyMultipleChoiceRelation = surveyHasMultipleChoiceRelationRepository.findBySurveyIdAndQuestionId(surveyId, question.getQuestionId());
                if (surveyMultipleChoiceRelation.isEmpty()) {
                    surveyHasMultipleChoiceRelationRepository.createRelation(surveyId, question.getQuestionId());
                    surveyMultipleChoiceRelation = surveyHasMultipleChoiceRelationRepository.findBySurveyIdAndQuestionId(surveyId, question.getQuestionId());
                }
                long relationId = surveyMultipleChoiceRelation.get().getSMcRelationId();
                for (AnswerOption answerOption : submittedAnswerOptions) {
                    var existingRelation = surveyHasAnswerOptionRelationRepository.findByRelationIdAndAnswerId(relationId, answerOption.getAnswerOptionId());
                    if (existingRelation.isEmpty()) {
                        surveyHasAnswerOptionRelationRepository.createRelation(answerOption.getAnswerOptionId(), (int) relationId);
                    }
                }

            } else {
                var existingRelation = surveyHasShortAnswerRelationRepository.findBySurveyIdAndQuestionId(surveyId, question.getQuestionId());
                if (existingRelation.isEmpty()) {
                    surveyHasShortAnswerRelationRepository.createRelation(surveyId, question.getQuestionId(), question.getSubmittedAnswer().getSubmittedAnswerText());
                }
            }
        }

        resetQuestionnaire();
    }


    public void resetQuestionnaire() {

        for (ArrayList<Question> questions : GlobalVars.questionsPerPanel) {
            for (Question question : questions) {
                question.setSubmittedAnswer(null);
                for (CheckBox checkbox : question.getAnswerCheckBoxes()) {
                    checkbox.setSelected(false);
                }

                TextField textField = question.getAnswerTextField();
                if (textField != null) {
                    textField.setText("");
                }

                TextArea textArea = question.getAnswerTextArea();
                if (textArea != null) {
                    textArea.setText("");
                }

                ListView<AnswerOption> list = question.getAnswerOptionListView();
                if (list != null) {
                    list.getItems().clear();
                }

            }
        }
    }


    private List<Question> discardSecondDimension(List<SurveyPage> pages) {
        List<Question> results = new ArrayList<>();
        pages.forEach(page -> results.addAll(page.getQuestions()));

        return results;
    }

    public List<Survey> getSurveys(int questionnaireId, String fromDate, String toDate) {
        return surveyRepository.findSurveysByQuestionnaireIdAndDateRange(questionnaireId, fromDate, toDate)
                .stream()
                .map(surveyMapper::mapSurvey)
                .toList();
    }

    public SubmittedAnswer getAnswer(int surveyId, Question question) {
        if (question.getQuestionType().equals(QuestionType.SHORT_ANSWER)) {
            return getShortAnswerSubmittedAnswer(surveyId, question.getQuestionId());
        } else {
            return getMultipleChoiceSubmittedAnswer(surveyId, question.getQuestionId());
        }
    }

    private SubmittedAnswer getShortAnswerSubmittedAnswer(int surveyId, long questionId) {
        SubmittedAnswer submittedAnswer = new SubmittedAnswer();
        String answer = surveyRepository.findShortAnswerBySurveyIdAndQuestionId(surveyId, questionId);
        if (answer != null) {
            submittedAnswer.setSubmittedAnswerText(answer);
        }
        return submittedAnswer;
    }

    private SubmittedAnswer getMultipleChoiceSubmittedAnswer(int surveyId, long questionId) {
        SubmittedAnswer submittedAnswer = new SubmittedAnswer();
        List<AnswerOption> answerOptions =
                surveyRepository.findMultipleChoiceAnswersBySurveyIdAndQuestionId(surveyId, questionId)
                        .stream()
                        .map(answerOptionMapper::mapAnswerOption)
                        .toList();
        submittedAnswer.setSubmittedAnswerOptions(answerOptions);
        return submittedAnswer;
    }
}
