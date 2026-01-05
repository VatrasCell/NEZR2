package de.vatrascell.nezr.category;

import de.vatrascell.nezr.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {

    Category mapCategory(de.vatrascell.nezr.category.Category category);
}
