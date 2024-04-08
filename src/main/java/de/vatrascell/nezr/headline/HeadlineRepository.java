package de.vatrascell.nezr.headline;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HeadlineRepository extends JpaRepository<Headline, Long> {

    @Override
    List<Headline> findAll();

    Optional<Headline> findByName(String name);

}
