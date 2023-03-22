/*
 * Copyright 2004 original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.extremecomponents.test;

import java.util.Collection;

import junit.framework.TestCase;

import org.extremecomponents.table.bean.Column;
import org.extremecomponents.table.bean.Table;
import org.extremecomponents.table.context.MockContext;
import org.extremecomponents.table.core.TableConstants;
import org.extremecomponents.table.core.TableModel;
import org.extremecomponents.table.core.TableModelImpl;
import org.extremecomponents.table.limit.Filter;
import org.extremecomponents.table.limit.MockLimit;

/**
 * @author Jeff Johnston
 */
public abstract class ExtremeTableTestCase extends TestCase {
    // meant to be overridden
    protected static String PREFERENCES_LOCATION = "/org/extremecomponents/table/resource/extremecomponents.properties";
    protected static String CHAR_COLUMN = "firstName";
    protected static String DATE_COLUMN = "born";
    protected static String NUMERIC_COLUMN = "salary";
    
    protected TableModel model;

    protected final void setUp() throws Exception {
        MockContext context = new MockContext();
        context.setApplicationInitParameter(TableConstants.PREFERENCES_LOCATION, PREFERENCES_LOCATION);
        model = new TableModelImpl(context);
        onSetUp();
    }

    /**
     * Subclasses can override this method in place of the setUp() method, which
     * is final in this class.
     * 
     * @throws Exception
     */
    protected void onSetUp() throws Exception {
    }
    
    protected MockLimit getLimit() {
        return new MockLimit();
    }
    
    protected Filter getFilter(String alias, String property, String value) {
        return new Filter(alias, property, value);
    }

    protected Filter getCharFilter(String value) {
        return new Filter(CHAR_COLUMN, CHAR_COLUMN, value);
    }
    
    protected Filter getDateFilter(String value) {
        return new Filter(DATE_COLUMN, DATE_COLUMN, value);
    }

    protected Filter getNumericFilter(String value) {
        return new Filter(NUMERIC_COLUMN, NUMERIC_COLUMN, value);
    }

    protected Collection getTestData() {
        PresidentsDao dao = new PresidentsDao();
        return dao.getPresidents();
    }
    
    protected Table getTableWithTestData() {
        return getTable(getTestData());
    }
    
    protected Table getTable() {
        return getTable(null);
    }
    
    protected Table getTable(Object items) {
        Table table = new Table(model);
        table.setItems(items);
        return table;
    }
    
    protected Column getColumn(String name) {
        Column column = new Column(model);
        column.setProperty(name);
        return column;
    }
    
    protected Column getCharColumn() {
        Column column = new Column(model);
        column.setProperty(CHAR_COLUMN);
        return column;
    }
    
    protected Column getDateColumn() {
        Column column = new Column(model);
        column.setProperty(DATE_COLUMN);
        return column;
    }

    protected Column getNumericColumn() {
        Column column = new Column(model);
        column.setProperty(NUMERIC_COLUMN);
        return column;
    }
}
