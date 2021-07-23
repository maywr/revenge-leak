/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio.hud;

import java.awt.Color;

public interface HUDList {
    public int getSize();

    public String getItem(int var1);

    public Color getItemColor(int var1);

    public boolean sortUp();

    public boolean sortRight();
}

