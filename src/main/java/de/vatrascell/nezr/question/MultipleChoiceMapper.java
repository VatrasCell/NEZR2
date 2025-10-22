package de.vatrascell.nezr.question;

import de.vatrascell.nezr.model.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MultipleChoiceMapper {
    MultipleChoiceMapper INSTANCE = Mappers.getMapper(MultipleChoiceMapper.class);

    @Mapping(target = "questionnaireId", source = "questionnaireId")
    @Mapping(target = "questionId", source = "multipleChoiceQuestion.multipleChoiceId")
    Question mapMultipleChoiceQuestion(MultipleChoiceQuestion multipleChoiceQuestion, long questionnaireId);
}
