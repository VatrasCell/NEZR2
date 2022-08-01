package de.vatrascell.nezr.model;

import de.vatrascell.nezr.flag.FlagList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class Question implements Comparable<Question>, Comparator<Question> {

    public static final String QUESTION = "question";
    public static final String CATEGORY = "category";
    public static final String POSITION = "position";
    public static final String QUESTION_TYPE = "questionType";
    public static String EDIT = "edit";
    public static String DELETE = "delete";

    private String question;
    private Integer questionId;
    private QuestionType questionType;
    private Category category;
    private String date;
    private FlagList flags;
    private int position;
    private Headline headline;
    private SubmittedAnswer submittedAnswer = new SubmittedAnswer();
    private List<AnswerOption> answerOptions = new ArrayList<>();
    private Label questionLabel;
    private Pane scene;
    private List<CheckBox> answerCheckBoxes = new ArrayList<>();
    private TextField answerTextField;
    private ListView<AnswerOption> answerOptionListView;
    private TextArea answerTextArea;
    private Question target;
    private int questionnaireId;

    /**
     *
     */
    public Question() {
        super();
    }

    public Question(int size) {
        this.question = "";
        this.questionType = QuestionType.MULTIPLE_CHOICE;
        this.position = ++size;
        this.flags = new FlagList();
    }

    @Override
    public int compareTo(Question o) {
        return Integer.compare(this.getPosition(), o.getPosition());
    }

    @Override
    public int compare(Question o1, Question o2) {
        return o1.compareTo(o2);
    }

    @Override
    public String toString() {
        return question;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question1 = (Question) o;
        return getPosition() == question1.getPosition() &&
                getQuestionnaireId() == question1.getQuestionnaireId() &&
                Objects.equals(getQuestion(), question1.getQuestion()) &&
                Objects.equals(getQuestionId(), question1.getQuestionId()) &&
                getQuestionType() == question1.getQuestionType() &&
                Objects.equals(getCategory(), question1.getCategory()) &&
                Objects.equals(getDate(), question1.getDate()) &&
                Objects.equals(getFlags(), question1.getFlags()) &&
                Objects.equals(getHeadline(), question1.getHeadline()) &&
                Objects.equals(getSubmittedAnswer(), question1.getSubmittedAnswer()) &&
                Objects.equals(getAnswerOptions(), question1.getAnswerOptions()) &&
                Objects.equals(getQuestionLabel(), question1.getQuestionLabel()) &&
                Objects.equals(getScene(), question1.getScene()) &&
                Objects.equals(getAnswerCheckBoxes(), question1.getAnswerCheckBoxes()) &&
                Objects.equals(getAnswerTextField(), question1.getAnswerTextField()) &&
                Objects.equals(getAnswerOptionListView(), question1.getAnswerOptionListView()) &&
                Objects.equals(getAnswerTextArea(), question1.getAnswerTextArea()) &&
                Objects.equals(getTarget(), question1.getTarget());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getQuestion(), getQuestionId(), getQuestionType(), getCategory(),
                getDate(), getFlags(), getPosition(), getHeadline(), getSubmittedAnswer(),
                getAnswerOptions(), getQuestionLabel(), getScene(), getAnswerCheckBoxes(),
                getAnswerTextField(), getAnswerOptionListView(), getAnswerTextArea(),
                getTarget(), getQuestionnaireId());
    }

    public String toDebugString() {
        return "Question [de.vatrascell.nezr.question=" + question + ", type=" + questionType + ", category=" + category + ", flags=" + flags
                + ", Position=" + position + ", headline=" + headline + ", answerOptions="
                + answerOptions + "]";
    }
}
