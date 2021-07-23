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

public class GameSenseTheme
implements Theme {
    protected ColorScheme scheme;
    protected Renderer componentRenderer;
    protected Renderer containerRenderer;
    protected Renderer panelRenderer;

    public GameSenseTheme(ColorScheme scheme, int height, int border, int scroll) {
        this.scheme = scheme;
        this.panelRenderer = new ComponentRenderer(0, height, border, scroll);
        this.containerRenderer = new ComponentRenderer(1, height, border, scroll);
        this.componentRenderer = new ComponentRenderer(2, height, border, scroll);
    }

    @Override
    public Renderer getPanelRenderer() {
        return this.panelRenderer;
    }

    @Override
    public Renderer getContainerRenderer() {
        return this.containerRenderer;
    }

    @Override
    public Renderer getComponentRenderer() {
        return this.componentRenderer;
    }

    protected class ComponentRenderer
    extends RendererBase {
        protected final int level;
        protected final int border;

        public ComponentRenderer(int level, int height, int border, int scroll) {
            super(height + 2 * border, 0, 0, 0, scroll);
            this.level = level;
            this.border = border;
        }

        @Override
        public void renderRect(Context context, String text, boolean focus, boolean active, Rectangle rectangle, boolean overlay) {
            Color color = this.getMainColor(focus, active);
            context.getInterface().fillRect(rectangle, color, color, color, color);
            if (overlay) {
                Color overlayColor = context.isHovered() ? new Color(255, 255, 255, 64) : new Color(255, 255, 255, 0);
                context.getInterface().fillRect(context.getRect(), overlayColor, overlayColor, overlayColor, overlayColor);
            }
            Point stringPos = new Point(rectangle.getLocation());
            stringPos.translate(0, this.border);
            context.getInterface().drawString(stringPos, text, this.getFontColor(focus));
        }

        @Override
        public void renderBackground(Context context, boolean focus) {
        }

        @Override
        public void renderBorder(Context context, boolean focus, boolean active, boolean open) {
            Color color = this.getDefaultColorScheme().getOutlineColor();
            if (this.level == 0) {
                context.getInterface().fillRect(new Rectangle(context.getPos(), new Dimension(context.getSize().width, 1)), color, color, color, color);
                context.getInterface().fillRect(new Rectangle(context.getPos(), new Dimension(1, context.getSize().height)), color, color, color, color);
                context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x + context.getSize().width - 1, context.getPos().y), new Dimension(1, context.getSize().height)), color, color, color, color);
            }
            if (this.level != 0) {
                if (!open) return;
            }
            context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x, context.getPos().y + context.getSize().height - 1), new Dimension(context.getSize().width, 1)), color, color, color, color);
            context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x, context.getPos().y + this.getHeight(open) - 1), new Dimension(context.getSize().width, 1)), color, color, color, color);
        }

        @Override
        public int renderScrollBar(Context context, boolean focus, boolean active, boolean scroll, int childHeight, int scrollPosition) {
            if (!scroll) return scrollPosition;
            int containerHeight = context.getSize().height - this.getHeight(true);
            int a = (int)((double)scrollPosition / (double)childHeight * (double)containerHeight);
            int b = (int)((double)(scrollPosition + containerHeight) / (double)childHeight * (double)containerHeight);
            Color background = this.getMainColor(focus, false);
            Color slider = this.getMainColor(focus, true);
            context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x + context.getSize().width - this.getRightBorder(true), context.getPos().y + this.getHeight(true)), new Dimension(this.getRightBorder(true), a)), background, background, background, background);
            context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x + context.getSize().width - this.getRightBorder(true), context.getPos().y + this.getHeight(true) + a), new Dimension(this.getRightBorder(true), b - a)), slider, slider, slider, slider);
            context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x + context.getSize().width - this.getRightBorder(true), context.getPos().y + this.getHeight(true) + b), new Dimension(this.getRightBorder(true), context.getSize().height - this.getHeight(true) - b)), background, background, background, background);
            Color color = this.getDefaultColorScheme().getOutlineColor();
            context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x + context.getSize().width - this.getRightBorder(true) - 1, context.getPos().y + this.getHeight(true)), new Dimension(1, context.getSize().height - this.getHeight(true))), color, color, color, color);
            if (!context.isClicked()) return scrollPosition;
            if (context.getInterface().getMouse().x < context.getPos().x + context.getSize().width - this.getRightBorder(true)) return scrollPosition;
            return (int)((double)((context.getInterface().getMouse().y - context.getPos().y - this.getHeight(true)) * childHeight) / (double)containerHeight - (double)containerHeight / 2.0);
        }

        @Override
        public Color getMainColor(boolean focus, boolean active) {
            Color color = active ? this.getColorScheme().getActiveColor() : this.getColorScheme().getBackgroundColor();
            if (active) return new Color(color.getRed(), color.getGreen(), color.getBlue(), this.getColorScheme().getOpacity());
            if (this.level >= 2) return new Color(color.getRed(), color.getGreen(), color.getBlue(), this.getColorScheme().getOpacity());
            color = this.getColorScheme().getInactiveColor();
            return new Color(color.getRed(), color.getGreen(), color.getBlue(), this.getColorScheme().getOpacity());
        }

        @Override
        public Color getBackgroundColor(boolean focus) {
            return new Color(0, 0, 0, 0);
        }

        @Override
        public ColorScheme getDefaultColorScheme() {
            return GameSenseTheme.this.scheme;
        }
    }

}

