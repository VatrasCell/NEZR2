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
import survey.SurveyService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SurveyService.class)
public class ExportControllerTest {

    public final static String PATH = "1_TEST-ORT_TEST-FRAGEBOGEN.xlsx";

    @Mock
    public Questionnaire questionnaire;

    @InjectMocks
    public ExportController exportController = new ExportControllerImpl();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(SurveyService.class);
    }

    @Test
    public void testExcelExport() {
        //arrange
        List<Question> questions = new ArrayList<>();
        PowerMockito.mockStatic(SurveyService.class);
        when(SurveyService.getQuestions(anyInt())).thenReturn(questions);
        //act
        boolean result = exportController.excelNeu(PATH, questionnaire, "2020-07-01", "2020-08-01");
        //assert
        assertTrue(result);
    }
}