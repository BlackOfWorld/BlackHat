package me.bow.treecapitatorultimate.Utils;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.bukkit.Bukkit;

import java.lang.reflect.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReflectionUtils {
    private static final String OBC_PREFIX = Bukkit.getServer().getClass().getPackage().getName();
    private static final String NMS_PREFIX = OBC_PREFIX.replace("org.bukkit.craftbukkit", "net.minecraft.server");
    private static final String VERSION = OBC_PREFIX.replace("org.bukkit.craftbukkit", "").replace(".", "");
    @SuppressWarnings("RegExpRedundantEscape")
    private static final Pattern MATCH_VARIABLE = Pattern.compile("\\{([^\\}]+)\\}");
    private static final Map<String, Field> fieldCache = new HashMap<>();
    private static final Map<String, Class<?>> classCache = new HashMap<>();
    private static final Table<Class<?>, MethodParams, Method> methodParamCache = HashBasedTable.create();
    private static final Table<Class<?>, String, Method> methodCache = HashBasedTable.create();
    private static final Table<Class<?>, ConstructorParams, ConstructorInvoker> constructorParamCache = HashBasedTable.create();
    private static final Map<EnumParam, Object> enumCache = new HashMap<>();
    private static Field signature;
    static {
        try {
            signature = Field.class.getDeclaredField("signature");
            signature.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieve a class from its full name.
     * <p>
     * Strings enclosed with curly brackets - such as {TEXT} - will be replaced according to the following table:
     * <p>
     * <table border="1">
     * <tr>
     * <th>Variable</th>
     * <th>Content</th>
     * </tr>
     * <tr>
     * <td>{nms}</td>
     * <td>Actual package name of net.minecraft.server.VERSION</td>
     * </tr>
     * <tr>
     * <td>{obc}</td>
     * <td>Actual pacakge name of org.bukkit.craftbukkit.VERSION</td>
     * </tr>
     * <tr>
     * <td>{version}</td>
     * <td>The current Minecraft package VERSION, if any.</td>
     * </tr>
     * </table>
     *
     * @param lookupName - the class name with variables.
     * @return The looked up class.
     * @throws IllegalArgumentException If a variable or class could not be found.
     */
    public static Class<?> getClass(String lookupName) {
        return getCanonicalClass(expandVariables(lookupName));
    }

    public static boolean classExists(String lookupName) {
        try {
            Class<?> clazz = getCanonicalClass(expandVariables(lookupName));
            return clazz != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends AccessibleObject> T setAccessible(T object, boolean access) {
        AccessController.doPrivileged((PrivilegedAction) () -> {
            object.setAccessible(access);
            return null;
        });
        return object;
    }

    public static <T> Field getField(String clazz, String name) {
        return getField(getClass(clazz), name, null, 0);
    }

    public static <T> Field getField(Class<?> clazz, String name) {
        return getField(clazz, name, null, 0);
    }

    /**
     * Retrieve a field accessor for a specific field type and name.
     *
     * @param target    - the target type.
     * @param name      - the name of the field, or NULL to ignore.
     * @param fieldType - a compatible field type.
     * @return The field accessor.
     */
    public static <T> Field getField(Class<?> target, String name, Class<T> fieldType) {
        return getField(target, name, fieldType, 0);
    }

    /**
     * Retrieve a field accessor for a specific field type and name.
     *
     * @param className - lookup name of the class, see {@link #getClass(String)}.
     * @param name      - the name of the field, or NULL to ignore.
     * @param fieldType - a compatible field type.
     * @return The field accessor.
     */
    public static <T> Field getField(String className, String name, Class<T> fieldType) {
        return getField(getClass(className), name, fieldType, 0);
    }

    /**
     * Retrieve a field accessor for a specific field type and name.
     *
     * @param target    - the target type.
     * @param fieldType - a compatible field type.
     * @param index     - the number of compatible fields to skip.
     * @return The field accessor.
     */
    public static <T> Field getField(Class<?> target, Class<T> fieldType, int index) {
        return getField(target, null, fieldType, index);
    }

    /**
     * Retrieve a field accessor for a specific field type and name.
     *
     * @param className - lookup name of the class, see {@link #getClass(String)}.
     * @param fieldType - a compatible field type.
     * @param index     - the number of compatible fields to skip.
     * @return The field accessor.
     */
    public static <T> Field getField(String className, Class<T> fieldType, int index) {
        return getField(getClass(className), fieldType, index);
    }

    // Common method
    private static <T> Field getField(Class<?> target, String name, Class<T> fieldType, int index) {
        Class<?> clazz = target;
        do {
            for (final Field field : clazz.getDeclaredFields()) {
                if ((name == null || field.getName().equals(name)) && (fieldType == null || fieldType.isAssignableFrom(field.getType())) && index-- <= 0) {
                    return setAccessible(field, true);
                }
            }
        } while ((clazz = clazz.getSuperclass()) != null);
        throw new IllegalArgumentException("Cannot find field with type " + fieldType);
    }
    public static String getFieldSignature(Field f) {
        try {
            return (String)signature.get(f);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Cannot find field signature for "+f.getName());
    }
    public static Field getFieldCached(Class<?> clazz, String name) {
        if (fieldCache.containsKey(clazz.getName() + "." + name)) {
            return fieldCache.get(clazz.getName() + "." + name);
        }
        Field f = getField(clazz, name);
        fieldCache.put(clazz.getName() + "." + name, f);
        return f;
    }

    private static boolean forceSetField(Object classInstance, Field f, Object newVal) throws Exception {
        f.setAccessible(true);
        Object origVal = f.get(classInstance);
        f.set(classInstance, newVal);
        Object oldNewVal = f.get(classInstance);
        if (oldNewVal != null && !oldNewVal.equals(origVal)) {
            f.setAccessible(false);
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

    public static boolean setFinalStatic(Object classInstance, String fieldName, Object newValue) throws Exception {
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
                if (method.getName().equals(name) && (paramlength == -1 || method.getParameterTypes().length == paramlength)) {
                    return setAccessible(method, true);
                }
            }
        } while ((clazz = clazz.getSuperclass()) != null);
        throw new RuntimeException("Can't find method " + name + " with params length " + paramlength);
    }

    public static Class<?> getClassCached(String lookupName) {
        if (classCache.containsKey(lookupName)) {
            return classCache.get(lookupName);
        }
        Class<?> classForName = getClass(lookupName);
        classCache.put(lookupName, classForName);
        return classForName;
    }

    public static Object getEnumVariableCached(String clazzName, String enumName) {
        Class<?> clazz = ReflectionUtils.getClassCached(clazzName);
        EnumParam enumParam = new EnumParam(clazz, enumName);
        if (enumCache.containsKey(enumParam)) {
            return enumCache.get(enumParam);
        }
        Object enumValue = getEnumVariable(clazz, enumName);
        enumCache.put(enumParam, enumValue);
        return enumValue;
    }

    public static Object getEnumVariable(String clazzName, String enumName) {
        return getEnumVariable(ReflectionUtils.getClass(clazzName), enumName);
    }

    public static Object getEnumVariable(Class<?> clazz, String enumName) {
        try {
            Method method = clazz.getDeclaredMethod("values");
            Object obj = method.invoke(null);
            Object[] enumValues = (Object[]) obj;
            for (Object enumVal : enumValues) {
                if (!enumVal.toString().equalsIgnoreCase(enumName)) continue;
                return enumVal;
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Method getMethod(String clazzName, String name, Class<?>... params) {
        return getTypedMethod(getClass(clazzName), name, null, params);
    }

    public static Method getMethod(Class<?> clazz, String name, Class<?>... params) {
        return getTypedMethod(clazz, name, null, params);
    }


    public static Method getTypedMethod(Class<?> target, String methodName, Class<?> returnType, Class<?>... params) {
        Class<?> clazz = target;
        do {
            for (final Method method : clazz.getDeclaredMethods()) {
                if ((methodName == null || method.getName().equals(methodName)) && (returnType == null || method.getReturnType().equals(returnType)) && (params == null || Arrays.equals(method.getParameterTypes(), params))) {
                    return setAccessible(method, true);
                }
            }
        } while ((clazz = clazz.getSuperclass()) != null);


        throw new IllegalStateException(String.format("Unable to find method %s (%s).", methodName, Arrays.asList(params)));
    }

    private static String expandVariables(String name) {
        StringBuffer output = new StringBuffer();
        Matcher matcher = MATCH_VARIABLE.matcher(name);

        while (matcher.find()) {
            String variable = matcher.group(1);
            String replacement;

            // Expand all detected variables
            if ("nms".equalsIgnoreCase(variable))
                replacement = NMS_PREFIX;
            else if ("obc".equalsIgnoreCase(variable))
                replacement = OBC_PREFIX;
            else if ("version".equalsIgnoreCase(variable))
                replacement = VERSION;
            else
                throw new IllegalArgumentException("Unknown variable: " + variable);

            // Assume the expanded variables are all packages, and append a dot
            if (replacement.length() > 0 && matcher.end() < name.length() && name.charAt(matcher.end()) != '.')
                replacement += ".";
            matcher.appendReplacement(output, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(output);
        return output.toString();
    }

    /**
     * Retrieve a class in the net.minecraft.server.VERSION.* package.
     *
     * @param name - the name of the class, excluding the package.
     * @throws IllegalArgumentException If the class doesn't exist.
     */
    public static Class<?> getMinecraftClass(String name) {
        return getCanonicalClass(NMS_PREFIX + "." + name);
    }

    /**
     * Retrieve a class in the org.bukkit.craftbukkit.VERSION.* package.
     *
     * @param name - the name of the class, excluding the package.
     * @throws IllegalArgumentException If the class doesn't exist.
     */
    public static Class<?> getCraftBukkitClass(String name) {
        return getCanonicalClass(OBC_PREFIX + "." + name);
    }

    /**
     * Retrieve a class by its canonical name.
     *
     * @param canonicalName - the canonical name.
     * @return The class.
     */
    private static Class<?> getCanonicalClass(String canonicalName) {
        try {
            return Class.forName(canonicalName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Cannot find " + canonicalName, e);
        }
    }

    /**
     * Retrieve a class from its full name, without knowing its type on compile time.
     * <p>
     * This is useful when looking up fields by a NMS or OBC type.
     * <p>
     *
     * @param lookupName - the class name with variables.
     * @return The class.
     * @see {@link #getClass()} for more information.
     */
    public static Class<Object> getUntypedClass(String lookupName) {
        @SuppressWarnings({"rawtypes", "unchecked"})
        Class<Object> clazz = (Class) getClass(lookupName);
        return clazz;
    }

    /**
     * Search for the first publically and privately defined constructor of the given name and parameter count.
     *
     * @param className - lookup name of the class, see {@link #getClass(String)}.
     * @param params    - the expected parameters.
     * @return An object that invokes this constructor.
     * @throws IllegalStateException If we cannot find this method.
     */
    public static ConstructorInvoker getConstructor(String className, Class<?>... params) {
        return getConstructor(getClass(className), params);
    }

    /**
     * Search for the first publically and privately defined constructor of the given name and parameter count.
     *
     * @param clazz  - a class to start with.
     * @param params - the expected parameters.
     * @return An object that invokes this constructor.
     * @throws IllegalStateException If we cannot find this method.
     */
    public static ConstructorInvoker getConstructor(Class<?> clazz, Class<?>... params) {
        for (final Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (params == null || Arrays.equals(constructor.getParameterTypes(), params)) {
                constructor.setAccessible(true);

                return arguments -> {
                    try {
                        return constructor.newInstance(arguments);
                    } catch (Exception e) {
                        throw new RuntimeException("Cannot invoke constructor " + constructor, e);
                    }
                };
            }
        }

        throw new IllegalStateException(String.format("Unable to find constructor for %s (%s).", clazz, Arrays.asList(params)));
    }

    public static Method getMethodCached(Class<?> clazz, String methodName, Class<?>... params) {
        MethodParams methodParams = new MethodParams(methodName, params);
        if (methodParamCache.contains(clazz, methodParams)) {
            return methodParamCache.get(clazz, methodParams);
        }
        Method method = getMethod(clazz, methodName, params);
        methodParamCache.put(clazz, methodParams, method);
        return method;
    }

    public static ConstructorInvoker getConstructorCached(String clazzName, Class<?>... params) {
        return getConstructorCached(getClassCached(clazzName), params);
    }

    public static ConstructorInvoker getConstructorCached(Class<?> clazz, Class<?>... params) {
        ConstructorParams constructorParams = new ConstructorParams(params);
        if (constructorParamCache.contains(clazz, constructorParams)) {
            return constructorParamCache.get(clazz, constructorParams);
        }
        ConstructorInvoker invoker = getConstructor(clazz, params);
        constructorParamCache.put(clazz, constructorParams, invoker);
        return invoker;
    }

    public static boolean versionIsNewerOrEqualAs(int major, int minor, int patch) {
        return getMajorVersion() >= major && getMinorVersion() >= minor && getPatchVersion() >= patch;
    }

    private static int getMajorVersion() {
        return Integer.parseInt(getVersionSanitized().split("_")[0]);
    }

    private static String getVersionSanitized() {
        return VERSION.replaceAll("[^\\d_]", "");
    }

    private static int getMinorVersion() {
        return Integer.parseInt(getVersionSanitized().split("_")[1]);
    }

    private static int getPatchVersion() {
        String[] split = getVersionSanitized().split("_");
        if (split.length < 3) {
            return 0;
        }
        return Integer.parseInt(split[2]);
    }

    public interface ConstructorInvoker {
        /**
         * Invoke a constructor for a specific class.
         *
         * @param arguments - the arguments to pass to the constructor.
         * @return The constructed object.
         */
        Object invoke(Object... arguments);
    }

    private static class MethodParams {
        private final String name;
        private final Class<?>[] params;

        MethodParams(final String name, final Class<?>[] params) {
            this.name = name;
            this.params = params;
        }

        // Ugly autogenned Lombok code
        @Override
        public boolean equals(final Object o) {
            if (o == this) return true;
            if (!(o instanceof MethodParams)) return false;
            final MethodParams that = (MethodParams) o;
            if (!that.canEqual(this)) return false;
            final Object thisName = this.name;
            final Object thatName = that.name;
            if (thisName == null && thatName == null) {
                return Arrays.deepEquals(this.params, that.params);
            } else if (thisName.equals(thatName)) {
                return Arrays.deepEquals(this.params, that.params);
            }
            return false;
        }

        boolean canEqual(final Object that) {
            return that instanceof MethodParams;
        }

        @Override
        public int hashCode() {
            int result = 1;
            final Object thisName = this.name;
            result = result * 31 + ((thisName == null) ? 0 : thisName.hashCode());
            result = result * 31 + Arrays.deepHashCode(this.params);
            return result;
        }
    }

    // Necessary for deepequals
    private static class ConstructorParams {
        private final Class<?>[] params;

        ConstructorParams(Class<?>[] params) {
            this.params = params;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ConstructorParams that = (ConstructorParams) o;

            return Arrays.deepEquals(params, that.params);
        }

        @Override
        public int hashCode() {
            return Arrays.deepHashCode(params);
        }
    }

    private static class EnumParam {
        private final Class<?> clazz;
        private final String enumVal;

        EnumParam(Class<?> clazz, String enumVal) {
            this.clazz = clazz;
            this.enumVal = enumVal;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EnumParam that = (EnumParam) o;

            return this.clazz.getName().equals(that.clazz.getName()) && this.enumVal.equals(that.enumVal);
        }

        @Override
        public int hashCode() {
            int result = 1;
            final Object thisName = this.enumVal;
            result = result * 31 + ((thisName == null) ? 0 : thisName.hashCode());
            result = result * 31 + clazz.hashCode();
            return result;
        }
    }
}