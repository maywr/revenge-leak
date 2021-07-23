/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.PlayerControllerMP
 *  net.minecraft.world.GameType
 */
package com.rianix.revenge.module.modules.player;

import com.rianix.revenge.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.world.GameType;

public class Gamemode
extends Module {
    public Gamemode() {
        super("Gamemode", "Fake gamemode 1.", 0, Module.Category.PLAYER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Gamemode.mc.field_71442_b.func_78746_a(GameType.CREATIVE);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Gamemode.mc.field_71442_b.func_78746_a(GameType.SURVIVAL);
    }
}

