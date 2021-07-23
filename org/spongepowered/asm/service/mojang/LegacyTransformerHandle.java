/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.launchwrapper.IClassTransformer
 */
package org.spongepowered.asm.service.mojang;

import java.lang.annotation.Annotation;
import javax.annotation.Resource;
import net.minecraft.launchwrapper.IClassTransformer;
import org.spongepowered.asm.service.ILegacyClassTransformer;

class LegacyTransformerHandle
implements ILegacyClassTransformer {
    private final IClassTransformer transformer;

    LegacyTransformerHandle(IClassTransformer transformer) {
        this.transformer = transformer;
    }

    @Override
    public String getName() {
        return this.transformer.getClass().getName();
    }

    @Override
    public boolean isDelegationExcluded() {
        if (this.transformer.getClass().getAnnotation(Resource.class) == null) return false;
        return true;
    }

    @Override
    public byte[] transformClassBytes(String name, String transformedName, byte[] basicClass) {
        return this.transformer.transform(name, transformedName, basicClass);
    }
}

