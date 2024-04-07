package de.vatrascell.nezr.location;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Location {

    @Id
    private long locationId;
    private String name;
}
