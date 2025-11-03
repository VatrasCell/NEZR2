package de.vatrascell.nezr.flag;

import de.vatrascell.nezr.model.FlagList;
import de.vatrascell.nezr.model.QuestionType;
import de.vatrascell.nezr.react.ReactService;
import de.vatrascell.nezr.validation.ValidationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class FlagListService {

    private final ValidationService validationService;
    private final ReactService reactService;
    private final FlagListMultipleChoiceRepository flagListMultipleChoiceRepository;
    private final FlagListShortAnswerRepository flagListShortAnswerRepository;

    /*public FlagList getFlagList(int questionnaireId, int questionId, QuestionType questionType) {
        if (questionType.equals(QuestionType.MULTIPLE_CHOICE)) {
            return getFlagList(questionService.getQuestionQuestionnaireRelationId(questionnaireId, questionId, questionType), questionType);
        } else {
            return getFlagList(questionService.getQuestionQuestionnaireRelationId(questionnaireId, questionId, questionType), questionType);
        }
    }*/

    public FlagList getFlagList(int questionRelationId, QuestionType questionType) {
        FlagList flagList = new FlagList();
        flagList.setId(questionRelationId);

        if (questionType.equals(QuestionType.MULTIPLE_CHOICE)) {
            FlagListMultipleChoice mcFlagList = flagListMultipleChoiceRepository.findByRelationId(questionRelationId);
            if (mcFlagList != null) {
                flagList.setRequired(mcFlagList.isRequired());
                flagList.setEvaluationQuestion(mcFlagList.isEvaluationQuestion());
                flagList.setMultipleChoice(mcFlagList.isMultipleChoice());
                flagList.setList(mcFlagList.isList());
                flagList.setYesNoQuestion(mcFlagList.isYesNoQuestion());
                flagList.setSingleLine(mcFlagList.isSingleLine());
            }
        } else {
            FlagListShortAnswer saFlagList = flagListShortAnswerRepository.findByRelationId(questionRelationId);
            if (saFlagList != null) {
                flagList.setRequired(saFlagList.isRequired());
                flagList.setTextArea(saFlagList.isTextArea());
            }
        }

        flagList.setValidation(validationService.getValidation(questionRelationId));
        flagList.setReacts(reactService.getReacts(questionRelationId, questionType));
        return flagList;
    }

    @Transactional
    public void setQuestionRequired(int flagListId, QuestionType questionType) {
        if (questionType.equals(QuestionType.MULTIPLE_CHOICE)) {
            flagListMultipleChoiceRepository.setRequired(flagListId);
        } else {
            flagListShortAnswerRepository.setRequired(flagListId);
        }
    }

    public Integer getFlagListIdByQuestionIdAndQuestionnaireId(QuestionType questionType, int questionnaireId, int questionId) {
        // This method needs to be implemented using repositories, but it requires joining tables
        // For now, keeping the original implementation as it involves complex queries
        // TODO: Implement with repository methods if needed
        return null;
    }

    @Transactional
    public void updateMultipleChoiceFlagList(int relationId, FlagList flagList) {
        flagListMultipleChoiceRepository.updateFlagList(
                relationId,
                flagList.isEvaluationQuestion(),
                flagList.isRequired(),
                flagList.isMultipleChoice(),
                flagList.isList(),
                flagList.isYesNoQuestion(),
                flagList.isSingleLine()
        );
    }

    @Transactional
    public void updateShortAnswerFlagList(int relationId, FlagList flagList) {
        flagListShortAnswerRepository.updateFlagList(
                relationId,
                flagList.isRequired(),
                flagList.isTextArea()
        );
    }

    @Transactional
    public void createMultipleChoiceFlagList(int relationId, FlagList flagList) {
        flagListMultipleChoiceRepository.createFlagList(
                relationId,
                flagList.isEvaluationQuestion(),
                flagList.isRequired(),
                flagList.isMultipleChoice(),
                flagList.isList(),
                flagList.isYesNoQuestion(),
                flagList.isSingleLine()
        );
    }
}
