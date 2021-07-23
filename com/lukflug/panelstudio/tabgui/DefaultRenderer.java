/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio.tabgui;

import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.Interface;
import com.lukflug.panelstudio.tabgui.TabGUIRenderer;
import com.lukflug.panelstudio.theme.ColorScheme;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public class DefaultRenderer
implements TabGUIRenderer {
    protected ColorScheme scheme;
    protected int height;
    protected int border;
    protected int up;
    protected int down;
    protected int left;
    protected int right;
    protected int enter;

    public DefaultRenderer(ColorScheme scheme, int height, int border, int up, int down, int left, int right, int enter) {
        this.scheme = scheme;
        this.border = border;
        this.height = height;
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
        this.enter = enter;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getBorder() {
        return this.border;
    }

    @Override
    public void renderBackground(Context context, int offset, int height) {
        Color bgcolor = this.scheme.getBackgroundColor();
        bgcolor = new Color(bgcolor.getRed(), bgcolor.getGreen(), bgcolor.getBlue(), this.scheme.getOpacity());
        Color border = this.scheme.getOutlineColor();
        Color active = this.scheme.getActiveColor();
        context.getInterface().fillRect(context.getRect(), bgcolor, bgcolor, bgcolor, bgcolor);
        context.getInterface().drawRect(context.getRect(), border, border, border, border);
        Point p = context.getPos();
        p.translate(0, offset);
        Rectangle rect = new Rectangle(p, new Dimension(context.getSize().width, height));
        context.getInterface().fillRect(rect, active, active, active, active);
        context.getInterface().drawRect(rect, border, border, border, border);
    }

    @Override
    public void renderCaption(Context context, String caption, int index, int height, boolean active) {
        Color color = active ? this.scheme.getActiveColor() : this.scheme.getFontColor();
        Point p = context.getPos();
        p.translate(0, index * height);
        context.getInterface().drawString(p, caption, color);
    }

    @Override
    public ColorScheme getColorScheme() {
        return this.scheme;
    }

    @Override
    public boolean isUpKey(int key) {
        if (key != this.up) return false;
        return true;
    }

    @Override
    public boolean isDownKey(int key) {
        if (key != this.down) return false;
        return true;
    }

    @Override
    public boolean isSelectKey(int key) {
        if (key == this.right) return true;
        if (key == this.enter) return true;
        return false;
    }

    @Override
    public boolean isEscapeKey(int key) {
        if (key != this.left) return false;
        return true;
    }
}

