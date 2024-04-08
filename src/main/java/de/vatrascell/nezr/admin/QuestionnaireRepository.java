package de.vatrascell.nezr.admin;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface QuestionnaireRepository extends JpaRepository<Questionnaire, Long> {

    @Override
    List<Questionnaire> findAll();

    List<Questionnaire> findByLocationName(String locationName);

    @Modifying
    @Query("UPDATE Questionnaire q SET q.isActive = TRUE WHERE q.questionnaireId = :questionnaireId")
    void setQuestionnaireActiveById(@Param("questionnaireId") long questionnaireId);

    @Modifying
    @Query("UPDATE Questionnaire q SET q.isActive = FALSE WHERE q.questionnaireId = :questionnaireId")
    void setQuestionnaireInactiveById(@Param("questionnaireId") long questionnaireId);

    @Modifying
    @Query("UPDATE Questionnaire q SET q.isActive = FALSE WHERE NOT q.questionnaireId = :questionnaireId")
    void setAllOtherQuestionnairesInactiveById(@Param("questionnaireId") long questionnaireId);

    @Modifying
    @Query("UPDATE Questionnaire q SET q.name = :name WHERE q.questionnaireId = :questionnaireId")
    void updateNameByQuestionnaireId(@Param("name") String name, @Param("questionnaireId") long questionnaireId);

    @Modifying
    @Query("UPDATE Questionnaire q SET q.isFinal = :isFinal WHERE q.questionnaireId = :questionnaireId")
    void updateIsFinalByQuestionnaireId(@Param("isFinal") boolean isFinal, @Param("questionnaireId") long questionnaireId);

    Optional<Questionnaire> findByName(String name);

}
