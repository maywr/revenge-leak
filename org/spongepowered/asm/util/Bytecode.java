/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.spongepowered.asm.util;

import com.google.common.base.Joiner;
import com.google.common.primitives.Ints;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.lib.ClassReader;
import org.spongepowered.asm.lib.ClassVisitor;
import org.spongepowered.asm.lib.ClassWriter;
import org.spongepowered.asm.lib.Label;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.AnnotationNode;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.lib.tree.FieldInsnNode;
import org.spongepowered.asm.lib.tree.FieldNode;
import org.spongepowered.asm.lib.tree.FrameNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.lib.tree.IntInsnNode;
import org.spongepowered.asm.lib.tree.JumpInsnNode;
import org.spongepowered.asm.lib.tree.LabelNode;
import org.spongepowered.asm.lib.tree.LdcInsnNode;
import org.spongepowered.asm.lib.tree.LineNumberNode;
import org.spongepowered.asm.lib.tree.MethodInsnNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.lib.tree.TypeInsnNode;
import org.spongepowered.asm.lib.tree.VarInsnNode;
import org.spongepowered.asm.lib.util.CheckClassAdapter;
import org.spongepowered.asm.lib.util.TraceClassVisitor;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.util.Constants;
import org.spongepowered.asm.util.throwables.SyntheticBridgeException;

public final class Bytecode {
    public static final int[] CONSTANTS_INT = new int[]{2, 3, 4, 5, 6, 7, 8};
    public static final int[] CONSTANTS_FLOAT = new int[]{11, 12, 13};
    public static final int[] CONSTANTS_DOUBLE = new int[]{14, 15};
    public static final int[] CONSTANTS_LONG = new int[]{9, 10};
    public static final int[] CONSTANTS_ALL = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18};
    private static final Object[] CONSTANTS_VALUES = new Object[]{null, -1, 0, 1, 2, 3, 4, 5, 0L, 1L, Float.valueOf(0.0f), Float.valueOf(1.0f), Float.valueOf(2.0f), 0.0, 1.0};
    private static final String[] CONSTANTS_TYPES = new String[]{null, "I", "I", "I", "I", "I", "I", "I", "J", "J", "F", "F", "F", "D", "D", "I", "I"};
    private static final String[] BOXING_TYPES = new String[]{null, "java/lang/Boolean", "java/lang/Character", "java/lang/Byte", "java/lang/Short", "java/lang/Integer", "java/lang/Float", "java/lang/Long", "java/lang/Double", null, null, null};
    private static final String[] UNBOXING_METHODS = new String[]{null, "booleanValue", "charValue", "byteValue", "shortValue", "intValue", "floatValue", "longValue", "doubleValue", null, null, null};
    private static final Class<?>[] MERGEABLE_MIXIN_ANNOTATIONS = new Class[]{Overwrite.class, Intrinsic.class, Final.class, Debug.class};
    private static Pattern mergeableAnnotationPattern = Bytecode.getMergeableAnnotationPattern();
    private static final Logger logger = LogManager.getLogger((String)"mixin");

    private Bytecode() {
    }

    public static MethodNode findMethod(ClassNode classNode, String name, String desc) {
        MethodNode method;
        Iterator<MethodNode> iterator = classNode.methods.iterator();
        do {
            if (!iterator.hasNext()) return null;
            method = iterator.next();
        } while (!method.name.equals(name) || !method.desc.equals(desc));
        return method;
    }

    public static AbstractInsnNode findInsn(MethodNode method, int opcode) {
        AbstractInsnNode insn;
        ListIterator<AbstractInsnNode> findReturnIter = method.instructions.iterator();
        do {
            if (!findReturnIter.hasNext()) return null;
        } while ((insn = (AbstractInsnNode)findReturnIter.next()).getOpcode() != opcode);
        return insn;
    }

    public static MethodInsnNode findSuperInit(MethodNode method, String superName) {
        if (!"<init>".equals(method.name)) {
            return null;
        }
        int news = 0;
        ListIterator<AbstractInsnNode> iter = method.instructions.iterator();
        while (iter.hasNext()) {
            AbstractInsnNode insn = (AbstractInsnNode)iter.next();
            if (insn instanceof TypeInsnNode && insn.getOpcode() == 187) {
                ++news;
                continue;
            }
            if (!(insn instanceof MethodInsnNode) || insn.getOpcode() != 183) continue;
            MethodInsnNode methodNode = (MethodInsnNode)insn;
            if (!"<init>".equals(methodNode.name)) continue;
            if (news > 0) {
                --news;
                continue;
            }
            if (methodNode.owner.equals(superName)) return methodNode;
        }
        return null;
    }

    public static void textify(ClassNode classNode, OutputStream out) {
        classNode.accept(new TraceClassVisitor(new PrintWriter(out)));
    }

    public static void textify(MethodNode methodNode, OutputStream out) {
        TraceClassVisitor trace = new TraceClassVisitor(new PrintWriter(out));
        MethodVisitor mv = trace.visitMethod(methodNode.access, methodNode.name, methodNode.desc, methodNode.signature, methodNode.exceptions.toArray(new String[0]));
        methodNode.accept(mv);
        trace.visitEnd();
    }

    public static void dumpClass(ClassNode classNode) {
        ClassWriter cw = new ClassWriter(3);
        classNode.accept(cw);
        Bytecode.dumpClass(cw.toByteArray());
    }

    public static void dumpClass(byte[] bytes) {
        ClassReader cr = new ClassReader(bytes);
        CheckClassAdapter.verify(cr, true, new PrintWriter(System.out));
    }

    public static void printMethodWithOpcodeIndices(MethodNode method) {
        System.err.printf("%s%s\n", method.name, method.desc);
        int i = 0;
        ListIterator<AbstractInsnNode> iter = method.instructions.iterator();
        while (iter.hasNext()) {
            System.err.printf("[%4d] %s\n", i++, Bytecode.describeNode((AbstractInsnNode)iter.next()));
        }
    }

    public static void printMethod(MethodNode method) {
        System.err.printf("%s%s\n", method.name, method.desc);
        ListIterator<AbstractInsnNode> iter = method.instructions.iterator();
        while (iter.hasNext()) {
            System.err.print("  ");
            Bytecode.printNode((AbstractInsnNode)iter.next());
        }
    }

    public static void printNode(AbstractInsnNode node) {
        System.err.printf("%s\n", Bytecode.describeNode(node));
    }

    public static String describeNode(AbstractInsnNode node) {
        if (node == null) {
            return String.format("   %-14s ", "null");
        }
        if (node instanceof LabelNode) {
            return String.format("[%s]", ((LabelNode)node).getLabel());
        }
        String out = String.format("   %-14s ", node.getClass().getSimpleName().replace("Node", ""));
        if (node instanceof JumpInsnNode) {
            return out + String.format("[%s] [%s]", Bytecode.getOpcodeName(node), ((JumpInsnNode)node).label.getLabel());
        }
        if (node instanceof VarInsnNode) {
            return out + String.format("[%s] %d", Bytecode.getOpcodeName(node), ((VarInsnNode)node).var);
        }
        if (node instanceof MethodInsnNode) {
            MethodInsnNode mth = (MethodInsnNode)node;
            return out + String.format("[%s] %s %s %s", Bytecode.getOpcodeName(node), mth.owner, mth.name, mth.desc);
        }
        if (node instanceof FieldInsnNode) {
            FieldInsnNode fld = (FieldInsnNode)node;
            return out + String.format("[%s] %s %s %s", Bytecode.getOpcodeName(node), fld.owner, fld.name, fld.desc);
        }
        if (node instanceof LineNumberNode) {
            LineNumberNode ln = (LineNumberNode)node;
            return out + String.format("LINE=[%d] LABEL=[%s]", ln.line, ln.start.getLabel());
        }
        if (node instanceof LdcInsnNode) {
            return out + ((LdcInsnNode)node).cst;
        }
        if (node instanceof IntInsnNode) {
            return out + ((IntInsnNode)node).operand;
        }
        if (!(node instanceof FrameNode)) return out + String.format("[%s] ", Bytecode.getOpcodeName(node));
        return out + String.format("[%s] ", Bytecode.getOpcodeName(((FrameNode)node).type, "H_INVOKEINTERFACE", -1));
    }

    public static String getOpcodeName(AbstractInsnNode node) {
        if (node == null) return "";
        String string = Bytecode.getOpcodeName(node.getOpcode());
        return string;
    }

    public static String getOpcodeName(int opcode) {
        return Bytecode.getOpcodeName(opcode, "UNINITIALIZED_THIS", 1);
    }

    private static String getOpcodeName(int opcode, String start, int min) {
        if (opcode >= min) {
            boolean found = false;
            try {
                for (Field f : Opcodes.class.getDeclaredFields()) {
                    if (!found && !f.getName().equals(start)) continue;
                    found = true;
                    if (f.getType() != Integer.TYPE || f.getInt(null) != opcode) continue;
                    return f.getName();
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (opcode < 0) return "UNKNOWN";
        String string = String.valueOf(opcode);
        return string;
    }

    public static boolean methodHasLineNumbers(MethodNode method) {
        ListIterator<AbstractInsnNode> iter = method.instructions.iterator();
        do {
            if (!iter.hasNext()) return false;
        } while (!(iter.next() instanceof LineNumberNode));
        return true;
    }

    public static boolean methodIsStatic(MethodNode method) {
        if ((method.access & 8) != 8) return false;
        return true;
    }

    public static boolean fieldIsStatic(FieldNode field) {
        if ((field.access & 8) != 8) return false;
        return true;
    }

    public static int getFirstNonArgLocalIndex(MethodNode method) {
        boolean bl;
        if ((method.access & 8) == 0) {
            bl = true;
            return Bytecode.getFirstNonArgLocalIndex(Type.getArgumentTypes(method.desc), bl);
        }
        bl = false;
        return Bytecode.getFirstNonArgLocalIndex(Type.getArgumentTypes(method.desc), bl);
    }

    public static int getFirstNonArgLocalIndex(Type[] args, boolean includeThis) {
        int n;
        if (includeThis) {
            n = 1;
            return Bytecode.getArgsSize(args) + n;
        }
        n = 0;
        return Bytecode.getArgsSize(args) + n;
    }

    public static int getArgsSize(Type[] args) {
        int size = 0;
        Type[] arrtype = args;
        int n = arrtype.length;
        int n2 = 0;
        while (n2 < n) {
            Type type = arrtype[n2];
            size += type.getSize();
            ++n2;
        }
        return size;
    }

    public static void loadArgs(Type[] args, InsnList insns, int pos) {
        Bytecode.loadArgs(args, insns, pos, -1);
    }

    public static void loadArgs(Type[] args, InsnList insns, int start, int end) {
        Bytecode.loadArgs(args, insns, start, end, null);
    }

    public static void loadArgs(Type[] args, InsnList insns, int start, int end, Type[] casts) {
        int pos = start;
        int index = 0;
        Type[] arrtype = args;
        int n = arrtype.length;
        int n2 = 0;
        while (n2 < n) {
            Type type = arrtype[n2];
            insns.add(new VarInsnNode(type.getOpcode(21), pos));
            if (casts != null && index < casts.length && casts[index] != null) {
                insns.add(new TypeInsnNode(192, casts[index].getInternalName()));
            }
            if (end >= start && (pos += type.getSize()) >= end) {
                return;
            }
            ++index;
            ++n2;
        }
    }

    public static Map<LabelNode, LabelNode> cloneLabels(InsnList source) {
        HashMap<LabelNode, LabelNode> labels = new HashMap<LabelNode, LabelNode>();
        ListIterator<AbstractInsnNode> iter = source.iterator();
        while (iter.hasNext()) {
            AbstractInsnNode insn = (AbstractInsnNode)iter.next();
            if (!(insn instanceof LabelNode)) continue;
            labels.put((LabelNode)insn, new LabelNode(((LabelNode)insn).getLabel()));
        }
        return labels;
    }

    public static String generateDescriptor(Object returnType, Object ... args) {
        String string;
        StringBuilder sb = new StringBuilder().append('(');
        for (Object arg : args) {
            sb.append(Bytecode.toDescriptor(arg));
        }
        if (returnType != null) {
            string = Bytecode.toDescriptor(returnType);
            return sb.append(')').append(string).toString();
        }
        string = "V";
        return sb.append(')').append(string).toString();
    }

    private static String toDescriptor(Object arg) {
        if (arg instanceof String) {
            return (String)arg;
        }
        if (arg instanceof Type) {
            return arg.toString();
        }
        if (arg instanceof Class) {
            return Type.getDescriptor((Class)arg);
        }
        if (arg == null) {
            return "";
        }
        String string = arg.toString();
        return string;
    }

    public static String getDescriptor(Type[] args) {
        return "(" + Joiner.on("").join((Object[])args) + ")";
    }

    public static String getDescriptor(Type[] args, Type returnType) {
        return Bytecode.getDescriptor(args) + returnType.toString();
    }

    public static String changeDescriptorReturnType(String desc, String returnType) {
        if (desc == null) {
            return null;
        }
        if (returnType != null) return desc.substring(0, desc.lastIndexOf(41) + 1) + returnType;
        return desc;
    }

    public static String getSimpleName(Class<? extends Annotation> annotationType) {
        return annotationType.getSimpleName();
    }

    public static String getSimpleName(AnnotationNode annotation) {
        return Bytecode.getSimpleName(annotation.desc);
    }

    public static String getSimpleName(String desc) {
        int pos = Math.max(desc.lastIndexOf(47), 0);
        return desc.substring(pos + 1).replace(";", "");
    }

    public static boolean isConstant(AbstractInsnNode insn) {
        if (insn != null) return Ints.contains((int[])CONSTANTS_ALL, (int)insn.getOpcode());
        return false;
    }

    public static Object getConstant(AbstractInsnNode insn) {
        if (insn == null) {
            return null;
        }
        if (insn instanceof LdcInsnNode) {
            return ((LdcInsnNode)insn).cst;
        }
        if (insn instanceof IntInsnNode) {
            int value = ((IntInsnNode)insn).operand;
            if (insn.getOpcode() == 16) return value;
            if (insn.getOpcode() != 17) throw new IllegalArgumentException("IntInsnNode with invalid opcode " + insn.getOpcode() + " in getConstant");
            return value;
        }
        int index = Ints.indexOf((int[])CONSTANTS_ALL, (int)insn.getOpcode());
        if (index < 0) {
            return null;
        }
        Object object = CONSTANTS_VALUES[index];
        return object;
    }

    public static Type getConstantType(AbstractInsnNode insn) {
        if (insn == null) {
            return null;
        }
        if (insn instanceof LdcInsnNode) {
            Object cst = ((LdcInsnNode)insn).cst;
            if (cst instanceof Integer) {
                return Type.getType("I");
            }
            if (cst instanceof Float) {
                return Type.getType("F");
            }
            if (cst instanceof Long) {
                return Type.getType("J");
            }
            if (cst instanceof Double) {
                return Type.getType("D");
            }
            if (cst instanceof String) {
                return Type.getType("Ljava/lang/String;");
            }
            if (!(cst instanceof Type)) throw new IllegalArgumentException("LdcInsnNode with invalid payload type " + cst.getClass() + " in getConstant");
            return Type.getType("Ljava/lang/Class;");
        }
        int index = Ints.indexOf((int[])CONSTANTS_ALL, (int)insn.getOpcode());
        if (index < 0) {
            return null;
        }
        Type type = Type.getType(CONSTANTS_TYPES[index]);
        return type;
    }

    public static boolean hasFlag(ClassNode classNode, int flag) {
        if ((classNode.access & flag) != flag) return false;
        return true;
    }

    public static boolean hasFlag(MethodNode method, int flag) {
        if ((method.access & flag) != flag) return false;
        return true;
    }

    public static boolean hasFlag(FieldNode field, int flag) {
        if ((field.access & flag) != flag) return false;
        return true;
    }

    public static boolean compareFlags(MethodNode m1, MethodNode m2, int flag) {
        if (Bytecode.hasFlag(m1, flag) != Bytecode.hasFlag(m2, flag)) return false;
        return true;
    }

    public static boolean compareFlags(FieldNode f1, FieldNode f2, int flag) {
        if (Bytecode.hasFlag(f1, flag) != Bytecode.hasFlag(f2, flag)) return false;
        return true;
    }

    public static Visibility getVisibility(MethodNode method) {
        return Bytecode.getVisibility(method.access & 7);
    }

    public static Visibility getVisibility(FieldNode field) {
        return Bytecode.getVisibility(field.access & 7);
    }

    private static Visibility getVisibility(int flags) {
        if ((flags & 4) != 0) {
            return Visibility.PROTECTED;
        }
        if ((flags & 2) != 0) {
            return Visibility.PRIVATE;
        }
        if ((flags & 1) == 0) return Visibility.PACKAGE;
        return Visibility.PUBLIC;
    }

    public static void setVisibility(MethodNode method, Visibility visibility) {
        method.access = Bytecode.setVisibility(method.access, visibility.access);
    }

    public static void setVisibility(FieldNode field, Visibility visibility) {
        field.access = Bytecode.setVisibility(field.access, visibility.access);
    }

    public static void setVisibility(MethodNode method, int access) {
        method.access = Bytecode.setVisibility(method.access, access);
    }

    public static void setVisibility(FieldNode field, int access) {
        field.access = Bytecode.setVisibility(field.access, access);
    }

    private static int setVisibility(int oldAccess, int newAccess) {
        return oldAccess & -8 | newAccess & 7;
    }

    public static int getMaxLineNumber(ClassNode classNode, int min, int pad) {
        int max = 0;
        Iterator<MethodNode> iterator = classNode.methods.iterator();
        block0 : while (iterator.hasNext()) {
            MethodNode method = iterator.next();
            ListIterator<AbstractInsnNode> iter = method.instructions.iterator();
            do {
                if (!iter.hasNext()) continue block0;
                AbstractInsnNode insn = (AbstractInsnNode)iter.next();
                if (!(insn instanceof LineNumberNode)) continue;
                max = Math.max(max, ((LineNumberNode)insn).line);
            } while (true);
            break;
        }
        return Math.max(min, max + pad);
    }

    public static String getBoxingType(Type type) {
        if (type == null) {
            return null;
        }
        String string = BOXING_TYPES[type.getSort()];
        return string;
    }

    public static String getUnboxingMethod(Type type) {
        if (type == null) {
            return null;
        }
        String string = UNBOXING_METHODS[type.getSort()];
        return string;
    }

    public static void mergeAnnotations(ClassNode from, ClassNode to) {
        to.visibleAnnotations = Bytecode.mergeAnnotations(from.visibleAnnotations, to.visibleAnnotations, "class", from.name);
        to.invisibleAnnotations = Bytecode.mergeAnnotations(from.invisibleAnnotations, to.invisibleAnnotations, "class", from.name);
    }

    public static void mergeAnnotations(MethodNode from, MethodNode to) {
        to.visibleAnnotations = Bytecode.mergeAnnotations(from.visibleAnnotations, to.visibleAnnotations, "method", from.name);
        to.invisibleAnnotations = Bytecode.mergeAnnotations(from.invisibleAnnotations, to.invisibleAnnotations, "method", from.name);
    }

    public static void mergeAnnotations(FieldNode from, FieldNode to) {
        to.visibleAnnotations = Bytecode.mergeAnnotations(from.visibleAnnotations, to.visibleAnnotations, "field", from.name);
        to.invisibleAnnotations = Bytecode.mergeAnnotations(from.invisibleAnnotations, to.invisibleAnnotations, "field", from.name);
    }

    private static List<AnnotationNode> mergeAnnotations(List<AnnotationNode> from, List<AnnotationNode> to, String type, String name) {
        try {
            if (from == null) {
                return to;
            }
            if (to == null) {
                to = new ArrayList<AnnotationNode>();
            }
            Iterator<AnnotationNode> iterator = from.iterator();
            while (iterator.hasNext()) {
                AnnotationNode annotation = iterator.next();
                if (!Bytecode.isMergeableAnnotation(annotation)) continue;
                Iterator<AnnotationNode> iter = to.iterator();
                while (iter.hasNext()) {
                    if (!iter.next().desc.equals(annotation.desc)) continue;
                    iter.remove();
                    break;
                }
                to.add(annotation);
            }
            return to;
        }
        catch (Exception ex) {
            logger.warn("Exception encountered whilst merging annotations for {} {}", new Object[]{type, name});
        }
        return to;
    }

    private static boolean isMergeableAnnotation(AnnotationNode annotation) {
        if (!annotation.desc.startsWith("L" + Constants.MIXIN_PACKAGE_REF)) return true;
        return mergeableAnnotationPattern.matcher(annotation.desc).matches();
    }

    private static Pattern getMergeableAnnotationPattern() {
        StringBuilder sb = new StringBuilder("^L(");
        int i = 0;
        while (i < MERGEABLE_MIXIN_ANNOTATIONS.length) {
            if (i > 0) {
                sb.append('|');
            }
            sb.append(MERGEABLE_MIXIN_ANNOTATIONS[i].getName().replace('.', '/'));
            ++i;
        }
        return Pattern.compile(sb.append(");$").toString());
    }

    public static void compareBridgeMethods(MethodNode a, MethodNode b) {
        ListIterator<AbstractInsnNode> ia = a.instructions.iterator();
        ListIterator<AbstractInsnNode> ib = b.instructions.iterator();
        int index = 0;
        while (ia.hasNext() && ib.hasNext()) {
            AbstractInsnNode na = ia.next();
            AbstractInsnNode nb = ib.next();
            if (!(na instanceof LabelNode)) {
                if (na instanceof MethodInsnNode) {
                    MethodInsnNode ma = (MethodInsnNode)na;
                    MethodInsnNode mb = (MethodInsnNode)nb;
                    if (!ma.name.equals(mb.name)) {
                        throw new SyntheticBridgeException(SyntheticBridgeException.Problem.BAD_INVOKE_NAME, a.name, a.desc, index, na, nb);
                    }
                    if (!ma.desc.equals(mb.desc)) {
                        throw new SyntheticBridgeException(SyntheticBridgeException.Problem.BAD_INVOKE_DESC, a.name, a.desc, index, na, nb);
                    }
                } else {
                    if (na.getOpcode() != nb.getOpcode()) {
                        throw new SyntheticBridgeException(SyntheticBridgeException.Problem.BAD_INSN, a.name, a.desc, index, na, nb);
                    }
                    if (na instanceof VarInsnNode) {
                        VarInsnNode va = (VarInsnNode)na;
                        VarInsnNode vb = (VarInsnNode)nb;
                        if (va.var != vb.var) {
                            throw new SyntheticBridgeException(SyntheticBridgeException.Problem.BAD_LOAD, a.name, a.desc, index, na, nb);
                        }
                    } else if (na instanceof TypeInsnNode) {
                        TypeInsnNode ta = (TypeInsnNode)na;
                        TypeInsnNode tb = (TypeInsnNode)nb;
                        if (ta.getOpcode() == 192 && !ta.desc.equals(tb.desc)) {
                            throw new SyntheticBridgeException(SyntheticBridgeException.Problem.BAD_CAST, a.name, a.desc, index, na, nb);
                        }
                    }
                }
            }
            ++index;
        }
        if (ia.hasNext()) throw new SyntheticBridgeException(SyntheticBridgeException.Problem.BAD_LENGTH, a.name, a.desc, index, null, null);
        if (!ib.hasNext()) return;
        throw new SyntheticBridgeException(SyntheticBridgeException.Problem.BAD_LENGTH, a.name, a.desc, index, null, null);
    }

    public static enum Visibility {
        PRIVATE(2),
        PROTECTED(4),
        PACKAGE(0),
        PUBLIC(1);
        
        static final int MASK = 7;
        final int access;

        private Visibility(int access) {
            this.access = access;
        }
    }

}

