package application;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class GlobalFuncs {
	
	/**
	 * Findet das aktuelle Datum heraus im Format "yyyy-MM-dd".
	 * @return datum String
	 */
	public static String getcurDate() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.now();
		return dtf.format(localDate);
	}
}
