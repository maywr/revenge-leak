/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.mixin.injection.points;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.lib.tree.MethodInsnNode;
import org.spongepowered.asm.lib.tree.TypeInsnNode;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.struct.InjectionPointData;
import org.spongepowered.asm.mixin.injection.struct.MemberInfo;
import org.spongepowered.asm.mixin.refmap.IMixinContext;

@InjectionPoint.AtCode(value="NEW")
public class BeforeNew
extends InjectionPoint {
    private final String target;
    private final String desc;
    private final int ordinal;

    public BeforeNew(InjectionPointData data) {
        super(data);
        this.ordinal = data.getOrdinal();
        String target = Strings.emptyToNull((String)data.get("class", data.get("target", "")).replace('.', '/'));
        MemberInfo member = MemberInfo.parseAndValidate(target, data.getContext());
        this.target = member.toCtorType();
        this.desc = member.toCtorDesc();
    }

    public boolean hasDescriptor() {
        if (this.desc == null) return false;
        return true;
    }

    @Override
    public boolean find(String desc, InsnList insns, Collection<AbstractInsnNode> nodes) {
        Object insn;
        boolean found = false;
        int ordinal = 0;
        ArrayList newNodes = new ArrayList();
        Collection<Object> candidates = this.desc != null ? newNodes : nodes;
        ListIterator<AbstractInsnNode> iter = insns.iterator();
        while (iter.hasNext()) {
            insn = iter.next();
            if (!(insn instanceof TypeInsnNode) || ((AbstractInsnNode)insn).getOpcode() != 187 || !this.matchesOwner((TypeInsnNode)insn)) continue;
            if (this.ordinal == -1 || this.ordinal == ordinal) {
                candidates.add(insn);
                found = this.desc == null;
            }
            ++ordinal;
        }
        if (this.desc == null) return found;
        insn = newNodes.iterator();
        while (insn.hasNext()) {
            TypeInsnNode newNode = (TypeInsnNode)insn.next();
            if (!this.findCtor(insns, newNode)) continue;
            nodes.add(newNode);
            found = true;
        }
        return found;
    }

    /*
     * Unable to fully structure code
     */
    protected boolean findCtor(InsnList insns, TypeInsnNode newNode) {
        indexOf = insns.indexOf(newNode);
        iter = insns.iterator(indexOf);
        do lbl-1000: // 3 sources:
        {
            if (iter.hasNext() == false) return false;
            insn = (AbstractInsnNode)iter.next();
            if (!(insn instanceof MethodInsnNode) || insn.getOpcode() != 183) ** GOTO lbl-1000
            methodNode = (MethodInsnNode)insn;
        } while (!"<init>".equals(methodNode.name) || !methodNode.owner.equals(newNode.desc) || !methodNode.desc.equals(this.desc));
        return true;
    }

    private boolean matchesOwner(TypeInsnNode insn) {
        if (this.target == null) return true;
        if (this.target.equals(insn.desc)) return true;
        return false;
    }
}

