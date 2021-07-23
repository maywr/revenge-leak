/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.tools.obfuscation;

import java.util.Iterator;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.tools.Diagnostic;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.obfuscation.mapping.common.MappingMethod;
import org.spongepowered.tools.obfuscation.AnnotatedMixin;
import org.spongepowered.tools.obfuscation.AnnotatedMixinElementHandler;
import org.spongepowered.tools.obfuscation.ObfuscationData;
import org.spongepowered.tools.obfuscation.ObfuscationType;
import org.spongepowered.tools.obfuscation.interfaces.IMixinAnnotationProcessor;
import org.spongepowered.tools.obfuscation.interfaces.IObfuscationDataProvider;
import org.spongepowered.tools.obfuscation.interfaces.IObfuscationManager;
import org.spongepowered.tools.obfuscation.mirror.AnnotationHandle;
import org.spongepowered.tools.obfuscation.mirror.MethodHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeUtils;

public class AnnotatedMixinElementHandlerSoftImplements
extends AnnotatedMixinElementHandler {
    AnnotatedMixinElementHandlerSoftImplements(IMixinAnnotationProcessor ap, AnnotatedMixin mixin) {
        super(ap, mixin);
    }

    public void process(AnnotationHandle implementsAnnotation) {
        if (!this.mixin.remap()) {
            return;
        }
        List<AnnotationHandle> interfaces = implementsAnnotation.getAnnotationList("value");
        if (interfaces.size() < 1) {
            this.ap.printMessage(Diagnostic.Kind.WARNING, "Empty @Implements annotation", this.mixin.getMixin(), implementsAnnotation.asMirror());
            return;
        }
        Iterator<AnnotationHandle> iterator = interfaces.iterator();
        while (iterator.hasNext()) {
            AnnotationHandle interfaceAnnotation = iterator.next();
            Interface.Remap remap = interfaceAnnotation.getValue("remap", Interface.Remap.ALL);
            if (remap == Interface.Remap.NONE) continue;
            try {
                TypeHandle iface = new TypeHandle((DeclaredType)interfaceAnnotation.getValue("iface"));
                String prefix = (String)interfaceAnnotation.getValue("prefix");
                this.processSoftImplements(remap, iface, prefix);
            }
            catch (Exception ex) {
                this.ap.printMessage(Diagnostic.Kind.ERROR, "Unexpected error: " + ex.getClass().getName() + ": " + ex.getMessage(), this.mixin.getMixin(), interfaceAnnotation.asMirror());
            }
        }
    }

    private void processSoftImplements(Interface.Remap remap, TypeHandle iface, String prefix) {
        for (ExecutableElement method : iface.getEnclosedElements(ElementKind.METHOD)) {
            this.processMethod(remap, iface, prefix, method);
        }
        Iterator<Object> iterator = iface.getInterfaces().iterator();
        while (iterator.hasNext()) {
            TypeHandle superInterface = (TypeHandle)iterator.next();
            this.processSoftImplements(remap, superInterface, prefix);
        }
    }

    private void processMethod(Interface.Remap remap, TypeHandle iface, String prefix, ExecutableElement method) {
        MethodHandle mixinMethod;
        String name = method.getSimpleName().toString();
        String sig = TypeUtils.getJavaSignature(method);
        String desc = TypeUtils.getDescriptor(method);
        if (remap != Interface.Remap.ONLY_PREFIXED && (mixinMethod = this.mixin.getHandle().findMethod(name, sig)) != null) {
            this.addInterfaceMethodMapping(remap, iface, null, mixinMethod, name, desc);
        }
        if (prefix == null) return;
        MethodHandle prefixedMixinMethod = this.mixin.getHandle().findMethod(prefix + name, sig);
        if (prefixedMixinMethod == null) return;
        this.addInterfaceMethodMapping(remap, iface, prefix, prefixedMixinMethod, name, desc);
    }

    private void addInterfaceMethodMapping(Interface.Remap remap, TypeHandle iface, String prefix, MethodHandle method, String name, String desc) {
        MappingMethod mapping = new MappingMethod(iface.getName(), name, desc);
        ObfuscationData<MappingMethod> obfData = this.obf.getDataProvider().getObfMethod(mapping);
        if (obfData.isEmpty()) {
            if (!remap.forceRemap()) return;
            this.ap.printMessage(Diagnostic.Kind.ERROR, "No obfuscation mapping for soft-implementing method", method.getElement());
            return;
        }
        this.addMethodMappings(method.getName(), desc, this.applyPrefix(obfData, prefix));
    }

    private ObfuscationData<MappingMethod> applyPrefix(ObfuscationData<MappingMethod> data, String prefix) {
        if (prefix == null) {
            return data;
        }
        ObfuscationData<MappingMethod> prefixed = new ObfuscationData<MappingMethod>();
        Iterator<ObfuscationType> iterator = data.iterator();
        while (iterator.hasNext()) {
            ObfuscationType type = iterator.next();
            MappingMethod mapping = data.get(type);
            prefixed.put(type, mapping.addPrefix(prefix));
        }
        return prefixed;
    }
}

