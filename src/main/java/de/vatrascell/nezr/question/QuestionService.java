package de.vatrascell.nezr.question;

import de.vatrascell.nezr.answerOption.AnswerOptionService;
import de.vatrascell.nezr.category.CategoryService;
import de.vatrascell.nezr.flag.FlagListService;
import de.vatrascell.nezr.flag.React;
import de.vatrascell.nezr.headline.HeadlineService;
import de.vatrascell.nezr.model.AnswerOption;
import de.vatrascell.nezr.model.Category;
import de.vatrascell.nezr.model.FlagList;
import de.vatrascell.nezr.model.Headline;
import de.vatrascell.nezr.model.Question;
import de.vatrascell.nezr.model.QuestionType;
import de.vatrascell.nezr.validation.ValidationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
@AllArgsConstructor
public class QuestionService {

    private final CategoryService categoryService;
    private final HeadlineService headlineService;
    private final FlagListService flagListService;
    private final AnswerOptionService answerOptionService;
    private final ValidationService validationService;

    private final MultipleChoiceQuestionRepository multipleChoiceQuestionRepository;
    private final ShortAnswerQuestionRepository shortAnswerQuestionRepository;

    public int getMaxQuestionPosition(int questionnaireId) {
        int maxPosMc = Objects.requireNonNull(getMaxPosition(questionnaireId, QuestionType.MULTIPLE_CHOICE));
        int maxPosFf = Objects.requireNonNull(getMaxPosition(questionnaireId, QuestionType.SHORT_ANSWER));

        return Math.max(maxPosFf, maxPosMc);
    }

    private Integer getMaxPosition(int questionnaireId, QuestionType questionType) {
        if (questionType.equals(QuestionType.MULTIPLE_CHOICE)) {
            return multipleChoiceQuestionRepository.findMaxPositionByQuestionnaireId((long) questionnaireId);
        } else {
            return shortAnswerQuestionRepository.findMaxPositionByQuestionnaireId((long) questionnaireId);
        }
    }

    // TODO rework method
    /*public void getPossibleFlags(FlagList flags, QuestionEditParam param) {
        if (param.isRequired()) {
            flags.add(new Symbol(SymbolType.REQUIRED));
        }
        if (param.isList()) {
            flags.add(new Symbol(SymbolType.LIST));
        }
        if (param.isMultipleChoice()) {
            flags.add(new Symbol(SymbolType.MC));
        }
        if (param.isTextArea()) {
            flags.add(new Symbol(SymbolType.TEXT));
        }
        if (param.isYesNoQuestion()) {
            flags.add(new Symbol(SymbolType.JN));
            if (param.isSingleLine()) {
                flags.add(new Symbol(SymbolType.JNExcel));
            }
        }

        /*if (param.isNumeric()) {
            if (param.getNumberType().equals("Größer gleich Zahl")) {
                flags.add(new Number(NumberOperator.GTE, param.getCountChars()));
            }

            if (param.getNumberType().equals("Kleiner gleich Zahl")) {
                flags.add(new Number(NumberOperator.LTE, param.getCountChars()));
            }

            if (param.getNumberType().equals("Genau wie die Zahl")) {
                flags.add(new Number(NumberOperator.EQ, param.getCountChars()));
            }
        }

        if (param.isEvaluationQuestion()) {
            flags.add(new Symbol(SymbolType.B));
        }
    }*/

    //TODO duplicateQuestion

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveShortAnswerQuestion(int questionnaireId, Question question) {
        // de.vatrascell.nezr.react
        for (React react : question.getFlags().getReacts()) {
            if (question.getFlags().isRequired()) {
                provideQuestionRequired(questionnaireId, react.getQuestionType());
            }
        }

        // category
        Category category = categoryService.createCategory(question.getCategory().getName());

        //headline
        Headline headline = question.getHeadline() == null ? null : headlineService.createHeadline(question.getHeadline().getName());

        // de.vatrascell.nezr.question
        Integer shortAnswerId = provideShortAnswerQuestion(question.getQuestion(), category.getId());

        categoryService.setCategoryOnQuestion(category.getId(), shortAnswerId, question.getQuestionType());

        if (headline != null) {
            headlineService.setHeadlineOnQuestion(headline.getId(), shortAnswerId, question.getQuestionType());
        }

        // questionnaire
        Integer relationId = getQuestionQuestionnaireRelationId(questionnaireId, shortAnswerId, question.getQuestionType());

        if (relationId != null) {
            flagListService.updateShortAnswerFlagList(relationId, question.getFlags());
            setPositionOnShortAnswerQuestionnaireRelation(question.getPosition(), relationId);
        } else {
            Integer validationId = null;
            if (question.getFlags().getValidation() != null) {
                validationId = validationService.save(question.getFlags().getValidation()).getId();
            }
            createShortAnswerQuestionnaireRelation(questionnaireId, shortAnswerId, question.getPosition(), validationId);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveMultipleChoice(int questionnaireId, Question question) {
        // de.vatrascell.nezr.react
        for (React react : question.getFlags().getReacts()) {
            if (question.getFlags().isRequired()) {
                provideQuestionRequired(questionnaireId, react.getQuestionType());
            }
        }

        // answerOptions
        question.getAnswerOptions().stream()
                .filter(answerOption -> answerOption.getAnswerOptionId() == null)
                .forEach(answerOption -> answerOption.setAnswerOptionId(answerOptionService.provideAnswerOptionId(answerOption.getName())));

        // category
        Category category = categoryService.createCategory(question.getCategory().getName());

        //headline
        Headline headline = question.getHeadline() == null ? null : headlineService.createHeadline(question.getHeadline().getName());

        // de.vatrascell.nezr.question
        Integer multipleChoiceId = provideMultipleChoiceQuestion(question.getQuestion(), category.getId());

        categoryService.setCategoryOnQuestion(category.getId(), multipleChoiceId, question.getQuestionType());
        if (headline != null) {
            headlineService.setHeadlineOnQuestion(headline.getId(), multipleChoiceId, question.getQuestionType());
        }

        // answers
        List<Integer> oldRelationIds = getMultipleChoiceAnswerOptionsRelationIds(Objects.requireNonNull(multipleChoiceId));
        List<Integer> newRelationIds = new ArrayList<>();

        for (AnswerOption answerOption : question.getAnswerOptions()) {
            Integer relationId = getMultipleChoiceAnswersRelationId(multipleChoiceId, answerOption.getAnswerOptionId());
            if (relationId != null) {
                newRelationIds.add(relationId);
            } else {
                answerOptionService.createMultipleChoiceAnswerOptionsRelation(multipleChoiceId, answerOption.getAnswerOptionId());
            }
        }

        for (int oldRelationId : oldRelationIds) {
            if (!newRelationIds.contains(oldRelationId)) {
                answerOptionService.deleteMultipleChoiceAnswerOptionsRelation(oldRelationId);
            }
        }

        // questionnaire
        Integer relationId = getQuestionQuestionnaireRelationId(questionnaireId, multipleChoiceId, question.getQuestionType());

        if (relationId != null) {
            newRelationIds.add(relationId);

            flagListService.updateMultipleChoiceFlagList(relationId, question.getFlags());
            setPositionOnMultipleChoiceQuestionnaireRelation(question.getPosition(), relationId);
        } else {
            createMultipleChoiceQuestionnaireRelation(questionnaireId, multipleChoiceId, question.getPosition());
            relationId = getQuestionQuestionnaireRelationId(questionnaireId, multipleChoiceId, question.getQuestionType());

            flagListService.createMultipleChoiceFlagList(relationId, question.getFlags());
        }
    }

    public boolean doQuestionExistsInQuestionnaire(String question, int questionnaireId, QuestionType questionType) {
        Integer questionId = getQuestionId(question, questionType);
        if (questionId != null) {
            return getQuestionQuestionnaireRelationId(questionnaireId, questionId, questionType) != null;
        }

        return false;
    }

    public List<Integer> getMultipleChoiceAnswerOptionsRelationIds(int multipleChoiceId) {
        return multipleChoiceQuestionRepository.findRelationIdsByMultipleChoiceId((long) multipleChoiceId)
                .stream()
                .map(Long::intValue)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public Integer getMultipleChoiceAnswersRelationId(int multipleChoiceId, int answerId) {
        Long relationId = multipleChoiceQuestionRepository.findRelationIdByIds((long) multipleChoiceId, (long) answerId);
        return relationId != null ? relationId.intValue() : null;
    }

    public Integer getQuestionQuestionnaireRelationId(int questionnaireId, int questionId, QuestionType questionType) {
        Long relationId = questionType.equals(QuestionType.MULTIPLE_CHOICE) ?
                multipleChoiceQuestionRepository.findRelationIdByQuestionnaireAndQuestion((long) questionnaireId, (long) questionId) :
                shortAnswerQuestionRepository.findRelationIdByQuestionnaireAndQuestion((long) questionnaireId, (long) questionId);
        return relationId != null ? relationId.intValue() : null;
    }

    public Integer getQuestionId(String question, QuestionType questionType) {
        Long questionId = questionType.equals(QuestionType.MULTIPLE_CHOICE) ?
                multipleChoiceQuestionRepository.findIdByQuestion(question) :
                shortAnswerQuestionRepository.findIdByQuestion(question);
        return questionId != null ? questionId.intValue() : null;
    }

    private void deleteFlagsFromTargetQuestion(int questionnaireId, int questionId) {
        deleteMultipleChoiceReactFlagsFromTargetQuestion(questionnaireId, questionId);
        deleteShortAnswerReactFlagsFromTargetQuestion(questionnaireId, questionId);
    }

    private void deleteShortAnswerReactFlagsFromTargetQuestion(int questionnaireId, int questionId) {
        List<Object[]> relations = shortAnswerQuestionRepository.findTargetQuestionRelationsForShortAnswer(questionId, questionnaireId);
        for (Object[] row : relations) {
            Long relationId = ((Number) row[0]).longValue();
            Long targetQuestionId = ((Number) row[1]).longValue();

            FlagList flags = flagListService.getFlagList(relationId.intValue(), QuestionType.SHORT_ANSWER);

            if (flags != null && !flags.getReacts().isEmpty()) {
                flags.setReacts(null);
                flagListService.setQuestionRequired(getQuestionQuestionnaireRelationId(questionnaireId, questionId, QuestionType.SHORT_ANSWER), QuestionType.SHORT_ANSWER);
            }
        }
    }

    private void deleteMultipleChoiceReactFlagsFromTargetQuestion(int questionnaireId, int questionId) {
        List<Object[]> relations = multipleChoiceQuestionRepository.findTargetQuestionRelationsForMultipleChoice(questionId, questionnaireId);
        for (Object[] row : relations) {
            Long relationId = ((Number) row[0]).longValue();
            Long targetQuestionId = ((Number) row[1]).longValue();

            FlagList flags = flagListService.getFlagList(relationId.intValue(), QuestionType.MULTIPLE_CHOICE);

            if (flags != null && !flags.getReacts().isEmpty()) {
                flags.setReacts(null);
                flagListService.setQuestionRequired(getQuestionQuestionnaireRelationId(questionnaireId, questionId, QuestionType.MULTIPLE_CHOICE), QuestionType.MULTIPLE_CHOICE);
            }
        }
    }

    private void provideQuestionRequired(int questionnaireId, QuestionType questionType) {
        FlagList flagList = flagListService.getFlagList(questionnaireId, questionType);
        if (!flagList.isRequired()) {
            flagList.setRequired(true);

            flagListService.setQuestionRequired(flagList.getId(), questionType);
        }
    }


    private Integer provideMultipleChoiceQuestion(String question, int categoryId) {
        Integer multipleChoiceId = getQuestionId(question, QuestionType.MULTIPLE_CHOICE);

        if (multipleChoiceId == null) {
            multipleChoiceQuestionRepository.createMultipleChoice(question, categoryId);
            multipleChoiceId = getQuestionId(question, QuestionType.MULTIPLE_CHOICE);
        }

        return multipleChoiceId;
    }


    private Integer provideShortAnswerQuestion(String question, int categoryId) {
        Integer shortAnswerId = getQuestionId(question, QuestionType.SHORT_ANSWER);

        if (shortAnswerId == null) {
            shortAnswerQuestionRepository.createShortAnswer(question, categoryId);
            shortAnswerId = getQuestionId(question, QuestionType.SHORT_ANSWER);
        }

        return shortAnswerId;
    }

    private void setPositionOnMultipleChoiceQuestionnaireRelation(int position, int relationId) {
        multipleChoiceQuestionRepository.updatePosition(position, (long) relationId);
    }

    private void setPositionOnShortAnswerQuestionnaireRelation(int position, int relationId) {
        shortAnswerQuestionRepository.updatePosition(position, (long) relationId);
    }

    private void createMultipleChoiceQuestionnaireRelation(int questionnaireId, int multipleChoiceId, int position) {
        multipleChoiceQuestionRepository.createRelation(questionnaireId, multipleChoiceId, position);
    }

    private void createShortAnswerQuestionnaireRelation(int questionnaireId, int shortAnswerId, int position, Integer validationId) {
        if (validationId != null) {
            shortAnswerQuestionRepository.createRelationWithValidation(questionnaireId, shortAnswerId, position, validationId);
        } else {
            shortAnswerQuestionRepository.createRelation(questionnaireId, shortAnswerId, position);
        }
    }
}
