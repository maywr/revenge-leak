/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio;

import com.lukflug.panelstudio.Component;
import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.Interface;
import com.lukflug.panelstudio.theme.Renderer;

public class FocusableComponent
implements Component {
    protected String title;
    protected String description;
    protected Renderer renderer;
    private boolean focus = false;

    public FocusableComponent(String title, String description, Renderer renderer) {
        this.title = title;
        this.renderer = renderer;
        this.description = description;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public void render(Context context) {
        context.setHeight(this.renderer.getHeight(false));
        context.setDescription(this.description);
    }

    @Override
    public void handleKey(Context context, int scancode) {
        context.setHeight(this.renderer.getHeight(false));
    }

    @Override
    public void handleButton(Context context, int button) {
        context.setHeight(this.renderer.getHeight(false));
        this.updateFocus(context, button);
    }

    @Override
    public void getHeight(Context context) {
        context.setHeight(this.renderer.getHeight(false));
    }

    @Override
    public void handleScroll(Context context, int diff) {
        context.setHeight(this.renderer.getHeight(false));
    }

    @Override
    public void enter(Context context) {
        context.setHeight(this.renderer.getHeight(false));
    }

    @Override
    public void exit(Context context) {
        context.setHeight(this.renderer.getHeight(false));
    }

    public boolean hasFocus(Context context) {
        if (!context.hasFocus()) return false;
        if (!this.focus) return false;
        return true;
    }

    @Override
    public void releaseFocus() {
        this.focus = false;
    }

    protected void updateFocus(Context context, int button) {
        if (!context.getInterface().getButton(button)) return;
        this.focus = context.isHovered();
        this.handleFocus(context, this.focus && context.hasFocus());
    }

    protected void handleFocus(Context context, boolean focus) {
    }
}

