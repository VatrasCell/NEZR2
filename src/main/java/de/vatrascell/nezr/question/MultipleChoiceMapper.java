package de.vatrascell.nezr.question;

import de.vatrascell.nezr.flag.FlagListMapper;
import de.vatrascell.nezr.model.Question;
import de.vatrascell.nezr.react.ReactMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {FlagListMapper.class, ReactMapper.class})
public interface MultipleChoiceMapper {

    @Mapping(target = "questionnaireId", source = "questionnaireId")
    @Mapping(target = "questionId", source = "multipleChoiceQuestion.multipleChoiceId")
    @Mapping(target = "flags", source = "multipleChoiceQuestion.flagListMultipleChoice")
    @Mapping(target = "flags.reacts", source = "multipleChoiceQuestion.reacts")
    Question mapMultipleChoiceQuestion(MultipleChoiceQuestion multipleChoiceQuestion, long questionnaireId);
}
