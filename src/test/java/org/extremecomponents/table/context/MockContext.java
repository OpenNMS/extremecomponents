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
package org.extremecomponents.table.context;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Jeff Johnston
 */
public class MockContext implements Context {
    public Map parameterMap = new HashMap();
    public Map page = new HashMap();
    public Map request = new HashMap();
    public Map application = new HashMap();
    public Map applicationInit = new HashMap();
    public Map session = new HashMap();

    public Object getApplicationInitParameter(String name) {
        return applicationInit.get(name);
    }

    public void setApplicationInitParameter(String name, Object value) {
        applicationInit.put(name, value);
    }

    public Object getApplicationAttribute(String name) {
        return application.get(name);
    }

    public void setApplicationAttribute(String name, Object value) {
        application.put(name, value);
    }

    public void removeApplicationAttribute(String name) {
        application.remove(name);
    }

    public Object getPageAttribute(String name) {
        return page.get(name);
    }

    public void setPageAttribute(String name, Object value) {
        page.put(name, value);
    }

    public void removePageAttribute(String name) {
        page.remove(name);
    }
    
    public void addParameter(String name, String value) {
        String[] values = new String[]{value.toString()};
        parameterMap.put(name, values);
    }

    public String getParameter(String name) {
        String[] values = (String[]) parameterMap.get(name);
        if (values != null && values.length > 0) {
            return values[0];
        }
        
        return null;
    }

    public Map getParameterMap() {
        return parameterMap;
    }

    public Object getRequestAttribute(String name) {
        return request.get(name);
    }

    public void setRequestAttribute(String name, Object value) {
        request.put(name, value);
    }

    public void removeRequestAttribute(String name) {
        request.remove(name);
    }

    public Object getSessionAttribute(String name) {
        return session.get(name);
    }

    public void setSessionAttribute(String name, Object value) {
        session.put(name, value);
    }

    public void removeSessionAttribute(String name) {
        session.remove(name);
    }

    public Writer getWriter() {
        return new StringWriter();
    }

    public Locale getLocale() {
        return Locale.US;
    }

    public String getContextPath() {
        return "/extremecomponents";
    }

    public Object getContextObject() {
        return request;
    }
}
