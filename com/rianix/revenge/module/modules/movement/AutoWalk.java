/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.settings.GameSettings
 *  net.minecraft.client.settings.KeyBinding
 */
package com.rianix.revenge.module.modules.movement;

import com.rianix.revenge.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class AutoWalk
extends Module {
    public AutoWalk() {
        super("AutoWalk", "Presses the move forward key.", 0, Module.Category.MOVEMENT);
    }

    @Override
    public void update() {
        KeyBinding.func_74510_a((int)AutoWalk.mc.field_71474_y.field_74351_w.func_151463_i(), (boolean)true);
    }
}

