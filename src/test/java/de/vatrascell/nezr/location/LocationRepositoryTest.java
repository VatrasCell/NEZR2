package de.vatrascell.nezr.location;

import de.vatrascell.nezr.application.Main;
import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Main.class)
@ActiveProfiles("test")
class LocationRepositoryTest {

    private static final JFXPanel ignored = new JFXPanel();

    @Autowired
    private LocationRepository locationRepository;

    @Test
    void findAllShouldReturnAllLocations() {
        //arrange

        //act
        List<Location> locations = locationRepository.findAll();

        //assert
        assertThat(locations).isNotNull();
    }
}