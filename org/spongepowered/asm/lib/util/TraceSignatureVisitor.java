/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.lib.util;

import org.spongepowered.asm.lib.signature.SignatureVisitor;

public final class TraceSignatureVisitor
extends SignatureVisitor {
    private final StringBuilder declaration;
    private boolean isInterface;
    private boolean seenFormalParameter;
    private boolean seenInterfaceBound;
    private boolean seenParameter;
    private boolean seenInterface;
    private StringBuilder returnType;
    private StringBuilder exceptions;
    private int argumentStack;
    private int arrayStack;
    private String separator = "";

    public TraceSignatureVisitor(int access) {
        super(327680);
        this.isInterface = (access & 512) != 0;
        this.declaration = new StringBuilder();
    }

    private TraceSignatureVisitor(StringBuilder buf) {
        super(327680);
        this.declaration = buf;
    }

    @Override
    public void visitFormalTypeParameter(String name) {
        this.declaration.append(this.seenFormalParameter ? ", " : "<").append(name);
        this.seenFormalParameter = true;
        this.seenInterfaceBound = false;
    }

    @Override
    public SignatureVisitor visitClassBound() {
        this.separator = " extends ";
        this.startType();
        return this;
    }

    @Override
    public SignatureVisitor visitInterfaceBound() {
        this.separator = this.seenInterfaceBound ? ", " : " extends ";
        this.seenInterfaceBound = true;
        this.startType();
        return this;
    }

    @Override
    public SignatureVisitor visitSuperclass() {
        this.endFormals();
        this.separator = " extends ";
        this.startType();
        return this;
    }

    @Override
    public SignatureVisitor visitInterface() {
        this.separator = this.seenInterface ? ", " : (this.isInterface ? " extends " : " implements ");
        this.seenInterface = true;
        this.startType();
        return this;
    }

    @Override
    public SignatureVisitor visitParameterType() {
        this.endFormals();
        if (this.seenParameter) {
            this.declaration.append(", ");
        } else {
            this.seenParameter = true;
            this.declaration.append('(');
        }
        this.startType();
        return this;
    }

    @Override
    public SignatureVisitor visitReturnType() {
        this.endFormals();
        if (this.seenParameter) {
            this.seenParameter = false;
        } else {
            this.declaration.append('(');
        }
        this.declaration.append(')');
        this.returnType = new StringBuilder();
        return new TraceSignatureVisitor(this.returnType);
    }

    @Override
    public SignatureVisitor visitExceptionType() {
        if (this.exceptions == null) {
            this.exceptions = new StringBuilder();
            return new TraceSignatureVisitor(this.exceptions);
        }
        this.exceptions.append(", ");
        return new TraceSignatureVisitor(this.exceptions);
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public void visitBaseType(char descriptor) {
        switch (descriptor) {
            case 'V': {
                this.declaration.append("void");
                ** break;
            }
            case 'B': {
                this.declaration.append("byte");
                ** break;
            }
            case 'J': {
                this.declaration.append("long");
                ** break;
            }
            case 'Z': {
                this.declaration.append("boolean");
                ** break;
            }
            case 'I': {
                this.declaration.append("int");
                ** break;
            }
            case 'S': {
                this.declaration.append("short");
                ** break;
            }
            case 'C': {
                this.declaration.append("char");
                ** break;
            }
            case 'F': {
                this.declaration.append("float");
                ** break;
            }
        }
        this.declaration.append("double");
lbl36: // 9 sources:
        this.endType();
    }

    @Override
    public void visitTypeVariable(String name) {
        this.declaration.append(name);
        this.endType();
    }

    @Override
    public SignatureVisitor visitArrayType() {
        this.startType();
        this.arrayStack |= 1;
        return this;
    }

    @Override
    public void visitClassType(String name) {
        if ("java/lang/Object".equals(name)) {
            boolean needObjectClass;
            boolean bl = needObjectClass = this.argumentStack % 2 != 0 || this.seenParameter;
            if (needObjectClass) {
                this.declaration.append(this.separator).append(name.replace('/', '.'));
            }
        } else {
            this.declaration.append(this.separator).append(name.replace('/', '.'));
        }
        this.separator = "";
        this.argumentStack *= 2;
    }

    @Override
    public void visitInnerClassType(String name) {
        if (this.argumentStack % 2 != 0) {
            this.declaration.append('>');
        }
        this.argumentStack /= 2;
        this.declaration.append('.');
        this.declaration.append(this.separator).append(name.replace('/', '.'));
        this.separator = "";
        this.argumentStack *= 2;
    }

    @Override
    public void visitTypeArgument() {
        if (this.argumentStack % 2 == 0) {
            ++this.argumentStack;
            this.declaration.append('<');
        } else {
            this.declaration.append(", ");
        }
        this.declaration.append('?');
    }

    @Override
    public SignatureVisitor visitTypeArgument(char tag) {
        if (this.argumentStack % 2 == 0) {
            ++this.argumentStack;
            this.declaration.append('<');
        } else {
            this.declaration.append(", ");
        }
        if (tag == '+') {
            this.declaration.append("? extends ");
        } else if (tag == '-') {
            this.declaration.append("? super ");
        }
        this.startType();
        return this;
    }

    @Override
    public void visitEnd() {
        if (this.argumentStack % 2 != 0) {
            this.declaration.append('>');
        }
        this.argumentStack /= 2;
        this.endType();
    }

    public String getDeclaration() {
        return this.declaration.toString();
    }

    public String getReturnType() {
        if (this.returnType == null) {
            return null;
        }
        String string = this.returnType.toString();
        return string;
    }

    public String getExceptions() {
        if (this.exceptions == null) {
            return null;
        }
        String string = this.exceptions.toString();
        return string;
    }

    private void endFormals() {
        if (!this.seenFormalParameter) return;
        this.declaration.append('>');
        this.seenFormalParameter = false;
    }

    private void startType() {
        this.arrayStack *= 2;
    }

    private void endType() {
        if (this.arrayStack % 2 == 0) {
            this.arrayStack /= 2;
            return;
        }
        while (this.arrayStack % 2 != 0) {
            this.arrayStack /= 2;
            this.declaration.append("[]");
        }
    }
}

