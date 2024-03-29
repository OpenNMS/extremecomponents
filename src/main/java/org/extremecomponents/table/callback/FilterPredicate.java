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
package org.extremecomponents.table.callback;

import java.util.Iterator;
import java.util.Locale;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.extremecomponents.table.bean.Column;
import org.extremecomponents.table.core.TableModel;
import org.extremecomponents.util.ExtremeUtils;

/**
 * Use the Jakarta Collections predicate pattern to filter out the table.
 * 
 * @author Jeff Johnston
 */
public final class FilterPredicate implements Predicate<TableModel> {
    private static Log logger = LogFactory.getLog(FilterPredicate.class);
    private TableModel model;

    public FilterPredicate(TableModel model) {
        this.model = model;
    }

    /**
     * Use the filter parameters to filter out the table.
     */
    public boolean evaluate(TableModel bean) {
        boolean match = false;

        try {
            for (Iterator iter = model.getColumnHandler().getColumns().iterator(); iter.hasNext();) {
                Column column = (Column) iter.next();
                String alias = column.getAlias();
                String filterValue = model.getLimit().getFilterSet().getFilterValue(alias);

                if (StringUtils.isEmpty(filterValue)) {
                    continue;
                }

                String property = column.getProperty();
                Object value = PropertyUtils.getProperty(bean, property);

                if (value == null) {
                    continue;
                }

                if (column.isDate()) {
                    Locale locale = model.getLocale();
                    value = ExtremeUtils.formatDate(column.getParse(), column.getFormat(), value, locale);
                } else if (column.isCurrency()) {
                    Locale locale = model.getLocale();
                    value = ExtremeUtils.formatNumber(column.getFormat(), value, locale);
                }

                if (!isSearchMatch(value.toString(), filterValue)) {
                    match = false; // as soon as fail just short circuit

                    break;
                }

                match = true;
            }
        } catch (Exception e) {
            logger.error("FilterPredicate.evaluate() had problems", e);
        }

        return match;
    }

    private boolean isSearchMatch(String value, String search) {
        value = value.toLowerCase().trim();
        search = search.toLowerCase().trim();

        if (search.startsWith("*") && value.endsWith(StringUtils.replace(search, "*", ""))) {
            return true;
        } else if (search.endsWith("*") && value.startsWith(StringUtils.replace(search, "*", ""))) {
            return true;
        } else if (StringUtils.contains(value, search)) {
            return true;
        }

        return false;
    }
}
