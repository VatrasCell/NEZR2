package de.vatrascell.nezr.landing;

import de.vatrascell.nezr.admin.QuestionnaireMapper;
import de.vatrascell.nezr.admin.QuestionnaireRepository;
import de.vatrascell.nezr.model.Questionnaire;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LandingService {

    private final QuestionnaireRepository questionnaireRepository;
    private final QuestionnaireMapper questionnaireMapper;

    public Questionnaire getActiveQuestionnaire() {

        return questionnaireMapper.map(questionnaireRepository.getQuestionnaireByIsActiveIsTrue());
    }
}
