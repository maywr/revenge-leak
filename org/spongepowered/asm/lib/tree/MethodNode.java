/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.lib.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.spongepowered.asm.lib.AnnotationVisitor;
import org.spongepowered.asm.lib.Attribute;
import org.spongepowered.asm.lib.ClassVisitor;
import org.spongepowered.asm.lib.Handle;
import org.spongepowered.asm.lib.Label;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.TypePath;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.AnnotationNode;
import org.spongepowered.asm.lib.tree.FieldInsnNode;
import org.spongepowered.asm.lib.tree.FrameNode;
import org.spongepowered.asm.lib.tree.IincInsnNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.lib.tree.InsnNode;
import org.spongepowered.asm.lib.tree.IntInsnNode;
import org.spongepowered.asm.lib.tree.InvokeDynamicInsnNode;
import org.spongepowered.asm.lib.tree.JumpInsnNode;
import org.spongepowered.asm.lib.tree.LabelNode;
import org.spongepowered.asm.lib.tree.LdcInsnNode;
import org.spongepowered.asm.lib.tree.LineNumberNode;
import org.spongepowered.asm.lib.tree.LocalVariableAnnotationNode;
import org.spongepowered.asm.lib.tree.LocalVariableNode;
import org.spongepowered.asm.lib.tree.LookupSwitchInsnNode;
import org.spongepowered.asm.lib.tree.MethodInsnNode;
import org.spongepowered.asm.lib.tree.MultiANewArrayInsnNode;
import org.spongepowered.asm.lib.tree.ParameterNode;
import org.spongepowered.asm.lib.tree.TableSwitchInsnNode;
import org.spongepowered.asm.lib.tree.TryCatchBlockNode;
import org.spongepowered.asm.lib.tree.TypeAnnotationNode;
import org.spongepowered.asm.lib.tree.TypeInsnNode;
import org.spongepowered.asm.lib.tree.VarInsnNode;

public class MethodNode
extends MethodVisitor {
    public int access;
    public String name;
    public String desc;
    public String signature;
    public List<String> exceptions;
    public List<ParameterNode> parameters;
    public List<AnnotationNode> visibleAnnotations;
    public List<AnnotationNode> invisibleAnnotations;
    public List<TypeAnnotationNode> visibleTypeAnnotations;
    public List<TypeAnnotationNode> invisibleTypeAnnotations;
    public List<Attribute> attrs;
    public Object annotationDefault;
    public List<AnnotationNode>[] visibleParameterAnnotations;
    public List<AnnotationNode>[] invisibleParameterAnnotations;
    public InsnList instructions;
    public List<TryCatchBlockNode> tryCatchBlocks;
    public int maxStack;
    public int maxLocals;
    public List<LocalVariableNode> localVariables;
    public List<LocalVariableAnnotationNode> visibleLocalVariableAnnotations;
    public List<LocalVariableAnnotationNode> invisibleLocalVariableAnnotations;
    private boolean visited;

    public MethodNode() {
        this(327680);
        if (this.getClass() == MethodNode.class) return;
        throw new IllegalStateException();
    }

    public MethodNode(int api) {
        super(api);
        this.instructions = new InsnList();
    }

    public MethodNode(int access, String name, String desc, String signature, String[] exceptions) {
        this(327680, access, name, desc, signature, exceptions);
        if (this.getClass() == MethodNode.class) return;
        throw new IllegalStateException();
    }

    public MethodNode(int api, int access, String name, String desc, String signature, String[] exceptions) {
        super(api);
        boolean isAbstract;
        this.access = access;
        this.name = name;
        this.desc = desc;
        this.signature = signature;
        this.exceptions = new ArrayList<String>(exceptions == null ? 0 : exceptions.length);
        boolean bl = isAbstract = (access & 1024) != 0;
        if (!isAbstract) {
            this.localVariables = new ArrayList<LocalVariableNode>(5);
        }
        this.tryCatchBlocks = new ArrayList<TryCatchBlockNode>();
        if (exceptions != null) {
            this.exceptions.addAll(Arrays.asList(exceptions));
        }
        this.instructions = new InsnList();
    }

    @Override
    public void visitParameter(String name, int access) {
        if (this.parameters == null) {
            this.parameters = new ArrayList<ParameterNode>(5);
        }
        this.parameters.add(new ParameterNode(name, access));
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        return new AnnotationNode((List<Object>)new ArrayList<Object>(0){

            @Override
            public boolean add(Object o) {
                MethodNode.this.annotationDefault = o;
                return super.add(o);
            }
        });
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        AnnotationNode an = new AnnotationNode(desc);
        if (visible) {
            if (this.visibleAnnotations == null) {
                this.visibleAnnotations = new ArrayList<AnnotationNode>(1);
            }
            this.visibleAnnotations.add(an);
            return an;
        }
        if (this.invisibleAnnotations == null) {
            this.invisibleAnnotations = new ArrayList<AnnotationNode>(1);
        }
        this.invisibleAnnotations.add(an);
        return an;
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        TypeAnnotationNode an = new TypeAnnotationNode(typeRef, typePath, desc);
        if (visible) {
            if (this.visibleTypeAnnotations == null) {
                this.visibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(1);
            }
            this.visibleTypeAnnotations.add(an);
            return an;
        }
        if (this.invisibleTypeAnnotations == null) {
            this.invisibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(1);
        }
        this.invisibleTypeAnnotations.add(an);
        return an;
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
        AnnotationNode an = new AnnotationNode(desc);
        if (visible) {
            if (this.visibleParameterAnnotations == null) {
                int params = Type.getArgumentTypes(this.desc).length;
                this.visibleParameterAnnotations = new List[params];
            }
            if (this.visibleParameterAnnotations[parameter] == null) {
                this.visibleParameterAnnotations[parameter] = new ArrayList<AnnotationNode>(1);
            }
            this.visibleParameterAnnotations[parameter].add(an);
            return an;
        }
        if (this.invisibleParameterAnnotations == null) {
            int params = Type.getArgumentTypes(this.desc).length;
            this.invisibleParameterAnnotations = new List[params];
        }
        if (this.invisibleParameterAnnotations[parameter] == null) {
            this.invisibleParameterAnnotations[parameter] = new ArrayList<AnnotationNode>(1);
        }
        this.invisibleParameterAnnotations[parameter].add(an);
        return an;
    }

    @Override
    public void visitAttribute(Attribute attr) {
        if (this.attrs == null) {
            this.attrs = new ArrayList<Attribute>(1);
        }
        this.attrs.add(attr);
    }

    @Override
    public void visitCode() {
    }

    @Override
    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
        this.instructions.add(new FrameNode(type, nLocal, local == null ? null : this.getLabelNodes(local), nStack, stack == null ? null : this.getLabelNodes(stack)));
    }

    @Override
    public void visitInsn(int opcode) {
        this.instructions.add(new InsnNode(opcode));
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        this.instructions.add(new IntInsnNode(opcode, operand));
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        this.instructions.add(new VarInsnNode(opcode, var));
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        this.instructions.add(new TypeInsnNode(opcode, type));
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        this.instructions.add(new FieldInsnNode(opcode, owner, name, desc));
    }

    @Deprecated
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        if (this.api >= 327680) {
            super.visitMethodInsn(opcode, owner, name, desc);
            return;
        }
        this.instructions.add(new MethodInsnNode(opcode, owner, name, desc));
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        if (this.api < 327680) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
            return;
        }
        this.instructions.add(new MethodInsnNode(opcode, owner, name, desc, itf));
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object ... bsmArgs) {
        this.instructions.add(new InvokeDynamicInsnNode(name, desc, bsm, bsmArgs));
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        this.instructions.add(new JumpInsnNode(opcode, this.getLabelNode(label)));
    }

    @Override
    public void visitLabel(Label label) {
        this.instructions.add(this.getLabelNode(label));
    }

    @Override
    public void visitLdcInsn(Object cst) {
        this.instructions.add(new LdcInsnNode(cst));
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        this.instructions.add(new IincInsnNode(var, increment));
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label ... labels) {
        this.instructions.add(new TableSwitchInsnNode(min, max, this.getLabelNode(dflt), this.getLabelNodes(labels)));
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        this.instructions.add(new LookupSwitchInsnNode(this.getLabelNode(dflt), keys, this.getLabelNodes(labels)));
    }

    @Override
    public void visitMultiANewArrayInsn(String desc, int dims) {
        this.instructions.add(new MultiANewArrayInsnNode(desc, dims));
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        AbstractInsnNode insn = this.instructions.getLast();
        while (insn.getOpcode() == -1) {
            insn = insn.getPrevious();
        }
        TypeAnnotationNode an = new TypeAnnotationNode(typeRef, typePath, desc);
        if (visible) {
            if (insn.visibleTypeAnnotations == null) {
                insn.visibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(1);
            }
            insn.visibleTypeAnnotations.add(an);
            return an;
        }
        if (insn.invisibleTypeAnnotations == null) {
            insn.invisibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(1);
        }
        insn.invisibleTypeAnnotations.add(an);
        return an;
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        this.tryCatchBlocks.add(new TryCatchBlockNode(this.getLabelNode(start), this.getLabelNode(end), this.getLabelNode(handler), type));
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        TryCatchBlockNode tcb = this.tryCatchBlocks.get((typeRef & 16776960) >> 8);
        TypeAnnotationNode an = new TypeAnnotationNode(typeRef, typePath, desc);
        if (visible) {
            if (tcb.visibleTypeAnnotations == null) {
                tcb.visibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(1);
            }
            tcb.visibleTypeAnnotations.add(an);
            return an;
        }
        if (tcb.invisibleTypeAnnotations == null) {
            tcb.invisibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(1);
        }
        tcb.invisibleTypeAnnotations.add(an);
        return an;
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        this.localVariables.add(new LocalVariableNode(name, desc, signature, this.getLabelNode(start), this.getLabelNode(end), index));
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible) {
        LocalVariableAnnotationNode an = new LocalVariableAnnotationNode(typeRef, typePath, this.getLabelNodes(start), this.getLabelNodes(end), index, desc);
        if (visible) {
            if (this.visibleLocalVariableAnnotations == null) {
                this.visibleLocalVariableAnnotations = new ArrayList<LocalVariableAnnotationNode>(1);
            }
            this.visibleLocalVariableAnnotations.add(an);
            return an;
        }
        if (this.invisibleLocalVariableAnnotations == null) {
            this.invisibleLocalVariableAnnotations = new ArrayList<LocalVariableAnnotationNode>(1);
        }
        this.invisibleLocalVariableAnnotations.add(an);
        return an;
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        this.instructions.add(new LineNumberNode(line, this.getLabelNode(start)));
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        this.maxStack = maxStack;
        this.maxLocals = maxLocals;
    }

    @Override
    public void visitEnd() {
    }

    protected LabelNode getLabelNode(Label l) {
        if (l.info instanceof LabelNode) return (LabelNode)l.info;
        l.info = new LabelNode();
        return (LabelNode)l.info;
    }

    private LabelNode[] getLabelNodes(Label[] l) {
        LabelNode[] nodes = new LabelNode[l.length];
        int i = 0;
        while (i < l.length) {
            nodes[i] = this.getLabelNode(l[i]);
            ++i;
        }
        return nodes;
    }

    private Object[] getLabelNodes(Object[] objs) {
        Object[] nodes = new Object[objs.length];
        int i = 0;
        while (i < objs.length) {
            Object o = objs[i];
            if (o instanceof Label) {
                o = this.getLabelNode((Label)o);
            }
            nodes[i] = o;
            ++i;
        }
        return nodes;
    }

    public void check(int api) {
        int i;
        if (api != 262144) return;
        if (this.visibleTypeAnnotations != null && this.visibleTypeAnnotations.size() > 0) {
            throw new RuntimeException();
        }
        if (this.invisibleTypeAnnotations != null && this.invisibleTypeAnnotations.size() > 0) {
            throw new RuntimeException();
        }
        int n = this.tryCatchBlocks == null ? 0 : this.tryCatchBlocks.size();
        for (i = 0; i < n; ++i) {
            TryCatchBlockNode tcb = this.tryCatchBlocks.get(i);
            if (tcb.visibleTypeAnnotations != null && tcb.visibleTypeAnnotations.size() > 0) {
                throw new RuntimeException();
            }
            if (tcb.invisibleTypeAnnotations == null || tcb.invisibleTypeAnnotations.size() <= 0) continue;
            throw new RuntimeException();
        }
        for (i = 0; i < this.instructions.size(); ++i) {
            boolean itf;
            AbstractInsnNode insn = this.instructions.get(i);
            if (insn.visibleTypeAnnotations != null && insn.visibleTypeAnnotations.size() > 0) {
                throw new RuntimeException();
            }
            if (insn.invisibleTypeAnnotations != null && insn.invisibleTypeAnnotations.size() > 0) {
                throw new RuntimeException();
            }
            if (!(insn instanceof MethodInsnNode) || (itf = ((MethodInsnNode)insn).itf) == (insn.opcode == 185)) continue;
            throw new RuntimeException();
        }
        if (this.visibleLocalVariableAnnotations != null && this.visibleLocalVariableAnnotations.size() > 0) {
            throw new RuntimeException();
        }
        if (this.invisibleLocalVariableAnnotations == null) return;
        if (this.invisibleLocalVariableAnnotations.size() <= 0) return;
        throw new RuntimeException();
    }

    public void accept(ClassVisitor cv) {
        String[] exceptions = new String[this.exceptions.size()];
        this.exceptions.toArray(exceptions);
        MethodVisitor mv = cv.visitMethod(this.access, this.name, this.desc, this.signature, exceptions);
        if (mv == null) return;
        this.accept(mv);
    }

    public void accept(MethodVisitor mv) {
        List<AnnotationNode> l;
        AnnotationNode an;
        AnnotationNode an2;
        int j;
        int i;
        int n = this.parameters == null ? 0 : this.parameters.size();
        for (i = 0; i < n; ++i) {
            ParameterNode parameter = this.parameters.get(i);
            mv.visitParameter(parameter.name, parameter.access);
        }
        if (this.annotationDefault != null) {
            AnnotationVisitor av = mv.visitAnnotationDefault();
            AnnotationNode.accept(av, null, this.annotationDefault);
            if (av != null) {
                av.visitEnd();
            }
        }
        n = this.visibleAnnotations == null ? 0 : this.visibleAnnotations.size();
        for (i = 0; i < n; ++i) {
            an2 = this.visibleAnnotations.get(i);
            an2.accept(mv.visitAnnotation(an2.desc, true));
        }
        n = this.invisibleAnnotations == null ? 0 : this.invisibleAnnotations.size();
        for (i = 0; i < n; ++i) {
            an2 = this.invisibleAnnotations.get(i);
            an2.accept(mv.visitAnnotation(an2.desc, false));
        }
        n = this.visibleTypeAnnotations == null ? 0 : this.visibleTypeAnnotations.size();
        for (i = 0; i < n; ++i) {
            an2 = this.visibleTypeAnnotations.get(i);
            an2.accept(mv.visitTypeAnnotation(((TypeAnnotationNode)an2).typeRef, ((TypeAnnotationNode)an2).typePath, ((TypeAnnotationNode)an2).desc, true));
        }
        n = this.invisibleTypeAnnotations == null ? 0 : this.invisibleTypeAnnotations.size();
        for (i = 0; i < n; ++i) {
            an2 = this.invisibleTypeAnnotations.get(i);
            an2.accept(mv.visitTypeAnnotation(((TypeAnnotationNode)an2).typeRef, ((TypeAnnotationNode)an2).typePath, ((TypeAnnotationNode)an2).desc, false));
        }
        n = this.visibleParameterAnnotations == null ? 0 : this.visibleParameterAnnotations.length;
        for (i = 0; i < n; ++i) {
            l = this.visibleParameterAnnotations[i];
            if (l == null) continue;
            for (j = 0; j < l.size(); ++j) {
                an = l.get(j);
                an.accept(mv.visitParameterAnnotation(i, an.desc, true));
            }
        }
        n = this.invisibleParameterAnnotations == null ? 0 : this.invisibleParameterAnnotations.length;
        for (i = 0; i < n; ++i) {
            l = this.invisibleParameterAnnotations[i];
            if (l == null) continue;
            for (j = 0; j < l.size(); ++j) {
                an = l.get(j);
                an.accept(mv.visitParameterAnnotation(i, an.desc, false));
            }
        }
        if (this.visited) {
            this.instructions.resetLabels();
        }
        n = this.attrs == null ? 0 : this.attrs.size();
        for (i = 0; i < n; ++i) {
            mv.visitAttribute(this.attrs.get(i));
        }
        if (this.instructions.size() > 0) {
            mv.visitCode();
            n = this.tryCatchBlocks == null ? 0 : this.tryCatchBlocks.size();
            for (i = 0; i < n; ++i) {
                this.tryCatchBlocks.get(i).updateIndex(i);
                this.tryCatchBlocks.get(i).accept(mv);
            }
            this.instructions.accept(mv);
            n = this.localVariables == null ? 0 : this.localVariables.size();
            for (i = 0; i < n; ++i) {
                this.localVariables.get(i).accept(mv);
            }
            n = this.visibleLocalVariableAnnotations == null ? 0 : this.visibleLocalVariableAnnotations.size();
            for (i = 0; i < n; ++i) {
                this.visibleLocalVariableAnnotations.get(i).accept(mv, true);
            }
            n = this.invisibleLocalVariableAnnotations == null ? 0 : this.invisibleLocalVariableAnnotations.size();
            for (i = 0; i < n; ++i) {
                this.invisibleLocalVariableAnnotations.get(i).accept(mv, false);
            }
            mv.visitMaxs(this.maxStack, this.maxLocals);
            this.visited = true;
        }
        mv.visitEnd();
    }

}

