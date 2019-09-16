package me.bow.treecapitatorultimate.Utils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class ReflectionUtils {

    @SuppressWarnings("unchecked")
    private static <T extends AccessibleObject> T setAccessible(T object, boolean access) {
        AccessController.doPrivileged((PrivilegedAction) () -> {
            object.setAccessible(access);
            return null;
        });
        return object;
    }

    public static Field getField(Class<?> clazz, String name) {
        do {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getName().equals(name)) {
                    return setAccessible(field, true);
                }
            }
        } while ((clazz = clazz.getSuperclass()) != null);
        throw new RuntimeException("Can't find field " + name);
    }

    private static boolean forceSetField(Object classInstance, Field f, Object newVal) throws Exception {
        Object origVal = f.get(classInstance);
        f.setAccessible(true);
        f.set(classInstance, newVal);
        if (!f.get(classInstance).equals(origVal)) {
            return true;
        }
        Field modField = setAccessible(Field.class.getDeclaredField("modifiers"), true);
        if ((f.getModifiers() & Modifier.FINAL) == Modifier.FINAL) {
            modField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
        }
        if ((f.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
            modField.setInt(f, f.getModifiers() & ~Modifier.STATIC);
        }
        f.set(classInstance, newVal);
        setAccessible(modField, false);
        return !f.get(classInstance).equals(origVal);
    }

    public static boolean setFinalStatic(Object classInstance, String fieldName, Boolean newValue) throws Exception {
        Class<?> clazz = classInstance.getClass();
        do {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getName().equals(fieldName)) {
                    return forceSetField(classInstance, field, newValue);
                }
            }
        } while ((clazz = clazz.getSuperclass()) != null);
        throw new RuntimeException("Can't find field " + fieldName);
    }

    public static Method getMethod(Class<?> clazz, String name, int paramlength) {
        do {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(name) && (method.getParameterTypes().length == paramlength)) {
                    return setAccessible(method, true);
                }
            }
        } while ((clazz = clazz.getSuperclass()) != null);
        throw new RuntimeException("Can't find method " + name + " with params length " + paramlength);
    }
}