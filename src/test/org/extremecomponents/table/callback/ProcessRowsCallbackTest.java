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
package org.extremecomponents.table.callback;

import java.util.Collection;

import org.extremecomponents.table.bean.Column;
import org.extremecomponents.table.bean.Table;
import org.extremecomponents.table.core.TableConstants;
import org.extremecomponents.table.limit.Filter;
import org.extremecomponents.table.limit.FilterSet;
import org.extremecomponents.table.limit.MockLimit;
import org.extremecomponents.test.ExtremeTableTestCase;

/**
 * @author Jeff Johnston
 */
public class ProcessRowsCallbackTest extends ExtremeTableTestCase {
    public void testRetrieveRowsWithCollection() {
        testRetrieveRows(getTestData());
    }

    public void testRetrieveRowsNoCollection() {
        testRetrieveRows("null");
        testRetrieveRows(null);
    }

    public void testRetrieveRows(Object items) {
        Table table = getTable(items);
        model.addTable(table);

        ProcessRowsCallback processRowsCallback = new ProcessRowsCallback();

        Collection rows = null;

        try {
            rows = processRowsCallback.retrieveRows(model);
        } catch (Exception e) {
            fail("The rows are not retrieved!");
        }

        assertNotNull("The rows are retrieved with no collection:", rows);
    }

    public void testFilterRows() {
        MockLimit limit = getLimit();
        Filter filter = getCharFilter("a");
        FilterSet filterSet = new FilterSet(TableConstants.FILTER_ACTION, new Filter[] { filter });
        limit.setFilterSet(filterSet);
        model.setLimit(limit);

        Table table = getTableWithTestData();
        model.addTable(table);

        Column charColumn = getCharColumn();
        model.addColumn(charColumn);

        Column numericColumn = getNumericColumn();
        model.addColumn(numericColumn);

        Column dateColumn = getDateColumn();
        model.addColumn(dateColumn);

        ProcessRowsCallback processRowsCallback = new ProcessRowsCallback();

        Collection rows = null;

        try {
            rows = processRowsCallback.filterRows(model, (Collection) table.getItems());
        } catch (Exception e) {
            fail("The rows are not filtered!");
        }

        assertTrue("The rows are filtered:", !rows.isEmpty());
    }
}
