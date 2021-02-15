package export;

import export.impl.ExportServiceImpl;
import org.junit.Test;
import util.DBTestUtil;

import static org.junit.Assert.assertEquals;

/**
 * {@link ExportServiceImpl}
 */
public class ExportServiceTest extends DBTestUtil {

    @Test
    public void testGetSurveyCount() {
        //act
        int result = ExportServiceImpl.getSurveyCount();

        //assert
        assertEquals(0, result);
    }

    @Test
    public void getAnswerPositions() {
    }

    @Test
    public void testGetAnswerPositions() {
    }
}