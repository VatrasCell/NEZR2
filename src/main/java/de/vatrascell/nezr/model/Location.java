package de.vatrascell.nezr.model;

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

    public Location(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
