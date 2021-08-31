package de.vatrascell.nezr.model.tableObject.converter;

import de.vatrascell.nezr.model.Questionnaire;
import de.vatrascell.nezr.model.tableObject.QuestionnaireTableObject;

import java.util.List;
import java.util.stream.Collectors;

public class QuestionnaireTableObjectConverter {

    public static QuestionnaireTableObject convert(Questionnaire questionnaire) {
        QuestionnaireTableObject tableObject = new QuestionnaireTableObject();
        tableObject.setId(questionnaire.getId());
        tableObject.setActive(questionnaire.isActive().get());
        tableObject.setDate(questionnaire.getDate());
        tableObject.setFinal(questionnaire.isFinal().get());
        tableObject.setLocation(questionnaire.getLocation());
        tableObject.setName(questionnaire.getName());

        return tableObject;
    }

    public static List<QuestionnaireTableObject> convert(List<Questionnaire> questionnaires) {
        return questionnaires.stream().map(QuestionnaireTableObjectConverter::convert).collect(Collectors.toList());
    }
}
