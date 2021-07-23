/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.lib;

public abstract class AnnotationVisitor {
    protected final int api;
    protected AnnotationVisitor av;

    public AnnotationVisitor(int api) {
        this(api, null);
    }

    public AnnotationVisitor(int api, AnnotationVisitor av) {
        if (api != 262144 && api != 327680) {
            throw new IllegalArgumentException();
        }
        this.api = api;
        this.av = av;
    }

    public void visit(String name, Object value) {
        if (this.av == null) return;
        this.av.visit(name, value);
    }

    public void visitEnum(String name, String desc, String value) {
        if (this.av == null) return;
        this.av.visitEnum(name, desc, value);
    }

    public AnnotationVisitor visitAnnotation(String name, String desc) {
        if (this.av == null) return null;
        return this.av.visitAnnotation(name, desc);
    }

    public AnnotationVisitor visitArray(String name) {
        if (this.av == null) return null;
        return this.av.visitArray(name);
    }

    public void visitEnd() {
        if (this.av == null) return;
        this.av.visitEnd();
    }
}

