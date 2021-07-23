/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.lib;

import org.spongepowered.asm.lib.ByteVector;
import org.spongepowered.asm.lib.Edge;
import org.spongepowered.asm.lib.Frame;
import org.spongepowered.asm.lib.MethodWriter;

public class Label {
    static final int DEBUG = 1;
    static final int RESOLVED = 2;
    static final int RESIZED = 4;
    static final int PUSHED = 8;
    static final int TARGET = 16;
    static final int STORE = 32;
    static final int REACHABLE = 64;
    static final int JSR = 128;
    static final int RET = 256;
    static final int SUBROUTINE = 512;
    static final int VISITED = 1024;
    static final int VISITED2 = 2048;
    public Object info;
    int status;
    int line;
    int position;
    private int referenceCount;
    private int[] srcAndRefPositions;
    int inputStackTop;
    int outputStackMax;
    Frame frame;
    Label successor;
    Edge successors;
    Label next;

    public int getOffset() {
        if ((this.status & 2) != 0) return this.position;
        throw new IllegalStateException("Label offset position has not been resolved yet");
    }

    void put(MethodWriter owner, ByteVector out, int source, boolean wideOffset) {
        if ((this.status & 2) == 0) {
            if (wideOffset) {
                this.addReference(-1 - source, out.length);
                out.putInt(-1);
                return;
            }
            this.addReference(source, out.length);
            out.putShort(-1);
            return;
        }
        if (wideOffset) {
            out.putInt(this.position - source);
            return;
        }
        out.putShort(this.position - source);
    }

    private void addReference(int sourcePosition, int referencePosition) {
        if (this.srcAndRefPositions == null) {
            this.srcAndRefPositions = new int[6];
        }
        if (this.referenceCount >= this.srcAndRefPositions.length) {
            int[] a = new int[this.srcAndRefPositions.length + 6];
            System.arraycopy(this.srcAndRefPositions, 0, a, 0, this.srcAndRefPositions.length);
            this.srcAndRefPositions = a;
        }
        this.srcAndRefPositions[this.referenceCount++] = sourcePosition;
        this.srcAndRefPositions[this.referenceCount++] = referencePosition;
    }

    boolean resolve(MethodWriter owner, int position, byte[] data) {
        boolean needUpdate = false;
        this.status |= 2;
        this.position = position;
        int i = 0;
        while (i < this.referenceCount) {
            int offset;
            int source = this.srcAndRefPositions[i++];
            int reference = this.srcAndRefPositions[i++];
            if (source >= 0) {
                offset = position - source;
                if (offset < -32768 || offset > 32767) {
                    int opcode = data[reference - 1] & 255;
                    data[reference - 1] = opcode <= 168 ? (byte)(opcode + 49) : (byte)(opcode + 20);
                    needUpdate = true;
                }
                data[reference++] = (byte)(offset >>> 8);
                data[reference] = (byte)offset;
                continue;
            }
            offset = position + source + 1;
            data[reference++] = (byte)(offset >>> 24);
            data[reference++] = (byte)(offset >>> 16);
            data[reference++] = (byte)(offset >>> 8);
            data[reference] = (byte)offset;
        }
        return needUpdate;
    }

    Label getFirst() {
        Label label;
        if (this.frame == null) {
            label = this;
            return label;
        }
        label = this.frame.owner;
        return label;
    }

    boolean inSubroutine(long id) {
        if ((this.status & 1024) == 0) return false;
        if ((this.srcAndRefPositions[(int)(id >>> 32)] & (int)id) == 0) return false;
        return true;
    }

    boolean inSameSubroutine(Label block) {
        if ((this.status & 1024) == 0) return false;
        if ((block.status & 1024) == 0) {
            return false;
        }
        int i = 0;
        while (i < this.srcAndRefPositions.length) {
            if ((this.srcAndRefPositions[i] & block.srcAndRefPositions[i]) != 0) {
                return true;
            }
            ++i;
        }
        return false;
    }

    void addToSubroutine(long id, int nbSubroutines) {
        if ((this.status & 1024) == 0) {
            this.status |= 1024;
            this.srcAndRefPositions = new int[nbSubroutines / 32 + 1];
        }
        int[] arrn = this.srcAndRefPositions;
        int n = (int)(id >>> 32);
        arrn[n] = arrn[n] | (int)id;
    }

    /*
     * Unable to fully structure code
     */
    void visitSubroutine(Label JSR, long id, int nbSubroutines) {
        stack = this;
        block0 : do lbl-1000: // 4 sources:
        {
            block5 : {
                block4 : {
                    if (stack == null) return;
                    l = stack;
                    stack = l.next;
                    l.next = null;
                    if (JSR == null) break block4;
                    if ((l.status & 2048) != 0) ** GOTO lbl-1000
                    l.status |= 2048;
                    if ((l.status & 256) != 0 && !l.inSameSubroutine(JSR)) {
                        e = new Edge();
                        e.info = l.inputStackTop;
                        e.successor = JSR.successors.successor;
                        e.next = l.successors;
                        l.successors = e;
                    }
                    break block5;
                }
                if (l.inSubroutine(id)) ** GOTO lbl-1000
                l.addToSubroutine(id, nbSubroutines);
            }
            e = l.successors;
            do {
                if (e == null) continue block0;
                if (((l.status & 128) == 0 || e != l.successors.next) && e.successor.next == null) {
                    e.successor.next = stack;
                    stack = e.successor;
                }
                e = e.next;
            } while (true);
            break;
        } while (true);
    }

    public String toString() {
        return "L" + System.identityHashCode(this);
    }
}

