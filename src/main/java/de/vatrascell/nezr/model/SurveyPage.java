package de.vatrascell.nezr.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class SurveyPage {

    private Map<String, Question> questions = new HashMap<>();
    private int pageNumber;
    private Headline headline;

    public void addQuestion(String key, Question question) {
        this.questions.put(key, question);
    }
}
