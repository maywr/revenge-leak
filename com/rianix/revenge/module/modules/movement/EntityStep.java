/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.entity.Entity
 */
package com.rianix.revenge.module.modules.movement;

import com.rianix.revenge.module.Module;
import com.rianix.revenge.setting.settings.SettingInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;

public class EntityStep
extends Module {
    SettingInteger height = this.register("Height", 2, 1, 10);

    public EntityStep() {
        super("EntityStep", "Step up blocks with entity.", 0, Module.Category.MOVEMENT);
    }

    @Override
    public void update() {
        if (EntityStep.mc.field_71439_g.func_184187_bx() == null) return;
        EntityStep.mc.field_71439_g.func_184187_bx().field_70138_W = this.height.getValue();
    }
}

