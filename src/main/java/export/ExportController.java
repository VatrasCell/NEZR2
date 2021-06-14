package export;

import model.Questionnaire;

public interface ExportController {

	boolean createExcelFile(Questionnaire questionnaire, String fromDate, String toDate);
}
