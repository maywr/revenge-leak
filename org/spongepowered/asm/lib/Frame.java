/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.lib;

import org.spongepowered.asm.lib.ClassWriter;
import org.spongepowered.asm.lib.Item;
import org.spongepowered.asm.lib.Label;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.lib.Type;

class Frame {
    static final int DIM = -268435456;
    static final int ARRAY_OF = 268435456;
    static final int ELEMENT_OF = -268435456;
    static final int KIND = 251658240;
    static final int TOP_IF_LONG_OR_DOUBLE = 8388608;
    static final int VALUE = 8388607;
    static final int BASE_KIND = 267386880;
    static final int BASE_VALUE = 1048575;
    static final int BASE = 16777216;
    static final int OBJECT = 24117248;
    static final int UNINITIALIZED = 25165824;
    private static final int LOCAL = 33554432;
    private static final int STACK = 50331648;
    static final int TOP = 16777216;
    static final int BOOLEAN = 16777225;
    static final int BYTE = 16777226;
    static final int CHAR = 16777227;
    static final int SHORT = 16777228;
    static final int INTEGER = 16777217;
    static final int FLOAT = 16777218;
    static final int DOUBLE = 16777219;
    static final int LONG = 16777220;
    static final int NULL = 16777221;
    static final int UNINITIALIZED_THIS = 16777222;
    static final int[] SIZE;
    Label owner;
    int[] inputLocals;
    int[] inputStack;
    private int[] outputLocals;
    private int[] outputStack;
    int outputStackTop;
    private int initializationCount;
    private int[] initializations;

    Frame() {
    }

    final void set(ClassWriter cw, int nLocal, Object[] local, int nStack, Object[] stack) {
        int i = Frame.convert(cw, nLocal, local, this.inputLocals);
        while (i < local.length) {
            this.inputLocals[i++] = 16777216;
        }
        int nStackTop = 0;
        int j = 0;
        do {
            if (j >= nStack) {
                this.inputStack = new int[nStack + nStackTop];
                Frame.convert(cw, nStack, stack, this.inputStack);
                this.outputStackTop = 0;
                this.initializationCount = 0;
                return;
            }
            if (stack[j] == Opcodes.LONG || stack[j] == Opcodes.DOUBLE) {
                ++nStackTop;
            }
            ++j;
        } while (true);
    }

    private static int convert(ClassWriter cw, int nInput, Object[] input, int[] output) {
        int i = 0;
        int j = 0;
        while (j < nInput) {
            if (input[j] instanceof Integer) {
                output[i++] = 16777216 | (Integer)input[j];
                if (input[j] == Opcodes.LONG || input[j] == Opcodes.DOUBLE) {
                    output[i++] = 16777216;
                }
            } else {
                output[i++] = input[j] instanceof String ? Frame.type(cw, Type.getObjectType((String)input[j]).getDescriptor()) : 25165824 | cw.addUninitializedType("", ((Label)input[j]).position);
            }
            ++j;
        }
        return i;
    }

    final void set(Frame f) {
        this.inputLocals = f.inputLocals;
        this.inputStack = f.inputStack;
        this.outputLocals = f.outputLocals;
        this.outputStack = f.outputStack;
        this.outputStackTop = f.outputStackTop;
        this.initializationCount = f.initializationCount;
        this.initializations = f.initializations;
    }

    private int get(int local) {
        if (this.outputLocals == null) return 33554432 | local;
        if (local >= this.outputLocals.length) {
            return 33554432 | local;
        }
        int type = this.outputLocals[local];
        if (type != 0) return type;
        this.outputLocals[local] = 33554432 | local;
        return this.outputLocals[local];
    }

    private void set(int local, int type) {
        int n;
        if (this.outputLocals == null) {
            this.outputLocals = new int[10];
        }
        if (local >= (n = this.outputLocals.length)) {
            int[] t = new int[Math.max(local + 1, 2 * n)];
            System.arraycopy(this.outputLocals, 0, t, 0, n);
            this.outputLocals = t;
        }
        this.outputLocals[local] = type;
    }

    private void push(int type) {
        int n;
        if (this.outputStack == null) {
            this.outputStack = new int[10];
        }
        if (this.outputStackTop >= (n = this.outputStack.length)) {
            int[] t = new int[Math.max(this.outputStackTop + 1, 2 * n)];
            System.arraycopy(this.outputStack, 0, t, 0, n);
            this.outputStack = t;
        }
        this.outputStack[this.outputStackTop++] = type;
        int top = this.owner.inputStackTop + this.outputStackTop;
        if (top <= this.owner.outputStackMax) return;
        this.owner.outputStackMax = top;
    }

    private void push(ClassWriter cw, String desc) {
        int type = Frame.type(cw, desc);
        if (type == 0) return;
        this.push(type);
        if (type != 16777220) {
            if (type != 16777219) return;
        }
        this.push(16777216);
    }

    private static int type(ClassWriter cw, String desc) {
        int data;
        int index = desc.charAt(0) == '(' ? desc.indexOf(41) + 1 : 0;
        switch (desc.charAt(index)) {
            case 'V': {
                return 0;
            }
            case 'B': 
            case 'C': 
            case 'I': 
            case 'S': 
            case 'Z': {
                return 16777217;
            }
            case 'F': {
                return 16777218;
            }
            case 'J': {
                return 16777220;
            }
            case 'D': {
                return 16777219;
            }
            case 'L': {
                String t = desc.substring(index + 1, desc.length() - 1);
                return 24117248 | cw.addType(t);
            }
        }
        int dims = index + 1;
        while (desc.charAt(dims) == '[') {
            ++dims;
        }
        switch (desc.charAt(dims)) {
            case 'Z': {
                data = 16777225;
                return dims - index << 28 | data;
            }
            case 'C': {
                data = 16777227;
                return dims - index << 28 | data;
            }
            case 'B': {
                data = 16777226;
                return dims - index << 28 | data;
            }
            case 'S': {
                data = 16777228;
                return dims - index << 28 | data;
            }
            case 'I': {
                data = 16777217;
                return dims - index << 28 | data;
            }
            case 'F': {
                data = 16777218;
                return dims - index << 28 | data;
            }
            case 'J': {
                data = 16777220;
                return dims - index << 28 | data;
            }
            case 'D': {
                data = 16777219;
                return dims - index << 28 | data;
            }
        }
        String t = desc.substring(dims + 1, desc.length() - 1);
        data = 24117248 | cw.addType(t);
        return dims - index << 28 | data;
    }

    private int pop() {
        if (this.outputStackTop <= 0) return 50331648 | -(--this.owner.inputStackTop);
        return this.outputStack[--this.outputStackTop];
    }

    private void pop(int elements) {
        if (this.outputStackTop >= elements) {
            this.outputStackTop -= elements;
            return;
        }
        this.owner.inputStackTop -= elements - this.outputStackTop;
        this.outputStackTop = 0;
    }

    private void pop(String desc) {
        char c = desc.charAt(0);
        if (c == '(') {
            this.pop((Type.getArgumentsAndReturnSizes(desc) >> 2) - 1);
            return;
        }
        if (c != 'J' && c != 'D') {
            this.pop(1);
            return;
        }
        this.pop(2);
    }

    private void init(int var) {
        int n;
        if (this.initializations == null) {
            this.initializations = new int[2];
        }
        if (this.initializationCount >= (n = this.initializations.length)) {
            int[] t = new int[Math.max(this.initializationCount + 1, 2 * n)];
            System.arraycopy(this.initializations, 0, t, 0, n);
            this.initializations = t;
        }
        this.initializations[this.initializationCount++] = var;
    }

    private int init(ClassWriter cw, int t) {
        int s;
        if (t == 16777222) {
            s = 24117248 | cw.addType(cw.thisName);
        } else {
            if ((t & -1048576) != 25165824) return t;
            String type = cw.typeTable[t & 1048575].strVal1;
            s = 24117248 | cw.addType(type);
        }
        int j = 0;
        while (j < this.initializationCount) {
            int u = this.initializations[j];
            int dim = u & -268435456;
            int kind = u & 251658240;
            if (kind == 33554432) {
                u = dim + this.inputLocals[u & 8388607];
            } else if (kind == 50331648) {
                u = dim + this.inputStack[this.inputStack.length - (u & 8388607)];
            }
            if (t == u) {
                return s;
            }
            ++j;
        }
        return t;
    }

    final void initInputFrame(ClassWriter cw, int access, Type[] args, int maxLocals) {
        this.inputLocals = new int[maxLocals];
        this.inputStack = new int[0];
        int i = 0;
        if ((access & 8) == 0) {
            this.inputLocals[i++] = (access & 524288) == 0 ? 24117248 | cw.addType(cw.thisName) : 16777222;
        }
        int j = 0;
        do {
            if (j >= args.length) {
                while (i < maxLocals) {
                    this.inputLocals[i++] = 16777216;
                }
                return;
            }
            int t = Frame.type(cw, args[j].getDescriptor());
            this.inputLocals[i++] = t;
            if (t == 16777220 || t == 16777219) {
                this.inputLocals[i++] = 16777216;
            }
            ++j;
        } while (true);
    }

    void execute(int opcode, int arg, ClassWriter cw, Item item) {
        switch (opcode) {
            case 0: 
            case 116: 
            case 117: 
            case 118: 
            case 119: 
            case 145: 
            case 146: 
            case 147: 
            case 167: 
            case 177: {
                return;
            }
            case 1: {
                this.push(16777221);
                return;
            }
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 16: 
            case 17: 
            case 21: {
                this.push(16777217);
                return;
            }
            case 9: 
            case 10: 
            case 22: {
                this.push(16777220);
                this.push(16777216);
                return;
            }
            case 11: 
            case 12: 
            case 13: 
            case 23: {
                this.push(16777218);
                return;
            }
            case 14: 
            case 15: 
            case 24: {
                this.push(16777219);
                this.push(16777216);
                return;
            }
            case 18: {
                switch (item.type) {
                    case 3: {
                        this.push(16777217);
                        return;
                    }
                    case 5: {
                        this.push(16777220);
                        this.push(16777216);
                        return;
                    }
                    case 4: {
                        this.push(16777218);
                        return;
                    }
                    case 6: {
                        this.push(16777219);
                        this.push(16777216);
                        return;
                    }
                    case 7: {
                        this.push(24117248 | cw.addType("java/lang/Class"));
                        return;
                    }
                    case 8: {
                        this.push(24117248 | cw.addType("java/lang/String"));
                        return;
                    }
                    case 16: {
                        this.push(24117248 | cw.addType("java/lang/invoke/MethodType"));
                        return;
                    }
                }
                this.push(24117248 | cw.addType("java/lang/invoke/MethodHandle"));
                return;
            }
            case 25: {
                this.push(this.get(arg));
                return;
            }
            case 46: 
            case 51: 
            case 52: 
            case 53: {
                this.pop(2);
                this.push(16777217);
                return;
            }
            case 47: 
            case 143: {
                this.pop(2);
                this.push(16777220);
                this.push(16777216);
                return;
            }
            case 48: {
                this.pop(2);
                this.push(16777218);
                return;
            }
            case 49: 
            case 138: {
                this.pop(2);
                this.push(16777219);
                this.push(16777216);
                return;
            }
            case 50: {
                this.pop(1);
                int t1 = this.pop();
                this.push(-268435456 + t1);
                return;
            }
            case 54: 
            case 56: 
            case 58: {
                int t1 = this.pop();
                this.set(arg, t1);
                if (arg <= 0) return;
                int t2 = this.get(arg - 1);
                if (t2 != 16777220 && t2 != 16777219) {
                    if ((t2 & 251658240) == 16777216) return;
                    this.set(arg - 1, t2 | 8388608);
                    return;
                }
                this.set(arg - 1, 16777216);
                return;
            }
            case 55: 
            case 57: {
                this.pop(1);
                int t1 = this.pop();
                this.set(arg, t1);
                this.set(arg + 1, 16777216);
                if (arg <= 0) return;
                int t2 = this.get(arg - 1);
                if (t2 != 16777220 && t2 != 16777219) {
                    if ((t2 & 251658240) == 16777216) return;
                    this.set(arg - 1, t2 | 8388608);
                    return;
                }
                this.set(arg - 1, 16777216);
                return;
            }
            case 79: 
            case 81: 
            case 83: 
            case 84: 
            case 85: 
            case 86: {
                this.pop(3);
                return;
            }
            case 80: 
            case 82: {
                this.pop(4);
                return;
            }
            case 87: 
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 170: 
            case 171: 
            case 172: 
            case 174: 
            case 176: 
            case 191: 
            case 194: 
            case 195: 
            case 198: 
            case 199: {
                this.pop(1);
                return;
            }
            case 88: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: 
            case 165: 
            case 166: 
            case 173: 
            case 175: {
                this.pop(2);
                return;
            }
            case 89: {
                int t1 = this.pop();
                this.push(t1);
                this.push(t1);
                return;
            }
            case 90: {
                int t1 = this.pop();
                int t2 = this.pop();
                this.push(t1);
                this.push(t2);
                this.push(t1);
                return;
            }
            case 91: {
                int t1 = this.pop();
                int t2 = this.pop();
                int t3 = this.pop();
                this.push(t1);
                this.push(t3);
                this.push(t2);
                this.push(t1);
                return;
            }
            case 92: {
                int t1 = this.pop();
                int t2 = this.pop();
                this.push(t2);
                this.push(t1);
                this.push(t2);
                this.push(t1);
                return;
            }
            case 93: {
                int t1 = this.pop();
                int t2 = this.pop();
                int t3 = this.pop();
                this.push(t2);
                this.push(t1);
                this.push(t3);
                this.push(t2);
                this.push(t1);
                return;
            }
            case 94: {
                int t1 = this.pop();
                int t2 = this.pop();
                int t3 = this.pop();
                int t4 = this.pop();
                this.push(t2);
                this.push(t1);
                this.push(t4);
                this.push(t3);
                this.push(t2);
                this.push(t1);
                return;
            }
            case 95: {
                int t1 = this.pop();
                int t2 = this.pop();
                this.push(t1);
                this.push(t2);
                return;
            }
            case 96: 
            case 100: 
            case 104: 
            case 108: 
            case 112: 
            case 120: 
            case 122: 
            case 124: 
            case 126: 
            case 128: 
            case 130: 
            case 136: 
            case 142: 
            case 149: 
            case 150: {
                this.pop(2);
                this.push(16777217);
                return;
            }
            case 97: 
            case 101: 
            case 105: 
            case 109: 
            case 113: 
            case 127: 
            case 129: 
            case 131: {
                this.pop(4);
                this.push(16777220);
                this.push(16777216);
                return;
            }
            case 98: 
            case 102: 
            case 106: 
            case 110: 
            case 114: 
            case 137: 
            case 144: {
                this.pop(2);
                this.push(16777218);
                return;
            }
            case 99: 
            case 103: 
            case 107: 
            case 111: 
            case 115: {
                this.pop(4);
                this.push(16777219);
                this.push(16777216);
                return;
            }
            case 121: 
            case 123: 
            case 125: {
                this.pop(3);
                this.push(16777220);
                this.push(16777216);
                return;
            }
            case 132: {
                this.set(arg, 16777217);
                return;
            }
            case 133: 
            case 140: {
                this.pop(1);
                this.push(16777220);
                this.push(16777216);
                return;
            }
            case 134: {
                this.pop(1);
                this.push(16777218);
                return;
            }
            case 135: 
            case 141: {
                this.pop(1);
                this.push(16777219);
                this.push(16777216);
                return;
            }
            case 139: 
            case 190: 
            case 193: {
                this.pop(1);
                this.push(16777217);
                return;
            }
            case 148: 
            case 151: 
            case 152: {
                this.pop(4);
                this.push(16777217);
                return;
            }
            case 168: 
            case 169: {
                throw new RuntimeException("JSR/RET are not supported with computeFrames option");
            }
            case 178: {
                this.push(cw, item.strVal3);
                return;
            }
            case 179: {
                this.pop(item.strVal3);
                return;
            }
            case 180: {
                this.pop(1);
                this.push(cw, item.strVal3);
                return;
            }
            case 181: {
                this.pop(item.strVal3);
                this.pop();
                return;
            }
            case 182: 
            case 183: 
            case 184: 
            case 185: {
                this.pop(item.strVal3);
                if (opcode != 184) {
                    int t1 = this.pop();
                    if (opcode == 183 && item.strVal2.charAt(0) == '<') {
                        this.init(t1);
                    }
                }
                this.push(cw, item.strVal3);
                return;
            }
            case 186: {
                this.pop(item.strVal2);
                this.push(cw, item.strVal2);
                return;
            }
            case 187: {
                this.push(25165824 | cw.addUninitializedType(item.strVal1, arg));
                return;
            }
            case 188: {
                this.pop();
                switch (arg) {
                    case 4: {
                        this.push(285212681);
                        return;
                    }
                    case 5: {
                        this.push(285212683);
                        return;
                    }
                    case 8: {
                        this.push(285212682);
                        return;
                    }
                    case 9: {
                        this.push(285212684);
                        return;
                    }
                    case 10: {
                        this.push(285212673);
                        return;
                    }
                    case 6: {
                        this.push(285212674);
                        return;
                    }
                    case 7: {
                        this.push(285212675);
                        return;
                    }
                }
                this.push(285212676);
                return;
            }
            case 189: {
                String s = item.strVal1;
                this.pop();
                if (s.charAt(0) == '[') {
                    this.push(cw, '[' + s);
                    return;
                }
                this.push(292552704 | cw.addType(s));
                return;
            }
            case 192: {
                String s = item.strVal1;
                this.pop();
                if (s.charAt(0) == '[') {
                    this.push(cw, s);
                    return;
                }
                this.push(24117248 | cw.addType(s));
                return;
            }
        }
        this.pop(arg);
        this.push(cw, item.strVal1);
    }

    final boolean merge(ClassWriter cw, Frame frame, int edge) {
        int t;
        int dim;
        int kind;
        int i;
        int s;
        boolean changed = false;
        int nLocal = this.inputLocals.length;
        int nStack = this.inputStack.length;
        if (frame.inputLocals == null) {
            frame.inputLocals = new int[nLocal];
            changed = true;
        }
        for (i = 0; i < nLocal; changed |= Frame.merge((ClassWriter)cw, (int)t, (int[])frame.inputLocals, (int)i), ++i) {
            if (this.outputLocals != null && i < this.outputLocals.length) {
                s = this.outputLocals[i];
                if (s == 0) {
                    t = this.inputLocals[i];
                } else {
                    dim = s & -268435456;
                    kind = s & 251658240;
                    if (kind == 16777216) {
                        t = s;
                    } else {
                        t = kind == 33554432 ? dim + this.inputLocals[s & 8388607] : dim + this.inputStack[nStack - (s & 8388607)];
                        if ((s & 8388608) != 0 && (t == 16777220 || t == 16777219)) {
                            t = 16777216;
                        }
                    }
                }
            } else {
                t = this.inputLocals[i];
            }
            if (this.initializations == null) continue;
            t = this.init(cw, t);
        }
        if (edge > 0) {
            i = 0;
            do {
                if (i >= nLocal) {
                    if (frame.inputStack != null) return changed |= Frame.merge(cw, edge, frame.inputStack, 0);
                    frame.inputStack = new int[1];
                    return changed |= Frame.merge(cw, edge, frame.inputStack, 0);
                }
                t = this.inputLocals[i];
                changed |= Frame.merge(cw, t, frame.inputLocals, i);
                ++i;
            } while (true);
        }
        int nInputStack = this.inputStack.length + this.owner.inputStackTop;
        if (frame.inputStack == null) {
            frame.inputStack = new int[nInputStack + this.outputStackTop];
            changed = true;
        }
        for (i = 0; i < nInputStack; changed |= Frame.merge((ClassWriter)cw, (int)t, (int[])frame.inputStack, (int)i), ++i) {
            t = this.inputStack[i];
            if (this.initializations == null) continue;
            t = this.init(cw, t);
        }
        i = 0;
        while (i < this.outputStackTop) {
            s = this.outputStack[i];
            dim = s & -268435456;
            kind = s & 251658240;
            if (kind == 16777216) {
                t = s;
            } else {
                t = kind == 33554432 ? dim + this.inputLocals[s & 8388607] : dim + this.inputStack[nStack - (s & 8388607)];
                if ((s & 8388608) != 0 && (t == 16777220 || t == 16777219)) {
                    t = 16777216;
                }
            }
            if (this.initializations != null) {
                t = this.init(cw, t);
            }
            changed |= Frame.merge(cw, t, frame.inputStack, nInputStack + i);
            ++i;
        }
        return changed;
    }

    private static boolean merge(ClassWriter cw, int t, int[] types, int index) {
        int v;
        int u = types[index];
        if (u == t) {
            return false;
        }
        if ((t & 268435455) == 16777221) {
            if (u == 16777221) {
                return false;
            }
            t = 16777221;
        }
        if (u == 0) {
            types[index] = t;
            return true;
        }
        if ((u & 267386880) == 24117248 || (u & -268435456) != 0) {
            if (t == 16777221) {
                return false;
            }
            if ((t & -1048576) == (u & -1048576)) {
                if ((u & 267386880) == 24117248) {
                    v = t & -268435456 | 24117248 | cw.getMergedType(t & 1048575, u & 1048575);
                } else {
                    int vdim = -268435456 + (u & -268435456);
                    v = vdim | 24117248 | cw.addType("java/lang/Object");
                }
            } else if ((t & 267386880) == 24117248 || (t & -268435456) != 0) {
                int tdim = ((t & -268435456) == 0 || (t & 267386880) == 24117248 ? 0 : -268435456) + (t & -268435456);
                int udim = ((u & -268435456) == 0 || (u & 267386880) == 24117248 ? 0 : -268435456) + (u & -268435456);
                v = Math.min(tdim, udim) | 24117248 | cw.addType("java/lang/Object");
            } else {
                v = 16777216;
            }
        } else {
            v = u == 16777221 ? ((t & 267386880) == 24117248 || (t & -268435456) != 0 ? t : 16777216) : 16777216;
        }
        if (u == v) return false;
        types[index] = v;
        return true;
    }

    static {
        int[] b = new int[202];
        String s = "EFFFFFFFFGGFFFGGFFFEEFGFGFEEEEEEEEEEEEEEEEEEEEDEDEDDDDDCDCDEEEEEEEEEEEEEEEEEEEEBABABBBBDCFFFGGGEDCDCDCDCDCDCDCDCDCDCEEEEDDDDDDDCDCDCEFEFDDEEFFDEDEEEBDDBBDDDDDDCCCCCCCCEFEDDDCDCDEEEEEEEEEEFEEEEEEDDEEDDEE";
        int i = 0;
        do {
            if (i >= b.length) {
                SIZE = b;
                return;
            }
            b[i] = s.charAt(i) - 69;
            ++i;
        } while (true);
    }
}

