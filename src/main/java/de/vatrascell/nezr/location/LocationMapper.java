package de.vatrascell.nezr.location;

import de.vatrascell.nezr.model.Location;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LocationMapper {

    Location mapLocation(de.vatrascell.nezr.location.Location location);

    de.vatrascell.nezr.location.Location mapLocation(Location location);
}
