/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio.settings;

import com.lukflug.panelstudio.Animation;
import com.lukflug.panelstudio.CollapsibleContainer;
import com.lukflug.panelstudio.Component;
import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.FocusableComponent;
import com.lukflug.panelstudio.Slider;
import com.lukflug.panelstudio.settings.ColorSetting;
import com.lukflug.panelstudio.settings.SimpleToggleable;
import com.lukflug.panelstudio.settings.Toggleable;
import com.lukflug.panelstudio.theme.ColorScheme;
import com.lukflug.panelstudio.theme.Renderer;
import java.awt.Color;

public class ColorComponent
extends CollapsibleContainer {
    protected ColorSetting setting;
    protected final boolean alpha;
    protected final boolean rainbow;
    protected ColorScheme scheme;
    protected ColorScheme overrideScheme;
    protected Toggleable colorModel;

    public ColorComponent(String title, String description, Renderer renderer, Animation animation, Renderer componentRenderer, ColorSetting setting, boolean alpha, boolean rainbow, Toggleable colorModel) {
        super(title, description, renderer, new SimpleToggleable(false), animation, null);
        this.setting = setting;
        this.alpha = alpha;
        this.rainbow = rainbow;
        this.scheme = new ColorSettingScheme(renderer);
        this.overrideScheme = new ColorSettingScheme(componentRenderer);
        this.colorModel = colorModel;
        if (rainbow) {
            this.addComponent(new ColorButton(componentRenderer));
        }
        this.addComponent(new ColorSlider(componentRenderer, 0));
        this.addComponent(new ColorSlider(componentRenderer, 1));
        this.addComponent(new ColorSlider(componentRenderer, 2));
        if (!alpha) return;
        this.addComponent(new ColorSlider(componentRenderer, 3));
    }

    @Override
    public void render(Context context) {
        this.renderer.overrideColorScheme(this.scheme);
        super.render(context);
        this.renderer.restoreColorScheme();
    }

    protected class ColorSettingScheme
    implements ColorScheme {
        ColorScheme scheme;

        public ColorSettingScheme(Renderer renderer) {
            this.scheme = renderer.getDefaultColorScheme();
        }

        @Override
        public Color getActiveColor() {
            return ColorComponent.this.setting.getValue();
        }

        @Override
        public Color getInactiveColor() {
            return this.scheme.getInactiveColor();
        }

        @Override
        public Color getBackgroundColor() {
            return this.scheme.getBackgroundColor();
        }

        @Override
        public Color getOutlineColor() {
            return this.scheme.getOutlineColor();
        }

        @Override
        public Color getFontColor() {
            return this.scheme.getFontColor();
        }

        @Override
        public int getOpacity() {
            return this.scheme.getOpacity();
        }
    }

    protected class ColorSlider
    extends Slider {
        private final int value;

        public ColorSlider(Renderer renderer, int value) {
            super("", null, renderer);
            this.value = value;
        }

        @Override
        public void render(Context context) {
            this.title = this.getTitle(this.value) + (int)((double)this.getMax() * this.getValue());
            this.renderer.overrideColorScheme(ColorComponent.this.overrideScheme);
            super.render(context);
            this.renderer.restoreColorScheme();
        }

        @Override
        protected double getValue() {
            Color c = ColorComponent.this.setting.getColor();
            if (this.value >= 3) return (double)c.getAlpha() / 255.0;
            if (ColorComponent.this.colorModel.isOn()) {
                return Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null)[this.value];
            }
            switch (this.value) {
                case 0: {
                    return (double)c.getRed() / 255.0;
                }
                case 1: {
                    return (double)c.getGreen() / 255.0;
                }
                case 2: {
                    return (double)c.getBlue() / 255.0;
                }
            }
            return (double)c.getAlpha() / 255.0;
        }

        @Override
        protected void setValue(double value) {
            Color c = ColorComponent.this.setting.getColor();
            float[] color = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
            switch (this.value) {
                case 0: {
                    c = ColorComponent.this.colorModel.isOn() ? Color.getHSBColor((float)value, color[1], color[2]) : new Color((int)(255.0 * value), c.getGreen(), c.getBlue());
                    if (ColorComponent.this.alpha) {
                        ColorComponent.this.setting.setValue(new Color(c.getRed(), c.getGreen(), c.getBlue(), ColorComponent.this.setting.getColor().getAlpha()));
                        return;
                    }
                    ColorComponent.this.setting.setValue(c);
                    return;
                }
                case 1: {
                    c = ColorComponent.this.colorModel.isOn() ? Color.getHSBColor(color[0], (float)value, color[2]) : new Color(c.getRed(), (int)(255.0 * value), c.getBlue());
                    if (ColorComponent.this.alpha) {
                        ColorComponent.this.setting.setValue(new Color(c.getRed(), c.getGreen(), c.getBlue(), ColorComponent.this.setting.getColor().getAlpha()));
                        return;
                    }
                    ColorComponent.this.setting.setValue(c);
                    return;
                }
                case 2: {
                    c = ColorComponent.this.colorModel.isOn() ? Color.getHSBColor(color[0], color[1], (float)value) : new Color(c.getRed(), c.getGreen(), (int)(255.0 * value));
                    if (ColorComponent.this.alpha) {
                        ColorComponent.this.setting.setValue(new Color(c.getRed(), c.getGreen(), c.getBlue(), ColorComponent.this.setting.getColor().getAlpha()));
                        return;
                    }
                    ColorComponent.this.setting.setValue(c);
                    return;
                }
                case 3: {
                    ColorComponent.this.setting.setValue(new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)(255.0 * value)));
                }
            }
        }

        protected String getTitle(int value) {
            switch (value) {
                case 0: {
                    String string;
                    if (ColorComponent.this.colorModel.isOn()) {
                        string = "Hue:";
                        return string + " \u00a77";
                    }
                    string = "Red:";
                    return string + " \u00a77";
                }
                case 1: {
                    String string;
                    if (ColorComponent.this.colorModel.isOn()) {
                        string = "Saturation:";
                        return string + " \u00a77";
                    }
                    string = "Green:";
                    return string + " \u00a77";
                }
                case 2: {
                    String string;
                    if (ColorComponent.this.colorModel.isOn()) {
                        string = "Brightness:";
                        return string + " \u00a77";
                    }
                    string = "Blue:";
                    return string + " \u00a77";
                }
                case 3: {
                    return "Alpha: \u00a77";
                }
            }
            return "";
        }

        protected int getMax() {
            if (!ColorComponent.this.colorModel.isOn()) {
                return 255;
            }
            if (this.value == 0) {
                return 360;
            }
            if (this.value >= 3) return 255;
            return 100;
        }
    }

    protected class ColorButton
    extends FocusableComponent {
        public ColorButton(Renderer renderer) {
            super("Rainbow", null, renderer);
        }

        @Override
        public void render(Context context) {
            super.render(context);
            this.renderer.overrideColorScheme(ColorComponent.this.overrideScheme);
            this.renderer.renderTitle(context, this.title, this.hasFocus(context), ColorComponent.this.setting.getRainbow());
            this.renderer.restoreColorScheme();
        }

        @Override
        public void handleButton(Context context, int button) {
            super.handleButton(context, button);
            if (button != 0) return;
            if (!context.isClicked()) return;
            ColorComponent.this.setting.setRainbow(!ColorComponent.this.setting.getRainbow());
        }
    }

}

