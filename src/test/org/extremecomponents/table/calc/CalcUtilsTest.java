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
package org.extremecomponents.table.calc;

import java.math.BigDecimal;
import java.util.Collection;

import org.extremecomponents.table.bean.Column;
import org.extremecomponents.table.bean.Table;
import org.extremecomponents.test.ExtremeTableTestCase;

/**
 * @author Jeff Johnston
 */
public class CalcUtilsTest extends ExtremeTableTestCase {
    public void testGetCalcResults() {
        Collection rows = getTestData();

        model.setCollectionOfFilteredBeans(rows);

        Table table = getTable(rows);
        model.addTable(table);

        Column numericColumn = getNumericColumn();
        numericColumn.setCalc(" ,total");
        model.addColumn(numericColumn);

        CalcResult calcResult = null;

        try {
            calcResult = CalcUtils.getCalcResultsByPosition(model, numericColumn, 1);
        } catch (Exception e) {
            fail("Could not find the calc result!");
        }

        assertNotNull("Have the calc result:", calcResult);
    }

    public void testGetFirstCalcColumnTitles() {
        Collection rows = getTestData();

        model.setCollectionOfFilteredBeans(rows);

        Table table = getTable(rows);
        model.addTable(table);

        Column numericColumn = getNumericColumn();
        numericColumn.setCalc("average,total");
        numericColumn.setCalcTitle("Average,Total");
        model.addColumn(numericColumn);

        String[] values = null;

        try {
            values = CalcUtils.getFirstCalcColumnTitles(model);
        } catch (Exception e) {
            fail("Could not find the calc titles!");
        }

        assertTrue("Have the calc titles:", values.length > 0);
    }

    public void testEachRowCalcValue() {
        Collection rows = getTestData();

        model.setCollectionOfFilteredBeans(rows);

        Table table = getTable(rows);
        model.addTable(table);

        Column numericColumn = getNumericColumn();
        numericColumn.setCalc("total");
        model.addColumn(numericColumn);

        Number value = null;

        try {
            TestValue testValue = new TestValue();
            CalcUtils.eachRowCalcValue(testValue, getTestData(), numericColumn.getProperty());
            value = testValue.getValue();
        } catch (Exception e) {
            fail("Could not find each calc value!");
        }

        assertTrue("Have the each calc value:", value.doubleValue() > 1);
    }

    private static class TestValue implements CalcHandler {
        double total = 0.00;

        public void processCalcValue(Number value) {
            total += value.doubleValue();
        }

        public Number getValue() {
            return new BigDecimal(total);
        }
    }
}
