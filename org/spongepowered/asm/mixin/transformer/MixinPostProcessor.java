/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.mixin.transformer;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.spongepowered.asm.lib.ClassReader;
import org.spongepowered.asm.lib.ClassVisitor;
import org.spongepowered.asm.lib.FieldVisitor;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.AnnotationNode;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.lib.tree.InsnNode;
import org.spongepowered.asm.lib.tree.MethodInsnNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.mixin.transformer.MixinConfig;
import org.spongepowered.asm.mixin.transformer.MixinInfo;
import org.spongepowered.asm.mixin.transformer.throwables.MixinTransformerError;
import org.spongepowered.asm.transformers.MixinClassWriter;
import org.spongepowered.asm.transformers.TreeTransformer;
import org.spongepowered.asm.util.Bytecode;

class MixinPostProcessor
extends TreeTransformer
implements MixinConfig.IListener {
    private final Set<String> syntheticInnerClasses = new HashSet<String>();
    private final Map<String, MixinInfo> accessorMixins = new HashMap<String, MixinInfo>();
    private final Set<String> loadable = new HashSet<String>();

    MixinPostProcessor() {
    }

    @Override
    public void onInit(MixinInfo mixin) {
        Iterator<String> iterator = mixin.getSyntheticInnerClasses().iterator();
        while (iterator.hasNext()) {
            String innerClass = iterator.next();
            this.registerSyntheticInner(innerClass.replace('/', '.'));
        }
    }

    @Override
    public void onPrepare(MixinInfo mixin) {
        String className = mixin.getClassName();
        if (mixin.isLoadable()) {
            this.registerLoadable(className);
        }
        if (!mixin.isAccessor()) return;
        this.registerAccessor(mixin);
    }

    void registerSyntheticInner(String className) {
        this.syntheticInnerClasses.add(className);
    }

    void registerLoadable(String className) {
        this.loadable.add(className);
    }

    void registerAccessor(MixinInfo mixin) {
        this.registerLoadable(mixin.getClassName());
        this.accessorMixins.put(mixin.getClassName(), mixin);
    }

    boolean canTransform(String className) {
        if (this.syntheticInnerClasses.contains(className)) return true;
        if (this.loadable.contains(className)) return true;
        return false;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isDelegationExcluded() {
        return true;
    }

    @Override
    public byte[] transformClassBytes(String name, String transformedName, byte[] bytes) {
        if (this.syntheticInnerClasses.contains(transformedName)) {
            return this.processSyntheticInner(bytes);
        }
        if (!this.accessorMixins.containsKey(transformedName)) return bytes;
        MixinInfo mixin = this.accessorMixins.get(transformedName);
        return this.processAccessor(bytes, mixin);
    }

    private byte[] processSyntheticInner(byte[] bytes) {
        ClassReader cr = new ClassReader(bytes);
        MixinClassWriter cw = new MixinClassWriter(cr, 0);
        ClassVisitor visibilityVisitor = new ClassVisitor(327680, cw){

            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                super.visit(version, access | 1, name, signature, superName, interfaces);
            }

            @Override
            public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
                if ((access & 6) != 0) return super.visitField(access, name, desc, signature, value);
                access |= 1;
                return super.visitField(access, name, desc, signature, value);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                if ((access & 6) != 0) return super.visitMethod(access, name, desc, signature, exceptions);
                access |= 1;
                return super.visitMethod(access, name, desc, signature, exceptions);
            }
        };
        cr.accept(visibilityVisitor, 8);
        return cw.toByteArray();
    }

    private byte[] processAccessor(byte[] bytes, MixinInfo mixin) {
        if (!MixinEnvironment.getCompatibilityLevel().isAtLeast(MixinEnvironment.CompatibilityLevel.JAVA_8)) {
            return bytes;
        }
        boolean transformed = false;
        MixinInfo.MixinClassNode classNode = mixin.getClassNode(0);
        ClassInfo targetClass = mixin.getTargets().get(0);
        Iterator<MixinInfo.MixinMethodNode> iter = classNode.mixinMethods.iterator();
        do {
            if (!iter.hasNext()) {
                if (!transformed) return bytes;
                return this.writeClass(classNode);
            }
            MixinInfo.MixinMethodNode methodNode = iter.next();
            if (!Bytecode.hasFlag(methodNode, 8)) continue;
            AnnotationNode accessor = methodNode.getVisibleAnnotation(Accessor.class);
            AnnotationNode invoker = methodNode.getVisibleAnnotation(Invoker.class);
            if (accessor == null && invoker == null) continue;
            ClassInfo.Method method = MixinPostProcessor.getAccessorMethod(mixin, methodNode, targetClass);
            MixinPostProcessor.createProxy(methodNode, targetClass, method);
            transformed = true;
        } while (true);
    }

    private static ClassInfo.Method getAccessorMethod(MixinInfo mixin, MethodNode methodNode, ClassInfo targetClass) throws MixinTransformerError {
        ClassInfo.Method method = mixin.getClassInfo().findMethod(methodNode, 10);
        if (method.isRenamed()) return method;
        throw new MixinTransformerError("Unexpected state: " + mixin + " loaded before " + targetClass + " was conformed");
    }

    private static void createProxy(MethodNode methodNode, ClassInfo targetClass, ClassInfo.Method method) {
        methodNode.instructions.clear();
        Type[] args = Type.getArgumentTypes(methodNode.desc);
        Type returnType = Type.getReturnType(methodNode.desc);
        Bytecode.loadArgs(args, methodNode.instructions, 0);
        methodNode.instructions.add(new MethodInsnNode(184, targetClass.getName(), method.getName(), methodNode.desc, false));
        methodNode.instructions.add(new InsnNode(returnType.getOpcode(172)));
        methodNode.maxStack = Bytecode.getFirstNonArgLocalIndex(args, false);
        methodNode.maxLocals = 0;
    }

}

