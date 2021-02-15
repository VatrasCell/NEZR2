package survey;

import org.junit.Test;
import util.DBTestUtil;

import static org.junit.Assert.*;

public class SurveyServiceTest extends DBTestUtil {

    @Test
    public void saveSurvey() {

        //arrange
        addQuestionnaire();
    }

    @Test
    public void createSurvey() {

        //arrange
        addQuestionnaire();

        //act
        int result = SurveyService.createSurvey(1);

        //assert
        assertEquals(1, result);
    }

    @Test
    public void resetQuestionnaire() {
    }
}