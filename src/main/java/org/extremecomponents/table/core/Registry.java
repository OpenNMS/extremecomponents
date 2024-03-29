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

import java.util.Map;

/**
 * The Registry holds all the parameters, whether specific to the table actions,
 * or user defined. The methods closely match the servlet spec on purpose.
 * 
 * @author Jeff Johnston
 */
public interface Registry {

    public Map<String, String[]> handleState(Map<String, String[]> tableParameterMap);

    public void addParameter(String name, Object value);

    public String getParameter(String parameter);

    public void setParameterMap();

    public Map<String, String[]> getParameterMap();

    public void removeParameter(String parameter);
}