/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.client.event.RenderWorldLastEvent
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.InputEvent
 *  net.minecraftforge.fml.common.gameevent.InputEvent$KeyInputEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ClientTickEvent
 *  org.lwjgl.input.Keyboard
 */
package com.rianix.revenge.module;

import com.rianix.revenge.module.Module;
import com.rianix.revenge.module.modules.client.ClickGuiModule;
import com.rianix.revenge.module.modules.combat.FastBow;
import com.rianix.revenge.module.modules.misc.AutoFish;
import com.rianix.revenge.module.modules.misc.BoatPlace;
import com.rianix.revenge.module.modules.movement.AutoWalk;
import com.rianix.revenge.module.modules.movement.EntityStep;
import com.rianix.revenge.module.modules.movement.FastSwim;
import com.rianix.revenge.module.modules.movement.IceSpeed;
import com.rianix.revenge.module.modules.movement.ReverseStep;
import com.rianix.revenge.module.modules.movement.Spider;
import com.rianix.revenge.module.modules.movement.Sprint;
import com.rianix.revenge.module.modules.movement.Step;
import com.rianix.revenge.module.modules.player.AntiHunger;
import com.rianix.revenge.module.modules.player.AutoRespawn;
import com.rianix.revenge.module.modules.player.Bot;
import com.rianix.revenge.module.modules.player.Gamemode;
import com.rianix.revenge.module.modules.player.Velocity;
import com.rianix.revenge.module.modules.render.CustomFOV;
import com.rianix.revenge.module.modules.render.CustomTime;
import com.rianix.revenge.module.modules.render.EntityGlow;
import com.rianix.revenge.module.modules.render.FullBright;
import com.rianix.revenge.module.modules.render.NoBob;
import com.rianix.revenge.module.modules.render.NoWeather;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class ModuleManager {
    public ArrayList<Module> modules = new ArrayList();

    public ModuleManager() {
        MinecraftForge.EVENT_BUS.register((Object)this);
        this.init();
    }

    public void init() {
        this.modules.add(new NoBob());
        this.modules.add(new Bot());
        this.modules.add(new IceSpeed());
        this.modules.add(new ReverseStep());
        this.modules.add(new FastSwim());
        this.modules.add(new Step());
        this.modules.add(new EntityStep());
        this.modules.add(new Sprint());
        this.modules.add(new Velocity());
        this.modules.add(new AutoWalk());
        this.modules.add(new Spider());
        this.modules.add(new FullBright());
        this.modules.add(new CustomFOV());
        this.modules.add(new EntityGlow());
        this.modules.add(new ClickGuiModule());
        this.modules.add(new Gamemode());
        this.modules.add(new AutoRespawn());
        this.modules.add(new AntiHunger());
        this.modules.add(new BoatPlace());
        this.modules.add(new CustomTime());
        this.modules.add(new AutoFish());
        this.modules.add(new FastBow());
        this.modules.add(new NoWeather());
    }

    public ArrayList<Module> getModules() {
        return this.modules;
    }

    public Module getModule(String name) {
        Module m;
        Iterator<Module> iterator = this.modules.iterator();
        do {
            if (!iterator.hasNext()) return null;
        } while (!(m = iterator.next()).getName().equalsIgnoreCase(name));
        return m;
    }

    public ArrayList<Module> getModsInCategory(Module.Category cat) {
        ArrayList<Module> mods = new ArrayList<Module>();
        Iterator<Module> iterator = this.modules.iterator();
        while (iterator.hasNext()) {
            Module m = iterator.next();
            if (m.getCategory() != cat) continue;
            mods.add(m);
        }
        return mods;
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        if (!Keyboard.getEventKeyState()) return;
        Iterator<Module> iterator = this.modules.iterator();
        while (iterator.hasNext()) {
            Module m = iterator.next();
            if (m.getKey() != Keyboard.getEventKey()) continue;
            m.toggle();
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        Iterator<Module> iterator = this.modules.iterator();
        while (iterator.hasNext()) {
            Module m = iterator.next();
            if (!m.isToggled()) continue;
            m.update();
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        Iterator<Module> iterator = this.modules.iterator();
        while (iterator.hasNext()) {
            Module m = iterator.next();
            if (!m.isToggled()) continue;
            m.render();
        }
    }
}

