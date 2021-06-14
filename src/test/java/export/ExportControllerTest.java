package export;

import export.impl.ExportControllerImpl;
import model.Question;
import model.Questionnaire;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import questionList.QuestionListService;
import util.DBTestUtil;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyInt;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * {@link ExportController}
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(QuestionListService.class)
//TODO fix test
public class ExportControllerTest extends DBTestUtil {

    public final static String PATH = "1_TEST-ORT_TEST-FRAGEBOGEN.xlsx";

    @Mock
    public Questionnaire questionnaire;

    @InjectMocks
    public ExportController exportController = new ExportControllerImpl();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(QuestionListService.class);
    }

    @Test
    public void testExcelExport() {
        //arrange
        List<Question> questions = new ArrayList<>();
        PowerMockito.mockStatic(QuestionListService.class);
        when(QuestionListService.getQuestions(anyInt())).thenReturn(questions);
        //act
        //boolean result = exportController.createExcelFile(PATH, questionnaire, "2020-07-01", "2020-08-01");
        //assert
        //assertTrue(result);
    }
}