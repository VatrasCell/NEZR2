package de.vatrascell.nezr.admin;

import de.vatrascell.nezr.application.Main;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Main.class)
@ActiveProfiles("test")
class QuestionnaireRepositoryTest {

    @Autowired
    private QuestionnaireRepository questionnaireRepository;

    @Test
    void testFindAll() {
        questionnaireRepository.setQuestionnaireActiveById(1);

        List<Questionnaire> questionnaires = questionnaireRepository.findAll();

        assertThat(questionnaires).isNotNull();
    }

}