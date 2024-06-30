package de.vatrascell.nezr.multipleChoiceQuestion;

import de.vatrascell.nezr.application.Main;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Main.class)
@ActiveProfiles("test")
@Log4j2
class MultipleChoiceQuestionRepositoryTest {

    @Autowired
    private MultipleChoiceQuestionRepository multipleChoiceQuestionRepository;

    @Test
    void test() {
        List<MultipleChoiceQuestion> multipleChoiceQuestions = multipleChoiceQuestionRepository.findAll();

        assertThat(multipleChoiceQuestions).isNotNull();
        log.info(multipleChoiceQuestions);
    }

}