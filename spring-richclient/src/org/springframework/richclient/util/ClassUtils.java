/*
 * Copyright 2002-2004 the original author or authors.
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
package org.springframework.richclient.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.enums.CodedEnum;
import org.springframework.util.ArrayUtils;

/**
 * Misc static utility functions for java classes.
 *
 * @author Kerth Donald
 */
public class ClassUtils {
    private static final Log logger = LogFactory.getLog(ClassUtils.class);
    private static Set simpleClasses = new HashSet();
    static {
        simpleClasses.add(String.class);
        simpleClasses.add(Integer.class);
        simpleClasses.add(Float.class);
        simpleClasses.add(Double.class);
        simpleClasses.add(Long.class);
        simpleClasses.add(Short.class);
        simpleClasses.add(Byte.class);
        simpleClasses.add(BigInteger.class);
        simpleClasses.add(Date.class);
        simpleClasses.add(Class.class);
        simpleClasses.add(Boolean.class);
        simpleClasses.add(Timestamp.class);
        simpleClasses.add(Calendar.class);
        simpleClasses.add(URL.class);
        simpleClasses.add(InetAddress.class);
    }

    private ClassUtils() {
    }

    /**
     * Intializes the specified class if not initialized already.
     *
     * This is required for EnumUtils if the enum class has not yet been
     * loaded.
     */
    public static void initializeClass(Class clazz) {
        try {
            Class.forName(clazz.getName(), true, Thread.currentThread()
                    .getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the qualified class field name with the specified value. For
     * example, with a class defined with a static field "NORMAL" with value =
     * "0", passing in "0" would return: className.NORMAL.
     *
     * @return The qualified field.
     */
    public static String getClassFieldNameWithValue(Class clazz, Object value) {
        Field[] fields = clazz.getFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            try {
                Object constant = field.get(null);
                if (value.equals(constant)) {
                    return clazz.getName() + "." + field.getName();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Gets the field value for the specified qualified field name.
     */
    public static Object getFieldValue(String qualifiedFieldName) {
        Class clazz;
        try {
            clazz = classForName(ClassUtils.qualifier(qualifiedFieldName));
        } catch (ClassNotFoundException cnfe) {
            return null;
        }
        try {
            return clazz.getField(ClassUtils.unqualify(qualifiedFieldName))
                    .get(null);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Load the class with the specified name.
     *
     * @param name
     * @return The loaded class.
     * @throws ClassNotFoundException
     */
    public static Class classForName(String name) throws ClassNotFoundException {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(
                    name);
        } catch (Exception e) {
            return Class.forName(name);
        }
    }

    public static Method findMethod(String methodName, Class clazz,
            Class[] parmTypes) {
        try {
            return clazz.getMethod(methodName, parmTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static String unqualify(String qualifiedName) {
        return ClassUtils.unqualify(qualifiedName, '.');
    }

    /**
     * Returns the unqualified class name of the specified class.
     *
     * @param clazz the class to get the name for
     * @return The unqualified, short name.
     */
    public static String unqualify(Class clazz) {
        return unqualify(clazz.getName());
    }

    public static String unqualify(String qualifiedName, char separator) {
        return qualifiedName
                .substring(qualifiedName.lastIndexOf(separator) + 1);
    }

    /**
     * Returns the qualifier for a name separated by dots. The qualified part
     * is everything up to the last dot separator.
     *
     * @param qualifiedName
     *            The qualified name.
     * @return The qualifier portion.
     */
    public static String qualifier(String qualifiedName) {
        int loc = qualifiedName.lastIndexOf('.');
        if (loc < 0) {
            return "";
        } else {
            return qualifiedName.substring(0, loc);
        }
    }

    /**
     * Check if the given class represents a primitive wrapper array.
     */
    public static boolean isPrimitiveWrapperArray(Class clazz) {
        return org.springframework.beans.BeanUtils
                .isPrimitiveWrapperArray(clazz);
    }

    /**
     * Check if the given class represents a primitive array.
     */
    public static boolean isPrimitiveArray(Class clazz) {
        return org.springframework.beans.BeanUtils.isPrimitiveArray(clazz);
    }

    /**
     * Does the provided bean class represent a simple scalar property? A
     * simple scalar property is considered a value property; that is, it is
     * not another bean. Examples include primitives, primitive wrappers,
     * Enums, and Strings.
     */
    public static boolean isSimpleScalar(Class clazz) {
        return clazz.isPrimitive() || simpleClasses.contains(clazz)
                || CodedEnum.class.isAssignableFrom(clazz);
    }

    public static Method getStaticMethod(String name, Class locatorClass,
            Class[] args) {
        try {
            logger.debug("Attempting to get method '" + name + "' on class "
                    + locatorClass + " with arguments '"
                    + ArrayUtils.toString(args) + "'");
            Method method = locatorClass.getDeclaredMethod(name, args);
            if ((method.getModifiers() & Modifier.STATIC) != 0) {
                return method;
            } else {
                return null;
            }
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * Given a {@link Map} where the keys are {@link Class}es, search the map
     * for the closest match of the key to the <tt>typeClass</tt>.  This is
     * extremely useful to support polymorphism (and an absolute requirement
     * to find proxied classes where classes are acting as keys in a map).<p />
     *
     * For example: If the Map has keys of Number.class and String.class,
     * using a <tt>typeClass</tt> of Long.class will find the Number.class
     * entry and return its value.<p />
     *
     * When doing the search, it looks for the most exact match it can,
     * giving preference to interfaces over class inheritance.  As a
     * performance optimiziation, if it finds a match it stores the
     * derived match in the map so it does not have to be derived again.
     *
     * @param typeClass the kind of class to search for
     * @param classMap the map where the keys are of type Class
     * @return null only if it can't find any match
     */
    public static Object getValueFromMapForClass(final Class typeClass,
                                                 final Map classMap) {
        Object val = classMap.get(typeClass);
        if (val == null) {
            // search through the interfaces first
            val = getValueFromMapForInterfaces(typeClass, classMap);

            if (val == null) {
                // now go up through the inheritance hierarchy
                val = getValueFromMapForSuperClass(typeClass, classMap);
            }

            if (val == null) {
                // not found anywhere
                logger.warn("Could not find a definition for " + typeClass +
                    " in " + classMap.keySet());
                return null;
            }
            else {
                // remember this so it doesn't have to be looked-up again
                classMap.put(typeClass, val);
                return val;
            }
        }
        else {
            return val;
        }
    }

    private static Object getValueFromMapForInterfaces(final Class typeClass,
                                                       final Map classMap) {
        final Class[] interfaces = typeClass.getInterfaces();

        if (logger.isDebugEnabled()) {
            logger.debug("searching through " + Arrays.asList(interfaces));
        }

        for (int i = 0; i < interfaces.length; i++) {
            final Class anInterface = interfaces[i];
            final Object val = classMap.get(anInterface);
            if (val != null) {
                return val;
            }
        }

        // not found, but now check the parent interfaces
        for (int i = 0; i < interfaces.length; i++) {
            final Class anInterface = interfaces[i];
            final Object val = getValueFromMapForInterfaces(anInterface,
                classMap);
            if (val != null) {
                return val;
            }
        }

        return null;
    }

    private static Object getValueFromMapForSuperClass(final Class typeClass,
                                                       final Map classMap) {
        Class superClass = typeClass.getSuperclass();
        while (superClass != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("searching for " + superClass);
            }
            Object val = classMap.get(superClass);
            if (val != null) {
                return val;
            }

            // try the interfaces
            val = getValueFromMapForInterfaces(superClass, classMap);
            if (val != null) {
                return val;
            }

            superClass = superClass.getSuperclass();
        }
        return null;
    }


    /**
     * Is the given name a property in the class?  In other words, does it
     * have a setter and/or a getter method?
     *
     * @param theClass     the class to look for the property in
     * @param propertyName the name of the property
     *
     * @return true if there is either a setter or a getter for the property
     */
    public static boolean isAProperty(Class theClass, String propertyName) {
        if (theClass == null) throw new IllegalArgumentException("theClass == null");
        if (propertyName == null) throw new IllegalArgumentException("propertyName == null");

        // assumes propertyName.length > 1
        final String getterName = "get" +
            propertyName.substring(0, 1).toUpperCase() +
            propertyName.substring(1);
        try {
            theClass.getMethod(getterName, null);
            // the fact that it didn't throw an exception means that it's here
            // currently ignores such things as being public or non-void
            return true;
        }
        catch (NoSuchMethodException e) {
            // ignore
        }

        // assumes propertyName.length > 1
        final String setterName = "set" +
            propertyName.substring(0, 1).toUpperCase() +
            propertyName.substring(1);
        final Method[] methods = theClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (setterName.equals(method.getName())) {
                // currently ignores such things as being public void, or
                //   taking a single parameter
                return true;
            }
        }

        return false;
    }

}
