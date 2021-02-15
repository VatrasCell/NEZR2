package model;

import java.util.HashMap;
import java.util.Map;

public enum QuestionType {
    SHORT_ANSWER("FF"),
    MULTIPLE_CHOICE("MC");

    private static final Map<String, QuestionType> lookup = new HashMap<>();
    private String questionType;

    static {
        for(QuestionType env : QuestionType.values())
        {
            lookup.put(env.getQuestionType(), env);
        }
    }

    QuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getQuestionType() {
        return questionType;
    }

    public static QuestionType get(String value) {
        return lookup.get(value);
    }

    @Override
    public String toString() {
        return this.equals(SHORT_ANSWER) ? "Freie Frage" : "Multiple Choice";
    }
}
