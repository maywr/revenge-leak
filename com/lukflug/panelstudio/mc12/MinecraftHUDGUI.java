/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiScreen
 */
package com.lukflug.panelstudio.mc12;

import com.lukflug.panelstudio.ClickGUI;
import com.lukflug.panelstudio.hud.HUDClickGUI;
import com.lukflug.panelstudio.mc12.MinecraftGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public abstract class MinecraftHUDGUI
extends MinecraftGUI {
    protected boolean hudEditor = false;

    @Override
    public void enterGUI() {
        this.hudEditor = false;
        super.enterGUI();
    }

    @Override
    public void exitGUI() {
        this.hudEditor = false;
        super.exitGUI();
    }

    public void enterHUDEditor() {
        this.hudEditor = true;
        if (this.getHUDGUI().isOn()) {
            this.getHUDGUI().toggle();
        }
        Minecraft.func_71410_x().func_147108_a((GuiScreen)this);
    }

    public void render() {
        if (this.getHUDGUI().isOn()) return;
        if (this.hudEditor) return;
        this.renderGUI();
    }

    public void handleKeyEvent(int scancode) {
        if (scancode == 1) return;
        if (this.getHUDGUI().isOn()) return;
        if (this.hudEditor) return;
        this.getHUDGUI().handleKey(scancode);
    }

    protected abstract HUDClickGUI getHUDGUI();

    @Override
    protected ClickGUI getGUI() {
        return this.getHUDGUI();
    }
}

