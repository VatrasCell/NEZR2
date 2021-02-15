package question;

import application.NotificationController;
import flag.FlagList;
import message.MessageId;
import model.QuestionEditParam;
import model.QuestionType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import util.DBTestUtil;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

/**
 * {@link QuestionService}
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(NotificationController.class)
public class QuestionServiceTest extends DBTestUtil {

    @Before
    public void init() {
        MockitoAnnotations.initMocks(NotificationController.class);
        PowerMockito.mockStatic(NotificationController.class);
    }

    @Test
    public void testGetCategories() {

        //arrange
        List<String> categories = Arrays.asList("A1", "A2", "A3", "A4", "A5", "A6", "A7",
        "B1", "B2", "B3", "B4", "C1", "C2", "C3", "D1");

        addCategories();

        //act
        List<String> results = QuestionService.getCategories();

        //assert
        assertEquals(categories, results);
    }

    @Test
    @Ignore // rework getPossibleFlags method
    public void testGetPossibleFlags() {

        //arrange
        FlagList flagList = new FlagList();
        QuestionEditParam param = new QuestionEditParam(QuestionType.MULTIPLE_CHOICE, true, false, false,
                false, false, false, false, false, false,
                null, 0);

        //act
        QuestionService.getPossibleFlags(flagList, param);

        //assert
        assertEquals(flagList, flagList);
    }

    @Test
    public void testProvideQuestionRequired() {

        //arrange
        addMultipleChoiceQuestion();

        //act
        QuestionService.provideQuestionRequired(1, QuestionType.MULTIPLE_CHOICE, 1);

        //assert
        assertTrue(QuestionService.isQuestionRequired(1, QuestionType.MULTIPLE_CHOICE, 1));
    }

    @Test
    public void testGetFlagsEmpty() {

        //arrange
        addMultipleChoiceQuestion();

        //act
        String result = QuestionService.getFlags(1, QuestionType.MULTIPLE_CHOICE, 1);

        //assert
        assertEquals("", result);
    }

    @Test
    public void testGetFlags() {

        //arrange
        addMultipleChoiceQuestionRequired();

        //act
        String result = QuestionService.getFlags(1, QuestionType.MULTIPLE_CHOICE, 1);

        //assert
        assertEquals("+", result);
    }

    @Test
    public void testProvideCategoryAlreadyExists() {

        //arrange
        addCategories();

        //act
        QuestionService.provideCategory("A1");

        //assert
        verifyStatic(NotificationController.class);
        NotificationController.createErrorMessage(eq(MessageId.TITLE_CREATE_CATEGORY), eq(MessageId.MESSAGE_CATEGORY_ALREADY_EXISTS));

    }

    @Test
    public void testCreateCategory() {

        //arrange
        String category = "A1";

        //act
        QuestionService.createCategory(category);

        //assert
        verifyStatic(NotificationController.class);
        NotificationController.createMessage(
                eq(MessageId.TITLE_CREATE_CATEGORY), eq(MessageId.MESSAGE_CATEGORY_CREATED_SUCCESSFULLY), eq(category));

        List<String> result = QuestionService.getCategories();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(category, result.get(0));
    }

    @Test
    public void testCheckCategory() {

        //arrange
        addCategories();

        //act
        boolean result = QuestionService.checkCategory("A1");

        //assert
        assertTrue(result);
    }

    @Test
    public void testCheckCategoryFalse() {

        //arrange
        addCategories();

        //act
        boolean result = QuestionService.checkCategory("V1");

        //assert
        assertFalse(result);
    }

    @Test
    public void testGetMultipleChoiceId() {

        //arrange
        addMultipleChoiceQuestion();

        //act
        Integer result = QuestionService.getMultipleChoiceId("Geschlecht");

        //assert
        assertNotNull(result);
        assertEquals(1, result.intValue());
    }

    @Test
    public void testGetMultipleChoiceIdNull() {

        //act
        Integer result = QuestionService.getMultipleChoiceId("Geschlecht");

        //assert
        assertNull(result);
    }

    @Test
    public void testGetShortAnswerId() {

        //arrange
        addShortAnswerQuestion();

        //act
        Integer result = QuestionService.getShortAnswerId("Frage_1");

        //assert
        assertNotNull(result);
        assertEquals(1, result.intValue());
    }

    @Test
    public void testGetShortAnswerIdNull() {

        //act
        Integer result = QuestionService.getShortAnswerId("Frage_1");

        //assert
        assertNull(result);
    }

    @Test
    public void testGetCategoryId() {

        //arrange
        addCategories();

        //act
        Integer result = QuestionService.getCategoryId("A1");

        //assert
        assertNotNull(result);
        assertEquals(1, result.intValue());
    }

    @Test
    public void testGetCategoryIdNull() {

        //act
        Integer result = QuestionService.getCategoryId("A1");

        //assert
        assertNull(result);
    }

    @Test
    public void testProvideCategoryId() {

        //arrange
        addCategories();

        //act
        Integer result = QuestionService.provideCategoryId("A1");

        //assert
        assertNotNull(result);
        assertEquals(1, result.intValue());
    }

    @Test
    public void testProvideCategoryIdDoNotExists() {

        //act
        Integer result = QuestionService.provideCategoryId("A1");

        //assert
        assertNotNull(result);
        assertEquals(1, result.intValue());
    }

    @Test
    public void testCreateMultipleChoiceQuestion() {

        //arrange
        addCategories();

        //act
        QuestionService.createMultipleChoiceQuestion("Frage", 1);

        //assert
        Integer result = QuestionService.getMultipleChoiceId("Frage");

        assertNotNull(result);
        assertEquals(1, result.intValue());
    }

    @Test
    public void testProvideMultipleChoiceQuestion() {

        //arrange
        addMultipleChoiceQuestion();

        //act
        Integer result = QuestionService.provideMultipleChoiceQuestion("Geschlecht", 1);

        //assert
        assertNotNull(result);
        assertEquals(1, result.intValue());
    }

    @Test
    public void testProvideMultipleChoiceQuestionDoNotExists() {

        //arrange
        addCategories();

        //act
        Integer result = QuestionService.provideMultipleChoiceQuestion("Geschlecht", 1);

        //assert
        assertNotNull(result);
        assertEquals(1, result.intValue());
    }

    @Test
    public void testCreateShortAnswerQuestion() {

        //arrange
        addCategories();

        //act
        QuestionService.createShortAnswerQuestion("Frage_1", 1);

        //assert
        Integer result = QuestionService.getShortAnswerId("Frage_1");

        assertNotNull(result);
        assertEquals(1, result.intValue());
    }

    @Test
    public void testProvideShortAnswerQuestion() {

        //arrange
        addShortAnswerQuestion();

        //act
        Integer result = QuestionService.provideShortAnswerQuestion("Frage_1", 1);

        //assert
        assertNotNull(result);
        assertEquals(1, result.intValue());
    }

    @Test
    public void testProvideShortAnswerQuestionDoNotExists() {

        //arrange
        addCategories();

        //act
        Integer result = QuestionService.provideShortAnswerQuestion("Frage_1", 1);

        //assert
        assertNotNull(result);
        assertEquals(1, result.intValue());
    }

    @Test
    public void testGetMultipleChoiceQuestionnaireRelationIdEmpty() {

        //act
        Integer result = QuestionService.getMultipleChoiceQuestionnaireRelationId(1, 1);

        //assert
        assertNull(result);
    }

    @Test
    public void testGetMultipleChoiceQuestionnaireRelationId() {

        //arrange
        addMultipleChoiceQuestion();

        //act
        Integer result = QuestionService.getMultipleChoiceQuestionnaireRelationId(1, 1);

        //assert
        assertNotNull(result);
        assertEquals(1, result.intValue());
    }

    @Test
    public void testGetShortAnswerQuestionnaireRelationIdEmpty() {

        //act
        Integer result = QuestionService.getShortAnswerQuestionnaireRelationId(1, 1);

        //assert
        assertNull(result);
    }

    @Test
    public void getShortAnswerQuestionnaireRelationId() {

        //arrange
        addShortAnswerQuestion();

        //act
        Integer result = QuestionService.getShortAnswerQuestionnaireRelationId(1, 1);

        //assert
        assertNotNull(result);
        assertEquals(1, result.intValue());
    }

    @Test
    public void testGetMultipleChoiceAnswersRelationIdsEmpty() {

        //act
        List<Integer> result = QuestionService.getMultipleChoiceAnswersRelationIds(1);

        //assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void getMultipleChoiceAnswersRelationIds() {
    }

    @Test
    public void getMultipleChoiceAnswersRelationId() {
    }

    @Test
    public void createMultipleChoiceAnswersRelation() {
    }

    @Test
    public void deleteMultipleChoiceAnswersRelation() {
    }

    @Test
    public void testDeleteMultipleChoiceAnswersRelation() {
    }

    @Test
    public void setFlagsOnMultipleChoiceQuestionnaireRelation() {
    }

    @Test
    public void setFlagsOnShortAnswerQuestionnaireRelation() {
    }

    @Test
    public void setPositionOnMultipleChoiceQuestionnaireRelation() {
    }

    @Test
    public void setPositionOnShortAnswerQuestionnaireRelation() {
    }

    @Test
    public void createMultipleChoiceQuestionnaireRelation() {
    }

    @Test
    public void createShortAnswerQuestionnaireRelation() {
    }

    @Test
    public void testDeleteAnswers() {
    }

    @Test
    public void getAnswerId() {
    }

    @Test
    public void createAnswer() {
    }

    @Test
    public void getAnswers() {
    }

    @Test
    public void getCountPosition() {
    }

    @Test
    public void saveShortAnswerQuestion() {
    }

    @Test
    public void saveMultipleChoice() {
    }

    @Test
    public void saveEvaluationQuestion() {
    }

    @Test
    public void provideAnswerId() {
    }

    @Test
    public void deleteAnswers() {
    }
}