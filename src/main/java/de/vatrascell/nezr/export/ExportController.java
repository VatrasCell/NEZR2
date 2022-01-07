package de.vatrascell.nezr.export;

import de.vatrascell.nezr.application.controller.NotificationController;
import de.vatrascell.nezr.export.model.ExcelQuestionModel;
import de.vatrascell.nezr.export.model.converter.ExcelQuestionModelConverter;
import de.vatrascell.nezr.message.MessageId;
import de.vatrascell.nezr.model.Question;
import de.vatrascell.nezr.model.QuestionType;
import de.vatrascell.nezr.model.Questionnaire;
import de.vatrascell.nezr.model.Survey;
import de.vatrascell.nezr.questionList.QuestionListService;
import de.vatrascell.nezr.survey.SurveyService;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ExportController {

    private final Workbook wb = new XSSFWorkbook();
    private final CreationHelper crHelper = this.wb.getCreationHelper();
    private Sheet sheet;

    private final QuestionListService questionListService;
    private final SurveyService surveyService;

    @Autowired
    @Lazy
    public ExportController(QuestionListService questionListService, SurveyService surveyService) {
        this.questionListService = questionListService;
        this.surveyService = surveyService;
    }

    public void createExcelFile(Questionnaire questionnaire, String fromDate, String toDate) {
        createExcelFile(createFileName(questionnaire),
                questionnaire, fromDate, toDate);
    }

    public void createExcelFile(File file, Questionnaire questionnaire, String fromDate, String toDate) {
        sheet = this.wb.createSheet(WorkbookUtil.createSafeSheetName(String.format("%s-%s", questionnaire.getName(), questionnaire.getDate())));
        List<Question> questions = questionListService.getQuestions(questionnaire.getId());
        List<ExcelQuestionModel> excelQuestionModels = ExcelQuestionModelConverter.convert(questions, 1);

        createInfoRow(questionnaire.getName(), questionnaire.getDate(), fromDate, toDate);
        createCategoryAndQuestionRow(excelQuestionModels);
        createAnswerOptionRow(excelQuestionModels);
        createAnswerRows(excelQuestionModels, questionnaire.getId(), fromDate, toDate);

        saveExcelFile(file);
        NotificationController.createMessage(
                MessageId.TITLE_EXCEL_EXPORT,
                MessageId.MESSAGE_EXPORTED_SUCCESSFULLY);
    }

    private void createAnswerRows(List<ExcelQuestionModel> excelQuestionModels, int questionnaireId, String fromDate, String toDate) {
        final String SELECTED = "1";
        final String NOT_SELECTED = "";
        int rowIndex = 4;
        List<Survey> surveys = surveyService.getSurveys(questionnaireId, fromDate, toDate);
        for (Survey survey : surveys) {
            Row row = this.sheet.createRow(rowIndex);
            row.createCell(0).setCellValue(this.crHelper.createRichTextString(survey.getCreationDate()));
            List<ExcelQuestionModel> excelQuestionModelsInSurvey = getAnswersForSurvey(survey.getSurveyId(), excelQuestionModels);
            for (ExcelQuestionModel model : excelQuestionModelsInSurvey) {
                Question question = model.getQuestion();
                for (int i = 0; i < model.getAnswerOptions().size(); ++i) {
                    if (question.getQuestionType().equals(QuestionType.SHORT_ANSWER)) {
                        String value = question.getSubmittedAnswer().getSubmittedAnswerText() == null ?
                                NOT_SELECTED : question.getSubmittedAnswer().getSubmittedAnswerText();
                        row.createCell(model.getFistCellPosition() + i)
                                .setCellValue(this.crHelper.createRichTextString(value));
                    } else {
                        if (question.getFlags().isSingleLine()) {
                            String value = question.getSubmittedAnswer().getSubmittedAnswerOptions().isEmpty() ?
                                    NOT_SELECTED : question.getSubmittedAnswer().getSubmittedAnswerOptions().get(0).getValue();
                            row.createCell(model.getFistCellPosition() + i)
                                    .setCellValue(this.crHelper.createRichTextString(value));
                        } else {
                            String value = question.getSubmittedAnswer().getSubmittedAnswerOptions().contains(question.getAnswerOptions().get(i)) ?
                                    SELECTED : NOT_SELECTED;
                            row.createCell(model.getFistCellPosition() + i)
                                    .setCellValue(value);
                        }
                    }
                }
            }
            rowIndex++;
        }
    }

    private List<ExcelQuestionModel> getAnswersForSurvey(int surveyId, List<ExcelQuestionModel> excelQuestionModels) {
        List<ExcelQuestionModel> excelQuestionModelsInSurvey = new ArrayList<>(excelQuestionModels);

        for (ExcelQuestionModel model : excelQuestionModelsInSurvey) {
            model.setSubmittedAnswer(surveyService.getAnswer(surveyId, model.getQuestion()));
        }

        return excelQuestionModelsInSurvey;
    }

    private void createInfoRow(String questionnaireName, String questionnaireCreationDate, String fromDate, String toDate) {
        final int ROW_INDEX = 0;
        Row row = this.sheet.createRow(ROW_INDEX);
        row.createCell(0).setCellValue(this.crHelper.createRichTextString(
                String.format("\"%s\" erstellt am %s mit Befragungen vom %s bis zum %s", questionnaireName, questionnaireCreationDate, fromDate, toDate))
        );
    }

    private void createCategoryAndQuestionRow(List<ExcelQuestionModel> excelQuestionModels) {
        final int ROW_INDEX = 2;
        Row row = this.sheet.createRow(ROW_INDEX);
        row.setHeightInPoints(6.0F * this.sheet.getDefaultRowHeightInPoints());

        CellStyle cellStyle = this.wb.createCellStyle();
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);

        Font font = this.wb.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 9);
        cellStyle.setFont(font);

        for (ExcelQuestionModel model : excelQuestionModels) {
            if (model.isMergeCell()) {
                CellRangeAddress cellRangeAddress = new CellRangeAddress(ROW_INDEX, ROW_INDEX,
                        model.getFistCellPosition(), model.getLastCellPosition());

                RegionUtil.setBorderTop(BorderStyle.THIN, cellRangeAddress, this.sheet);
                RegionUtil.setBorderLeft(BorderStyle.THIN, cellRangeAddress, this.sheet);
                RegionUtil.setBorderRight(BorderStyle.THIN, cellRangeAddress, this.sheet);
                RegionUtil.setBorderBottom(BorderStyle.THIN, cellRangeAddress, this.sheet);

                this.sheet.addMergedRegion(cellRangeAddress);
            }

            String value = String.format("%s\n%s", model.getCategory(), model.getQuestionValue());
            row.createCell(model.getFistCellPosition()).setCellValue(value);
            row.getCell(model.getFistCellPosition()).setCellStyle(cellStyle);
        }
    }

    private void createAnswerOptionRow(List<ExcelQuestionModel> excelQuestionModels) {
        final int ROW_INDEX = 3;
        Row row = this.sheet.createRow(ROW_INDEX);

        createSurveyCreationDateColumn(row);

        for (ExcelQuestionModel model : excelQuestionModels) {
            for (int i = 0; i < model.getAnswerOptions().size(); ++i) {
                row.createCell(model.getFistCellPosition() + i).setCellValue(model.getAnswerOptions().get(i));
                row.getCell(model.getFistCellPosition() + i).setCellStyle(getAnswerOptionCellStyle());
            }
        }
    }

    private void createSurveyCreationDateColumn(Row row) {
        row.createCell(0).setCellValue("Datum");
        row.getCell(0).setCellStyle(getAnswerOptionCellStyle());
    }

    private CellStyle getAnswerOptionCellStyle() {
        CellStyle cellStyle = this.wb.createCellStyle();
        cellStyle.setRotation((short) 90);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);

        Font font;
        font = this.wb.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 9);

        cellStyle.setFont(font);

        return cellStyle;
    }

    private File createFileName(Questionnaire questionnaire) {
        File file = new File(String.format("%s\\%s_%s_%s.xlsx", "exportExcel", questionnaire.getId(), questionnaire.getLocation(), questionnaire.getName()));
        int prefix = 1;
        while (file.exists()) {
            file = new File(String.format("%s\\%s_%s_%s_%s.xlsx", "exportExcel", questionnaire.getId(), questionnaire.getLocation(), questionnaire.getName(), ++prefix));
        }

        return file;
    }

    private void saveExcelFile(File file) {
        File theDir = new File("exportExcel");

        if (!theDir.exists()) {
            try {
                theDir.mkdir();
            } catch (SecurityException se) {
                //
            }
        }

        try {
            FileOutputStream fOut = new FileOutputStream(file.getPath());

            this.wb.write(fOut);
            fOut.close();
            this.wb.close();
        } catch (IOException e) {
            //ErrorLog.fehlerBerichtB("ERROR",getClass() + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
            e.printStackTrace();
        }
    }
}
