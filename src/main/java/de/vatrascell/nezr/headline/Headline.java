package de.vatrascell.nezr.headline;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Headline {

    @Id
    private int id;
    private String name;

    public Headline(String name) {
        this.name = name;
    }

}
