/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio;

import com.lukflug.panelstudio.Animation;
import com.lukflug.panelstudio.CollapsibleContainer;
import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.FixedComponent;
import com.lukflug.panelstudio.Interface;
import com.lukflug.panelstudio.PanelConfig;
import com.lukflug.panelstudio.settings.AnimatedToggleable;
import com.lukflug.panelstudio.settings.Toggleable;
import com.lukflug.panelstudio.theme.Renderer;
import java.awt.Point;

public class DraggableContainer
extends CollapsibleContainer
implements FixedComponent {
    protected boolean dragging = false;
    protected Point attachPoint;
    protected Point position;
    protected int width;
    protected boolean bodyDrag = false;

    public DraggableContainer(String title, String description, Renderer renderer, Toggleable open, Animation animation, Toggleable toggle, Point position, int width) {
        super(title, description, renderer, open, animation, toggle);
        this.position = position;
        this.width = width;
    }

    @Override
    public void handleButton(Context context, int button) {
        if (this.bodyDrag) {
            super.handleButton(context, button);
        } else {
            context.setHeight(this.renderer.getHeight(this.open.getValue() != 0.0));
        }
        if (context.isClicked() && button == 0) {
            this.dragging = true;
            this.attachPoint = context.getInterface().getMouse();
        } else if (!context.getInterface().getButton(0) && this.dragging) {
            Point mouse = context.getInterface().getMouse();
            this.dragging = false;
            Point p = this.getPosition(context.getInterface());
            p.translate(mouse.x - this.attachPoint.x, mouse.y - this.attachPoint.y);
            this.setPosition(context.getInterface(), p);
        }
        if (this.bodyDrag) return;
        super.handleButton(context, button);
    }

    @Override
    public Point getPosition(Interface inter) {
        if (!this.dragging) return this.position;
        Point point = new Point(this.position);
        point.translate(inter.getMouse().x - this.attachPoint.x, inter.getMouse().y - this.attachPoint.y);
        return point;
    }

    @Override
    public void setPosition(Interface inter, Point position) {
        this.position = new Point(position);
    }

    @Override
    public int getWidth(Interface inter) {
        return this.width;
    }

    @Override
    protected void handleFocus(Context context, boolean focus) {
        if (!focus) return;
        context.requestFocus();
    }

    @Override
    public void saveConfig(Interface inter, PanelConfig config) {
        config.savePositon(this.position);
        config.saveState(this.open.isOn());
    }

    @Override
    public void loadConfig(Interface inter, PanelConfig config) {
        Point pos = config.loadPosition();
        if (pos != null) {
            this.position = pos;
        }
        if (this.open.isOn() == config.loadState()) return;
        this.open.toggle();
    }
}

