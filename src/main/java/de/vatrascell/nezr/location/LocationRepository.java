package de.vatrascell.nezr.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    @Override
    List<Location> findAll();

    Location getByName(String name);

}
