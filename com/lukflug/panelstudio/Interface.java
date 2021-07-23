/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

public interface Interface {
    public static final int LBUTTON = 0;
    public static final int RBUTTON = 1;

    public Point getMouse();

    public boolean getButton(int var1);

    public void drawString(Point var1, String var2, Color var3);

    public int getFontWidth(String var1);

    public int getFontHeight();

    public void fillTriangle(Point var1, Point var2, Point var3, Color var4, Color var5, Color var6);

    public void drawLine(Point var1, Point var2, Color var3, Color var4);

    public void fillRect(Rectangle var1, Color var2, Color var3, Color var4, Color var5);

    public void drawRect(Rectangle var1, Color var2, Color var3, Color var4, Color var5);

    public int loadImage(String var1);

    public void drawImage(Rectangle var1, int var2, boolean var3, int var4);

    public void window(Rectangle var1);

    public void restore();
}

