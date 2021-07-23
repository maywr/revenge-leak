/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.rianix.revenge.util;

public class Timer {
    private static long currentMS = 0L;
    private static long lastMS = -1L;

    public static void setCurrentMS() {
        currentMS = System.nanoTime() / 1000000L;
    }

    public static boolean hasDelayRun(long time) {
        if (currentMS - lastMS < time) return false;
        return true;
    }

    public static void setLastMS() {
        lastMS = System.nanoTime() / 1000000L;
    }

    public void reset() {
        currentMS = System.nanoTime() / 1000000L;
    }
}

