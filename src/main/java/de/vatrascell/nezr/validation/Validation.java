package de.vatrascell.nezr.validation;

public class Validation {

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isNumbers() {
        return isNumbers;
    }

    public void setNumbers(boolean numbers) {
        isNumbers = numbers;
    }

    public boolean isLetters() {
        return isLetters;
    }

    public void setLetters(boolean letters) {
        isLetters = letters;
    }

    public boolean isAlphanumeric() {
        return isAlphanumeric;
    }

    public void setAlphanumeric(boolean alphanumeric) {
        isAlphanumeric = alphanumeric;
    }

    public boolean isAllChars() {
        return isAllChars;
    }

    public void setAllChars(boolean allChars) {
        isAllChars = allChars;
    }

    public boolean isRegex() {
        return isRegex;
    }

    public void setRegex(boolean regex) {
        isRegex = regex;
    }

    public boolean isHasLength() {
        return hasLength;
    }

    public void setHasLength(boolean hasLength) {
        this.hasLength = hasLength;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "Validation{" +
                "id=" + id +
                ", isNumbers=" + isNumbers +
                ", isLetters=" + isLetters +
                ", isAlphanumeric=" + isAlphanumeric +
                ", isAllChars=" + isAllChars +
                ", isRegex=" + isRegex +
                ", hasLength=" + hasLength +
                ", regex='" + regex + '\'' +
                ", minLength=" + minLength +
                ", maxLength=" + maxLength +
                ", length=" + length +
                '}';
    }
}
