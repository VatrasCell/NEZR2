package flag;

import model.QuestionType;
import validation.Validation;

import java.util.ArrayList;
import java.util.List;

public class FlagList {

    private int id;
    private boolean isList;
    private boolean isMultipleChoice;
    private boolean isTextArea;
    private boolean isYesNoQuestion;
    private boolean isSingleLine;
    private boolean isRequired;
    private boolean isEvaluationQuestion;
    private Validation validation;
    private List<React> reacts = new ArrayList<>();

    private List<Symbol> possibleFlags;

    public FlagList() {
        super();
        //possibleFlags = getPossibleFlags();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isList() {
        return isList;
    }

    public void setList(boolean list) {
        isList = list;
    }

    public boolean isMultipleChoice() {
        return isMultipleChoice;
    }

    public void setMultipleChoice(boolean multipleChoice) {
        isMultipleChoice = multipleChoice;
    }

    public boolean isTextArea() {
        return isTextArea;
    }

    public void setTextArea(boolean textArea) {
        isTextArea = textArea;
    }

    public boolean isYesNoQuestion() {
        return isYesNoQuestion;
    }

    public void setYesNoQuestion(boolean yesNoQuestion) {
        isYesNoQuestion = yesNoQuestion;
    }

    public boolean isSingleLine() {
        return isSingleLine;
    }

    public void setSingleLine(boolean singleLine) {
        isSingleLine = singleLine;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    public boolean isEvaluationQuestion() {
        return isEvaluationQuestion;
    }

    public void setEvaluationQuestion(boolean evaluationQuestion) {
        isEvaluationQuestion = evaluationQuestion;
    }

    public Validation getValidation() {
        return validation;
    }

    public void setValidation(Validation validation) {
        this.validation = validation;
    }

    public List<React> getReacts() {
        return reacts;
    }

    public void setReacts(List<React> reacts) {
        this.reacts = reacts;
    }

    public void addReact(React react) {
        this.reacts.add(react);
    }

    //TODO refactor
    @Deprecated
    public List<Symbol> getPossibleFlags() {
        List<Symbol> results = new ArrayList<>();
        for (SymbolType symbolType : SymbolType.values()) {
            //if(!this.has(symbolType))
            //	results.add(new Symbol(symbolType));
        }
        return results;
    }

    public boolean hasMultipleChoiceReact() {
        for (React react : reacts) {
            if (react.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
                return true;
            }
        }
        return false;
    }

    public boolean hasShortAnswerReact() {
        for (React react : reacts) {
            if (react.getQuestionType() == QuestionType.SHORT_ANSWER) {
                return true;
            }
        }
        return false;
    }

    @Deprecated
    public String createFlagString() {
        return "";
    }
}
