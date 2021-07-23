/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.tools.obfuscation;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.tools.obfuscation.AnnotatedMixins;
import org.spongepowered.tools.obfuscation.SupportedOptions;

public abstract class MixinObfuscationProcessor
extends AbstractProcessor {
    protected AnnotatedMixins mixins;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.mixins = AnnotatedMixins.getMixinsForEnvironment(processingEnv);
    }

    protected void processMixins(RoundEnvironment roundEnv) {
        this.mixins.onPassStarted();
        Iterator<? extends Element> iterator = roundEnv.getElementsAnnotatedWith(Mixin.class).iterator();
        while (iterator.hasNext()) {
            Element elem = iterator.next();
            if (elem.getKind() == ElementKind.CLASS || elem.getKind() == ElementKind.INTERFACE) {
                this.mixins.registerMixin((TypeElement)elem);
                continue;
            }
            this.mixins.printMessage(Diagnostic.Kind.ERROR, "Found an @Mixin annotation on an element which is not a class or interface", elem);
        }
    }

    protected void postProcess(RoundEnvironment roundEnv) {
        this.mixins.onPassCompleted(roundEnv);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        try {
            return SourceVersion.valueOf("RELEASE_8");
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return super.getSupportedSourceVersion();
        }
    }

    @Override
    public Set<String> getSupportedOptions() {
        return SupportedOptions.getAllOptions();
    }
}

