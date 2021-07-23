/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 */
package com.rianix.revenge.module.modules.movement;

import com.rianix.revenge.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class Spider
extends Module {
    public Spider() {
        super("Spider", "Allows you crawl up the walls like a spider.", 0, Module.Category.MOVEMENT);
    }

    @Override
    public void update() {
        super.update();
        if (!Spider.mc.field_71439_g.field_70123_F) return;
        Spider.mc.field_71439_g.field_70181_x = 0.20000000298023224;
        Spider.mc.field_71439_g.field_70122_E = true;
    }
}

