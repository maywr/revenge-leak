/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.obfuscation.mapping.common;

import com.google.common.base.Objects;
import org.spongepowered.asm.obfuscation.mapping.IMapping;

public class MappingMethod
implements IMapping<MappingMethod> {
    private final String owner;
    private final String name;
    private final String desc;

    public MappingMethod(String fullyQualifiedName, String desc) {
        this(MappingMethod.getOwnerFromName(fullyQualifiedName), MappingMethod.getBaseName(fullyQualifiedName), desc);
    }

    public MappingMethod(String owner, String simpleName, String desc) {
        this.owner = owner;
        this.name = simpleName;
        this.desc = desc;
    }

    @Override
    public IMapping.Type getType() {
        return IMapping.Type.METHOD;
    }

    @Override
    public String getName() {
        String string;
        if (this.name == null) {
            return null;
        }
        if (this.owner != null) {
            string = this.owner + "/";
            return string + this.name;
        }
        string = "";
        return string + this.name;
    }

    @Override
    public String getSimpleName() {
        return this.name;
    }

    @Override
    public String getOwner() {
        return this.owner;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }

    @Override
    public MappingMethod getSuper() {
        return null;
    }

    public boolean isConstructor() {
        return "<init>".equals(this.name);
    }

    @Override
    public MappingMethod move(String newOwner) {
        return new MappingMethod(newOwner, this.getSimpleName(), this.getDesc());
    }

    @Override
    public MappingMethod remap(String newName) {
        return new MappingMethod(this.getOwner(), newName, this.getDesc());
    }

    @Override
    public MappingMethod transform(String newDesc) {
        return new MappingMethod(this.getOwner(), this.getSimpleName(), newDesc);
    }

    @Override
    public MappingMethod copy() {
        return new MappingMethod(this.getOwner(), this.getSimpleName(), this.getDesc());
    }

    public MappingMethod addPrefix(String prefix) {
        String simpleName = this.getSimpleName();
        if (simpleName == null) return this;
        if (!simpleName.startsWith(prefix)) return new MappingMethod(this.getOwner(), prefix + simpleName, this.getDesc());
        return this;
    }

    public int hashCode() {
        return Objects.hashCode(this.getName(), this.getDesc());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof MappingMethod)) return false;
        if (!Objects.equal(this.name, ((MappingMethod)obj).name)) return false;
        if (!Objects.equal(this.desc, ((MappingMethod)obj).desc)) return false;
        return true;
    }

    @Override
    public String serialise() {
        return this.toString();
    }

    public String toString() {
        String desc = this.getDesc();
        return String.format("%s%s%s", this.getName(), desc != null ? " " : "", desc != null ? desc : "");
    }

    private static String getBaseName(String name) {
        String string;
        if (name == null) {
            return null;
        }
        int pos = name.lastIndexOf(47);
        if (pos > -1) {
            string = name.substring(pos + 1);
            return string;
        }
        string = name;
        return string;
    }

    private static String getOwnerFromName(String name) {
        if (name == null) {
            return null;
        }
        int pos = name.lastIndexOf(47);
        if (pos <= -1) return null;
        String string = name.substring(0, pos);
        return string;
    }
}

