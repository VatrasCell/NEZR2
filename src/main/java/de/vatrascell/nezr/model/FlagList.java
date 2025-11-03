package de.vatrascell.nezr.model;

import de.vatrascell.nezr.flag.React;
import de.vatrascell.nezr.flag.Symbol;
import de.vatrascell.nezr.flag.SymbolType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
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

    public void setFlagListByParam(QuestionEditParam param) {
        this.isList = param.isList();
        this.isMultipleChoice = param.isMultipleChoice();
        this.isTextArea = param.isTextArea();
        this.isYesNoQuestion = param.isYesNoQuestion();
        this.isSingleLine = param.isSingleLine();
        this.isRequired = param.isRequired();
        this.isEvaluationQuestion = param.isEvaluationQuestion();
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
