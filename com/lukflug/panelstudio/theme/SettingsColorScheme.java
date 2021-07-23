/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.settings.ColorSetting;
import com.lukflug.panelstudio.settings.NumberSetting;
import com.lukflug.panelstudio.theme.ColorScheme;
import java.awt.Color;

public class SettingsColorScheme
implements ColorScheme {
    protected final ColorSetting activeColor;
    protected final ColorSetting inactiveColor;
    protected final ColorSetting backgroundColor;
    protected final ColorSetting outlineColor;
    protected final ColorSetting fontColor;
    protected final NumberSetting opacity;

    public SettingsColorScheme(ColorSetting activeColor, ColorSetting inactiveColor, ColorSetting backgroundColor, ColorSetting outlineColor, ColorSetting fontColor, NumberSetting opacity) {
        this.activeColor = activeColor;
        this.inactiveColor = inactiveColor;
        this.backgroundColor = backgroundColor;
        this.outlineColor = outlineColor;
        this.fontColor = fontColor;
        this.opacity = opacity;
    }

    @Override
    public Color getActiveColor() {
        return this.activeColor.getValue();
    }

    @Override
    public Color getInactiveColor() {
        return this.inactiveColor.getValue();
    }

    @Override
    public Color getBackgroundColor() {
        return this.backgroundColor.getValue();
    }

    @Override
    public Color getOutlineColor() {
        return this.outlineColor.getValue();
    }

    @Override
    public Color getFontColor() {
        return this.fontColor.getValue();
    }

    @Override
    public int getOpacity() {
        return (int)this.opacity.getNumber();
    }
}

