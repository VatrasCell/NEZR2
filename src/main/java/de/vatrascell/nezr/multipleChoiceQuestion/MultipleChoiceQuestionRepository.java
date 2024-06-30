package de.vatrascell.nezr.multipleChoiceQuestion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MultipleChoiceQuestionRepository extends JpaRepository<MultipleChoiceQuestion, Long> {

    @Override
    List<MultipleChoiceQuestion> findAll();
}
