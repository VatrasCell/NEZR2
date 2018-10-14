package flag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum SymbolType {
	TEXT("TEXT"),
	JNExcel("X"),
	JN("JN"),
	REQUIRED("+"),
	LIST("LIST"),
	MC("*"),
	B("B");
	
	private final String flag;
	
	private SymbolType(String flag) {
		this.flag = flag;
	}
	
	public static List<String> stringValues() {
		List<String> results = new ArrayList<>();
		for (SymbolType symbolType : SymbolType.values()) {
			results.add(symbolType.toString());
		}
		return results;
	}
	
	public static Optional<SymbolType> valueOfString(String value) {
        return Arrays.stream(values())
            .filter(legNo -> legNo.toString().equals(value))
            .findFirst();
    }
	
	public String toString() {
		return flag;
	}
}
