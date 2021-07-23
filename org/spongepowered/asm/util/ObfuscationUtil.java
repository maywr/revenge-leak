/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.util;

public abstract class ObfuscationUtil {
    private ObfuscationUtil() {
    }

    public static String mapDescriptor(String desc, IClassRemapper remapper) {
        return ObfuscationUtil.remapDescriptor(desc, remapper, false);
    }

    public static String unmapDescriptor(String desc, IClassRemapper remapper) {
        return ObfuscationUtil.remapDescriptor(desc, remapper, true);
    }

    private static String remapDescriptor(String desc, IClassRemapper remapper, boolean unmap) {
        StringBuilder sb = new StringBuilder();
        StringBuilder token = null;
        int pos = 0;
        do {
            if (pos >= desc.length()) {
                if (token == null) return sb.toString();
                throw new IllegalArgumentException("Invalid descriptor '" + desc + "', missing ';'");
            }
            char c = desc.charAt(pos);
            if (token != null) {
                if (c == ';') {
                    sb.append('L').append(ObfuscationUtil.remap(token.toString(), remapper, unmap)).append(';');
                    token = null;
                } else {
                    token.append(c);
                }
            } else if (c == 'L') {
                token = new StringBuilder();
            } else {
                sb.append(c);
            }
            ++pos;
        } while (true);
    }

    private static Object remap(String typeName, IClassRemapper remapper, boolean unmap) {
        String result;
        String string;
        String string2 = result = unmap ? remapper.unmap(typeName) : remapper.map(typeName);
        if (result != null) {
            string = result;
            return string;
        }
        string = typeName;
        return string;
    }

    public static interface IClassRemapper {
        public String map(String var1);

        public String unmap(String var1);
    }

}

