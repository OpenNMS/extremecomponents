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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.extremecomponents.table.context.Context;
import org.extremecomponents.table.core.Registry;
import org.extremecomponents.table.core.TableConstants;

public abstract class AbstractLimitFactory implements LimitFactory {
    protected String tableId;
    protected String prefixWithTableId;
    protected boolean isExported;
    protected Registry registry;
    protected Context context;

    public boolean isExported() {
        return isExported;
    }

    boolean getExported() {
        String exportTableId = context.getParameter(TableConstants.EXPORT_TABLE_ID);
        if (StringUtils.isBlank(exportTableId)) {
            return false;
        }

        return exportTableId.equals(tableId);
    }

    public int getCurrentRowsDisplayed(int totalRows, int rowsDisplayed) {
        if (isExported || !showPagination()) {
            return totalRows;
        }

        String currentRowsDisplayed = registry.getParameter(prefixWithTableId + TableConstants.CURRENT_ROWS_DISPLAYED);
        if (StringUtils.isNotBlank(currentRowsDisplayed)) {
            return Integer.parseInt(currentRowsDisplayed);
        }

        return rowsDisplayed;
    }

    public int getPage() {
        if (isExported) {
            return 1;
        }

        String page = registry.getParameter(prefixWithTableId + TableConstants.PAGE);
        if (!StringUtils.isEmpty(page)) {
            return Integer.parseInt(page);
        }

        return 1;
    }

    public Sort getSort() {
        Map<String, String> sortedParameters = getSortedOrFilteredParameters(TableConstants.SORT);
        if (sortedParameters == null) {
            return new Sort();
        }

        for (Iterator<String> iter = sortedParameters.keySet().iterator(); iter.hasNext();) {
            String propertyOrAlias = iter.next();
            String value = sortedParameters.get(propertyOrAlias);

            if (value.equals(TableConstants.SORT_DEFAULT)) {
                break;
            }

            String property = getProperty(propertyOrAlias);
            return new Sort(propertyOrAlias, property, value);
        }

        return new Sort();
    }

    public FilterSet getFilterSet() {
        Map<String, String> filteredParameters = getSortedOrFilteredParameters(TableConstants.FILTER);
        FilterSet filterSet = getFilterSet(filteredParameters);
        if (filterSet.isCleared()) {
            removeFilterParameters();
            filterSet = new FilterSet(filterSet.getAction(), new Filter[]{});
       }

        return filterSet;
    }

    /**
     * Remove filter parameters from the Registry. If the parameter starts
     * with a value in the Registry it will be removed.
     */
    void removeFilterParameters() {
        Set<String> set = registry.getParameterMap().keySet();
        for (Iterator<String> iter = set.iterator(); iter.hasNext();) {
            String name = iter.next();
            if (name.startsWith(prefixWithTableId + TableConstants.FILTER)) {
                iter.remove();
            }
        }
    }

    FilterSet getFilterSet(Map<String,String> filteredParameters) {
        if (filteredParameters == null) {
            return new FilterSet();
        }

        String action = filteredParameters.get(TableConstants.ACTION);
        List<Filter> filters = new ArrayList<>();

        for (Iterator<String> iter = filteredParameters.keySet().iterator(); iter.hasNext();) {
            String propertyOrAlias = iter.next();
            String value = filteredParameters.get(propertyOrAlias);

            if (StringUtils.isBlank(value) || propertyOrAlias.equals(TableConstants.ACTION)) {
                continue;
            }
            
            String property = getProperty(propertyOrAlias);
            filters.add(new Filter(propertyOrAlias, property, value));
        }

        return new FilterSet(action, filters.toArray(new Filter[filters.size()]));
    }

    public Map<String, String> getSortedOrFilteredParameters(String parameter) {
        Map<String, String> subset = new HashMap<>();

        String find = prefixWithTableId + parameter;

        Set<String> set = registry.getParameterMap().keySet();
        for (Iterator<String> iter = set.iterator(); iter.hasNext();) {
            String key = iter.next();
            if (key.startsWith(find)) {
                String value = registry.getParameter(key);
                if (StringUtils.isNotBlank(value)) {
                    String propertyOrAlias = StringUtils.substringAfter(key, find);
                    subset.put(propertyOrAlias, value);
                }
            }
        }

        return subset;
    }

    /**
     * If alias not equal property,check the alias parpameter to get the property,
     */
    private String getProperty(String propertyOrAlias) {
        String property = registry.getParameter(prefixWithTableId + TableConstants.ALIAS + propertyOrAlias);
        if (StringUtils.isNotBlank(property)) {
            return property;
        }
        
        return propertyOrAlias;
    }

    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("tableId", tableId);
        return builder.toString();
    }

    protected abstract boolean showPagination();
}
