/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.mixin.transformer.ext;

import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.transformer.ClassInfo;

public interface ITargetClassContext {
    public ClassInfo getClassInfo();

    public ClassNode getClassNode();
}

