/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.lib;

import java.io.IOException;
import java.io.InputStream;
import org.spongepowered.asm.lib.AnnotationVisitor;
import org.spongepowered.asm.lib.Attribute;
import org.spongepowered.asm.lib.ByteVector;
import org.spongepowered.asm.lib.ClassVisitor;
import org.spongepowered.asm.lib.ClassWriter;
import org.spongepowered.asm.lib.Context;
import org.spongepowered.asm.lib.FieldVisitor;
import org.spongepowered.asm.lib.Handle;
import org.spongepowered.asm.lib.Item;
import org.spongepowered.asm.lib.Label;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.MethodWriter;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.TypePath;

public class ClassReader {
    static final boolean SIGNATURES = true;
    static final boolean ANNOTATIONS = true;
    static final boolean FRAMES = true;
    static final boolean WRITER = true;
    static final boolean RESIZE = true;
    public static final int SKIP_CODE = 1;
    public static final int SKIP_DEBUG = 2;
    public static final int SKIP_FRAMES = 4;
    public static final int EXPAND_FRAMES = 8;
    static final int EXPAND_ASM_INSNS = 256;
    public final byte[] b;
    private final int[] items;
    private final String[] strings;
    private final int maxStringLength;
    public final int header;

    public ClassReader(byte[] b) {
        this(b, 0, b.length);
    }

    public ClassReader(byte[] b, int off, int len) {
        this.b = b;
        if (this.readShort(off + 6) > 52) {
            throw new IllegalArgumentException();
        }
        this.items = new int[this.readUnsignedShort(off + 8)];
        int n = this.items.length;
        this.strings = new String[n];
        int max = 0;
        int index = off + 10;
        int i = 1;
        do {
            int size;
            if (i >= n) {
                this.maxStringLength = max;
                this.header = index;
                return;
            }
            this.items[i] = index + 1;
            switch (b[index]) {
                case 3: 
                case 4: 
                case 9: 
                case 10: 
                case 11: 
                case 12: 
                case 18: {
                    size = 5;
                    break;
                }
                case 5: 
                case 6: {
                    size = 9;
                    ++i;
                    break;
                }
                case 1: {
                    size = 3 + this.readUnsignedShort(index + 1);
                    if (size <= max) break;
                    max = size;
                    break;
                }
                case 15: {
                    size = 4;
                    break;
                }
                default: {
                    size = 3;
                }
            }
            index += size;
            ++i;
        } while (true);
    }

    public int getAccess() {
        return this.readUnsignedShort(this.header);
    }

    public String getClassName() {
        return this.readClass(this.header + 2, new char[this.maxStringLength]);
    }

    public String getSuperName() {
        return this.readClass(this.header + 4, new char[this.maxStringLength]);
    }

    public String[] getInterfaces() {
        int index = this.header + 6;
        int n = this.readUnsignedShort(index);
        String[] interfaces = new String[n];
        if (n <= 0) return interfaces;
        char[] buf = new char[this.maxStringLength];
        int i = 0;
        while (i < n) {
            interfaces[i] = this.readClass(index += 2, buf);
            ++i;
        }
        return interfaces;
    }

    /*
     * Unable to fully structure code
     */
    void copyPool(ClassWriter classWriter) {
        buf = new char[this.maxStringLength];
        ll = this.items.length;
        items2 = new Item[ll];
        i = 1;
        do {
            if (i >= ll) {
                off = this.items[1] - 1;
                classWriter.pool.putByteArray(this.b, off, this.header - off);
                classWriter.items = items2;
                classWriter.threshold = (int)(0.75 * (double)ll);
                classWriter.index = ll;
                return;
            }
            index = this.items[i];
            tag = this.b[index - 1];
            item = new Item(i);
            switch (tag) {
                case 9: 
                case 10: 
                case 11: {
                    nameType = this.items[this.readUnsignedShort(index + 2)];
                    item.set(tag, this.readClass(index, buf), this.readUTF8(nameType, buf), this.readUTF8(nameType + 2, buf));
                    ** break;
                }
                case 3: {
                    item.set(this.readInt(index));
                    ** break;
                }
                case 4: {
                    item.set(Float.intBitsToFloat(this.readInt(index)));
                    ** break;
                }
                case 12: {
                    item.set(tag, this.readUTF8(index, buf), this.readUTF8(index + 2, buf), null);
                    ** break;
                }
                case 5: {
                    item.set(this.readLong(index));
                    ++i;
                    ** break;
                }
                case 6: {
                    item.set(Double.longBitsToDouble(this.readLong(index)));
                    ++i;
                    ** break;
                }
                case 1: {
                    s = this.strings[i];
                    if (s == null) {
                        index = this.items[i];
                        s = this.strings[i] = this.readUTF(index + 2, this.readUnsignedShort(index), buf);
                    }
                    item.set(tag, s, null, null);
                    ** break;
                }
                case 15: {
                    fieldOrMethodRef = this.items[this.readUnsignedShort(index + 1)];
                    nameType = this.items[this.readUnsignedShort(fieldOrMethodRef + 2)];
                    item.set(20 + this.readByte(index), this.readClass(fieldOrMethodRef, buf), this.readUTF8(nameType, buf), this.readUTF8(nameType + 2, buf));
                    ** break;
                }
                case 18: {
                    if (classWriter.bootstrapMethods == null) {
                        this.copyBootstrapMethods(classWriter, items2, buf);
                    }
                    nameType = this.items[this.readUnsignedShort(index + 2)];
                    item.set(this.readUTF8(nameType, buf), this.readUTF8(nameType + 2, buf), this.readUnsignedShort(index));
                    ** break;
                }
            }
            item.set(tag, this.readUTF8(index, buf), null, null);
lbl58: // 10 sources:
            index2 = item.hashCode % items2.length;
            item.next = items2[index2];
            items2[index2] = item;
            ++i;
        } while (true);
    }

    private void copyBootstrapMethods(ClassWriter classWriter, Item[] items, char[] c) {
        int u = this.getAttributes();
        boolean found = false;
        for (int i = this.readUnsignedShort((int)u); i > 0; u += 6 + this.readInt((int)(u + 4)), --i) {
            String attrName = this.readUTF8(u + 2, c);
            if (!"BootstrapMethods".equals(attrName)) continue;
            found = true;
            break;
        }
        if (!found) {
            return;
        }
        int boostrapMethodCount = this.readUnsignedShort(u + 8);
        int j = 0;
        int v = u + 10;
        do {
            if (j >= boostrapMethodCount) {
                int attrSize = this.readInt(u + 4);
                ByteVector bootstrapMethods = new ByteVector(attrSize + 62);
                bootstrapMethods.putByteArray(this.b, u + 10, attrSize - 2);
                classWriter.bootstrapMethodsCount = boostrapMethodCount;
                classWriter.bootstrapMethods = bootstrapMethods;
                return;
            }
            int position = v - u - 10;
            int hashCode = this.readConst(this.readUnsignedShort(v), c).hashCode();
            for (int k = this.readUnsignedShort((int)(v + 2)); k > 0; hashCode ^= this.readConst((int)this.readUnsignedShort((int)(v + 4)), (char[])c).hashCode(), v += 2, --k) {
            }
            v += 4;
            Item item = new Item(j);
            item.set(position, hashCode & Integer.MAX_VALUE);
            int index = item.hashCode % items.length;
            item.next = items[index];
            items[index] = item;
            ++j;
        } while (true);
    }

    public ClassReader(InputStream is) throws IOException {
        this(ClassReader.readClass(is, false));
    }

    public ClassReader(String name) throws IOException {
        this(ClassReader.readClass(ClassLoader.getSystemResourceAsStream(name.replace('.', '/') + ".class"), true));
    }

    private static byte[] readClass(InputStream is, boolean close) throws IOException {
        if (is == null) {
            throw new IOException("Class not found");
        }
        try {
            byte[] b = new byte[is.available()];
            int len = 0;
            do {
                int n;
                if ((n = is.read(b, len, b.length - len)) == -1) {
                    byte[] c;
                    if (len < b.length) {
                        c = new byte[len];
                        System.arraycopy(b, 0, c, 0, len);
                        b = c;
                    }
                    c = b;
                    return c;
                }
                if ((len += n) != b.length) continue;
                int last = is.read();
                if (last < 0) {
                    byte[] arrby = b;
                    return arrby;
                }
                byte[] c = new byte[b.length + 1000];
                System.arraycopy(b, 0, c, 0, len);
                c[len++] = (byte)last;
                b = c;
            } while (true);
        }
        finally {
            if (close) {
                is.close();
            }
        }
    }

    public void accept(ClassVisitor classVisitor, int flags) {
        this.accept(classVisitor, new Attribute[0], flags);
    }

    public void accept(ClassVisitor classVisitor, Attribute[] attrs, int flags) {
        int v;
        int i;
        int u = this.header;
        char[] c = new char[this.maxStringLength];
        Context context = new Context();
        context.attrs = attrs;
        context.flags = flags;
        context.buffer = c;
        int access = this.readUnsignedShort(u);
        String name = this.readClass(u + 2, c);
        String superClass = this.readClass(u + 4, c);
        String[] interfaces = new String[this.readUnsignedShort(u + 6)];
        u += 8;
        for (int i2 = 0; i2 < interfaces.length; u += 2, ++i2) {
            interfaces[i2] = this.readClass(u, c);
        }
        String signature = null;
        String sourceFile = null;
        String sourceDebug = null;
        String enclosingOwner = null;
        String enclosingName = null;
        String enclosingDesc = null;
        int anns = 0;
        int ianns = 0;
        int tanns = 0;
        int itanns = 0;
        int innerClasses = 0;
        Attribute attributes = null;
        u = this.getAttributes();
        for (i = this.readUnsignedShort((int)u); i > 0; u += 6 + this.readInt((int)(u + 4)), --i) {
            String attrName = this.readUTF8(u + 2, c);
            if ("SourceFile".equals(attrName)) {
                sourceFile = this.readUTF8(u + 8, c);
                continue;
            }
            if ("InnerClasses".equals(attrName)) {
                innerClasses = u + 8;
                continue;
            }
            if ("EnclosingMethod".equals(attrName)) {
                enclosingOwner = this.readClass(u + 8, c);
                int item = this.readUnsignedShort(u + 10);
                if (item == 0) continue;
                enclosingName = this.readUTF8(this.items[item], c);
                enclosingDesc = this.readUTF8(this.items[item] + 2, c);
                continue;
            }
            if ("Signature".equals(attrName)) {
                signature = this.readUTF8(u + 8, c);
                continue;
            }
            if ("RuntimeVisibleAnnotations".equals(attrName)) {
                anns = u + 8;
                continue;
            }
            if ("RuntimeVisibleTypeAnnotations".equals(attrName)) {
                tanns = u + 8;
                continue;
            }
            if ("Deprecated".equals(attrName)) {
                access |= 131072;
                continue;
            }
            if ("Synthetic".equals(attrName)) {
                access |= 266240;
                continue;
            }
            if ("SourceDebugExtension".equals(attrName)) {
                int len = this.readInt(u + 4);
                sourceDebug = this.readUTF(u + 8, len, new char[len]);
                continue;
            }
            if ("RuntimeInvisibleAnnotations".equals(attrName)) {
                ianns = u + 8;
                continue;
            }
            if ("RuntimeInvisibleTypeAnnotations".equals(attrName)) {
                itanns = u + 8;
                continue;
            }
            if ("BootstrapMethods".equals(attrName)) {
                int[] bootstrapMethods = new int[this.readUnsignedShort(u + 8)];
                int v2 = u + 10;
                for (int j = 0; j < bootstrapMethods.length; v2 += 2 + this.readUnsignedShort((int)(v2 + 2)) << 1, ++j) {
                    bootstrapMethods[j] = v2;
                }
                context.bootstrapMethods = bootstrapMethods;
                continue;
            }
            Attribute attr = this.readAttribute(attrs, attrName, u + 8, this.readInt(u + 4), c, -1, null);
            if (attr == null) continue;
            attr.next = attributes;
            attributes = attr;
        }
        classVisitor.visit(this.readInt(this.items[1] - 7), access, name, signature, superClass, interfaces);
        if ((flags & 2) == 0 && (sourceFile != null || sourceDebug != null)) {
            classVisitor.visitSource(sourceFile, sourceDebug);
        }
        if (enclosingOwner != null) {
            classVisitor.visitOuterClass(enclosingOwner, enclosingName, enclosingDesc);
        }
        if (anns != 0) {
            int v3 = anns + 2;
            for (i = this.readUnsignedShort((int)anns); i > 0; --i) {
                v3 = this.readAnnotationValues(v3 + 2, c, true, classVisitor.visitAnnotation(this.readUTF8(v3, c), true));
            }
        }
        if (ianns != 0) {
            v = ianns + 2;
            for (i = this.readUnsignedShort((int)ianns); i > 0; --i) {
                v = this.readAnnotationValues(v + 2, c, true, classVisitor.visitAnnotation(this.readUTF8(v, c), false));
            }
        }
        if (tanns != 0) {
            v = tanns + 2;
            for (i = this.readUnsignedShort((int)tanns); i > 0; --i) {
                v = this.readAnnotationTarget(context, v);
                v = this.readAnnotationValues(v + 2, c, true, classVisitor.visitTypeAnnotation(context.typeRef, context.typePath, this.readUTF8(v, c), true));
            }
        }
        if (itanns != 0) {
            v = itanns + 2;
            for (i = this.readUnsignedShort((int)itanns); i > 0; --i) {
                v = this.readAnnotationTarget(context, v);
                v = this.readAnnotationValues(v + 2, c, true, classVisitor.visitTypeAnnotation(context.typeRef, context.typePath, this.readUTF8(v, c), false));
            }
        }
        while (attributes != null) {
            Attribute attr = attributes.next;
            attributes.next = null;
            classVisitor.visitAttribute(attributes);
            attributes = attr;
        }
        if (innerClasses != 0) {
            int v4 = innerClasses + 2;
            for (int i3 = this.readUnsignedShort((int)innerClasses); i3 > 0; v4 += 8, --i3) {
                classVisitor.visitInnerClass(this.readClass(v4, c), this.readClass(v4 + 2, c), this.readUTF8(v4 + 4, c), this.readUnsignedShort(v4 + 6));
            }
        }
        u = this.header + 10 + 2 * interfaces.length;
        for (i = this.readUnsignedShort((int)(u - 2)); i > 0; --i) {
            u = this.readField(classVisitor, context, u);
        }
        i = this.readUnsignedShort((u += 2) - 2);
        do {
            if (i <= 0) {
                classVisitor.visitEnd();
                return;
            }
            u = this.readMethod(classVisitor, context, u);
            --i;
        } while (true);
    }

    private int readField(ClassVisitor classVisitor, Context context, int u) {
        int i;
        int v;
        char[] c = context.buffer;
        int access = this.readUnsignedShort(u);
        String name = this.readUTF8(u + 2, c);
        String desc = this.readUTF8(u + 4, c);
        String signature = null;
        int anns = 0;
        int ianns = 0;
        int tanns = 0;
        int itanns = 0;
        Object value = null;
        Attribute attributes = null;
        for (int i2 = this.readUnsignedShort((int)(u += 6)); i2 > 0; u += 6 + this.readInt((int)(u + 4)), --i2) {
            String attrName = this.readUTF8(u + 2, c);
            if ("ConstantValue".equals(attrName)) {
                int item = this.readUnsignedShort(u + 8);
                value = item == 0 ? null : this.readConst(item, c);
                continue;
            }
            if ("Signature".equals(attrName)) {
                signature = this.readUTF8(u + 8, c);
                continue;
            }
            if ("Deprecated".equals(attrName)) {
                access |= 131072;
                continue;
            }
            if ("Synthetic".equals(attrName)) {
                access |= 266240;
                continue;
            }
            if ("RuntimeVisibleAnnotations".equals(attrName)) {
                anns = u + 8;
                continue;
            }
            if ("RuntimeVisibleTypeAnnotations".equals(attrName)) {
                tanns = u + 8;
                continue;
            }
            if ("RuntimeInvisibleAnnotations".equals(attrName)) {
                ianns = u + 8;
                continue;
            }
            if ("RuntimeInvisibleTypeAnnotations".equals(attrName)) {
                itanns = u + 8;
                continue;
            }
            Attribute attr = this.readAttribute(context.attrs, attrName, u + 8, this.readInt(u + 4), c, -1, null);
            if (attr == null) continue;
            attr.next = attributes;
            attributes = attr;
        }
        u += 2;
        FieldVisitor fv = classVisitor.visitField(access, name, desc, signature, value);
        if (fv == null) {
            return u;
        }
        if (anns != 0) {
            v = anns + 2;
            for (int i3 = this.readUnsignedShort((int)anns); i3 > 0; --i3) {
                v = this.readAnnotationValues(v + 2, c, true, fv.visitAnnotation(this.readUTF8(v, c), true));
            }
        }
        if (ianns != 0) {
            v = ianns + 2;
            for (i = this.readUnsignedShort((int)ianns); i > 0; --i) {
                v = this.readAnnotationValues(v + 2, c, true, fv.visitAnnotation(this.readUTF8(v, c), false));
            }
        }
        if (tanns != 0) {
            v = tanns + 2;
            for (i = this.readUnsignedShort((int)tanns); i > 0; --i) {
                v = this.readAnnotationTarget(context, v);
                v = this.readAnnotationValues(v + 2, c, true, fv.visitTypeAnnotation(context.typeRef, context.typePath, this.readUTF8(v, c), true));
            }
        }
        if (itanns != 0) {
            v = itanns + 2;
            for (i = this.readUnsignedShort((int)itanns); i > 0; --i) {
                v = this.readAnnotationTarget(context, v);
                v = this.readAnnotationValues(v + 2, c, true, fv.visitTypeAnnotation(context.typeRef, context.typePath, this.readUTF8(v, c), false));
            }
        }
        do {
            if (attributes == null) {
                fv.visitEnd();
                return u;
            }
            Attribute attr = attributes.next;
            attributes.next = null;
            fv.visitAttribute(attributes);
            attributes = attr;
        } while (true);
    }

    private int readMethod(ClassVisitor classVisitor, Context context, int u) {
        int v;
        char[] c = context.buffer;
        context.access = this.readUnsignedShort(u);
        context.name = this.readUTF8(u + 2, c);
        context.desc = this.readUTF8(u + 4, c);
        int code = 0;
        int exception = 0;
        String[] exceptions = null;
        String signature = null;
        int methodParameters = 0;
        int anns = 0;
        int ianns = 0;
        int tanns = 0;
        int itanns = 0;
        int dann = 0;
        int mpanns = 0;
        int impanns = 0;
        int firstAttribute = u += 6;
        Attribute attributes = null;
        for (int i = this.readUnsignedShort((int)u); i > 0; u += 6 + this.readInt((int)(u + 4)), --i) {
            String attrName = this.readUTF8(u + 2, c);
            if ("Code".equals(attrName)) {
                if ((context.flags & 1) != 0) continue;
                code = u + 8;
                continue;
            }
            if ("Exceptions".equals(attrName)) {
                exceptions = new String[this.readUnsignedShort(u + 8)];
                exception = u + 10;
                for (int j = 0; j < exceptions.length; exception += 2, ++j) {
                    exceptions[j] = this.readClass(exception, c);
                }
                continue;
            }
            if ("Signature".equals(attrName)) {
                signature = this.readUTF8(u + 8, c);
                continue;
            }
            if ("Deprecated".equals(attrName)) {
                context.access |= 131072;
                continue;
            }
            if ("RuntimeVisibleAnnotations".equals(attrName)) {
                anns = u + 8;
                continue;
            }
            if ("RuntimeVisibleTypeAnnotations".equals(attrName)) {
                tanns = u + 8;
                continue;
            }
            if ("AnnotationDefault".equals(attrName)) {
                dann = u + 8;
                continue;
            }
            if ("Synthetic".equals(attrName)) {
                context.access |= 266240;
                continue;
            }
            if ("RuntimeInvisibleAnnotations".equals(attrName)) {
                ianns = u + 8;
                continue;
            }
            if ("RuntimeInvisibleTypeAnnotations".equals(attrName)) {
                itanns = u + 8;
                continue;
            }
            if ("RuntimeVisibleParameterAnnotations".equals(attrName)) {
                mpanns = u + 8;
                continue;
            }
            if ("RuntimeInvisibleParameterAnnotations".equals(attrName)) {
                impanns = u + 8;
                continue;
            }
            if ("MethodParameters".equals(attrName)) {
                methodParameters = u + 8;
                continue;
            }
            Attribute attr = this.readAttribute(context.attrs, attrName, u + 8, this.readInt(u + 4), c, -1, null);
            if (attr == null) continue;
            attr.next = attributes;
            attributes = attr;
        }
        u += 2;
        MethodVisitor mv = classVisitor.visitMethod(context.access, context.name, context.desc, signature, exceptions);
        if (mv == null) {
            return u;
        }
        if (mv instanceof MethodWriter) {
            MethodWriter mw = (MethodWriter)mv;
            if (mw.cw.cr == this && signature == mw.signature) {
                boolean sameExceptions = false;
                if (exceptions == null) {
                    sameExceptions = mw.exceptionCount == 0;
                } else if (exceptions.length == mw.exceptionCount) {
                    sameExceptions = true;
                    for (int j = exceptions.length - 1; j >= 0; --j) {
                        if (mw.exceptions[j] == this.readUnsignedShort(exception -= 2)) continue;
                        sameExceptions = false;
                        break;
                    }
                }
                if (sameExceptions) {
                    mw.classReaderOffset = firstAttribute;
                    mw.classReaderLength = u - firstAttribute;
                    return u;
                }
            }
        }
        if (methodParameters != 0) {
            v = methodParameters + 1;
            for (int i = this.b[methodParameters] & 255; i > 0; --i, v += 4) {
                mv.visitParameter(this.readUTF8(v, c), this.readUnsignedShort(v + 2));
            }
        }
        if (dann != 0) {
            AnnotationVisitor dv = mv.visitAnnotationDefault();
            this.readAnnotationValue(dann, c, null, dv);
            if (dv != null) {
                dv.visitEnd();
            }
        }
        if (anns != 0) {
            v = anns + 2;
            for (int i = this.readUnsignedShort((int)anns); i > 0; --i) {
                v = this.readAnnotationValues(v + 2, c, true, mv.visitAnnotation(this.readUTF8(v, c), true));
            }
        }
        if (ianns != 0) {
            v = ianns + 2;
            for (int i = this.readUnsignedShort((int)ianns); i > 0; --i) {
                v = this.readAnnotationValues(v + 2, c, true, mv.visitAnnotation(this.readUTF8(v, c), false));
            }
        }
        if (tanns != 0) {
            v = tanns + 2;
            for (int i = this.readUnsignedShort((int)tanns); i > 0; --i) {
                v = this.readAnnotationTarget(context, v);
                v = this.readAnnotationValues(v + 2, c, true, mv.visitTypeAnnotation(context.typeRef, context.typePath, this.readUTF8(v, c), true));
            }
        }
        if (itanns != 0) {
            v = itanns + 2;
            for (int i = this.readUnsignedShort((int)itanns); i > 0; --i) {
                v = this.readAnnotationTarget(context, v);
                v = this.readAnnotationValues(v + 2, c, true, mv.visitTypeAnnotation(context.typeRef, context.typePath, this.readUTF8(v, c), false));
            }
        }
        if (mpanns != 0) {
            this.readParameterAnnotations(mv, context, mpanns, true);
        }
        if (impanns != 0) {
            this.readParameterAnnotations(mv, context, impanns, false);
        }
        while (attributes != null) {
            Attribute attr = attributes.next;
            attributes.next = null;
            mv.visitAttribute(attributes);
            attributes = attr;
        }
        if (code != 0) {
            mv.visitCode();
            this.readCode(mv, context, code);
        }
        mv.visitEnd();
        return u;
    }

    /*
     * Unable to fully structure code
     */
    private void readCode(MethodVisitor mv, Context context, int u) {
        b = this.b;
        c = context.buffer;
        maxStack = this.readUnsignedShort(u);
        maxLocals = this.readUnsignedShort(u + 2);
        codeLength = this.readInt(u + 4);
        codeStart = u += 8;
        codeEnd = u + codeLength;
        context.labels = new Label[codeLength + 2];
        labels = context.labels;
        this.readLabel(codeLength + 1, labels);
        block31 : while (u < codeEnd) {
            offset = u - codeStart;
            opcode = b[u] & 255;
            switch (ClassWriter.TYPE[opcode]) {
                case 0: 
                case 4: {
                    ++u;
                    continue block31;
                }
                case 9: {
                    this.readLabel(offset + this.readShort(u + 1), labels);
                    u += 3;
                    continue block31;
                }
                case 18: {
                    this.readLabel(offset + this.readUnsignedShort(u + 1), labels);
                    u += 3;
                    continue block31;
                }
                case 10: {
                    this.readLabel(offset + this.readInt(u + 1), labels);
                    u += 5;
                    continue block31;
                }
                case 17: {
                    opcode = b[u + 1] & 255;
                    if (opcode == 132) {
                        u += 6;
                        continue block31;
                    }
                    u += 4;
                    continue block31;
                }
                case 14: {
                    u = u + 4 - (offset & 3);
                    this.readLabel(offset + this.readInt(u), labels);
                    for (i = this.readInt((int)(u + 8)) - this.readInt((int)(u + 4)) + 1; i > 0; u += 4, --i) {
                        this.readLabel(offset + this.readInt(u + 12), labels);
                    }
                    u += 12;
                    continue block31;
                }
                case 15: {
                    u = u + 4 - (offset & 3);
                    this.readLabel(offset + this.readInt(u), labels);
                    for (i = this.readInt((int)(u + 4)); i > 0; u += 8, --i) {
                        this.readLabel(offset + this.readInt(u + 12), labels);
                    }
                    u += 8;
                    continue block31;
                }
                case 1: 
                case 3: 
                case 11: {
                    u += 2;
                    continue block31;
                }
                case 2: 
                case 5: 
                case 6: 
                case 12: 
                case 13: {
                    u += 3;
                    continue block31;
                }
                case 7: 
                case 8: {
                    u += 5;
                    continue block31;
                }
            }
            u += 4;
        }
        for (i = this.readUnsignedShort((int)u); i > 0; u += 8, --i) {
            start = this.readLabel(this.readUnsignedShort(u + 2), labels);
            end = this.readLabel(this.readUnsignedShort(u + 4), labels);
            handler = this.readLabel(this.readUnsignedShort(u + 6), labels);
            type = this.readUTF8(this.items[this.readUnsignedShort(u + 8)], c);
            mv.visitTryCatchBlock(start, end, handler, type);
        }
        tanns = null;
        itanns = null;
        tann = 0;
        itann = 0;
        ntoff = -1;
        nitoff = -1;
        varTable = 0;
        varTypeTable = 0;
        zip = true;
        unzip = (context.flags & 8) != 0;
        stackMap = 0;
        stackMapSize = 0;
        frameCount = 0;
        frame = null;
        attributes = null;
        i = this.readUnsignedShort(u += 2);
        do {
            block98 : {
                block101 : {
                    block96 : {
                        block100 : {
                            block99 : {
                                block97 : {
                                    if (i <= 0) break block96;
                                    attrName = this.readUTF8(u + 2, c);
                                    if (!"LocalVariableTable".equals(attrName)) break block97;
                                    if ((context.flags & 2) == 0) {
                                        varTable = u + 8;
                                        v = u;
                                        for (j = this.readUnsignedShort((int)(u + 8)); j > 0; v += 10, --j) {
                                            label = this.readUnsignedShort(v + 10);
                                            if (labels[label] == null) {
                                                this.readLabel((int)label, (Label[])labels).status |= 1;
                                            }
                                            if (labels[label += this.readUnsignedShort(v + 12)] != null) continue;
                                            this.readLabel((int)label, (Label[])labels).status |= 1;
                                        }
                                    }
                                    break block98;
                                }
                                if (!"LocalVariableTypeTable".equals(attrName)) break block99;
                                varTypeTable = u + 8;
                                break block98;
                            }
                            if (!"LineNumberTable".equals(attrName)) break block100;
                            if ((context.flags & 2) != 0) break block98;
                            v = u;
                            break block101;
                        }
                        if ("RuntimeVisibleTypeAnnotations".equals(attrName)) {
                            tanns = this.readTypeAnnotations(mv, context, u + 8, true);
                            ntoff = tanns.length == 0 || this.readByte(tanns[0]) < 67 ? -1 : this.readUnsignedShort(tanns[0] + 1);
                        } else if ("RuntimeInvisibleTypeAnnotations".equals(attrName)) {
                            itanns = this.readTypeAnnotations(mv, context, u + 8, false);
                            nitoff = itanns.length == 0 || this.readByte(itanns[0]) < 67 ? -1 : this.readUnsignedShort(itanns[0] + 1);
                        } else if ("StackMapTable".equals(attrName)) {
                            if ((context.flags & 4) == 0) {
                                stackMap = u + 10;
                                stackMapSize = this.readInt(u + 4);
                                frameCount = this.readUnsignedShort(u + 8);
                            }
                        } else if ("StackMap".equals(attrName)) {
                            if ((context.flags & 4) == 0) {
                                zip = false;
                                stackMap = u + 10;
                                stackMapSize = this.readInt(u + 4);
                                frameCount = this.readUnsignedShort(u + 8);
                            }
                        } else {
                            for (j = 0; j < context.attrs.length; ++j) {
                                if (!context.attrs[j].type.equals(attrName) || (attr = context.attrs[j].read(this, u + 8, this.readInt(u + 4), c, codeStart - 8, labels)) == null) continue;
                                attr.next = attributes;
                                attributes = attr;
                            }
                        }
                        break block98;
                    }
                    u += 2;
                    if (stackMap != 0) {
                        frame = context;
                        frame.offset = -1;
                        frame.mode = 0;
                        frame.localCount = 0;
                        frame.localDiff = 0;
                        frame.stackCount = 0;
                        frame.local = new Object[maxLocals];
                        frame.stack = new Object[maxStack];
                        if (unzip) {
                            this.getImplicitFrame(context);
                        }
                        for (i = stackMap; i < stackMap + stackMapSize - 2; ++i) {
                            if (b[i] != 8 || (v = this.readUnsignedShort(i + 1)) < 0 || v >= codeLength || (b[codeStart + v] & 255) != 187) continue;
                            this.readLabel(v, labels);
                        }
                    }
                    if ((context.flags & 256) != 0) {
                        mv.visitFrame(-1, maxLocals, null, 0, null);
                    }
                    break;
                }
                for (j = this.readUnsignedShort((int)(u + 8)); j > 0; v += 4, --j) {
                    label = this.readUnsignedShort(v + 10);
                    if (labels[label] == null) {
                        this.readLabel((int)label, (Label[])labels).status |= 1;
                    }
                    l = labels[label];
                    while (l.line > 0) {
                        if (l.next == null) {
                            l.next = new Label();
                        }
                        l = l.next;
                    }
                    l.line = this.readUnsignedShort(v + 12);
                }
            }
            u += 6 + this.readInt(u + 4);
            --i;
        } while (true);
        opcodeDelta = (context.flags & 256) == 0 ? -33 : 0;
        u = codeStart;
        block41 : do {
            block103 : {
                block102 : {
                    if (u >= codeEnd) break block102;
                    offset = u - codeStart;
                    l = labels[offset];
                    if (l != null) {
                        next = l.next;
                        l.next = null;
                        mv.visitLabel(l);
                        if ((context.flags & 2) == 0 && l.line > 0) {
                            mv.visitLineNumber(l.line, l);
                            while (next != null) {
                                mv.visitLineNumber(next.line, l);
                                next = next.next;
                            }
                        }
                    }
                    break block103;
                }
                if (labels[codeLength] != null) {
                    mv.visitLabel(labels[codeLength]);
                }
                if ((context.flags & 2) == 0 && varTable != 0) {
                    typeTable = null;
                    if (varTypeTable != 0) {
                        u = varTypeTable + 2;
                        typeTable = new int[this.readUnsignedShort(varTypeTable) * 3];
                        i = typeTable.length;
                        while (i > 0) {
                            typeTable[--i] = u + 6;
                            typeTable[--i] = this.readUnsignedShort(u + 8);
                            typeTable[--i] = this.readUnsignedShort(u);
                            u += 10;
                        }
                    }
                    u = varTable + 2;
                    i = this.readUnsignedShort(varTable);
                    break;
                }
                ** GOTO lbl393
            }
            while (frame != null && (frame.offset == offset || frame.offset == -1)) {
                if (frame.offset != -1) {
                    if (!zip || unzip) {
                        mv.visitFrame(-1, frame.localCount, frame.local, frame.stackCount, frame.stack);
                    } else {
                        mv.visitFrame(frame.mode, frame.localDiff, frame.local, frame.stackCount, frame.stack);
                    }
                }
                if (frameCount > 0) {
                    stackMap = this.readFrame(stackMap, zip, unzip, frame);
                    --frameCount;
                    continue;
                }
                frame = null;
            }
            opcode = b[u] & 255;
            switch (ClassWriter.TYPE[opcode]) {
                case 0: {
                    mv.visitInsn(opcode);
                    ++u;
                    ** break;
                }
                case 4: {
                    if (opcode > 54) {
                        mv.visitVarInsn(54 + ((opcode -= 59) >> 2), opcode & 3);
                    } else {
                        mv.visitVarInsn(21 + ((opcode -= 26) >> 2), opcode & 3);
                    }
                    ++u;
                    ** break;
                }
                case 9: {
                    mv.visitJumpInsn(opcode, labels[offset + this.readShort(u + 1)]);
                    u += 3;
                    ** break;
                }
                case 10: {
                    mv.visitJumpInsn(opcode + opcodeDelta, labels[offset + this.readInt(u + 1)]);
                    u += 5;
                    ** break;
                }
                case 18: {
                    opcode = opcode < 218 ? opcode - 49 : opcode - 20;
                    target = labels[offset + this.readUnsignedShort(u + 1)];
                    if (opcode == 167 || opcode == 168) {
                        mv.visitJumpInsn(opcode + 33, target);
                    } else {
                        opcode = opcode <= 166 ? (opcode + 1 ^ 1) - 1 : opcode ^ 1;
                        endif = new Label();
                        mv.visitJumpInsn(opcode, endif);
                        mv.visitJumpInsn(200, target);
                        mv.visitLabel(endif);
                        if (stackMap != 0 && (frame == null || frame.offset != offset + 3)) {
                            mv.visitFrame(256, 0, null, 0, null);
                        }
                    }
                    u += 3;
                    ** break;
                }
                case 17: {
                    opcode = b[u + 1] & 255;
                    if (opcode == 132) {
                        mv.visitIincInsn(this.readUnsignedShort(u + 2), this.readShort(u + 4));
                        u += 6;
                        ** break;
                    }
                    mv.visitVarInsn(opcode, this.readUnsignedShort(u + 2));
                    u += 4;
                    ** break;
                }
                case 14: {
                    u = u + 4 - (offset & 3);
                    label = offset + this.readInt(u);
                    min = this.readInt(u + 4);
                    max = this.readInt(u + 8);
                    table = new Label[max - min + 1];
                    u += 12;
                    for (i = 0; i < table.length; u += 4, ++i) {
                        table[i] = labels[offset + this.readInt(u)];
                    }
                    mv.visitTableSwitchInsn(min, max, labels[label], table);
                    ** break;
                }
                case 15: {
                    u = u + 4 - (offset & 3);
                    label = offset + this.readInt(u);
                    len = this.readInt(u + 4);
                    keys = new int[len];
                    values = new Label[len];
                    u += 8;
                    for (i = 0; i < len; u += 8, ++i) {
                        keys[i] = this.readInt(u);
                        values[i] = labels[offset + this.readInt(u + 4)];
                    }
                    mv.visitLookupSwitchInsn(labels[label], keys, values);
                    ** break;
                }
                case 3: {
                    mv.visitVarInsn(opcode, b[u + 1] & 255);
                    u += 2;
                    ** break;
                }
                case 1: {
                    mv.visitIntInsn(opcode, b[u + 1]);
                    u += 2;
                    ** break;
                }
                case 2: {
                    mv.visitIntInsn(opcode, this.readShort(u + 1));
                    u += 3;
                    ** break;
                }
                case 11: {
                    mv.visitLdcInsn(this.readConst(b[u + 1] & 255, c));
                    u += 2;
                    ** break;
                }
                case 12: {
                    mv.visitLdcInsn(this.readConst(this.readUnsignedShort(u + 1), c));
                    u += 3;
                    ** break;
                }
                case 6: 
                case 7: {
                    cpIndex = this.items[this.readUnsignedShort(u + 1)];
                    itf = b[cpIndex - 1] == 11;
                    iowner = this.readClass(cpIndex, c);
                    cpIndex = this.items[this.readUnsignedShort(cpIndex + 2)];
                    iname = this.readUTF8(cpIndex, c);
                    idesc = this.readUTF8(cpIndex + 2, c);
                    if (opcode < 182) {
                        mv.visitFieldInsn(opcode, iowner, iname, idesc);
                    } else {
                        mv.visitMethodInsn(opcode, iowner, iname, idesc, itf);
                    }
                    if (opcode == 185) {
                        u += 5;
                        ** break;
                    }
                    u += 3;
                    ** break;
                }
                case 8: {
                    cpIndex = this.items[this.readUnsignedShort(u + 1)];
                    bsmIndex = context.bootstrapMethods[this.readUnsignedShort(cpIndex)];
                    bsm = (Handle)this.readConst(this.readUnsignedShort(bsmIndex), c);
                    bsmArgCount = this.readUnsignedShort(bsmIndex + 2);
                    bsmArgs = new Object[bsmArgCount];
                    bsmIndex += 4;
                    for (i = 0; i < bsmArgCount; bsmIndex += 2, ++i) {
                        bsmArgs[i] = this.readConst(this.readUnsignedShort(bsmIndex), c);
                    }
                    cpIndex = this.items[this.readUnsignedShort(cpIndex + 2)];
                    iname = this.readUTF8(cpIndex, c);
                    idesc = this.readUTF8(cpIndex + 2, c);
                    mv.visitInvokeDynamicInsn(iname, idesc, bsm, bsmArgs);
                    u += 5;
                    ** break;
                }
                case 5: {
                    mv.visitTypeInsn(opcode, this.readClass(u + 1, c));
                    u += 3;
                    ** break;
                }
                case 13: {
                    mv.visitIincInsn(b[u + 1] & 255, b[u + 2]);
                    u += 3;
                    ** break;
                }
            }
            mv.visitMultiANewArrayInsn(this.readClass(u + 1, c), b[u + 3] & 255);
            u += 4;
lbl370: // 21 sources:
            while (tanns != null && tann < tanns.length && ntoff <= offset) {
                if (ntoff == offset) {
                    v = this.readAnnotationTarget(context, tanns[tann]);
                    this.readAnnotationValues(v + 2, c, true, mv.visitInsnAnnotation(context.typeRef, context.typePath, this.readUTF8(v, c), true));
                }
                ntoff = ++tann >= tanns.length || this.readByte(tanns[tann]) < 67 ? -1 : this.readUnsignedShort(tanns[tann] + 1);
            }
            do {
                if (itanns == null || itann >= itanns.length || nitoff > offset) continue block41;
                if (nitoff == offset) {
                    v = this.readAnnotationTarget(context, itanns[itann]);
                    this.readAnnotationValues(v + 2, c, true, mv.visitInsnAnnotation(context.typeRef, context.typePath, this.readUTF8(v, c), false));
                }
                nitoff = ++itann >= itanns.length || this.readByte(itanns[itann]) < 67 ? -1 : this.readUnsignedShort(itanns[itann] + 1);
            } while (true);
            break;
        } while (true);
        do {
            block105 : {
                block106 : {
                    block104 : {
                        if (i <= 0) break block104;
                        start = this.readUnsignedShort(u);
                        length = this.readUnsignedShort(u + 2);
                        index = this.readUnsignedShort(u + 8);
                        vsignature = null;
                        if (typeTable == null) break block105;
                        break block106;
                    }
                    if (tanns != null) {
                        for (i = 0; i < tanns.length; ++i) {
                            if (this.readByte(tanns[i]) >> 1 != 32) continue;
                            v = this.readAnnotationTarget(context, tanns[i]);
                            v = this.readAnnotationValues(v + 2, c, true, mv.visitLocalVariableAnnotation(context.typeRef, context.typePath, context.start, context.end, context.index, this.readUTF8(v, c), true));
                        }
                    }
                    if (itanns != null) {
                        for (i = 0; i < itanns.length; ++i) {
                            if (this.readByte(itanns[i]) >> 1 != 32) continue;
                            v = this.readAnnotationTarget(context, itanns[i]);
                            v = this.readAnnotationValues(v + 2, c, true, mv.visitLocalVariableAnnotation(context.typeRef, context.typePath, context.start, context.end, context.index, this.readUTF8(v, c), false));
                        }
                    }
                    do {
                        if (attributes == null) {
                            mv.visitMaxs(maxStack, maxLocals);
                            return;
                        }
                        attr = attributes.next;
                        attributes.next = null;
                        mv.visitAttribute(attributes);
                        attributes = attr;
                    } while (true);
                }
                for (j = 0; j < typeTable.length; j += 3) {
                    if (typeTable[j] != start || typeTable[j + 1] != index) continue;
                    vsignature = this.readUTF8(typeTable[j + 2], c);
                    break;
                }
            }
            mv.visitLocalVariable(this.readUTF8(u + 4, c), this.readUTF8(u + 6, c), vsignature, labels[start], labels[start + length], index);
            u += 10;
            --i;
        } while (true);
    }

    /*
     * Unable to fully structure code
     */
    private int[] readTypeAnnotations(MethodVisitor mv, Context context, int u, boolean visible) {
        c = context.buffer;
        offsets = new int[this.readUnsignedShort(u)];
        u += 2;
        i = 0;
        while (i < offsets.length) {
            offsets[i] = u;
            target = this.readInt(u);
            switch (target >>> 24) {
                case 0: 
                case 1: 
                case 22: {
                    u += 2;
                    ** break;
                }
                case 19: 
                case 20: 
                case 21: {
                    ++u;
                    ** break;
                }
                case 64: 
                case 65: {
                    for (j = this.readUnsignedShort((int)(u + 1)); j > 0; u += 6, --j) {
                        start = this.readUnsignedShort(u + 3);
                        length = this.readUnsignedShort(u + 5);
                        this.readLabel(start, context.labels);
                        this.readLabel(start + length, context.labels);
                    }
                    u += 3;
                    ** break;
                }
                case 71: 
                case 72: 
                case 73: 
                case 74: 
                case 75: {
                    u += 4;
                    ** break;
                }
            }
            u += 3;
lbl30: // 5 sources:
            pathLength = this.readByte(u);
            if (target >>> 24 == 66) {
                path = pathLength == 0 ? null : new TypePath(this.b, u);
                u += 1 + 2 * pathLength;
                u = this.readAnnotationValues(u + 2, c, true, mv.visitTryCatchAnnotation(target, path, this.readUTF8(u, c), visible));
            } else {
                u = this.readAnnotationValues(u + 3 + 2 * pathLength, c, true, null);
            }
            ++i;
        }
        return offsets;
    }

    private int readAnnotationTarget(Context context, int u) {
        int target = this.readInt(u);
        switch (target >>> 24) {
            case 0: 
            case 1: 
            case 22: {
                target &= -65536;
                u += 2;
                break;
            }
            case 19: 
            case 20: 
            case 21: {
                target &= -16777216;
                ++u;
                break;
            }
            case 64: 
            case 65: {
                target &= -16777216;
                int n = this.readUnsignedShort(u + 1);
                context.start = new Label[n];
                context.end = new Label[n];
                context.index = new int[n];
                u += 3;
                for (int i = 0; i < n; u += 6, ++i) {
                    int start = this.readUnsignedShort(u);
                    int length = this.readUnsignedShort(u + 2);
                    context.start[i] = this.readLabel(start, context.labels);
                    context.end[i] = this.readLabel(start + length, context.labels);
                    context.index[i] = this.readUnsignedShort(u + 4);
                }
                break;
            }
            case 71: 
            case 72: 
            case 73: 
            case 74: 
            case 75: {
                target &= -16776961;
                u += 4;
                break;
            }
            default: {
                target &= target >>> 24 < 67 ? -256 : -16777216;
                u += 3;
            }
        }
        int pathLength = this.readByte(u);
        context.typeRef = target;
        context.typePath = pathLength == 0 ? null : new TypePath(this.b, u);
        return u + 1 + 2 * pathLength;
    }

    private void readParameterAnnotations(MethodVisitor mv, Context context, int v, boolean visible) {
        AnnotationVisitor av;
        int i;
        int n = this.b[v++] & 255;
        int synthetics = Type.getArgumentTypes(context.desc).length - n;
        for (i = 0; i < synthetics; ++i) {
            av = mv.visitParameterAnnotation(i, "Ljava/lang/Synthetic;", false);
            if (av == null) continue;
            av.visitEnd();
        }
        char[] c = context.buffer;
        while (i < n + synthetics) {
            int j = this.readUnsignedShort(v);
            v += 2;
            while (j > 0) {
                av = mv.visitParameterAnnotation(i, this.readUTF8(v, c), visible);
                v = this.readAnnotationValues(v + 2, c, true, av);
                --j;
            }
            ++i;
        }
    }

    private int readAnnotationValues(int v, char[] buf, boolean named, AnnotationVisitor av) {
        int i = this.readUnsignedShort(v);
        v += 2;
        if (named) {
            while (i > 0) {
                v = this.readAnnotationValue(v + 2, buf, this.readUTF8(v, buf), av);
                --i;
            }
        } else {
            while (i > 0) {
                v = this.readAnnotationValue(v, buf, null, av);
                --i;
            }
        }
        if (av == null) return v;
        av.visitEnd();
        return v;
    }

    private int readAnnotationValue(int v, char[] buf, String name, AnnotationVisitor av) {
        if (av == null) {
            switch (this.b[v] & 255) {
                case 101: {
                    return v + 5;
                }
                case 64: {
                    return this.readAnnotationValues(v + 3, buf, true, null);
                }
                case 91: {
                    return this.readAnnotationValues(v + 1, buf, false, null);
                }
            }
            return v + 3;
        }
        switch (this.b[v++] & 255) {
            case 68: 
            case 70: 
            case 73: 
            case 74: {
                av.visit(name, this.readConst(this.readUnsignedShort(v), buf));
                return v += 2;
            }
            case 66: {
                av.visit(name, (byte)this.readInt(this.items[this.readUnsignedShort(v)]));
                return v += 2;
            }
            case 90: {
                av.visit(name, this.readInt(this.items[this.readUnsignedShort(v)]) == 0 ? Boolean.FALSE : Boolean.TRUE);
                return v += 2;
            }
            case 83: {
                av.visit(name, (short)this.readInt(this.items[this.readUnsignedShort(v)]));
                return v += 2;
            }
            case 67: {
                av.visit(name, Character.valueOf((char)this.readInt(this.items[this.readUnsignedShort(v)])));
                return v += 2;
            }
            case 115: {
                av.visit(name, this.readUTF8(v, buf));
                return v += 2;
            }
            case 101: {
                av.visitEnum(name, this.readUTF8(v, buf), this.readUTF8(v + 2, buf));
                return v += 4;
            }
            case 99: {
                av.visit(name, Type.getType(this.readUTF8(v, buf)));
                return v += 2;
            }
            case 64: {
                return this.readAnnotationValues(v + 2, buf, true, av.visitAnnotation(name, this.readUTF8(v, buf)));
            }
            case 91: {
                int size = this.readUnsignedShort(v);
                v += 2;
                if (size == 0) {
                    return this.readAnnotationValues(v - 2, buf, false, av.visitArray(name));
                }
                switch (this.b[v++] & 255) {
                    case 66: {
                        byte[] bv = new byte[size];
                        int i = 0;
                        do {
                            if (i >= size) {
                                av.visit(name, bv);
                                return --v;
                            }
                            bv[i] = (byte)this.readInt(this.items[this.readUnsignedShort(v)]);
                            v += 3;
                            ++i;
                        } while (true);
                    }
                    case 90: {
                        boolean[] zv = new boolean[size];
                        int i = 0;
                        do {
                            if (i >= size) {
                                av.visit(name, zv);
                                return --v;
                            }
                            zv[i] = this.readInt(this.items[this.readUnsignedShort(v)]) != 0;
                            v += 3;
                            ++i;
                        } while (true);
                    }
                    case 83: {
                        short[] sv = new short[size];
                        int i = 0;
                        do {
                            if (i >= size) {
                                av.visit(name, sv);
                                return --v;
                            }
                            sv[i] = (short)this.readInt(this.items[this.readUnsignedShort(v)]);
                            v += 3;
                            ++i;
                        } while (true);
                    }
                    case 67: {
                        char[] cv = new char[size];
                        int i = 0;
                        do {
                            if (i >= size) {
                                av.visit(name, cv);
                                return --v;
                            }
                            cv[i] = (char)this.readInt(this.items[this.readUnsignedShort(v)]);
                            v += 3;
                            ++i;
                        } while (true);
                    }
                    case 73: {
                        int[] iv = new int[size];
                        int i = 0;
                        do {
                            if (i >= size) {
                                av.visit(name, iv);
                                return --v;
                            }
                            iv[i] = this.readInt(this.items[this.readUnsignedShort(v)]);
                            v += 3;
                            ++i;
                        } while (true);
                    }
                    case 74: {
                        long[] lv = new long[size];
                        int i = 0;
                        do {
                            if (i >= size) {
                                av.visit(name, lv);
                                return --v;
                            }
                            lv[i] = this.readLong(this.items[this.readUnsignedShort(v)]);
                            v += 3;
                            ++i;
                        } while (true);
                    }
                    case 70: {
                        float[] fv = new float[size];
                        int i = 0;
                        do {
                            if (i >= size) {
                                av.visit(name, fv);
                                return --v;
                            }
                            fv[i] = Float.intBitsToFloat(this.readInt(this.items[this.readUnsignedShort(v)]));
                            v += 3;
                            ++i;
                        } while (true);
                    }
                    case 68: {
                        double[] dv = new double[size];
                        int i = 0;
                        do {
                            if (i >= size) {
                                av.visit(name, dv);
                                return --v;
                            }
                            dv[i] = Double.longBitsToDouble(this.readLong(this.items[this.readUnsignedShort(v)]));
                            v += 3;
                            ++i;
                        } while (true);
                    }
                }
                v = this.readAnnotationValues(v - 3, buf, false, av.visitArray(name));
            }
        }
        return v;
    }

    private void getImplicitFrame(Context frame) {
        String desc = frame.desc;
        Object[] locals = frame.local;
        int local = 0;
        if ((frame.access & 8) == 0) {
            locals[local++] = "<init>".equals(frame.name) ? Opcodes.UNINITIALIZED_THIS : this.readClass(this.header + 2, frame.buffer);
        }
        int i = 1;
        block8 : do {
            int j = i;
            switch (desc.charAt(i++)) {
                case 'B': 
                case 'C': 
                case 'I': 
                case 'S': 
                case 'Z': {
                    locals[local++] = Opcodes.INTEGER;
                    continue block8;
                }
                case 'F': {
                    locals[local++] = Opcodes.FLOAT;
                    continue block8;
                }
                case 'J': {
                    locals[local++] = Opcodes.LONG;
                    continue block8;
                }
                case 'D': {
                    locals[local++] = Opcodes.DOUBLE;
                    continue block8;
                }
                case '[': {
                    while (desc.charAt(i) == '[') {
                        ++i;
                    }
                    if (desc.charAt(i) == 'L') {
                        ++i;
                        while (desc.charAt(i) != ';') {
                            ++i;
                        }
                    }
                    locals[local++] = desc.substring(j, ++i);
                    continue block8;
                }
                case 'L': {
                    while (desc.charAt(i) != ';') {
                        ++i;
                    }
                    locals[local++] = desc.substring(j + 1, i++);
                    continue block8;
                }
            }
            break;
        } while (true);
        frame.localCount = local;
    }

    private int readFrame(int stackMap, boolean zip, boolean unzip, Context frame) {
        int tag;
        int delta;
        char[] c = frame.buffer;
        Label[] labels = frame.labels;
        if (zip) {
            tag = this.b[stackMap++] & 255;
        } else {
            tag = 255;
            frame.offset = -1;
        }
        frame.localDiff = 0;
        if (tag < 64) {
            delta = tag;
            frame.mode = 3;
            frame.stackCount = 0;
        } else if (tag < 128) {
            delta = tag - 64;
            stackMap = this.readFrameType(frame.stack, 0, stackMap, c, labels);
            frame.mode = 4;
            frame.stackCount = 1;
        } else {
            delta = this.readUnsignedShort(stackMap);
            stackMap += 2;
            if (tag == 247) {
                stackMap = this.readFrameType(frame.stack, 0, stackMap, c, labels);
                frame.mode = 4;
                frame.stackCount = 1;
            } else if (tag >= 248 && tag < 251) {
                frame.mode = 2;
                frame.localDiff = 251 - tag;
                frame.localCount -= frame.localDiff;
                frame.stackCount = 0;
            } else if (tag == 251) {
                frame.mode = 3;
                frame.stackCount = 0;
            } else if (tag < 255) {
                int local = unzip ? frame.localCount : 0;
                for (int i = tag - 251; i > 0; --i) {
                    stackMap = this.readFrameType(frame.local, local++, stackMap, c, labels);
                }
                frame.mode = 1;
                frame.localDiff = tag - 251;
                frame.localCount += frame.localDiff;
                frame.stackCount = 0;
            } else {
                frame.mode = 0;
                int n = this.readUnsignedShort(stackMap);
                stackMap += 2;
                frame.localDiff = n;
                frame.localCount = n;
                int local = 0;
                while (n > 0) {
                    stackMap = this.readFrameType(frame.local, local++, stackMap, c, labels);
                    --n;
                }
                n = this.readUnsignedShort(stackMap);
                stackMap += 2;
                frame.stackCount = n;
                int stack = 0;
                while (n > 0) {
                    stackMap = this.readFrameType(frame.stack, stack++, stackMap, c, labels);
                    --n;
                }
            }
        }
        frame.offset += delta + 1;
        this.readLabel(frame.offset, labels);
        return stackMap;
    }

    private int readFrameType(Object[] frame, int index, int v, char[] buf, Label[] labels) {
        int type = this.b[v++] & 255;
        switch (type) {
            case 0: {
                frame[index] = Opcodes.TOP;
                return v;
            }
            case 1: {
                frame[index] = Opcodes.INTEGER;
                return v;
            }
            case 2: {
                frame[index] = Opcodes.FLOAT;
                return v;
            }
            case 3: {
                frame[index] = Opcodes.DOUBLE;
                return v;
            }
            case 4: {
                frame[index] = Opcodes.LONG;
                return v;
            }
            case 5: {
                frame[index] = Opcodes.NULL;
                return v;
            }
            case 6: {
                frame[index] = Opcodes.UNINITIALIZED_THIS;
                return v;
            }
            case 7: {
                frame[index] = this.readClass(v, buf);
                return v += 2;
            }
        }
        frame[index] = this.readLabel(this.readUnsignedShort(v), labels);
        v += 2;
        return v;
    }

    protected Label readLabel(int offset, Label[] labels) {
        if (labels[offset] != null) return labels[offset];
        labels[offset] = new Label();
        return labels[offset];
    }

    private int getAttributes() {
        int j;
        int i;
        int u = this.header + 8 + this.readUnsignedShort(this.header + 6) * 2;
        for (i = this.readUnsignedShort((int)u); i > 0; u += 8, --i) {
            for (j = this.readUnsignedShort((int)(u + 8)); j > 0; u += 6 + this.readInt((int)(u + 12)), --j) {
            }
        }
        i = this.readUnsignedShort(u += 2);
        while (i > 0) {
            for (j = this.readUnsignedShort((int)(u + 8)); j > 0; u += 6 + this.readInt((int)(u + 12)), --j) {
            }
            u += 8;
            --i;
        }
        return u + 2;
    }

    private Attribute readAttribute(Attribute[] attrs, String type, int off, int len, char[] buf, int codeOff, Label[] labels) {
        int i = 0;
        while (i < attrs.length) {
            if (attrs[i].type.equals(type)) {
                return attrs[i].read(this, off, len, buf, codeOff, labels);
            }
            ++i;
        }
        return new Attribute(type).read(this, off, len, null, -1, null);
    }

    public int getItemCount() {
        return this.items.length;
    }

    public int getItem(int item) {
        return this.items[item];
    }

    public int getMaxStringLength() {
        return this.maxStringLength;
    }

    public int readByte(int index) {
        return this.b[index] & 255;
    }

    public int readUnsignedShort(int index) {
        byte[] b = this.b;
        return (b[index] & 255) << 8 | b[index + 1] & 255;
    }

    public short readShort(int index) {
        byte[] b = this.b;
        return (short)((b[index] & 255) << 8 | b[index + 1] & 255);
    }

    public int readInt(int index) {
        byte[] b = this.b;
        return (b[index] & 255) << 24 | (b[index + 1] & 255) << 16 | (b[index + 2] & 255) << 8 | b[index + 3] & 255;
    }

    public long readLong(int index) {
        long l1 = this.readInt(index);
        long l0 = (long)this.readInt(index + 4) & 0xFFFFFFFFL;
        return l1 << 32 | l0;
    }

    public String readUTF8(int index, char[] buf) {
        int item = this.readUnsignedShort(index);
        if (index == 0) return null;
        if (item == 0) {
            return null;
        }
        String s = this.strings[item];
        if (s != null) {
            return s;
        }
        index = this.items[item];
        this.strings[item] = this.readUTF(index + 2, this.readUnsignedShort(index), buf);
        return this.strings[item];
    }

    private String readUTF(int index, int utfLen, char[] buf) {
        int endIndex = index + utfLen;
        byte[] b = this.b;
        int strLen = 0;
        int st = 0;
        int cc = 0;
        while (index < endIndex) {
            int c = b[index++];
            switch (st) {
                case 0: {
                    if ((c &= 255) < 128) {
                        buf[strLen++] = (char)c;
                        break;
                    }
                    if (c < 224 && c > 191) {
                        cc = (char)(c & 31);
                        st = 1;
                        break;
                    }
                    cc = (char)(c & 15);
                    st = 2;
                    break;
                }
                case 1: {
                    buf[strLen++] = (char)(cc << 6 | c & 63);
                    st = 0;
                    break;
                }
                case 2: {
                    cc = (char)(cc << 6 | c & 63);
                    st = 1;
                }
            }
        }
        return new String(buf, 0, strLen);
    }

    public String readClass(int index, char[] buf) {
        return this.readUTF8(this.items[this.readUnsignedShort(index)], buf);
    }

    public Object readConst(int item, char[] buf) {
        int index = this.items[item];
        switch (this.b[index - 1]) {
            case 3: {
                return this.readInt(index);
            }
            case 4: {
                return Float.valueOf(Float.intBitsToFloat(this.readInt(index)));
            }
            case 5: {
                return this.readLong(index);
            }
            case 6: {
                return Double.longBitsToDouble(this.readLong(index));
            }
            case 7: {
                return Type.getObjectType(this.readUTF8(index, buf));
            }
            case 8: {
                return this.readUTF8(index, buf);
            }
            case 16: {
                return Type.getMethodType(this.readUTF8(index, buf));
            }
        }
        int tag = this.readByte(index);
        int[] items = this.items;
        int cpIndex = items[this.readUnsignedShort(index + 1)];
        boolean itf = this.b[cpIndex - 1] == 11;
        String owner = this.readClass(cpIndex, buf);
        cpIndex = items[this.readUnsignedShort(cpIndex + 2)];
        String name = this.readUTF8(cpIndex, buf);
        String desc = this.readUTF8(cpIndex + 2, buf);
        return new Handle(tag, owner, name, desc, itf);
    }
}

