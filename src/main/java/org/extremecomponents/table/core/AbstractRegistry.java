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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.extremecomponents.table.context.Context;
import org.extremecomponents.table.state.State;
import org.extremecomponents.tree.TreeConstants;

/**
 * @author Jeff Johnston
 */
public abstract class AbstractRegistry implements Registry {
    protected Map<String, String[]> parameterMap;
    protected Context context;
    protected String tableId;
    protected String prefixWithTableId;
    protected String state;
    protected String stateAttr;
    protected boolean autoIncludeParameters;

    public void setParameterMap() {
        Map<String, String[]> tableParameterMap = new HashMap<>();
        Map<String, String[]> userDefinedParameterMap = new HashMap<>();

        Map<String, String[]> params = context.getParameterMap();
        for (Iterator<String> iter = params.keySet().iterator(); iter.hasNext();) {
            String paramName = iter.next();

            //throw-away parameter for exporting so never store this
            if (paramName.equals(TableConstants.EXPORT_TABLE_ID) ||
                    paramName.equals(TableConstants.EXTREME_COMPONENTS_INSTANCE)) {
                continue;
            }

            if (paramName.startsWith(prefixWithTableId + TableConstants.PAGE)
                    || paramName.startsWith(prefixWithTableId + TableConstants.CURRENT_ROWS_DISPLAYED)
                    || paramName.startsWith(prefixWithTableId + TableConstants.SORT)
                    || paramName.startsWith(prefixWithTableId + TableConstants.FILTER)
                    || paramName.startsWith(prefixWithTableId + TableConstants.EXPORT_VIEW)
                    || paramName.startsWith(prefixWithTableId + TableConstants.EXPORT_FILE_NAME)
                    || paramName.startsWith(prefixWithTableId + TableConstants.ALIAS)
                    || paramName.startsWith(prefixWithTableId + TreeConstants.OPEN)) {
                String[] paramValues = TableModelUtils.getValueAsArray(params.get(paramName));
                tableParameterMap.put(paramName, paramValues);
            } else {
                if (autoIncludeParameters) {
                    String[] paramValues = TableModelUtils.getValueAsArray(params.get(paramName));
                    userDefinedParameterMap.put(paramName, paramValues);
                }
            }
        }

        this.parameterMap = handleState(tableParameterMap);
        parameterMap.putAll(userDefinedParameterMap);
    }

    public Map<String, String[]> handleState(Map<String, String[]> tableParameterMap) {
        final State s = TableCache.getInstance().getState(this.state);

        if (tableParameterMap.isEmpty()) {
            Map<String, String[]> stateParameters = s.getParameters(context, tableId, stateAttr);
            if (stateParameters != null) {
                tableParameterMap = stateParameters;
            }
        }

        handleStateInternal(s, tableParameterMap);

        return tableParameterMap;
    }

    /**
     * Add additional parameter to the registry.
     */
    public void addParameter(String name, Object value) {
        String[] paramValues = TableModelUtils.getValueAsArray(value);
        parameterMap.put(name, paramValues);
    }

    /**
     * Get parameter out of the parameterMap. If there is more than one
     * parameter value related to this parameter then this will return the first
     * value.
     *
     * @param name The parameter name to add
     */
    public String getParameter(String parameter) {
        String[] values = parameterMap.get(parameter);
        if (values != null && values.length > 0) {
            return values[0];
        }

        return null;
    }

    /**
     * Get all the parameters as a Map
     */
    public Map<String, String[]> getParameterMap() {
        return parameterMap;
    }

    /**
     * Remove specific parameter from the Registry. If the parameter starts
     * with a value in the Registry it will be removed.
     *
     * @param name The parameter name to remove
     */
    public void removeParameter(String parameter) {
        parameterMap.remove(parameter);
    }

    protected abstract void handleStateInternal(State state, Map<String, String[]> tableParameterMap);
}
