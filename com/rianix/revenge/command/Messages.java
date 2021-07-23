/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 */
package com.rianix.revenge.command;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class Messages {
    public static void sendPlayerMessage(String ... message) {
        String[] arrstring = message;
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String m = arrstring[n2];
            Minecraft.func_71410_x().field_71439_g.func_71165_d(m);
            ++n2;
        }
    }

    public static void sendSilentMessage(String ... message) {
        String[] arrstring = message;
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String m = arrstring[n2];
            Minecraft.func_71410_x().field_71439_g.func_145747_a((ITextComponent)new TextComponentString(m));
            ++n2;
        }
    }

    public static void sendClientMessage(String ... message) {
        String[] arrstring = message;
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String m = arrstring[n2];
            String prefix = (Object)ChatFormatting.WHITE + "[" + (Object)ChatFormatting.GRAY + "Revenge" + (Object)ChatFormatting.RESET + (Object)ChatFormatting.WHITE + "] " + (Object)ChatFormatting.WHITE;
            Minecraft.func_71410_x().field_71439_g.func_145747_a((ITextComponent)new TextComponentString(prefix + m));
            ++n2;
        }
    }
}

