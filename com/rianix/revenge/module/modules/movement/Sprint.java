/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.multiplayer.WorldClient
 *  net.minecraft.client.settings.GameSettings
 *  net.minecraft.client.settings.KeyBinding
 *  net.minecraft.util.FoodStats
 */
package com.rianix.revenge.module.modules.movement;

import com.rianix.revenge.module.Module;
import com.rianix.revenge.setting.settings.SettingMode;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.FoodStats;

public class Sprint
extends Module {
    ArrayList<String> modes = new ArrayList();
    SettingMode mode = this.register("Mode", this.modes, "Rage");

    public Sprint() {
        super("Sprint", "Automatic sprints.", 0, Module.Category.MOVEMENT);
        this.modes.add("Legit");
        this.modes.add("Rage");
    }

    @Override
    public void onDisable() {
        if (Sprint.mc.field_71441_e == null) return;
        Sprint.mc.field_71439_g.func_70031_b(false);
    }

    @Override
    public void update() {
        if (this.mode.getValue().equals("Legit") && Sprint.mc.field_71474_y.field_74351_w.func_151470_d() && !Sprint.mc.field_71439_g.func_70093_af() && !Sprint.mc.field_71439_g.func_184587_cr() && !Sprint.mc.field_71439_g.field_70123_F && Sprint.mc.field_71462_r == null && !((float)Sprint.mc.field_71439_g.func_71024_bL().func_75116_a() <= 6.0f)) {
            Sprint.mc.field_71439_g.func_70031_b(true);
        }
        if (this.mode.getValue().equals("Rage") && (Sprint.mc.field_71474_y.field_74351_w.func_151470_d() || Sprint.mc.field_71474_y.field_74368_y.func_151470_d() || Sprint.mc.field_71474_y.field_74370_x.func_151470_d() || Sprint.mc.field_71474_y.field_74366_z.func_151470_d()) && !Sprint.mc.field_71439_g.func_70093_af() && !Sprint.mc.field_71439_g.field_70123_F && !((float)Sprint.mc.field_71439_g.func_71024_bL().func_75116_a() <= 6.0f)) {
            Sprint.mc.field_71439_g.func_70031_b(true);
        }
        KeyBinding.func_74510_a((int)Sprint.mc.field_71474_y.field_151444_V.func_151463_i(), (boolean)true);
    }
}

