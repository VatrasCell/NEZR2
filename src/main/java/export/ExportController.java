package export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

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

import flag.SymbolType;
import model.Frage;
import model.Fragebogen;
import survey.SurveyService;

public class ExportController {
	
	private Workbook wb = new XSSFWorkbook();
	private Sheet sheet = this.wb.createSheet(WorkbookUtil.createSafeSheetName("NEZR"));
	private CreationHelper crHelper = this.wb.getCreationHelper();
	private Vector<Row> rows = new Vector<Row>();
	
	/**
	  * Erstellt Excel Datei nach neuer Vorlage.
	  * @param Path String: Dateipfad
	  * @param fb FragebogenDialog: der Fragebogen
	  * @param von String: Datum
	  * @param bis String: Datum
	  * @return boolean
	 */
	public boolean excelNeu(String Path, Fragebogen fb, String von, String bis) {
		 Vector<Frage> fragen = SurveyService.getFragen(fb);
		 
		 Row infoRow = this.sheet.createRow(0);
		 Row katRow = this.sheet.createRow(2);
		 Row faRow = this.sheet.createRow(3);

		 infoRow.createCell(0).setCellValue(this.crHelper.createRichTextString("\"" + fb.getName() + "\" erstellt am " + fb.getDate() + " mit Befragungen vom " + von + " bis zum " + bis));
		 for (int i = 0; i < ExportService.getAnzahlBefragung(); i++) {
			 this.rows.addElement(this.sheet.createRow(4 + i));
		 }

		 katRow.setHeightInPoints(6.0F * this.sheet.getDefaultRowHeightInPoints());

		 Vector<String> kats = new Vector<String>();
		 Vector<Integer> pos = new Vector<Integer>();
		 Vector<CellRangeAddress> katsCells = new Vector<CellRangeAddress>();
		 String katOld = "";
		 int anzahl = 0;

		 int i = 0;
		Font font;
		for (int over = 0; i < fragen.size(); over++) {
			 Frage frage = (Frage) fragen.get(i);
			 if (frage.getFrage().equals(katOld)) {
				 anzahl++;
			} else {
				 kats.add(frage.getKategorie() + "\n" + frage.getFrage());
				 if ((anzahl != 0) && (anzahl > 1)) {
					 CellRangeAddress cellRangeAddress = new CellRangeAddress( 2,  2,
							 over - anzahl,  over - 1);

					 katsCells.addElement(cellRangeAddress);
					 this.sheet.addMergedRegion(cellRangeAddress);
					 pos.addElement(Integer.valueOf(over - anzahl));
				}
				 else if (anzahl != 0) {
					 pos.addElement(Integer.valueOf(over - 1));
				}

				 katOld = frage.getFrage();
				 anzahl = 1;
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

			if (frage.getArt().equals("FF")) {
				faRow.createCell(over).setCellValue(this.crHelper.createRichTextString(frage.getFrage()));
				faRow.getCell(over).setCellStyle(formatTableHead);
				makeAntwortenToExecel(frage, "", over, von, bis);
			} else if (/*(frage.getFlags().indexOf("B") >= 0) || */(frage.getFlags().is(SymbolType.JNExcel)) && (frage.getFlags().is(SymbolType.JN))
					|| (frage.getFlags().is(SymbolType.LIST))) {
				/*if (frage.getKategorie().equals(frage.getFrage())false) {
					for (int j = 0; j < frage.getAntwort_moeglichkeit().size(); j++) {
						faRow.createCell(over + j).setCellValue(
								this.crHelper.createRichTextString((String) frage.getAntwort_moeglichkeit().get(j)));
						faRow.getCell(over + j).setCellStyle(formatTableHead);
						if (frage.getFlags().indexOf("LIST") >= 0) {
							makeAntwortenToExecel(frage, "", over + j);
						} else {
							makeAntwortenToExecel(frage, (String) frage.getAntwort_moeglichkeit().get(j), over + j);
						}
					}

					over += frage.getAntwort_moeglichkeit().size() - 1;
					anzahl += frage.getAntwort_moeglichkeit().size() - 1;
				} else*/ if ((frage.getFlags().is(SymbolType.JNExcel)) && (frage.getFlags().is(SymbolType.JN))) {
					faRow.createCell(over).setCellValue(this.crHelper.createRichTextString(frage.getFrage()));
					faRow.getCell(over).setCellStyle(formatTableHead);
					makeAntwortenToExecel(frage, "ja", over, von, bis);
				} else {
					faRow.createCell(over).setCellValue(this.crHelper.createRichTextString(frage.getFrage()));
					faRow.getCell(over).setCellStyle(formatTableHead);
					makeAntwortenToExecel(frage, "", over, von, bis);
				}
			} else/* if (/*frage.getKategorie().equals(frage.getFrage()) false) {
				faRow.createCell(over).setCellValue(this.crHelper.createRichTextString(frage.getFrage()));
				faRow.getCell(over).setCellStyle(formatTableHead);
				makeAntwortenToExecel(frage, "", over);
			} else*/ {
				for (int j = 0; j < frage.getAntwort_moeglichkeit().size(); j++) {
					faRow.createCell(over + j).setCellValue(
							this.crHelper.createRichTextString((String) frage.getAntwort_moeglichkeit().get(j)));
					faRow.getCell(over + j).setCellStyle(formatTableHead);
					makeAntwortenToExecel(frage, (String) frage.getAntwort_moeglichkeit().get(j), over + j, von, bis);
				}

				over += frage.getAntwort_moeglichkeit().size() - 1;
				anzahl += frage.getAntwort_moeglichkeit().size() - 1;
			}

			if (i == fragen.size() - 1) {
				pos.addElement(Integer.valueOf(over));
			}
			 i++;
		}

		for (int j = 0; j < kats.size(); j++) {
			String kat = (String) kats.get(j);

			Vector<String> word = new Vector<String>();

			while ((kat.length() >= 12) && (kat.indexOf(" ", 10) != -1)) {
				word.addElement(kat.substring(0, kat.indexOf(" ", 10)));
				if (kat.indexOf(" ", 10) != -1) {
					kat = kat.substring(kat.indexOf(" ", 10));
				} else {
					word.addElement(kat);
				}
			}

			word.addElement(kat);

			kat = "";

			for (String string : word) {
				kat = kat + string.trim() + "\n";
			}

			kat = kat.substring(0, kat.length() - 1);

			katRow.createCell(((Integer) pos.get(j)).intValue()).setCellValue(kat);
		}

		for (int z = 0; z < pos.size(); z++) {
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

			for (int y = 0; y < katsCells.size(); y++) {
				if (((CellRangeAddress) katsCells.get(y)).getFirstColumn() == ((Integer) pos.get(z)).intValue()) {
					RegionUtil.setBorderTop(1, (CellRangeAddress) katsCells.get(y), this.sheet);
					RegionUtil.setBorderLeft(1, (CellRangeAddress) katsCells.get(y), this.sheet);
					RegionUtil.setBorderRight(1, (CellRangeAddress) katsCells.get(y), this.sheet);
					RegionUtil.setBorderBottom(1, (CellRangeAddress) katsCells.get(y), this.sheet);
				}
			}

			katRow.getCell(((Integer) pos.get(z)).intValue()).setCellStyle(formatTableHeadKat);
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
	 * @param frage FrageErstellen: Fragenobjekt
	 * @param antwort String
	 * @param pos int: Position in Tabelle
	 */
	private void makeAntwortenToExecel(Frage frage, String antwort, int pos, String von, String bis) {
		Vector<ExcelCell> antPos = new Vector<ExcelCell>();
		if (antwort.equals("")) {
			antPos = ExportService.getAntwortenPosition(frage, von, bis);
		} else {
			antPos = ExportService.getAntwortenPosition(frage, antwort, von, bis);
		}

		if (antPos.size() > 0) {
			for (int z = 0; z < antPos.size(); z++) {
				CellStyle formatTableCell = this.wb.createCellStyle();
				formatTableCell.setAlignment(HorizontalAlignment.CENTER);
				formatTableCell.setVerticalAlignment(VerticalAlignment.CENTER);
				formatTableCell.setWrapText(true);

				Font font = this.wb.createFont();
				font.setFontName("Arial");
				font.setFontHeightInPoints((short) 9);
				formatTableCell.setFont(font);

				String string = ((ExcelCell) antPos.get(z)).getAntworten().toString();
				string = string.replaceAll("[\\[]", "");
				string = string.replaceAll("[]]", "");
				string = string.replaceAll("[,]", "\n");
				Cell cell = ((Row) this.rows.get(((antPos.get(z)).getIdBefragung()) - 1)).createCell(pos);
				for (String aString : ((ExcelCell) antPos.get(z)).getAntworten()) {
					try {
						Integer.parseInt(aString);
					} catch (NumberFormatException e) {
					}
				}

				cell.setCellValue(string);

				cell.setCellStyle(formatTableCell);
			}
		}
	}
}
