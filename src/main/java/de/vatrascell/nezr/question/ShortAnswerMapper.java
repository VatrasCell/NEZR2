package de.vatrascell.nezr.question;

import de.vatrascell.nezr.model.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ShortAnswerMapper {
    ShortAnswerMapper INSTANCE = Mappers.getMapper(ShortAnswerMapper.class);

    @Mapping(target = "questionnaireId", source = "questionnaireId")
    @Mapping(target = "questionId", source = "shortAnswerQuestion.shortAnswerId")
    Question mapShortAnswerQuestion(ShortAnswerQuestion shortAnswerQuestion, long questionnaireId);
}
