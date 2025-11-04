package de.vatrascell.nezr.react;

import de.vatrascell.nezr.flag.React;
import de.vatrascell.nezr.model.QuestionType;
import de.vatrascell.nezr.relation.MultipleChoiceHasReactRelationRepository;
import de.vatrascell.nezr.relation.ShortAnswerHasReactRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReactService {

    private final MultipleChoiceHasReactRelationRepository multipleChoiceHasReactRelationRepository;
    private final ShortAnswerHasReactRelationRepository shortAnswerHasReactRelationRepository;

    public List<React> getReacts(int questionRelationId, QuestionType questionType) {
        List<de.vatrascell.nezr.react.React> entities;
        if (questionType.equals(QuestionType.SHORT_ANSWER)) {
            entities = shortAnswerHasReactRelationRepository.findReactsByRelationId(questionRelationId);
        } else {
            entities = multipleChoiceHasReactRelationRepository.findReactsByRelationId(questionRelationId);
        }

        return entities.stream()
                .map(entity -> new React(
                        (int) entity.getReactId(),
                        entity.getShortAnswerQuestion() != null ? QuestionType.SHORT_ANSWER : QuestionType.MULTIPLE_CHOICE,
                        entity.getShortAnswerQuestion() != null ? (int) entity.getShortAnswerQuestion().getShortAnswerId() : (int) entity.getMultipleChoiceQuestion().getMultipleChoiceId(),
                        entity.getAnswerPosition()))
                .collect(Collectors.toList());
    }
}
