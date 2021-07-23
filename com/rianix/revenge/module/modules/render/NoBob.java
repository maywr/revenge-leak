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

public class NoBob
extends Module {
    public NoBob() {
        super("NoBob", "", 0, Module.Category.RENDER);
    }

    @Override
    public void onEnable() {
        NoBob.mc.field_71474_y.field_74336_f = false;
    }

    @Override
    public void onDisable() {
        NoBob.mc.field_71474_y.field_74336_f = true;
    }
}

