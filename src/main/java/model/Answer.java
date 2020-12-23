package model;

import java.util.Objects;

public class Answer {

    public static final String ID = "id";
    public static final String VALUE = "value";

    private int id;
    private String value;

    public Answer() {
    }

    public Answer(String value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
        Answer answer = (Answer) o;
        return getId() == answer.getId() &&
                Objects.equals(getValue(), answer.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getValue());
    }
}
