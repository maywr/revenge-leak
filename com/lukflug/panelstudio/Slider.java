/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio;

import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.FocusableComponent;
import com.lukflug.panelstudio.Interface;
import com.lukflug.panelstudio.theme.Renderer;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public abstract class Slider
extends FocusableComponent {
    protected boolean attached = false;

    public Slider(String title, String description, Renderer renderer) {
        super(title, description, renderer);
    }

    @Override
    public void render(Context context) {
        super.render(context);
        if (this.attached) {
            double value = (double)(context.getInterface().getMouse().x - context.getPos().x) / (double)(context.getSize().width - 1);
            if (value < 0.0) {
                value = 0.0;
            } else if (value > 1.0) {
                value = 1.0;
            }
            this.setValue(value);
        }
        if (!context.getInterface().getButton(0)) {
            this.attached = false;
        }
        this.renderer.renderRect(context, "", this.hasFocus(context), false, new Rectangle(new Point(context.getPos().x + (int)((double)context.getSize().width * this.getValue()), context.getPos().y), new Dimension((int)((double)context.getSize().width * (1.0 - this.getValue())), this.renderer.getHeight(false))), false);
        this.renderer.renderRect(context, this.title, this.hasFocus(context), true, new Rectangle(context.getPos(), new Dimension((int)((double)context.getSize().width * this.getValue()), this.renderer.getHeight(false))), true);
    }

    @Override
    public void handleButton(Context context, int button) {
        super.handleButton(context, button);
        if (button != 0) return;
        if (!context.isClicked()) return;
        this.attached = true;
    }

    protected abstract double getValue();

    protected abstract void setValue(double var1);
}

