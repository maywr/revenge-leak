/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityOtherPlayerMP
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.multiplayer.WorldClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.world.GameType
 *  net.minecraft.world.World
 */
package com.rianix.revenge.module.modules.player;

import com.mojang.authlib.GameProfile;
import com.rianix.revenge.module.Module;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.world.GameType;
import net.minecraft.world.World;

public class Bot
extends Module {
    private EntityOtherPlayerMP bot;

    public Bot() {
        super("Bot", "Spawns a bot", 0, Module.Category.PLAYER);
    }

    @Override
    public void onEnable() {
        if (!Bot.NullCheck() && !Bot.mc.field_71439_g.field_70128_L) {
            this.bot = new EntityOtherPlayerMP((World)Bot.mc.field_71441_e, new GameProfile(UUID.fromString("0d14a8d5-4576-4ab8-a610-88c542c9fcbf"), "cattyn"));
            this.bot.func_82149_j((Entity)Bot.mc.field_71439_g);
            this.bot.field_70759_as = Bot.mc.field_71439_g.field_70759_as;
            this.bot.field_70177_z = Bot.mc.field_71439_g.field_70177_z;
            this.bot.field_70125_A = Bot.mc.field_71439_g.field_70125_A;
            this.bot.func_71033_a(GameType.SURVIVAL);
            this.bot.func_70606_j(20.0f);
            Bot.mc.field_71441_e.func_73027_a(-1337, (Entity)this.bot);
            this.bot.func_70636_d();
            return;
        }
        this.disable();
    }

    @Override
    public void onDisable() {
        if (Bot.mc.field_71441_e == null) return;
        Bot.mc.field_71441_e.func_73028_b(-1337);
    }
}

