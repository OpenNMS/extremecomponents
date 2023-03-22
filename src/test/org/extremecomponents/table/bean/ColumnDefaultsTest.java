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
package org.extremecomponents.table.bean;

import org.apache.commons.lang.StringUtils;
import org.extremecomponents.test.ExtremeTableTestCase;

/**
 * @author Jeff Johnston
 */
public class ColumnDefaultsTest extends ExtremeTableTestCase {
    public void testGetCalcTitle() {
        assertNotNull("null pointer in title:", ColumnDefaults.getCalcTitle(model, null));

        String calcTitle[] = ColumnDefaults.getCalcTitle(model, new String[]{"Total"});
        
        assertNotNull("Have the title:", calcTitle);

        String calcTitleWithKey[] = ColumnDefaults.getCalcTitle(model, new String[]{"column.calc.total", "column.calc.average"});
        
        for (int i = 0; i < calcTitleWithKey.length; i++) {
            String title = calcTitleWithKey[i];
            assertFalse("Have the title with key:", StringUtils.contains(title, "."));
        }
    }
    
    public void testGetParse() {
        Table table = getTableWithTestData();
        model.addTable(table);
        
        Column dateColumn = getDateColumn();
        dateColumn.setCell("date");
        model.addColumn(dateColumn);

        String parse = ColumnDefaults.getParse(model, dateColumn, "yyyy-MM-dd");
        
        assertEquals("The parse setting the parse first", parse, "yyyy-MM-dd");

        parse = ColumnDefaults.getParse(model, dateColumn, null);
        
        assertEquals("The parse getting the default parse", parse, "yyyy-MM-dd");
    }
    
    public void testGetFormat() {
        Table table = getTableWithTestData();
        model.addTable(table);
        
        Column dateColumn = getDateColumn();
        dateColumn.setCell("date");
        model.addColumn(dateColumn);

        String format = ColumnDefaults.getFormat(model, dateColumn, "MM/dd/yyyy");
        
        assertEquals("The parse setting the format first", format, "MM/dd/yyyy");

        format = ColumnDefaults.getFormat(model, dateColumn, null);
        
        assertEquals("The format getting the default format", format, "MM/dd/yyyy");
    }    
    
    /**
     * test for correct default value for escapeAutoFormat
     */
    public void testIsEscapeAutoFormat(){
        Table table = getTableWithTestData();
        model.addTable(table);

        Column column = getCharColumn();
        model.addColumn(column);

        assertFalse("The default escapeAutoFormat is not false", column.isEscapeAutoFormat());
    }
}
