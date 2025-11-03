package de.vatrascell.nezr.flag;

import de.vatrascell.nezr.model.FlagList;
import de.vatrascell.nezr.model.QuestionType;
import de.vatrascell.nezr.react.ReactService;
import de.vatrascell.nezr.validation.ValidationService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Log4j2
class FlagListServiceTest {

    @Mock
    private ValidationService validationService;

    @Mock
    private ReactService reactService;

    @Mock
    private FlagListMultipleChoiceRepository flagListMultipleChoiceRepository;

    @Mock
    private FlagListShortAnswerRepository flagListShortAnswerRepository;

    @InjectMocks
    private FlagListService flagListService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetFlagListWithMultipleChoiceQuestionType() {
        // Given
        int questionRelationId = 1;
        QuestionType questionType = QuestionType.MULTIPLE_CHOICE;

        // Mock the repository and services
        FlagListMultipleChoice mcFlagList = new FlagListMultipleChoice();
        mcFlagList.setRequired(true);
        mcFlagList.setEvaluationQuestion(true);
        mcFlagList.setMultipleChoice(true);
        mcFlagList.setList(true);
        mcFlagList.setYesNoQuestion(true);
        mcFlagList.setSingleLine(true);

        when(flagListMultipleChoiceRepository.findByRelationId(questionRelationId)).thenReturn(mcFlagList);
        when(validationService.getValidation(questionRelationId)).thenReturn(new de.vatrascell.nezr.model.Validation());
        when(reactService.getReacts(questionRelationId, questionType)).thenReturn(List.of());

        // When
        FlagList result = flagListService.getFlagList(questionRelationId, questionType);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(questionRelationId);
        assertThat(result.isRequired()).isTrue();
        assertThat(result.isEvaluationQuestion()).isTrue();
        assertThat(result.isMultipleChoice()).isTrue();
        assertThat(result.isList()).isTrue();
        assertThat(result.isYesNoQuestion()).isTrue();
        assertThat(result.isSingleLine()).isTrue();

        // Verify repository and service calls
        verify(flagListMultipleChoiceRepository).findByRelationId(questionRelationId);
        verify(validationService).getValidation(questionRelationId);
        verify(reactService).getReacts(questionRelationId, questionType);
    }

    @Test
    void testGetFlagListWithShortAnswerQuestionType() {
        // Given
        int questionRelationId = 1;
        QuestionType questionType = QuestionType.SHORT_ANSWER;

        // Mock the repository and services
        FlagListShortAnswer saFlagList = new FlagListShortAnswer();
        saFlagList.setRequired(true);
        saFlagList.setTextArea(true);

        when(flagListShortAnswerRepository.findByRelationId(questionRelationId)).thenReturn(saFlagList);
        when(validationService.getValidation(questionRelationId)).thenReturn(new de.vatrascell.nezr.model.Validation());
        when(reactService.getReacts(questionRelationId, questionType)).thenReturn(List.of());

        // When
        FlagList result = flagListService.getFlagList(questionRelationId, questionType);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(questionRelationId);
        assertThat(result.isRequired()).isTrue();
        assertThat(result.isTextArea()).isTrue();

        // Verify repository and service calls
        verify(flagListShortAnswerRepository).findByRelationId(questionRelationId);
        verify(validationService).getValidation(questionRelationId);
        verify(reactService).getReacts(questionRelationId, questionType);
    }

    @Test
    void testSetQuestionRequiredWithMultipleChoiceQuestionType() {
        // Given
        int flagListId = 1;
        QuestionType questionType = QuestionType.MULTIPLE_CHOICE;

        // When
        flagListService.setQuestionRequired(flagListId, questionType);

        // Then
        // Verify that the repository method was called
        verify(flagListMultipleChoiceRepository).setRequired(flagListId);
    }

    @Test
    void testSetQuestionRequiredWithShortAnswerQuestionType() {
        // Given
        int flagListId = 1;
        QuestionType questionType = QuestionType.SHORT_ANSWER;

        // When
        flagListService.setQuestionRequired(flagListId, questionType);

        // Then
        // Verify that the repository method was called
        verify(flagListShortAnswerRepository).setRequired(flagListId);
    }

    @Test
    void testGetFlagListIdByQuestionIdAndQuestionnaireIdWithShortAnswer() {
        // Given
        QuestionType questionType = QuestionType.SHORT_ANSWER;
        int questionnaireId = 1;
        int questionId = 1;

        // When
        Integer result = flagListService.getFlagListIdByQuestionIdAndQuestionnaireId(questionType, questionnaireId, questionId);

        // Then
        // The method returns null as per current implementation (TODO: implement with repository)
        assertThat(result).isNull();
    }

    @Test
    void testGetFlagListIdByQuestionIdAndQuestionnaireIdWithMultipleChoice() {
        // Given
        QuestionType questionType = QuestionType.MULTIPLE_CHOICE;
        int questionnaireId = 1;
        int questionId = 1;

        // When
        Integer result = flagListService.getFlagListIdByQuestionIdAndQuestionnaireId(questionType, questionnaireId, questionId);

        // Then
        // The method returns null as per current implementation (TODO: implement with repository)
        assertThat(result).isNull();
    }

    @Test
    void testUpdateMultipleChoiceFlagList() {
        // Given
        int relationId = 1;
        FlagList flagList = new FlagList();
        flagList.setEvaluationQuestion(true);
        flagList.setRequired(true);
        flagList.setMultipleChoice(true);
        flagList.setList(true);
        flagList.setYesNoQuestion(true);
        flagList.setSingleLine(true);

        // When
        flagListService.updateMultipleChoiceFlagList(relationId, flagList);

        // Then
        // Verify that the repository method was called with correct parameters
        verify(flagListMultipleChoiceRepository).updateFlagList(
                relationId,
                true,
                true,
                true,
                true,
                true,
                true
        );
    }

    @Test
    void testUpdateShortAnswerFlagList() {
        // Given
        int relationId = 1;
        FlagList flagList = new FlagList();
        flagList.setRequired(true);
        flagList.setTextArea(true);

        // When
        flagListService.updateShortAnswerFlagList(relationId, flagList);

        // Then
        // Verify that the repository method was called with correct parameters
        verify(flagListShortAnswerRepository).updateFlagList(
                relationId,
                true, // isRequired
                true  // isTextArea
        );
    }

    @Test
    void testCreateMultipleChoiceFlagList() {
        // Given
        int relationId = 1;
        FlagList flagList = new FlagList();
        flagList.setEvaluationQuestion(true);
        flagList.setRequired(true);
        flagList.setMultipleChoice(true);
        flagList.setList(true);
        flagList.setYesNoQuestion(true);
        flagList.setSingleLine(true);

        // When
        flagListService.createMultipleChoiceFlagList(relationId, flagList);

        // Then
        // Verify that the repository method was called with correct parameters
        verify(flagListMultipleChoiceRepository).createFlagList(
                relationId,
                true,
                true,
                true,
                true,
                true,
                true
        );
    }
}
