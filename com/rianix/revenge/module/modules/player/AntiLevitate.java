/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 */
package com.rianix.revenge.module.modules.player;

import com.rianix.revenge.module.Module;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class AntiLevitate
extends Module {
    public AntiLevitate() {
        super("AntiLevitate", "Removes the effect of levitation from you.", 0, Module.Category.PLAYER);
    }

    @Override
    public void update() {
        if (!AntiLevitate.mc.field_71439_g.func_70644_a(Objects.requireNonNull(Potion.func_180142_b((String)"levitation")))) return;
        AntiLevitate.mc.field_71439_g.func_184596_c(Potion.func_180142_b((String)"levitation"));
    }
}

