package export;

import model.Questionnaire;

import java.io.File;

public interface ExportController {

    void createExcelFile(Questionnaire questionnaire, String fromDate, String toDate);

    void createExcelFile(File file, Questionnaire questionnaire, String fromDate, String toDate);

    boolean createExcelFileOld(Questionnaire questionnaire, String fromDate, String toDate);
}
