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
package org.extremecomponents.table.callback;

import java.util.Comparator;
import java.util.Objects;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Override BeanComparator to workaround the NestedNullException thrown by
 * PropertyUtils.getProperty(...) when a parent object in the hierarchy is null.
 * 
 * @author vbala
 * @version 1.0
 */
public class NullSafeBeanComparator<T> extends BeanComparator<T> {
    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(NullSafeBeanComparator.class);

    protected boolean nullsAreHigh = true;

    protected String property;

    protected transient Comparator<T> comparator;

    @Override
    public Comparator<T> getComparator() {
        return comparator;
    }

    public void setComparator(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public boolean isNullsAreHigh() {
        return nullsAreHigh;
    }

    public void setNullsAreHigh(boolean nullsAreHigh) {
        this.nullsAreHigh = nullsAreHigh;
    }

    @Override
    public String getProperty() {
        return property;
    }

    @Override
    public void setProperty(final String property) {
        this.property = property;
    }

    /**
     * Compare beans safely. Handles NestedNullException thrown by PropertyUtils
     * when the parent object is null
     */
    @SuppressWarnings("unchecked")
    @Override
    public int compare(T o1, T o2) {

        if (property == null) {
            // use the object's compare since no property is specified
            return this.comparator.compare(o1, o2);
        }

        T val1 = null;
        T val2 = null;

        try {
            try {
                val1 = (T) PropertyUtils.getProperty(o1, property);
            } catch (NestedNullException ignored) {
                // just leave it null if we can't get the property
            }

            try {
                val2 = (T) PropertyUtils.getProperty(o2, property);
            } catch (NestedNullException ignored) {
                // just leave it null if we can't get the property
            }

            if (val1 == val2) {
                return 0;
            }

            if (val1 == null) {
                return this.nullsAreHigh ? 1 : -1;
            }

            if (val2 == null) {
                return this.nullsAreHigh ? -1 : 1;
            }

            return this.comparator.compare(val1, val2);
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e);
            return 0;
        }
    }

    public NullSafeBeanComparator(String property, Comparator<T> c) {
        this.comparator = c;
        this.property = property;
    }

    public NullSafeBeanComparator(String property, Comparator<T> c, boolean nullAreHigh) {
        this.comparator = c;
        this.property = property;
        this.nullsAreHigh = nullAreHigh;

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(comparator, nullsAreHigh, property);
        return result;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        NullSafeBeanComparator other = (NullSafeBeanComparator) obj;
        return Objects.equals(comparator, other.comparator) && nullsAreHigh == other.nullsAreHigh
                && Objects.equals(property, other.property);
    }
}