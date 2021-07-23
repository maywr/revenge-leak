/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.jodah.typetools;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import sun.misc.Unsafe;

public final class TypeResolver {
    private static final Map<Class<?>, Reference<Map<TypeVariable<?>, Type>>> TYPE_VARIABLE_CACHE = Collections.synchronizedMap(new WeakHashMap());
    private static volatile boolean CACHE_ENABLED = true;
    private static boolean RESOLVES_LAMBDAS;
    private static Method GET_CONSTANT_POOL;
    private static Method GET_CONSTANT_POOL_SIZE;
    private static Method GET_CONSTANT_POOL_METHOD_AT;
    private static final Map<String, Method> OBJECT_METHODS;
    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPERS;
    private static final Double JAVA_VERSION;

    private TypeResolver() {
    }

    public static void enableCache() {
        CACHE_ENABLED = true;
    }

    public static void disableCache() {
        TYPE_VARIABLE_CACHE.clear();
        CACHE_ENABLED = false;
    }

    public static <T, S extends T> Class<?> resolveRawArgument(Class<T> type, Class<S> subType) {
        return TypeResolver.resolveRawArgument(TypeResolver.resolveGenericType(type, subType), subType);
    }

    public static Class<?> resolveRawArgument(Type genericType, Class<?> subType) {
        Class<?>[] arguments = TypeResolver.resolveRawArguments(genericType, subType);
        if (arguments == null) {
            return Unknown.class;
        }
        if (arguments.length == 1) return arguments[0];
        throw new IllegalArgumentException("Expected 1 argument for generic type " + genericType + " but found " + arguments.length);
    }

    public static <T, S extends T> Class<?>[] resolveRawArguments(Class<T> type, Class<S> subType) {
        return TypeResolver.resolveRawArguments(TypeResolver.resolveGenericType(type, subType), subType);
    }

    public static Class<?>[] resolveRawArguments(Type genericType, Class<?> subType) {
        Class[] result = null;
        Class functionalInterface = null;
        if (RESOLVES_LAMBDAS && subType.isSynthetic()) {
            Class fi;
            Class class_ = genericType instanceof ParameterizedType && ((ParameterizedType)genericType).getRawType() instanceof Class ? (Class)((ParameterizedType)genericType).getRawType() : (fi = genericType instanceof Class ? (Class)genericType : null);
            if (fi != null && fi.isInterface()) {
                functionalInterface = fi;
            }
        }
        if (genericType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType)genericType;
            Type[] arguments = paramType.getActualTypeArguments();
            result = new Class[arguments.length];
            int i = 0;
            while (i < arguments.length) {
                result[i] = TypeResolver.resolveRawClass(arguments[i], subType, functionalInterface);
                ++i;
            }
            return result;
        }
        if (genericType instanceof TypeVariable) {
            result = new Class[]{TypeResolver.resolveRawClass(genericType, subType, functionalInterface)};
            return result;
        }
        if (!(genericType instanceof Class)) return result;
        TypeVariable<Class<T>>[] typeParams = ((Class)genericType).getTypeParameters();
        result = new Class[typeParams.length];
        int i = 0;
        while (i < typeParams.length) {
            result[i] = TypeResolver.resolveRawClass(typeParams[i], subType, functionalInterface);
            ++i;
        }
        return result;
    }

    public static Type resolveGenericType(Class<?> type, Type subType) {
        Type superClass;
        Type result;
        Class rawType = subType instanceof ParameterizedType ? (Class)((ParameterizedType)subType).getRawType() : (Class)subType;
        if (type.equals((Object)rawType)) {
            return subType;
        }
        if (type.isInterface()) {
            for (Type superInterface : rawType.getGenericInterfaces()) {
                if (superInterface == null || superInterface.equals(Object.class) || (result = TypeResolver.resolveGenericType(type, superInterface)) == null) continue;
                return result;
            }
        }
        if ((superClass = rawType.getGenericSuperclass()) == null) return null;
        if (superClass.equals(Object.class)) return null;
        result = TypeResolver.resolveGenericType(type, superClass);
        if (result == null) return null;
        return result;
    }

    public static Class<?> resolveRawClass(Type genericType, Class<?> subType) {
        return TypeResolver.resolveRawClass(genericType, subType, null);
    }

    private static Class<?> resolveRawClass(Type genericType, Class<?> subType, Class<?> functionalInterface) {
        if (genericType instanceof Class) {
            return (Class)genericType;
        }
        if (genericType instanceof ParameterizedType) {
            return TypeResolver.resolveRawClass(((ParameterizedType)genericType).getRawType(), subType, functionalInterface);
        }
        if (genericType instanceof GenericArrayType) {
            GenericArrayType arrayType = (GenericArrayType)genericType;
            Class<?> component = TypeResolver.resolveRawClass(arrayType.getGenericComponentType(), subType, functionalInterface);
            return Array.newInstance(component, 0).getClass();
        }
        if (genericType instanceof TypeVariable) {
            TypeVariable variable = (TypeVariable)genericType;
            genericType = TypeResolver.getTypeVariableMap(subType, functionalInterface).get(variable);
            genericType = genericType == null ? TypeResolver.resolveBound(variable) : TypeResolver.resolveRawClass(genericType, subType, functionalInterface);
        }
        if (!(genericType instanceof Class)) return Unknown.class;
        Class class_ = (Class)genericType;
        return class_;
    }

    private static Map<TypeVariable<?>, Type> getTypeVariableMap(Class<?> targetType, Class<?> functionalInterface) {
        Class<?> type;
        Reference<Map<TypeVariable<?>, Type>> ref = TYPE_VARIABLE_CACHE.get(targetType);
        Map<TypeVariable<?>, Type> map = ref != null ? ref.get() : null;
        if (map != null) return map;
        map = new HashMap();
        if (functionalInterface != null) {
            TypeResolver.populateLambdaArgs(functionalInterface, targetType, map);
        }
        TypeResolver.populateSuperTypeArgs(targetType.getGenericInterfaces(), map, functionalInterface != null);
        Type genericType = targetType.getGenericSuperclass();
        for (type = targetType.getSuperclass(); type != null && !Object.class.equals(type); type = type.getSuperclass()) {
            if (genericType instanceof ParameterizedType) {
                TypeResolver.populateTypeArgs((ParameterizedType)genericType, map, false);
            }
            TypeResolver.populateSuperTypeArgs(type.getGenericInterfaces(), map, false);
            genericType = type.getGenericSuperclass();
        }
        type = targetType;
        do {
            if (!type.isMemberClass()) {
                if (!CACHE_ENABLED) return map;
                TYPE_VARIABLE_CACHE.put(targetType, new WeakReference(map));
                return map;
            }
            genericType = type.getGenericSuperclass();
            if (genericType instanceof ParameterizedType) {
                TypeResolver.populateTypeArgs((ParameterizedType)genericType, map, functionalInterface != null);
            }
            type = type.getEnclosingClass();
        } while (true);
    }

    private static void populateSuperTypeArgs(Type[] types, Map<TypeVariable<?>, Type> map, boolean depthFirst) {
        Type[] arrtype = types;
        int n = arrtype.length;
        int n2 = 0;
        while (n2 < n) {
            Type type = arrtype[n2];
            if (type instanceof ParameterizedType) {
                Type rawType;
                ParameterizedType parameterizedType = (ParameterizedType)type;
                if (!depthFirst) {
                    TypeResolver.populateTypeArgs(parameterizedType, map, depthFirst);
                }
                if ((rawType = parameterizedType.getRawType()) instanceof Class) {
                    TypeResolver.populateSuperTypeArgs(((Class)rawType).getGenericInterfaces(), map, depthFirst);
                }
                if (depthFirst) {
                    TypeResolver.populateTypeArgs(parameterizedType, map, depthFirst);
                }
            } else if (type instanceof Class) {
                TypeResolver.populateSuperTypeArgs(((Class)type).getGenericInterfaces(), map, depthFirst);
            }
            ++n2;
        }
    }

    private static void populateTypeArgs(ParameterizedType type, Map<TypeVariable<?>, Type> map, boolean depthFirst) {
        Type owner;
        if (!(type.getRawType() instanceof Class)) return;
        TypeVariable<Class<T>>[] typeVariables = ((Class)type.getRawType()).getTypeParameters();
        Type[] typeArguments = type.getActualTypeArguments();
        if (type.getOwnerType() != null && (owner = type.getOwnerType()) instanceof ParameterizedType) {
            TypeResolver.populateTypeArgs((ParameterizedType)owner, map, depthFirst);
        }
        int i = 0;
        while (i < typeArguments.length) {
            TypeVariable variable = typeVariables[i];
            Type typeArgument = typeArguments[i];
            if (typeArgument instanceof Class) {
                map.put(variable, typeArgument);
            } else if (typeArgument instanceof GenericArrayType) {
                map.put(variable, typeArgument);
            } else if (typeArgument instanceof ParameterizedType) {
                map.put(variable, typeArgument);
            } else if (typeArgument instanceof TypeVariable) {
                Type existingType;
                TypeVariable typeVariableArgument = (TypeVariable)typeArgument;
                if (depthFirst && (existingType = map.get(variable)) != null) {
                    map.put(typeVariableArgument, existingType);
                } else {
                    Type resolvedType = map.get(typeVariableArgument);
                    if (resolvedType == null) {
                        resolvedType = TypeResolver.resolveBound(typeVariableArgument);
                    }
                    map.put(variable, resolvedType);
                }
            }
            ++i;
        }
    }

    public static Type resolveBound(TypeVariable<?> typeVariable) {
        Type[] bounds = typeVariable.getBounds();
        if (bounds.length == 0) {
            return Unknown.class;
        }
        Type bound = bounds[0];
        if (bound instanceof TypeVariable) {
            bound = TypeResolver.resolveBound((TypeVariable)bound);
        }
        if (bound == Object.class) {
            return Unknown.class;
        }
        Type type = bound;
        return type;
    }

    private static void populateLambdaArgs(Class<?> functionalInterface, Class<?> lambdaType, Map<TypeVariable<?>, Type> map) {
        if (!RESOLVES_LAMBDAS) return;
        Method[] arrmethod = functionalInterface.getMethods();
        int n = arrmethod.length;
        int n2 = 0;
        while (n2 < n) {
            Method objectMethod;
            Method m = arrmethod[n2];
            if (!(TypeResolver.isDefaultMethod(m) || Modifier.isStatic(m.getModifiers()) || m.isBridge() || (objectMethod = OBJECT_METHODS.get(m.getName())) != null && Arrays.equals(m.getTypeParameters(), objectMethod.getTypeParameters()))) {
                Type returnTypeVar = m.getGenericReturnType();
                Type[] paramTypeVars = m.getGenericParameterTypes();
                Member member = TypeResolver.getMemberRef(lambdaType);
                if (member == null) {
                    return;
                }
                if (returnTypeVar instanceof TypeVariable) {
                    Class<Object> returnType = member instanceof Method ? ((Method)member).getReturnType() : ((Constructor)member).getDeclaringClass();
                    if (!(returnType = TypeResolver.wrapPrimitives(returnType)).equals(Void.class)) {
                        map.put((TypeVariable)returnTypeVar, returnType);
                    }
                }
                Class<?>[] arguments = member instanceof Method ? ((Method)member).getParameterTypes() : ((Constructor)member).getParameterTypes();
                int paramOffset = 0;
                if (paramTypeVars.length > 0 && paramTypeVars[0] instanceof TypeVariable && paramTypeVars.length == arguments.length + 1) {
                    Class<?> instanceType = member.getDeclaringClass();
                    map.put((TypeVariable)paramTypeVars[0], instanceType);
                    paramOffset = 1;
                }
                int argOffset = 0;
                if (paramTypeVars.length < arguments.length) {
                    argOffset = arguments.length - paramTypeVars.length;
                }
                int i = 0;
                while (i + argOffset < arguments.length) {
                    if (paramTypeVars[i] instanceof TypeVariable) {
                        map.put((TypeVariable)paramTypeVars[i + paramOffset], TypeResolver.wrapPrimitives(arguments[i + argOffset]));
                    }
                    ++i;
                }
                return;
            }
            ++n2;
        }
    }

    private static boolean isDefaultMethod(Method m) {
        if (!(JAVA_VERSION >= 1.8)) return false;
        if (!m.isDefault()) return false;
        return true;
    }

    private static Member getMemberRef(Class<?> type) {
        Object constantPool;
        try {
            constantPool = GET_CONSTANT_POOL.invoke(type, new Object[0]);
        }
        catch (Exception ignore) {
            return null;
        }
        Member result = null;
        int i = TypeResolver.getConstantPoolSize(constantPool) - 1;
        while (i >= 0) {
            Member member = TypeResolver.getConstantPoolMethodAt(constantPool, i);
            if (!(member == null || member instanceof Constructor && member.getDeclaringClass().getName().equals("java.lang.invoke.SerializedLambda") || member.getDeclaringClass().isAssignableFrom(type))) {
                result = member;
                if (!(member instanceof Method)) return result;
                if (!TypeResolver.isAutoBoxingMethod((Method)member)) {
                    return result;
                }
            }
            --i;
        }
        return result;
    }

    private static boolean isAutoBoxingMethod(Method method) {
        Class<?>[] parameters = method.getParameterTypes();
        if (!method.getName().equals("valueOf")) return false;
        if (parameters.length != 1) return false;
        if (!parameters[0].isPrimitive()) return false;
        if (!TypeResolver.wrapPrimitives(parameters[0]).equals(method.getDeclaringClass())) return false;
        return true;
    }

    private static Class<?> wrapPrimitives(Class<?> clazz) {
        Class<?> class_;
        if (clazz.isPrimitive()) {
            class_ = PRIMITIVE_WRAPPERS.get(clazz);
            return class_;
        }
        class_ = clazz;
        return class_;
    }

    private static int getConstantPoolSize(Object constantPool) {
        try {
            return (Integer)GET_CONSTANT_POOL_SIZE.invoke(constantPool, new Object[0]);
        }
        catch (Exception ignore) {
            return 0;
        }
    }

    private static Member getConstantPoolMethodAt(Object constantPool, int i) {
        try {
            return (Member)GET_CONSTANT_POOL_METHOD_AT.invoke(constantPool, i);
        }
        catch (Exception ignore) {
            return null;
        }
    }

    static {
        OBJECT_METHODS = new HashMap<String, Method>();
        JAVA_VERSION = Double.parseDouble(System.getProperty("java.specification.version", "0"));
        try {
            Unsafe unsafe = AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>(){

                @Override
                public Unsafe run() throws Exception {
                    Field f = Unsafe.class.getDeclaredField("theUnsafe");
                    f.setAccessible(true);
                    return (Unsafe)f.get(null);
                }
            });
            GET_CONSTANT_POOL = Class.class.getDeclaredMethod("getConstantPool", new Class[0]);
            String constantPoolName = JAVA_VERSION < 9.0 ? "sun.reflect.ConstantPool" : "jdk.internal.reflect.ConstantPool";
            Class<?> constantPoolClass = Class.forName(constantPoolName);
            GET_CONSTANT_POOL_SIZE = constantPoolClass.getDeclaredMethod("getSize", new Class[0]);
            GET_CONSTANT_POOL_METHOD_AT = constantPoolClass.getDeclaredMethod("getMethodAt", Integer.TYPE);
            Field overrideField = AccessibleObject.class.getDeclaredField("override");
            long overrideFieldOffset = unsafe.objectFieldOffset(overrideField);
            unsafe.putBoolean((Object)GET_CONSTANT_POOL, overrideFieldOffset, true);
            unsafe.putBoolean((Object)GET_CONSTANT_POOL_SIZE, overrideFieldOffset, true);
            unsafe.putBoolean((Object)GET_CONSTANT_POOL_METHOD_AT, overrideFieldOffset, true);
            Object constantPool = GET_CONSTANT_POOL.invoke(Object.class, new Object[0]);
            GET_CONSTANT_POOL_SIZE.invoke(constantPool, new Object[0]);
            for (Method method : Object.class.getDeclaredMethods()) {
                OBJECT_METHODS.put(method.getName(), method);
            }
            RESOLVES_LAMBDAS = true;
        }
        catch (Exception unsafe) {
            // empty catch block
        }
        HashMap<Class<Object>, Class<Void>> types = new HashMap<Class<Object>, Class<Void>>();
        types.put(Boolean.TYPE, Boolean.class);
        types.put(Byte.TYPE, Byte.class);
        types.put(Character.TYPE, Character.class);
        types.put(Double.TYPE, Double.class);
        types.put(Float.TYPE, Float.class);
        types.put(Integer.TYPE, Integer.class);
        types.put(Long.TYPE, Long.class);
        types.put(Short.TYPE, Short.class);
        types.put(Void.TYPE, Void.class);
        PRIMITIVE_WRAPPERS = Collections.unmodifiableMap(types);
    }

    public static final class Unknown {
        private Unknown() {
        }
    }

}

