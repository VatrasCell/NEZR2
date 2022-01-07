package de.vatrascell.nezr.model.tableObject.converter;

import de.vatrascell.nezr.admin.AdminController;
import de.vatrascell.nezr.model.Questionnaire;
import de.vatrascell.nezr.model.tableObject.QuestionnaireTableObject;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class QuestionnaireTableObjectConverter {

    public static QuestionnaireTableObject convert(Questionnaire questionnaire, AdminController adminController) {
        QuestionnaireTableObject tableObject = new QuestionnaireTableObject(adminController);
        tableObject.setId(questionnaire.getId());
        tableObject.setActive(questionnaire.isActive().get());
        tableObject.setDate(questionnaire.getDate());
        tableObject.setFinal(questionnaire.isFinal().get());
        tableObject.setLocation(questionnaire.getLocation());
        tableObject.setName(questionnaire.getName());

        return tableObject;
    }

    public static List<QuestionnaireTableObject> convert(List<Questionnaire> questionnaires, AdminController adminController) {
        return questionnaires.stream()
                .map(questionnaire -> QuestionnaireTableObjectConverter.convert(questionnaire, adminController))
                .collect(Collectors.toList());
    }
}
