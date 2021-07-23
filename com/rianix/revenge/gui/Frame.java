/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.FontRenderer
 */
package com.rianix.revenge.gui;

import com.rianix.revenge.gui.Button;
import com.rianix.revenge.gui.ClickGui;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class Frame {
    public String name;
    public int x;
    public int y;
    public int width;
    public int height;
    public int barheight;
    public int dragx;
    public int dragy;
    public int offset;
    public boolean hovered;
    public boolean dragging;
    public boolean open;
    ArrayList<Button> buttons;

    public Frame(String name, int x, int y, int width, int barheight) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.barheight = barheight;
        this.height = 200;
        this.width = width;
        this.buttons = new ArrayList();
    }

    public void update(int mouseX, int mouseY) {
        if (this.dragging) {
            this.x = mouseX - this.dragx;
            this.y = mouseY - this.dragy;
        }
        Iterator<Button> iterator = this.buttons.iterator();
        while (iterator.hasNext()) {
            Button b = iterator.next();
            b.update(mouseX, mouseY);
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0 && this.bounding(mouseX, mouseY)) {
            this.dragging = true;
            this.dragx = mouseX - this.x;
            this.dragy = mouseY - this.y;
        }
        if (mouseButton == 1 && this.bounding(mouseX, mouseY)) {
            this.open = !this.open;
        }
        Iterator<Button> iterator = this.buttons.iterator();
        while (iterator.hasNext()) {
            Button b = iterator.next();
            b.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
        this.dragging = false;
        Iterator<Button> iterator = this.buttons.iterator();
        while (iterator.hasNext()) {
            Button b = iterator.next();
            b.mouseReleased(mouseX, mouseY, state);
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        Iterator<Button> iterator = this.buttons.iterator();
        while (iterator.hasNext()) {
            Button b = iterator.next();
            b.keyTyped(typedChar, keyCode);
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ClickGui.func_73734_a((int)this.x, (int)this.y, (int)(this.x + this.width), (int)(this.y + this.barheight), (int)1879113985);
        Minecraft.func_71410_x().field_71466_p.func_175063_a(this.name, (float)(this.x + 2), (float)(this.y + 2), -1);
        this.offset = this.barheight;
        Iterator<Button> iterator = this.buttons.iterator();
        while (iterator.hasNext()) {
            Button b = iterator.next();
            if (!this.open) continue;
            b.drawScreen(mouseX, mouseY, partialTicks, this.offset);
            this.offset += this.barheight;
        }
    }

    public boolean bounding(int mouseX, int mouseY) {
        if (mouseX < this.x) return false;
        if (mouseX > this.x + this.width) return false;
        if (mouseY < this.y) return false;
        if (mouseY > this.y + this.barheight) return false;
        return true;
    }
}

