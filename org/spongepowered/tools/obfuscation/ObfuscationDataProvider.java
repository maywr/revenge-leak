/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.tools.obfuscation;

import java.util.Iterator;
import java.util.List;
import org.spongepowered.asm.mixin.injection.struct.MemberInfo;
import org.spongepowered.asm.obfuscation.mapping.IMapping;
import org.spongepowered.asm.obfuscation.mapping.common.MappingField;
import org.spongepowered.asm.obfuscation.mapping.common.MappingMethod;
import org.spongepowered.tools.obfuscation.ObfuscationData;
import org.spongepowered.tools.obfuscation.ObfuscationEnvironment;
import org.spongepowered.tools.obfuscation.ObfuscationType;
import org.spongepowered.tools.obfuscation.interfaces.IMixinAnnotationProcessor;
import org.spongepowered.tools.obfuscation.interfaces.IObfuscationDataProvider;
import org.spongepowered.tools.obfuscation.interfaces.ITypeHandleProvider;
import org.spongepowered.tools.obfuscation.mirror.TypeHandle;

public class ObfuscationDataProvider
implements IObfuscationDataProvider {
    private final IMixinAnnotationProcessor ap;
    private final List<ObfuscationEnvironment> environments;

    public ObfuscationDataProvider(IMixinAnnotationProcessor ap, List<ObfuscationEnvironment> environments) {
        this.ap = ap;
        this.environments = environments;
    }

    @Override
    public <T> ObfuscationData<T> getObfEntryRecursive(MemberInfo targetMember) {
        MemberInfo currentTarget = targetMember;
        ObfuscationData<String> obfTargetNames = this.getObfClass(currentTarget.owner);
        ObfuscationData<T> obfData = this.getObfEntry(currentTarget);
        try {
            while (obfData.isEmpty()) {
                TypeHandle targetType = this.ap.getTypeProvider().getTypeHandle(currentTarget.owner);
                if (targetType == null) {
                    return obfData;
                }
                TypeHandle superClass = targetType.getSuperclass();
                obfData = this.getObfEntryUsing(currentTarget, superClass);
                if (!obfData.isEmpty()) {
                    return ObfuscationDataProvider.applyParents(obfTargetNames, obfData);
                }
                for (TypeHandle iface : targetType.getInterfaces()) {
                    obfData = this.getObfEntryUsing(currentTarget, iface);
                    if (obfData.isEmpty()) continue;
                    return ObfuscationDataProvider.applyParents(obfTargetNames, obfData);
                }
                if (superClass == null) {
                    return obfData;
                }
                currentTarget = currentTarget.move(superClass.getName());
            }
            return obfData;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return this.getObfEntry(targetMember);
        }
    }

    private <T> ObfuscationData<T> getObfEntryUsing(MemberInfo targetMember, TypeHandle targetClass) {
        ObfuscationData obfuscationData;
        if (targetClass == null) {
            obfuscationData = new ObfuscationData();
            return obfuscationData;
        }
        obfuscationData = this.getObfEntry(targetMember.move(targetClass.getName()));
        return obfuscationData;
    }

    @Override
    public <T> ObfuscationData<T> getObfEntry(MemberInfo targetMember) {
        if (!targetMember.isField()) return this.getObfMethod(targetMember.asMethodMapping());
        return this.getObfField(targetMember);
    }

    @Override
    public <T> ObfuscationData<T> getObfEntry(IMapping<T> mapping) {
        if (mapping == null) return new ObfuscationData();
        if (mapping.getType() == IMapping.Type.FIELD) {
            return this.getObfField((MappingField)mapping);
        }
        if (mapping.getType() != IMapping.Type.METHOD) return new ObfuscationData();
        return this.getObfMethod((MappingMethod)mapping);
    }

    @Override
    public ObfuscationData<MappingMethod> getObfMethodRecursive(MemberInfo targetMember) {
        return this.getObfEntryRecursive(targetMember);
    }

    @Override
    public ObfuscationData<MappingMethod> getObfMethod(MemberInfo method) {
        return this.getRemappedMethod(method, method.isConstructor());
    }

    @Override
    public ObfuscationData<MappingMethod> getRemappedMethod(MemberInfo method) {
        return this.getRemappedMethod(method, true);
    }

    private ObfuscationData<MappingMethod> getRemappedMethod(MemberInfo method, boolean remapDescriptor) {
        ObfuscationData<MappingMethod> data = new ObfuscationData<MappingMethod>();
        Iterator<ObfuscationEnvironment> iterator = this.environments.iterator();
        do {
            if (!iterator.hasNext()) {
                if (!data.isEmpty()) return data;
                if (remapDescriptor) return this.remapDescriptor(data, method);
                return data;
            }
            ObfuscationEnvironment env = iterator.next();
            MappingMethod obfMethod = env.getObfMethod(method);
            if (obfMethod == null) continue;
            data.put(env.getType(), obfMethod);
        } while (true);
    }

    @Override
    public ObfuscationData<MappingMethod> getObfMethod(MappingMethod method) {
        return this.getRemappedMethod(method, method.isConstructor());
    }

    @Override
    public ObfuscationData<MappingMethod> getRemappedMethod(MappingMethod method) {
        return this.getRemappedMethod(method, true);
    }

    private ObfuscationData<MappingMethod> getRemappedMethod(MappingMethod method, boolean remapDescriptor) {
        ObfuscationData<MappingMethod> data = new ObfuscationData<MappingMethod>();
        Iterator<ObfuscationEnvironment> iterator = this.environments.iterator();
        do {
            if (!iterator.hasNext()) {
                if (!data.isEmpty()) return data;
                if (remapDescriptor) return this.remapDescriptor(data, new MemberInfo(method));
                return data;
            }
            ObfuscationEnvironment env = iterator.next();
            MappingMethod obfMethod = env.getObfMethod(method);
            if (obfMethod == null) continue;
            data.put(env.getType(), obfMethod);
        } while (true);
    }

    public ObfuscationData<MappingMethod> remapDescriptor(ObfuscationData<MappingMethod> data, MemberInfo method) {
        Iterator<ObfuscationEnvironment> iterator = this.environments.iterator();
        while (iterator.hasNext()) {
            ObfuscationEnvironment env = iterator.next();
            MemberInfo obfMethod = env.remapDescriptor(method);
            if (obfMethod == null) continue;
            data.put(env.getType(), obfMethod.asMethodMapping());
        }
        return data;
    }

    @Override
    public ObfuscationData<MappingField> getObfFieldRecursive(MemberInfo targetMember) {
        return this.getObfEntryRecursive(targetMember);
    }

    @Override
    public ObfuscationData<MappingField> getObfField(MemberInfo field) {
        return this.getObfField(field.asFieldMapping());
    }

    @Override
    public ObfuscationData<MappingField> getObfField(MappingField field) {
        ObfuscationData<MappingField> data = new ObfuscationData<MappingField>();
        Iterator<ObfuscationEnvironment> iterator = this.environments.iterator();
        while (iterator.hasNext()) {
            ObfuscationEnvironment env = iterator.next();
            MappingField obfField = env.getObfField(field);
            if (obfField == null) continue;
            if (obfField.getDesc() == null && field.getDesc() != null) {
                obfField = obfField.transform(env.remapDescriptor(field.getDesc()));
            }
            data.put(env.getType(), obfField);
        }
        return data;
    }

    @Override
    public ObfuscationData<String> getObfClass(TypeHandle type) {
        return this.getObfClass(type.getName());
    }

    @Override
    public ObfuscationData<String> getObfClass(String className) {
        ObfuscationData<String> data = new ObfuscationData<String>(className);
        Iterator<ObfuscationEnvironment> iterator = this.environments.iterator();
        while (iterator.hasNext()) {
            ObfuscationEnvironment env = iterator.next();
            String obfClass = env.getObfClass(className);
            if (obfClass == null) continue;
            data.put(env.getType(), obfClass);
        }
        return data;
    }

    private static <T> ObfuscationData<T> applyParents(ObfuscationData<String> parents, ObfuscationData<T> members) {
        Iterator<ObfuscationType> iterator = members.iterator();
        while (iterator.hasNext()) {
            ObfuscationType type = iterator.next();
            String obfClass = parents.get(type);
            T obfMember = members.get(type);
            members.put(type, MemberInfo.fromMapping((IMapping)obfMember).move(obfClass).asMapping());
        }
        return members;
    }
}

