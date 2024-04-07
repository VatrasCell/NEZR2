package de.vatrascell.nezr.location;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LocationRepositoryTest {


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