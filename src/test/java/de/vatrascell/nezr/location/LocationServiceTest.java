package de.vatrascell.nezr.location;

import org.junit.Ignore;
import org.junit.Test;
import de.vatrascell.nezr.util.DBTestUtil;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * {@link LocationService}
 */
public class LocationServiceTest extends DBTestUtil {

    @Test
    @Ignore //TODO UTF 8 ist scheiße
    public void testGetLocations() {

        //arrange
        List<String> locations = Arrays.asList("Bayerischer Wald", "R\u00fcgen", "Saarschleife", "Schwarzwald", "Lipno");

        //act
        List<String> result = LocationService.getLocations();

        //assert
        assertEquals(locations, result);
    }
}