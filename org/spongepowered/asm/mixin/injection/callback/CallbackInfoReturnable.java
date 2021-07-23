/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.mixin.injection.callback;

import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CancellationException;

public class CallbackInfoReturnable<R>
extends CallbackInfo {
    private R returnValue;

    public CallbackInfoReturnable(String name, boolean cancellable) {
        super(name, cancellable);
        this.returnValue = null;
    }

    public CallbackInfoReturnable(String name, boolean cancellable, R returnValue) {
        super(name, cancellable);
        this.returnValue = returnValue;
    }

    public CallbackInfoReturnable(String name, boolean cancellable, byte returnValue) {
        super(name, cancellable);
        this.returnValue = returnValue;
    }

    public CallbackInfoReturnable(String name, boolean cancellable, char returnValue) {
        super(name, cancellable);
        this.returnValue = Character.valueOf(returnValue);
    }

    public CallbackInfoReturnable(String name, boolean cancellable, double returnValue) {
        super(name, cancellable);
        this.returnValue = returnValue;
    }

    public CallbackInfoReturnable(String name, boolean cancellable, float returnValue) {
        super(name, cancellable);
        this.returnValue = Float.valueOf(returnValue);
    }

    public CallbackInfoReturnable(String name, boolean cancellable, int returnValue) {
        super(name, cancellable);
        this.returnValue = returnValue;
    }

    public CallbackInfoReturnable(String name, boolean cancellable, long returnValue) {
        super(name, cancellable);
        this.returnValue = returnValue;
    }

    public CallbackInfoReturnable(String name, boolean cancellable, short returnValue) {
        super(name, cancellable);
        this.returnValue = returnValue;
    }

    public CallbackInfoReturnable(String name, boolean cancellable, boolean returnValue) {
        super(name, cancellable);
        this.returnValue = returnValue;
    }

    public void setReturnValue(R returnValue) throws CancellationException {
        super.cancel();
        this.returnValue = returnValue;
    }

    public R getReturnValue() {
        return this.returnValue;
    }

    public byte getReturnValueB() {
        if (this.returnValue != null) return (Byte)this.returnValue;
        return 0;
    }

    public char getReturnValueC() {
        if (this.returnValue != null) return ((Character)this.returnValue).charValue();
        return '\u0000';
    }

    public double getReturnValueD() {
        if (this.returnValue != null) return (Double)this.returnValue;
        return 0.0;
    }

    public float getReturnValueF() {
        if (this.returnValue != null) return ((Float)this.returnValue).floatValue();
        return 0.0f;
    }

    public int getReturnValueI() {
        if (this.returnValue != null) return (Integer)this.returnValue;
        return 0;
    }

    public long getReturnValueJ() {
        if (this.returnValue != null) return (Long)this.returnValue;
        return 0L;
    }

    public short getReturnValueS() {
        if (this.returnValue != null) return (Short)this.returnValue;
        return 0;
    }

    public boolean getReturnValueZ() {
        if (this.returnValue != null) return (Boolean)this.returnValue;
        return false;
    }

    static String getReturnAccessor(Type returnType) {
        if (returnType.getSort() == 10) return "getReturnValue";
        if (returnType.getSort() != 9) return String.format("getReturnValue%s", returnType.getDescriptor());
        return "getReturnValue";
    }

    static String getReturnDescriptor(Type returnType) {
        if (returnType.getSort() == 10 || returnType.getSort() == 9) return String.format("()%s", "Ljava/lang/Object;");
        return String.format("()%s", returnType.getDescriptor());
    }
}

