package de.vatrascell.nezr.react;

import de.vatrascell.nezr.flag.React;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReactMapper {

    @Mapping(source = "reactId", target = "id")
    @Mapping(source = "shortAnswerQuestion.shortAnswerId", target = "shortAnswerId")
    @Mapping(source = "multipleChoiceQuestion.multipleChoiceId", target = "multipleChoiceId")
    React mapReact(de.vatrascell.nezr.react.React react);
}
