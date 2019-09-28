package application;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

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
	
	/**
	 * Gibt die Bildschirmhöhe zur�ck.
	 * <p>
	 * @return die Höhe des Bildschirmes als int.
	 */
	public static int getScreenHeight() {
		Dimension d = getScreenSize();
		double heightDouble = d.getHeight();
		return (int) heightDouble;
	}

	/**
	 * Gibt die Bildschirmbreite zurück.
	 * <p>
	 * @return die Breite des Bildschirmes als int.
	 */
	public static int getScreenWidth() {
		Dimension d = getScreenSize();
		double widthDouble = d.getWidth();
		return (int) widthDouble;
	}

	public static URL getURL(String url) {
		return Objects.requireNonNull(GlobalFuncs.class.getClassLoader().getResource(url));
	}

	private static Dimension getScreenSize() {
		return Toolkit.getDefaultToolkit().getScreenSize();
		// return new Dimension((int)main.getWidth(), (int)main.getHeight());
	}
}
