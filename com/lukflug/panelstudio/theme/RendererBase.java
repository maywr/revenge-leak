/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.theme.ColorScheme;
import com.lukflug.panelstudio.theme.Renderer;
import java.awt.Color;
import java.awt.Rectangle;

public abstract class RendererBase
implements Renderer {
    protected final int height;
    protected final int offset;
    protected final int border;
    protected final int left;
    protected final int right;
    protected ColorScheme scheme = null;

    public RendererBase(int height, int offset, int border, int left, int right) {
        this.height = height;
        this.offset = offset;
        this.border = border;
        this.left = left;
        this.right = right;
    }

    @Override
    public int getHeight(boolean open) {
        return this.height;
    }

    @Override
    public int getOffset() {
        return this.offset;
    }

    @Override
    public int getBorder() {
        return this.border;
    }

    @Override
    public int getBottomBorder() {
        return 0;
    }

    @Override
    public int getLeftBorder(boolean scroll) {
        if (!scroll) return 0;
        return this.left;
    }

    @Override
    public int getRightBorder(boolean scroll) {
        if (!scroll) return 0;
        return this.right;
    }

    @Override
    public void renderTitle(Context context, String text, boolean focus) {
        this.renderTitle(context, text, focus, false);
    }

    @Override
    public void renderTitle(Context context, String text, boolean focus, boolean active) {
        this.renderRect(context, text, focus, active, context.getRect(), true);
    }

    @Override
    public void renderTitle(Context context, String text, boolean focus, boolean active, boolean open) {
        this.renderTitle(context, text, focus, active);
    }

    @Override
    public int renderScrollBar(Context context, boolean focus, boolean active, boolean scroll, int childHeight, int scrollPosition) {
        return scrollPosition;
    }

    @Override
    public Color getFontColor(boolean focus) {
        return this.getColorScheme().getFontColor();
    }

    @Override
    public void overrideColorScheme(ColorScheme scheme) {
        this.scheme = scheme;
    }

    @Override
    public void restoreColorScheme() {
        this.scheme = null;
    }

    protected ColorScheme getColorScheme() {
        if (this.scheme != null) return this.scheme;
        return this.getDefaultColorScheme();
    }
}

