/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.tools.obfuscation;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import org.spongepowered.asm.mixin.injection.struct.MemberInfo;
import org.spongepowered.asm.obfuscation.mapping.common.MappingField;
import org.spongepowered.asm.obfuscation.mapping.common.MappingMethod;
import org.spongepowered.asm.util.ObfuscationUtil;
import org.spongepowered.tools.obfuscation.ObfuscationType;
import org.spongepowered.tools.obfuscation.interfaces.IMixinAnnotationProcessor;
import org.spongepowered.tools.obfuscation.interfaces.IObfuscationEnvironment;
import org.spongepowered.tools.obfuscation.interfaces.ITypeHandleProvider;
import org.spongepowered.tools.obfuscation.mapping.IMappingConsumer;
import org.spongepowered.tools.obfuscation.mapping.IMappingProvider;
import org.spongepowered.tools.obfuscation.mapping.IMappingWriter;
import org.spongepowered.tools.obfuscation.mirror.TypeHandle;
import org.spongepowered.tools.obfuscation.service.ObfuscationTypeDescriptor;

public abstract class ObfuscationEnvironment
implements IObfuscationEnvironment {
    protected final ObfuscationType type;
    protected final IMappingProvider mappingProvider;
    protected final IMappingWriter mappingWriter;
    protected final RemapperProxy remapper = new RemapperProxy();
    protected final IMixinAnnotationProcessor ap;
    protected final String outFileName;
    protected final List<String> inFileNames;
    private boolean initDone;

    protected ObfuscationEnvironment(ObfuscationType type) {
        this.type = type;
        this.ap = type.getAnnotationProcessor();
        this.inFileNames = type.getInputFileNames();
        this.outFileName = type.getOutputFileName();
        this.mappingProvider = this.getMappingProvider(this.ap, this.ap.getProcessingEnvironment().getFiler());
        this.mappingWriter = this.getMappingWriter(this.ap, this.ap.getProcessingEnvironment().getFiler());
    }

    public String toString() {
        return this.type.toString();
    }

    protected abstract IMappingProvider getMappingProvider(Messager var1, Filer var2);

    protected abstract IMappingWriter getMappingWriter(Messager var1, Filer var2);

    private boolean initMappings() {
        if (!this.initDone) {
            this.initDone = true;
            if (this.inFileNames == null) {
                this.ap.printMessage(Diagnostic.Kind.ERROR, "The " + this.type.getConfig().getInputFileOption() + " argument was not supplied, obfuscation processing will not occur");
                return false;
            }
            int successCount = 0;
            for (String inputFileName : this.inFileNames) {
                File inputFile = new File(inputFileName);
                try {
                    if (!inputFile.isFile()) continue;
                    this.ap.printMessage(Diagnostic.Kind.NOTE, "Loading " + this.type + " mappings from " + inputFile.getAbsolutePath());
                    this.mappingProvider.read(inputFile);
                    ++successCount;
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (successCount < 1) {
                this.ap.printMessage(Diagnostic.Kind.ERROR, "No valid input files for " + this.type + " could be read, processing may not be sucessful.");
                this.mappingProvider.clear();
            }
        }
        if (this.mappingProvider.isEmpty()) return false;
        return true;
    }

    public ObfuscationType getType() {
        return this.type;
    }

    @Override
    public MappingMethod getObfMethod(MemberInfo method) {
        MappingMethod obfd = this.getObfMethod(method.asMethodMapping());
        if (obfd != null) return obfd;
        if (!method.isFullyQualified()) {
            return obfd;
        }
        TypeHandle type = this.ap.getTypeProvider().getTypeHandle(method.owner);
        if (type == null) return null;
        if (type.isImaginary()) {
            return null;
        }
        TypeMirror superClass = type.getElement().getSuperclass();
        if (superClass.getKind() != TypeKind.DECLARED) {
            return null;
        }
        String superClassName = ((TypeElement)((DeclaredType)superClass).asElement()).getQualifiedName().toString();
        return this.getObfMethod(new MemberInfo(method.name, superClassName.replace('.', '/'), method.desc, method.matchAll));
    }

    @Override
    public MappingMethod getObfMethod(MappingMethod method) {
        return this.getObfMethod(method, true);
    }

    @Override
    public MappingMethod getObfMethod(MappingMethod method, boolean lazyRemap) {
        String remappedOwner;
        if (!this.initMappings()) return null;
        boolean remapped = true;
        MappingMethod mapping = null;
        for (MappingMethod md = method; md != null && mapping == null; md = md.getSuper()) {
            mapping = this.mappingProvider.getMethodMapping(md);
        }
        if (mapping == null) {
            if (lazyRemap) {
                return null;
            }
            mapping = method.copy();
            remapped = false;
        }
        if ((remappedOwner = this.getObfClass(mapping.getOwner())) == null || remappedOwner.equals(method.getOwner()) || remappedOwner.equals(mapping.getOwner())) {
            if (!remapped) return null;
            MappingMethod mappingMethod = mapping;
            return mappingMethod;
        }
        if (remapped) {
            return mapping.move(remappedOwner);
        }
        String desc = ObfuscationUtil.mapDescriptor(mapping.getDesc(), this.remapper);
        return new MappingMethod(remappedOwner, mapping.getSimpleName(), desc);
    }

    @Override
    public MemberInfo remapDescriptor(MemberInfo method) {
        String desc;
        String newOwner;
        String newDesc;
        boolean transformed = false;
        String owner = method.owner;
        if (owner != null && (newOwner = this.remapper.map(owner)) != null) {
            owner = newOwner;
            transformed = true;
        }
        if ((desc = method.desc) != null && !(newDesc = ObfuscationUtil.mapDescriptor(method.desc, this.remapper)).equals(method.desc)) {
            desc = newDesc;
            transformed = true;
        }
        if (!transformed) return null;
        MemberInfo memberInfo = new MemberInfo(method.name, owner, desc, method.matchAll);
        return memberInfo;
    }

    @Override
    public String remapDescriptor(String desc) {
        return ObfuscationUtil.mapDescriptor(desc, this.remapper);
    }

    @Override
    public MappingField getObfField(MemberInfo field) {
        return this.getObfField(field.asFieldMapping(), true);
    }

    @Override
    public MappingField getObfField(MappingField field) {
        return this.getObfField(field, true);
    }

    @Override
    public MappingField getObfField(MappingField field, boolean lazyRemap) {
        String remappedOwner;
        if (!this.initMappings()) {
            return null;
        }
        MappingField mapping = this.mappingProvider.getFieldMapping(field);
        if (mapping == null) {
            if (lazyRemap) {
                return null;
            }
            mapping = field;
        }
        if ((remappedOwner = this.getObfClass(mapping.getOwner())) != null && !remappedOwner.equals(field.getOwner())) {
            if (!remappedOwner.equals(mapping.getOwner())) return mapping.move(remappedOwner);
        }
        if (mapping == field) return null;
        MappingField mappingField = mapping;
        return mappingField;
    }

    @Override
    public String getObfClass(String className) {
        if (this.initMappings()) return this.mappingProvider.getClassMapping(className);
        return null;
    }

    @Override
    public void writeMappings(Collection<IMappingConsumer> consumers) {
        IMappingConsumer.MappingSet fields = new IMappingConsumer.MappingSet();
        IMappingConsumer.MappingSet methods = new IMappingConsumer.MappingSet();
        Iterator<IMappingConsumer> iterator = consumers.iterator();
        do {
            if (!iterator.hasNext()) {
                this.mappingWriter.write(this.outFileName, this.type, fields, methods);
                return;
            }
            IMappingConsumer mappings = iterator.next();
            fields.addAll(mappings.getFieldMappings(this.type));
            methods.addAll(mappings.getMethodMappings(this.type));
        } while (true);
    }

    final class RemapperProxy
    implements ObfuscationUtil.IClassRemapper {
        RemapperProxy() {
        }

        @Override
        public String map(String typeName) {
            if (ObfuscationEnvironment.this.mappingProvider != null) return ObfuscationEnvironment.this.mappingProvider.getClassMapping(typeName);
            return null;
        }

        @Override
        public String unmap(String typeName) {
            if (ObfuscationEnvironment.this.mappingProvider != null) return ObfuscationEnvironment.this.mappingProvider.getClassMapping(typeName);
            return null;
        }
    }

}

