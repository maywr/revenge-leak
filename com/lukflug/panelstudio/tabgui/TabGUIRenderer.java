/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio.tabgui;

import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.theme.ColorScheme;

public interface TabGUIRenderer {
    public int getHeight();

    public int getBorder();

    public void renderBackground(Context var1, int var2, int var3);

    public void renderCaption(Context var1, String var2, int var3, int var4, boolean var5);

    public ColorScheme getColorScheme();

    public boolean isUpKey(int var1);

    public boolean isDownKey(int var1);

    public boolean isSelectKey(int var1);

    public boolean isEscapeKey(int var1);
}

