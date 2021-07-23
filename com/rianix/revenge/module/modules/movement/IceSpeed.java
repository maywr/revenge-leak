/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 */
package com.rianix.revenge.module.modules.movement;

import com.rianix.revenge.module.Module;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class IceSpeed
extends Module {
    public IceSpeed() {
        super("IceSpeed", "", 0, Module.Category.MOVEMENT);
    }

    @Override
    public void update() {
        Blocks.field_150432_aD.field_149765_K = 1.2f;
        Blocks.field_185778_de.field_149765_K = 1.2f;
        Blocks.field_150403_cj.field_149765_K = 1.2f;
    }

    @Override
    public void onDisable() {
        Blocks.field_150432_aD.field_149765_K = 0.98f;
        Blocks.field_185778_de.field_149765_K = 0.98f;
        Blocks.field_150403_cj.field_149765_K = 0.98f;
    }
}

