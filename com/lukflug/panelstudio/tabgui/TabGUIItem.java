/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio.tabgui;

import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.settings.Toggleable;
import com.lukflug.panelstudio.tabgui.TabGUIComponent;

public class TabGUIItem
implements TabGUIComponent {
    protected String title;
    protected Toggleable toggle;

    public TabGUIItem(String title, Toggleable toggle) {
        this.title = title;
        this.toggle = toggle;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public void render(Context context) {
    }

    @Override
    public void handleButton(Context context, int button) {
    }

    @Override
    public void handleKey(Context context, int scancode) {
    }

    @Override
    public void handleScroll(Context context, int diff) {
    }

    @Override
    public void getHeight(Context context) {
    }

    @Override
    public void enter(Context context) {
    }

    @Override
    public void exit(Context context) {
    }

    @Override
    public boolean isActive() {
        return this.toggle.isOn();
    }

    @Override
    public boolean select() {
        this.toggle.toggle();
        return false;
    }

    @Override
    public void releaseFocus() {
    }
}

