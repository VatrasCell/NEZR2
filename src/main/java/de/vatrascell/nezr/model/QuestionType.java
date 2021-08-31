package de.vatrascell.nezr.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum QuestionType {
    SHORT_ANSWER("FF"),
    MULTIPLE_CHOICE("MC");

    public static final String MULTIPLE_CHOICE_STRING = "Multiple Choice";
    public static final String SHORT_ANSWER_STRING = "Freie Frage";

    private static final Map<String, QuestionType> lookup = new HashMap<>();
    private String questionType;

    static {
        for (QuestionType env : QuestionType.values()) {
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
        return this.equals(SHORT_ANSWER) ? SHORT_ANSWER_STRING : MULTIPLE_CHOICE_STRING;
    }

    public static List<String> toList() {
        return Arrays.asList(MULTIPLE_CHOICE_STRING, SHORT_ANSWER_STRING);
    }
}
