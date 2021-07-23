/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.mixin.injection.struct;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.tree.AnnotationNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.modify.LocalVariableDiscriminator;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo;
import org.spongepowered.asm.mixin.injection.struct.InvalidMemberDescriptorException;
import org.spongepowered.asm.mixin.injection.struct.MemberInfo;
import org.spongepowered.asm.mixin.injection.throwables.InvalidInjectionPointException;
import org.spongepowered.asm.mixin.refmap.IMixinContext;

public class InjectionPointData {
    private static final Pattern AT_PATTERN = InjectionPointData.createPattern();
    private final Map<String, String> args = new HashMap<String, String>();
    private final IMixinContext context;
    private final MethodNode method;
    private final AnnotationNode parent;
    private final String at;
    private final String type;
    private final InjectionPoint.Selector selector;
    private final String target;
    private final String slice;
    private final int ordinal;
    private final int opcode;
    private final String id;

    public InjectionPointData(IMixinContext context, MethodNode method, AnnotationNode parent, String at, List<String> args, String target, String slice, int ordinal, int opcode, String id) {
        this.context = context;
        this.method = method;
        this.parent = parent;
        this.at = at;
        this.target = target;
        this.slice = Strings.nullToEmpty((String)slice);
        this.ordinal = Math.max(-1, ordinal);
        this.opcode = opcode;
        this.id = id;
        this.parseArgs(args);
        this.args.put("target", target);
        this.args.put("ordinal", String.valueOf(ordinal));
        this.args.put("opcode", String.valueOf(opcode));
        Matcher matcher = AT_PATTERN.matcher(at);
        this.type = InjectionPointData.parseType(matcher, at);
        this.selector = InjectionPointData.parseSelector(matcher);
    }

    private void parseArgs(List<String> args) {
        if (args == null) {
            return;
        }
        Iterator<String> iterator = args.iterator();
        while (iterator.hasNext()) {
            String arg = iterator.next();
            if (arg == null) continue;
            int eqPos = arg.indexOf(61);
            if (eqPos > -1) {
                this.args.put(arg.substring(0, eqPos), arg.substring(eqPos + 1));
                continue;
            }
            this.args.put(arg, "");
        }
    }

    public String getAt() {
        return this.at;
    }

    public String getType() {
        return this.type;
    }

    public InjectionPoint.Selector getSelector() {
        return this.selector;
    }

    public IMixinContext getContext() {
        return this.context;
    }

    public MethodNode getMethod() {
        return this.method;
    }

    public Type getMethodReturnType() {
        return Type.getReturnType(this.method.desc);
    }

    public AnnotationNode getParent() {
        return this.parent;
    }

    public String getSlice() {
        return this.slice;
    }

    public LocalVariableDiscriminator getLocalVariableDiscriminator() {
        return LocalVariableDiscriminator.parse(this.parent);
    }

    public String get(String key, String defaultValue) {
        String string;
        String value = this.args.get(key);
        if (value != null) {
            string = value;
            return string;
        }
        string = defaultValue;
        return string;
    }

    public int get(String key, int defaultValue) {
        return InjectionPointData.parseInt(this.get(key, String.valueOf(defaultValue)), defaultValue);
    }

    public boolean get(String key, boolean defaultValue) {
        return InjectionPointData.parseBoolean(this.get(key, String.valueOf(defaultValue)), defaultValue);
    }

    public MemberInfo get(String key) {
        try {
            return MemberInfo.parseAndValidate(this.get(key, ""), this.context);
        }
        catch (InvalidMemberDescriptorException ex) {
            throw new InvalidInjectionPointException(this.context, "Failed parsing @At(\"%s\").%s descriptor \"%s\" on %s", this.at, key, this.target, InjectionInfo.describeInjector(this.context, this.parent, this.method));
        }
    }

    public MemberInfo getTarget() {
        try {
            return MemberInfo.parseAndValidate(this.target, this.context);
        }
        catch (InvalidMemberDescriptorException ex) {
            throw new InvalidInjectionPointException(this.context, "Failed parsing @At(\"%s\") descriptor \"%s\" on %s", this.at, this.target, InjectionInfo.describeInjector(this.context, this.parent, this.method));
        }
    }

    public int getOrdinal() {
        return this.ordinal;
    }

    public int getOpcode() {
        return this.opcode;
    }

    public int getOpcode(int defaultOpcode) {
        int n;
        if (this.opcode > 0) {
            n = this.opcode;
            return n;
        }
        n = defaultOpcode;
        return n;
    }

    public int getOpcode(int defaultOpcode, int ... validOpcodes) {
        int[] arrn = validOpcodes;
        int n = arrn.length;
        int n2 = 0;
        while (n2 < n) {
            int validOpcode = arrn[n2];
            if (this.opcode == validOpcode) {
                return this.opcode;
            }
            ++n2;
        }
        return defaultOpcode;
    }

    public String getId() {
        return this.id;
    }

    public String toString() {
        return this.type;
    }

    private static Pattern createPattern() {
        return Pattern.compile(String.format("^([^:]+):?(%s)?$", Joiner.on('|').join((Object[])InjectionPoint.Selector.values())));
    }

    public static String parseType(String at) {
        Matcher matcher = AT_PATTERN.matcher(at);
        return InjectionPointData.parseType(matcher, at);
    }

    private static String parseType(Matcher matcher, String at) {
        String string;
        if (matcher.matches()) {
            string = matcher.group(1);
            return string;
        }
        string = at;
        return string;
    }

    private static InjectionPoint.Selector parseSelector(Matcher matcher) {
        InjectionPoint.Selector selector;
        if (matcher.matches() && matcher.group(2) != null) {
            selector = InjectionPoint.Selector.valueOf(matcher.group(2));
            return selector;
        }
        selector = InjectionPoint.Selector.DEFAULT;
        return selector;
    }

    private static int parseInt(String string, int defaultValue) {
        try {
            return Integer.parseInt(string);
        }
        catch (Exception ex) {
            return defaultValue;
        }
    }

    private static boolean parseBoolean(String string, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(string);
        }
        catch (Exception ex) {
            return defaultValue;
        }
    }
}

