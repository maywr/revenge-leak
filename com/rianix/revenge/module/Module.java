/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.multiplayer.WorldClient
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 */
package com.rianix.revenge.module;

import com.rianix.revenge.Revenge;
import com.rianix.revenge.setting.Setting;
import com.rianix.revenge.setting.SettingsManager;
import com.rianix.revenge.setting.settings.SettingBoolean;
import com.rianix.revenge.setting.settings.SettingDouble;
import com.rianix.revenge.setting.settings.SettingInteger;
import com.rianix.revenge.setting.settings.SettingMode;
import java.util.ArrayList;
import me.zero.alpine.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;

public class Module {
    public static final Minecraft mc = Minecraft.func_71410_x();
    public String name;
    public String description;
    public int key;
    public Category category;
    public boolean toggled;

    public Module(String name, String description, int key, Category category) {
        this.name = name;
        this.description = description;
        this.key = key;
        this.category = category;
    }

    public void enable() {
        Revenge.EVENT_BUS.subscribe((Object)this);
        MinecraftForge.EVENT_BUS.register((Object)this);
        this.onEnable();
    }

    public void disable() {
        Revenge.EVENT_BUS.subscribe((Object)this);
        MinecraftForge.EVENT_BUS.register((Object)this);
        this.onDisable();
    }

    public void toggle() {
        boolean bl = this.toggled = !this.toggled;
        if (this.toggled) {
            this.enable();
            return;
        }
        this.disable();
    }

    public void update() {
    }

    public void render() {
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onToggle() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getKey() {
        return this.key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean isToggled() {
        return this.toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }

    public SettingBoolean register(String name, boolean value) {
        SettingBoolean set = new SettingBoolean(name, this, value);
        Revenge.instance.settingsManager.settings.add(set);
        return set;
    }

    public SettingMode register(String name, ArrayList<String> values, String value) {
        SettingMode set = new SettingMode(name, this, values, value);
        Revenge.instance.settingsManager.settings.add(set);
        return set;
    }

    public SettingInteger register(String name, int value, int min, int max) {
        SettingInteger set = new SettingInteger(name, this, value, min, max);
        Revenge.instance.settingsManager.settings.add(set);
        return set;
    }

    public SettingDouble register(String name, double value, double min, double max) {
        SettingDouble set = new SettingDouble(name, this, (int)value, (int)min, (int)max);
        Revenge.instance.settingsManager.settings.add(set);
        return set;
    }

    public static boolean NullCheck() {
        if (Module.mc.field_71439_g == null) return true;
        if (Module.mc.field_71441_e == null) return true;
        return false;
    }

    public static enum Category {
        COMBAT,
        MOVEMENT,
        PLAYER,
        RENDER,
        MISC,
        CLIENT;
        
    }

}

