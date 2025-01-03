/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.builder;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Assists in implementing {@link Object#equals(Object)} methods.
 *
 * <p>This class provides methods to build a good equals method for any
 * class. It follows rules laid out in
 * <a href="https://www.oracle.com/java/technologies/effectivejava.html">Effective Java</a>
 * , by Joshua Bloch. In particular the rule for comparing {@code doubles},
 * {@code floats}, and arrays can be tricky. Also, making sure that
 * {@code equals()} and {@code hashCode()} are consistent can be
 * difficult.</p>
 *
 * <p>Two Objects that compare as equals must generate the same hash code,
 * but two Objects with the same hash code do not have to be equal.</p>
 *
 * <p>All relevant fields should be included in the calculation of equals.
 * Derived fields may be ignored. In particular, any field used in
 * generating a hash code must be used in the equals method, and vice
 * versa.</p>
 *
 * <p>Typical use for the code is as follows:</p>
 * <pre>
 * public boolean equals(Object obj) {
 *   if (obj == null) { return false; }
 *   if (obj == this) { return true; }
 *   if (obj.getClass() != getClass()) {
 *     return false;
 *   }
 *   MyClass rhs = (MyClass) obj;
 *   return new EqualsBuilder()
 *                 .appendSuper(super.equals(obj))
 *                 .append(field1, rhs.field1)
 *                 .append(field2, rhs.field2)
 *                 .append(field3, rhs.field3)
 *                 .isEquals();
 *  }
 * </pre>
 *
 * <p>Alternatively, there is a method that uses reflection to determine
 * the fields to test. Because these fields are usually private, the method,
 * {@code reflectionEquals}, uses {@code AccessibleObject.setAccessible} to
 * change the visibility of the fields. This will fail under a security
 * manager, unless the appropriate permissions are set up correctly. It is
 * also slower than testing explicitly.  Non-primitive fields are compared using
 * {@code equals()}.</p>
 *
 * <p>A typical invocation for this method would look like:</p>
 * <pre>
 * public boolean equals(Object obj) {
 *   return EqualsBuilder.reflectionEquals(this, obj);
 * }
 * </pre>
 *
 * <p>The {@link EqualsExclude} annotation can be used to exclude fields from being
 * used by the {@code reflectionEquals} methods.</p>
 *
 * @since 1.0
 */
public class EqualsBuilder implements Builder<Boolean> {

    /**
     * A registry of objects used by reflection methods to detect cyclical object references and avoid infinite loops.
     *
     * @since 3.0
     */
    private static final ThreadLocal<Set<Pair<IDKey, IDKey>>> REGISTRY = ThreadLocal.withInitial(HashSet::new);

    /*
     * NOTE: we cannot store the actual objects in a HashSet, as that would use the very hashCode()
     * we are in the process of calculating.
     *
     * So we generate a one-to-one mapping from the original object to a new object.
     *
     * Now HashSet uses equals() to determine if two elements with the same hash code really
     * are equal, so we also need to ensure that the replacement objects are only equal
     * if the original objects are identical.
     *
     * The original implementation (2.4 and before) used the System.identityHashCode()
     * method - however this is not guaranteed to generate unique ids (e.g. LANG-459)
     *
     * We now use the IDKey helper class (adapted from org.apache.axis.utils.IDKey)
     * to disambiguate the duplicate ids.
     */

    /**
     * Converters value pair into a register pair.
     *
     * @param lhs {@code this} object
     * @param rhs the other object
     * @return the pair
     */
    static Pair<IDKey, IDKey> getRegisterPair(final Object lhs, final Object rhs) {
        return Pair.of(new IDKey(lhs), new IDKey(rhs));
    }

    /**
     * Returns the registry of object pairs being traversed by the reflection
     * methods in the current thread.
     *
     * @return Set the registry of objects being traversed
     * @since 3.0
     */
    static Set<Pair<IDKey, IDKey>> getRegistry() {
        return REGISTRY.get();
    }

    /**
     * Returns {@code true} if the registry contains the given object pair.
     * Used by the reflection methods to avoid infinite loops.
     * Objects might be swapped therefore a check is needed if the object pair
     * is registered in given or swapped order.
     *
     * @param lhs {@code this} object to lookup in registry
     * @param rhs the other object to lookup on registry
     * @return boolean {@code true} if the registry contains the given object.
     * @since 3.0
     */
    static boolean isRegistered(final Object lhs, final Object rhs) {
        final Set<Pair<IDKey, IDKey>> registry = getRegistry();
        final Pair<IDKey, IDKey> pair = getRegisterPair(lhs, rhs);
        final Pair<IDKey, IDKey> swappedPair = Pair.of(pair.getRight(), pair.getLeft());
        return registry != null && (registry.contains(pair) || registry.contains(swappedPair));
    }

    /**
     * This method uses reflection to determine if the two {@link Object}s
     * are equal.
     *
     * <p>It uses {@code AccessibleObject.setAccessible} to gain access to private
     * fields. This means that it will throw a security exception if run under
     * a security manager, if the permissions are not set up correctly. It is also
     * not as efficient as testing explicitly. Non-primitive fields are compared using
     * {@code equals()}.</p>
     *
     * <p>If the TestTransients parameter is set to {@code true}, transient
     * members will be tested, otherwise they are ignored, as they are likely
     * derived fields, and not part of the value of the {@link Object}.</p>
     *
     * <p>Static fields will not be tested. Superclass fields will be included.</p>
     *
     * @param lhs  {@code this} object
     * @param rhs  the other object
     * @param testTransients  whether to include transient fields
     * @return {@code true} if the two Objects have tested equals.
     * @see EqualsExclude
     */
    public static boolean reflectionEquals(final Object lhs, final Object rhs, final boolean testTransients) {
        return reflectionEquals(lhs, rhs, testTransients, null);
    }

    /**
     * This method uses reflection to determine if the two {@link Object}s
     * are equal.
     *
     * <p>It uses {@code AccessibleObject.setAccessible} to gain access to private
     * fields. This means that it will throw a security exception if run under
     * a security manager, if the permissions are not set up correctly. It is also
     * not as efficient as testing explicitly. Non-primitive fields are compared using
     * {@code equals()}.</p>
     *
     * <p>If the testTransients parameter is set to {@code true}, transient
     * members will be tested, otherwise they are ignored, as they are likely
     * derived fields, and not part of the value of the {@link Object}.</p>
     *
     * <p>Static fields will not be included. Superclass fields will be appended
     * up to and including the specified superclass. A null superclass is treated
     * as java.lang.Object.</p>
     *
     * <p>If the testRecursive parameter is set to {@code true}, non primitive
     * (and non primitive wrapper) field types will be compared by
     * {@link EqualsBuilder} recursively instead of invoking their
     * {@code equals()} method. Leading to a deep reflection equals test.
     *
     * @param lhs  {@code this} object
     * @param rhs  the other object
     * @param testTransients  whether to include transient fields
     * @param reflectUpToClass  the superclass to reflect up to (inclusive),
     *  may be {@code null}
     * @param testRecursive  whether to call reflection equals on non-primitive
     *  fields recursively.
     * @param excludeFields  array of field names to exclude from testing
     * @return {@code true} if the two Objects have tested equals.
     * @see EqualsExclude
     * @since 3.6
     */
    public static boolean reflectionEquals(final Object lhs, final Object rhs, final boolean testTransients, final Class<?> reflectUpToClass,
            final boolean testRecursive, final String... excludeFields) {
        if (lhs == rhs) {
            return true;
        }
        if (lhs == null || rhs == null) {
            return false;
        }
        // @formatter:off
        return new EqualsBuilder()
            .setExcludeFields(excludeFields)
            .setReflectUpToClass(reflectUpToClass)
            .setTestTransients(testTransients)
            .setTestRecursive(testRecursive)
            .reflectionAppend(lhs, rhs)
            .isEquals();
        // @formatter:on
    }

    /**
     * This method uses reflection to determine if the two {@link Object}s
     * are equal.
     *
     * <p>It uses {@code AccessibleObject.setAccessible} to gain access to private
     * fields. This means that it will throw a security exception if run under
     * a security manager, if the permissions are not set up correctly. It is also
     * not as efficient as testing explicitly. Non-primitive fields are compared using
     * {@code equals()}.</p>
     *
     * <p>If the testTransients parameter is set to {@code true}, transient
     * members will be tested, otherwise they are ignored, as they are likely
     * derived fields, and not part of the value of the {@link Object}.</p>
     *
     * <p>Static fields will not be included. Superclass fields will be appended
     * up to and including the specified superclass. A null superclass is treated
     * as java.lang.Object.</p>
     *
     * @param lhs  {@code this} object
     * @param rhs  the other object
     * @param testTransients  whether to include transient fields
     * @param reflectUpToClass  the superclass to reflect up to (inclusive),
     *  may be {@code null}
     * @param excludeFields  array of field names to exclude from testing
     * @return {@code true} if the two Objects have tested equals.
     * @see EqualsExclude
     * @since 2.0
     */
    public static boolean reflectionEquals(final Object lhs, final Object rhs, final boolean testTransients, final Class<?> reflectUpToClass,
            final String... excludeFields) {
        return reflectionEquals(lhs, rhs, testTransients, reflectUpToClass, false, excludeFields);
    }

    /**
     * This method uses reflection to determine if the two {@link Object}s
     * are equal.
     *
     * <p>It uses {@code AccessibleObject.setAccessible} to gain access to private
     * fields. This means that it will throw a security exception if run under
     * a security manager, if the permissions are not set up correctly. It is also
     * not as efficient as testing explicitly. Non-primitive fields are compared using
     * {@code equals()}.</p>
     *
     * <p>Transient members will be not be tested, as they are likely derived
     * fields, and not part of the value of the Object.</p>
     *
     * <p>Static fields will not be tested. Superclass fields will be included.</p>
     *
     * @param lhs  {@code this} object
     * @param rhs  the other object
     * @param excludeFields  Collection of String field names to exclude from testing
     * @return {@code true} if the two Objects have tested equals.
     * @see EqualsExclude
     */
    public static boolean reflectionEquals(final Object lhs, final Object rhs, final Collection<String> excludeFields) {
        return reflectionEquals(lhs, rhs, ReflectionToStringBuilder.toNoNullStringArray(excludeFields));
    }

    /**
     * This method uses reflection to determine if the two {@link Object}s
     * are equal.
     *
     * <p>It uses {@code AccessibleObject.setAccessible} to gain access to private
     * fields. This means that it will throw a security exception if run under
     * a security manager, if the permissions are not set up correctly. It is also
     * not as efficient as testing explicitly. Non-primitive fields are compared using
     * {@code equals()}.</p>
     *
     * <p>Transient members will be not be tested, as they are likely derived
     * fields, and not part of the value of the Object.</p>
     *
     * <p>Static fields will not be tested. Superclass fields will be included.</p>
     *
     * @param lhs  {@code this} object
     * @param rhs  the other object
     * @param excludeFields  array of field names to exclude from testing
     * @return {@code true} if the two Objects have tested equals.
     * @see EqualsExclude
     */
    public static boolean reflectionEquals(final Object lhs, final Object rhs, final String... excludeFields) {
        return reflectionEquals(lhs, rhs, false, null, excludeFields);
    }

    /**
     * Registers the given object pair.
     * Used by the reflection methods to avoid infinite loops.
     *
     * @param lhs {@code this} object to register
     * @param rhs the other object to register
     */
    private static void register(final Object lhs, final Object rhs) {
        getRegistry().add(getRegisterPair(lhs, rhs));
    }

    /**
     * Unregisters the given object pair.
     *
     * <p>
     * Used by the reflection methods to avoid infinite loops.
     * </p>
     *
     * @param lhs {@code this} object to unregister
     * @param rhs the other object to unregister
     * @since 3.0
     */
    private static void unregister(final Object lhs, final Object rhs) {
        final Set<Pair<IDKey, IDKey>> registry = getRegistry();
        registry.remove(getRegisterPair(lhs, rhs));
        if (registry.isEmpty()) {
            REGISTRY.remove();
        }
    }

    /**
     * If the fields tested are equals.
     * The default value is {@code true}.
     */
    private boolean isEquals = true;

    private boolean testTransients;

    private boolean testRecursive;

    private List<Class<?>> bypassReflectionClasses;

    private Class<?> reflectUpToClass;

    private String[] excludeFields;

    /**
     * Constructor for EqualsBuilder.
     *
     * <p>Starts off assuming that equals is {@code true}.</p>
     * @see Object#equals(Object)
     */
    public EqualsBuilder() {
        // set up default classes to bypass reflection for
        bypassReflectionClasses = new ArrayList<>(1);
        bypassReflectionClasses.add(String.class); //hashCode field being lazy but not transient
    }

    /**
     * Test if two {@code booleans}s are equal.
     *
     * @param lhs  the left-hand side {@code boolean}
     * @param rhs  the right-hand side {@code boolean}
     * @return {@code this} instance.
      */
    public EqualsBuilder append(final boolean lhs, final boolean rhs) {
        if (!isEquals) {
            return this;
        }
        isEquals = lhs == rhs;
        return this;
    }

    /**
     * Deep comparison of array of {@code boolean}. Length and all
     * values are compared.
     *
     * <p>The method {@link #append(boolean, boolean)} is used.</p>
     *
     * @param lhs  the left-hand side {@code boolean[]}
     * @param rhs  the right-hand side {@code boolean[]}
     * @return {@code this} instance.
     */
    public EqualsBuilder append(final boolean[] lhs, final boolean[] rhs) {
        if (!isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            setEquals(false);
            return this;
        }
        if (lhs.length != rhs.length) {
            setEquals(false);
            return this;
        }
        for (int i = 0; i < lhs.length && isEquals; ++i) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    /**
     * Test if two {@code byte}s are equal.
     *
     * @param lhs  the left-hand side {@code byte}
     * @param rhs  the right-hand side {@code byte}
     * @return {@code this} instance.
     */
    public EqualsBuilder append(final byte lhs, final byte rhs) {
        if (isEquals) {
            isEquals = lhs == rhs;
        }
        return this;
    }

    /**
     * Deep comparison of array of {@code byte}. Length and all
     * values are compared.
     *
     * <p>The method {@link #append(byte, byte)} is used.</p>
     *
     * @param lhs  the left-hand side {@code byte[]}
     * @param rhs  the right-hand side {@code byte[]}
     * @return {@code this} instance.
     */
    public EqualsBuilder append(final byte[] lhs, final byte[] rhs) {
        if (!isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            setEquals(false);
            return this;
        }
        if (lhs.length != rhs.length) {
            setEquals(false);
            return this;
        }
        for (int i = 0; i < lhs.length && isEquals; ++i) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    /**
     * Test if two {@code char}s are equal.
     *
     * @param lhs  the left-hand side {@code char}
     * @param rhs  the right-hand side {@code char}
     * @return {@code this} instance.
     */
    public EqualsBuilder append(final char lhs, final char rhs) {
        if (isEquals) {
            isEquals = lhs == rhs;
        }
        return this;
    }

    /**
     * Deep comparison of array of {@code char}. Length and all
     * values are compared.
     *
     * <p>The method {@link #append(char, char)} is used.</p>
     *
     * @param lhs  the left-hand side {@code char[]}
     * @param rhs  the right-hand side {@code char[]}
     * @return {@code this} instance.
     */
    public EqualsBuilder append(final char[] lhs, final char[] rhs) {
        if (!isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            setEquals(false);
            return this;
        }
        if (lhs.length != rhs.length) {
            setEquals(false);
            return this;
        }
        for (int i = 0; i < lhs.length && isEquals; ++i) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    /**
     * Test if two {@code double}s are equal by testing that the
     * pattern of bits returned by {@code doubleToLong} are equal.
     *
     * <p>This handles NaNs, Infinities, and {@code -0.0}.</p>
     *
     * <p>It is compatible with the hash code generated by
     * {@link HashCodeBuilder}.</p>
     *
     * @param lhs  the left-hand side {@code double}
     * @param rhs  the right-hand side {@code double}
     * @return {@code this} instance.
     */
    public EqualsBuilder append(final double lhs, final double rhs) {
        if (isEquals) {
            return append(Double.doubleToLongBits(lhs), Double.doubleToLongBits(rhs));
        }
        return this;
    }

    /**
     * Deep comparison of array of {@code double}. Length and all
     * values are compared.
     *
     * <p>The method {@link #append(double, double)} is used.</p>
     *
     * @param lhs  the left-hand side {@code double[]}
     * @param rhs  the right-hand side {@code double[]}
     * @return {@code this} instance.
     */
    public EqualsBuilder append(final double[] lhs, final double[] rhs) {
        if (!isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            setEquals(false);
            return this;
        }
        if (lhs.length != rhs.length) {
            setEquals(false);
            return this;
        }
        for (int i = 0; i < lhs.length && isEquals; ++i) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    /**
     * Test if two {@code float}s are equal by testing that the
     * pattern of bits returned by doubleToLong are equal.
     *
     * <p>This handles NaNs, Infinities, and {@code -0.0}.</p>
     *
     * <p>It is compatible with the hash code generated by
     * {@link HashCodeBuilder}.</p>
     *
     * @param lhs  the left-hand side {@code float}
     * @param rhs  the right-hand side {@code float}
     * @return {@code this} instance.
     */
    public EqualsBuilder append(final float lhs, final float rhs) {
        if (isEquals) {
            return append(Float.floatToIntBits(lhs), Float.floatToIntBits(rhs));
        }
        return this;
    }

    /**
     * Deep comparison of array of {@code float}. Length and all
     * values are compared.
     *
     * <p>The method {@link #append(float, float)} is used.</p>
     *
     * @param lhs  the left-hand side {@code float[]}
     * @param rhs  the right-hand side {@code float[]}
     * @return {@code this} instance.
     */
    public EqualsBuilder append(final float[] lhs, final float[] rhs) {
        if (!isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            setEquals(false);
            return this;
        }
        if (lhs.length != rhs.length) {
            setEquals(false);
            return this;
        }
        for (int i = 0; i < lhs.length && isEquals; ++i) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    /**
     * Test if two {@code int}s are equal.
     *
     * @param lhs  the left-hand side {@code int}
     * @param rhs  the right-hand side {@code int}
     * @return {@code this} instance.
     */
    public EqualsBuilder append(final int lhs, final int rhs) {
        if (isEquals) {
            isEquals = lhs == rhs;
        }
        return this;
    }

    /**
     * Deep comparison of array of {@code int}. Length and all
     * values are compared.
     *
     * <p>The method {@link #append(int, int)} is used.</p>
     *
     * @param lhs  the left-hand side {@code int[]}
     * @param rhs  the right-hand side {@code int[]}
     * @return {@code this} instance.
     */
    public EqualsBuilder append(final int[] lhs, final int[] rhs) {
        if (!isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            setEquals(false);
            return this;
        }
        if (lhs.length != rhs.length) {
            setEquals(false);
            return this;
        }
        for (int i = 0; i < lhs.length && isEquals; ++i) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    /**
     * Test if two {@code long}s are equal.
     *
     * @param lhs
     *                  the left-hand side {@code long}
     * @param rhs
     *                  the right-hand side {@code long}
     * @return {@code this} instance.
     */
    public EqualsBuilder append(final long lhs, final long rhs) {
        if (isEquals) {
            isEquals = lhs == rhs;
        }
        return this;
    }

    /**
     * Deep comparison of array of {@code long}. Length and all
     * values are compared.
     *
     * <p>The method {@link #append(long, long)} is used.</p>
     *
     * @param lhs  the left-hand side {@code long[]}
     * @param rhs  the right-hand side {@code long[]}
     * @return {@code this} instance.
     */
    public EqualsBuilder append(final long[] lhs, final long[] rhs) {
        if (!isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            setEquals(false);
            return this;
        }
        if (lhs.length != rhs.length) {
            setEquals(false);
            return this;
        }
        for (int i = 0; i < lhs.length && isEquals; ++i) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    /**
     * Test if two {@link Object}s are equal using either
     * #{@link #reflectionAppend(Object, Object)}, if object are non
     * primitives (or wrapper of primitives) or if field {@code testRecursive}
     * is set to {@code false}. Otherwise, using their
     * {@code equals} method.
     *
     * @param lhs  the left-hand side object
     * @param rhs  the right-hand side object
     * @return {@code this} instance.
     */
    public EqualsBuilder append(final Object lhs, final Object rhs) {
        if (!isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            setEquals(false);
            return this;
        }
        final Class<?> lhsClass = lhs.getClass();
        if (lhsClass.isArray()) {
            // factor out array case in order to keep method small enough
            // to be inlined
            appendArray(lhs, rhs);
        } else // The simple case, not an array, just test the element
        if (testRecursive && !ClassUtils.isPrimitiveOrWrapper(lhsClass)) {
            reflectionAppend(lhs, rhs);
        } else {
            isEquals = lhs.equals(rhs);
        }
        return this;
    }

    /**
     * Performs a deep comparison of two {@link Object} arrays.
     *
     * <p>This also will be called for the top level of
     * multi-dimensional, ragged, and multi-typed arrays.</p>
     *
     * <p>Note that this method does not compare the type of the arrays; it only
     * compares the contents.</p>
     *
     * @param lhs  the left-hand side {@code Object[]}
     * @param rhs  the right-hand side {@code Object[]}
     * @return {@code this} instance.
     */
    public EqualsBuilder append(final Object[] lhs, final Object[] rhs) {
        if (!isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            setEquals(false);
            return this;
        }
        if (lhs.length != rhs.length) {
            setEquals(false);
            return this;
        }
        for (int i = 0; i < lhs.length && isEquals; ++i) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    /**
     * Test if two {@code short}s are equal.
     *
     * @param lhs  the left-hand side {@code short}
     * @param rhs  the right-hand side {@code short}
     * @return {@code this} instance.
     */
    public EqualsBuilder append(final short lhs, final short rhs) {
        if (isEquals) {
            isEquals = lhs == rhs;
        }
        return this;
    }

    /**
     * Deep comparison of array of {@code short}. Length and all
     * values are compared.
     *
     * <p>The method {@link #append(short, short)} is used.</p>
     *
     * @param lhs  the left-hand side {@code short[]}
     * @param rhs  the right-hand side {@code short[]}
     * @return {@code this} instance.
     */
    public EqualsBuilder append(final short[] lhs, final short[] rhs) {
        if (!isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            setEquals(false);
            return this;
        }
        if (lhs.length != rhs.length) {
            setEquals(false);
            return this;
        }
        for (int i = 0; i < lhs.length && isEquals; ++i) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    /**
     * Test if an {@link Object} is equal to an array.
     *
     * @param lhs  the left-hand side object, an array
     * @param rhs  the right-hand side object
     */
    private void appendArray(final Object lhs, final Object rhs) {
        // First we compare different dimensions, for example: a boolean[][] to a boolean[]
        // then we 'Switch' on type of array, to dispatch to the correct handler
        // This handles multidimensional arrays of the same depth
        if (lhs.getClass() != rhs.getClass()) {
            setEquals(false);
        } else if (lhs instanceof long[]) {
            append((long[]) lhs, (long[]) rhs);
        } else if (lhs instanceof int[]) {
            append((int[]) lhs, (int[]) rhs);
        } else if (lhs instanceof short[]) {
            append((short[]) lhs, (short[]) rhs);
        } else if (lhs instanceof char[]) {
            append((char[]) lhs, (char[]) rhs);
        } else if (lhs instanceof byte[]) {
            append((byte[]) lhs, (byte[]) rhs);
        } else if (lhs instanceof double[]) {
            append((double[]) lhs, (double[]) rhs);
        } else if (lhs instanceof float[]) {
            append((float[]) lhs, (float[]) rhs);
        } else if (lhs instanceof boolean[]) {
            append((boolean[]) lhs, (boolean[]) rhs);
        } else {
            // Not an array of primitives
            append((Object[]) lhs, (Object[]) rhs);
        }
    }

    /**
     * Adds the result of {@code super.equals()} to this builder.
     *
     * @param superEquals  the result of calling {@code super.equals()}
     * @return {@code this} instance.
     * @since 2.0
     */
    public EqualsBuilder appendSuper(final boolean superEquals) {
        if (!isEquals) {
            return this;
        }
        isEquals = superEquals;
        return this;
    }

    /**
     * Returns {@code true} if the fields that have been checked
     * are all equal.
     *
     * @return {@code true} if all of the fields that have been checked
     *         are equal, {@code false} otherwise.
     *
     * @since 3.0
     */
    @Override
    public Boolean build() {
        return Boolean.valueOf(isEquals());
    }

    /**
     * Returns {@code true} if the fields that have been checked
     * are all equal.
     *
     * @return boolean
     */
    public boolean isEquals() {
        return isEquals;
    }

    /**
     * Tests if two {@code objects} by using reflection.
     *
     * <p>It uses {@code AccessibleObject.setAccessible} to gain access to private
     * fields. This means that it will throw a security exception if run under
     * a security manager, if the permissions are not set up correctly. It is also
     * not as efficient as testing explicitly. Non-primitive fields are compared using
     * {@code equals()}.</p>
     *
     * <p>If the testTransients field is set to {@code true}, transient
     * members will be tested, otherwise they are ignored, as they are likely
     * derived fields, and not part of the value of the {@link Object}.</p>
     *
     * <p>Static fields will not be included. Superclass fields will be appended
     * up to and including the specified superclass in field {@code reflectUpToClass}.
     * A null superclass is treated as java.lang.Object.</p>
     *
     * <p>Field names listed in field {@code excludeFields} will be ignored.</p>
     *
     * <p>If either class of the compared objects is contained in
     * {@code bypassReflectionClasses}, both objects are compared by calling
     * the equals method of the left-hand side object with the right-hand side object as an argument.</p>
     *
     * @param lhs  the left-hand side object
     * @param rhs  the right-hand side object
     * @return {@code this} instance.
     */
    public EqualsBuilder reflectionAppend(final Object lhs, final Object rhs) {
        if (!isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            isEquals = false;
            return this;
        }

        // Find the leaf class since there may be transients in the leaf
        // class or in classes between the leaf and root.
        // If we are not testing transients or a subclass has no ivars,
        // then a subclass can test equals to a superclass.
        final Class<?> lhsClass = lhs.getClass();
        final Class<?> rhsClass = rhs.getClass();
        Class<?> testClass;
        if (lhsClass.isInstance(rhs)) {
            testClass = lhsClass;
            if (!rhsClass.isInstance(lhs)) {
                // rhsClass is a subclass of lhsClass
                testClass = rhsClass;
            }
        } else if (rhsClass.isInstance(lhs)) {
            testClass = rhsClass;
            if (!lhsClass.isInstance(rhs)) {
                // lhsClass is a subclass of rhsClass
                testClass = lhsClass;
            }
        } else {
            // The two classes are not related.
            isEquals = false;
            return this;
        }

        try {
            if (testClass.isArray()) {
                append(lhs, rhs);
            } else //If either class is being excluded, call normal object equals method on lhsClass.
            if (bypassReflectionClasses != null
                    && (bypassReflectionClasses.contains(lhsClass) || bypassReflectionClasses.contains(rhsClass))) {
                isEquals = lhs.equals(rhs);
            } else {
                reflectionAppend(lhs, rhs, testClass);
                while (testClass.getSuperclass() != null && testClass != reflectUpToClass) {
                    testClass = testClass.getSuperclass();
                    reflectionAppend(lhs, rhs, testClass);
                }
            }
        } catch (final IllegalArgumentException e) {
            // In this case, we tried to test a subclass vs. a superclass and
            // the subclass has ivars or the ivars are transient and
            // we are testing transients.
            // If a subclass has ivars that we are trying to test them, we get an
            // exception and we know that the objects are not equal.
            isEquals = false;
        }
        return this;
    }

    /**
     * Appends the fields and values defined by the given object of the
     * given Class.
     *
     * @param lhs  the left-hand side object
     * @param rhs  the right-hand side object
     * @param clazz  the class to append details of
     */
    private void reflectionAppend(
        final Object lhs,
        final Object rhs,
        final Class<?> clazz) {

        if (isRegistered(lhs, rhs)) {
            return;
        }

        try {
            register(lhs, rhs);
            final Field[] fields = clazz.getDeclaredFields();
            AccessibleObject.setAccessible(fields, true);
            for (int i = 0; i < fields.length && isEquals; i++) {
                final Field field = fields[i];
                if (!ArrayUtils.contains(excludeFields, field.getName())
                    && !field.getName().contains("$")
                    && (testTransients || !Modifier.isTransient(field.getModifiers()))
                    && !Modifier.isStatic(field.getModifiers())
                    && !field.isAnnotationPresent(EqualsExclude.class)) {
                    append(Reflection.getUnchecked(field, lhs), Reflection.getUnchecked(field, rhs));
                }
            }
        } finally {
            unregister(lhs, rhs);
        }
    }

    /**
     * Reset the EqualsBuilder so you can use the same object again.
     *
     * @since 2.5
     */
    public void reset() {
        isEquals = true;
    }

    /**
     * Sets {@link Class}es whose instances should be compared by calling their {@code equals}
     * although being in recursive mode. So the fields of these classes will not be compared recursively by reflection.
     *
     * <p>Here you should name classes having non-transient fields which are cache fields being set lazily.<br>
     * Prominent example being {@link String} class with its hash code cache field. Due to the importance
     * of the {@link String} class, it is included in the default bypasses classes. Usually, if you use
     * your own set of classes here, remember to include {@link String} class, too.</p>
     *
     * @param bypassReflectionClasses  classes to bypass reflection test
     * @return {@code this} instance.
     * @see #setTestRecursive(boolean)
     * @since 3.8
     */
    public EqualsBuilder setBypassReflectionClasses(final List<Class<?>> bypassReflectionClasses) {
        this.bypassReflectionClasses = bypassReflectionClasses;
        return this;
    }

    /**
     * Sets the {@code isEquals} value.
     *
     * @param isEquals The value to set.
     * @since 2.1
     */
    protected void setEquals(final boolean isEquals) {
        this.isEquals = isEquals;
    }

    /**
     * Sets field names to be excluded by reflection tests.
     *
     * @param excludeFields the fields to exclude
     * @return {@code this} instance.
     * @since 3.6
     */
    public EqualsBuilder setExcludeFields(final String... excludeFields) {
        this.excludeFields = excludeFields;
        return this;
    }

    /**
     * Sets the superclass to reflect up to at reflective tests.
     *
     * @param reflectUpToClass the super class to reflect up to
     * @return {@code this} instance.
     * @since 3.6
     */
    public EqualsBuilder setReflectUpToClass(final Class<?> reflectUpToClass) {
        this.reflectUpToClass = reflectUpToClass;
        return this;
    }

    /**
     * Sets whether to test fields recursively, instead of using their equals method, when reflectively comparing objects.
     * String objects, which cache a hash value, are automatically excluded from recursive testing.
     * You may specify other exceptions by calling {@link #setBypassReflectionClasses(List)}.
     *
     * @param testRecursive whether to do a recursive test
     * @return {@code this} instance.
     * @see #setBypassReflectionClasses(List)
     * @since 3.6
     */
    public EqualsBuilder setTestRecursive(final boolean testRecursive) {
        this.testRecursive = testRecursive;
        return this;
    }

    /**
     * Sets whether to include transient fields when reflectively comparing objects.
     *
     * @param testTransients whether to test transient fields
     * @return {@code this} instance.
     * @since 3.6
     */
    public EqualsBuilder setTestTransients(final boolean testTransients) {
        this.testTransients = testTransients;
        return this;
    }
}
