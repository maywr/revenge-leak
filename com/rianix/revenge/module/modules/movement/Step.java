/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 */
package com.rianix.revenge.module.modules.movement;

import com.rianix.revenge.module.Module;
import com.rianix.revenge.setting.settings.SettingDouble;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class Step
extends Module {
    SettingDouble height = this.register("Height", 2.5, 0.5, 2.0);

    public Step() {
        super("Step", "Step up blocks.", 19, Module.Category.MOVEMENT);
    }

    @Override
    public void update() {
        Step.mc.field_71439_g.field_70138_W = (float)this.height.getValue();
    }

    @Override
    public void onDisable() {
        Step.mc.field_71439_g.field_70138_W = 0.5f;
    }
}

