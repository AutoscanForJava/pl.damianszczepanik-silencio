package pl.szczepanik.silencio.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
public class ReflectionUtility {

    public static <T> T invokeMethod(Object instance, String methodName, Object... arguments)
            throws Exception {
        try {
            Method[] methods = instance.getClass().getDeclaredMethods();
            // instead of providing list of types let's assume
            // that there is only one method in given class with passed name
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    method.setAccessible(true);
                    if (arguments == null) {
                        return (T) method.invoke(instance);
                    } else {
                        return (T) method.invoke(instance, arguments);
                    }
                }
            }
            throw new RuntimeException("Could not find method: " + methodName);
        } catch (IllegalAccessException | IllegalArgumentException | SecurityException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            // if method throws an exception the cause needs to be extracted to pass to caller
            throw (Exception) e.getCause();
        }
    }

    public static <T> T getField(Object instance, String fieldName, Class<T> returnType) {
        try {
            Field field = instance.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(instance);
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            // if method throws an exception the cause needs to be extracted to pass to caller
            throw new RuntimeException(e);
        }
    }

    public static void setField(Object instance, String fieldName, Object value) {
        try {
            Field field = instance.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(instance, value);
        } catch (IllegalAccessException | IllegalArgumentException | SecurityException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        } 
    }
}
