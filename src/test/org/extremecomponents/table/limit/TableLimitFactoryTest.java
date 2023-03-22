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

import org.extremecomponents.table.context.MockContext;
import org.extremecomponents.table.core.TableCache;
import org.extremecomponents.table.core.TableConstants;
import org.extremecomponents.table.state.State;
import org.extremecomponents.test.ExtremeTableTestCase;
import org.extremecomponents.test.ExtremeTableTestHelper;
import org.extremecomponents.test.TableParametersAdapter;

public class TableLimitFactoryTest extends ExtremeTableTestCase {
    public void testInit() {
        MockContext context = (MockContext)model.getContext();
        
        // make use of the helper to add parameters to the context
        TableParametersAdapter adapter = new TableParametersAdapter(context);
        ExtremeTableTestHelper helper = new ExtremeTableTestHelper(adapter);
        helper.addFilter(CHAR_COLUMN, "Hello World");

        // save the state for the test
        State state = TableCache.getInstance().getState("org.extremecomponents.table.state.PersistState");
        state.saveParameters(context, TableConstants.EXTREME_COMPONENTS, context.getParameterMap());
        
        // set the state in the factory and initialize it...
        // (only for test as typically the TableFactory will init the factory)
        TableLimitFactory limitFactory = new TableLimitFactory(context, TableConstants.EXTREME_COMPONENTS, TableConstants.STATE_PERSIST, null);
        
        // validate that getting the parameters from the state
        // this would fail if it was not
//        Map parameterMap = limitFactory.getRegistry().getParameterMap();
//        String filter = helper.getFilter(CHAR_COLUMN);
//        String[] param = (String[])parameterMap.get(filter);
//        
//        assertTrue(param[0].equals("Hello World"));
    }
}
