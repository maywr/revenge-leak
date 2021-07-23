/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.tools.obfuscation.mirror;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.spongepowered.asm.mixin.injection.struct.MemberInfo;
import org.spongepowered.asm.obfuscation.mapping.common.MappingMethod;
import org.spongepowered.asm.util.SignaturePrinter;
import org.spongepowered.tools.obfuscation.mirror.AnnotationHandle;
import org.spongepowered.tools.obfuscation.mirror.FieldHandle;
import org.spongepowered.tools.obfuscation.mirror.MethodHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeUtils;

public class TypeHandleSimulated
extends TypeHandle {
    private final TypeElement simulatedType;

    public TypeHandleSimulated(String name, TypeMirror type) {
        this(TypeUtils.getPackage(type), name, type);
    }

    public TypeHandleSimulated(PackageElement pkg, String name, TypeMirror type) {
        super(pkg, name);
        this.simulatedType = (TypeElement)((DeclaredType)type).asElement();
    }

    @Override
    protected TypeElement getTargetElement() {
        return this.simulatedType;
    }

    @Override
    public boolean isPublic() {
        return true;
    }

    @Override
    public boolean isImaginary() {
        return false;
    }

    @Override
    public boolean isSimulated() {
        return true;
    }

    @Override
    public AnnotationHandle getAnnotation(Class<? extends Annotation> annotationClass) {
        return null;
    }

    @Override
    public TypeHandle getSuperclass() {
        return null;
    }

    @Override
    public String findDescriptor(MemberInfo memberInfo) {
        if (memberInfo == null) return null;
        String string = memberInfo.desc;
        return string;
    }

    @Override
    public FieldHandle findField(String name, String type, boolean caseSensitive) {
        return new FieldHandle(null, name, type);
    }

    @Override
    public MethodHandle findMethod(String name, String desc, boolean caseSensitive) {
        return new MethodHandle(null, name, desc);
    }

    @Override
    public MappingMethod getMappingMethod(String name, String desc) {
        String rawSignature;
        MappingMethod mappingMethod;
        String signature = new SignaturePrinter(name, desc).setFullyQualified(true).toDescriptor();
        MethodHandle method = TypeHandleSimulated.findMethodRecursive(this, name, signature, rawSignature = TypeUtils.stripGenerics(signature), true);
        if (method != null) {
            mappingMethod = method.asMapping(true);
            return mappingMethod;
        }
        mappingMethod = super.getMappingMethod(name, desc);
        return mappingMethod;
    }

    private static MethodHandle findMethodRecursive(TypeHandle target, String name, String signature, String rawSignature, boolean matchCase) {
        TypeMirror iface;
        TypeElement elem = target.getTargetElement();
        if (elem == null) {
            return null;
        }
        MethodHandle method = TypeHandle.findMethod(target, name, signature, rawSignature, matchCase);
        if (method != null) {
            return method;
        }
        Iterator<? extends TypeMirror> iterator = elem.getInterfaces().iterator();
        do {
            if (iterator.hasNext()) continue;
            TypeMirror superClass = elem.getSuperclass();
            if (superClass == null) return null;
            if (superClass.getKind() != TypeKind.NONE) return TypeHandleSimulated.findMethodRecursive(superClass, name, signature, rawSignature, matchCase);
            return null;
        } while ((method = TypeHandleSimulated.findMethodRecursive(iface = iterator.next(), name, signature, rawSignature, matchCase)) == null);
        return method;
    }

    private static MethodHandle findMethodRecursive(TypeMirror target, String name, String signature, String rawSignature, boolean matchCase) {
        if (!(target instanceof DeclaredType)) {
            return null;
        }
        TypeElement element = (TypeElement)((DeclaredType)target).asElement();
        return TypeHandleSimulated.findMethodRecursive(new TypeHandle(element), name, signature, rawSignature, matchCase);
    }
}

