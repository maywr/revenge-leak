/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.settings.GameSettings
 */
package com.rianix.revenge.module.modules.render;

import com.rianix.revenge.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

public class FullBright
extends Module {
    public FullBright() {
        super("FullBright", "Turns up brightness to see in the dark.", 0, Module.Category.RENDER);
    }

    @Override
    public void update() {
        FullBright.mc.field_71474_y.field_74333_Y = 100.0f;
    }

    @Override
    public void onDisable() {
        FullBright.mc.field_71474_y.field_74333_Y = 1.0f;
    }
}

