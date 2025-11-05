package de.vatrascell.nezr.headline;

import de.vatrascell.nezr.model.Headline;
import de.vatrascell.nezr.model.QuestionType;
import de.vatrascell.nezr.question.MultipleChoiceQuestionRepository;
import de.vatrascell.nezr.question.ShortAnswerQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HeadlineService {

    private final HeadlineRepository headlineRepository;
    private final MultipleChoiceQuestionRepository multipleChoiceQuestionRepository;
    private final ShortAnswerQuestionRepository shortAnswerQuestionRepository;

    public List<Headline> getHeadlines() {
        return headlineRepository.findAll()
                .stream()
                .map(this::convert)
                .toList();
    }

    public Headline getHeadline(long headlineId) {
        return convert(headlineRepository.findById(headlineId)
                .orElseThrow());
    }

    public Headline getHeadlineByName(String name) {
        return convert(headlineRepository.findByName(name)
                .orElseThrow());
    }

    public Headline createHeadline(String name) {
        return convert(headlineRepository.findByName(name)
                .orElse(headlineRepository.save(new de.vatrascell.nezr.headline.Headline(name))));
    }

    public void setHeadlineOnQuestion(long headlineId, long questionId, QuestionType questionType) {
        if (questionType.equals(QuestionType.MULTIPLE_CHOICE)) {
            multipleChoiceQuestionRepository.updateHeadline(headlineId, questionId);
        } else {
            shortAnswerQuestionRepository.updateHeadline(headlineId, questionId);
        }
    }

    private Headline convert(de.vatrascell.nezr.headline.Headline headline) {
        return new Headline(headline.getHeadlineId(), headline.getName());
    }
}
