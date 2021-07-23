/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.lib.tree.analysis;

import java.util.List;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.tree.analysis.AnalyzerException;
import org.spongepowered.asm.lib.tree.analysis.BasicValue;
import org.spongepowered.asm.lib.tree.analysis.BasicVerifier;
import org.spongepowered.asm.lib.tree.analysis.Value;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SimpleVerifier
extends BasicVerifier {
    private final Type currentClass;
    private final Type currentSuperClass;
    private final List<Type> currentClassInterfaces;
    private final boolean isInterface;
    private ClassLoader loader = this.getClass().getClassLoader();

    public SimpleVerifier() {
        this(null, null, false);
    }

    public SimpleVerifier(Type currentClass, Type currentSuperClass, boolean isInterface) {
        this(currentClass, currentSuperClass, null, isInterface);
    }

    public SimpleVerifier(Type currentClass, Type currentSuperClass, List<Type> currentClassInterfaces, boolean isInterface) {
        this(327680, currentClass, currentSuperClass, currentClassInterfaces, isInterface);
    }

    protected SimpleVerifier(int api, Type currentClass, Type currentSuperClass, List<Type> currentClassInterfaces, boolean isInterface) {
        super(api);
        this.currentClass = currentClass;
        this.currentSuperClass = currentSuperClass;
        this.currentClassInterfaces = currentClassInterfaces;
        this.isInterface = isInterface;
    }

    public void setClassLoader(ClassLoader loader) {
        this.loader = loader;
    }

    @Override
    public BasicValue newValue(Type type) {
        boolean isArray;
        BasicValue v;
        if (type == null) {
            return BasicValue.UNINITIALIZED_VALUE;
        }
        boolean bl = isArray = type.getSort() == 9;
        if (isArray) {
            switch (type.getElementType().getSort()) {
                case 1: 
                case 2: 
                case 3: 
                case 4: {
                    return new BasicValue(type);
                }
            }
        }
        if (!BasicValue.REFERENCE_VALUE.equals(v = super.newValue(type))) return v;
        if (!isArray) {
            return new BasicValue(type);
        }
        v = this.newValue(type.getElementType());
        String desc = v.getType().getDescriptor();
        int i = 0;
        while (i < type.getDimensions()) {
            desc = '[' + desc;
            ++i;
        }
        return new BasicValue(Type.getType(desc));
    }

    @Override
    protected boolean isArrayValue(BasicValue value) {
        Type t = value.getType();
        if (t == null) return false;
        if ("Lnull;".equals(t.getDescriptor())) return true;
        if (t.getSort() != 9) return false;
        return true;
    }

    @Override
    protected BasicValue getElementValue(BasicValue objectArrayValue) throws AnalyzerException {
        Type arrayType = objectArrayValue.getType();
        if (arrayType == null) throw new Error("Internal error");
        if (arrayType.getSort() == 9) {
            return this.newValue(Type.getType(arrayType.getDescriptor().substring(1)));
        }
        if (!"Lnull;".equals(arrayType.getDescriptor())) throw new Error("Internal error");
        return objectArrayValue;
    }

    @Override
    protected boolean isSubTypeOf(BasicValue value, BasicValue expected) {
        Type expectedType = expected.getType();
        Type type = value.getType();
        switch (expectedType.getSort()) {
            case 5: 
            case 6: 
            case 7: 
            case 8: {
                return type.equals(expectedType);
            }
            case 9: 
            case 10: {
                if ("Lnull;".equals(type.getDescriptor())) {
                    return true;
                }
                if (type.getSort() == 10) return this.isAssignableFrom(expectedType, type);
                if (type.getSort() != 9) return false;
                return this.isAssignableFrom(expectedType, type);
            }
        }
        throw new Error("Internal error");
    }

    @Override
    public BasicValue merge(BasicValue v, BasicValue w) {
        if (v.equals(w)) return v;
        Type t = v.getType();
        Type u = w.getType();
        if (t == null) return BasicValue.UNINITIALIZED_VALUE;
        if (t.getSort() != 10) {
            if (t.getSort() != 9) return BasicValue.UNINITIALIZED_VALUE;
        }
        if (u == null) return BasicValue.UNINITIALIZED_VALUE;
        if (u.getSort() != 10) {
            if (u.getSort() != 9) return BasicValue.UNINITIALIZED_VALUE;
        }
        if ("Lnull;".equals(t.getDescriptor())) {
            return w;
        }
        if ("Lnull;".equals(u.getDescriptor())) {
            return v;
        }
        if (this.isAssignableFrom(t, u)) {
            return v;
        }
        if (this.isAssignableFrom(u, t)) {
            return w;
        }
        do {
            if (t == null) return BasicValue.REFERENCE_VALUE;
            if (!this.isInterface(t)) continue;
            return BasicValue.REFERENCE_VALUE;
        } while (!this.isAssignableFrom(t = this.getSuperClass(t), u));
        return this.newValue(t);
    }

    protected boolean isInterface(Type t) {
        if (this.currentClass == null) return this.getClass(t).isInterface();
        if (!t.equals(this.currentClass)) return this.getClass(t).isInterface();
        return this.isInterface;
    }

    protected Type getSuperClass(Type t) {
        if (this.currentClass != null && t.equals(this.currentClass)) {
            return this.currentSuperClass;
        }
        Class<?> c = this.getClass(t).getSuperclass();
        if (c == null) {
            return null;
        }
        Type type = Type.getType(c);
        return type;
    }

    protected boolean isAssignableFrom(Type t, Type u) {
        if (t.equals(u)) {
            return true;
        }
        if (this.currentClass != null && t.equals(this.currentClass)) {
            if (this.getSuperClass(u) == null) {
                return false;
            }
            if (!this.isInterface) return this.isAssignableFrom(t, this.getSuperClass(u));
            if (u.getSort() == 10) return true;
            if (u.getSort() == 9) return true;
            return false;
        }
        if (this.currentClass != null && u.equals(this.currentClass)) {
            if (this.isAssignableFrom(t, this.currentSuperClass)) {
                return true;
            }
        } else {
            Class<Object> tc = this.getClass(t);
            if (!tc.isInterface()) return tc.isAssignableFrom(this.getClass(u));
            tc = Object.class;
            return tc.isAssignableFrom(this.getClass(u));
        }
        if (this.currentClassInterfaces == null) return false;
        int i = 0;
        while (i < this.currentClassInterfaces.size()) {
            Type v = this.currentClassInterfaces.get(i);
            if (this.isAssignableFrom(t, v)) {
                return true;
            }
            ++i;
        }
        return false;
    }

    protected Class<?> getClass(Type t) {
        try {
            if (t.getSort() != 9) return Class.forName(t.getClassName(), false, this.loader);
            return Class.forName(t.getDescriptor().replace('/', '.'), false, this.loader);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e.toString());
        }
    }
}

