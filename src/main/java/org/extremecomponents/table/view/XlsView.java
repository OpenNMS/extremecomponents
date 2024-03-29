/*
 * Copyright 2004 original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.extremecomponents.table.view;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.extremecomponents.table.bean.Column;
import org.extremecomponents.table.calc.CalcResult;
import org.extremecomponents.table.calc.CalcUtils;
import org.extremecomponents.table.core.PreferencesConstants;
import org.extremecomponents.table.core.TableModel;
import org.extremecomponents.util.ExtremeUtils;

/**
 * com.extremecomp.table.view.XlsView.java -
 *
 * @author paul horn
 */
public class XlsView implements View {
    private static Log logger = LogFactory.getLog(XlsView.class);
    public static final int WIDTH_MULT = 240; // width per char
    public static final int MIN_CHARS = 8; // minimum char width
    public static final short DEFAULT_FONT_HEIGHT = 8;
    public static final double NON_NUMERIC = -.99999;
    public static final String DEFAULT_MONEY_FORMAT = "$###,###,##0.00";
    public static final String DEFAULT_PERCENT_FORMAT = "##0.0%";
    public static final String NBSP = "&nbsp;";

    private HSSFWorkbook wb;
    private HSSFSheet sheet;
    private Map<String, HSSFCellStyle> styles;
    private short rownum;
    private short cellnum;
    private HSSFRow hssfRow;
    private String moneyFormat;
    private String percentFormat;
    private String encoding;

    public XlsView() {
    }

    public void beforeBody(TableModel model) {
        logger.debug("XlsView.init()");

        moneyFormat = model.getPreferences().getPreference(PreferencesConstants.TABLE_EXPORTABLE + "format.money");
        if (StringUtils.isEmpty(moneyFormat)) {
            moneyFormat = DEFAULT_MONEY_FORMAT;
        }
        percentFormat = model.getPreferences().getPreference(PreferencesConstants.TABLE_EXPORTABLE + "format.percent");
        if (StringUtils.isEmpty(percentFormat)) {
            percentFormat = DEFAULT_PERCENT_FORMAT;
        }

        encoding = model.getExportHandler().getCurrentExport().getEncoding();

        wb = new HSSFWorkbook();
        sheet = wb.createSheet();

        wb.setSheetName(0, "Export Workbook");

        styles = initStyles(wb);
        final HSSFPrintSetup ps = sheet.getPrintSetup();

        sheet.setAutobreaks(true);
        ps.setFitHeight((short) 1);
        ps.setFitWidth((short) 1);

        createHeader(model);
    }

    public void body(TableModel model, Column column) {
        if (column.isFirstColumn()) {
            rownum++;
            cellnum = 0;
            hssfRow = sheet.createRow(rownum);
        }

        String value = ExportViewUtils.parseXLS(column.getCellDisplay());

        HSSFCell hssfCell = hssfRow.createCell(cellnum);

        setCellEncoding(hssfCell);

        if (column.isEscapeAutoFormat()) {
            writeToCellAsText(hssfCell, value, "");
        } else {
            writeToCellFormatted(hssfCell, value, "");
        }
        cellnum++;
    }

    public Object afterBody(TableModel model) {
        if (model.getLimit().getTotalRows() != 0) {
            totals(model);
        }
        return wb;
    }

    private void createHeader(TableModel model) {
        rownum = 0;
        cellnum = 0;
        HSSFRow row = sheet.createRow(rownum);

        List<Column> columns = model.getColumnHandler().getHeaderColumns();
        for (Iterator<Column> iter = columns.iterator(); iter.hasNext();) {
            Column column = iter.next();
            String title = column.getCellDisplay();
            HSSFCell hssfCell = row.createCell(cellnum);

            setCellEncoding(hssfCell);

            hssfCell.setCellStyle(styles.get("titleStyle"));
            hssfCell.setCellType(CellType.STRING);
            hssfCell.setCellValue(title);
            int valWidth = (title + "").length() * WIDTH_MULT;
            sheet.setColumnWidth(hssfCell.getColumnIndex(), (short) valWidth);

            cellnum++;
        }
    }

    private void writeToCellAsText(HSSFCell cell, String value, String styleModifier) {
        // format text
        if (value.trim().equals(NBSP)) {
            value = "";
        }
        cell.setCellStyle(styles.get("textStyle" + styleModifier));
        fixWidthAndPopulate(cell, NON_NUMERIC, value);
    }

    private void writeToCellFormatted(HSSFCell cell, String value, String styleModifier) {
        double numeric = NON_NUMERIC;

        try {
            numeric = Double.parseDouble(value);
        } catch (Exception e) {
            numeric = NON_NUMERIC;
        }

        if (value.startsWith("$") || value.endsWith("%") || value.startsWith("($")) {
            boolean moneyFlag = (value.startsWith("$") || value.startsWith("($"));
            boolean percentFlag = value.endsWith("%");

            value = StringUtils.replace(value, "$", "");
            value = StringUtils.replace(value, "%", "");
            value = StringUtils.replace(value, ",", "");
            value = StringUtils.replace(value, "(", "-");
            value = StringUtils.replace(value, ")", "");

            try {
                numeric = Double.parseDouble(value);
            } catch (Exception e) {
                numeric = NON_NUMERIC;
            }

            cell.setCellType(CellType.NUMERIC);

            if (moneyFlag) {
                // format money
                cell.setCellStyle(styles.get("moneyStyle" + styleModifier));
            } else if (percentFlag) {
                // format percent
                numeric = numeric / 100;
                cell.setCellStyle(styles.get("percentStyle" + styleModifier));
            }
        } else if (numeric != NON_NUMERIC) {
            // format numeric
            cell.setCellStyle(styles.get("numericStyle" + styleModifier));
        } else {
            // format text
            if (value.trim().equals(NBSP)) {
                value = "";
            }
            cell.setCellStyle(styles.get("textStyle" + styleModifier));
        }

        fixWidthAndPopulate(cell, numeric, value);
    }

    private void fixWidthAndPopulate(HSSFCell cell, double numeric, String value) {
        int valWidth = 0;

        if (numeric != NON_NUMERIC) {
            cell.setCellValue(numeric);
            valWidth = (cell.getNumericCellValue() + "$,.").length() * WIDTH_MULT;
        } else {
            cell.setCellValue(value);
            valWidth = (cell.getStringCellValue() + "").length() * WIDTH_MULT;

            if (valWidth < (WIDTH_MULT * MIN_CHARS)) {
                valWidth = WIDTH_MULT * MIN_CHARS;
            }
        }

        if (valWidth > sheet.getColumnWidth(cell.getColumnIndex())) {
            sheet.setColumnWidth(cell.getColumnIndex(), valWidth);
        }
    }

    private Map<String, HSSFCellStyle> initStyles(HSSFWorkbook wb) {
        return initStyles(wb, DEFAULT_FONT_HEIGHT);
    }

    private Map<String, HSSFCellStyle> initStyles(HSSFWorkbook wb, short fontHeight) {
        Map<String, HSSFCellStyle> result = new HashMap<>();
        HSSFCellStyle titleStyle = wb.createCellStyle();
        HSSFCellStyle textStyle = wb.createCellStyle();
        HSSFCellStyle boldStyle = wb.createCellStyle();
        HSSFCellStyle numericStyle = wb.createCellStyle();
        HSSFCellStyle numericStyleBold = wb.createCellStyle();
        HSSFCellStyle moneyStyle = wb.createCellStyle();
        HSSFCellStyle moneyStyleBold = wb.createCellStyle();
        HSSFCellStyle percentStyle = wb.createCellStyle();
        HSSFCellStyle percentStyleBold = wb.createCellStyle();

        // Add to export totals
        HSSFCellStyle moneyStyle_Totals = wb.createCellStyle();
        HSSFCellStyle naStyle_Totals = wb.createCellStyle();
        HSSFCellStyle numericStyle_Totals = wb.createCellStyle();
        HSSFCellStyle percentStyle_Totals = wb.createCellStyle();
        HSSFCellStyle textStyle_Totals = wb.createCellStyle();

        result.put("titleStyle", titleStyle);
        result.put("textStyle", textStyle);
        result.put("boldStyle", boldStyle);
        result.put("numericStyle", numericStyle);
        result.put("numericStyleBold", numericStyleBold);
        result.put("moneyStyle", moneyStyle);
        result.put("moneyStyleBold", moneyStyleBold);
        result.put("percentStyle", percentStyle);
        result.put("percentStyleBold", percentStyleBold);

        // Add to export totals
        result.put("moneyStyle_Totals", moneyStyle_Totals);
        result.put("naStyle_Totals", naStyle_Totals);
        result.put("numericStyle_Totals", numericStyle_Totals);
        result.put("percentStyle_Totals", percentStyle_Totals);
        result.put("textStyle_Totals", textStyle_Totals);

        HSSFDataFormat format = wb.createDataFormat();

        // Global fonts
        HSSFFont font = wb.createFont();
        font.setBold(false);
        font.setColor(HSSFColorPredefined.BLACK.getIndex());
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setFontHeightInPoints(fontHeight);

        HSSFFont fontBold = wb.createFont();
        fontBold.setBold(true);
        fontBold.setColor(HSSFColorPredefined.BLACK.getIndex());
        fontBold.setFontName(HSSFFont.FONT_ARIAL);
        fontBold.setFontHeightInPoints(fontHeight);

        // Money Style
        moneyStyle.setFont(font);
        moneyStyle.setAlignment(HorizontalAlignment.RIGHT);
        moneyStyle.setDataFormat(format.getFormat(moneyFormat));

        // Money Style Bold
        moneyStyleBold.setFont(fontBold);
        moneyStyleBold.setAlignment(HorizontalAlignment.RIGHT);
        moneyStyleBold.setDataFormat(format.getFormat(moneyFormat));

        // Percent Style
        percentStyle.setFont(font);
        percentStyle.setAlignment(HorizontalAlignment.RIGHT);
        percentStyle.setDataFormat(format.getFormat(percentFormat));

        // Percent Style Bold
        percentStyleBold.setFont(fontBold);
        percentStyleBold.setAlignment(HorizontalAlignment.RIGHT);
        percentStyleBold.setDataFormat(format.getFormat(percentFormat));

        // Standard Numeric Style
        numericStyle.setFont(font);
        numericStyle.setAlignment(HorizontalAlignment.RIGHT);

        // Standard Numeric Style Bold
        numericStyleBold.setFont(fontBold);
        numericStyleBold.setAlignment(HorizontalAlignment.RIGHT);

        // Title Style
        titleStyle.setFont(font);
        titleStyle.setFillForegroundColor(HSSFColorPredefined.GREY_25_PERCENT.getIndex());
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setBorderBottom(BorderStyle.THIN);
        titleStyle.setBottomBorderColor(HSSFColorPredefined.BLACK.getIndex());
        titleStyle.setBorderLeft(BorderStyle.THIN);
        titleStyle.setLeftBorderColor(HSSFColorPredefined.BLACK.getIndex());
        titleStyle.setBorderRight(BorderStyle.THIN);
        titleStyle.setRightBorderColor(HSSFColorPredefined.BLACK.getIndex());
        titleStyle.setBorderTop(BorderStyle.THIN);
        titleStyle.setTopBorderColor(HSSFColorPredefined.BLACK.getIndex());
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Standard Text Style
        textStyle.setFont(font);
        textStyle.setWrapText(true);

        // Standard Text Style
        boldStyle.setFont(fontBold);
        boldStyle.setWrapText(true);

        // Money Style Total
        moneyStyle_Totals.setFont(fontBold);
        moneyStyle_Totals.setFillForegroundColor(HSSFColorPredefined.GREY_25_PERCENT.getIndex());
        moneyStyle_Totals.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        moneyStyle_Totals.setBorderBottom(BorderStyle.THIN);
        moneyStyle_Totals.setBottomBorderColor(HSSFColorPredefined.BLACK.getIndex());
        moneyStyle_Totals.setBorderTop(BorderStyle.THIN);
        moneyStyle_Totals.setTopBorderColor(HSSFColorPredefined.BLACK.getIndex());
        moneyStyle_Totals.setAlignment(HorizontalAlignment.RIGHT);
        moneyStyle_Totals.setVerticalAlignment(VerticalAlignment.CENTER);
        moneyStyle_Totals.setDataFormat(format.getFormat(moneyFormat));

        // n/a Style Total
        naStyle_Totals.setFont(fontBold);
        naStyle_Totals.setFillForegroundColor(HSSFColorPredefined.GREY_25_PERCENT.getIndex());
        naStyle_Totals.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        naStyle_Totals.setBorderBottom(BorderStyle.THIN);
        naStyle_Totals.setBottomBorderColor(HSSFColorPredefined.BLACK.getIndex());
        naStyle_Totals.setBorderTop(BorderStyle.THIN);
        naStyle_Totals.setTopBorderColor(HSSFColorPredefined.BLACK.getIndex());
        naStyle_Totals.setAlignment(HorizontalAlignment.RIGHT);
        naStyle_Totals.setVerticalAlignment(VerticalAlignment.CENTER);

        // Numeric Style Total
        numericStyle_Totals.setFont(fontBold);
        numericStyle_Totals.setFillForegroundColor(HSSFColorPredefined.GREY_25_PERCENT.getIndex());
        numericStyle_Totals.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        numericStyle_Totals.setBorderBottom(BorderStyle.THIN);
        numericStyle_Totals.setBottomBorderColor(HSSFColorPredefined.BLACK.getIndex());
        numericStyle_Totals.setBorderTop(BorderStyle.THIN);
        numericStyle_Totals.setTopBorderColor(HSSFColorPredefined.BLACK.getIndex());
        numericStyle_Totals.setAlignment(HorizontalAlignment.RIGHT);
        numericStyle_Totals.setVerticalAlignment(VerticalAlignment.CENTER);

        // Percent Style Total
        percentStyle_Totals.setFont(fontBold);
        percentStyle_Totals.setFillForegroundColor(HSSFColorPredefined.GREY_25_PERCENT.getIndex());
        percentStyle_Totals.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        percentStyle_Totals.setBorderBottom(BorderStyle.THIN);
        percentStyle_Totals.setBottomBorderColor(HSSFColorPredefined.BLACK.getIndex());
        percentStyle_Totals.setBorderTop(BorderStyle.THIN);
        percentStyle_Totals.setTopBorderColor(HSSFColorPredefined.BLACK.getIndex());
        percentStyle_Totals.setAlignment(HorizontalAlignment.RIGHT);
        percentStyle_Totals.setVerticalAlignment(VerticalAlignment.CENTER);
        percentStyle_Totals.setDataFormat(format.getFormat(percentFormat));

        // Text Style Total
        textStyle_Totals.setFont(fontBold);
        textStyle_Totals.setFillForegroundColor(HSSFColorPredefined.GREY_25_PERCENT.getIndex());
        textStyle_Totals.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        textStyle_Totals.setBorderBottom(BorderStyle.THIN);
        textStyle_Totals.setBottomBorderColor(HSSFColorPredefined.BLACK.getIndex());
        textStyle_Totals.setBorderTop(BorderStyle.THIN);
        textStyle_Totals.setTopBorderColor(HSSFColorPredefined.BLACK.getIndex());
        textStyle_Totals.setAlignment(HorizontalAlignment.LEFT);
        textStyle_Totals.setVerticalAlignment(VerticalAlignment.CENTER);

        return result;
    }

    // Add to export totals
    public void totals(TableModel model) {
        Column firstCalcColumn = model.getColumnHandler().getFirstCalcColumn();
        if (firstCalcColumn != null) {
            int rows = firstCalcColumn.getCalc().length;
            for (int i = 0; i < rows; i++) {
                rownum++;
                HSSFRow row = sheet.createRow(rownum);
                cellnum = 0;
                for (Iterator<Column> iter = model.getColumnHandler().getColumns().iterator(); iter.hasNext();) {
                    Column column = iter.next();
                    if (column.isFirstColumn()) {
                        String calcTitle = CalcUtils.getFirstCalcColumnTitleByPosition(model, i);
                        HSSFCell cell = row.createCell(cellnum);
                        setCellEncoding(cell);
                        if (column.isEscapeAutoFormat()) {
                            writeToCellAsText(cell, calcTitle, "_Totals");
                        } else {
                            writeToCellFormatted(cell, calcTitle, "_Totals");
                        }
                        cellnum++;
                        continue;
                    }

                    if (column.isCalculated()) {
                        CalcResult calcResult = CalcUtils.getCalcResultsByPosition(model, column, i);
                        Number value = calcResult.getValue();
                        HSSFCell cell = row.createCell(cellnum);
                        setCellEncoding(cell);
                        if (value != null) {
                            if (column.isEscapeAutoFormat()) {
                                writeToCellAsText(cell, value.toString(), "_Totals");
                            } else {
                                writeToCellFormatted(cell, ExtremeUtils.formatNumber(column.getFormat(), value, model.getLocale()), "_Totals");
                            }
                        } else {
                            cell.setCellStyle(styles.get("naStyle_Totals"));
                            cell.setCellValue("n/a");
                        }
                        cellnum++;
                    } else {
                        HSSFCell cell = row.createCell(cellnum);
                        setCellEncoding(cell);
                        writeToCellFormatted(cell, "", "_Totals");
                        cellnum++;
                    }
                }
            }
        }

    }


    //add to set Cell encoding
    private void setCellEncoding(HSSFCell cell) {
        // not sure how to do this in newer POI, not sure it's even relevant
        /*
        if (encoding.equalsIgnoreCase("UTF")) {
            cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        } else if (encoding.equalsIgnoreCase("UNICODE")) {
            cell.setEncoding(HSSFCell.ENCODING_COMPRESSED_UNICODE);
        }
        */
    }

}
