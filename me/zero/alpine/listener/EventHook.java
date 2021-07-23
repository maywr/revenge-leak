/*
 * Decompiled with CFR <Could not determine version>.
 */
package me.zero.alpine.listener;

@FunctionalInterface
public interface EventHook<T> {
    public void invoke(T var1);
}

