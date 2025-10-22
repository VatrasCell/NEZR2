package de.vatrascell.nezr.validation;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Validation {

    @Id
    private int id;
    private boolean isNumbers;
    private boolean isLetters;
    private boolean isAlphanumeric;
    private boolean isAllChars;
    private boolean isRegex;
    private boolean hasLength;
    private String regex;
    private int minLength;
    private int maxLength;
    private int length;
}
