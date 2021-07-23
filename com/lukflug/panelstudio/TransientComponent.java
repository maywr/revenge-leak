/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio;

import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.FixedComponent;
import com.lukflug.panelstudio.FocusableComponent;
import com.lukflug.panelstudio.Interface;
import com.lukflug.panelstudio.PanelManager;
import com.lukflug.panelstudio.settings.Toggleable;
import com.lukflug.panelstudio.theme.Renderer;

public class TransientComponent
extends FocusableComponent {
    protected Toggleable toggle;
    protected FixedComponent component;
    protected PanelManager manager;

    public TransientComponent(String title, String description, Renderer renderer, Toggleable toggle, FixedComponent component, PanelManager manager) {
        super(title, description, renderer);
        this.toggle = toggle;
        this.component = component;
        this.manager = manager;
    }

    @Override
    public void render(Context context) {
        super.render(context);
        this.renderer.renderTitle(context, this.title, this.hasFocus(context), this.toggle.isOn(), this.manager.getComponentToggleable(this.component).isOn());
    }

    @Override
    public void handleButton(Context context, int button) {
        super.handleButton(context, button);
        if (button == 0 && context.isClicked()) {
            this.toggle.toggle();
            return;
        }
        if (!context.isHovered()) return;
        if (button != 1) return;
        if (!context.getInterface().getButton(1)) return;
        this.manager.getComponentToggleable(this.component).toggle();
        context.releaseFocus();
    }
}

