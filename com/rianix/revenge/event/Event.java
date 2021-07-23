/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 */
package com.rianix.revenge.event;

import me.zero.alpine.type.Cancellable;
import net.minecraft.client.Minecraft;

public class Event
extends Cancellable {
    private Era era;
    private float partialTicks;

    public Event() {
        this.partialTicks = Minecraft.func_71410_x().func_184121_ak();
    }

    public Event(Era era) {
        this.era = era;
        this.partialTicks = Minecraft.func_71410_x().func_184121_ak();
    }

    public Era getEra() {
        return this.era;
    }

    public void setEra(Era era) {
        this.era = era;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public static enum Era {
        PRE,
        POST,
        PERI;
        
    }

}

