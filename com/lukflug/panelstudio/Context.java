/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio;

import com.lukflug.panelstudio.Interface;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public final class Context {
    private Interface inter;
    private Dimension size;
    private Point position;
    private boolean focus;
    private boolean onTop;
    private boolean focusRequested = false;
    private boolean focusOverride = false;
    private String description = null;

    public Context(Context context, int left, int right, int offset, boolean focus, boolean onTop) {
        this.inter = context.getInterface();
        this.size = new Dimension(context.getSize().width - left - right, 0);
        this.position = new Point(context.getPos());
        this.position.translate(left, offset);
        this.focus = context.hasFocus() && focus;
        this.onTop = context.onTop() && onTop;
    }

    public Context(Interface inter, int width, Point position, boolean focus, boolean onTop) {
        this.inter = inter;
        this.size = new Dimension(width, 0);
        this.position = new Point(position);
        this.focus = focus;
        this.onTop = onTop;
    }

    public Interface getInterface() {
        return this.inter;
    }

    public Dimension getSize() {
        return new Dimension(this.size);
    }

    public void setHeight(int height) {
        this.size.height = height;
    }

    public Point getPos() {
        return new Point(this.position);
    }

    public boolean hasFocus() {
        return this.focus;
    }

    public boolean onTop() {
        return this.onTop;
    }

    public void requestFocus() {
        this.focusRequested = true;
    }

    public void releaseFocus() {
        this.focusRequested = false;
        this.focusOverride = true;
    }

    public boolean foucsRequested() {
        return this.focusRequested;
    }

    public boolean focusReleased() {
        return this.focusOverride;
    }

    public boolean isHovered() {
        if (!new Rectangle(this.position, this.size).contains(this.inter.getMouse())) return false;
        if (!this.onTop) return false;
        return true;
    }

    public boolean isClicked() {
        if (!this.isHovered()) return false;
        if (!this.inter.getButton(0)) return false;
        return true;
    }

    public Rectangle getRect() {
        return new Rectangle(this.position, this.size);
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

