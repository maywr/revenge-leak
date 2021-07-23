/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio.settings;

import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.Slider;
import com.lukflug.panelstudio.settings.NumberSetting;
import com.lukflug.panelstudio.theme.Renderer;

public class NumberComponent
extends Slider {
    protected NumberSetting setting;
    protected String text;

    public NumberComponent(String text, String description, Renderer renderer, NumberSetting setting, double min, double max) {
        super("", description, renderer);
        this.setting = setting;
        this.text = text;
    }

    @Override
    public void render(Context context) {
        this.title = this.setting.getPrecision() == 0 ? String.format("%s: \u00a77%d", this.text, (int)this.setting.getNumber()) : String.format("%s: \u00a77%." + this.setting.getPrecision() + "f", this.text, this.setting.getNumber());
        super.render(context);
    }

    @Override
    protected double getValue() {
        return (this.setting.getNumber() - this.setting.getMinimumValue()) / (this.setting.getMaximumValue() - this.setting.getMinimumValue());
    }

    @Override
    protected void setValue(double value) {
        this.setting.setNumber(value * (this.setting.getMaximumValue() - this.setting.getMinimumValue()) + this.setting.getMinimumValue());
    }
}

