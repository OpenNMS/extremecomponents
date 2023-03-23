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
package org.extremecomponents.table.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.extremecomponents.table.bean.Table;
import org.extremecomponents.table.limit.MockLimit;
import org.extremecomponents.test.ExtremeTableTestCase;

/**
 * @author Jeff Johnston
 */
public class TableModelUtilsTest extends ExtremeTableTestCase {
    public void testRetrieveRows() {
        Table table = getTable(new ArrayList());
        model.addTable(table);

        Collection rows = null;

        try {
            rows = TableModelUtils.retrieveRows(model);
        } catch (Exception e) {
            fail("Could not retrieve rows.");
        }

        assertNotNull("Retrieved the rows:", rows);
    }

    public void testRetrieveRowsNoCollection() {
        Table table = getTable();
        model.addTable(table);

        Collection rows = null;

        try {
            rows = TableModelUtils.retrieveRows(model);
        } catch (Exception e) {
            fail("Could not retrieve rows.");
        }

        assertNotNull("Retrieved the rows with no collection:", rows);
    }

    public void testGetParamValueAsArray() {
        String paramValue[] = TableModelUtils.getValueAsArray(null);
        assertTrue(paramValue instanceof String[]);

        paramValue = TableModelUtils.getValueAsArray("TestParam");
        assertTrue(paramValue instanceof String[]);

        paramValue = TableModelUtils.getValueAsArray(new String[]{"foo", "foo2"});
        assertTrue(paramValue instanceof String[]);

        paramValue = TableModelUtils.getValueAsArray(new ArrayList());
        assertTrue(paramValue instanceof String[]);

        paramValue = TableModelUtils.getValueAsArray(new Integer(12));
        assertTrue(paramValue instanceof String[]);
    }
    
    public void testGetCurrentRowsSpecificPage() {
        Collection rows = getTestData();
        
        //restrict to 15 elements
        int i = 0;
        for (Iterator iter = rows.iterator(); iter.hasNext();) {
            iter.next();
            if (i != 15) {
                i++;
                continue;
            }
            iter.remove();
        }
        
        assertEquals(15, rows.size());
        
        MockLimit limit = getLimit();
        limit.setRowStart(16);
        limit.setRowEnd(31);
        
        model.setLimit(limit);

        Collection results = TableModelUtils.getCurrentRows(model, rows);
        assertEquals(15, results.size());
    }
    
    public void testGetCurrentRowsRowEndTooLarge() {
        Collection rows = getTestData();
        
        //restrict to 15 elements
        int i = 0;
        for (Iterator iter = rows.iterator(); iter.hasNext();) {
            iter.next();
            if (i != 30) {
                i++;
                continue;
            }
            iter.remove();
        }
        
        assertEquals(30, rows.size());
        
        MockLimit limit = getLimit();
        limit.setRowStart(15);
        limit.setRowEnd(35);
        
        model.setLimit(limit);
        
        final Collection results = TableModelUtils.getCurrentRows(model, rows);
        assertEquals(15, results.size());
    }    
}
