package de.vatrascell.nezr.start;

import de.vatrascell.nezr.application.ScreenController;
import de.vatrascell.nezr.model.Questionnaire;
import de.vatrascell.nezr.model.SceneName;
import de.vatrascell.nezr.util.DBTestUtil;
import org.junit.Ignore;
import org.junit.Test;

import static de.vatrascell.nezr.application.GlobalFuncs.getURL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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