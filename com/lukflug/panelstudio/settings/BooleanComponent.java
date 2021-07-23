/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio.settings;

import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.FocusableComponent;
import com.lukflug.panelstudio.settings.Toggleable;
import com.lukflug.panelstudio.theme.Renderer;

public class BooleanComponent
extends FocusableComponent {
    protected Toggleable setting;

    public BooleanComponent(String title, String description, Renderer renderer, Toggleable setting) {
        super(title, description, renderer);
        this.setting = setting;
    }

    @Override
    public void render(Context context) {
        super.render(context);
        this.renderer.renderTitle(context, this.title, this.hasFocus(context), this.setting.isOn());
    }

    @Override
    public void handleButton(Context context, int button) {
        super.handleButton(context, button);
        if (button != 0) return;
        if (!context.isClicked()) return;
        this.setting.toggle();
    }
}

