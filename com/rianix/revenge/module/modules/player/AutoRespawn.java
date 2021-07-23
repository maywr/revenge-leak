/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.gui.GuiGameOver
 *  net.minecraft.client.gui.GuiScreen
 */
package com.rianix.revenge.module.modules.player;

import com.rianix.revenge.command.Messages;
import com.rianix.revenge.module.Module;
import com.rianix.revenge.setting.settings.SettingBoolean;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiScreen;

public class AutoRespawn
extends Module {
    SettingBoolean coords = this.register("DeathCoords", true);

    public AutoRespawn() {
        super("AutoRespawn", "Removes the death screen.", 0, Module.Category.PLAYER);
    }

    @Override
    public void update() {
        super.update();
        if (AutoRespawn.mc.field_71462_r instanceof GuiGameOver) {
            AutoRespawn.mc.field_71439_g.func_71004_bE();
            mc.func_147108_a(null);
        }
        if (!this.coords.getValue()) return;
        if (!(AutoRespawn.mc.field_71462_r instanceof GuiGameOver)) return;
        Messages.sendClientMessage("You have died at x" + (int)AutoRespawn.mc.field_71439_g.field_70165_t + " y" + (int)AutoRespawn.mc.field_71439_g.field_70163_u + " z" + (int)AutoRespawn.mc.field_71439_g.field_70161_v);
    }
}

