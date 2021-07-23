/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.util;

public final class Counter {
    public int value;

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != Counter.class) return false;
        if (((Counter)obj).value != this.value) return false;
        return true;
    }

    public int hashCode() {
        return this.value;
    }
}

