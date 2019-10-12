package export;

import java.util.ArrayList;

/**
 * @author Eric
 * @version 1.0.0
 */
public class ExcelCell {
	private int idBefragung;
	private ArrayList<String> antworten;

	public ExcelCell() {
	}

	public ExcelCell(int idBefragung, ArrayList<String> antworten) {
		this.idBefragung = idBefragung;
		this.antworten = antworten;
	}

	public int getIdBefragung() {
		return this.idBefragung;
	}

	public void setIdBefragung(int idBefragung) {
		this.idBefragung = idBefragung;
	}

	public ArrayList<String> getAntworten() {
		return this.antworten;
	}

	public void setAntworten(ArrayList<String> antworten) {
		this.antworten = antworten;
	}
}
