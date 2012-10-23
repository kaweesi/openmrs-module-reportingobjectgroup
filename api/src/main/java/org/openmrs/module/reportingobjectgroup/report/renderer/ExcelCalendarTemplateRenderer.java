package org.openmrs.module.reportingobjectgroup.report.renderer;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer;
import org.openmrs.module.reporting.report.renderer.RenderingException;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reportingobjectgroup.util.ReportingObjectGroupUtil;

/**
 * This is exactly the same as the ExcelTemplateRenderer, except that it will render a calendar, according to the parameters defined in @RollingDailyIndicatorReportDefinition.
 * To use this functionaliy, read the javadoc in @RollingDailyIndicatorDataSetEvaluator
 * 
 * To select the placement of the calendar in your excel template, you just need to add:  DRAW CALENDAR WIDGET HERE
 * to the cell that you want to be the upper most left cell of your calendar.
 * 
 * Note, this only supports one calendar per report.
 * 
 * @author dthomas
 *
 */
@Handler
public class ExcelCalendarTemplateRenderer extends ExcelTemplateRenderer {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private static final String DRAW_CALENDAR_HERE = "DRAW CALENDAR WIDGET HERE";
	
	private static final SimpleDateFormat sdfIndicatorVar = new SimpleDateFormat("yyyyMMdd");
	
	//TODO: internationalize
	private static final String[] days = {
        "Sunday", "Monday", "Tuesday",
        "Wednesday", "Thursday", "Friday", "Saturday"};

	private static final String[]  months = {
        "January", "February", "March","April", "May", "June","July", "August",
        "September","October", "November", "December"};


	public ExcelCalendarTemplateRenderer() {}
	
	/** 
	 * @see ReportRenderer#render(ReportData, String, OutputStream)
	 */
	@Override
	public void render(ReportData reportData, String argument, OutputStream out) throws IOException, RenderingException {
		
		try {
			log.debug("Attempting to render report with ExcelTemplateRenderer");
			ReportDesign design = getDesign(argument);
			HSSFWorkbook wb = this.getExcelTemplate(design);
			Map<String, String> repeatSections = this.getRepeatingSections(design);

			// Put together base set of replacements.  Any dataSet with only one row is included.
			Map<String, Object> replacements = this.getBaseReplacementData(reportData, design);
			
			// Iterate across all of the sheets in the workbook, and configure all those that need to be added/cloned
			List<SheetToAdd> sheetsToAdd = new ArrayList<SheetToAdd>();

			Set<String> usedSheetNames = new HashSet<String>();
			int numberOfSheets = wb.getNumberOfSheets();
			
			for (int sheetNum=0; sheetNum<numberOfSheets; sheetNum++) {
					
				
				HSSFSheet currentSheet = wb.getSheetAt(sheetNum);
				String originalSheetName = wb.getSheetName(sheetNum);
				
				//draw the calendar
				if (reportData.getContext().getParameterValue("startDate") != null && reportData.getContext().getParameterValue("endDate") != null){
					Date startDate = (Date) reportData.getContext().getParameterValue("startDate");
					Date endDate = (Date) reportData.getContext().getParameterValue("endDate");
					drawCalendar(startDate, endDate, currentSheet, wb);
				} else {
					log.debug("Skipping calendar rendering because startDate and endDate parameters are not defined in this report.");
				}
				
				String dataSetName = getRepeatingSheetProperty(sheetNum, repeatSections);
				if (dataSetName != null) {
					
					DataSet repeatingSheetDataSet = getDataSet(reportData, dataSetName, replacements);
					int dataSetRowNum = 0;
					for (Iterator<DataSetRow> rowIterator = repeatingSheetDataSet.iterator(); rowIterator.hasNext();) {
						DataSetRow dataSetRow = rowIterator.next();
						dataSetRowNum++;
						Map<String, Object> newReplacements = getReplacementData(replacements, reportData, design, dataSetName, dataSetRow, dataSetRowNum);
						HSSFSheet newSheet = (dataSetRowNum == 1 ? currentSheet : wb.cloneSheet(sheetNum));
						sheetsToAdd.add(new SheetToAdd(newSheet, sheetNum, originalSheetName, newReplacements));
					}
				}
				else {
					sheetsToAdd.add(new SheetToAdd(currentSheet, sheetNum, originalSheetName, replacements));
				}
			}
			
			// Then iterate across all of these and add them in
			for (int i=0; i<sheetsToAdd.size(); i++) {
				addSheet(wb, sheetsToAdd.get(i), usedSheetNames, reportData, design, repeatSections);
			}

			wb.write(out);
		}
		catch (Exception e) {
			throw new RenderingException("Unable to render results due to: " + e, e);
		}
	}
	
	
	private static void drawCalendar(Date startDate, Date endDate, HSSFSheet sheet, HSSFWorkbook wb){
		{
			
			Calendar cal = new GregorianCalendar();
			cal.setTime(startDate);
			
			if (startDate.getTime() >= endDate.getTime())
				throw new IllegalArgumentException("start date must be before end date!");
			
			HSSFCell calStartCell = getCellWithCalendarInsert(sheet);
			//these are 0-based indexes
			if (calStartCell == null)
					return;
			int colWeekStartNum = calStartCell.getColumnIndex() + 1;
			int dateCol = calStartCell.getColumnIndex();
			
			//clear the calendar insert cell
			calStartCell.setCellValue(new HSSFRichTextString(""));
			
			HSSFRow insertRow = sheet.getRow(calStartCell.getRowIndex());			
			//add buffer rows above and below where the calendar goes?
			//sheet.createRow(colRowStartNum+1);
			//sheet.createRow(colRowStartNum);

			//headers
			HSSFCellStyle headerStyle = wb.createCellStyle();
            headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            HSSFFont fontBold = wb.createFont();
            fontBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            headerStyle.setFont(fontBold);
            int colWeekStartNumIter = colWeekStartNum;
            for (int i = 0; i < days.length; i++) {
                //set column widths, the width is measured in units of 1/256th of a character width
                //sheet.setColumnWidth(i*2, 5*256); //the column is 5 characters wide
                //sheet.setColumnWidth(i*2 + 1, 13*256); //the column is 13 characters wide
                HSSFCell monthCell = insertRow.createCell(colWeekStartNumIter);
                monthCell.setCellType(HSSFCell.CELL_TYPE_STRING);
                monthCell.setCellStyle(headerStyle);
                HSSFRichTextString str = new HSSFRichTextString(days[i]);
                str.applyFont(fontBold);
                monthCell.setCellValue(str);
                sheet.addMergedRegion(new CellRangeAddress(insertRow.getRowNum(), insertRow.getRowNum(), colWeekStartNumIter, colWeekStartNumIter + 2));
                colWeekStartNumIter = colWeekStartNumIter + 3;
            }
            
            

            HSSFCellStyle oddStyle = wb.createCellStyle();
            oddStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            HSSFPalette palette = wb.getCustomPalette();
            palette.setColorAtIndex(HSSFColor.GREY_25_PERCENT.index,
                    (byte) 225,  //RGB red (0-255)
                    (byte) 225,    //RGB green
                    (byte) 245     //RGB blue
            );
            oddStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            oddStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            
            HSSFCellStyle evenStyle = wb.createCellStyle();
            evenStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
     
            
            //Now create the week rows, starting with sunday before or equal to start date
            Calendar calendarStart = ReportingObjectGroupUtil.findSundayBeforeOrEqualToStartDate(startDate);
            while (calendarStart.getTime().getTime() <= endDate.getTime()){
            	//draw a whole week:  0-based
            	//shift all contents of spreadsheet down 1 row:
            	insertRow = sheet.createRow(insertRow.getRowNum() + 1);
            	sheet.shiftRows(insertRow.getRowNum(), sheet.getLastRowNum(), 1);
            	HSSFCell dateCell = insertRow.createCell(dateCol);
            		
            	dateCell.setCellType(HSSFCell.CELL_TYPE_STRING);
            	if (dateCell.getRowIndex() % 2 == 0)
            		dateCell.setCellStyle(oddStyle);
            	else
            		dateCell.setCellStyle(evenStyle);
            	HSSFRichTextString weekDate = new HSSFRichTextString(Context.getDateFormat().format(calendarStart.getTime()));
            	weekDate.applyFont(fontBold);
            	dateCell.setCellValue(weekDate);
            	colWeekStartNumIter = colWeekStartNum;
            	Calendar weeklyCal = new GregorianCalendar();
            	weeklyCal.setTime(calendarStart.getTime());
            	for (int i = 0; i < days.length; i++) {
            		HSSFCell monthCell = insertRow.createCell(colWeekStartNumIter);
	                monthCell.setCellType(HSSFCell.CELL_TYPE_STRING);
	            	if (insertRow.getRowNum() % 2 == 0)
	            		monthCell.setCellStyle(oddStyle);
	            	else
	            		monthCell.setCellStyle(evenStyle);
	                monthCell.setCellValue(new HSSFRichTextString("#cal_" + sdfIndicatorVar.format(weeklyCal.getTime()) + "#")); //indicator_name
	                sheet.addMergedRegion(new CellRangeAddress(insertRow.getRowNum(), insertRow.getRowNum(), colWeekStartNumIter, colWeekStartNumIter + 2));
	                colWeekStartNumIter = colWeekStartNumIter + 3;
	                weeklyCal.add(Calendar.DATE, 1);
	            }
            	calendarStart.add(Calendar.DATE, 7);
            }

		}
	}
	
	private static HSSFCell getCellWithCalendarInsert(HSSFSheet sheet){
		for (Iterator<HSSFRow> rit = sheet.rowIterator(); rit.hasNext(); ) {
			HSSFRow row = rit.next();
			for (Iterator<HSSFCell> cit = row.cellIterator(); cit.hasNext(); ) {
				HSSFCell cell = cit.next();
				if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING && cell.getRichStringCellValue().getString().equals(DRAW_CALENDAR_HERE)){
					return cell;	
				}
			}
		}
		return null;
	}
	
}
