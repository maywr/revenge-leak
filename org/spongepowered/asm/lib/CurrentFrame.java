/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.lib;

import org.spongepowered.asm.lib.ClassWriter;
import org.spongepowered.asm.lib.Frame;
import org.spongepowered.asm.lib.Item;
import org.spongepowered.asm.lib.Label;

class CurrentFrame
extends Frame {
    CurrentFrame() {
    }

    @Override
    void execute(int opcode, int arg, ClassWriter cw, Item item) {
        super.execute(opcode, arg, cw, item);
        Frame successor = new Frame();
        this.merge(cw, successor, 0);
        this.set(successor);
        this.owner.inputStackTop = 0;
    }
}

