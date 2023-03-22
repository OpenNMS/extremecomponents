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
package org.extremecomponents.table.limit;

public class MockLimit implements Limit {
    private FilterSet filterSet;
    private Sort sort;
    private boolean exported;
    private int rowStart;
    private int rowEnd;
    private int currentRowsDisplayed;
    private int page;
    private int totalRows;

    public FilterSet getFilterSet() {
        return filterSet;
    }

    public void setFilterSet(FilterSet filterSet) {
        this.filterSet = filterSet;
    }

    public int getRowEnd() {
        return rowEnd;
    }

    public void setRowEnd(int rowEnd) {
        this.rowEnd = rowEnd;
    }

    public int getRowStart() {
        return rowStart;
    }

    public void setRowStart(int rowStart) {
        this.rowStart = rowStart;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getCurrentRowsDisplayed() {
        return currentRowsDisplayed;
    }

    public void setCurrentRowsDisplayed(int currentRowsDisplayed) {
        this.currentRowsDisplayed = currentRowsDisplayed;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public boolean isFiltered() {
        return filterSet.isFiltered();
    }

    public boolean isCleared() {
        return filterSet.isCleared();
    }

    public boolean isSorted() {
        return sort.isSorted();
    }

    public void setExported(boolean exported) {
        this.exported = exported;
    }

    public boolean isExported() {
        return exported;
    }

    public void setRowAttributes(int totalRows, int defaultRowsDisplayed) {
        throw new NoSuchMethodError("The mock cannot set and row attributes. They should be set manually.");
    }
}
