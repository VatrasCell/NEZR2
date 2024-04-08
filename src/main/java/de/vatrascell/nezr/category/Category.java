package de.vatrascell.nezr.category;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Category {

    @Id
    private int id;
    private String name;

    public Category(String name) {
        this.name = name;
    }
}
