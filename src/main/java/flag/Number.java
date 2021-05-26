package flag;

public class Number {
    private final String PREFIX = "INT";
    private NumberOperator operator;
    private int digits;

    /**
     * @param operator
     * @param digits
     */
    public Number(NumberOperator operator, int digits) {
        super();
        this.operator = operator;
        this.digits = digits;
    }

    /**
     * @return the operator
     */
    public NumberOperator getOperator() {
        return operator;
    }

    /**
     * @param operator the operator to set
     */
    public void setOperator(NumberOperator operator) {
        this.operator = operator;
    }

    /**
     * @return the digits
     */
    public int getDigits() {
        return digits;
    }

    /**
     * @param digits the digits to set
     */
    public void setDigits(int digits) {
        this.digits = digits;
    }

    /**
     * @return the pREFIX
     */
    public String getPREFIX() {
        return PREFIX;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return PREFIX + operator.toString() + digits;
    }


}
