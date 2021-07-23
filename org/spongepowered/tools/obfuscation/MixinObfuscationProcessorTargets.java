/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.tools.obfuscation;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.tools.obfuscation.AnnotatedMixins;
import org.spongepowered.tools.obfuscation.MixinObfuscationProcessor;
import org.spongepowered.tools.obfuscation.mirror.AnnotationHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeUtils;

@SupportedAnnotationTypes(value={"org.spongepowered.asm.mixin.Mixin", "org.spongepowered.asm.mixin.Shadow", "org.spongepowered.asm.mixin.Overwrite", "org.spongepowered.asm.mixin.gen.Accessor", "org.spongepowered.asm.mixin.Implements"})
public class MixinObfuscationProcessorTargets
extends MixinObfuscationProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            this.postProcess(roundEnv);
            return true;
        }
        this.processMixins(roundEnv);
        this.processShadows(roundEnv);
        this.processOverwrites(roundEnv);
        this.processAccessors(roundEnv);
        this.processInvokers(roundEnv);
        this.processImplements(roundEnv);
        this.postProcess(roundEnv);
        return true;
    }

    @Override
    protected void postProcess(RoundEnvironment roundEnv) {
        super.postProcess(roundEnv);
        try {
            this.mixins.writeReferences();
            this.mixins.writeMappings();
            return;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void processShadows(RoundEnvironment roundEnv) {
        Iterator<? extends Element> iterator = roundEnv.getElementsAnnotatedWith(Shadow.class).iterator();
        while (iterator.hasNext()) {
            Element elem = iterator.next();
            Element parent = elem.getEnclosingElement();
            if (!(parent instanceof TypeElement)) {
                this.mixins.printMessage(Diagnostic.Kind.ERROR, "Unexpected parent with type " + TypeUtils.getElementType(parent), elem);
                continue;
            }
            AnnotationHandle shadow = AnnotationHandle.of(elem, Shadow.class);
            if (elem.getKind() == ElementKind.FIELD) {
                this.mixins.registerShadow((TypeElement)parent, (VariableElement)elem, shadow);
                continue;
            }
            if (elem.getKind() == ElementKind.METHOD) {
                this.mixins.registerShadow((TypeElement)parent, (ExecutableElement)elem, shadow);
                continue;
            }
            this.mixins.printMessage(Diagnostic.Kind.ERROR, "Element is not a method or field", elem);
        }
    }

    private void processOverwrites(RoundEnvironment roundEnv) {
        Iterator<? extends Element> iterator = roundEnv.getElementsAnnotatedWith(Overwrite.class).iterator();
        while (iterator.hasNext()) {
            Element elem = iterator.next();
            Element parent = elem.getEnclosingElement();
            if (!(parent instanceof TypeElement)) {
                this.mixins.printMessage(Diagnostic.Kind.ERROR, "Unexpected parent with type " + TypeUtils.getElementType(parent), elem);
                continue;
            }
            if (elem.getKind() == ElementKind.METHOD) {
                this.mixins.registerOverwrite((TypeElement)parent, (ExecutableElement)elem);
                continue;
            }
            this.mixins.printMessage(Diagnostic.Kind.ERROR, "Element is not a method", elem);
        }
    }

    private void processAccessors(RoundEnvironment roundEnv) {
        Iterator<? extends Element> iterator = roundEnv.getElementsAnnotatedWith(Accessor.class).iterator();
        while (iterator.hasNext()) {
            Element elem = iterator.next();
            Element parent = elem.getEnclosingElement();
            if (!(parent instanceof TypeElement)) {
                this.mixins.printMessage(Diagnostic.Kind.ERROR, "Unexpected parent with type " + TypeUtils.getElementType(parent), elem);
                continue;
            }
            if (elem.getKind() == ElementKind.METHOD) {
                this.mixins.registerAccessor((TypeElement)parent, (ExecutableElement)elem);
                continue;
            }
            this.mixins.printMessage(Diagnostic.Kind.ERROR, "Element is not a method", elem);
        }
    }

    private void processInvokers(RoundEnvironment roundEnv) {
        Iterator<? extends Element> iterator = roundEnv.getElementsAnnotatedWith(Invoker.class).iterator();
        while (iterator.hasNext()) {
            Element elem = iterator.next();
            Element parent = elem.getEnclosingElement();
            if (!(parent instanceof TypeElement)) {
                this.mixins.printMessage(Diagnostic.Kind.ERROR, "Unexpected parent with type " + TypeUtils.getElementType(parent), elem);
                continue;
            }
            if (elem.getKind() == ElementKind.METHOD) {
                this.mixins.registerInvoker((TypeElement)parent, (ExecutableElement)elem);
                continue;
            }
            this.mixins.printMessage(Diagnostic.Kind.ERROR, "Element is not a method", elem);
        }
    }

    private void processImplements(RoundEnvironment roundEnv) {
        Iterator<? extends Element> iterator = roundEnv.getElementsAnnotatedWith(Implements.class).iterator();
        while (iterator.hasNext()) {
            Element elem = iterator.next();
            if (elem.getKind() == ElementKind.CLASS || elem.getKind() == ElementKind.INTERFACE) {
                AnnotationHandle implementsAnnotation = AnnotationHandle.of(elem, Implements.class);
                this.mixins.registerSoftImplements((TypeElement)elem, implementsAnnotation);
                continue;
            }
            this.mixins.printMessage(Diagnostic.Kind.ERROR, "Found an @Implements annotation on an element which is not a class or interface", elem);
        }
    }
}

