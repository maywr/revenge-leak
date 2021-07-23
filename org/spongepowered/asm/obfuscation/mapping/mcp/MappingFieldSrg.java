/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.obfuscation.mapping.mcp;

import org.spongepowered.asm.obfuscation.mapping.common.MappingField;

public class MappingFieldSrg
extends MappingField {
    private final String srg;

    public MappingFieldSrg(String srg) {
        super(MappingFieldSrg.getOwnerFromSrg(srg), MappingFieldSrg.getNameFromSrg(srg), null);
        this.srg = srg;
    }

    public MappingFieldSrg(MappingField field) {
        super(field.getOwner(), field.getName(), null);
        this.srg = field.getOwner() + "/" + field.getName();
    }

    @Override
    public String serialise() {
        return this.srg;
    }

    private static String getNameFromSrg(String srg) {
        String string;
        if (srg == null) {
            return null;
        }
        int pos = srg.lastIndexOf(47);
        if (pos > -1) {
            string = srg.substring(pos + 1);
            return string;
        }
        string = srg;
        return string;
    }

    private static String getOwnerFromSrg(String srg) {
        if (srg == null) {
            return null;
        }
        int pos = srg.lastIndexOf(47);
        if (pos <= -1) return null;
        String string = srg.substring(0, pos);
        return string;
    }
}

