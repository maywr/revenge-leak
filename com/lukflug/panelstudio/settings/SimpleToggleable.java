/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio.settings;

import com.lukflug.panelstudio.settings.Toggleable;

public class SimpleToggleable
implements Toggleable {
    private boolean value;

    public SimpleToggleable(boolean value) {
        this.value = value;
    }

    @Override
    public void toggle() {
        this.value = !this.value;
    }

    @Override
    public boolean isOn() {
        return this.value;
    }
}

