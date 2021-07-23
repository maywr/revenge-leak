/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio;

import java.awt.Point;

public interface PanelConfig {
    public void savePositon(Point var1);

    public Point loadPosition();

    public void saveState(boolean var1);

    public boolean loadState();
}

