/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBoat
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 */
package com.rianix.revenge.module.modules.misc;

import com.rianix.revenge.event.events.PacketEvent;
import com.rianix.revenge.module.Module;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.EventHook;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBoat;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;

public class BoatPlace
extends Module {
    @EventHandler
    private final Listener<PacketEvent.Send> receiveListener = new Listener<PacketEvent.Send>(event -> {
        if (!(event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) || !(BoatPlace.mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemBoat)) {
            if (!(BoatPlace.mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemBoat)) return;
        }
        event.cancel();
    }, new Predicate[0]);

    public BoatPlace() {
        super("BoatPlace", "Allows you to place the boat on servers where it is prohibited.", 0, Module.Category.MISC);
    }
}

