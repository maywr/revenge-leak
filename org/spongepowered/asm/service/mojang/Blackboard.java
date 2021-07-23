/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.launchwrapper.Launch
 */
package org.spongepowered.asm.service.mojang;

import java.util.Map;
import net.minecraft.launchwrapper.Launch;
import org.spongepowered.asm.service.IGlobalPropertyService;

public class Blackboard
implements IGlobalPropertyService {
    @Override
    public final <T> T getProperty(String key) {
        return (T)Launch.blackboard.get(key);
    }

    @Override
    public final void setProperty(String key, Object value) {
        Launch.blackboard.put(key, value);
    }

    @Override
    public final <T> T getProperty(String key, T defaultValue) {
        Object object;
        Object value = Launch.blackboard.get(key);
        if (value != null) {
            object = value;
            return (T)object;
        }
        object = defaultValue;
        return (T)object;
    }

    @Override
    public final String getPropertyString(String key, String defaultValue) {
        String string;
        Object value = Launch.blackboard.get(key);
        if (value != null) {
            string = value.toString();
            return string;
        }
        string = defaultValue;
        return string;
    }
}

