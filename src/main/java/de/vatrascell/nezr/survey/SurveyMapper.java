package de.vatrascell.nezr.survey;

import de.vatrascell.nezr.model.Survey;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SurveyMapper {

    Survey mapSurvey(de.vatrascell.nezr.survey.Survey survey);
}
