package model;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class FrageEditParam {
    private final QuestionType type;
    private final boolean list;
    private final boolean multipleChoice;
    private final boolean textarea;
    private final boolean yesNoQuestion;
    private final boolean singleLine;
    private final boolean numeric;
    private final boolean headline;
    private final boolean valuationAsk;
    private final String numberType;
    private final int countChars;

    private boolean required;

    public FrageEditParam(QuestionType type, boolean required, boolean list, boolean multipleChoice, boolean textarea,
                          boolean yesNoQuestion, boolean singleLine, boolean numeric, boolean headline, boolean valuationAsk,
                          String numberType, int countChars) {
        this.type = type;
        this.required = required;
        this.list = list;
        this.multipleChoice = multipleChoice;
        this.textarea = textarea;
        this.yesNoQuestion = yesNoQuestion;
        this.singleLine = singleLine;
        this.numeric = numeric;
        this.headline = headline;
        this.valuationAsk = valuationAsk;
        this.numberType = numberType;
        this.countChars = countChars;
    }

    public FrageEditParam(ChoiceBox<String> typeChoice, ChoiceBox<String> numberChoice, TextField txtCountChars,
                          CheckBox chckbxRequired, CheckBox chckbxMultipleChoice, CheckBox chckbxList, CheckBox chckbxTextArea,
                          CheckBox chckbxYesNoQuestion, CheckBox chckbxHeadline, CheckBox chckbxSingleLine, CheckBox chckbxNumeric) {
        switch (typeChoice.getSelectionModel().getSelectedItem()) {
            case "Multiple Choice":
                type = QuestionType.MULTIPLE_CHOICE;
                valuationAsk = false;
                break;
            case "Bewertungsfrage":
                type = QuestionType.MULTIPLE_CHOICE;
                valuationAsk = true;
                break;
            case "Freie Frage":
                type = QuestionType.SHORT_ANSWER;
                valuationAsk = false;
                break;
            default:
                type = null;
                valuationAsk = false;
        }

        numberType =  numberChoice.getSelectionModel().getSelectedItem();
        countChars = txtCountChars.getText().equals("") ? 0 : Integer.parseInt(txtCountChars.getText());

        required = chckbxRequired.isSelected();
        multipleChoice = chckbxMultipleChoice.isSelected();
        list = chckbxList.isSelected();
        textarea = chckbxTextArea.isSelected();
        yesNoQuestion = chckbxYesNoQuestion.isSelected();
        singleLine = chckbxSingleLine.isSelected();
        numeric = chckbxNumeric.isSelected();
        headline = chckbxHeadline.isSelected();

    }

    public boolean isTypeActivatable() {
        return !headline;
    }
    
    public boolean isRequiredActivatable() {
        return !headline;
    }
    
    public boolean isListActivatable() {
        return !headline && type.equals(QuestionType.MULTIPLE_CHOICE);
    }
    public boolean isMultipleChoiceActivatable() {
        return !headline && !yesNoQuestion && !valuationAsk && !textarea && !singleLine && !numeric  && type.equals(QuestionType.MULTIPLE_CHOICE);
    }
    
    public boolean isTextareaActivatable() {
        return !headline && !multipleChoice && !yesNoQuestion && !singleLine && !list && !numeric && type.equals(QuestionType.SHORT_ANSWER);
    }

    public boolean isYesNoQuestionActivatable() {
        return !headline && !multipleChoice && !valuationAsk && !list && !numeric && !textarea && type.equals(QuestionType.MULTIPLE_CHOICE);
    }

    public boolean isSingleLineActivatable() {
        return !headline && yesNoQuestion && isYesNoQuestionActivatable();
    }

    public boolean isNumericActivatable() {
        return !headline && !textarea && !multipleChoice && !list && !yesNoQuestion && !singleLine && type.equals(QuestionType.SHORT_ANSWER);
    }

    public boolean isNumberTypeActivatable() {
        return isNumericActivatable();
    }

    public boolean isCountCharsActivatable() {
        return isNumericActivatable();
    }

    public boolean isAnswersListActivatable() {
        return !headline && !valuationAsk && type.equals(QuestionType.MULTIPLE_CHOICE);
    }

    public QuestionType getType() {
        return type;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isList() {
        return list;
    }

    public boolean isMultipleChoice() {
        return multipleChoice;
    }

    public boolean isTextarea() {
        return textarea;
    }

    public boolean isYesNoQuestion() {
        return yesNoQuestion;
    }

    public boolean isSingleLine() {
        return singleLine;
    }

    public boolean isNumeric() {
        return numeric;
    }

    public String getNumberType() {
        return numberType;
    }

    public int getCountChars() {
        return countChars;
    }

    public boolean isHeadline() {
        return headline;
    }

    public boolean isValuationAsk() {
        return valuationAsk;
    }
}
