/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 */
package com.rianix.revenge.gui;

import com.rianix.revenge.Revenge;
import com.rianix.revenge.gui.Button;
import com.rianix.revenge.gui.Frame;
import com.rianix.revenge.module.Module;
import com.rianix.revenge.module.ModuleManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.client.gui.GuiScreen;

public class ClickGui
extends GuiScreen {
    public ArrayList<Frame> frames;

    public ClickGui() {
        int offset = 0;
        this.frames = new ArrayList();
        Module.Category[] arrcategory = Module.Category.values();
        int n = arrcategory.length;
        int n2 = 0;
        while (n2 < n) {
            Module.Category c = arrcategory[n2];
            Frame frame = new Frame(c.name(), 110 + offset, 15, 100, 12);
            for (Module m : Revenge.instance.moduleManager.getModsInCategory(c)) {
                Button button = new Button(frame, m);
                frame.buttons.add(button);
            }
            this.frames.add(frame);
            offset += 102;
            ++n2;
        }
    }

    public boolean func_73868_f() {
        return super.func_73868_f();
    }

    protected void func_73864_a(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.func_73864_a(mouseX, mouseY, mouseButton);
        Iterator<Frame> iterator = this.frames.iterator();
        while (iterator.hasNext()) {
            Frame f = iterator.next();
            f.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    protected void func_146286_b(int mouseX, int mouseY, int state) {
        super.func_146286_b(mouseX, mouseY, state);
        Iterator<Frame> iterator = this.frames.iterator();
        while (iterator.hasNext()) {
            Frame f = iterator.next();
            f.mouseReleased(mouseX, mouseY, state);
        }
    }

    protected void func_73869_a(char typedChar, int keyCode) throws IOException {
        super.func_73869_a(typedChar, keyCode);
        Iterator<Frame> iterator = this.frames.iterator();
        while (iterator.hasNext()) {
            Frame f = iterator.next();
            f.keyTyped(typedChar, keyCode);
        }
    }

    public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
        super.func_73863_a(mouseX, mouseY, partialTicks);
        Iterator<Frame> iterator = this.frames.iterator();
        while (iterator.hasNext()) {
            Frame f = iterator.next();
            f.update(mouseX, mouseY);
            f.drawScreen(mouseX, mouseY, partialTicks);
        }
    }
}

