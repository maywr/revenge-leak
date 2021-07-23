/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.theme.Renderer;

public interface Theme {
    public Renderer getPanelRenderer();

    public Renderer getContainerRenderer();

    public Renderer getComponentRenderer();
}

