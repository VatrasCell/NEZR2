package de.vatrascell.nezr.export;

import de.vatrascell.nezr.export.model.ExcelCell;
import de.vatrascell.nezr.model.AnswerOption;
import de.vatrascell.nezr.model.Question;
import de.vatrascell.nezr.model.QuestionType;
import de.vatrascell.nezr.survey.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
//TODO obsolete?
public class ExportService {

    private final SurveyRepository surveyRepository;

    public int getSurveyCount() {
        return surveyRepository.findAll().size();
    }

    public ArrayList<ExcelCell> getAnswerPositions(Question question, String fromDate, String toDate) {
        ArrayList<ExcelCell> excelCells = new ArrayList<>();
        if (isFlaggedMultipleChoiceQuestion(question)) {
            excelCells.addAll(getMultipleChoiceAnswerCells(question.getQuestionnaireId(), question.getQuestionId(), fromDate, toDate));
        } else if (question.getQuestionType().equals(QuestionType.SHORT_ANSWER)) {
            excelCells.addAll(getShortAnswerAnswerCells(question.getQuestionnaireId(), question.getQuestionId(), fromDate, toDate));
        }
        return excelCells;
    }

    public ArrayList<ExcelCell> getAnswerPositions(Question question, AnswerOption answerOption, String fromDate, String toDate) {
        return surveyRepository.findSurveysByQuestionnaireIdAndDateRange(question.getQuestionnaireId(), fromDate, toDate)
                .stream()
                .filter(survey -> surveyRepository.findMultipleChoiceAnswersBySurveyIdAndQuestionId(survey.getSurveyId(), question.getQuestionId())
                        .stream()
                        .anyMatch(ao -> ao.getAnswerOptionId().equals(answerOption.getAnswerOptionId())))
                .map(survey -> new ExcelCell(survey.getSurveyId(), List.of("1")))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private boolean isFlaggedMultipleChoiceQuestion(Question question) {
        return ((question.getQuestionType().equals(QuestionType.MULTIPLE_CHOICE)) && (question.getFlags().isEvaluationQuestion()))
                || (question.getFlags().isList())
                || (question.getFlags().isYesNoQuestion());
    }

    private List<ExcelCell> getMultipleChoiceAnswerCells(long questionnaireId, long questionId, String fromDate, String toDate) {
        return surveyRepository.findSurveysByQuestionnaireIdAndDateRange(questionnaireId, fromDate, toDate)
                .stream()
                .map(survey -> {
                    List<String> answerNames = surveyRepository.findMultipleChoiceAnswersBySurveyIdAndQuestionId(survey.getSurveyId(), questionId)
                            .stream()
                            .map(de.vatrascell.nezr.answerOption.AnswerOption::getName)
                            .collect(Collectors.toList());
                    return new ExcelCell(survey.getSurveyId(), answerNames);
                })
                .collect(Collectors.toList());
    }

    private List<ExcelCell> getShortAnswerAnswerCells(long questionnaireId, long questionId, String fromDate, String toDate) {
        return surveyRepository.findSurveysByQuestionnaireIdAndDateRange((int) questionnaireId, fromDate, toDate)
                .stream()
                .map(survey -> {
                    String answer = surveyRepository.findShortAnswerBySurveyIdAndQuestionId(survey.getSurveyId(), questionId);
                    List<String> answers = answer != null ? List.of(answer) : List.of();
                    return new ExcelCell(survey.getSurveyId(), answers);
                })
                .collect(Collectors.toList());
    }
}
