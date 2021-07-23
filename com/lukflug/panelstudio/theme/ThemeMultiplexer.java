/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.theme.Renderer;
import com.lukflug.panelstudio.theme.RendererProxy;
import com.lukflug.panelstudio.theme.Theme;

public abstract class ThemeMultiplexer
implements Theme {
    protected final Renderer panelRenderer = new PanelRenderer();
    protected final Renderer containerRenderer = new ContainerRenderer();
    protected final Renderer componentRenderer = new ComponentRenderer();

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

    protected abstract Theme getTheme();

    protected class ComponentRenderer
    extends RendererProxy {
        protected ComponentRenderer() {
        }

        @Override
        protected Renderer getRenderer() {
            return ThemeMultiplexer.this.getTheme().getComponentRenderer();
        }
    }

    protected class ContainerRenderer
    extends RendererProxy {
        protected ContainerRenderer() {
        }

        @Override
        protected Renderer getRenderer() {
            return ThemeMultiplexer.this.getTheme().getContainerRenderer();
        }
    }

    protected class PanelRenderer
    extends RendererProxy {
        protected PanelRenderer() {
        }

        @Override
        protected Renderer getRenderer() {
            return ThemeMultiplexer.this.getTheme().getPanelRenderer();
        }
    }

}

