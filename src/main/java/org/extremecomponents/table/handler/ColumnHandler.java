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
package org.extremecomponents.table.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.extremecomponents.table.bean.Column;
import org.extremecomponents.table.calc.CalcResult;
import org.extremecomponents.table.calc.CalcUtils;
import org.extremecomponents.table.cell.Cell;
import org.extremecomponents.table.core.PreferencesConstants;
import org.extremecomponents.table.core.TableCache;
import org.extremecomponents.table.core.TableConstants;
import org.extremecomponents.table.core.TableModel;
import org.extremecomponents.table.core.TableModelUtils;

/**
 * The first pass over the table just loads up the column properties. The
 * properties will be loaded up and held here.
 * 
 * @author Jeff Johnston
 */
public class ColumnHandler {
    private TableModel model;
    private List<Column> columns = new ArrayList<>();
    private Column firstColumn;
    private Column lastColumn;

    public ColumnHandler(TableModel model) {
        this.model = model;
    }

    public void addAutoGenerateColumn(Column column) {
        column.addAttribute(TableConstants.IS_AUTO_GENERATE_COLUMN, "true");
        addColumn(column);
    }

    public void addColumn(Column column) {
        column.defaults();
        addColumnAttributes(column);

        if (!isViewAllowed(column)) {
            return;
        }

        if (firstColumn == null) {
            firstColumn = column;
            column.setFirstColumn(true);
        }

        if (lastColumn != null) {
            lastColumn.setLastColumn(false);
        }
        lastColumn = column;
        column.setLastColumn(true);

        columns.add(column);
    }
    
    public void addColumnAttributes(Column column) {
        String interceptor = TableModelUtils.getInterceptPreference(model, column.getInterceptor(), PreferencesConstants.COLUMN_INTERCEPTOR);
        column.setInterceptor(interceptor);
        TableCache.getInstance().getColumnInterceptor(interceptor).addColumnAttributes(model, column);
    }

    public void modifyColumnAttributes(Column column) {
        TableCache.getInstance().getColumnInterceptor(column.getInterceptor()).modifyColumnAttributes(model, column);
    }

    public int columnCount() {
        return columns.size();
    }

    public List<Column> getColumns() {
        return columns;
    }

    public Column getColumnByAlias(String alias) {
        for (Iterator<Column> iter = columns.iterator(); iter.hasNext();) {
            Column column = iter.next();
            String columnAlias = column.getAlias();
            if (columnAlias != null && columnAlias.equals(alias)) {
                return column;
            }
        }

        return null;
    }

    public boolean hasMetatData() {
        return columnCount() > 0;
    }

    public List<Column> getFilterColumns() {
        boolean cleared = model.getLimit().isCleared();

        for (Iterator<Column> iter = columns.iterator(); iter.hasNext();) {
            Column column = iter.next();
            String value = model.getLimit().getFilterSet().getFilterValue(column.getAlias());
            if (cleared || StringUtils.isEmpty(value)) {
                value = "";
            }
            Cell cell = TableModelUtils.getFilterCell(column, value);
            column.setCellDisplay(cell.getHtmlDisplay(model, column));
        }

        return columns;
    }

    public List<Column> getHeaderColumns() {
        for (Iterator<Column> iter = columns.iterator(); iter.hasNext();) {
            Column column = iter.next();
            Cell cell = TableModelUtils.getHeaderCell(column, column.getTitle());

            boolean isExported = model.getLimit().isExported();
            if (!isExported) {
                column.setCellDisplay(cell.getHtmlDisplay(model, column));
            } else {
                column.setCellDisplay(cell.getExportDisplay(model, column));
            }
        }

        return columns;
    }

    private boolean isViewAllowed(Column column) {
        String view = model.getTableHandler().getTable().getView();

        boolean isExported = model.getLimit().isExported();
        if (isExported) {
            view = model.getExportHandler().getCurrentExport().getView();
        }

        boolean allowView = allowView(column, view);
        boolean denyView = denyView(column, view);

        return allowView && !denyView;
    }

    private boolean allowView(Column column, String view) {
        String[] viewsAllowed = column.getViewsAllowed();
        if (viewsAllowed == null || viewsAllowed.length == 0) {
            return true;
        }

        for (int i = 0; i < viewsAllowed.length; i++) {
            if (view.equals(viewsAllowed[i])) {
                return true;
            }
        }

        return false;
    }

    private boolean denyView(Column column, String view) {
        String[] viewsDenied = column.getViewsDenied();
        if (viewsDenied == null || viewsDenied.length == 0) {
            return false;
        }

        for (int i = 0; i < viewsDenied.length; i++) {
            if (view.equals(viewsDenied[i])) {
                return true;
            }
        }

        return false;
    }
    
    public Column getFirstCalcColumn() {
        for (Iterator<Column> iter = columns.iterator(); iter.hasNext();) {
            Column column = iter.next();
            if (column.isCalculated()) {
                return column;
            }
        }

        return null;
    }    

    public CalcResult[] getCalcResults(Column column) {
        if (!column.isCalculated()) {
            return new CalcResult[] {};
        }

        return CalcUtils.getCalcResults(model, column);
    }
}