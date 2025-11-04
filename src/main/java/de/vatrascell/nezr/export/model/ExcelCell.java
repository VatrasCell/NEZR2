package de.vatrascell.nezr.export.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class ExcelCell {
    private int surveyId;
    private ArrayList<String> answers;
}
