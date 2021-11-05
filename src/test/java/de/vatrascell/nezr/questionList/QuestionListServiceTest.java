package de.vatrascell.nezr.questionList;

import de.vatrascell.nezr.model.Question;
import de.vatrascell.nezr.util.DBTestUtil;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Ignore
public class QuestionListServiceTest extends DBTestUtil {

    @Test
    public void testGetMultipleChoiceQuestions() {

        //arrange
        addMultipleChoiceQuestion();

        //act
        List<Question> result = QuestionListService.getMultipleChoiceQuestions(1);

        //assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetShortAnswerQuestions() {

        //arrange
        addShortAnswerQuestion();

        //act
        List<Question> result = QuestionListService.getShortAnswerQuestions(1);

        //assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetQuestions() {

        //arrange
        addMultipleChoiceQuestion();
        addMultipleChoiceQuestionRequired();
        addShortAnswerQuestion();

        //act
        List<Question> result = QuestionListService.getQuestions(1);

        //assert
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    public void getHeadlines() {
    }

    @Test
    public void getHeadline() {
    }

    @Test
    public void deleteQuestion() {
    }

    @Test
    public void testDeleteQuestion() {
    }

    @Test
    public void deleteShortAnswerQuestion() {
    }

    @Test
    public void deleteShortAnswerHasAnswerRelation() {
    }

    @Test
    public void deleteMultipleChoiceQuestion() {
    }

    @Test
    public void deleteMultipleChoiceHasAnswerRelation() {
    }

    @Test
    public void doesMultipleChoiceQuestionExistsInOtherQuestionnaire() {
    }

    @Test
    public void doesShortAnswerQuestionExistsInOtherQuestionnaire() {
    }

    @Test
    public void deleteShortAnswerQuestionnaireRelation() {
    }

    @Test
    public void deleteMultipleChoiceQuestionnaireRelation() {
    }

    @Test
    public void getMultipleChoiceQuestionAnswers() {
    }

    @Test
    public void getMultipleChoiceQuestionsByQuestionnaireId() {
    }

    @Test
    public void getShortAnswerQuestionsByQuestionnaireId() {
    }
}