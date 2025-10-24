package de.vatrascell.nezr.answerOption;

import de.vatrascell.nezr.model.AnswerOption;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AnswerOptionMapper {

    AnswerOption mapAnswerOption(de.vatrascell.nezr.answerOption.AnswerOption answerOption);
}
