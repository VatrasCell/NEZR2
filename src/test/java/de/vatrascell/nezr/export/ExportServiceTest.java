package de.vatrascell.nezr.export;

import de.vatrascell.nezr.util.DBTestUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * {@link ExportService}
 */
public class ExportServiceTest extends DBTestUtil {

    @Test
    public void testGetSurveyCount() {
        //act
        int result = ExportService.getSurveyCount();

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