package de.vatrascell.nezr.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private long categoryId;
    private String name;

    public Category(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
