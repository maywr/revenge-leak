/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.mixin.transformer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.lib.tree.MethodInsnNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.mixin.struct.MemberRef;
import org.spongepowered.asm.mixin.transformer.ClassInfo;

abstract class ClassContext {
    private final Set<ClassInfo.Method> upgradedMethods = new HashSet<ClassInfo.Method>();

    ClassContext() {
    }

    abstract String getClassRef();

    abstract ClassNode getClassNode();

    abstract ClassInfo getClassInfo();

    void addUpgradedMethod(MethodNode method) {
        ClassInfo.Method md = this.getClassInfo().findMethod(method);
        if (md == null) {
            throw new IllegalStateException("Meta method for " + method.name + " not located in " + this);
        }
        this.upgradedMethods.add(md);
    }

    protected void upgradeMethods() {
        Iterator<MethodNode> iterator = this.getClassNode().methods.iterator();
        while (iterator.hasNext()) {
            MethodNode method = iterator.next();
            this.upgradeMethod(method);
        }
    }

    private void upgradeMethod(MethodNode method) {
        ListIterator<AbstractInsnNode> iter = method.instructions.iterator();
        while (iter.hasNext()) {
            MemberRef.Method methodRef;
            AbstractInsnNode insn = (AbstractInsnNode)iter.next();
            if (!(insn instanceof MethodInsnNode) || !((MemberRef)(methodRef = new MemberRef.Method((MethodInsnNode)insn))).getOwner().equals(this.getClassRef())) continue;
            ClassInfo.Method md = this.getClassInfo().findMethod(((MemberRef)methodRef).getName(), ((MemberRef)methodRef).getDesc(), 10);
            this.upgradeMethodRef(method, methodRef, md);
        }
    }

    protected void upgradeMethodRef(MethodNode containingMethod, MemberRef methodRef, ClassInfo.Method method) {
        if (methodRef.getOpcode() != 183) {
            return;
        }
        if (!this.upgradedMethods.contains(method)) return;
        methodRef.setOpcode(182);
    }
}

