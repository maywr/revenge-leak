/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.tools.obfuscation;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.spongepowered.tools.obfuscation.ObfuscationEnvironment;
import org.spongepowered.tools.obfuscation.interfaces.IMixinAnnotationProcessor;
import org.spongepowered.tools.obfuscation.interfaces.IOptionProvider;
import org.spongepowered.tools.obfuscation.service.ObfuscationTypeDescriptor;

public final class ObfuscationType {
    private static final Map<String, ObfuscationType> types = new LinkedHashMap<String, ObfuscationType>();
    private final String key;
    private final ObfuscationTypeDescriptor descriptor;
    private final IMixinAnnotationProcessor ap;
    private final IOptionProvider options;

    private ObfuscationType(ObfuscationTypeDescriptor descriptor, IMixinAnnotationProcessor ap) {
        this.key = descriptor.getKey();
        this.descriptor = descriptor;
        this.ap = ap;
        this.options = ap;
    }

    public final ObfuscationEnvironment createEnvironment() {
        try {
            Class<? extends ObfuscationEnvironment> cls = this.descriptor.getEnvironmentType();
            Constructor<? extends ObfuscationEnvironment> ctor = cls.getDeclaredConstructor(ObfuscationType.class);
            ctor.setAccessible(true);
            return ctor.newInstance(this);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public String toString() {
        return this.key;
    }

    public String getKey() {
        return this.key;
    }

    public ObfuscationTypeDescriptor getConfig() {
        return this.descriptor;
    }

    public IMixinAnnotationProcessor getAnnotationProcessor() {
        return this.ap;
    }

    public boolean isDefault() {
        String defaultEnv = this.options.getOption("defaultObfuscationEnv");
        if (defaultEnv == null) {
            if (this.key.equals("searge")) return true;
        }
        if (defaultEnv == null) return false;
        if (!this.key.equals(defaultEnv.toLowerCase())) return false;
        return true;
    }

    public boolean isSupported() {
        if (this.getInputFileNames().size() <= 0) return false;
        return true;
    }

    public List<String> getInputFileNames() {
        String extraInputFiles;
        ImmutableList.Builder builder = ImmutableList.builder();
        String inputFile = this.options.getOption(this.descriptor.getInputFileOption());
        if (inputFile != null) {
            builder.add(inputFile);
        }
        if ((extraInputFiles = this.options.getOption(this.descriptor.getExtraInputFilesOption())) == null) return builder.build();
        String[] arrstring = extraInputFiles.split(";");
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String extraInputFile = arrstring[n2];
            builder.add(extraInputFile.trim());
            ++n2;
        }
        return builder.build();
    }

    public String getOutputFileName() {
        return this.options.getOption(this.descriptor.getOutputFileOption());
    }

    public static Iterable<ObfuscationType> types() {
        return types.values();
    }

    public static ObfuscationType create(ObfuscationTypeDescriptor descriptor, IMixinAnnotationProcessor ap) {
        String key = descriptor.getKey();
        if (types.containsKey(key)) {
            throw new IllegalArgumentException("Obfuscation type with key " + key + " was already registered");
        }
        ObfuscationType type = new ObfuscationType(descriptor, ap);
        types.put(key, type);
        return type;
    }

    public static ObfuscationType get(String key) {
        ObfuscationType type = types.get(key);
        if (type != null) return type;
        throw new IllegalArgumentException("Obfuscation type with key " + key + " was not registered");
    }
}

