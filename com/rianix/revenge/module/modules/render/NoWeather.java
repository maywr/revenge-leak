/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.WorldClient
 */
package com.rianix.revenge.module.modules.render;

import com.rianix.revenge.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;

public class NoWeather
extends Module {
    public NoWeather() {
        super("NoWeather", "", 0, Module.Category.RENDER);
    }

    @Override
    public void update() {
        if (!NoWeather.mc.field_71441_e.func_72896_J()) return;
        NoWeather.mc.field_71441_e.func_72894_k(0.0f);
    }
}

