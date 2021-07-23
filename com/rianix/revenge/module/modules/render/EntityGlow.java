/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.WorldClient
 *  net.minecraft.entity.Entity
 */
package com.rianix.revenge.module.modules.render;

import com.rianix.revenge.module.Module;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;

public class EntityGlow
extends Module {
    public EntityGlow() {
        super("EntityGlow", "Highlights entities.", 0, Module.Category.RENDER);
    }

    @Override
    public void update() {
        Iterator iterator = EntityGlow.mc.field_71441_e.field_72996_f.iterator();
        while (iterator.hasNext()) {
            Entity entity = (Entity)iterator.next();
            if (entity.func_184202_aL()) continue;
            entity.func_184195_f(true);
        }
    }

    @Override
    public void onDisable() {
        Iterator iterator = EntityGlow.mc.field_71441_e.field_72996_f.iterator();
        while (iterator.hasNext()) {
            Entity entity = (Entity)iterator.next();
            if (!entity.func_184202_aL()) continue;
            entity.func_184195_f(false);
        }
    }
}

