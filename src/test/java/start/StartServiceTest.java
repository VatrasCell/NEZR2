package start;

import model.Questionnaire;
import org.junit.Test;
import util.DBTestUtil;

import static org.junit.Assert.*;

public class StartServiceTest extends DBTestUtil {

    @Test
    public void testGetActiveQuestionnaire() {

        //arrange
        addQuestionnaire();

        //act
        Questionnaire result = StartService.getActiveQuestionnaire();

        //assert
        assertNotNull(result);
        assertEquals("Besucherumfrage", result.getName());
        assertTrue(result.isActive().get());
    }
}