package de.vatrascell.nezr.relation;

import de.vatrascell.nezr.answerOption.AnswerOption;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "survey_has_answer_option")
public class SurveyHasAnswerOptionRelation {

    @Id
    private long sAoRelationId;
    @ManyToOne
    @JoinColumn(name = "answer_option_id")
    private AnswerOption answerOption;
    @ManyToOne
    @JoinColumn(name = "s_mc_relation_id")
    private SurveyHasMultipleChoiceRelation surveyHasMultipleChoiceRelation;
}
