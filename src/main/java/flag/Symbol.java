package flag;

public class Symbol {

    private SymbolType symbolType;

    /**
     * @param symbolType
     */
    public Symbol(SymbolType symbolType) {
        super();
        this.symbolType = symbolType;
    }

    /**
     * @return the type
     */
    public SymbolType getType() {
        return symbolType;
    }

    /**
     * @return bool
     */
    public boolean isType(SymbolType symbolType) {
        return this.symbolType.toString().equals(symbolType.toString());
    }

    /**
     * @param symbolType the type to set
     */
    public void setType(SymbolType symbolType) {
        this.symbolType = symbolType;
    }

    @Override
    public String toString() {
        return symbolType.toString();
    }
}
