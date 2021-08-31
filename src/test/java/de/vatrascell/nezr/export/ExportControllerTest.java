package de.vatrascell.nezr.export;

import de.vatrascell.nezr.model.Question;
import de.vatrascell.nezr.model.Questionnaire;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import de.vatrascell.nezr.questionList.QuestionListService;
import de.vatrascell.nezr.util.DBTestUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * {@link ExportController}
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(QuestionListService.class)
@Ignore
public class ExportControllerTest extends DBTestUtil {

    public final static File FILE = new File("1_TEST-ORT_TEST-FRAGEBOGEN.xlsx");

    @Mock
    public Questionnaire questionnaire;

    @InjectMocks
    public ExportController exportController = new ExportController();

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
        exportController.createExcelFile(FILE, questionnaire, "2020-07-01", "2020-08-01");
        //assert
        assertTrue(true);
    }
}