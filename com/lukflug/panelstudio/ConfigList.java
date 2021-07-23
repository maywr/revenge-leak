/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio;

import com.lukflug.panelstudio.PanelConfig;

public interface ConfigList {
    public void begin(boolean var1);

    public void end(boolean var1);

    public PanelConfig addPanel(String var1);

    public PanelConfig getPanel(String var1);
}

