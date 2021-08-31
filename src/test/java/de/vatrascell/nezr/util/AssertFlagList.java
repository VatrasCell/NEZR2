package de.vatrascell.nezr.util;

import de.vatrascell.nezr.flag.FlagList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AssertFlagList {

    public static void equals(FlagList excepted, FlagList actual) {
        assertNotNull(excepted);
        assertNotNull(actual);
        assertEquals(excepted.isEvaluationQuestion(), actual.isEvaluationQuestion());
        assertEquals(excepted.isRequired(), actual.isRequired());
        assertEquals(excepted.isMultipleChoice(), actual.isMultipleChoice());
        assertEquals(excepted.isList(), actual.isList());
        assertEquals(excepted.isYesNoQuestion(), actual.isYesNoQuestion());
        assertEquals(excepted.isSingleLine(), actual.isSingleLine());
        assertEquals(excepted.isTextArea(), actual.isTextArea());
    }
}
