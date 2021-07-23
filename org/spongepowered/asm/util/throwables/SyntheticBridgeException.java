/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.util.throwables;

import java.io.PrintStream;
import java.util.ListIterator;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.lib.tree.MethodInsnNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.lib.tree.TypeInsnNode;
import org.spongepowered.asm.lib.tree.VarInsnNode;
import org.spongepowered.asm.mixin.refmap.IMixinContext;
import org.spongepowered.asm.mixin.throwables.MixinException;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.mixin.transformer.meta.MixinMerged;
import org.spongepowered.asm.util.Annotations;
import org.spongepowered.asm.util.Bytecode;
import org.spongepowered.asm.util.PrettyPrinter;

public class SyntheticBridgeException
extends MixinException {
    private static final long serialVersionUID = 1L;
    private final Problem problem;
    private final String name;
    private final String desc;
    private final int index;
    private final AbstractInsnNode a;
    private final AbstractInsnNode b;

    public SyntheticBridgeException(Problem problem, String name, String desc, int index, AbstractInsnNode a, AbstractInsnNode b) {
        super(problem.getMessage(name, desc, index, a, b));
        this.problem = problem;
        this.name = name;
        this.desc = desc;
        this.index = index;
        this.a = a;
        this.b = b;
    }

    public void printAnalysis(IMixinContext context, MethodNode mda, MethodNode mdb) {
        PrettyPrinter printer = new PrettyPrinter();
        printer.addWrapped(100, this.getMessage(), new Object[0]).hr();
        printer.add().kv("Method", this.name + this.desc).kv("Problem Type", (Object)this.problem).add().hr();
        String merged = (String)Annotations.getValue(Annotations.getVisible(mda, MixinMerged.class), "mixin");
        String owner = merged != null ? merged : context.getTargetClassRef().replace('/', '.');
        this.printMethod(printer.add("Existing method").add().kv("Owner", owner).add(), mda).hr();
        this.printMethod(printer.add("Incoming method").add().kv("Owner", context.getClassRef().replace('/', '.')).add(), mdb).hr();
        this.printProblem(printer, context, mda, mdb).print(System.err);
    }

    private PrettyPrinter printMethod(PrettyPrinter printer, MethodNode method) {
        int index = 0;
        ListIterator<AbstractInsnNode> iter = method.instructions.iterator();
        while (iter.hasNext()) {
            printer.kv(index == this.index ? ">>>>" : "", Bytecode.describeNode((AbstractInsnNode)iter.next()));
            ++index;
        }
        return printer.add();
    }

    /*
     * Unable to fully structure code
     */
    private PrettyPrinter printProblem(PrettyPrinter printer, IMixinContext context, MethodNode mda, MethodNode mdb) {
        target = Type.getObjectType(context.getTargetClassRef());
        printer.add("Analysis").add();
        switch (1.$SwitchMap$org$spongepowered$asm$util$throwables$SyntheticBridgeException$Problem[this.problem.ordinal()]) {
            case 1: {
                printer.add("The bridge methods are not compatible because they contain incompatible opcodes");
                printer.add("at index " + this.index + ":").add();
                printer.kv("Existing opcode: %s", Bytecode.getOpcodeName(this.a));
                printer.kv("Incoming opcode: %s", Bytecode.getOpcodeName(this.b)).add();
                printer.add("This implies that the bridge methods are from different interfaces. This problem");
                printer.add("may not be resolvable without changing the base interfaces.").add();
                return printer;
            }
            case 2: {
                printer.add("The bridge methods are not compatible because they contain different variables at");
                printer.add("opcode index " + this.index + ".").add();
                ia = mda.instructions.iterator();
                ib = mdb.instructions.iterator();
                argsa = Type.getArgumentTypes(mda.desc);
                argsb = Type.getArgumentTypes(mdb.desc);
                index = 0;
                while (ia.hasNext() && ib.hasNext()) {
                    na = ia.next();
                    nb = ib.next();
                    if (na instanceof VarInsnNode && nb instanceof VarInsnNode) {
                        va = (VarInsnNode)na;
                        vb = (VarInsnNode)nb;
                        ta = va.var > 0 ? argsa[va.var - 1] : target;
                        tb = vb.var > 0 ? argsb[vb.var - 1] : target;
                        printer.kv("Target " + index, "%8s %-2d %s", new Object[]{Bytecode.getOpcodeName(va), va.var, ta});
                        printer.kv("Incoming " + index, "%8s %-2d %s", new Object[]{Bytecode.getOpcodeName(vb), vb.var, tb});
                        if (ta.equals(tb)) {
                            printer.kv("", "Types match: %s", new Object[]{ta});
                        } else if (ta.getSort() != tb.getSort()) {
                            printer.kv("", "Types are incompatible");
                        } else if (ta.getSort() == 10) {
                            superClass = ClassInfo.getCommonSuperClassOrInterface(ta, tb);
                            printer.kv("", "Common supertype: %s", new Object[]{superClass});
                        }
                        printer.add();
                    }
                    ++index;
                }
                printer.add("Since this probably means that the methods come from different interfaces, you");
                printer.add("may have a \"multiple inheritance\" problem, it may not be possible to implement");
                printer.add("both root interfaces");
                return printer;
            }
            case 3: {
                printer.add("Incompatible CHECKCAST encountered at opcode " + this.index + ", this could indicate that the bridge");
                printer.add("is casting down for contravariant generic types. It may be possible to coalesce the");
                printer.add("bridges by adjusting the types in the target method.").add();
                ta = Type.getObjectType(((TypeInsnNode)this.a).desc);
                tb = Type.getObjectType(((TypeInsnNode)this.b).desc);
                printer.kv("Target type", ta);
                printer.kv("Incoming type", tb);
                printer.kv("Common supertype", ClassInfo.getCommonSuperClassOrInterface(ta, tb)).add();
                return printer;
            }
            case 4: {
                printer.add("Incompatible invocation targets in synthetic bridge. This is extremely unusual");
                printer.add("and implies that a remapping transformer has incorrectly remapped a method. This");
                printer.add("is an unrecoverable error.");
                return printer;
            }
            case 5: {
                mdna = (MethodInsnNode)this.a;
                mdnb = (MethodInsnNode)this.b;
                arga = Type.getArgumentTypes(mdna.desc);
                argb = Type.getArgumentTypes(mdnb.desc);
                if (arga.length != argb.length) {
                    argCount = Type.getArgumentTypes(mda.desc).length;
                    winner = arga.length == argCount ? "The TARGET" : (argb.length == argCount ? " The INCOMING" : "NEITHER");
                    printer.add("Mismatched invocation descriptors in synthetic bridge implies that a remapping");
                    printer.add("transformer has incorrectly coalesced a bridge method with a conflicting name.");
                    printer.add("Overlapping bridge methods should always have the same number of arguments, yet");
                    printer.add("the target method has %d arguments, the incoming method has %d. This is an", new Object[]{arga.length, argb.length});
                    printer.add("unrecoverable error. %s method has the expected arg count of %d", new Object[]{winner, argCount});
                    return printer;
                }
                rta = Type.getReturnType(mdna.desc);
                rtb = Type.getReturnType(mdnb.desc);
                printer.add("Incompatible invocation descriptors in synthetic bridge implies that generified");
                printer.add("types are incompatible over one or more generic superclasses or interfaces. It may");
                printer.add("be possible to adjust the generic types on implemented members to rectify this");
                printer.add("problem by coalescing the appropriate generic types.").add();
                this.printTypeComparison(printer, "return type", rta, rtb);
                i = 0;
                while (i < arga.length) {
                    this.printTypeComparison(printer, "arg " + i, arga[i], argb[i]);
                    ++i;
                }
                return printer;
            }
            case 6: {
                printer.add("Mismatched bridge method length implies the bridge methods are incompatible");
                printer.add("and may originate from different superinterfaces. This is an unrecoverable");
                printer.add("error.").add();
                ** break;
            }
        }
lbl134: // 2 sources:
        return printer;
    }

    private PrettyPrinter printTypeComparison(PrettyPrinter printer, String index, Type tpa, Type tpb) {
        printer.kv("Target " + index, "%s", tpa);
        printer.kv("Incoming " + index, "%s", tpb);
        if (tpa.equals(tpb)) {
            printer.kv("Analysis", "Types match: %s", tpa);
            return printer.add();
        }
        if (tpa.getSort() != tpb.getSort()) {
            printer.kv("Analysis", "Types are incompatible");
            return printer.add();
        }
        if (tpa.getSort() != 10) return printer.add();
        ClassInfo superClass = ClassInfo.getCommonSuperClassOrInterface(tpa, tpb);
        printer.kv("Analysis", "Common supertype: L%s;", superClass);
        return printer.add();
    }

    public static enum Problem {
        BAD_INSN("Conflicting opcodes %4$s and %5$s at offset %3$d in synthetic bridge method %1$s%2$s"),
        BAD_LOAD("Conflicting variable access at offset %3$d in synthetic bridge method %1$s%2$s"),
        BAD_CAST("Conflicting type cast at offset %3$d in synthetic bridge method %1$s%2$s"),
        BAD_INVOKE_NAME("Conflicting synthetic bridge target method name in synthetic bridge method %1$s%2$s Existing:%6$s Incoming:%7$s"),
        BAD_INVOKE_DESC("Conflicting synthetic bridge target method descriptor in synthetic bridge method %1$s%2$s Existing:%8$s Incoming:%9$s"),
        BAD_LENGTH("Mismatched bridge method length for synthetic bridge method %1$s%2$s unexpected extra opcode at offset %3$d");
        
        private final String message;

        private Problem(String message) {
            this.message = message;
        }

        String getMessage(String name, String desc, int index, AbstractInsnNode a, AbstractInsnNode b) {
            return String.format(this.message, name, desc, index, Bytecode.getOpcodeName(a), Bytecode.getOpcodeName(a), Problem.getInsnName(a), Problem.getInsnName(b), Problem.getInsnDesc(a), Problem.getInsnDesc(b));
        }

        private static String getInsnName(AbstractInsnNode node) {
            if (!(node instanceof MethodInsnNode)) return "";
            String string = ((MethodInsnNode)node).name;
            return string;
        }

        private static String getInsnDesc(AbstractInsnNode node) {
            if (!(node instanceof MethodInsnNode)) return "";
            String string = ((MethodInsnNode)node).desc;
            return string;
        }
    }

}
