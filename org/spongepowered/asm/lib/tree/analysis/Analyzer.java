/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.lib.tree.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.IincInsnNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.lib.tree.JumpInsnNode;
import org.spongepowered.asm.lib.tree.LabelNode;
import org.spongepowered.asm.lib.tree.LookupSwitchInsnNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.lib.tree.TableSwitchInsnNode;
import org.spongepowered.asm.lib.tree.TryCatchBlockNode;
import org.spongepowered.asm.lib.tree.VarInsnNode;
import org.spongepowered.asm.lib.tree.analysis.AnalyzerException;
import org.spongepowered.asm.lib.tree.analysis.Frame;
import org.spongepowered.asm.lib.tree.analysis.Interpreter;
import org.spongepowered.asm.lib.tree.analysis.Subroutine;
import org.spongepowered.asm.lib.tree.analysis.Value;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Analyzer<V extends Value>
implements Opcodes {
    private final Interpreter<V> interpreter;
    private int n;
    private InsnList insns;
    private List<TryCatchBlockNode>[] handlers;
    private Frame<V>[] frames;
    private Subroutine[] subroutines;
    private boolean[] queued;
    private int[] queue;
    private int top;

    public Analyzer(Interpreter<V> interpreter) {
        this.interpreter = interpreter;
    }

    /*
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    public Frame<V>[] analyze(String owner, MethodNode m) throws AnalyzerException {
        if ((m.access & 1280) != 0) {
            this.frames = new Frame[0];
            return this.frames;
        }
        this.n = m.instructions.size();
        this.insns = m.instructions;
        this.handlers = new List[this.n];
        this.frames = new Frame[this.n];
        this.subroutines = new Subroutine[this.n];
        this.queued = new boolean[this.n];
        this.queue = new int[this.n];
        this.top = 0;
        for (i = 0; i < m.tryCatchBlocks.size(); ++i) {
            tcb = m.tryCatchBlocks.get(i);
            begin = this.insns.indexOf(tcb.start);
            end = this.insns.indexOf(tcb.end);
            for (j = begin; j < end; ++j) {
                insnHandlers = this.handlers[j];
                if (insnHandlers == null) {
                    this.handlers[j] = insnHandlers = new ArrayList<TryCatchBlockNode>();
                }
                insnHandlers.add(tcb);
            }
        }
        main = new Subroutine(null, m.maxLocals, null);
        subroutineCalls = new ArrayList<AbstractInsnNode>();
        subroutineHeads = new HashMap<LabelNode, Subroutine>();
        this.findSubroutine(0, main, subroutineCalls);
        while (!subroutineCalls.isEmpty()) {
            jsr = (JumpInsnNode)subroutineCalls.remove(0);
            sub = (Subroutine)subroutineHeads.get(jsr.label);
            if (sub == null) {
                sub = new Subroutine(jsr.label, m.maxLocals, jsr);
                subroutineHeads.put(jsr.label, sub);
                this.findSubroutine(this.insns.indexOf(jsr.label), sub, subroutineCalls);
                continue;
            }
            sub.callers.add(jsr);
        }
        for (i = 0; i < this.n; ++i) {
            if (this.subroutines[i] == null || this.subroutines[i].start != null) continue;
            this.subroutines[i] = null;
        }
        current = this.newFrame(m.maxLocals, m.maxStack);
        handler = this.newFrame(m.maxLocals, m.maxStack);
        current.setReturn(this.interpreter.newValue(Type.getReturnType(m.desc)));
        args = Type.getArgumentTypes(m.desc);
        local = 0;
        if ((m.access & 8) == 0) {
            ctype = Type.getObjectType(owner);
            current.setLocal(local++, this.interpreter.newValue(ctype));
        }
        for (i = 0; i < args.length; ++i) {
            current.setLocal(local++, this.interpreter.newValue(args[i]));
            if (args[i].getSize() != 2) continue;
            current.setLocal(local++, this.interpreter.newValue(null));
        }
        while (local < m.maxLocals) {
            current.setLocal(local++, this.interpreter.newValue(null));
        }
        this.merge(0, current, null);
        this.init(owner, m);
        block9 : do lbl-1000: // 3 sources:
        {
            if (this.top <= 0) return this.frames;
            insn = this.queue[--this.top];
            f = this.frames[insn];
            subroutine = this.subroutines[insn];
            this.queued[insn] = false;
            insnNode = null;
            insnNode = m.instructions.get(insn);
            insnOpcode = insnNode.getOpcode();
            insnType = insnNode.getType();
            if (insnType == 8 || insnType == 15 || insnType == 14) {
                this.merge(insn + 1, f, subroutine);
                this.newControlFlowEdge(insn, insn + 1);
            } else {
                current.init(f).execute(insnNode, this.interpreter);
                v0 = subroutine = subroutine == null ? null : subroutine.copy();
                if (insnNode instanceof JumpInsnNode) {
                    j = (JumpInsnNode)insnNode;
                    if (insnOpcode != 167 && insnOpcode != 168) {
                        this.merge(insn + 1, current, subroutine);
                        this.newControlFlowEdge(insn, insn + 1);
                    }
                    jump = this.insns.indexOf(j.label);
                    if (insnOpcode == 168) {
                        this.merge(jump, current, new Subroutine(j.label, m.maxLocals, j));
                    } else {
                        this.merge(jump, current, subroutine);
                    }
                    this.newControlFlowEdge(insn, jump);
                } else if (insnNode instanceof LookupSwitchInsnNode) {
                    lsi = (LookupSwitchInsnNode)insnNode;
                    jump = this.insns.indexOf(lsi.dflt);
                    this.merge(jump, current, subroutine);
                    this.newControlFlowEdge(insn, jump);
                    for (j = 0; j < lsi.labels.size(); ++j) {
                        label = lsi.labels.get(j);
                        jump = this.insns.indexOf(label);
                        this.merge(jump, current, subroutine);
                        this.newControlFlowEdge(insn, jump);
                    }
                } else if (insnNode instanceof TableSwitchInsnNode) {
                    tsi = (TableSwitchInsnNode)insnNode;
                    jump = this.insns.indexOf(tsi.dflt);
                    this.merge(jump, current, subroutine);
                    this.newControlFlowEdge(insn, jump);
                    for (j = 0; j < tsi.labels.size(); ++j) {
                        label = tsi.labels.get(j);
                        jump = this.insns.indexOf(label);
                        this.merge(jump, current, subroutine);
                        this.newControlFlowEdge(insn, jump);
                    }
                } else if (insnOpcode == 169) {
                    if (subroutine == null) {
                        throw new AnalyzerException(insnNode, "RET instruction outside of a sub routine");
                    }
                    for (i = 0; i < subroutine.callers.size(); ++i) {
                        caller = subroutine.callers.get(i);
                        call = this.insns.indexOf(caller);
                        if (this.frames[call] == null) continue;
                        this.merge(call + 1, this.frames[call], current, this.subroutines[call], subroutine.access);
                        this.newControlFlowEdge(insn, call + 1);
                    }
                } else if (insnOpcode != 191 && (insnOpcode < 172 || insnOpcode > 177)) {
                    if (subroutine != null) {
                        if (insnNode instanceof VarInsnNode) {
                            var = ((VarInsnNode)insnNode).var;
                            subroutine.access[var] = true;
                            if (insnOpcode == 22 || insnOpcode == 24 || insnOpcode == 55 || insnOpcode == 57) {
                                subroutine.access[var + 1] = true;
                            }
                        } else if (insnNode instanceof IincInsnNode) {
                            var = ((IincInsnNode)insnNode).var;
                            subroutine.access[var] = true;
                        }
                    }
                    this.merge(insn + 1, current, subroutine);
                    this.newControlFlowEdge(insn, insn + 1);
                }
            }
            if ((insnHandlers = this.handlers[insn]) == null) ** GOTO lbl-1000
            i = 0;
            do {
                if (i >= insnHandlers.size()) continue block9;
                tcb = insnHandlers.get(i);
                type = tcb.type == null ? Type.getObjectType("java/lang/Throwable") : Type.getObjectType(tcb.type);
                jump = this.insns.indexOf(tcb.handler);
                if (this.newControlFlowExceptionEdge(insn, tcb)) {
                    handler.init(f);
                    handler.clearStack();
                    handler.push(this.interpreter.newValue(type));
                    this.merge(jump, handler, subroutine);
                }
                ++i;
            } while (true);
            break;
        } while (true);
        catch (AnalyzerException e) {
            throw new AnalyzerException(e.node, "Error at instruction " + insn + ": " + e.getMessage(), e);
        }
        catch (Exception e) {
            throw new AnalyzerException(insnNode, "Error at instruction " + insn + ": " + e.getMessage(), e);
        }
    }

    private void findSubroutine(int insn, Subroutine sub, List<AbstractInsnNode> calls) throws AnalyzerException {
        while (insn >= 0) {
            LabelNode l;
            int i;
            if (insn >= this.n) {
                throw new AnalyzerException(null, "Execution can fall off end of the code");
            }
            if (this.subroutines[insn] != null) {
                return;
            }
            this.subroutines[insn] = sub.copy();
            AbstractInsnNode node = this.insns.get(insn);
            if (node instanceof JumpInsnNode) {
                if (node.getOpcode() == 168) {
                    calls.add(node);
                } else {
                    JumpInsnNode jnode = (JumpInsnNode)node;
                    this.findSubroutine(this.insns.indexOf(jnode.label), sub, calls);
                }
            } else if (node instanceof TableSwitchInsnNode) {
                TableSwitchInsnNode tsnode = (TableSwitchInsnNode)node;
                this.findSubroutine(this.insns.indexOf(tsnode.dflt), sub, calls);
                for (i = tsnode.labels.size() - 1; i >= 0; --i) {
                    l = tsnode.labels.get(i);
                    this.findSubroutine(this.insns.indexOf(l), sub, calls);
                }
            } else if (node instanceof LookupSwitchInsnNode) {
                LookupSwitchInsnNode lsnode = (LookupSwitchInsnNode)node;
                this.findSubroutine(this.insns.indexOf(lsnode.dflt), sub, calls);
                for (i = lsnode.labels.size() - 1; i >= 0; --i) {
                    l = lsnode.labels.get(i);
                    this.findSubroutine(this.insns.indexOf(l), sub, calls);
                }
            }
            List<TryCatchBlockNode> insnHandlers = this.handlers[insn];
            if (insnHandlers != null) {
                for (i = 0; i < insnHandlers.size(); ++i) {
                    TryCatchBlockNode tcb = insnHandlers.get(i);
                    this.findSubroutine(this.insns.indexOf(tcb.handler), sub, calls);
                }
            }
            switch (node.getOpcode()) {
                case 167: 
                case 169: 
                case 170: 
                case 171: 
                case 172: 
                case 173: 
                case 174: 
                case 175: 
                case 176: 
                case 177: 
                case 191: {
                    return;
                }
            }
            ++insn;
        }
        throw new AnalyzerException(null, "Execution can fall off end of the code");
    }

    public Frame<V>[] getFrames() {
        return this.frames;
    }

    public List<TryCatchBlockNode> getHandlers(int insn) {
        return this.handlers[insn];
    }

    protected void init(String owner, MethodNode m) throws AnalyzerException {
    }

    protected Frame<V> newFrame(int nLocals, int nStack) {
        return new Frame(nLocals, nStack);
    }

    protected Frame<V> newFrame(Frame<? extends V> src) {
        return new Frame<V>(src);
    }

    protected void newControlFlowEdge(int insn, int successor) {
    }

    protected boolean newControlFlowExceptionEdge(int insn, int successor) {
        return true;
    }

    protected boolean newControlFlowExceptionEdge(int insn, TryCatchBlockNode tcb) {
        return this.newControlFlowExceptionEdge(insn, this.insns.indexOf(tcb.handler));
    }

    private void merge(int insn, Frame<V> frame, Subroutine subroutine) throws AnalyzerException {
        boolean changes;
        Frame<V> oldFrame = this.frames[insn];
        Subroutine oldSubroutine = this.subroutines[insn];
        if (oldFrame == null) {
            this.frames[insn] = this.newFrame(frame);
            changes = true;
        } else {
            changes = oldFrame.merge(frame, this.interpreter);
        }
        if (oldSubroutine == null) {
            if (subroutine != null) {
                this.subroutines[insn] = subroutine.copy();
                changes = true;
            }
        } else if (subroutine != null) {
            changes |= oldSubroutine.merge(subroutine);
        }
        if (!changes) return;
        if (this.queued[insn]) return;
        this.queued[insn] = true;
        this.queue[this.top++] = insn;
    }

    private void merge(int insn, Frame<V> beforeJSR, Frame<V> afterRET, Subroutine subroutineBeforeJSR, boolean[] access) throws AnalyzerException {
        boolean changes;
        Frame<V> oldFrame = this.frames[insn];
        Subroutine oldSubroutine = this.subroutines[insn];
        afterRET.merge(beforeJSR, access);
        if (oldFrame == null) {
            this.frames[insn] = this.newFrame(afterRET);
            changes = true;
        } else {
            changes = oldFrame.merge(afterRET, this.interpreter);
        }
        if (oldSubroutine != null && subroutineBeforeJSR != null) {
            changes |= oldSubroutine.merge(subroutineBeforeJSR);
        }
        if (!changes) return;
        if (this.queued[insn]) return;
        this.queued[insn] = true;
        this.queue[this.top++] = insn;
    }
}

