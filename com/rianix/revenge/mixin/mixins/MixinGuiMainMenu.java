/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.gui.GuiMainMenu
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.util.text.TextFormatting
 */
package com.rianix.revenge.mixin.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GuiMainMenu.class})
public class MixinGuiMainMenu
extends GuiScreen {
    @Inject(method={"drawScreen"}, at={@At(value="TAIL")}, cancellable=true)
    public void drawText(CallbackInfo ci) {
        this.field_146297_k.field_71466_p.func_175063_a((Object)TextFormatting.WHITE + "Revenge " + (Object)TextFormatting.GRAY + "1.0", 1.0f, 1.0f, -1);
        this.field_146297_k.field_71466_p.func_175063_a((Object)TextFormatting.WHITE + "made by " + (Object)TextFormatting.GRAY + "rianix and BlackBro4", 1.0f, (float)(this.field_146297_k.field_71466_p.field_78288_b + 1), -1);
        this.field_146297_k.field_71466_p.func_175063_a((Object)TextFormatting.WHITE + "thanks to " + (Object)TextFormatting.GRAY + "stupitdog", 1.0f, 20.0f, -1);
    }
}

