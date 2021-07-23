/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.theme.ColorScheme;
import java.awt.Color;
import java.awt.Rectangle;

public interface Renderer {
    public int getHeight(boolean var1);

    public int getOffset();

    public int getBorder();

    public int getBottomBorder();

    public int getLeftBorder(boolean var1);

    public int getRightBorder(boolean var1);

    public void renderTitle(Context var1, String var2, boolean var3);

    public void renderTitle(Context var1, String var2, boolean var3, boolean var4);

    public void renderTitle(Context var1, String var2, boolean var3, boolean var4, boolean var5);

    public void renderRect(Context var1, String var2, boolean var3, boolean var4, Rectangle var5, boolean var6);

    public void renderBackground(Context var1, boolean var2);

    public void renderBorder(Context var1, boolean var2, boolean var3, boolean var4);

    public int renderScrollBar(Context var1, boolean var2, boolean var3, boolean var4, int var5, int var6);

    public Color getMainColor(boolean var1, boolean var2);

    public Color getBackgroundColor(boolean var1);

    public Color getFontColor(boolean var1);

    public ColorScheme getDefaultColorScheme();

    public void overrideColorScheme(ColorScheme var1);

    public void restoreColorScheme();

    public static Color brighter(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        g += 64;
        b += 64;
        if ((r += 64) > 255) {
            r = 255;
        }
        if (g > 255) {
            g = 255;
        }
        if (b <= 255) return new Color(r, g, b, color.getAlpha());
        b = 255;
        return new Color(r, g, b, color.getAlpha());
    }

    public static Color darker(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        g -= 64;
        b -= 64;
        if ((r -= 64) < 0) {
            r = 0;
        }
        if (g < 0) {
            g = 0;
        }
        if (b >= 0) return new Color(r, g, b, color.getAlpha());
        b = 0;
        return new Color(r, g, b, color.getAlpha());
    }
}

