package start;

import application.ScreenController;
import model.Questionnaire;
import model.SceneName;
import org.junit.Ignore;
import org.junit.Test;
import util.DBTestUtil;

import static application.GlobalFuncs.getURL;
import static org.junit.Assert.*;

public class StartServiceTest extends DBTestUtil {

    @Test
    @Ignore
    //TODO remove dependency
    public void testGetActiveQuestionnaire() {

        //arrange
        addQuestionnaire();
        ScreenController.addScreen(SceneName.ADMIN, getURL(SceneName.ADMIN_PATH));

        //act
        Questionnaire result = StartService.getActiveQuestionnaire();

        //assert
        assertNotNull(result);
        assertEquals("Besucherumfrage", result.getName());
        assertTrue(result.isActive().get());
    }
}