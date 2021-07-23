/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.lib.tree.FrameNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.lib.tree.LabelNode;
import org.spongepowered.asm.lib.tree.LineNumberNode;
import org.spongepowered.asm.lib.tree.LocalVariableNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.lib.tree.VarInsnNode;
import org.spongepowered.asm.lib.tree.analysis.Analyzer;
import org.spongepowered.asm.lib.tree.analysis.AnalyzerException;
import org.spongepowered.asm.lib.tree.analysis.BasicValue;
import org.spongepowered.asm.lib.tree.analysis.Frame;
import org.spongepowered.asm.lib.tree.analysis.Interpreter;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.util.asm.MixinVerifier;
import org.spongepowered.asm.util.throwables.LVTGeneratorException;

public final class Locals {
    private static final Map<String, List<LocalVariableNode>> calculatedLocalVariables = new HashMap<String, List<LocalVariableNode>>();

    private Locals() {
    }

    public static void loadLocals(Type[] locals, InsnList insns, int pos, int limit) {
        while (pos < locals.length) {
            if (limit <= 0) return;
            if (locals[pos] != null) {
                insns.add(new VarInsnNode(locals[pos].getOpcode(21), pos));
                --limit;
            }
            ++pos;
        }
    }

    public static LocalVariableNode[] getLocalsAt(ClassNode classNode, MethodNode method, AbstractInsnNode node) {
        Type argType;
        for (int i = 0; i < 3 && (node instanceof LabelNode || node instanceof LineNumberNode); ++i) {
            node = Locals.nextNode(method.instructions, node);
        }
        ClassInfo classInfo = ClassInfo.forName(classNode.name);
        if (classInfo == null) {
            throw new LVTGeneratorException("Could not load class metadata for " + classNode.name + " generating LVT for " + method.name);
        }
        ClassInfo.Method methodInfo = classInfo.findMethod(method);
        if (methodInfo == null) {
            throw new LVTGeneratorException("Could not locate method metadata for " + method.name + " generating LVT in " + classNode.name);
        }
        List<ClassInfo.FrameData> frames = methodInfo.getFrames();
        LocalVariableNode[] frame = new LocalVariableNode[method.maxLocals];
        int local = 0;
        int index = 0;
        if ((method.access & 8) == 0) {
            frame[local++] = new LocalVariableNode("this", classNode.name, null, null, null, 0);
        }
        Type[] arrtype = Type.getArgumentTypes(method.desc);
        int n = arrtype.length;
        for (int i = 0; i < n; local += argType.getSize(), ++i) {
            argType = arrtype[i];
            frame[local] = new LocalVariableNode("arg" + index++, argType.toString(), null, null, null, local);
        }
        int initialFrameSize = local;
        int frameIndex = -1;
        int locals = 0;
        ListIterator<AbstractInsnNode> iter = method.instructions.iterator();
        while (iter.hasNext()) {
            AbstractInsnNode insn = (AbstractInsnNode)iter.next();
            if (!(insn instanceof FrameNode)) {
                if (insn instanceof VarInsnNode) {
                    VarInsnNode varNode = (VarInsnNode)insn;
                    frame[varNode.var] = Locals.getLocalVariableAt(classNode, method, node, varNode.var);
                }
            } else {
                FrameNode frameNode = (FrameNode)insn;
                ClassInfo.FrameData frameData = ++frameIndex < frames.size() ? frames.get(frameIndex) : null;
                locals = frameData != null && frameData.type == 0 ? Math.min(locals, frameData.locals) : frameNode.local.size();
                int localPos = 0;
                for (int framePos = 0; framePos < frame.length; ++framePos, ++localPos) {
                    Object localType;
                    Object object = localType = localPos < frameNode.local.size() ? frameNode.local.get(localPos) : null;
                    if (localType instanceof String) {
                        frame[framePos] = Locals.getLocalVariableAt(classNode, method, node, framePos);
                        continue;
                    }
                    if (localType instanceof Integer) {
                        boolean is64bitValue;
                        boolean isMarkerType = localType == Opcodes.UNINITIALIZED_THIS || localType == Opcodes.NULL;
                        boolean is32bitValue = localType == Opcodes.INTEGER || localType == Opcodes.FLOAT;
                        boolean bl = is64bitValue = localType == Opcodes.DOUBLE || localType == Opcodes.LONG;
                        if (localType == Opcodes.TOP) continue;
                        if (isMarkerType) {
                            frame[framePos] = null;
                            continue;
                        }
                        if (!is32bitValue) {
                            if (!is64bitValue) throw new LVTGeneratorException("Unrecognised locals opcode " + localType + " in locals array at position " + localPos + " in " + classNode.name + "." + method.name + method.desc);
                        }
                        frame[framePos] = Locals.getLocalVariableAt(classNode, method, node, framePos);
                        if (!is64bitValue) continue;
                        frame[++framePos] = null;
                        continue;
                    }
                    if (localType != null) throw new LVTGeneratorException("Invalid value " + localType + " in locals array at position " + localPos + " in " + classNode.name + "." + method.name + method.desc);
                    if (framePos < initialFrameSize || framePos < locals || locals <= 0) continue;
                    frame[framePos] = null;
                }
            }
            if (insn != node) continue;
        }
        int l = 0;
        while (l < frame.length) {
            if (frame[l] != null && frame[l].desc == null) {
                frame[l] = null;
            }
            ++l;
        }
        return frame;
    }

    public static LocalVariableNode getLocalVariableAt(ClassNode classNode, MethodNode method, AbstractInsnNode node, int var) {
        return Locals.getLocalVariableAt(classNode, method, method.instructions.indexOf(node), var);
    }

    private static LocalVariableNode getLocalVariableAt(ClassNode classNode, MethodNode method, int pos, int var) {
        LocalVariableNode localVariableNode;
        LocalVariableNode localVariableNode2 = null;
        LocalVariableNode fallbackNode = null;
        for (LocalVariableNode local : Locals.getLocalVariableTable(classNode, method)) {
            if (local.index != var) continue;
            if (Locals.isOpcodeInRange(method.instructions, local, pos)) {
                localVariableNode2 = local;
                continue;
            }
            if (localVariableNode2 != null) continue;
            fallbackNode = local;
        }
        if (localVariableNode2 == null && !method.localVariables.isEmpty()) {
            for (LocalVariableNode local : Locals.getGeneratedLocalVariableTable(classNode, method)) {
                if (local.index != var || !Locals.isOpcodeInRange(method.instructions, local, pos)) continue;
                localVariableNode2 = local;
            }
        }
        if (localVariableNode2 != null) {
            localVariableNode = localVariableNode2;
            return localVariableNode;
        }
        localVariableNode = fallbackNode;
        return localVariableNode;
    }

    private static boolean isOpcodeInRange(InsnList insns, LocalVariableNode local, int pos) {
        if (insns.indexOf(local.start) >= pos) return false;
        if (insns.indexOf(local.end) <= pos) return false;
        return true;
    }

    public static List<LocalVariableNode> getLocalVariableTable(ClassNode classNode, MethodNode method) {
        if (!method.localVariables.isEmpty()) return method.localVariables;
        return Locals.getGeneratedLocalVariableTable(classNode, method);
    }

    public static List<LocalVariableNode> getGeneratedLocalVariableTable(ClassNode classNode, MethodNode method) {
        String methodId = String.format("%s.%s%s", classNode.name, method.name, method.desc);
        List<LocalVariableNode> localVars = calculatedLocalVariables.get(methodId);
        if (localVars != null) {
            return localVars;
        }
        localVars = Locals.generateLocalVariableTable(classNode, method);
        calculatedLocalVariables.put(methodId, localVars);
        return localVars;
    }

    public static List<LocalVariableNode> generateLocalVariableTable(ClassNode classNode, MethodNode method) {
        ArrayList<Type> interfaces = null;
        if (classNode.interfaces != null) {
            interfaces = new ArrayList<Type>();
            for (String iface : classNode.interfaces) {
                interfaces.add(Type.getObjectType(iface));
            }
        }
        Type objectType = null;
        if (classNode.superName != null) {
            objectType = Type.getObjectType(classNode.superName);
        }
        Analyzer<BasicValue> analyzer = new Analyzer<BasicValue>(new MixinVerifier(Type.getObjectType(classNode.name), objectType, interfaces, false));
        try {
            analyzer.analyze(classNode.name, method);
        }
        catch (AnalyzerException ex) {
            ex.printStackTrace();
        }
        Frame<BasicValue>[] frames = analyzer.getFrames();
        int methodSize = method.instructions.size();
        ArrayList<LocalVariableNode> localVariables = new ArrayList<LocalVariableNode>();
        LocalVariableNode[] localNodes = new LocalVariableNode[method.maxLocals];
        BasicValue[] locals = new BasicValue[method.maxLocals];
        LabelNode[] labels = new LabelNode[methodSize];
        String[] lastKnownType = new String[method.maxLocals];
        int i = 0;
        do {
            block20 : {
                LabelNode label;
                Frame<BasicValue> f;
                block21 : {
                    block19 : {
                        if (i >= methodSize) break block19;
                        f = frames[i];
                        if (f == null) break block20;
                        label = null;
                        break block21;
                    }
                    LabelNode label2 = null;
                    for (int k = 0; k < localNodes.length; ++k) {
                        if (localNodes[k] == null) continue;
                        if (label2 == null) {
                            label2 = new LabelNode();
                            method.instructions.add(label2);
                        }
                        localNodes[k].end = label2;
                        localVariables.add(localNodes[k]);
                    }
                    int n = methodSize - 1;
                    while (n >= 0) {
                        if (labels[n] != null) {
                            method.instructions.insert(method.instructions.get(n), labels[n]);
                        }
                        --n;
                    }
                    return localVariables;
                }
                for (int j = 0; j < f.getLocals(); ++j) {
                    BasicValue local = f.getLocal(j);
                    if (local == null && locals[j] == null || local != null && local.equals(locals[j])) continue;
                    if (label == null) {
                        AbstractInsnNode existingLabel = method.instructions.get(i);
                        if (existingLabel instanceof LabelNode) {
                            label = (LabelNode)existingLabel;
                        } else {
                            labels[i] = label = new LabelNode();
                        }
                    }
                    if (local == null && locals[j] != null) {
                        localVariables.add(localNodes[j]);
                        localNodes[j].end = label;
                        localNodes[j] = null;
                    } else if (local != null) {
                        if (locals[j] != null) {
                            localVariables.add(localNodes[j]);
                            localNodes[j].end = label;
                            localNodes[j] = null;
                        }
                        String desc = local.getType() != null ? local.getType().getDescriptor() : lastKnownType[j];
                        localNodes[j] = new LocalVariableNode("var" + j, desc, null, label, null, j);
                        if (desc != null) {
                            lastKnownType[j] = desc;
                        }
                    }
                    locals[j] = local;
                }
            }
            ++i;
        } while (true);
    }

    private static AbstractInsnNode nextNode(InsnList insns, AbstractInsnNode insn) {
        int index = insns.indexOf(insn) + 1;
        if (index <= 0) return insn;
        if (index >= insns.size()) return insn;
        return insns.get(index);
    }
}

