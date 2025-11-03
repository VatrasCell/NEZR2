package de.vatrascell.nezr.validation;

import de.vatrascell.nezr.model.Validation;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ValidationMapper {

    Validation mapValidation(de.vatrascell.nezr.validation.Validation validation);

    de.vatrascell.nezr.validation.Validation mapValidation(Validation validation);
}
