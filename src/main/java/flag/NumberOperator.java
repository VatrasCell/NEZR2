package flag;

import java.util.Arrays;
import java.util.Optional;

public enum NumberOperator {
	EQ("=="),
	GTE(">="),
	LTE("<=");
	
	private final String operator;
	
	NumberOperator(String operator) {
		this.operator = operator;
	}
	
	public static Optional<NumberOperator> valueOfString(String value) {
        return Arrays.stream(values())
            .filter(legNo -> legNo.toString().equals(value))
            .findFirst();
    }
	
	public String toString() {
		return operator;
	}
}
