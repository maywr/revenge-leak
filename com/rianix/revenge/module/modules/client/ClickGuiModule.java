/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiScreen
 */
package com.rianix.revenge.module.modules.client;

import com.rianix.revenge.Revenge;
import com.rianix.revenge.gui.ClickGui;
import com.rianix.revenge.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class ClickGuiModule
extends Module {
    public ClickGuiModule() {
        super("ClickGui", "Displays Gui screen.", 54, Module.Category.CLIENT);
    }

    @Override
    public void onEnable() {
        mc.func_147108_a((GuiScreen)Revenge.instance.clickGui);
        this.toggle();
    }
}

