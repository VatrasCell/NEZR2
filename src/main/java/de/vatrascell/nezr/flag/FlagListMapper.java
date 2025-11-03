package de.vatrascell.nezr.flag;

import de.vatrascell.nezr.model.FlagList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FlagListMapper {

    @Mapping(source = "QMcRelationId", target = "id")
    FlagList mapFlagListMultipleChoice(FlagListMultipleChoice flagListMultipleChoice);

    @Mapping(source = "QSaRelationId", target = "id")
    FlagList mapFlagLisShortAnswer(FlagListShortAnswer flagListShortAnswer);
}
