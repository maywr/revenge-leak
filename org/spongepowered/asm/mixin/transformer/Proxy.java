/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.launchwrapper.IClassTransformer
 *  org.apache.logging.log4j.LogManager
 */
package org.spongepowered.asm.mixin.transformer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.transformer.MixinTransformer;
import org.spongepowered.asm.service.ILegacyClassTransformer;

public final class Proxy
implements IClassTransformer,
ILegacyClassTransformer {
    private static List<Proxy> proxies = new ArrayList<Proxy>();
    private static MixinTransformer transformer = new MixinTransformer();
    private boolean isActive = true;

    public Proxy() {
        Iterator<Proxy> iterator = proxies.iterator();
        do {
            if (!iterator.hasNext()) {
                proxies.add(this);
                LogManager.getLogger((String)"mixin").debug("Adding new mixin transformer proxy #{}", new Object[]{proxies.size()});
                return;
            }
            Proxy hook = iterator.next();
            hook.isActive = false;
        } while (true);
    }

    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!this.isActive) return basicClass;
        return transformer.transformClassBytes(name, transformedName, basicClass);
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isDelegationExcluded() {
        return true;
    }

    @Override
    public byte[] transformClassBytes(String name, String transformedName, byte[] basicClass) {
        if (!this.isActive) return basicClass;
        return transformer.transformClassBytes(name, transformedName, basicClass);
    }
}

