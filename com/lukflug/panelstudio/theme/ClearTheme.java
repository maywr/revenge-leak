/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.Interface;
import com.lukflug.panelstudio.theme.ColorScheme;
import com.lukflug.panelstudio.theme.Renderer;
import com.lukflug.panelstudio.theme.RendererBase;
import com.lukflug.panelstudio.theme.Theme;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public class ClearTheme
implements Theme {
    protected ColorScheme scheme;
    protected Renderer componentRenderer;
    protected Renderer panelRenderer;
    protected final boolean gradient;

    public ClearTheme(ColorScheme scheme, boolean gradient, int height, int border) {
        this.scheme = scheme;
        this.gradient = gradient;
        this.panelRenderer = new ComponentRenderer(true, height, border);
        this.componentRenderer = new ComponentRenderer(false, height, border);
    }

    @Override
    public Renderer getPanelRenderer() {
        return this.panelRenderer;
    }

    @Override
    public Renderer getContainerRenderer() {
        return this.componentRenderer;
    }

    @Override
    public Renderer getComponentRenderer() {
        return this.componentRenderer;
    }

    protected class ComponentRenderer
    extends RendererBase {
        protected final boolean panel;

        public ComponentRenderer(boolean panel, int height, int border) {
            super(height + 2 * border, border, 0, 0, 0);
            this.panel = panel;
        }

        @Override
        public void renderTitle(Context context, String text, boolean focus, boolean active) {
            if (this.panel) {
                super.renderTitle(context, text, focus, active);
                return;
            }
            Color overlayColor = context.isHovered() ? new Color(0, 0, 0, 64) : new Color(0, 0, 0, 0);
            context.getInterface().fillRect(context.getRect(), overlayColor, overlayColor, overlayColor, overlayColor);
            Color fontColor = this.getFontColor(focus);
            if (active) {
                fontColor = this.getMainColor(focus, true);
            }
            Point stringPos = new Point(context.getPos());
            stringPos.translate(0, this.getOffset());
            context.getInterface().drawString(stringPos, text, fontColor);
        }

        @Override
        public void renderTitle(Context context, String text, boolean focus, boolean active, boolean open) {
            Point p2;
            Point p3;
            Point p1;
            super.renderTitle(context, text, focus, active, open);
            if (this.panel) return;
            Color color = this.getFontColor(active);
            if (open) {
                p3 = new Point(context.getPos().x + context.getSize().width - 2, context.getPos().y + context.getSize().height / 4);
                p2 = new Point(context.getPos().x + context.getSize().width - context.getSize().height / 2, context.getPos().y + context.getSize().height * 3 / 4);
                p1 = new Point(context.getPos().x + context.getSize().width - context.getSize().height + 2, context.getPos().y + context.getSize().height / 4);
            } else {
                p3 = new Point(context.getPos().x + context.getSize().width - context.getSize().height * 3 / 4, context.getPos().y + 2);
                p2 = new Point(context.getPos().x + context.getSize().width - context.getSize().height / 4, context.getPos().y + context.getSize().height / 2);
                p1 = new Point(context.getPos().x + context.getSize().width - context.getSize().height * 3 / 4, context.getPos().y + context.getSize().height - 2);
            }
            context.getInterface().fillTriangle(p1, p2, p3, color, color, color);
        }

        @Override
        public void renderRect(Context context, String text, boolean focus, boolean active, Rectangle rectangle, boolean overlay) {
            if (this.panel || active) {
                Color color = this.getMainColor(focus, true);
                Color color2 = this.getBackgroundColor(focus);
                if (ClearTheme.this.gradient && this.panel) {
                    context.getInterface().fillRect(rectangle, color, color, color2, color2);
                } else {
                    context.getInterface().fillRect(rectangle, color, color, color, color);
                }
            }
            if (!this.panel && overlay) {
                Color overlayColor = context.isHovered() ? new Color(0, 0, 0, 64) : new Color(0, 0, 0, 0);
                context.getInterface().fillRect(context.getRect(), overlayColor, overlayColor, overlayColor, overlayColor);
            }
            Point stringPos = new Point(rectangle.getLocation());
            stringPos.translate(0, this.getOffset());
            context.getInterface().drawString(stringPos, text, this.getFontColor(focus));
        }

        @Override
        public void renderBackground(Context context, boolean focus) {
            if (!this.panel) return;
            Color color = this.getBackgroundColor(focus);
            context.getInterface().fillRect(context.getRect(), color, color, color, color);
        }

        @Override
        public void renderBorder(Context context, boolean focus, boolean active, boolean open) {
        }

        @Override
        public Color getMainColor(boolean focus, boolean active) {
            if (!active) return new Color(0, 0, 0, 0);
            return this.getColorScheme().getActiveColor();
        }

        @Override
        public Color getBackgroundColor(boolean focus) {
            Color color = this.getColorScheme().getBackgroundColor();
            return new Color(color.getRed(), color.getGreen(), color.getBlue(), this.getColorScheme().getOpacity());
        }

        @Override
        public ColorScheme getDefaultColorScheme() {
            return ClearTheme.this.scheme;
        }
    }

}

