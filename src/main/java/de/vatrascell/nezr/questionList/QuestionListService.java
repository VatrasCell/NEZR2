package de.vatrascell.nezr.questionList;

import de.vatrascell.nezr.category.CategoryService;
import de.vatrascell.nezr.flag.FlagListService;
import de.vatrascell.nezr.headline.HeadlineService;
import de.vatrascell.nezr.model.Question;
import de.vatrascell.nezr.model.QuestionType;
import de.vatrascell.nezr.question.AnswerOption;
import de.vatrascell.nezr.question.AnswerOptionService;
import de.vatrascell.nezr.question.MultipleChoiceMapper;
import de.vatrascell.nezr.question.MultipleChoiceQuestion;
import de.vatrascell.nezr.question.MultipleChoiceQuestionRepository;
import de.vatrascell.nezr.question.ShortAnswerMapper;
import de.vatrascell.nezr.question.ShortAnswerQuestion;
import de.vatrascell.nezr.question.ShortAnswerQuestionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class QuestionListService {

    private final AnswerOptionService answerOptionService;
    private final CategoryService categoryService;
    private final FlagListService flagListService;
    private final HeadlineService headlineService;
    private final MultipleChoiceQuestionRepository multipleChoiceQuestionRepository;
    private final ShortAnswerQuestionRepository shortAnswerQuestionRepository;
    private final MultipleChoiceMapper multipleChoiceMapper;
    private final ShortAnswerMapper shortAnswerMapper;

    public List<Question> getQuestions(long questionnaireId) {
        List<Question> questions = new ArrayList<>();
        questions.addAll(Objects.requireNonNull(getMultipleChoiceQuestions(questionnaireId)));
        questions.addAll(Objects.requireNonNull(getShortAnswerQuestions(questionnaireId)));

        questions.sort(Comparator.comparing(Question::getPosition));

        return questions;
    }

    private List<Question> getMultipleChoiceQuestions(long questionnaireId) {
        List<MultipleChoiceQuestion> multipleChoiceQuestions =
                multipleChoiceQuestionRepository.findMultipleChoiceQuestionsByQuestionnaireId(questionnaireId);

        multipleChoiceQuestions.forEach(question -> {
            question.setAnswerOptions(multipleChoiceQuestionRepository.findAnswerOptionsWithQuestionIdsByQuestionId(
                    question.getMultipleChoiceId()));
        });

        return multipleChoiceQuestions.stream()
                .map((MultipleChoiceQuestion multipleChoiceQuestion) -> multipleChoiceMapper.mapMultipleChoiceQuestion(multipleChoiceQuestion, questionnaireId))
                .toList();
    }

    private List<Question> getShortAnswerQuestions(long questionnaireId) {
        List<ShortAnswerQuestion> shortAnswerQuestions =
                shortAnswerQuestionRepository.findShortAnswerQuestionsByQuestionnaireId(questionnaireId);
        return shortAnswerQuestions.stream()
                .map((ShortAnswerQuestion shortAnswerQuestion) -> shortAnswerMapper.mapShortAnswerQuestion(shortAnswerQuestion, questionnaireId))
                .toList();
    }

    public List<AnswerOption> getMultipleChoiceQuestionAnswers(int questionnaireId, int questionId) {
        //return multipleChoiceQuestionRepository.findMultipleChoiceQuestionAnswers(questionnaireId, questionId);
        return null;
    }

    public List<Integer> getQuestionsByQuestionnaireId(int questionnaireId, QuestionType questionType) {
        if (questionType.equals(QuestionType.MULTIPLE_CHOICE)) {
            return multipleChoiceQuestionRepository.findMultipleChoiceIdsByQuestionnaireId(questionnaireId);
        } else if (questionType.equals(QuestionType.SHORT_ANSWER)) {
            return shortAnswerQuestionRepository.findShortAnswerIdsByQuestionnaireId(questionnaireId);
        }
        return new ArrayList<>();
    }

    public boolean doesQuestionExistsInOtherQuestionnaire(int questionnaireId, int questionId, QuestionType questionType) {
        List<Integer> relations;
        if (questionType.equals(QuestionType.MULTIPLE_CHOICE)) {
            relations = multipleChoiceQuestionRepository.findOtherMultipleChoiceQuestionnaireRelationIds(questionnaireId, questionId);
        } else if (questionType.equals(QuestionType.SHORT_ANSWER)) {
            relations = shortAnswerQuestionRepository.findOtherShortAnswerQuestionnaireRelationIds(questionnaireId, questionId);
        } else {
            return true;
        }
        return !relations.isEmpty();
    }

    @Transactional
    public void deleteQuestion(int questionnaireId, Question question) {
        deleteQuestion(questionnaireId, question.getQuestionId(), question.getQuestionType());
    }

    @Transactional
    public void deleteQuestion(int questionnaireId, long questionId, QuestionType questionType) {
        //TODO refactor
        //QuestionService.deleteFlagsFromTargetQuestion(myCon, questionnaireId, questionId);
        /*
        if (questionType.equals(QuestionType.MULTIPLE_CHOICE)) {
            multipleChoiceQuestionRepository.deleteMultipleChoiceQuestionnaireRelation(questionnaireId, questionId);

            if (!doesQuestionExistsInOtherQuestionnaire(questionnaireId, questionId, questionType)) {
                multipleChoiceQuestionRepository.deleteMultipleChoiceHasAnswerRelation(questionId);
                multipleChoiceQuestionRepository.deleteMultipleChoiceQuestion(questionId);
            }
        } else if (questionType.equals(QuestionType.SHORT_ANSWER)) {
            shortAnswerQuestionRepository.deleteShortAnswerQuestionnaireRelation(questionnaireId, questionId);

            if (!doesQuestionExistsInOtherQuestionnaire(questionnaireId, questionId, questionType)) {
                shortAnswerQuestionRepository.deleteShortAnswerQuestion(questionId);
            }
        }

        answerOptionService.deleteUnbindedAnswerOptions();*/
    }

}
