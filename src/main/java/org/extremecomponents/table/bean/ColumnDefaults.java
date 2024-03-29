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
package org.extremecomponents.table.bean;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.extremecomponents.table.core.RetrievalUtils;
import org.extremecomponents.table.core.MessagesConstants;
import org.extremecomponents.table.core.PreferencesConstants;
import org.extremecomponents.table.core.TableConstants;
import org.extremecomponents.table.core.TableModel;
import org.extremecomponents.table.core.TableModelUtils;
import org.extremecomponents.util.ExtremeUtils;

/**
 * Pull all the default values for the ColumnTag. Because the default values
 * could be coming from the properties or resource bundle this class will
 * abstract that out.
 * 
 * @author Jeff Johnston
 */
final class ColumnDefaults {
    private static Log logger = LogFactory.getLog(ColumnDefaults.class);

    private ColumnDefaults() {
    }

    static String getCell(TableModel model, String cell) {
        String result;

        if (StringUtils.isNotBlank(cell)) {
            result = model.getPreferences().getPreference(PreferencesConstants.COLUMN_CELL + cell);
            if (StringUtils.isBlank(result)) {
                result = cell;
            }
        } else {
            result = model.getPreferences().getPreference(PreferencesConstants.COLUMN_CELL + TableConstants.CELL_DISPLAY);
        }

        return result;
    }

    static String getFilterCell(TableModel model, String filterCell) {
        String result;

        if (StringUtils.isNotBlank(filterCell)) {
            result = model.getPreferences().getPreference(PreferencesConstants.COLUMN_FILTER_CELL + filterCell);
            if (StringUtils.isBlank(result)) {
                result = filterCell;
            }
        } else {
            result = model.getPreferences().getPreference(PreferencesConstants.COLUMN_FILTER_CELL + TableConstants.CELL_FILTER);
        }

        return result;
    }

    static String getHeaderCell(TableModel model, String headerCell) {
        String result;

        if (StringUtils.isNotBlank(headerCell)) {
            result = model.getPreferences().getPreference(PreferencesConstants.COLUMN_HEADER_CELL + headerCell);
            if (StringUtils.isBlank(result)) {
                result = headerCell;
            }
        } else {
            result = model.getPreferences().getPreference(PreferencesConstants.COLUMN_HEADER_CELL + TableConstants.CELL_HEADER);
        }

        return result;
    }

    static String getParse(TableModel model, Column column, String parse) {
        if (StringUtils.isNotBlank(parse)) {
            return parse;
        }

        if (column.isDate()) {
            return model.getPreferences().getPreference(PreferencesConstants.COLUMN_PARSE + TableConstants.DATE);
        }

        return parse;
    }

    /**
     * If this is a named format then it should be in the resource bundle. For
     * backwards compatibility check the properties file also.
     */
    static String getFormat(TableModel model, Column column, String format) {
        String result = getFormatInResourceBundle(model, column, format);
        if (StringUtils.isBlank(result)) {
            result = getFormatInProperties(model, column, format);
        }

        if (StringUtils.isNotBlank(result)) {
            return result;
        }

        return format;
    }

    static String getFormatInResourceBundle(TableModel model, Column column, String format) {
        if (StringUtils.isNotBlank(format) && isNamedFormat(format)) {
            return model.getMessages().getMessage(MessagesConstants.COLUMN_FORMAT + format);
        }

        if (StringUtils.isBlank(format)) {
            if (column.isCurrency()) {
                return model.getMessages().getMessage(MessagesConstants.COLUMN_FORMAT + TableConstants.CURRENCY);
            } else if (column.isDate()) {
                return model.getMessages().getMessage(MessagesConstants.COLUMN_FORMAT + TableConstants.DATE);
            }
        }

        return null;
    }

    static String getFormatInProperties(TableModel model, Column column, String format) {
        if (StringUtils.isNotBlank(format) && isNamedFormat(format)) {
            return model.getPreferences().getPreference(PreferencesConstants.COLUMN_FORMAT + format);
        }

        if (StringUtils.isBlank(format)) {
            if (column.isCurrency()) {
                return model.getPreferences().getPreference(PreferencesConstants.COLUMN_FORMAT + TableConstants.CURRENCY);
            } else if (column.isDate()) {
                return model.getPreferences().getPreference(PreferencesConstants.COLUMN_FORMAT + TableConstants.DATE);
            }
        }

        return null;
    }

    /**
     * If the format contains any of these formats then it is custom format
     * doing inline.
     */
    static boolean isNamedFormat(String format) {
        char[] args = { '#', '/', '-' };
        return StringUtils.containsNone(format, args);
    }

    static Boolean isSortable(TableModel model, Boolean sortable) {
        if (sortable == null) {
            return model.getTableHandler().getTable().isSortable();
        }

        return sortable;
    }

    static Boolean isFilterable(TableModel model, Boolean filterable) {
        if (filterable == null) {
            return model.getTableHandler().getTable().isFilterable();
        }

        return filterable;
    }

    static String getTitle(TableModel model, String title, String property) {
        if (StringUtils.isEmpty(title)) {
            return ExtremeUtils.camelCaseToWord(property);
        }

        if (TableModelUtils.isResourceBundleProperty(title)) {
            String resourceValue = model.getMessages().getMessage(title);
            if (resourceValue != null) {
                return resourceValue;
            }
        }

        return title;
    }

    static String getHeaderClass(TableModel model, String headerClass) {
        if (StringUtils.isBlank(headerClass)) {
            return model.getPreferences().getPreference(PreferencesConstants.TABLE_HEADER_CLASS);
        }

        return headerClass;
    }

    static String getAlias(String alias, String property) {
        if (StringUtils.isBlank(alias) && StringUtils.isNotBlank(property)) {
            return property;
        }

        return alias;
    }

    public static String[] getCalcTitle(TableModel model, String[] calcTitle) {
        List<String> results = new ArrayList<>();
        
        if (calcTitle == null) {
            return new String[]{};
        }
        
        for (int i = 0; i < calcTitle.length; i++) {
            String title = calcTitle[i];
            
            if (TableModelUtils.isResourceBundleProperty(title)) {
                String resourceValue = model.getMessages().getMessage(title);
                if (resourceValue == null) {
                    resourceValue = title;
                }
                
                if (StringUtils.isNotBlank(resourceValue)) {
                    results.add(resourceValue);
                }
            } else {
                results.add(title);
            }
        }
        
        return results.toArray(new String[results.size()]);
    }
    
    static Boolean isEscapeAutoFormat(TableModel model, Boolean escapeAutoFormat) {
        if (escapeAutoFormat == null) {
            return Boolean.valueOf(model.getPreferences().getPreference(PreferencesConstants.COLUMN_ESCAPE_AUTO_FORMAT));
        }

        return escapeAutoFormat;
    }
    
    static Object getFilterOptions(TableModel model, Object filterOptions) {
        if (model == null) return null;

        try {
            if (filterOptions != null) {
                return RetrievalUtils.retrieveCollection(model.getContext(), filterOptions);
            }
        } catch (Exception e) {
            logger.warn("unable to retrieve collection for context " + model.getContext(), e);
        }
        
        return null;
    }
}
