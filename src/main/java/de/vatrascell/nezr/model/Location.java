package de.vatrascell.nezr.model;

import de.vatrascell.nezr.flag.Default;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Location {

    String name;
    String logoPath;
    Coordinate coordinates;

    @Default
    public Location(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
