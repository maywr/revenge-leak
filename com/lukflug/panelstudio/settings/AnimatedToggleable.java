/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio.settings;

import com.lukflug.panelstudio.Animation;
import com.lukflug.panelstudio.settings.Toggleable;

public final class AnimatedToggleable
implements Toggleable {
    private final Toggleable toggle;
    private final Animation animation;

    public AnimatedToggleable(Toggleable toggle, Animation animation) {
        this.toggle = toggle;
        this.animation = animation;
        if (toggle.isOn()) {
            animation.initValue(1.0);
            return;
        }
        animation.initValue(0.0);
    }

    @Override
    public void toggle() {
        this.toggle.toggle();
        if (this.toggle.isOn()) {
            this.animation.setValue(1.0);
            return;
        }
        this.animation.setValue(0.0);
    }

    @Override
    public boolean isOn() {
        return this.toggle.isOn();
    }

    public double getValue() {
        if (this.animation.getTarget() == (double)this.toggle.isOn()) return this.animation.getValue();
        if (this.toggle.isOn()) {
            this.animation.setValue(1.0);
            return this.animation.getValue();
        }
        this.animation.setValue(0.0);
        return this.animation.getValue();
    }
}

