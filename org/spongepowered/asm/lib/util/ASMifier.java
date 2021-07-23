/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.lib.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.spongepowered.asm.lib.Attribute;
import org.spongepowered.asm.lib.ClassReader;
import org.spongepowered.asm.lib.ClassVisitor;
import org.spongepowered.asm.lib.Handle;
import org.spongepowered.asm.lib.Label;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.TypePath;
import org.spongepowered.asm.lib.util.ASMifiable;
import org.spongepowered.asm.lib.util.Printer;
import org.spongepowered.asm.lib.util.TraceClassVisitor;

public class ASMifier
extends Printer {
    protected final String name;
    protected final int id;
    protected Map<Label, String> labelNames;
    private static final int ACCESS_CLASS = 262144;
    private static final int ACCESS_FIELD = 524288;
    private static final int ACCESS_INNER = 1048576;

    public ASMifier() {
        this(327680, "cw", 0);
        if (this.getClass() == ASMifier.class) return;
        throw new IllegalStateException();
    }

    protected ASMifier(int api, String name, int id) {
        super(api);
        this.name = name;
        this.id = id;
    }

    public static void main(String[] args) throws Exception {
        int i = 0;
        int flags = 2;
        boolean ok = true;
        if (args.length < 1 || args.length > 2) {
            ok = false;
        }
        if (ok && "-debug".equals(args[0])) {
            i = 1;
            flags = 0;
            if (args.length != 2) {
                ok = false;
            }
        }
        if (!ok) {
            System.err.println("Prints the ASM code to generate the given class.");
            System.err.println("Usage: ASMifier [-debug] <fully qualified class name or class file name>");
            return;
        }
        ClassReader cr = args[i].endsWith(".class") || args[i].indexOf(92) > -1 || args[i].indexOf(47) > -1 ? new ClassReader(new FileInputStream(args[i])) : new ClassReader(args[i]);
        cr.accept(new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out)), flags);
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        n = name.lastIndexOf(47);
        if (n == -1) {
            simpleName = name;
        } else {
            this.text.add("package asm." + name.substring(0, n).replace('/', '.') + ";\n");
            simpleName = name.substring(n + 1);
        }
        this.text.add("import java.util.*;\n");
        this.text.add("import org.objectweb.asm.*;\n");
        this.text.add("public class " + simpleName + "Dump implements Opcodes {\n\n");
        this.text.add("public static byte[] dump () throws Exception {\n\n");
        this.text.add("ClassWriter cw = new ClassWriter(0);\n");
        this.text.add("FieldVisitor fv;\n");
        this.text.add("MethodVisitor mv;\n");
        this.text.add("AnnotationVisitor av0;\n\n");
        this.buf.setLength(0);
        this.buf.append("cw.visit(");
        switch (version) {
            case 196653: {
                this.buf.append("V1_1");
                ** break;
            }
            case 46: {
                this.buf.append("V1_2");
                ** break;
            }
            case 47: {
                this.buf.append("V1_3");
                ** break;
            }
            case 48: {
                this.buf.append("V1_4");
                ** break;
            }
            case 49: {
                this.buf.append("V1_5");
                ** break;
            }
            case 50: {
                this.buf.append("V1_6");
                ** break;
            }
            case 51: {
                this.buf.append("V1_7");
                ** break;
            }
        }
        this.buf.append(version);
lbl58: // 8 sources:
        this.buf.append(", ");
        this.appendAccess(access | 262144);
        this.buf.append(", ");
        this.appendConstant(name);
        this.buf.append(", ");
        this.appendConstant(signature);
        this.buf.append(", ");
        this.appendConstant(superName);
        this.buf.append(", ");
        if (interfaces == null || interfaces.length <= 0) {
            this.buf.append("null");
        } else {
            this.buf.append("new String[] {");
            for (i = 0; i < interfaces.length; ++i) {
                this.buf.append(i == 0 ? " " : ", ");
                this.appendConstant(interfaces[i]);
            }
            this.buf.append(" }");
        }
        this.buf.append(");\n\n");
        this.text.add(this.buf.toString());
    }

    @Override
    public void visitSource(String file, String debug) {
        this.buf.setLength(0);
        this.buf.append("cw.visitSource(");
        this.appendConstant(file);
        this.buf.append(", ");
        this.appendConstant(debug);
        this.buf.append(");\n\n");
        this.text.add(this.buf.toString());
    }

    @Override
    public void visitOuterClass(String owner, String name, String desc) {
        this.buf.setLength(0);
        this.buf.append("cw.visitOuterClass(");
        this.appendConstant(owner);
        this.buf.append(", ");
        this.appendConstant(name);
        this.buf.append(", ");
        this.appendConstant(desc);
        this.buf.append(");\n\n");
        this.text.add(this.buf.toString());
    }

    @Override
    public ASMifier visitClassAnnotation(String desc, boolean visible) {
        return this.visitAnnotation(desc, visible);
    }

    @Override
    public ASMifier visitClassTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        return this.visitTypeAnnotation(typeRef, typePath, desc, visible);
    }

    @Override
    public void visitClassAttribute(Attribute attr) {
        this.visitAttribute(attr);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        this.buf.setLength(0);
        this.buf.append("cw.visitInnerClass(");
        this.appendConstant(name);
        this.buf.append(", ");
        this.appendConstant(outerName);
        this.buf.append(", ");
        this.appendConstant(innerName);
        this.buf.append(", ");
        this.appendAccess(access | 1048576);
        this.buf.append(");\n\n");
        this.text.add(this.buf.toString());
    }

    @Override
    public ASMifier visitField(int access, String name, String desc, String signature, Object value) {
        this.buf.setLength(0);
        this.buf.append("{\n");
        this.buf.append("fv = cw.visitField(");
        this.appendAccess(access | 524288);
        this.buf.append(", ");
        this.appendConstant(name);
        this.buf.append(", ");
        this.appendConstant(desc);
        this.buf.append(", ");
        this.appendConstant(signature);
        this.buf.append(", ");
        this.appendConstant(value);
        this.buf.append(");\n");
        this.text.add(this.buf.toString());
        ASMifier a = this.createASMifier("fv", 0);
        this.text.add(a.getText());
        this.text.add("}\n");
        return a;
    }

    @Override
    public ASMifier visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        this.buf.setLength(0);
        this.buf.append("{\n");
        this.buf.append("mv = cw.visitMethod(");
        this.appendAccess(access);
        this.buf.append(", ");
        this.appendConstant(name);
        this.buf.append(", ");
        this.appendConstant(desc);
        this.buf.append(", ");
        this.appendConstant(signature);
        this.buf.append(", ");
        if (exceptions == null || exceptions.length <= 0) {
            this.buf.append("null");
        } else {
            this.buf.append("new String[] {");
            for (int i = 0; i < exceptions.length; ++i) {
                this.buf.append(i == 0 ? " " : ", ");
                this.appendConstant(exceptions[i]);
            }
            this.buf.append(" }");
        }
        this.buf.append(");\n");
        this.text.add(this.buf.toString());
        ASMifier a = this.createASMifier("mv", 0);
        this.text.add(a.getText());
        this.text.add("}\n");
        return a;
    }

    @Override
    public void visitClassEnd() {
        this.text.add("cw.visitEnd();\n\n");
        this.text.add("return cw.toByteArray();\n");
        this.text.add("}\n");
        this.text.add("}\n");
    }

    @Override
    public void visit(String name, Object value) {
        this.buf.setLength(0);
        this.buf.append("av").append(this.id).append(".visit(");
        ASMifier.appendConstant(this.buf, name);
        this.buf.append(", ");
        ASMifier.appendConstant(this.buf, value);
        this.buf.append(");\n");
        this.text.add(this.buf.toString());
    }

    @Override
    public void visitEnum(String name, String desc, String value) {
        this.buf.setLength(0);
        this.buf.append("av").append(this.id).append(".visitEnum(");
        ASMifier.appendConstant(this.buf, name);
        this.buf.append(", ");
        ASMifier.appendConstant(this.buf, desc);
        this.buf.append(", ");
        ASMifier.appendConstant(this.buf, value);
        this.buf.append(");\n");
        this.text.add(this.buf.toString());
    }

    @Override
    public ASMifier visitAnnotation(String name, String desc) {
        this.buf.setLength(0);
        this.buf.append("{\n");
        this.buf.append("AnnotationVisitor av").append(this.id + 1).append(" = av");
        this.buf.append(this.id).append(".visitAnnotation(");
        ASMifier.appendConstant(this.buf, name);
        this.buf.append(", ");
        ASMifier.appendConstant(this.buf, desc);
        this.buf.append(");\n");
        this.text.add(this.buf.toString());
        ASMifier a = this.createASMifier("av", this.id + 1);
        this.text.add(a.getText());
        this.text.add("}\n");
        return a;
    }

    @Override
    public ASMifier visitArray(String name) {
        this.buf.setLength(0);
        this.buf.append("{\n");
        this.buf.append("AnnotationVisitor av").append(this.id + 1).append(" = av");
        this.buf.append(this.id).append(".visitArray(");
        ASMifier.appendConstant(this.buf, name);
        this.buf.append(");\n");
        this.text.add(this.buf.toString());
        ASMifier a = this.createASMifier("av", this.id + 1);
        this.text.add(a.getText());
        this.text.add("}\n");
        return a;
    }

    @Override
    public void visitAnnotationEnd() {
        this.buf.setLength(0);
        this.buf.append("av").append(this.id).append(".visitEnd();\n");
        this.text.add(this.buf.toString());
    }

    @Override
    public ASMifier visitFieldAnnotation(String desc, boolean visible) {
        return this.visitAnnotation(desc, visible);
    }

    @Override
    public ASMifier visitFieldTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        return this.visitTypeAnnotation(typeRef, typePath, desc, visible);
    }

    @Override
    public void visitFieldAttribute(Attribute attr) {
        this.visitAttribute(attr);
    }

    @Override
    public void visitFieldEnd() {
        this.buf.setLength(0);
        this.buf.append(this.name).append(".visitEnd();\n");
        this.text.add(this.buf.toString());
    }

    @Override
    public void visitParameter(String parameterName, int access) {
        this.buf.setLength(0);
        this.buf.append(this.name).append(".visitParameter(");
        ASMifier.appendString(this.buf, parameterName);
        this.buf.append(", ");
        this.appendAccess(access);
        this.text.add(this.buf.append(");\n").toString());
    }

    @Override
    public ASMifier visitAnnotationDefault() {
        this.buf.setLength(0);
        this.buf.append("{\n").append("av0 = ").append(this.name).append(".visitAnnotationDefault();\n");
        this.text.add(this.buf.toString());
        ASMifier a = this.createASMifier("av", 0);
        this.text.add(a.getText());
        this.text.add("}\n");
        return a;
    }

    @Override
    public ASMifier visitMethodAnnotation(String desc, boolean visible) {
        return this.visitAnnotation(desc, visible);
    }

    @Override
    public ASMifier visitMethodTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        return this.visitTypeAnnotation(typeRef, typePath, desc, visible);
    }

    @Override
    public ASMifier visitParameterAnnotation(int parameter, String desc, boolean visible) {
        this.buf.setLength(0);
        this.buf.append("{\n").append("av0 = ").append(this.name).append(".visitParameterAnnotation(").append(parameter).append(", ");
        this.appendConstant(desc);
        this.buf.append(", ").append(visible).append(");\n");
        this.text.add(this.buf.toString());
        ASMifier a = this.createASMifier("av", 0);
        this.text.add(a.getText());
        this.text.add("}\n");
        return a;
    }

    @Override
    public void visitMethodAttribute(Attribute attr) {
        this.visitAttribute(attr);
    }

    @Override
    public void visitCode() {
        this.text.add(this.name + ".visitCode();\n");
    }

    @Override
    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
        this.buf.setLength(0);
        switch (type) {
            case -1: 
            case 0: {
                this.declareFrameTypes(nLocal, local);
                this.declareFrameTypes(nStack, stack);
                if (type == -1) {
                    this.buf.append(this.name).append(".visitFrame(Opcodes.F_NEW, ");
                } else {
                    this.buf.append(this.name).append(".visitFrame(Opcodes.F_FULL, ");
                }
                this.buf.append(nLocal).append(", new Object[] {");
                this.appendFrameTypes(nLocal, local);
                this.buf.append("}, ").append(nStack).append(", new Object[] {");
                this.appendFrameTypes(nStack, stack);
                this.buf.append('}');
                break;
            }
            case 1: {
                this.declareFrameTypes(nLocal, local);
                this.buf.append(this.name).append(".visitFrame(Opcodes.F_APPEND,").append(nLocal).append(", new Object[] {");
                this.appendFrameTypes(nLocal, local);
                this.buf.append("}, 0, null");
                break;
            }
            case 2: {
                this.buf.append(this.name).append(".visitFrame(Opcodes.F_CHOP,").append(nLocal).append(", null, 0, null");
                break;
            }
            case 3: {
                this.buf.append(this.name).append(".visitFrame(Opcodes.F_SAME, 0, null, 0, null");
                break;
            }
            case 4: {
                this.declareFrameTypes(1, stack);
                this.buf.append(this.name).append(".visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {");
                this.appendFrameTypes(1, stack);
                this.buf.append('}');
            }
        }
        this.buf.append(");\n");
        this.text.add(this.buf.toString());
    }

    @Override
    public void visitInsn(int opcode) {
        this.buf.setLength(0);
        this.buf.append(this.name).append(".visitInsn(").append(OPCODES[opcode]).append(");\n");
        this.text.add(this.buf.toString());
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        this.buf.setLength(0);
        this.buf.append(this.name).append(".visitIntInsn(").append(OPCODES[opcode]).append(", ").append(opcode == 188 ? TYPES[operand] : Integer.toString(operand)).append(");\n");
        this.text.add(this.buf.toString());
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        this.buf.setLength(0);
        this.buf.append(this.name).append(".visitVarInsn(").append(OPCODES[opcode]).append(", ").append(var).append(");\n");
        this.text.add(this.buf.toString());
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        this.buf.setLength(0);
        this.buf.append(this.name).append(".visitTypeInsn(").append(OPCODES[opcode]).append(", ");
        this.appendConstant(type);
        this.buf.append(");\n");
        this.text.add(this.buf.toString());
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        this.buf.setLength(0);
        this.buf.append(this.name).append(".visitFieldInsn(").append(OPCODES[opcode]).append(", ");
        this.appendConstant(owner);
        this.buf.append(", ");
        this.appendConstant(name);
        this.buf.append(", ");
        this.appendConstant(desc);
        this.buf.append(");\n");
        this.text.add(this.buf.toString());
    }

    @Deprecated
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        if (this.api >= 327680) {
            super.visitMethodInsn(opcode, owner, name, desc);
            return;
        }
        this.doVisitMethodInsn(opcode, owner, name, desc, opcode == 185);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        if (this.api < 327680) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
            return;
        }
        this.doVisitMethodInsn(opcode, owner, name, desc, itf);
    }

    private void doVisitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        this.buf.setLength(0);
        this.buf.append(this.name).append(".visitMethodInsn(").append(OPCODES[opcode]).append(", ");
        this.appendConstant(owner);
        this.buf.append(", ");
        this.appendConstant(name);
        this.buf.append(", ");
        this.appendConstant(desc);
        this.buf.append(", ");
        this.buf.append(itf ? "true" : "false");
        this.buf.append(");\n");
        this.text.add(this.buf.toString());
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object ... bsmArgs) {
        this.buf.setLength(0);
        this.buf.append(this.name).append(".visitInvokeDynamicInsn(");
        this.appendConstant(name);
        this.buf.append(", ");
        this.appendConstant(desc);
        this.buf.append(", ");
        this.appendConstant(bsm);
        this.buf.append(", new Object[]{");
        int i = 0;
        do {
            if (i >= bsmArgs.length) {
                this.buf.append("});\n");
                this.text.add(this.buf.toString());
                return;
            }
            this.appendConstant(bsmArgs[i]);
            if (i != bsmArgs.length - 1) {
                this.buf.append(", ");
            }
            ++i;
        } while (true);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        this.buf.setLength(0);
        this.declareLabel(label);
        this.buf.append(this.name).append(".visitJumpInsn(").append(OPCODES[opcode]).append(", ");
        this.appendLabel(label);
        this.buf.append(");\n");
        this.text.add(this.buf.toString());
    }

    @Override
    public void visitLabel(Label label) {
        this.buf.setLength(0);
        this.declareLabel(label);
        this.buf.append(this.name).append(".visitLabel(");
        this.appendLabel(label);
        this.buf.append(");\n");
        this.text.add(this.buf.toString());
    }

    @Override
    public void visitLdcInsn(Object cst) {
        this.buf.setLength(0);
        this.buf.append(this.name).append(".visitLdcInsn(");
        this.appendConstant(cst);
        this.buf.append(");\n");
        this.text.add(this.buf.toString());
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        this.buf.setLength(0);
        this.buf.append(this.name).append(".visitIincInsn(").append(var).append(", ").append(increment).append(");\n");
        this.text.add(this.buf.toString());
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label ... labels) {
        int i;
        this.buf.setLength(0);
        for (i = 0; i < labels.length; ++i) {
            this.declareLabel(labels[i]);
        }
        this.declareLabel(dflt);
        this.buf.append(this.name).append(".visitTableSwitchInsn(").append(min).append(", ").append(max).append(", ");
        this.appendLabel(dflt);
        this.buf.append(", new Label[] {");
        i = 0;
        do {
            if (i >= labels.length) {
                this.buf.append(" });\n");
                this.text.add(this.buf.toString());
                return;
            }
            this.buf.append(i == 0 ? " " : ", ");
            this.appendLabel(labels[i]);
            ++i;
        } while (true);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        int i;
        this.buf.setLength(0);
        for (i = 0; i < labels.length; ++i) {
            this.declareLabel(labels[i]);
        }
        this.declareLabel(dflt);
        this.buf.append(this.name).append(".visitLookupSwitchInsn(");
        this.appendLabel(dflt);
        this.buf.append(", new int[] {");
        for (i = 0; i < keys.length; ++i) {
            this.buf.append(i == 0 ? " " : ", ").append(keys[i]);
        }
        this.buf.append(" }, new Label[] {");
        i = 0;
        do {
            if (i >= labels.length) {
                this.buf.append(" });\n");
                this.text.add(this.buf.toString());
                return;
            }
            this.buf.append(i == 0 ? " " : ", ");
            this.appendLabel(labels[i]);
            ++i;
        } while (true);
    }

    @Override
    public void visitMultiANewArrayInsn(String desc, int dims) {
        this.buf.setLength(0);
        this.buf.append(this.name).append(".visitMultiANewArrayInsn(");
        this.appendConstant(desc);
        this.buf.append(", ").append(dims).append(");\n");
        this.text.add(this.buf.toString());
    }

    @Override
    public ASMifier visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        return this.visitTypeAnnotation("visitInsnAnnotation", typeRef, typePath, desc, visible);
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        this.buf.setLength(0);
        this.declareLabel(start);
        this.declareLabel(end);
        this.declareLabel(handler);
        this.buf.append(this.name).append(".visitTryCatchBlock(");
        this.appendLabel(start);
        this.buf.append(", ");
        this.appendLabel(end);
        this.buf.append(", ");
        this.appendLabel(handler);
        this.buf.append(", ");
        this.appendConstant(type);
        this.buf.append(");\n");
        this.text.add(this.buf.toString());
    }

    @Override
    public ASMifier visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        return this.visitTypeAnnotation("visitTryCatchAnnotation", typeRef, typePath, desc, visible);
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        this.buf.setLength(0);
        this.buf.append(this.name).append(".visitLocalVariable(");
        this.appendConstant(name);
        this.buf.append(", ");
        this.appendConstant(desc);
        this.buf.append(", ");
        this.appendConstant(signature);
        this.buf.append(", ");
        this.appendLabel(start);
        this.buf.append(", ");
        this.appendLabel(end);
        this.buf.append(", ").append(index).append(");\n");
        this.text.add(this.buf.toString());
    }

    @Override
    public Printer visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible) {
        int i;
        this.buf.setLength(0);
        this.buf.append("{\n").append("av0 = ").append(this.name).append(".visitLocalVariableAnnotation(");
        this.buf.append(typeRef);
        if (typePath == null) {
            this.buf.append(", null, ");
        } else {
            this.buf.append(", TypePath.fromString(\"").append(typePath).append("\"), ");
        }
        this.buf.append("new Label[] {");
        for (i = 0; i < start.length; ++i) {
            this.buf.append(i == 0 ? " " : ", ");
            this.appendLabel(start[i]);
        }
        this.buf.append(" }, new Label[] {");
        for (i = 0; i < end.length; ++i) {
            this.buf.append(i == 0 ? " " : ", ");
            this.appendLabel(end[i]);
        }
        this.buf.append(" }, new int[] {");
        i = 0;
        do {
            if (i >= index.length) {
                this.buf.append(" }, ");
                this.appendConstant(desc);
                this.buf.append(", ").append(visible).append(");\n");
                this.text.add(this.buf.toString());
                ASMifier a = this.createASMifier("av", 0);
                this.text.add(a.getText());
                this.text.add("}\n");
                return a;
            }
            this.buf.append(i == 0 ? " " : ", ").append(index[i]);
            ++i;
        } while (true);
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        this.buf.setLength(0);
        this.buf.append(this.name).append(".visitLineNumber(").append(line).append(", ");
        this.appendLabel(start);
        this.buf.append(");\n");
        this.text.add(this.buf.toString());
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        this.buf.setLength(0);
        this.buf.append(this.name).append(".visitMaxs(").append(maxStack).append(", ").append(maxLocals).append(");\n");
        this.text.add(this.buf.toString());
    }

    @Override
    public void visitMethodEnd() {
        this.buf.setLength(0);
        this.buf.append(this.name).append(".visitEnd();\n");
        this.text.add(this.buf.toString());
    }

    public ASMifier visitAnnotation(String desc, boolean visible) {
        this.buf.setLength(0);
        this.buf.append("{\n").append("av0 = ").append(this.name).append(".visitAnnotation(");
        this.appendConstant(desc);
        this.buf.append(", ").append(visible).append(");\n");
        this.text.add(this.buf.toString());
        ASMifier a = this.createASMifier("av", 0);
        this.text.add(a.getText());
        this.text.add("}\n");
        return a;
    }

    public ASMifier visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        return this.visitTypeAnnotation("visitTypeAnnotation", typeRef, typePath, desc, visible);
    }

    public ASMifier visitTypeAnnotation(String method, int typeRef, TypePath typePath, String desc, boolean visible) {
        this.buf.setLength(0);
        this.buf.append("{\n").append("av0 = ").append(this.name).append(".").append(method).append("(");
        this.buf.append(typeRef);
        if (typePath == null) {
            this.buf.append(", null, ");
        } else {
            this.buf.append(", TypePath.fromString(\"").append(typePath).append("\"), ");
        }
        this.appendConstant(desc);
        this.buf.append(", ").append(visible).append(");\n");
        this.text.add(this.buf.toString());
        ASMifier a = this.createASMifier("av", 0);
        this.text.add(a.getText());
        this.text.add("}\n");
        return a;
    }

    public void visitAttribute(Attribute attr) {
        this.buf.setLength(0);
        this.buf.append("// ATTRIBUTE ").append(attr.type).append('\n');
        if (attr instanceof ASMifiable) {
            if (this.labelNames == null) {
                this.labelNames = new HashMap<Label, String>();
            }
            this.buf.append("{\n");
            ((ASMifiable)((Object)attr)).asmify(this.buf, "attr", this.labelNames);
            this.buf.append(this.name).append(".visitAttribute(attr);\n");
            this.buf.append("}\n");
        }
        this.text.add(this.buf.toString());
    }

    protected ASMifier createASMifier(String name, int id) {
        return new ASMifier(327680, name, id);
    }

    void appendAccess(int access) {
        boolean first = true;
        if ((access & 1) != 0) {
            this.buf.append("ACC_PUBLIC");
            first = false;
        }
        if ((access & 2) != 0) {
            this.buf.append("ACC_PRIVATE");
            first = false;
        }
        if ((access & 4) != 0) {
            this.buf.append("ACC_PROTECTED");
            first = false;
        }
        if ((access & 16) != 0) {
            if (!first) {
                this.buf.append(" + ");
            }
            this.buf.append("ACC_FINAL");
            first = false;
        }
        if ((access & 8) != 0) {
            if (!first) {
                this.buf.append(" + ");
            }
            this.buf.append("ACC_STATIC");
            first = false;
        }
        if ((access & 32) != 0) {
            if (!first) {
                this.buf.append(" + ");
            }
            if ((access & 262144) == 0) {
                this.buf.append("ACC_SYNCHRONIZED");
            } else {
                this.buf.append("ACC_SUPER");
            }
            first = false;
        }
        if ((access & 64) != 0 && (access & 524288) != 0) {
            if (!first) {
                this.buf.append(" + ");
            }
            this.buf.append("ACC_VOLATILE");
            first = false;
        }
        if ((access & 64) != 0 && (access & 262144) == 0 && (access & 524288) == 0) {
            if (!first) {
                this.buf.append(" + ");
            }
            this.buf.append("ACC_BRIDGE");
            first = false;
        }
        if ((access & 128) != 0 && (access & 262144) == 0 && (access & 524288) == 0) {
            if (!first) {
                this.buf.append(" + ");
            }
            this.buf.append("ACC_VARARGS");
            first = false;
        }
        if ((access & 128) != 0 && (access & 524288) != 0) {
            if (!first) {
                this.buf.append(" + ");
            }
            this.buf.append("ACC_TRANSIENT");
            first = false;
        }
        if ((access & 256) != 0 && (access & 262144) == 0 && (access & 524288) == 0) {
            if (!first) {
                this.buf.append(" + ");
            }
            this.buf.append("ACC_NATIVE");
            first = false;
        }
        if ((access & 16384) != 0 && ((access & 262144) != 0 || (access & 524288) != 0 || (access & 1048576) != 0)) {
            if (!first) {
                this.buf.append(" + ");
            }
            this.buf.append("ACC_ENUM");
            first = false;
        }
        if ((access & 8192) != 0 && ((access & 262144) != 0 || (access & 1048576) != 0)) {
            if (!first) {
                this.buf.append(" + ");
            }
            this.buf.append("ACC_ANNOTATION");
            first = false;
        }
        if ((access & 1024) != 0) {
            if (!first) {
                this.buf.append(" + ");
            }
            this.buf.append("ACC_ABSTRACT");
            first = false;
        }
        if ((access & 512) != 0) {
            if (!first) {
                this.buf.append(" + ");
            }
            this.buf.append("ACC_INTERFACE");
            first = false;
        }
        if ((access & 2048) != 0) {
            if (!first) {
                this.buf.append(" + ");
            }
            this.buf.append("ACC_STRICT");
            first = false;
        }
        if ((access & 4096) != 0) {
            if (!first) {
                this.buf.append(" + ");
            }
            this.buf.append("ACC_SYNTHETIC");
            first = false;
        }
        if ((access & 131072) != 0) {
            if (!first) {
                this.buf.append(" + ");
            }
            this.buf.append("ACC_DEPRECATED");
            first = false;
        }
        if ((access & 32768) != 0) {
            if (!first) {
                this.buf.append(" + ");
            }
            this.buf.append("ACC_MANDATED");
            first = false;
        }
        if (!first) return;
        this.buf.append('0');
    }

    protected void appendConstant(Object cst) {
        ASMifier.appendConstant(this.buf, cst);
    }

    static void appendConstant(StringBuffer buf, Object cst) {
        block35 : {
            block34 : {
                block33 : {
                    block32 : {
                        block31 : {
                            block30 : {
                                block29 : {
                                    block28 : {
                                        if (cst == null) {
                                            buf.append("null");
                                            return;
                                        }
                                        if (cst instanceof String) {
                                            ASMifier.appendString(buf, (String)cst);
                                            return;
                                        }
                                        if (cst instanceof Type) {
                                            buf.append("Type.getType(\"");
                                            buf.append(((Type)cst).getDescriptor());
                                            buf.append("\")");
                                            return;
                                        }
                                        if (cst instanceof Handle) {
                                            buf.append("new Handle(");
                                            Handle h = (Handle)cst;
                                            buf.append("Opcodes.").append(HANDLE_TAG[h.getTag()]).append(", \"");
                                            buf.append(h.getOwner()).append("\", \"");
                                            buf.append(h.getName()).append("\", \"");
                                            buf.append(h.getDesc()).append("\")");
                                            return;
                                        }
                                        if (cst instanceof Byte) {
                                            buf.append("new Byte((byte)").append(cst).append(')');
                                            return;
                                        }
                                        if (cst instanceof Boolean) {
                                            buf.append((Boolean)cst != false ? "Boolean.TRUE" : "Boolean.FALSE");
                                            return;
                                        }
                                        if (cst instanceof Short) {
                                            buf.append("new Short((short)").append(cst).append(')');
                                            return;
                                        }
                                        if (cst instanceof Character) {
                                            char c = ((Character)cst).charValue();
                                            buf.append("new Character((char)").append((int)c).append(')');
                                            return;
                                        }
                                        if (cst instanceof Integer) {
                                            buf.append("new Integer(").append(cst).append(')');
                                            return;
                                        }
                                        if (cst instanceof Float) {
                                            buf.append("new Float(\"").append(cst).append("\")");
                                            return;
                                        }
                                        if (cst instanceof Long) {
                                            buf.append("new Long(").append(cst).append("L)");
                                            return;
                                        }
                                        if (cst instanceof Double) {
                                            buf.append("new Double(\"").append(cst).append("\")");
                                            return;
                                        }
                                        if (cst instanceof byte[]) break block28;
                                        if (cst instanceof boolean[]) break block29;
                                        if (cst instanceof short[]) break block30;
                                        if (cst instanceof char[]) break block31;
                                        if (cst instanceof int[]) break block32;
                                        if (cst instanceof long[]) break block33;
                                        if (cst instanceof float[]) break block34;
                                        break block35;
                                    }
                                    byte[] v = (byte[])cst;
                                    buf.append("new byte[] {");
                                    int i = 0;
                                    do {
                                        if (i >= v.length) {
                                            buf.append('}');
                                            return;
                                        }
                                        buf.append(i == 0 ? "" : ",").append(v[i]);
                                        ++i;
                                    } while (true);
                                }
                                boolean[] v = (boolean[])cst;
                                buf.append("new boolean[] {");
                                int i = 0;
                                do {
                                    if (i >= v.length) {
                                        buf.append('}');
                                        return;
                                    }
                                    buf.append(i == 0 ? "" : ",").append(v[i]);
                                    ++i;
                                } while (true);
                            }
                            short[] v = (short[])cst;
                            buf.append("new short[] {");
                            int i = 0;
                            do {
                                if (i >= v.length) {
                                    buf.append('}');
                                    return;
                                }
                                buf.append(i == 0 ? "" : ",").append("(short)").append(v[i]);
                                ++i;
                            } while (true);
                        }
                        char[] v = (char[])cst;
                        buf.append("new char[] {");
                        int i = 0;
                        do {
                            if (i >= v.length) {
                                buf.append('}');
                                return;
                            }
                            buf.append(i == 0 ? "" : ",").append("(char)").append((int)v[i]);
                            ++i;
                        } while (true);
                    }
                    int[] v = (int[])cst;
                    buf.append("new int[] {");
                    int i = 0;
                    do {
                        if (i >= v.length) {
                            buf.append('}');
                            return;
                        }
                        buf.append(i == 0 ? "" : ",").append(v[i]);
                        ++i;
                    } while (true);
                }
                long[] v = (long[])cst;
                buf.append("new long[] {");
                int i = 0;
                do {
                    if (i >= v.length) {
                        buf.append('}');
                        return;
                    }
                    buf.append(i == 0 ? "" : ",").append(v[i]).append('L');
                    ++i;
                } while (true);
            }
            float[] v = (float[])cst;
            buf.append("new float[] {");
            int i = 0;
            do {
                if (i >= v.length) {
                    buf.append('}');
                    return;
                }
                buf.append(i == 0 ? "" : ",").append(v[i]).append('f');
                ++i;
            } while (true);
        }
        if (!(cst instanceof double[])) return;
        double[] v = (double[])cst;
        buf.append("new double[] {");
        int i = 0;
        do {
            if (i >= v.length) {
                buf.append('}');
                return;
            }
            buf.append(i == 0 ? "" : ",").append(v[i]).append('d');
            ++i;
        } while (true);
    }

    private void declareFrameTypes(int n, Object[] o) {
        int i = 0;
        while (i < n) {
            if (o[i] instanceof Label) {
                this.declareLabel((Label)o[i]);
            }
            ++i;
        }
    }

    private void appendFrameTypes(int n, Object[] o) {
        int i = 0;
        while (i < n) {
            if (i > 0) {
                this.buf.append(", ");
            }
            if (o[i] instanceof String) {
                this.appendConstant(o[i]);
            } else if (o[i] instanceof Integer) {
                switch ((Integer)o[i]) {
                    case 0: {
                        this.buf.append("Opcodes.TOP");
                        break;
                    }
                    case 1: {
                        this.buf.append("Opcodes.INTEGER");
                        break;
                    }
                    case 2: {
                        this.buf.append("Opcodes.FLOAT");
                        break;
                    }
                    case 3: {
                        this.buf.append("Opcodes.DOUBLE");
                        break;
                    }
                    case 4: {
                        this.buf.append("Opcodes.LONG");
                        break;
                    }
                    case 5: {
                        this.buf.append("Opcodes.NULL");
                        break;
                    }
                    case 6: {
                        this.buf.append("Opcodes.UNINITIALIZED_THIS");
                    }
                }
            } else {
                this.appendLabel((Label)o[i]);
            }
            ++i;
        }
    }

    protected void declareLabel(Label l) {
        String name;
        if (this.labelNames == null) {
            this.labelNames = new HashMap<Label, String>();
        }
        if ((name = this.labelNames.get(l)) != null) return;
        name = "l" + this.labelNames.size();
        this.labelNames.put(l, name);
        this.buf.append("Label ").append(name).append(" = new Label();\n");
    }

    protected void appendLabel(Label l) {
        this.buf.append(this.labelNames.get(l));
    }
}

