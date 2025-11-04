package de.vatrascell.nezr.export.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ExcelCell {
    private long surveyId;
    private List<String> answers;
}
