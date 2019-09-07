package application;

import java.awt.Dimension;
import java.awt.Toolkit;
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
	
	public static Dimension getScreenSize() {
		return Toolkit.getDefaultToolkit().getScreenSize();
		// return new Dimension((int)main.getWidth(), (int)main.getHeight());
	}
	
	/**
	 * Gibt die Bildschirmhöhe zur�ck.
	 * <p>
	 * @param d Dimension: die Bildschirmgröße von Toolkit.getDefaultToolkit().getScreenSize().
	 * @return die Höhe des Bildschirmes als int.
	 */
	public static int getScreenHeight() {
		Dimension d = getScreenSize();
		Double heightDouble = d.getHeight();
		Integer heightInt = Integer.valueOf(heightDouble.intValue());
		return heightInt;
	}

	/**
	 * Gibt die Bildschirmbreite zurück.
	 * <p>
	 * @param d die Bildschirmgröße von Toolkit.getDefaultToolkit().getScreenSize().
	 * @return die Breite des Bildschirmes als int.
	 */
	public static int getScreenWidth() {
		Dimension d = getScreenSize();
		Double widthDouble = d.getWidth();
		Integer widthInt = Integer.valueOf(widthDouble.intValue());
		return widthInt;
	}
}
