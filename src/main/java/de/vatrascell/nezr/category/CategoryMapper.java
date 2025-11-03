package de.vatrascell.nezr.category;

import de.vatrascell.nezr.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {

    @Mapping(source = "categoryId", target = "id")
    Category mapCategory(de.vatrascell.nezr.category.Category category);
}
