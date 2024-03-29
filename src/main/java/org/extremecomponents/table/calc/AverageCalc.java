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
import org.extremecomponents.table.core.TableModel;

/**
 * @author Jeff Johnston
 */
public class AverageCalc implements Calc {
    public Number getCalcResult(TableModel model, Column column) {
        Collection rows = model.getCollectionOfFilteredBeans();
        String property = column.getProperty();
        AverageValue totalValue = new AverageValue(rows.size());
        CalcUtils.eachRowCalcValue(totalValue, rows, property);

        return totalValue.getAverageValue();
    }
    
    private static class AverageValue implements CalcHandler {
        private double total = 0.00;
        private double rowCount;
        
        public AverageValue(double rowCount) {
            this.rowCount = rowCount;
        }
        
        public void processCalcValue(Number calcValue) {
            total += calcValue.doubleValue();
        }
        
        public Number getAverageValue() {
            if (rowCount > 0) {
                return BigDecimal.valueOf(total / rowCount);
            }
            
            return BigDecimal.valueOf(0.00);
        }
    }
}
