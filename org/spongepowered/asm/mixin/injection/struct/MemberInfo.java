/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.mixin.injection.struct;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.FieldInsnNode;
import org.spongepowered.asm.lib.tree.MethodInsnNode;
import org.spongepowered.asm.mixin.injection.struct.InvalidMemberDescriptorException;
import org.spongepowered.asm.mixin.refmap.IMixinContext;
import org.spongepowered.asm.mixin.refmap.IReferenceMapper;
import org.spongepowered.asm.mixin.throwables.MixinException;
import org.spongepowered.asm.obfuscation.mapping.IMapping;
import org.spongepowered.asm.obfuscation.mapping.common.MappingField;
import org.spongepowered.asm.obfuscation.mapping.common.MappingMethod;
import org.spongepowered.asm.util.SignaturePrinter;

public final class MemberInfo {
    public final String owner;
    public final String name;
    public final String desc;
    public final boolean matchAll;
    private final boolean forceField;
    private final String unparsed;

    public MemberInfo(String name, boolean matchAll) {
        this(name, null, null, matchAll);
    }

    public MemberInfo(String name, String owner, boolean matchAll) {
        this(name, owner, null, matchAll);
    }

    public MemberInfo(String name, String owner, String desc) {
        this(name, owner, desc, false);
    }

    public MemberInfo(String name, String owner, String desc, boolean matchAll) {
        this(name, owner, desc, matchAll, null);
    }

    public MemberInfo(String name, String owner, String desc, boolean matchAll, String unparsed) {
        if (owner != null && owner.contains(".")) {
            throw new IllegalArgumentException("Attempt to instance a MemberInfo with an invalid owner format");
        }
        this.owner = owner;
        this.name = name;
        this.desc = desc;
        this.matchAll = matchAll;
        this.forceField = false;
        this.unparsed = unparsed;
    }

    public MemberInfo(AbstractInsnNode insn) {
        this.matchAll = false;
        this.forceField = false;
        this.unparsed = null;
        if (insn instanceof MethodInsnNode) {
            MethodInsnNode methodNode = (MethodInsnNode)insn;
            this.owner = methodNode.owner;
            this.name = methodNode.name;
            this.desc = methodNode.desc;
            return;
        }
        if (!(insn instanceof FieldInsnNode)) throw new IllegalArgumentException("insn must be an instance of MethodInsnNode or FieldInsnNode");
        FieldInsnNode fieldNode = (FieldInsnNode)insn;
        this.owner = fieldNode.owner;
        this.name = fieldNode.name;
        this.desc = fieldNode.desc;
    }

    public MemberInfo(IMapping<?> mapping) {
        this.owner = mapping.getOwner();
        this.name = mapping.getSimpleName();
        this.desc = mapping.getDesc();
        this.matchAll = false;
        this.forceField = mapping.getType() == IMapping.Type.FIELD;
        this.unparsed = null;
    }

    private MemberInfo(MemberInfo remapped, MappingMethod method, boolean setOwner) {
        this.owner = setOwner ? method.getOwner() : remapped.owner;
        this.name = method.getSimpleName();
        this.desc = method.getDesc();
        this.matchAll = remapped.matchAll;
        this.forceField = false;
        this.unparsed = null;
    }

    private MemberInfo(MemberInfo original, String owner) {
        this.owner = owner;
        this.name = original.name;
        this.desc = original.desc;
        this.matchAll = original.matchAll;
        this.forceField = original.forceField;
        this.unparsed = null;
    }

    public String toString() {
        String desc;
        String owner = this.owner != null ? "L" + this.owner + ";" : "";
        String name = this.name != null ? this.name : "";
        String qualifier = this.matchAll ? "*" : "";
        String string = desc = this.desc != null ? this.desc : "";
        String separator = desc.startsWith("(") ? "" : (this.desc != null ? ":" : "");
        return owner + name + qualifier + separator + desc;
    }

    @Deprecated
    public String toSrg() {
        if (!this.isFullyQualified()) {
            throw new MixinException("Cannot convert unqualified reference to SRG mapping");
        }
        if (!this.desc.startsWith("(")) return this.owner + "/" + this.name;
        return this.owner + "/" + this.name + " " + this.desc;
    }

    public String toDescriptor() {
        if (this.desc != null) return new SignaturePrinter(this).setFullyQualified(true).toDescriptor();
        return "";
    }

    public String toCtorType() {
        String string;
        if (this.unparsed == null) {
            return null;
        }
        String returnType = this.getReturnType();
        if (returnType != null) {
            return returnType;
        }
        if (this.owner != null) {
            return this.owner;
        }
        if (this.name != null && this.desc == null) {
            return this.name;
        }
        if (this.desc != null) {
            string = this.desc;
            return string;
        }
        string = this.unparsed;
        return string;
    }

    public String toCtorDesc() {
        if (this.desc == null) return null;
        if (!this.desc.startsWith("(")) return null;
        if (this.desc.indexOf(41) <= -1) return null;
        return this.desc.substring(0, this.desc.indexOf(41) + 1) + "V";
    }

    public String getReturnType() {
        if (this.desc == null) return null;
        if (this.desc.indexOf(41) == -1) return null;
        if (this.desc.indexOf(40) != 0) {
            return null;
        }
        String returnType = this.desc.substring(this.desc.indexOf(41) + 1);
        if (!returnType.startsWith("L")) return returnType;
        if (!returnType.endsWith(";")) return returnType;
        return returnType.substring(1, returnType.length() - 1);
    }

    public IMapping<?> asMapping() {
        IMapping<MappingField> iMapping;
        if (this.isField()) {
            iMapping = this.asFieldMapping();
            return iMapping;
        }
        iMapping = this.asMethodMapping();
        return iMapping;
    }

    public MappingMethod asMethodMapping() {
        if (!this.isFullyQualified()) {
            throw new MixinException("Cannot convert unqualified reference " + this + " to MethodMapping");
        }
        if (!this.isField()) return new MappingMethod(this.owner, this.name, this.desc);
        throw new MixinException("Cannot convert a non-method reference " + this + " to MethodMapping");
    }

    public MappingField asFieldMapping() {
        if (this.isField()) return new MappingField(this.owner, this.name, this.desc);
        throw new MixinException("Cannot convert non-field reference " + this + " to FieldMapping");
    }

    public boolean isFullyQualified() {
        if (this.owner == null) return false;
        if (this.name == null) return false;
        if (this.desc == null) return false;
        return true;
    }

    public boolean isField() {
        if (this.forceField) return true;
        if (this.desc == null) return false;
        if (this.desc.startsWith("(")) return false;
        return true;
    }

    public boolean isConstructor() {
        return "<init>".equals(this.name);
    }

    public boolean isClassInitialiser() {
        return "<clinit>".equals(this.name);
    }

    public boolean isInitialiser() {
        if (this.isConstructor()) return true;
        if (this.isClassInitialiser()) return true;
        return false;
    }

    public MemberInfo validate() throws InvalidMemberDescriptorException {
        if (this.owner != null) {
            if (!this.owner.matches("(?i)^[\\w\\p{Sc}/]+$")) {
                throw new InvalidMemberDescriptorException("Invalid owner: " + this.owner);
            }
            try {
                if (!this.owner.equals(Type.getType(this.owner).getDescriptor())) {
                    throw new InvalidMemberDescriptorException("Invalid owner type specified: " + this.owner);
                }
            }
            catch (Exception ex) {
                throw new InvalidMemberDescriptorException("Invalid owner type specified: " + this.owner);
            }
        }
        if (this.name != null && !this.name.matches("(?i)^<?[\\w\\p{Sc}]+>?$")) {
            throw new InvalidMemberDescriptorException("Invalid name: " + this.name);
        }
        if (this.desc == null) return this;
        if (!this.desc.matches("^(\\([\\w\\p{Sc}\\[/;]*\\))?\\[*[\\w\\p{Sc}/;]+$")) {
            throw new InvalidMemberDescriptorException("Invalid descriptor: " + this.desc);
        }
        if (this.isField()) {
            if (this.desc.equals(Type.getType(this.desc).getDescriptor())) return this;
            throw new InvalidMemberDescriptorException("Invalid field type in descriptor: " + this.desc);
        }
        try {
            Type.getArgumentTypes(this.desc);
        }
        catch (Exception ex) {
            throw new InvalidMemberDescriptorException("Invalid descriptor: " + this.desc);
        }
        String retString = this.desc.substring(this.desc.indexOf(41) + 1);
        try {
            Type retType = Type.getType(retString);
            if (retString.equals(retType.getDescriptor())) return this;
            throw new InvalidMemberDescriptorException("Invalid return type \"" + retString + "\" in descriptor: " + this.desc);
        }
        catch (Exception ex) {
            throw new InvalidMemberDescriptorException("Invalid return type \"" + retString + "\" in descriptor: " + this.desc);
        }
    }

    public boolean matches(String owner, String name, String desc) {
        return this.matches(owner, name, desc, 0);
    }

    public boolean matches(String owner, String name, String desc, int ordinal) {
        if (this.desc != null && desc != null && !this.desc.equals(desc)) {
            return false;
        }
        if (this.name != null && name != null && !this.name.equals(name)) {
            return false;
        }
        if (this.owner != null && owner != null && !this.owner.equals(owner)) {
            return false;
        }
        if (ordinal == 0) return true;
        if (this.matchAll) return true;
        return false;
    }

    public boolean matches(String name, String desc) {
        return this.matches(name, desc, 0);
    }

    public boolean matches(String name, String desc, int ordinal) {
        if (this.name != null) {
            if (!this.name.equals(name)) return false;
        }
        if (this.desc != null) {
            if (desc == null) return false;
            if (!desc.equals(this.desc)) return false;
        }
        if (ordinal == 0) return true;
        if (!this.matchAll) return false;
        return true;
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != MemberInfo.class) {
            return false;
        }
        MemberInfo other = (MemberInfo)obj;
        if (this.matchAll != other.matchAll) return false;
        if (this.forceField != other.forceField) return false;
        if (!Objects.equal(this.owner, other.owner)) return false;
        if (!Objects.equal(this.name, other.name)) return false;
        if (!Objects.equal(this.desc, other.desc)) return false;
        return true;
    }

    public int hashCode() {
        return Objects.hashCode(this.matchAll, this.owner, this.name, this.desc);
    }

    public MemberInfo move(String newOwner) {
        if (newOwner == null) {
            if (this.owner == null) return this;
        }
        if (newOwner == null) return new MemberInfo(this, newOwner);
        if (!newOwner.equals(this.owner)) return new MemberInfo(this, newOwner);
        return this;
    }

    public MemberInfo transform(String newDesc) {
        if (newDesc == null) {
            if (this.desc == null) return this;
        }
        if (newDesc == null) return new MemberInfo(this.name, this.owner, newDesc, this.matchAll);
        if (!newDesc.equals(this.desc)) return new MemberInfo(this.name, this.owner, newDesc, this.matchAll);
        return this;
    }

    public MemberInfo remapUsing(MappingMethod srgMethod, boolean setOwner) {
        return new MemberInfo(this, srgMethod, setOwner);
    }

    public static MemberInfo parseAndValidate(String string) throws InvalidMemberDescriptorException {
        return MemberInfo.parse(string, null, null).validate();
    }

    public static MemberInfo parseAndValidate(String string, IMixinContext context) throws InvalidMemberDescriptorException {
        return MemberInfo.parse(string, context.getReferenceMapper(), context.getClassRef()).validate();
    }

    public static MemberInfo parse(String string) {
        return MemberInfo.parse(string, null, null);
    }

    public static MemberInfo parse(String string, IMixinContext context) {
        return MemberInfo.parse(string, context.getReferenceMapper(), context.getClassRef());
    }

    private static MemberInfo parse(String input, IReferenceMapper refMapper, String mixinClass) {
        boolean matchAll;
        String desc = null;
        String owner = null;
        String name = Strings.nullToEmpty((String)input).replaceAll("\\s", "");
        if (refMapper != null) {
            name = refMapper.remap(mixinClass, name);
        }
        int lastDotPos = name.lastIndexOf(46);
        int semiColonPos = name.indexOf(59);
        if (lastDotPos > -1) {
            owner = name.substring(0, lastDotPos).replace('.', '/');
            name = name.substring(lastDotPos + 1);
        } else if (semiColonPos > -1 && name.startsWith("L")) {
            owner = name.substring(1, semiColonPos).replace('.', '/');
            name = name.substring(semiColonPos + 1);
        }
        int parenPos = name.indexOf(40);
        int colonPos = name.indexOf(58);
        if (parenPos > -1) {
            desc = name.substring(parenPos);
            name = name.substring(0, parenPos);
        } else if (colonPos > -1) {
            desc = name.substring(colonPos + 1);
            name = name.substring(0, colonPos);
        }
        if ((name.indexOf(47) > -1 || name.indexOf(46) > -1) && owner == null) {
            owner = name;
            name = "";
        }
        if (matchAll = name.endsWith("*")) {
            name = name.substring(0, name.length() - 1);
        }
        if (!name.isEmpty()) return new MemberInfo(name, owner, desc, matchAll, input);
        name = null;
        return new MemberInfo(name, owner, desc, matchAll, input);
    }

    public static MemberInfo fromMapping(IMapping<?> mapping) {
        return new MemberInfo(mapping);
    }
}

