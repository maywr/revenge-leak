/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.tools.obfuscation.mirror;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.spongepowered.asm.mixin.injection.struct.MemberInfo;
import org.spongepowered.asm.obfuscation.mapping.common.MappingMethod;
import org.spongepowered.tools.obfuscation.mirror.AnnotationHandle;
import org.spongepowered.tools.obfuscation.mirror.FieldHandle;
import org.spongepowered.tools.obfuscation.mirror.MethodHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeReference;
import org.spongepowered.tools.obfuscation.mirror.TypeUtils;
import org.spongepowered.tools.obfuscation.mirror.mapping.ResolvableMappingMethod;

public class TypeHandle {
    private final String name;
    private final PackageElement pkg;
    private final TypeElement element;
    private TypeReference reference;

    public TypeHandle(PackageElement pkg, String name) {
        this.name = name.replace('.', '/');
        this.pkg = pkg;
        this.element = null;
    }

    public TypeHandle(TypeElement element) {
        this.pkg = TypeUtils.getPackage(element);
        this.name = TypeUtils.getInternalName(element);
        this.element = element;
    }

    public TypeHandle(DeclaredType type) {
        this((TypeElement)type.asElement());
    }

    public final String toString() {
        return this.name.replace('/', '.');
    }

    public final String getName() {
        return this.name;
    }

    public final PackageElement getPackage() {
        return this.pkg;
    }

    public final TypeElement getElement() {
        return this.element;
    }

    protected TypeElement getTargetElement() {
        return this.element;
    }

    public AnnotationHandle getAnnotation(Class<? extends Annotation> annotationClass) {
        return AnnotationHandle.of(this.getTargetElement(), annotationClass);
    }

    public final List<? extends Element> getEnclosedElements() {
        return TypeHandle.getEnclosedElements(this.getTargetElement());
    }

    public <T extends Element> List<T> getEnclosedElements(ElementKind ... kind) {
        return TypeHandle.getEnclosedElements(this.getTargetElement(), kind);
    }

    public TypeMirror getType() {
        if (this.getTargetElement() == null) return null;
        TypeMirror typeMirror = this.getTargetElement().asType();
        return typeMirror;
    }

    public TypeHandle getSuperclass() {
        TypeElement targetElement = this.getTargetElement();
        if (targetElement == null) {
            return null;
        }
        TypeMirror superClass = targetElement.getSuperclass();
        if (superClass == null) return null;
        if (superClass.getKind() != TypeKind.NONE) return new TypeHandle((DeclaredType)superClass);
        return null;
    }

    public List<TypeHandle> getInterfaces() {
        if (this.getTargetElement() == null) {
            return Collections.emptyList();
        }
        ImmutableList.Builder list = ImmutableList.builder();
        Iterator<? extends TypeMirror> iterator = this.getTargetElement().getInterfaces().iterator();
        while (iterator.hasNext()) {
            TypeMirror iface = iterator.next();
            list.add(new TypeHandle((DeclaredType)iface));
        }
        return list.build();
    }

    public boolean isPublic() {
        if (this.getTargetElement() == null) return false;
        if (!this.getTargetElement().getModifiers().contains((Object)Modifier.PUBLIC)) return false;
        return true;
    }

    public boolean isImaginary() {
        if (this.getTargetElement() != null) return false;
        return true;
    }

    public boolean isSimulated() {
        return false;
    }

    public final TypeReference getReference() {
        if (this.reference != null) return this.reference;
        this.reference = new TypeReference(this);
        return this.reference;
    }

    public MappingMethod getMappingMethod(String name, String desc) {
        return new ResolvableMappingMethod(this, name, desc);
    }

    public String findDescriptor(MemberInfo memberInfo) {
        ExecutableElement method;
        String desc = memberInfo.desc;
        if (desc != null) return desc;
        Iterator<T> iterator = this.getEnclosedElements(ElementKind.METHOD).iterator();
        do {
            if (!iterator.hasNext()) return desc;
        } while (!(method = (ExecutableElement)iterator.next()).getSimpleName().toString().equals(memberInfo.name));
        return TypeUtils.getDescriptor(method);
    }

    public final FieldHandle findField(VariableElement element) {
        return this.findField(element, true);
    }

    public final FieldHandle findField(VariableElement element, boolean caseSensitive) {
        return this.findField(element.getSimpleName().toString(), TypeUtils.getTypeName(element.asType()), caseSensitive);
    }

    public final FieldHandle findField(String name, String type) {
        return this.findField(name, type, true);
    }

    public FieldHandle findField(String name, String type, boolean caseSensitive) {
        VariableElement field;
        String rawType = TypeUtils.stripGenerics(type);
        Iterator<T> iterator = this.getEnclosedElements(ElementKind.FIELD).iterator();
        do {
            if (!iterator.hasNext()) return null;
            field = (VariableElement)iterator.next();
            if (!TypeHandle.compareElement(field, name, type, caseSensitive)) continue;
            return new FieldHandle(this.getTargetElement(), field);
        } while (!TypeHandle.compareElement(field, name, rawType, caseSensitive));
        return new FieldHandle(this.getTargetElement(), field, true);
    }

    public final MethodHandle findMethod(ExecutableElement element) {
        return this.findMethod(element, true);
    }

    public final MethodHandle findMethod(ExecutableElement element, boolean caseSensitive) {
        return this.findMethod(element.getSimpleName().toString(), TypeUtils.getJavaSignature(element), caseSensitive);
    }

    public final MethodHandle findMethod(String name, String signature) {
        return this.findMethod(name, signature, true);
    }

    public MethodHandle findMethod(String name, String signature, boolean matchCase) {
        String rawSignature = TypeUtils.stripGenerics(signature);
        return TypeHandle.findMethod(this, name, signature, rawSignature, matchCase);
    }

    protected static MethodHandle findMethod(TypeHandle target, String name, String signature, String rawSignature, boolean matchCase) {
        ExecutableElement method;
        Iterator<T> iterator = TypeHandle.getEnclosedElements(target.getTargetElement(), ElementKind.CONSTRUCTOR, ElementKind.METHOD).iterator();
        do {
            if (!iterator.hasNext()) return null;
            method = (ExecutableElement)iterator.next();
            if (TypeHandle.compareElement(method, name, signature, matchCase)) return new MethodHandle(target, method);
        } while (!TypeHandle.compareElement(method, name, rawSignature, matchCase));
        return new MethodHandle(target, method);
    }

    protected static boolean compareElement(Element elem, String name, String type, boolean matchCase) {
        try {
            String elementName = elem.getSimpleName().toString();
            String elementType = TypeUtils.getJavaSignature(elem);
            String rawElementType = TypeUtils.stripGenerics(elementType);
            boolean compared = matchCase ? name.equals(elementName) : name.equalsIgnoreCase(elementName);
            if (!compared) return false;
            if (type.length() == 0) return true;
            if (type.equals(elementType)) return true;
            if (!type.equals(rawElementType)) return false;
            return true;
        }
        catch (NullPointerException ex) {
            return false;
        }
    }

    protected static <T extends Element> List<T> getEnclosedElements(TypeElement targetElement, ElementKind ... kind) {
        if (kind == null) return TypeHandle.getEnclosedElements(targetElement);
        if (kind.length < 1) {
            return TypeHandle.getEnclosedElements(targetElement);
        }
        if (targetElement == null) {
            return Collections.emptyList();
        }
        ImmutableList.Builder list = ImmutableList.builder();
        Iterator<? extends Element> iterator = targetElement.getEnclosedElements().iterator();
        block0 : while (iterator.hasNext()) {
            Element elem = iterator.next();
            ElementKind[] arrelementKind = kind;
            int n = arrelementKind.length;
            int n2 = 0;
            do {
                if (n2 >= n) continue block0;
                ElementKind ek = arrelementKind[n2];
                if (elem.getKind() == ek) {
                    list.add(elem);
                    continue block0;
                }
                ++n2;
            } while (true);
            break;
        }
        return list.build();
    }

    protected static List<? extends Element> getEnclosedElements(TypeElement targetElement) {
        List<Object> list;
        if (targetElement != null) {
            list = targetElement.getEnclosedElements();
            return list;
        }
        list = Collections.emptyList();
        return list;
    }
}

