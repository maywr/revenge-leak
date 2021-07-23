/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.settings.GameSettings
 *  net.minecraft.client.settings.KeyBinding
 */
package com.rianix.revenge.module.modules.movement;

import com.rianix.revenge.module.Module;
import com.rianix.revenge.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class FastSwim
extends Module {
    private int motionDelay;

    public FastSwim() {
        super("FastSwim", "", 0, Module.Category.MOVEMENT);
    }

    @Override
    public void update() {
        Timer.setCurrentMS();
        if (!FastSwim.mc.field_71439_g.func_70090_H() && !FastSwim.mc.field_71439_g.func_180799_ab()) {
            Timer.setLastMS();
            return;
        }
        if (FastSwim.mc.field_71474_y.field_74311_E.func_151468_f()) return;
        if (FastSwim.mc.field_71474_y.field_74314_A.func_151468_f()) {
            return;
        }
        FastSwim.mc.field_71439_g.field_70159_w *= 1.190000057220459;
        FastSwim.mc.field_71439_g.field_70179_y *= 1.190000057220459;
        if (!FastSwim.mc.field_71439_g.func_70090_H()) return;
        if (!Timer.hasDelayRun(1000L)) return;
        ++this.motionDelay;
        this.motionDelay %= 2;
        if (this.motionDelay != 0) return;
        FastSwim.mc.field_71439_g.field_70181_x = 0.012000000104308128;
    }
}

