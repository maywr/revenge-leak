/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.Interface;
import com.lukflug.panelstudio.theme.DescriptionRenderer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public class FixedDescription
implements DescriptionRenderer {
    protected Point pos;

    public FixedDescription(Point pos) {
        this.pos = pos;
    }

    @Override
    public void renderDescription(Context context) {
        if (context.getDescription() == null) return;
        Rectangle r = new Rectangle(this.pos, new Dimension(context.getInterface().getFontWidth(context.getDescription()), context.getInterface().getFontHeight()));
        Color bgcolor = new Color(0, 0, 0);
        context.getInterface().fillRect(r, bgcolor, bgcolor, bgcolor, bgcolor);
        Color color = new Color(255, 255, 255);
        context.getInterface().drawRect(r, color, color, color, color);
        context.getInterface().drawString(this.pos, context.getDescription(), color);
    }
}

