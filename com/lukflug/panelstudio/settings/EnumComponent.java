/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio.settings;

import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.FocusableComponent;
import com.lukflug.panelstudio.settings.EnumSetting;
import com.lukflug.panelstudio.theme.Renderer;

public class EnumComponent
extends FocusableComponent {
    protected EnumSetting setting;

    public EnumComponent(String title, String description, Renderer renderer, EnumSetting setting) {
        super(title, description, renderer);
        this.setting = setting;
    }

    @Override
    public void render(Context context) {
        super.render(context);
        this.renderer.renderTitle(context, this.title + ": \u00a77" + this.setting.getValueName(), this.hasFocus(context));
    }

    @Override
    public void handleButton(Context context, int button) {
        super.handleButton(context, button);
        if (button != 0) return;
        if (!context.isClicked()) return;
        this.setting.increment();
    }
}

