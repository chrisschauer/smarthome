/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.config.core.validation.internal;

import java.math.BigDecimal;
import java.util.Map;

import org.eclipse.smarthome.config.core.ConfigDescriptionParameter.Type;

import com.google.common.collect.ImmutableMap;

/**
 * The {@link TypeIntrospections} provides a corresponding {@link TypeIntrospection} for each config description
 * parameter type.
 *
 * @author Thomas Höfer - Initial contribution
 */
final class TypeIntrospections {

    private static final Map<Type, TypeIntrospection> introspections = new ImmutableMap.Builder<Type, TypeIntrospection>()
            .put(Type.BOOLEAN, new BooleanIntrospection()).put(Type.TEXT, new StringIntrospection())
            .put(Type.INTEGER, new IntegerIntrospection()).put(Type.DECIMAL, new FloatIntrospection()).build();

    private TypeIntrospections() {
        super();
    }

    /**
     * Returns the corresponding {@link TypeIntrospection} for the given type.
     *
     * @param type the type for which the {@link TypeIntrospection} is to be returned
     *
     * @return the {@link TypeIntrospection} for the given type
     *
     * @throws IllegalArgumentException if no {@link TypeIntrospection} was found for the given type
     */
    static TypeIntrospection get(Type type) {
        TypeIntrospection typeIntrospection = introspections.get(type);
        if (typeIntrospection == null) {
            throw new IllegalArgumentException("There is no type introspection for type " + type);
        }
        return typeIntrospection;
    }

    /**
     * The {@link TypeIntrospection} provides operations to introspect the actual value for a configuration description
     * parameter.
     */
    static abstract class TypeIntrospection {

        private final Class<?> clazz;
        private final MessageKey minViolationMessageKey;
        private final MessageKey maxViolationMessageKey;

        private TypeIntrospection(Class<?> clazz) {
            this(clazz, null, null);
        }

        private TypeIntrospection(Class<?> clazz, MessageKey minViolationMessageKey,
                MessageKey maxViolationMessageKey) {
            this.clazz = clazz;
            this.minViolationMessageKey = minViolationMessageKey;
            this.maxViolationMessageKey = maxViolationMessageKey;
        }

        /**
         * Returns true, if the given value is less than the given min attribute, otherwise false.
         *
         * @param value the corresponding value
         * @param min the value of the min attribute
         *
         * @return true, if the given value is less than the given min attribute, otherwise false
         */
        abstract boolean isMinViolated(Object value, int min);

        /**
         * Returns true, if the given value is greater than the given max attribute, otherwise false.
         *
         * @param value the corresponding value
         * @param max the value of the max attribute
         *
         * @return true, if the given value is greater than the given max attribute, otherwise false
         */
        abstract boolean isMaxViolated(Object value, int max);

        /**
         * Returns true, if the given value can be assigned to the type of this introspection, otherwise false.
         *
         * @param value the corresponding value
         *
         * @return true, if the given value can be assigned to the type of this introspection, otherwise false
         */
        boolean isAssignable(Object value) {
            return clazz.isAssignableFrom(value.getClass());
        }

        /**
         * Returns true, if the given value is a big decimal, otherwise false.
         *
         * @param value the value to be analyzed
         *
         * @return true, if the given value is a big decimal, otherwise false
         */
        final boolean isBigDecimalInstance(Object value) {
            return value instanceof BigDecimal;
        }

        /**
         * Returns the corresponding {@link MessageKey} for the min attribute violation.
         *
         * @return the corresponding {@link MessageKey} for the min attribute violation
         */
        final MessageKey getMinViolationMessageKey() {
            return minViolationMessageKey;
        }

        /**
         * Returns the corresponding {@link MessageKey} for the max attribute violation.
         *
         * @return the corresponding {@link MessageKey} for the max attribute violation
         */
        final MessageKey getMaxViolationMessageKey() {
            return maxViolationMessageKey;
        }
    }

    private static final class BooleanIntrospection extends TypeIntrospection {

        private BooleanIntrospection() {
            super(Boolean.class);
        }

        @Override
        boolean isMinViolated(Object value, int min) {
            throw new UnsupportedOperationException("Min attribute not supported for boolean parameter.");
        }

        @Override
        boolean isMaxViolated(Object value, int max) {
            throw new UnsupportedOperationException("Max attribute not supported for boolean parameter.");
        }
    }

    private static final class FloatIntrospection extends TypeIntrospection {

        private FloatIntrospection() {
            super(Float.class, MessageKey.MIN_VALUE_NUMERIC_VIOLATED, MessageKey.MAX_VALUE_NUMERIC_VIOLATED);
        }

        @Override
        boolean isAssignable(Object value) {
            if (!super.isAssignable(value)) {
                return isBigDecimalInstance(value);
            }
            return true;
        }

        @Override
        boolean isMinViolated(Object value, int min) {
            if (isBigDecimalInstance(value)) {
                return Float.compare(((BigDecimal) value).floatValue(), min) < 0;
            }
            return Float.compare((float) value, min) < 0;
        }

        @Override
        boolean isMaxViolated(Object value, int max) {
            if (isBigDecimalInstance(value)) {
                return Float.compare(((BigDecimal) value).floatValue(), max) > 0;
            }
            return Float.compare((float) value, max) > 0;
        }

    }

    private static final class IntegerIntrospection extends TypeIntrospection {

        private IntegerIntrospection() {
            super(Integer.class, MessageKey.MIN_VALUE_NUMERIC_VIOLATED, MessageKey.MAX_VALUE_NUMERIC_VIOLATED);
        }

        @Override
        boolean isAssignable(Object value) {
            if (!super.isAssignable(value)) {
                return isBigDecimalInstance(value);
            }
            return true;
        }

        @Override
        boolean isMinViolated(Object value, int min) {
            if (isBigDecimalInstance(value)) {
                return ((BigDecimal) value).intValueExact() < min;
            }
            return (int) value < min;
        }

        @Override
        boolean isMaxViolated(Object value, int max) {
            if (isBigDecimalInstance(value)) {
                return ((BigDecimal) value).intValueExact() > max;
            }
            return (int) value > max;
        }
    }

    private static final class StringIntrospection extends TypeIntrospection {

        private StringIntrospection() {
            super(String.class, MessageKey.MIN_VALUE_TXT_VIOLATED, MessageKey.MAX_VALUE_TXT_VIOLATED);
        }

        @Override
        boolean isMinViolated(Object value, int min) {
            return ((String) value).length() < min;
        }

        @Override
        boolean isMaxViolated(Object value, int max) {
            return ((String) value).length() > max;
        }
    }
}
