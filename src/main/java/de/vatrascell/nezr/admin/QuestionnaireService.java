package de.vatrascell.nezr.admin;

import de.vatrascell.nezr.application.Database;
import de.vatrascell.nezr.application.util.DateUtil;
import de.vatrascell.nezr.model.Location;
import de.vatrascell.nezr.model.Questionnaire;
import de.vatrascell.nezr.questionList.QuestionListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionnaireService extends Database {

    private final QuestionnaireRepository questionnaireRepository;

    private final QuestionListService questionListService;

    private final DateUtil dateUtil;

    public List<Questionnaire> getQuestionnaires(String location) {

        return questionnaireRepository.findByLocationName(location)
                .stream()
                .map(this::convert)
                .toList();
    }

    public void activateQuestionnaire(long questionnaireId) {

        questionnaireRepository.setQuestionnaireActiveById(questionnaireId);
        questionnaireRepository.setAllOtherQuestionnairesInactiveById(questionnaireId);
    }

    public void disableQuestionnaire(long questionnaireId) {
        questionnaireRepository.setQuestionnaireInactiveById(questionnaireId);
    }

    public void copyQuestionnaire(Questionnaire questionnaire, Location location) {
        questionnaireRepository.save(de.vatrascell.nezr.admin.Questionnaire.builder()
                .creationDate(LocalDateTime.now())
                .isActive(false)
                .isFinal(false)
                .location(de.vatrascell.nezr.location.Location.builder().name(location.getName()).build())
                .build());

        //TODO add copy questions
        /*questionnaire.setId(createQuestionnaire(questionnaire.getName(), de.vatrascell.nezr.location));
        List<Question> questions = QuestionListService.getQuestions(questionnaire.getId());
        for (Question de.vatrascell.nezr.question : Objects.requireNonNull(questions)) {
            if (de.vatrascell.nezr.question.getQuestionType().equals(QuestionType.SHORT_ANSWER)) {
                QuestionService.saveShortAnswerQuestion(questionnaire.getId(), de.vatrascell.nezr.question);
            } else {
                QuestionService.saveMultipleChoice(questionnaire.getId(), de.vatrascell.nezr.question);
            }
        }
        return true;*/
    }

    public void deleteQuestionnaire(long questionnaireId) {
        questionnaireRepository.deleteById(questionnaireId);

        //TODO add delete cascade
        /*
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            myCon.setAutoCommit(false);

            List<Integer> multipleChoiceIds = questionListService.getQuestionsByQuestionnaireId(questionnaireId, QuestionType.MULTIPLE_CHOICE);

            if (multipleChoiceIds != null) {
                multipleChoiceIds.forEach(questionId -> {
                    try {
                        questionListService.deleteQuestion(myCon, questionnaireId, questionId, QuestionType.MULTIPLE_CHOICE);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }

            List<Integer> shortAnswerIds = questionListService.getQuestionsByQuestionnaireId(questionnaireId, QuestionType.SHORT_ANSWER);

            if (shortAnswerIds != null) {
                shortAnswerIds.forEach(questionId -> {
                    try {
                        questionListService.deleteQuestion(myCon, questionnaireId, questionId, QuestionType.SHORT_ANSWER);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }

            deleteQuestionnaire(myCon, questionnaireId);

            myCon.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;*/
    }

    public void renameQuestionnaire(Questionnaire questionnaire) {
        questionnaireRepository.updateNameByQuestionnaireId(questionnaire.getName(), questionnaire.getId());
    }

    public void updateIsFinal(boolean isFinal, Questionnaire questionnaire) {
        questionnaireRepository.updateIsFinalByQuestionnaireId(isFinal, questionnaire.getId());
    }

    public Questionnaire createQuestionnaire(String name) {
        return convert(questionnaireRepository.save(
                de.vatrascell.nezr.admin.Questionnaire.builder()
                        .creationDate(LocalDateTime.now())
                        .name(name)
                        .build()));
    }

    private Questionnaire convert(de.vatrascell.nezr.admin.Questionnaire questionnaire) {
        return new Questionnaire(
                questionnaire.getQuestionnaireId(),
                questionnaire.getCreationDate(),
                questionnaire.getName(),
                questionnaire.getLocation().getName(),
                questionnaire.isActive(),
                questionnaire.isFinal()
        );
    }
}
