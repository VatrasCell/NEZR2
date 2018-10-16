package flag;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlagList {
	
	private List<Flag> list = new ArrayList<>();
	private List<Symbol> possibleFlags = new ArrayList<>();

	public FlagList() {
		super();
		possibleFlags = getPossibleFlags();
	}
	
	public FlagList(String flags) {
		super();
		possibleFlags = getPossibleFlags();
		convertStringToFlagList(flags);
	}
	
	private List<Symbol> getPossibleFlags() {
		List<Symbol> results = new ArrayList<>();
		for (SymbolType symbolType : SymbolType.values()) {
			if(!this.is(symbolType))
				results.add(new Symbol(symbolType));
		}
		return results;
	}
	
	public void convertStringToFlagList(String flags) {
		String[] flagArr = flags.split(" ");
		for (String flag : flagArr) {
			Symbol symbol = getSymbolFromFlagString(flag);
			if(symbol != null) {
				this.add(symbol);
				continue;
			}		
			
			Number number = getNumberFromFlagString(flag);
			if(number != null) {
				this.add(number);
				continue;
			}
						
			React react = getReactMCFromFlagList(flag);
			if(react != null) {
				this.add(react);
				continue;
			}
						
			react = getReactMCFromFlagList(flag);
			if(react != null) {
				this.add(react);
				continue;
			}
		}
	}
	
	public boolean is(SymbolType expectation) {
		return getSymbol(expectation) != null ? true : false;
	}
	
	public Symbol getSymbol(SymbolType expectation) {
		for (Flag flag : list) {
			if(flag.getClass() == Symbol.class && flag.toString().equals(expectation.toString())) {
				return (Symbol)flag;
			}
		}
		return null;
	}
	
	public Symbol getSymbolFromFlagString(String flag) {
		if(SymbolType.stringValues().contains(flag)) {
			return new Symbol(SymbolType.valueOfString(flag).orElse(null));
		}
		return null;
	}
	
	public Number getNumberFromFlagString(String flag) {	
		Pattern MY_PATTERNint = Pattern.compile("INT[<>=]=[0-9]+");
		Matcher mint = MY_PATTERNint.matcher(flag);
		if (mint.find()) {
			return new Number(
					NumberOperator.valueOfString(mint.group(0).substring(3, 5)).orElse(null),
					Integer.parseInt(mint.group(0).substring(5))
				);
		}
		return null;
	}
	
	public React getReactMCFromFlagList(String flag) {
		Pattern MY_PATTERN = Pattern.compile("MC[0-9]+A[0-9]+");
		Matcher mges = MY_PATTERN.matcher(flag);
		
		if (mges.find()) {

			Pattern MY_PATTERN1 = Pattern.compile("MC[0-9]+");
			Matcher m1 = MY_PATTERN1.matcher(mges.group(0));
			Pattern MY_PATTERN2 = Pattern.compile("A[0-9]+");
			Matcher m2 = MY_PATTERN2.matcher(mges.group(0));
			if (m1.find() && m2.find()) {
				return new React(
						QuestionType.MC, 
						Integer.parseInt(m1.group(0).substring(2)), 
						Integer.parseInt(m2.group(0).substring(1))
					);
			}
		}
		return null;
	}
	
	public React getReactFFFromFlagList(String flag) {
		Pattern MY_PATTERN = Pattern.compile("FF[0-9]+A[0-9]+");
		Matcher mges = MY_PATTERN.matcher(flag);
		
		if (mges.find()) {

			Pattern MY_PATTERN1 = Pattern.compile("FF[0-9]+");
			Matcher m1 = MY_PATTERN1.matcher(mges.group(0));
			Pattern MY_PATTERN2 = Pattern.compile("A[0-9]+");
			Matcher m2 = MY_PATTERN2.matcher(mges.group(0));
			if (m1.find() && m2.find()) {
				return new React(
						QuestionType.FF, 
						Integer.parseInt(m1.group(0).substring(2)), 
						Integer.parseInt(m2.group(0).substring(1))
					);
			}
		}
		return null;
	}
	
	public boolean hasMCReact() {
		for (Flag flag : list) {
			if(flag.getClass() == React.class)
				if(((React)flag).getQuestionType() == QuestionType.MC)
					return true;
		}
		return false;
	}
	
	public boolean hasFFReact() {
		for (Flag flag : list) {
			if(flag.getClass() == React.class)
				if(((React)flag).getQuestionType() == QuestionType.FF)
					return true;
		}
		return false;
	}
	
	public String createFlagString() {
		List<String> flags = new ArrayList<>();
		for (Flag flag : list) {
			flags.add(flag.toString());
		}
		return String.join(" ", flags);
	}
	
	public void add(Flag element) {
		if(element.getClass() != Symbol.class || isFlagPossible(element)) {
			list.add(element);
		} else {
			System.out.println("Already exists!");
		}	
		possibleFlags = getPossibleFlags();
	}
	
	public void remove(Flag element) {
		list.remove(element);
		possibleFlags = getPossibleFlags();
	}
	
	public void remove(SymbolType type) {
		list.remove(getSymbol(type));
		possibleFlags = getPossibleFlags();
	}
	
	private boolean isFlagPossible(Flag element) {
		for (Flag flag : possibleFlags) {
			if(flag.toString().equals(element.toString()))
				return true;
		}
		return false;
	}
	
	public void update(int i, Flag element) {
		if(i >= 0 && i < list.size())
			list.add(i, element);
	}

	public Flag get(int i) {
		return list.get(i);
	}

	public int size() {
		return list.size();
	}
	
	public List<Flag> getAll() {
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Flag> List<T> getAll(Class<T> c) {
		List<T> results = new ArrayList<>();
		for (Flag flag : list) {
			if(flag.getClass() == c)
				results.add((T)flag);
		}
		return results;
	}
	
}
