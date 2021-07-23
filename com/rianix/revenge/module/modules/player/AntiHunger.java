/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.multiplayer.PlayerControllerMP
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer
 */
package com.rianix.revenge.module.modules.player;

import com.rianix.revenge.event.events.PacketEvent;
import com.rianix.revenge.module.Module;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.EventHook;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;

public class AntiHunger
extends Module {
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener = new Listener<PacketEvent.Receive>(event -> {
        if (!(event.getPacket() instanceof CPacketPlayer)) return;
        CPacketPlayer player = (CPacketPlayer)event.getPacket();
        double differenceY = AntiHunger.mc.field_71439_g.field_70163_u - AntiHunger.mc.field_71439_g.field_70137_T;
        boolean groundCheck = differenceY == 0.0;
        if (!groundCheck) return;
        if (AntiHunger.mc.field_71442_b.field_78778_j) return;
        AntiHunger.mc.field_71439_g.field_70122_E = true;
    }, new Predicate[0]);

    public AntiHunger() {
        super("AntiHunger", "Causes you to not lose hunger even while jumping.", 0, Module.Category.PLAYER);
    }
}

