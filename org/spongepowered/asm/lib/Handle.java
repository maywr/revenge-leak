/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.lib;

public final class Handle {
    final int tag;
    final String owner;
    final String name;
    final String desc;
    final boolean itf;

    @Deprecated
    public Handle(int tag, String owner, String name, String desc) {
        this(tag, owner, name, desc, tag == 9);
    }

    public Handle(int tag, String owner, String name, String desc, boolean itf) {
        this.tag = tag;
        this.owner = owner;
        this.name = name;
        this.desc = desc;
        this.itf = itf;
    }

    public int getTag() {
        return this.tag;
    }

    public String getOwner() {
        return this.owner;
    }

    public String getName() {
        return this.name;
    }

    public String getDesc() {
        return this.desc;
    }

    public boolean isInterface() {
        return this.itf;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Handle)) {
            return false;
        }
        Handle h = (Handle)obj;
        if (this.tag != h.tag) return false;
        if (this.itf != h.itf) return false;
        if (!this.owner.equals(h.owner)) return false;
        if (!this.name.equals(h.name)) return false;
        if (!this.desc.equals(h.desc)) return false;
        return true;
    }

    public int hashCode() {
        int n;
        if (this.itf) {
            n = 64;
            return this.tag + n + this.owner.hashCode() * this.name.hashCode() * this.desc.hashCode();
        }
        n = 0;
        return this.tag + n + this.owner.hashCode() * this.name.hashCode() * this.desc.hashCode();
    }

    public String toString() {
        String string;
        if (this.itf) {
            string = " itf";
            return this.owner + '.' + this.name + this.desc + " (" + this.tag + string + ')';
        }
        string = "";
        return this.owner + '.' + this.name + this.desc + " (" + this.tag + string + ')';
    }
}

