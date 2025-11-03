package de.vatrascell.nezr.question;

import de.vatrascell.nezr.flag.FlagListMapper;
import de.vatrascell.nezr.model.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = FlagListMapper.class)
public interface ShortAnswerMapper {

    @Mapping(target = "questionnaireId", source = "questionnaireId")
    @Mapping(target = "questionId", source = "shortAnswerQuestion.shortAnswerId")
    @Mapping(target = "flags", source = "shortAnswerQuestion.flagListShortAnswer")
    Question mapShortAnswerQuestion(ShortAnswerQuestion shortAnswerQuestion, long questionnaireId);
}
