/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio.hud;

import com.lukflug.panelstudio.Animation;
import com.lukflug.panelstudio.Component;
import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.DraggableContainer;
import com.lukflug.panelstudio.FixedComponent;
import com.lukflug.panelstudio.Interface;
import com.lukflug.panelstudio.PanelConfig;
import com.lukflug.panelstudio.settings.AnimatedToggleable;
import com.lukflug.panelstudio.settings.Toggleable;
import com.lukflug.panelstudio.theme.Renderer;
import com.lukflug.panelstudio.theme.RendererProxy;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

public class HUDPanel
extends DraggableContainer {
    protected Toggleable guiOpen;
    protected FixedComponent component;

    public HUDPanel(FixedComponent component, Renderer renderer, Toggleable open, Animation animation, Toggleable guiOpen, int minBorder) {
        super(component.getTitle(), null, new HUDRenderer(renderer, guiOpen, minBorder), open, animation, null, new Point(0, 0), 0);
        this.addComponent(component);
        this.guiOpen = guiOpen;
        this.component = component;
        this.bodyDrag = true;
    }

    @Override
    public void handleButton(Context context, int button) {
        if (!this.guiOpen.isOn()) return;
        super.handleButton(context, button);
    }

    @Override
    public void handleScroll(Context context, int diff) {
        if (!this.guiOpen.isOn()) return;
        super.handleScroll(context, diff);
    }

    @Override
    public Point getPosition(Interface inter) {
        this.position = this.component.getPosition(inter);
        this.position.translate(0, -this.renderer.getHeight(this.open.getValue() != 0.0) - this.renderer.getOffset());
        return super.getPosition(inter);
    }

    @Override
    public void setPosition(Interface inter, Point position) {
        this.component.setPosition(inter, new Point(position.x, position.y + this.renderer.getHeight(this.open.getValue() != 0.0) + this.renderer.getOffset()));
    }

    @Override
    public int getWidth(Interface inter) {
        return this.component.getWidth(inter) + this.renderer.getBorder() * 2 + this.renderer.getLeftBorder(this.scroll) + this.renderer.getRightBorder(this.scroll);
    }

    @Override
    protected Rectangle getClipRect(Context context, int height) {
        if (this.open.getValue() == 1.0) return null;
        return super.getClipRect(context, height);
    }

    @Override
    public void saveConfig(Interface inter, PanelConfig config) {
        this.component.saveConfig(inter, config);
        config.saveState(this.open.isOn());
    }

    @Override
    public void loadConfig(Interface inter, PanelConfig config) {
        this.component.loadConfig(inter, config);
        if (this.open.isOn() == config.loadState()) return;
        this.open.toggle();
    }

    protected static class HUDRenderer
    extends RendererProxy {
        Renderer renderer;
        protected Toggleable guiOpen;
        protected int minBorder;

        public HUDRenderer(Renderer renderer, Toggleable guiOpen, int minBorder) {
            this.renderer = renderer;
            this.guiOpen = guiOpen;
            this.minBorder = minBorder;
        }

        @Override
        public int getOffset() {
            return Math.max(this.renderer.getOffset(), this.minBorder);
        }

        @Override
        public int getBorder() {
            return Math.max(this.renderer.getBorder(), this.minBorder);
        }

        @Override
        public void renderTitle(Context context, String text, boolean focus) {
            if (!this.guiOpen.isOn()) return;
            this.renderer.renderTitle(context, text, focus);
        }

        @Override
        public void renderTitle(Context context, String text, boolean focus, boolean active) {
            if (!this.guiOpen.isOn()) return;
            this.renderer.renderTitle(context, text, focus, active);
        }

        @Override
        public void renderTitle(Context context, String text, boolean focus, boolean active, boolean open) {
            if (!this.guiOpen.isOn()) return;
            this.renderer.renderTitle(context, text, focus, open);
        }

        @Override
        public void renderRect(Context context, String text, boolean focus, boolean active, Rectangle rectangle, boolean overlay) {
            if (!this.guiOpen.isOn()) return;
            this.renderer.renderRect(context, text, focus, active, rectangle, overlay);
        }

        @Override
        public void renderBackground(Context context, boolean focus) {
            if (!this.guiOpen.isOn()) return;
            this.renderer.renderBackground(context, focus);
        }

        @Override
        public void renderBorder(Context context, boolean focus, boolean active, boolean open) {
            if (!this.guiOpen.isOn()) return;
            this.renderer.renderBorder(context, focus, active, open);
        }

        @Override
        public int renderScrollBar(Context context, boolean focus, boolean active, boolean scroll, int childHeight, int scrollPosition) {
            if (!this.guiOpen.isOn()) return scrollPosition;
            return this.renderer.renderScrollBar(context, focus, active, scroll, childHeight, scrollPosition);
        }

        @Override
        public Color getMainColor(boolean focus, boolean active) {
            if (!this.guiOpen.isOn()) return new Color(0, 0, 0, 0);
            return this.renderer.getMainColor(focus, active);
        }

        @Override
        public Color getBackgroundColor(boolean focus) {
            if (!this.guiOpen.isOn()) return new Color(0, 0, 0, 0);
            return this.renderer.getBackgroundColor(focus);
        }

        @Override
        public Color getFontColor(boolean focus) {
            if (!this.guiOpen.isOn()) return new Color(0, 0, 0, 0);
            return this.renderer.getFontColor(focus);
        }

        @Override
        protected Renderer getRenderer() {
            return this.renderer;
        }
    }

}

