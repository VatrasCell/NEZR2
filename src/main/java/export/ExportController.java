package export;

import model.Questionnaire;

public interface ExportController {

	/**
	  * Erstellt Excel Datei nach neuer Vorlage.
	  * @param Path String: Dateipfad
	  * @param fb FragebogenDialog: der Fragebogen
	  * @param von String: Datum
	  * @param bis String: Datum
	  * @return boolean
	 */
	boolean excelNeu(String Path, Questionnaire fb, String von, String bis);
}
