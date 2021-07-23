/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.util.asm;

import java.util.List;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.tree.analysis.SimpleVerifier;
import org.spongepowered.asm.mixin.transformer.ClassInfo;

public class MixinVerifier
extends SimpleVerifier {
    private Type currentClass;
    private Type currentSuperClass;
    private List<Type> currentClassInterfaces;
    private boolean isInterface;

    public MixinVerifier(Type currentClass, Type currentSuperClass, List<Type> currentClassInterfaces, boolean isInterface) {
        super(currentClass, currentSuperClass, currentClassInterfaces, isInterface);
        this.currentClass = currentClass;
        this.currentSuperClass = currentSuperClass;
        this.currentClassInterfaces = currentClassInterfaces;
        this.isInterface = isInterface;
    }

    @Override
    protected boolean isInterface(Type type) {
        if (this.currentClass == null) return ClassInfo.forType(type).isInterface();
        if (!type.equals(this.currentClass)) return ClassInfo.forType(type).isInterface();
        return this.isInterface;
    }

    @Override
    protected Type getSuperClass(Type type) {
        if (this.currentClass != null && type.equals(this.currentClass)) {
            return this.currentSuperClass;
        }
        ClassInfo c = ClassInfo.forType(type).getSuperClass();
        if (c == null) {
            return null;
        }
        Type type2 = Type.getType("L" + c.getName() + ";");
        return type2;
    }

    @Override
    protected boolean isAssignableFrom(Type type, Type other) {
        if (type.equals(other)) {
            return true;
        }
        if (this.currentClass != null && type.equals(this.currentClass)) {
            if (this.getSuperClass(other) == null) {
                return false;
            }
            if (!this.isInterface) return this.isAssignableFrom(type, this.getSuperClass(other));
            if (other.getSort() == 10) return true;
            if (other.getSort() == 9) return true;
            return false;
        }
        if (this.currentClass != null && other.equals(this.currentClass)) {
            if (this.isAssignableFrom(type, this.currentSuperClass)) {
                return true;
            }
        } else {
            ClassInfo typeInfo = ClassInfo.forType(type);
            if (typeInfo == null) {
                return false;
            }
            if (!typeInfo.isInterface()) return ClassInfo.forType(other).hasSuperClass(typeInfo);
            typeInfo = ClassInfo.forName("java/lang/Object");
            return ClassInfo.forType(other).hasSuperClass(typeInfo);
        }
        if (this.currentClassInterfaces == null) return false;
        int i = 0;
        while (i < this.currentClassInterfaces.size()) {
            Type v = this.currentClassInterfaces.get(i);
            if (this.isAssignableFrom(type, v)) {
                return true;
            }
            ++i;
        }
        return false;
    }
}

