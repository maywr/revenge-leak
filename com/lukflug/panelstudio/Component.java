/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio;

import com.lukflug.panelstudio.Context;

public interface Component {
    public String getTitle();

    public void render(Context var1);

    public void handleButton(Context var1, int var2);

    public void handleKey(Context var1, int var2);

    public void handleScroll(Context var1, int var2);

    public void getHeight(Context var1);

    public void enter(Context var1);

    public void exit(Context var1);

    public void releaseFocus();
}

