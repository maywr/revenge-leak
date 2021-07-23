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

public class ReverseStep
extends Module {
    SettingDouble speed = this.register("Speed", 5.0, 0.0, 20.0);

    public ReverseStep() {
        super("ReverseStep", "", 0, Module.Category.MOVEMENT);
    }

    @Override
    public void update() {
        if (ReverseStep.NullCheck()) return;
        if (ReverseStep.mc.field_71439_g.func_70090_H()) return;
        if (ReverseStep.mc.field_71439_g.func_180799_ab()) {
            return;
        }
        if (!ReverseStep.mc.field_71439_g.field_70122_E) return;
        ReverseStep.mc.field_71439_g.field_70181_x -= this.speed.getValue() / 10.0;
    }
}

