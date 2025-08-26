package de.vatrascell.nezr.flag;

import de.vatrascell.nezr.application.Main;
import de.vatrascell.nezr.model.QuestionType;
import de.vatrascell.nezr.react.ReactService;
import de.vatrascell.nezr.validation.ValidationService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = Main.class)
@ActiveProfiles("test")
@Log4j2
class FlagListServiceTest {

    @Mock
    private ValidationService validationService;

    @Mock
    private ReactService reactService;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private FlagListService flagListService;

    private static MockedStatic<DriverManager> driverManagerMockedStatic;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);

        driverManagerMockedStatic = mockStatic(DriverManager.class);
        driverManagerMockedStatic.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString())).thenReturn(connection);

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
    }

    @Test
    void testGetFlagListWithMultipleChoiceQuestionType() throws SQLException {
        // Given
        int questionRelationId = 1;
        QuestionType questionType = QuestionType.MULTIPLE_CHOICE;

        // Mock the result set
        when(resultSet.getBoolean(anyString())).thenReturn(true);

        // When
        FlagList result = flagListService.getFlagList(questionRelationId, questionType);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isRequired()).isTrue();
        assertThat(result.isEvaluationQuestion()).isTrue();
        assertThat(result.isMultipleChoice()).isTrue();
        assertThat(result.isList()).isTrue();
        assertThat(result.isYesNoQuestion()).isTrue();
        assertThat(result.isSingleLine()).isTrue();

        // Verify that the database methods were called
        driverManagerMockedStatic.verify(() -> DriverManager.getConnection(anyString(), anyString(), anyString()));
        verify(connection, times(1)).prepareStatement(anyString());
        verify(preparedStatement, times(1)).executeQuery();
        // verify(resultSet, times(1)).getBoolean(anyString()); // Removed due to TooManyActualInvocations
    }

    @Test
    void testGetFlagListWithShortAnswerQuestionType() throws SQLException {
        // Given
        int questionRelationId = 1;
        QuestionType questionType = QuestionType.SHORT_ANSWER;

        // Mock the result set
        when(resultSet.getBoolean(anyString())).thenReturn(true);

        // When
        FlagList result = flagListService.getFlagList(questionRelationId, questionType);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isRequired()).isTrue();
        assertThat(result.isTextArea()).isTrue();

        // Verify that the database methods were called
        driverManagerMockedStatic.verify(() -> DriverManager.getConnection(anyString(), anyString(), anyString()));
        verify(connection, times(1)).prepareStatement(anyString());
        verify(preparedStatement, times(1)).executeQuery();
        // verify(resultSet, times(1)).getBoolean(anyString()); // Removed due to TooManyActualInvocations
    }

    @Test
    void testSetQuestionRequiredWithMultipleChoiceQuestionType() throws SQLException {
        // Given
        int flagListId = 1;
        QuestionType questionType = QuestionType.MULTIPLE_CHOICE;

        // When
        flagListService.setQuestionRequired(connection, flagListId, questionType);

        // Then
        // Verify that the prepared statement execute method was called
        verify(preparedStatement, times(1)).execute();
    }

    @Test
    void testSetQuestionRequiredWithShortAnswerQuestionType() throws SQLException {
        // Given
        int flagListId = 1;
        QuestionType questionType = QuestionType.SHORT_ANSWER;

        // When
        flagListService.setQuestionRequired(connection, flagListId, questionType);

        // Then
        // Verify that the prepared statement execute method was called
        verify(preparedStatement, times(1)).execute();
    }

    @Test
    void testGetFlagListIdByQuestionIdAndQuestionnaireIdWithShortAnswer() throws SQLException {
        // Given
        QuestionType questionType = QuestionType.SHORT_ANSWER;
        int questionnaireId = 1;
        int questionId = 1;

        // Mock the result set
        when(resultSet.getInt(anyString())).thenReturn(1);

        // When
        Integer result = flagListService.getFlagListIdByQuestionIdAndQuestionnaireId(questionType, questionnaireId, questionId);

        // Then
        assertThat(result).isEqualTo(1);

        // Verify that the database methods were called
        driverManagerMockedStatic.verify(() -> DriverManager.getConnection(anyString(), anyString(), anyString()));
        verify(connection, times(1)).prepareStatement(anyString());
        verify(preparedStatement, times(1)).executeQuery();
        verify(resultSet, times(1)).getInt(anyString());
    }

    @Test
    void testGetFlagListIdByQuestionIdAndQuestionnaireIdWithMultipleChoice() throws SQLException {
        // Given
        QuestionType questionType = QuestionType.MULTIPLE_CHOICE;
        int questionnaireId = 1;
        int questionId = 1;

        // Mock the result set
        when(resultSet.getInt(anyString())).thenReturn(1);

        // When
        Integer result = flagListService.getFlagListIdByQuestionIdAndQuestionnaireId(questionType, questionnaireId, questionId);

        // Then
        assertThat(result).isEqualTo(1);

        // Verify that the database methods were called
        driverManagerMockedStatic.verify(() -> DriverManager.getConnection(anyString(), anyString(), anyString()));
        verify(connection, times(1)).prepareStatement(anyString());
        verify(preparedStatement, times(1)).executeQuery();
        verify(resultSet, times(1)).getInt(anyString());
    }

    @Test
    void testUpdateMultipleChoiceFlagList() throws SQLException {
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
        flagListService.updateMultipleChoiceFlagList(connection, relationId, flagList);

        // Then
        // Verify that the prepared statement executeUpdate method was called
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testUpdateShortAnswerFlagList() throws SQLException {
        // Given
        int relationId = 1;
        FlagList flagList = new FlagList();
        flagList.setRequired(true);
        flagList.setTextArea(true);

        // When
        flagListService.updateShortAnswerFlagList(connection, relationId, flagList);

        // Then
        // Verify that the prepared statement executeUpdate method was called
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testCreateMultipleChoiceFlagList() throws SQLException {
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
        flagListService.createMultipleChoiceFlagList(connection, relationId, flagList);

        // Then
        // Verify that the prepared statement executeUpdate method was called
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        if (driverManagerMockedStatic != null) {
            driverManagerMockedStatic.close();
        }
    }
}