/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.tools.obfuscation.mirror.mapping;

import java.util.Iterator;
import java.util.List;
import org.spongepowered.asm.obfuscation.mapping.common.MappingMethod;
import org.spongepowered.tools.obfuscation.mirror.MethodHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeUtils;

public final class ResolvableMappingMethod
extends MappingMethod {
    private final TypeHandle ownerHandle;

    public ResolvableMappingMethod(TypeHandle owner, String name, String desc) {
        super(owner.getName(), name, desc);
        this.ownerHandle = owner;
    }

    @Override
    public MappingMethod getSuper() {
        TypeHandle iface;
        if (this.ownerHandle == null) {
            return super.getSuper();
        }
        String name = this.getSimpleName();
        String desc = this.getDesc();
        String signature = TypeUtils.getJavaSignature(desc);
        TypeHandle superClass = this.ownerHandle.getSuperclass();
        if (superClass != null && superClass.findMethod(name, signature) != null) {
            return superClass.getMappingMethod(name, desc);
        }
        Iterator<TypeHandle> iterator = this.ownerHandle.getInterfaces().iterator();
        do {
            if (iterator.hasNext()) continue;
            if (superClass == null) return super.getSuper();
            return superClass.getMappingMethod(name, desc).getSuper();
        } while ((iface = iterator.next()).findMethod(name, signature) == null);
        return iface.getMappingMethod(name, desc);
    }

    public MappingMethod move(TypeHandle newOwner) {
        return new ResolvableMappingMethod(newOwner, this.getSimpleName(), this.getDesc());
    }

    @Override
    public MappingMethod remap(String newName) {
        return new ResolvableMappingMethod(this.ownerHandle, newName, this.getDesc());
    }

    @Override
    public MappingMethod transform(String newDesc) {
        return new ResolvableMappingMethod(this.ownerHandle, this.getSimpleName(), newDesc);
    }

    @Override
    public MappingMethod copy() {
        return new ResolvableMappingMethod(this.ownerHandle, this.getSimpleName(), this.getDesc());
    }
}

