package export;

import export.impl.ExportControllerImpl;
import model.Question;
import model.Questionnaire;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import survey.SurveyService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExportControllerTest {

    public ExportController exportController = new ExportControllerImpl();
    public final static String PATH = "1_TEST-ORT_TEST-FRAGEBOGEN.xlsx";

    @Mock
    public Questionnaire questionnaire;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExcelExport() {
        //arrange
        List<Question> questions = new ArrayList<>();
        when(SurveyService.getFragen(any())).thenReturn(questions);
        //act
        boolean result = exportController.excelNeu(PATH, questionnaire, "2020-07-01", "2020-08-01");
        //assert
        assertTrue(result);
    }
}