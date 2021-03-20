package model;

import javafx.scene.control.Button;
import question.QuestionController;

import java.util.Objects;

public class Answer {

    public static final String ID = "id";
    public static final String VALUE = "value";

    private int id;
    private String value;
    private Button edit;
    private Button delete;

    public Answer() {
        edit = QuestionController.initEditButton(this);
        delete = QuestionController.initDeleteButton(this);
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

    @SuppressWarnings("unused")
    public Button getEdit() {
        return edit;
    }

    @SuppressWarnings("unused")
    public void setEdit(Button edit) {
        this.edit = edit;
    }

    @SuppressWarnings("unused")
    public Button getDelete() {
        return delete;
    }

    @SuppressWarnings("unused")
    public void setDelete(Button delete) {
        this.delete = delete;
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

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", value='" + value + '\'' +
                '}';
    }
}
