package flag;

import java.util.Arrays;
import java.util.Optional;

public enum QuestionType {
	MC("MC"),
	FF("FF");
	
private final String type;
	
QuestionType(String type) {
		this.type = type;
	}
	
	public static Optional<QuestionType> valueOfString(String value) {
        return Arrays.stream(values())
            .filter(legNo -> legNo.toString().equals(value))
            .findFirst();
    }
	
	public String toString() {
		return type;
	}
}
