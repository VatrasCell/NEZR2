package de.vatrascell.nezr.validation;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "validation")
public class Validation {

    @Id
    private long validationId;
    private boolean isNumbers;
    private boolean isLetters;
    private boolean isAlphanumeric;
    private boolean isAllChars;
    private boolean isRegex;
    private boolean hasLength;
    private String regex;
    private Integer minLength;
    private Integer maxLength;
    private Integer length;
}
