package application;

import message.MessageId;
import org.junit.Test;

import static org.junit.Assert.*;

public class PropertyLoaderTest {

    @Test
    public void getPropertyValue() {
        //act
        PropertyLoader propertyLoader = new PropertyLoader("message");
        String result = propertyLoader.getPropertyValue(MessageId.MESSAGE_UNDEFINED_ERROR);

        //assert
        assertEquals("Ein Fehler ist aufgetreten.", result);
    }
}