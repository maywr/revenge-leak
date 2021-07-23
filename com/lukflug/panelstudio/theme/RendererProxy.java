/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.theme.ColorScheme;
import com.lukflug.panelstudio.theme.Renderer;
import java.awt.Color;
import java.awt.Rectangle;

public abstract class RendererProxy
implements Renderer {
    @Override
    public int getHeight(boolean open) {
        return this.getRenderer().getHeight(open);
    }

    @Override
    public int getOffset() {
        return this.getRenderer().getOffset();
    }

    @Override
    public int getBorder() {
        return this.getRenderer().getBorder();
    }

    @Override
    public int getBottomBorder() {
        return this.getRenderer().getBottomBorder();
    }

    @Override
    public int getLeftBorder(boolean scroll) {
        return this.getRenderer().getLeftBorder(scroll);
    }

    @Override
    public int getRightBorder(boolean scroll) {
        return this.getRenderer().getRightBorder(scroll);
    }

    @Override
    public void renderTitle(Context context, String text, boolean focus) {
        this.getRenderer().renderTitle(context, text, focus);
    }

    @Override
    public void renderTitle(Context context, String text, boolean focus, boolean active) {
        this.getRenderer().renderTitle(context, text, focus, active);
    }

    @Override
    public void renderTitle(Context context, String text, boolean focus, boolean active, boolean open) {
        this.getRenderer().renderTitle(context, text, focus, active, open);
    }

    @Override
    public void renderRect(Context context, String text, boolean focus, boolean active, Rectangle rectangle, boolean overlay) {
        this.getRenderer().renderRect(context, text, focus, active, rectangle, overlay);
    }

    @Override
    public void renderBackground(Context context, boolean focus) {
        this.getRenderer().renderBackground(context, focus);
    }

    @Override
    public void renderBorder(Context context, boolean focus, boolean active, boolean open) {
        this.getRenderer().renderBorder(context, focus, active, open);
    }

    @Override
    public int renderScrollBar(Context context, boolean focus, boolean active, boolean scroll, int childHeight, int scrollPosition) {
        return this.getRenderer().renderScrollBar(context, focus, active, scroll, childHeight, scrollPosition);
    }

    @Override
    public Color getMainColor(boolean focus, boolean active) {
        return this.getRenderer().getMainColor(focus, active);
    }

    @Override
    public Color getBackgroundColor(boolean focus) {
        return this.getRenderer().getBackgroundColor(focus);
    }

    @Override
    public Color getFontColor(boolean focus) {
        return this.getRenderer().getFontColor(focus);
    }

    @Override
    public ColorScheme getDefaultColorScheme() {
        return this.getRenderer().getDefaultColorScheme();
    }

    @Override
    public void overrideColorScheme(ColorScheme scheme) {
        this.getRenderer().overrideColorScheme(scheme);
    }

    @Override
    public void restoreColorScheme() {
        this.getRenderer().restoreColorScheme();
    }

    protected abstract Renderer getRenderer();
}

