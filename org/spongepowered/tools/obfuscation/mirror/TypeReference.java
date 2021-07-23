/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.tools.obfuscation.mirror;

import java.io.Serializable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import org.spongepowered.tools.obfuscation.mirror.TypeHandle;

public class TypeReference
implements Serializable,
Comparable<TypeReference> {
    private static final long serialVersionUID = 1L;
    private final String name;
    private transient TypeHandle handle;

    public TypeReference(TypeHandle handle) {
        this.name = handle.getName();
        this.handle = handle;
    }

    public TypeReference(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String getClassName() {
        return this.name.replace('/', '.');
    }

    public TypeHandle getHandle(ProcessingEnvironment processingEnv) {
        if (this.handle != null) return this.handle;
        TypeElement element = processingEnv.getElementUtils().getTypeElement(this.getClassName());
        try {
            this.handle = new TypeHandle(element);
            return this.handle;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return this.handle;
    }

    public String toString() {
        return String.format("TypeReference[%s]", this.name);
    }

    @Override
    public int compareTo(TypeReference other) {
        if (other == null) {
            return -1;
        }
        int n = this.name.compareTo(other.name);
        return n;
    }

    public boolean equals(Object other) {
        if (!(other instanceof TypeReference)) return false;
        if (this.compareTo((TypeReference)other) != 0) return false;
        return true;
    }

    public int hashCode() {
        return this.name.hashCode();
    }
}

