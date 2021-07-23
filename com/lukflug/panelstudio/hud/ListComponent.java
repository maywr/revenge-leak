/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio.hud;

import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.Interface;
import com.lukflug.panelstudio.PanelConfig;
import com.lukflug.panelstudio.hud.HUDComponent;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.theme.Renderer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

public class ListComponent
extends HUDComponent {
    protected HUDList list;
    protected boolean lastUp = false;
    protected boolean lastRight = false;

    public ListComponent(String name, Renderer renderer, Point position, HUDList list) {
        super(name, renderer, position);
        this.list = list;
    }

    @Override
    public void render(Context context) {
        super.render(context);
        int i = 0;
        while (i < this.list.getSize()) {
            String s = this.list.getItem(i);
            Point p = context.getPos();
            if (this.list.sortUp()) {
                p.translate(0, context.getSize().height - (i + 1) * context.getInterface().getFontHeight());
            } else {
                p.translate(0, i * context.getInterface().getFontHeight());
            }
            if (this.list.sortRight()) {
                p.translate(this.getWidth(context.getInterface()) - context.getInterface().getFontWidth(s), 0);
            }
            context.getInterface().drawString(p, s, this.list.getItemColor(i));
            ++i;
        }
    }

    @Override
    public Point getPosition(Interface inter) {
        int width = this.getWidth(inter);
        int height = this.renderer.getHeight(false) + (this.list.getSize() - 1) * inter.getFontHeight();
        if (this.lastUp != this.list.sortUp()) {
            if (this.list.sortUp()) {
                this.position.translate(0, height);
            } else {
                this.position.translate(0, -height);
            }
            this.lastUp = this.list.sortUp();
        }
        if (this.lastRight != this.list.sortRight()) {
            if (this.list.sortRight()) {
                this.position.translate(width, 0);
            } else {
                this.position.translate(-width, 0);
            }
            this.lastRight = this.list.sortRight();
        }
        if (this.list.sortUp()) {
            if (!this.list.sortRight()) return new Point(this.position.x, this.position.y - height);
            return new Point(this.position.x - width, this.position.y - height);
        }
        if (!this.list.sortRight()) return new Point(this.position);
        return new Point(new Point(this.position.x - width, this.position.y));
    }

    @Override
    public void setPosition(Interface inter, Point position) {
        int width = this.getWidth(inter);
        int height = this.renderer.getHeight(false) + (this.list.getSize() - 1) * inter.getFontHeight();
        if (this.list.sortUp()) {
            if (this.list.sortRight()) {
                this.position = new Point(position.x + width, position.y + height);
                return;
            }
            this.position = new Point(position.x, position.y + height);
            return;
        }
        if (this.list.sortRight()) {
            this.position = new Point(position.x + width, position.y);
            return;
        }
        this.position = new Point(position);
    }

    @Override
    public int getWidth(Interface inter) {
        int width = inter.getFontWidth(this.getTitle());
        int i = 0;
        while (i < this.list.getSize()) {
            String s = this.list.getItem(i);
            width = Math.max(width, inter.getFontWidth(s));
            ++i;
        }
        return width;
    }

    @Override
    public void getHeight(Context context) {
        context.setHeight(this.renderer.getHeight(false) + (this.list.getSize() - 1) * context.getInterface().getFontHeight());
    }

    @Override
    public void loadConfig(Interface inter, PanelConfig config) {
        super.loadConfig(inter, config);
        this.lastUp = this.list.sortUp();
        this.lastRight = this.list.sortRight();
    }
}

