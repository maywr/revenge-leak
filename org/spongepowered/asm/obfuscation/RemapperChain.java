/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.obfuscation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.spongepowered.asm.mixin.extensibility.IRemapper;

public class RemapperChain
implements IRemapper {
    private final List<IRemapper> remappers = new ArrayList<IRemapper>();

    public String toString() {
        return String.format("RemapperChain[%d]", this.remappers.size());
    }

    public RemapperChain add(IRemapper remapper) {
        this.remappers.add(remapper);
        return this;
    }

    @Override
    public String mapMethodName(String owner, String name, String desc) {
        Iterator<IRemapper> iterator = this.remappers.iterator();
        while (iterator.hasNext()) {
            IRemapper remapper = iterator.next();
            String newName = remapper.mapMethodName(owner, name, desc);
            if (newName == null || newName.equals(name)) continue;
            name = newName;
        }
        return name;
    }

    @Override
    public String mapFieldName(String owner, String name, String desc) {
        Iterator<IRemapper> iterator = this.remappers.iterator();
        while (iterator.hasNext()) {
            IRemapper remapper = iterator.next();
            String newName = remapper.mapFieldName(owner, name, desc);
            if (newName == null || newName.equals(name)) continue;
            name = newName;
        }
        return name;
    }

    @Override
    public String map(String typeName) {
        Iterator<IRemapper> iterator = this.remappers.iterator();
        while (iterator.hasNext()) {
            IRemapper remapper = iterator.next();
            String newName = remapper.map(typeName);
            if (newName == null || newName.equals(typeName)) continue;
            typeName = newName;
        }
        return typeName;
    }

    @Override
    public String unmap(String typeName) {
        Iterator<IRemapper> iterator = this.remappers.iterator();
        while (iterator.hasNext()) {
            IRemapper remapper = iterator.next();
            String newName = remapper.unmap(typeName);
            if (newName == null || newName.equals(typeName)) continue;
            typeName = newName;
        }
        return typeName;
    }

    @Override
    public String mapDesc(String desc) {
        Iterator<IRemapper> iterator = this.remappers.iterator();
        while (iterator.hasNext()) {
            IRemapper remapper = iterator.next();
            String newDesc = remapper.mapDesc(desc);
            if (newDesc == null || newDesc.equals(desc)) continue;
            desc = newDesc;
        }
        return desc;
    }

    @Override
    public String unmapDesc(String desc) {
        Iterator<IRemapper> iterator = this.remappers.iterator();
        while (iterator.hasNext()) {
            IRemapper remapper = iterator.next();
            String newDesc = remapper.unmapDesc(desc);
            if (newDesc == null || newDesc.equals(desc)) continue;
            desc = newDesc;
        }
        return desc;
    }
}

