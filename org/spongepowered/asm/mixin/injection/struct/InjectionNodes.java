/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.mixin.injection.struct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.util.Bytecode;

public class InjectionNodes
extends ArrayList<InjectionNode> {
    private static final long serialVersionUID = 1L;

    public InjectionNode add(AbstractInsnNode node) {
        InjectionNode injectionNode = this.get(node);
        if (injectionNode != null) return injectionNode;
        injectionNode = new InjectionNode(node);
        this.add(injectionNode);
        return injectionNode;
    }

    public InjectionNode get(AbstractInsnNode node) {
        InjectionNode injectionNode;
        Iterator iterator = this.iterator();
        do {
            if (!iterator.hasNext()) return null;
        } while (!(injectionNode = (InjectionNode)iterator.next()).matches(node));
        return injectionNode;
    }

    public boolean contains(AbstractInsnNode node) {
        if (this.get(node) == null) return false;
        return true;
    }

    public void replace(AbstractInsnNode oldNode, AbstractInsnNode newNode) {
        InjectionNode injectionNode = this.get(oldNode);
        if (injectionNode == null) return;
        injectionNode.replace(newNode);
    }

    public void remove(AbstractInsnNode node) {
        InjectionNode injectionNode = this.get(node);
        if (injectionNode == null) return;
        injectionNode.remove();
    }

    public static class InjectionNode
    implements Comparable<InjectionNode> {
        private static int nextId = 0;
        private final int id;
        private final AbstractInsnNode originalTarget;
        private AbstractInsnNode currentTarget;
        private Map<String, Object> decorations;

        public InjectionNode(AbstractInsnNode node) {
            this.currentTarget = this.originalTarget = node;
            this.id = nextId++;
        }

        public int getId() {
            return this.id;
        }

        public AbstractInsnNode getOriginalTarget() {
            return this.originalTarget;
        }

        public AbstractInsnNode getCurrentTarget() {
            return this.currentTarget;
        }

        public InjectionNode replace(AbstractInsnNode target) {
            this.currentTarget = target;
            return this;
        }

        public InjectionNode remove() {
            this.currentTarget = null;
            return this;
        }

        public boolean matches(AbstractInsnNode node) {
            if (this.originalTarget == node) return true;
            if (this.currentTarget == node) return true;
            return false;
        }

        public boolean isReplaced() {
            if (this.originalTarget == this.currentTarget) return false;
            return true;
        }

        public boolean isRemoved() {
            if (this.currentTarget != null) return false;
            return true;
        }

        public <V> InjectionNode decorate(String key, V value) {
            if (this.decorations == null) {
                this.decorations = new HashMap<String, Object>();
            }
            this.decorations.put(key, value);
            return this;
        }

        public boolean hasDecoration(String key) {
            if (this.decorations == null) return false;
            if (this.decorations.get(key) == null) return false;
            return true;
        }

        public <V> V getDecoration(String key) {
            Object object;
            if (this.decorations == null) {
                object = null;
                return (V)object;
            }
            object = this.decorations.get(key);
            return (V)object;
        }

        @Override
        public int compareTo(InjectionNode other) {
            if (other == null) {
                return Integer.MAX_VALUE;
            }
            int n = this.hashCode() - other.hashCode();
            return n;
        }

        public String toString() {
            return String.format("InjectionNode[%s]", Bytecode.describeNode(this.currentTarget).replaceAll("\\s+", " "));
        }
    }

}

