/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.common.Mod$EventHandler
 *  net.minecraftforge.fml.common.Mod$Instance
 *  net.minecraftforge.fml.common.event.FMLInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLPreInitializationEvent
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 *  org.lwjgl.opengl.Display
 */
package com.rianix.revenge;

import com.rianix.revenge.gui.ClickGui;
import com.rianix.revenge.module.ModuleManager;
import com.rianix.revenge.setting.SettingsManager;
import me.zero.alpine.EventManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.lwjgl.opengl.Display;

@Mod(modid="revenge", name="Revenge", version="1.0")
public class Revenge {
    public static final String MODID = "revenge";
    public static final String NAME = "Revenge";
    public static final String VERSION = "1.0";
    @Mod.Instance
    public static Revenge instance = new Revenge();
    public static final EventManager EVENT_BUS = new EventManager();
    public ModuleManager moduleManager;
    public SettingsManager settingsManager;
    public ClickGui clickGui;

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register((Object)instance);
        this.settingsManager = new SettingsManager();
        this.moduleManager = new ModuleManager();
        this.clickGui = new ClickGui();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Display.setTitle((String)"Revenge - v.1.0");
    }
}

