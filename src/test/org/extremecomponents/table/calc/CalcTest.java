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

import java.util.ArrayList;
import java.util.Collection;

import org.extremecomponents.table.bean.Column;
import org.extremecomponents.table.bean.Table;
import org.extremecomponents.test.ExtremeTableTestCase;

public class CalcTest extends ExtremeTableTestCase {
    public void testAverageResult() {
        testAverage(getTestData());
        testAverage(new ArrayList());
    }

    private void testAverage(Collection rows) {
        AverageCalc averageCalc = new AverageCalc();

        model.setCollectionOfFilteredBeans(getTestData());

        Table table = getTable(rows);
        model.addTable(table);

        Column numericOrder = getNumericColumn();
        numericOrder.setCalc("total");
        model.addColumn(numericOrder);

        Number num = averageCalc.getCalcResult(model, numericOrder);

        assertNotNull("Have the average calc:", num);
    }

    public void testTotalResult() {
        testTotal(getTestData());
        testTotal(new ArrayList());
    }

    private void testTotal(Collection rows) {
        TotalCalc totalCalc = new TotalCalc();

        model.setCollectionOfFilteredBeans(getTestData());

        Table table = getTable(rows);
        model.addTable(table);

        Column numericOrder = getNumericColumn();
        numericOrder.setCalc("total");
        model.addColumn(numericOrder);

        Number num = totalCalc.getCalcResult(model, numericOrder);

        assertNotNull("Have the total calc:", num);
    }
}
