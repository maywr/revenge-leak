/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.lib;

import org.spongepowered.asm.lib.ByteVector;

public class TypePath {
    public static final int ARRAY_ELEMENT = 0;
    public static final int INNER_TYPE = 1;
    public static final int WILDCARD_BOUND = 2;
    public static final int TYPE_ARGUMENT = 3;
    byte[] b;
    int offset;

    TypePath(byte[] b, int offset) {
        this.b = b;
        this.offset = offset;
    }

    public int getLength() {
        return this.b[this.offset];
    }

    public int getStep(int index) {
        return this.b[this.offset + 2 * index + 1];
    }

    public int getStepArgument(int index) {
        return this.b[this.offset + 2 * index + 2];
    }

    public static TypePath fromString(String typePath) {
        if (typePath == null) return null;
        if (typePath.length() == 0) {
            return null;
        }
        int n = typePath.length();
        ByteVector out = new ByteVector(n);
        out.putByte(0);
        int i = 0;
        do {
            char c;
            if (i >= n) {
                out.data[0] = (byte)(out.length / 2);
                return new TypePath(out.data, 0);
            }
            if ((c = typePath.charAt(i++)) == '[') {
                out.put11(0, 0);
                continue;
            }
            if (c == '.') {
                out.put11(1, 0);
                continue;
            }
            if (c == '*') {
                out.put11(2, 0);
                continue;
            }
            if (c < '0' || c > '9') continue;
            int typeArg = c - 48;
            while (i < n && (c = typePath.charAt(i)) >= '0' && c <= '9') {
                typeArg = typeArg * 10 + c - 48;
                ++i;
            }
            if (i < n && typePath.charAt(i) == ';') {
                ++i;
            }
            out.put11(3, typeArg);
        } while (true);
    }

    /*
     * Unable to fully structure code
     */
    public String toString() {
        length = this.getLength();
        result = new StringBuilder(length * 2);
        i = 0;
        while (i < length) {
            switch (this.getStep(i)) {
                case 0: {
                    result.append('[');
                    ** break;
                }
                case 1: {
                    result.append('.');
                    ** break;
                }
                case 2: {
                    result.append('*');
                    ** break;
                }
                case 3: {
                    result.append(this.getStepArgument(i)).append(';');
                    ** break;
                }
            }
            result.append('_');
lbl24: // 5 sources:
            ++i;
        }
        return result.toString();
    }
}

