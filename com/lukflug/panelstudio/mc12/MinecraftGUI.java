/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiScreen
 *  org.lwjgl.input.Mouse
 */
package com.lukflug.panelstudio.mc12;

import com.lukflug.panelstudio.ClickGUI;
import com.lukflug.panelstudio.mc12.GLInterface;
import java.awt.Point;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

public abstract class MinecraftGUI
extends GuiScreen {
    private Point mouse = new Point();
    private boolean lButton = false;
    private boolean rButton = false;

    public void enterGUI() {
        Minecraft.func_71410_x().func_147108_a((GuiScreen)this);
        this.getGUI().enter();
    }

    public void exitGUI() {
        this.getGUI().exit();
        Minecraft.func_71410_x().func_147108_a(null);
    }

    protected void renderGUI() {
        this.getInterface().getMatrices();
        GLInterface.begin();
        this.getGUI().render();
        GLInterface.end();
    }

    public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
        this.mouse = new Point(mouseX, mouseY);
        this.renderGUI();
        int scroll = Mouse.getDWheel();
        if (scroll == 0) return;
        if (scroll > 0) {
            this.getGUI().handleScroll(-this.getScrollSpeed());
            return;
        }
        this.getGUI().handleScroll(this.getScrollSpeed());
    }

    public void func_73864_a(int mouseX, int mouseY, int clickedButton) {
        this.mouse = new Point(mouseX, mouseY);
        switch (clickedButton) {
            case 0: {
                this.lButton = true;
                break;
            }
            case 1: {
                this.rButton = true;
            }
        }
        this.getGUI().handleButton(clickedButton);
    }

    public void func_146286_b(int mouseX, int mouseY, int releaseButton) {
        this.mouse = new Point(mouseX, mouseY);
        switch (releaseButton) {
            case 0: {
                this.lButton = false;
                break;
            }
            case 1: {
                this.rButton = false;
            }
        }
        this.getGUI().handleButton(releaseButton);
    }

    protected void func_73869_a(char typedChar, int keyCode) {
        if (keyCode == 1) {
            this.exitGUI();
            return;
        }
        this.getGUI().handleKey(keyCode);
    }

    public boolean func_73868_f() {
        return false;
    }

    protected abstract ClickGUI getGUI();

    protected abstract GUIInterface getInterface();

    protected abstract int getScrollSpeed();

    public abstract class GUIInterface
    extends GLInterface {
        public GUIInterface(boolean clipX) {
            super(clipX);
        }

        @Override
        public boolean getButton(int button) {
            switch (button) {
                case 0: {
                    return MinecraftGUI.this.lButton;
                }
                case 1: {
                    return MinecraftGUI.this.rButton;
                }
            }
            return false;
        }

        @Override
        public Point getMouse() {
            return new Point(MinecraftGUI.this.mouse);
        }

        @Override
        protected float getZLevel() {
            return MinecraftGUI.this.field_73735_i;
        }
    }

}

