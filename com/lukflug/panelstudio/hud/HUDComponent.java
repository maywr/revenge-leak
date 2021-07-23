/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio.hud;

import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.FixedComponent;
import com.lukflug.panelstudio.Interface;
import com.lukflug.panelstudio.PanelConfig;
import com.lukflug.panelstudio.theme.Renderer;
import java.awt.Point;

public abstract class HUDComponent
implements FixedComponent {
    protected String title;
    protected Renderer renderer;
    protected Point position;

    public HUDComponent(String title, Renderer renderer, Point position) {
        this.title = title;
        this.renderer = renderer;
        this.position = position;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public void render(Context context) {
        this.getHeight(context);
    }

    @Override
    public void handleButton(Context context, int button) {
        this.getHeight(context);
    }

    @Override
    public void handleKey(Context context, int scancode) {
        this.getHeight(context);
    }

    @Override
    public void handleScroll(Context context, int diff) {
        this.getHeight(context);
    }

    @Override
    public void enter(Context context) {
        this.getHeight(context);
    }

    @Override
    public void exit(Context context) {
        this.getHeight(context);
    }

    @Override
    public void releaseFocus() {
    }

    @Override
    public Point getPosition(Interface inter) {
        return new Point(this.position);
    }

    @Override
    public void setPosition(Interface inter, Point position) {
        this.position = position;
    }

    @Override
    public void saveConfig(Interface inter, PanelConfig config) {
        config.savePositon(this.position);
    }

    @Override
    public void loadConfig(Interface inter, PanelConfig config) {
        Point pos = config.loadPosition();
        if (pos == null) return;
        this.position = pos;
    }
}

