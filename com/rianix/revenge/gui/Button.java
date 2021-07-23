/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.gui.Gui
 */
package com.rianix.revenge.gui;

import com.rianix.revenge.gui.Frame;
import com.rianix.revenge.module.Module;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

public class Button {
    public Frame parent;
    public Module module;
    public int offset;

    public Button(Frame parent, Module module) {
        this.parent = parent;
        this.module = module;
    }

    public void update(int mouseX, int mouseY) {
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton != 0) return;
        if (!this.bounding(mouseX, mouseY)) return;
        this.module.toggle();
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks, int offset) {
        this.offset = offset;
        Gui.func_73734_a((int)this.parent.x, (int)(this.parent.y + offset), (int)(this.parent.x + this.parent.width), (int)(this.parent.y + offset + this.parent.barheight), (int)(this.module.isToggled() ? 1073807617 : 1879113985));
        Minecraft.func_71410_x().field_71466_p.func_175063_a(this.module.getName(), (float)(this.parent.x + 2), (float)(this.parent.y + offset + 2), -1);
    }

    public boolean bounding(int mouseX, int mouseY) {
        if (mouseX < this.parent.x) return false;
        if (mouseX > this.parent.x + this.parent.width) return false;
        if (mouseY < this.parent.y + this.offset) return false;
        if (mouseY > this.parent.y + this.offset + this.parent.barheight) return false;
        return true;
    }
}

