/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio.settings;

import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.FocusableComponent;
import com.lukflug.panelstudio.settings.KeybindSetting;
import com.lukflug.panelstudio.theme.Renderer;

public class KeybindComponent
extends FocusableComponent {
    protected KeybindSetting keybind;

    public KeybindComponent(Renderer renderer, KeybindSetting keybind) {
        super("Keybind: \u00a77", null, renderer);
        this.keybind = keybind;
    }

    @Override
    public void render(Context context) {
        super.render(context);
        String text = this.title + this.keybind.getKeyName();
        if (this.hasFocus(context)) {
            text = this.title + "...";
        }
        this.renderer.renderTitle(context, text, this.hasFocus(context));
    }

    @Override
    public void handleButton(Context context, int button) {
        super.handleButton(context, button);
        context.setHeight(this.renderer.getHeight(false));
        boolean isSelected = this.hasFocus(context);
        super.handleButton(context, button);
        if (!isSelected) return;
        if (this.hasFocus(context)) return;
        this.keybind.setKey(0);
    }

    @Override
    public void handleKey(Context context, int scancode) {
        super.handleKey(context, scancode);
        if (!this.hasFocus(context)) return;
        this.keybind.setKey(scancode);
        this.releaseFocus();
    }

    @Override
    public void exit(Context context) {
        super.exit(context);
        if (!this.hasFocus(context)) return;
        this.keybind.setKey(0);
        this.releaseFocus();
    }
}

