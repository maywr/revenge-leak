/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.settings.GameSettings
 */
package com.rianix.revenge.module.modules.render;

import com.rianix.revenge.module.Module;
import com.rianix.revenge.setting.settings.SettingInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

public class CustomFOV
extends Module {
    float defaultFov;
    SettingInteger fov = this.register("FOV", 140, 30, 170);

    public CustomFOV() {
        super("CustomFOV", "Allows you to change game FOV what you want.", 0, Module.Category.RENDER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.defaultFov = CustomFOV.mc.field_71474_y.field_74334_X;
    }

    @Override
    public void update() {
        super.update();
        CustomFOV.mc.field_71474_y.field_74334_X = this.fov.getValue();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        CustomFOV.mc.field_71474_y.field_74334_X = this.defaultFov;
    }
}

