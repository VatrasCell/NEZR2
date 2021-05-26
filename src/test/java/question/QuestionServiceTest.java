package question;

import application.NotificationController;
import flag.FlagList;
import flag.FlagListService;
import message.MessageId;
import model.Category;
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
import util.AssertFlagList;
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
        List<Category> categories = Arrays.asList(
                new Category(1, "A1"),
                new Category(2, "A2"),
                new Category(3, "A3"),
                new Category(4, "A4"),
                new Category(5, "A5"),
                new Category(6, "A6"),
                new Category(7, "A7"),
                new Category(8, "B1"),
                new Category(9, "B2"),
                new Category(10, "B3"),
                new Category(11, "B4"),
                new Category(12, "C1"),
                new Category(13, "C2"),
                new Category(14, "C3"),
                new Category(15, "D1"));

        addCategories();

        //act
        List<Category> results = QuestionService.getCategories();

        //assert
        assertEquals(categories, results);
    }

    @Test
    @Ignore // rework getPossibleFlags method
    public void testGetPossibleFlags() {

        //arrange
        FlagList flagList = new FlagList();
        QuestionEditParam param = new QuestionEditParam(QuestionType.MULTIPLE_CHOICE, true, false, false,
                false, false, false, false, false,
                null, 0);

        //act
        //QuestionService.getPossibleFlags(flagList, param);

        //assert
        assertEquals(flagList, flagList);
    }

    @Test
    public void testProvideQuestionRequired() {

        //arrange
        addMultipleChoiceQuestion();

        //act
        QuestionService.provideQuestionRequired(1, QuestionType.MULTIPLE_CHOICE);

        //assert
        assertTrue(QuestionService.isQuestionRequired(1, QuestionType.MULTIPLE_CHOICE, 1));
    }

    @Test
    public void testGetFlagsEmpty() {

        //arrange
        addMultipleChoiceQuestion();

        //act
        FlagList result = FlagListService.getFlagList(1, QuestionType.MULTIPLE_CHOICE);

        //assert
        AssertFlagList.equals(new FlagList(), result);
    }

    @Test
    public void testGetFlags() {

        //arrange
        addMultipleChoiceQuestionRequired();

        //act
        FlagList result = FlagListService.getFlagList(1, QuestionType.MULTIPLE_CHOICE);

        //assert
        FlagList flagList = new FlagList();
        flagList.setRequired(true);
        AssertFlagList.equals(flagList, result);
    }

    @Test
    public void testProvideCategoryAlreadyExists() {

        //arrange
        addCategories();

        //act
        QuestionService.createUniqueCategory("A1");

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

        List<Category> result = QuestionService.getCategories();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(category, result.get(0).getName());
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
        Category result = QuestionService.getCategory("A1");

        //assert
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    public void testGetCategoryIdNull() {

        //act
        Category result = QuestionService.getCategory("A1");

        //assert
        assertNull(result);
    }

    @Test
    public void testProvideCategoryId() {

        //arrange
        addCategories();

        //act
        Category result = QuestionService.provideCategory("A1");

        //assert
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    public void testProvideCategoryIdDoNotExists() {

        //act
        Category result = QuestionService.provideCategory("A1");

        //assert
        assertNotNull(result);
        assertEquals(1, result.getId());
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