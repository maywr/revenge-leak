/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.lib;

import org.spongepowered.asm.lib.AnnotationVisitor;
import org.spongepowered.asm.lib.ByteVector;
import org.spongepowered.asm.lib.ClassWriter;
import org.spongepowered.asm.lib.Item;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.TypePath;

final class AnnotationWriter
extends AnnotationVisitor {
    private final ClassWriter cw;
    private int size;
    private final boolean named;
    private final ByteVector bv;
    private final ByteVector parent;
    private final int offset;
    AnnotationWriter next;
    AnnotationWriter prev;

    AnnotationWriter(ClassWriter cw, boolean named, ByteVector bv, ByteVector parent, int offset) {
        super(327680);
        this.cw = cw;
        this.named = named;
        this.bv = bv;
        this.parent = parent;
        this.offset = offset;
    }

    @Override
    public void visit(String name, Object value) {
        ++this.size;
        if (this.named) {
            this.bv.putShort(this.cw.newUTF8(name));
        }
        if (value instanceof String) {
            this.bv.put12(115, this.cw.newUTF8((String)value));
            return;
        }
        if (value instanceof Byte) {
            this.bv.put12(66, this.cw.newInteger((int)((Byte)value).byteValue()).index);
            return;
        }
        if (value instanceof Boolean) {
            int v = (Boolean)value != false ? 1 : 0;
            this.bv.put12(90, this.cw.newInteger((int)v).index);
            return;
        }
        if (value instanceof Character) {
            this.bv.put12(67, this.cw.newInteger((int)((Character)value).charValue()).index);
            return;
        }
        if (value instanceof Short) {
            this.bv.put12(83, this.cw.newInteger((int)((Short)value).shortValue()).index);
            return;
        }
        if (value instanceof Type) {
            this.bv.put12(99, this.cw.newUTF8(((Type)value).getDescriptor()));
            return;
        }
        if (value instanceof byte[]) {
            byte[] v = (byte[])value;
            this.bv.put12(91, v.length);
            int i = 0;
            while (i < v.length) {
                this.bv.put12(66, this.cw.newInteger((int)v[i]).index);
                ++i;
            }
            return;
        }
        if (!(value instanceof boolean[])) {
            if (value instanceof short[]) {
                short[] v = (short[])value;
                this.bv.put12(91, v.length);
                int i = 0;
                while (i < v.length) {
                    this.bv.put12(83, this.cw.newInteger((int)v[i]).index);
                    ++i;
                }
                return;
            }
            if (value instanceof char[]) {
                char[] v = (char[])value;
                this.bv.put12(91, v.length);
                int i = 0;
                while (i < v.length) {
                    this.bv.put12(67, this.cw.newInteger((int)v[i]).index);
                    ++i;
                }
                return;
            }
            if (value instanceof int[]) {
                int[] v = (int[])value;
                this.bv.put12(91, v.length);
                int i = 0;
                while (i < v.length) {
                    this.bv.put12(73, this.cw.newInteger((int)v[i]).index);
                    ++i;
                }
                return;
            }
            if (value instanceof long[]) {
                long[] v = (long[])value;
                this.bv.put12(91, v.length);
                int i = 0;
                while (i < v.length) {
                    this.bv.put12(74, this.cw.newLong((long)v[i]).index);
                    ++i;
                }
                return;
            }
            if (value instanceof float[]) {
                float[] v = (float[])value;
                this.bv.put12(91, v.length);
                int i = 0;
                while (i < v.length) {
                    this.bv.put12(70, this.cw.newFloat((float)v[i]).index);
                    ++i;
                }
                return;
            }
            if (!(value instanceof double[])) {
                Item i = this.cw.newConstItem(value);
                this.bv.put12(".s.IFJDCS".charAt(i.type), i.index);
                return;
            }
            double[] v = (double[])value;
            this.bv.put12(91, v.length);
            int i = 0;
            while (i < v.length) {
                this.bv.put12(68, this.cw.newDouble((double)v[i]).index);
                ++i;
            }
            return;
        }
        boolean[] v = (boolean[])value;
        this.bv.put12(91, v.length);
        int i = 0;
        while (i < v.length) {
            this.bv.put12(90, this.cw.newInteger((int)(v[i] != false ? 1 : 0)).index);
            ++i;
        }
    }

    @Override
    public void visitEnum(String name, String desc, String value) {
        ++this.size;
        if (this.named) {
            this.bv.putShort(this.cw.newUTF8(name));
        }
        this.bv.put12(101, this.cw.newUTF8(desc)).putShort(this.cw.newUTF8(value));
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String desc) {
        ++this.size;
        if (this.named) {
            this.bv.putShort(this.cw.newUTF8(name));
        }
        this.bv.put12(64, this.cw.newUTF8(desc)).putShort(0);
        return new AnnotationWriter(this.cw, true, this.bv, this.bv, this.bv.length - 2);
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        ++this.size;
        if (this.named) {
            this.bv.putShort(this.cw.newUTF8(name));
        }
        this.bv.put12(91, 0);
        return new AnnotationWriter(this.cw, false, this.bv, this.bv, this.bv.length - 2);
    }

    @Override
    public void visitEnd() {
        if (this.parent == null) return;
        byte[] data = this.parent.data;
        data[this.offset] = (byte)(this.size >>> 8);
        data[this.offset + 1] = (byte)this.size;
    }

    int getSize() {
        int size = 0;
        AnnotationWriter aw = this;
        while (aw != null) {
            size += aw.bv.length;
            aw = aw.next;
        }
        return size;
    }

    void put(ByteVector out) {
        int n = 0;
        int size = 2;
        AnnotationWriter aw = this;
        AnnotationWriter last = null;
        while (aw != null) {
            ++n;
            size += aw.bv.length;
            aw.visitEnd();
            aw.prev = last;
            last = aw;
            aw = aw.next;
        }
        out.putInt(size);
        out.putShort(n);
        aw = last;
        while (aw != null) {
            out.putByteArray(aw.bv.data, 0, aw.bv.length);
            aw = aw.prev;
        }
    }

    static void put(AnnotationWriter[] panns, int off, ByteVector out) {
        int i;
        int size = 1 + 2 * (panns.length - off);
        for (i = off; i < panns.length; size += panns[i] == null ? 0 : panns[i].getSize(), ++i) {
        }
        out.putInt(size).putByte(panns.length - off);
        i = off;
        while (i < panns.length) {
            AnnotationWriter aw = panns[i];
            AnnotationWriter last = null;
            int n = 0;
            while (aw != null) {
                ++n;
                aw.visitEnd();
                aw.prev = last;
                last = aw;
                aw = aw.next;
            }
            out.putShort(n);
            aw = last;
            while (aw != null) {
                out.putByteArray(aw.bv.data, 0, aw.bv.length);
                aw = aw.prev;
            }
            ++i;
        }
    }

    /*
     * Unable to fully structure code
     */
    static void putTarget(int typeRef, TypePath typePath, ByteVector out) {
        switch (typeRef >>> 24) {
            case 0: 
            case 1: 
            case 22: {
                out.putShort(typeRef >>> 16);
                ** break;
            }
            case 19: 
            case 20: 
            case 21: {
                out.putByte(typeRef >>> 24);
                ** break;
            }
            case 71: 
            case 72: 
            case 73: 
            case 74: 
            case 75: {
                out.putInt(typeRef);
                ** break;
            }
        }
        out.put12(typeRef >>> 24, (typeRef & 16776960) >> 8);
lbl16: // 4 sources:
        if (typePath == null) {
            out.putByte(0);
            return;
        }
        length = typePath.b[typePath.offset] * 2 + 1;
        out.putByteArray(typePath.b, typePath.offset, length);
    }
}

