package export;

import java.util.Vector;

/**
 * @author Eric
 * @version 1.0.0
 */
public class ExcelCell {
	private int idBefragung;
	private Vector<String> antworten;

	public ExcelCell() {
	}

	public ExcelCell(int idBefragung, Vector<String> antworten) {
		this.idBefragung = idBefragung;
		this.antworten = antworten;
	}

	public int getIdBefragung() {
		return this.idBefragung;
	}

	public void setIdBefragung(int idBefragung) {
		this.idBefragung = idBefragung;
	}

	public Vector<String> getAntworten() {
		return this.antworten;
	}

	public void setAntworten(Vector<String> antworten) {
		this.antworten = antworten;
	}
}
