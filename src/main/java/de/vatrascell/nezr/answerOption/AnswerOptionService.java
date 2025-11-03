package de.vatrascell.nezr.answerOption;

import de.vatrascell.nezr.model.AnswerOption;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AnswerOptionService {

    private final AnswerOptionRepository answerOptionRepository;
    private final AnswerOptionMapper answerOptionMapper;

    public List<AnswerOption> getAnswerOptions(long questionId) {
        return answerOptionRepository.findAnswerOptionsByQuestionId(questionId)
                .stream().map(answerOptionMapper::mapAnswerOption)
                .toList();
    }

    public AnswerOption getAnswerOption(String name) {
        return answerOptionMapper.mapAnswerOption(answerOptionRepository.findByName(name));
    }

    public int provideAnswerOptionId(String answer) {
        AnswerOption existing = getAnswerOption(Objects.requireNonNull(answer));

        if (existing != null) {
            return existing.getAnswerOptionId();
        }

        createAnswerOption(answer);
        AnswerOption created = getAnswerOption(answer);
        return Objects.requireNonNull(created).getAnswerOptionId();
    }

    public void deleteAnswerOptions(ArrayList<Integer> answerIds, int multipleChoiceId) {
        for (Integer answerId : answerIds) {
            answerOptionRepository.deleteRelationByIds((long) multipleChoiceId, (long) answerId);
        }
        answerOptionRepository.deleteUnboundAnswerOptions();
    }

    public void deleteUnbindedAnswerOptions(Connection connection) {
        answerOptionRepository.deleteUnboundAnswerOptions();
    }

    public Integer getAnswerOptionId(String answer) {
        AnswerOption answerOption = getAnswerOption(answer);
        return answerOption != null ? answerOption.getAnswerOptionId() : null;
    }

    private void createAnswerOption(String answer) {
        var newOption = new de.vatrascell.nezr.answerOption.AnswerOption();
        newOption.setName(answer);
        answerOptionRepository.save(newOption);
    }

    public void deleteMultipleChoiceAnswerOptionsRelation(long relationId) {
        answerOptionRepository.deleteMultipleChoiceAnswerOptionsRelation(relationId);
    }

    private void deleteMultipleChoiceAnswerOptionsRelation(Connection connection, int answerId, int multipleChoiceId) {
        answerOptionRepository.deleteRelationByIds((long) multipleChoiceId, (long) answerId);
    }

    public void createMultipleChoiceAnswerOptionsRelation(long multipleChoiceId, long answerId) {
        answerOptionRepository.createMultipleChoiceAnswerOptionsRelation(multipleChoiceId, answerId);
    }
}
