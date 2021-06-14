package export.impl;

import export.ExcelCell;
import export.ExportController;
import model.AnswerOption;
import model.Question;
import model.QuestionType;
import model.Questionnaire;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
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
import question.QuestionService;
import questionList.QuestionListService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExportControllerImpl implements ExportController {
	
	private Workbook wb = new XSSFWorkbook();
	private Sheet sheet = this.wb.createSheet(WorkbookUtil.createSafeSheetName("NEZR"));
	private CreationHelper crHelper = this.wb.getCreationHelper();
	private ArrayList<Row> rows = new ArrayList<Row>();
	
	/**
	  * Erstellt Excel Datei nach neuer Vorlage.
	  * @param Path String: Dateipfad
	  * @param questionnaire FragebogenDialog: der Fragebogen
	  * @param von String: Datum
	  * @param bis String: Datum
	  * @return boolean
	 */
	public boolean excelNeu(String Path, Questionnaire questionnaire, String von, String bis) {
		 List<Question> questions = QuestionListService.getQuestions(questionnaire.getId());
		 
		 Row infoRow = this.sheet.createRow(0);
		 Row katRow = this.sheet.createRow(2);
		 Row faRow = this.sheet.createRow(3);

		 infoRow.createCell(0).setCellValue(this.crHelper.createRichTextString("\"" + questionnaire.getName() + "\" erstellt am " + questionnaire.getDate() + " mit Befragungen vom " + von + " bis zum " + bis));
		 for (int i = 0; i < ExportServiceImpl.getSurveyCount(); i++) {
			 this.rows.add(this.sheet.createRow(4 + i));
		 }

		 katRow.setHeightInPoints(6.0F * this.sheet.getDefaultRowHeightInPoints());

		 ArrayList<String> categoryList = new ArrayList<>();
		 ArrayList<Integer> positionList = new ArrayList<>();
		 ArrayList<CellRangeAddress> categoryCellList = new ArrayList<>();
		 String oldCategory = "";
		 int count = 0;

		 int i = 0;
		Font font;
		for (int over = 0; i < Objects.requireNonNull(questions).size(); over++) {
			 Question question = questions.get(i);
			 if (question.getQuestion().equals(oldCategory)) {
				 count++;
			} else {
				 categoryList.add(question.getCategory() + "\n" + question.getQuestion());
				 if ((count > 1)) {
					 CellRangeAddress cellRangeAddress = new CellRangeAddress( 2,  2,
							 over - count,  over - 1);

					 categoryCellList.add(cellRangeAddress);
					 this.sheet.addMergedRegion(cellRangeAddress);
					 positionList.add(over - count);
				}
				 else if (count != 0) {
					 positionList.add(over - 1);
				}

				 oldCategory = question.getQuestion();
				 count = 1;
			}

			 CellStyle formatTableHead = this.wb.createCellStyle();
			 formatTableHead.setRotation((short) 90);
			 formatTableHead.setAlignment(HorizontalAlignment.CENTER);
			 formatTableHead.setVerticalAlignment(VerticalAlignment.CENTER);
			 formatTableHead.setBorderBottom(BorderStyle.THIN);
			 formatTableHead.setBorderLeft(BorderStyle.THIN);
			 formatTableHead.setBorderRight(BorderStyle.THIN);
			 formatTableHead.setBorderTop(BorderStyle.THIN);

			 font = this.wb.createFont();
			 font.setFontName("Arial");
			 font.setFontHeightInPoints((short) 9);
			formatTableHead.setFont(font);

			if (question.getQuestionType().equals(QuestionType.SHORT_ANSWER)) {
				faRow.createCell(over).setCellValue(this.crHelper.createRichTextString(question.getQuestion()));
				faRow.getCell(over).setCellStyle(formatTableHead);
				makeAnswersToExcel(question, null, over, von, bis);
			} else if (/*(question.getFlags().indexOf("B") >= 0) || */(question.getFlags().isSingleLine()) && (question.getFlags().isYesNoQuestion())
					|| (question.getFlags().isList())) {
				/*if (question.getKategorie().equals(question.getFrage())false) {
					for (int j = 0; j < question.getAntwort_moeglichkeit().size(); j++) {
						faRow.createCell(over + j).setCellValue(
								this.crHelper.createRichTextString((String) question.getAntwort_moeglichkeit().get(j)));
						faRow.getCell(over + j).setCellStyle(formatTableHead);
						if (question.getFlags().indexOf("LIST") >= 0) {
							makeAntwortenToExecel(question, "", over + j);
						} else {
							makeAntwortenToExecel(question, (String) question.getAntwort_moeglichkeit().get(j), over + j);
						}
					}

					over += question.getAntwort_moeglichkeit().size() - 1;
					anzahl += question.getAntwort_moeglichkeit().size() - 1;
				} else*/ if ((question.getFlags().isSingleLine()) && (question.getFlags().isYesNoQuestion())) {
					faRow.createCell(over).setCellValue(this.crHelper.createRichTextString(question.getQuestion()));
					faRow.getCell(over).setCellStyle(formatTableHead);
					AnswerOption answerOption = QuestionService.getAnswerOption("ja");
					makeAnswersToExcel(question, answerOption, over, von, bis);
				} else {
					faRow.createCell(over).setCellValue(this.crHelper.createRichTextString(question.getQuestion()));
					faRow.getCell(over).setCellStyle(formatTableHead);
					makeAnswersToExcel(question, null, over, von, bis);
				}
			} else/* if (/*question.getKategorie().equals(question.getFrage()) false) {
				faRow.createCell(over).setCellValue(this.crHelper.createRichTextString(question.getFrage()));
				faRow.getCell(over).setCellStyle(formatTableHead);
				makeAntwortenToExecel(question, "", over);
			} else*/ {
				for (int j = 0; j < question.getAnswerOptions().size(); j++) {
					faRow.createCell(over + j).setCellValue(
							this.crHelper.createRichTextString((String) question.getAnswerOptions().get(j).getValue()));
					faRow.getCell(over + j).setCellStyle(formatTableHead);
					makeAnswersToExcel(question, question.getAnswerOptions().get(j), over + j, von, bis);
				}

				over += question.getAnswerOptions().size() - 1;
				count += question.getAnswerOptions().size() - 1;
			}

			if (i == questions.size() - 1) {
				positionList.add(over);
			}
			 i++;
		}

		for (int j = 0; j < categoryList.size(); j++) {
			String category = categoryList.get(j);

			ArrayList<String> word = new ArrayList<>();

			while ((category.length() >= 12) && (category.indexOf(" ", 10) != -1)) {
				word.add(category.substring(0, category.indexOf(" ", 10)));
				if (category.indexOf(" ", 10) != -1) {
					category = category.substring(category.indexOf(" ", 10));
				} else {
					word.add(category);
				}
			}

			word.add(category);

			category = "";

			for (String string : word) {
				category = category + string.trim() + "\n";
			}

			category = category.substring(0, category.length() - 1);

			katRow.createCell(positionList.get(j)).setCellValue(category);
		}

		for (Integer po : positionList) {
			CellStyle formatTableHeadKat = this.wb.createCellStyle();
			formatTableHeadKat.setWrapText(true);
			formatTableHeadKat.setAlignment(HorizontalAlignment.CENTER);
			formatTableHeadKat.setVerticalAlignment(VerticalAlignment.CENTER);
			formatTableHeadKat.setBorderBottom(BorderStyle.THIN);
			formatTableHeadKat.setBorderLeft(BorderStyle.THIN);
			formatTableHeadKat.setBorderRight(BorderStyle.THIN);
			formatTableHeadKat.setBorderTop(BorderStyle.THIN);

			Font font2 = this.wb.createFont();
			font2.setFontName("Arial");
			font2.setFontHeightInPoints((short) 9);
			formatTableHeadKat.setFont(font2);

			for (CellRangeAddress categoryCell : categoryCellList) {
				if (categoryCell.getFirstColumn() == po) {
					RegionUtil.setBorderTop(BorderStyle.THIN, categoryCell, this.sheet);
					RegionUtil.setBorderLeft(BorderStyle.THIN, categoryCell, this.sheet);
					RegionUtil.setBorderRight(BorderStyle.THIN, categoryCell, this.sheet);
					RegionUtil.setBorderBottom(BorderStyle.THIN, categoryCell, this.sheet);
				}
			}

			katRow.getCell(po).setCellStyle(formatTableHeadKat);
		}

		File theDir = new File("exportExcel");

		if (!theDir.exists()) {
		    try{
		        theDir.mkdir();
		    } 
		    catch(SecurityException se){
		        //
		    	return false;
		    }        
		}
		
		try {
			FileOutputStream fOut = new FileOutputStream("exportExcel/" + Path);
			this.wb.write(fOut);
			fOut.close();
			this.wb.close();
			return true;
		} catch (IOException e) {
			//ErrorLog.fehlerBerichtB("ERROR",getClass() + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Tr�gt die gegebenen Antworten in die Excel Tabelle ein.
	 *
	 * @param question     FrageErstellen: Fragenobjekt
	 * @param answerOption String
	 * @param pos          int: Position in Tabelle
	 */
	private void makeAnswersToExcel(Question question, AnswerOption answerOption, int pos, String von, String bis) {
		ArrayList<ExcelCell> antPos;
		if (answerOption == null) {
			antPos = ExportServiceImpl.getAnswerPositions(question, von, bis);
		} else {
			antPos = ExportServiceImpl.getAnswerPositions(question, answerOption, von, bis);
		}

		if (antPos.size() > 0) {
			for (ExcelCell antPo : antPos) {
				CellStyle formatTableCell = this.wb.createCellStyle();
				formatTableCell.setAlignment(HorizontalAlignment.CENTER);
				formatTableCell.setVerticalAlignment(VerticalAlignment.CENTER);
				formatTableCell.setWrapText(true);

				Font font = this.wb.createFont();
				font.setFontName("Arial");
				font.setFontHeightInPoints((short) 9);
				formatTableCell.setFont(font);

				String string = (antPo).getAnswers().toString();
				string = string.replaceAll("[\\[]", "");
				string = string.replaceAll("[]]", "");
				string = string.replaceAll("[,]", "\n");
				Cell cell = (this.rows.get((antPo.getSurveyId()) - 1)).createCell(pos);
				for (String aString : (antPo).getAnswers()) {
					try {
						Integer.parseInt(aString);
					} catch (NumberFormatException e) {
						e.getStackTrace();
					}
				}

				cell.setCellValue(string);

				cell.setCellStyle(formatTableCell);
			}
		}
	}
}
