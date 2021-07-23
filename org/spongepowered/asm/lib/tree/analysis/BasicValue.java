/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.lib.tree.analysis;

import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.tree.analysis.Value;

public class BasicValue
implements Value {
    public static final BasicValue UNINITIALIZED_VALUE = new BasicValue(null);
    public static final BasicValue INT_VALUE = new BasicValue(Type.INT_TYPE);
    public static final BasicValue FLOAT_VALUE = new BasicValue(Type.FLOAT_TYPE);
    public static final BasicValue LONG_VALUE = new BasicValue(Type.LONG_TYPE);
    public static final BasicValue DOUBLE_VALUE = new BasicValue(Type.DOUBLE_TYPE);
    public static final BasicValue REFERENCE_VALUE = new BasicValue(Type.getObjectType("java/lang/Object"));
    public static final BasicValue RETURNADDRESS_VALUE = new BasicValue(Type.VOID_TYPE);
    private final Type type;

    public BasicValue(Type type) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    @Override
    public int getSize() {
        if (this.type == Type.LONG_TYPE) return 2;
        if (this.type == Type.DOUBLE_TYPE) return 2;
        return 1;
    }

    public boolean isReference() {
        if (this.type == null) return false;
        if (this.type.getSort() == 10) return true;
        if (this.type.getSort() != 9) return false;
        return true;
    }

    public boolean equals(Object value) {
        if (value == this) {
            return true;
        }
        if (!(value instanceof BasicValue)) return false;
        if (this.type != null) return this.type.equals(((BasicValue)value).type);
        if (((BasicValue)value).type != null) return false;
        return true;
    }

    public int hashCode() {
        if (this.type == null) {
            return 0;
        }
        int n = this.type.hashCode();
        return n;
    }

    public String toString() {
        if (this == UNINITIALIZED_VALUE) {
            return ".";
        }
        if (this == RETURNADDRESS_VALUE) {
            return "A";
        }
        if (this != REFERENCE_VALUE) return this.type.getDescriptor();
        return "R";
    }
}

