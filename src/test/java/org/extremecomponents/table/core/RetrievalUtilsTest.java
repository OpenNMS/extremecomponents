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
package org.extremecomponents.table.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.extremecomponents.table.context.MockContext;
import org.extremecomponents.test.ExtremeTableTestCase;

/**
 * @author Jeff Johnston
 */
public class RetrievalUtilsTest extends ExtremeTableTestCase {
    public void testRetrieveFromPageScope() {
        MockContext context = new MockContext();

        String name = "President";
        String value = "Roslin";
        context.setPageAttribute(name, value);

        Object data = RetrievalUtils.retrieve(context, name);

        assertNotNull("There is data:", data);

        data = RetrievalUtils.retrieve(context, name, TableConstants.PAGE_SCOPE);

        assertNotNull("There is data in page scope:", data);
    }

    public void testRetrieveFromRequestScope() {
        MockContext context = new MockContext();

        String name = "President";
        String value = "Roslin";
        context.setRequestAttribute(name, value);

        Object data = RetrievalUtils.retrieve(context, name);

        assertNotNull("There is data:", data);

        data = RetrievalUtils.retrieve(context, name, TableConstants.REQUEST_SCOPE);

        assertNotNull("There is data in request scope:", data);
    }

    public void testRetrieveFromSessionScope() {
        MockContext context = new MockContext();

        String name = "President";
        String value = "Roslin";
        context.setSessionAttribute(name, value);

        Object data = RetrievalUtils.retrieve(context, name);

        assertNotNull("There is data:", data);

        data = RetrievalUtils.retrieve(context, name, TableConstants.SESSION_SCOPE);

        assertNotNull("There is data in session scope:", data);
    }

    public void testRetrieveFromApplicationScope() {
        MockContext context = new MockContext();

        String name = "President";
        String value = "Roslin";
        context.setApplicationAttribute(name, value);

        Object data = RetrievalUtils.retrieve(context, name);

        assertNotNull("There is data:", data);

        data = RetrievalUtils.retrieve(context, name, TableConstants.APPLICATION_SCOPE);

        assertNotNull("There is data in application scope:", data);
    }

    public void testRetrieveCollectionAsNull() {
        try {
            MockContext context = new MockContext();
            Collection data = RetrievalUtils.retrieveCollection(context, null);
            assertNotNull(data);
        } catch (Exception e) {
            fail();
        }
    }

    public void testRetrieveCollectionAsCollection() {
        try {
            MockContext context = new MockContext();
            Collection data = RetrievalUtils.retrieveCollection(context, getTestData());
            assertTrue(data != null && data.size() > 0);
        } catch (Exception e) {
            fail();
        }
    }

    public void testRetrieveCollectionAsMap() {
        try {
            Map map = new HashMap();
            map.put("foo1", "1");
            map.put("foo2", "2");
            map.put("foo3", "3");
            map.put("foo4", "4");
            map.put("foo5", "5");
            MockContext context = new MockContext();
            Collection data = RetrievalUtils.retrieveCollection(context, map);
            assertTrue(data != null && data.size() > 0);
        } catch (Exception e) {
            fail();
        }
    }

    public void testRetrieveCollectionAsNullString() {
        try {
            MockContext context = new MockContext();
            Collection data = RetrievalUtils.retrieveCollection(context, "null");
            assertTrue(data != null && data.size() == 0);
        } catch (Exception e) {
            fail();
        }
    }

    public void testRetrieveNestedCollection() {
        try {
            MockContext context = new MockContext();
            context.setRequestAttribute("nested", new NestedBean(getTestData()));
            Collection data = RetrievalUtils.retrieveCollection(context, "nested.rows");
            assertTrue(data != null && data.size() > 0);
        } catch (Exception e) {
            fail();
        }
    }

    public void testRetrieveNestedCollectionAsNull() {
        try {
            MockContext context = new MockContext();
            context.setRequestAttribute("nested", new NestedBean(getTestData()));
            Collection data = RetrievalUtils.retrieveCollection(context, "nested.row"); // wrong
                                                                                        // name
            assertTrue(data != null && data.size() == 0);
        } catch (Exception e) {
            fail();
        }
    }

    public void testRetrieveCollectionAsCollectionWithString() {
        try {
            MockContext context = new MockContext();
            Map map = new HashMap();
            map.put("foo1", "1");
            map.put("foo2", "2");
            map.put("foo3", "3");
            map.put("foo4", "4");
            map.put("foo5", "5");
            context.setRequestAttribute("rows", map);
            Collection data = RetrievalUtils.retrieveCollection(context, "rows");
            assertTrue(data != null && data.size() > 0);
        } catch (Exception e) {
            fail();
        }
    }

    public void testRetrieveCollectionAsMapWithString() {
        try {
            MockContext context = new MockContext();
            context.setRequestAttribute("rows", getTestData());
            Collection data = RetrievalUtils.retrieveCollection(context, "rows");
            assertTrue(data != null && data.size() > 0);
        } catch (Exception e) {
            fail();
        }
    }

    public static class NestedBean {
        private Collection rows;

        public NestedBean(Collection rows) {
            this.rows = rows;
        }

        public Collection getRows() {
            return rows;
        }
    }
}
