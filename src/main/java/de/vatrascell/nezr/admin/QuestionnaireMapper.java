package de.vatrascell.nezr.admin;

import de.vatrascell.nezr.model.Questionnaire;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface QuestionnaireMapper {
    QuestionnaireMapper INSTANCE = Mappers.getMapper(QuestionnaireMapper.class);

    @Mapping(target = "location", source = "location.name")
    @Mapping(target = "id", source = "questionnaireId")
    Questionnaire map(de.vatrascell.nezr.admin.Questionnaire questionnaire);
}
