package model;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class QuestionEditParam {
    private final QuestionType questionType;
    private final boolean list;
    private final boolean multipleChoice;
    private final boolean textArea;
    private final boolean yesNoQuestion;
    private final boolean singleLine;
    private final boolean numeric;
    private final boolean evaluationQuestion;
    //private final String numberType;
    //private final int countChars;

    private boolean required;

    public QuestionEditParam(QuestionType questionType, boolean required, boolean list, boolean multipleChoice, boolean textArea,
                             boolean yesNoQuestion, boolean singleLine, boolean numeric, boolean evaluationQuestion,
                             String numberType, int countChars) {
        this.questionType = questionType;
        this.required = required;
        this.list = list;
        this.multipleChoice = multipleChoice;
        this.textArea = textArea;
        this.yesNoQuestion = yesNoQuestion;
        this.singleLine = singleLine;
        this.numeric = numeric;
        this.evaluationQuestion = evaluationQuestion;
        //this.numberType = numberType;
        //this.countChars = countChars;
    }

    public QuestionEditParam(ChoiceBox<String> questionTypeChoiceBox, ChoiceBox<String> numberChoice, TextField txtCountChars,
                             CheckBox chckbxRequired, CheckBox evaluationQuestionCheckBox, CheckBox chckbxMultipleChoice, CheckBox chckbxList, CheckBox chckbxTextArea,
                             CheckBox chckbxYesNoQuestion, CheckBox chckbxSingleLine, CheckBox chckbxNumeric) {
        switch (questionTypeChoiceBox.getSelectionModel().getSelectedItem()) {
            case "Multiple Choice":
                questionType = QuestionType.MULTIPLE_CHOICE;
                break;
            case "Freie Frage":
                questionType = QuestionType.SHORT_ANSWER;
                break;
            default:
                questionType = null;
        }

        //numberType = numberChoice.getSelectionModel().getSelectedItem();
        //countChars = txtCountChars.getText().equals("") ? 0 : Integer.parseInt(txtCountChars.getText());
        evaluationQuestion = evaluationQuestionCheckBox.isSelected();
        required = chckbxRequired.isSelected();
        multipleChoice = chckbxMultipleChoice.isSelected();
        list = chckbxList.isSelected();
        textArea = chckbxTextArea.isSelected();
        yesNoQuestion = chckbxYesNoQuestion.isSelected();
        singleLine = chckbxSingleLine.isSelected();
        numeric = false;

    }

    public boolean isQuestionTypeActivatable() {
        return true;
    }

    public boolean isRequiredActivatable() {
        return true;
    }

    public boolean isListActivatable() {
        return questionType.equals(QuestionType.MULTIPLE_CHOICE);
    }

    public boolean isMultipleChoiceActivatable() {
        return !yesNoQuestion && !evaluationQuestion && !textArea && !singleLine && !numeric && questionType.equals(QuestionType.MULTIPLE_CHOICE);
    }

    public boolean isTextareaActivatable() {
        return !multipleChoice && !yesNoQuestion && !singleLine && !list && !numeric && questionType.equals(QuestionType.SHORT_ANSWER);
    }

    public boolean isYesNoQuestionActivatable() {
        return !multipleChoice && !evaluationQuestion && !list && !numeric && !textArea && questionType.equals(QuestionType.MULTIPLE_CHOICE);
    }

    public boolean isSingleLineActivatable() {
        return yesNoQuestion && isYesNoQuestionActivatable();
    }

    public boolean isNumericActivatable() {
        return !textArea && !multipleChoice && !list && !yesNoQuestion && !singleLine && questionType.equals(QuestionType.SHORT_ANSWER);
    }

    public boolean isNumberTypeActivatable() {
        return isNumericActivatable();
    }

    public boolean isCountCharsActivatable() {
        return isNumericActivatable();
    }

    public boolean isAnswersListActivatable() {
        return !evaluationQuestion && questionType.equals(QuestionType.MULTIPLE_CHOICE);
    }

    public QuestionType getQuestionType() {
        return questionType;
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

    public boolean isTextArea() {
        return textArea;
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

    /*public String getNumberType() {
        return numberType;
    }

    public int getCountChars() {
        return countChars;
    }*/

    public boolean isEvaluationQuestion() {
        return evaluationQuestion;
    }
}
