package de.vatrascell.nezr.admin;

import de.vatrascell.nezr.model.Questionnaire;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface QuestionnaireMapper {

    @Mapping(target = "location", source = "location.name")
    Questionnaire map(de.vatrascell.nezr.admin.Questionnaire questionnaire);
}
