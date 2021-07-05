package model;

import java.util.Objects;

public class AnswerOption {

    public static final String ID = "id";
    public static final String VALUE = "value";

    private Integer id;
    private String value;

    public AnswerOption() {
    }

    public AnswerOption(String value) {
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnswerOption that = (AnswerOption) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getValue());
    }

    @Override
    public String toString() {
        return this.value;
    }
}
