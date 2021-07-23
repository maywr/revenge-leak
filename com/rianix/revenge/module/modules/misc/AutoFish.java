/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.network.NetHandlerPlayClient
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemFishingRod
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItem
 *  net.minecraft.network.play.server.SPacketSoundEffect
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.SoundEvent
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
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;

public class AutoFish
extends Module {
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener = new Listener<PacketEvent.Receive>(event -> {
        if (!(event.getPacket() instanceof SPacketSoundEffect)) return;
        SPacketSoundEffect packet = (SPacketSoundEffect)event.getPacket();
        if (!packet.func_186978_a().equals((Object)SoundEvents.field_187609_F)) return;
        if (!(AutoFish.mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemFishingRod)) return;
        AutoFish.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        AutoFish.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
        AutoFish.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        AutoFish.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
    }, new Predicate[0]);

    public AutoFish() {
        super("AutoFish", "Fishes automatically.", 0, Module.Category.MISC);
    }
}

