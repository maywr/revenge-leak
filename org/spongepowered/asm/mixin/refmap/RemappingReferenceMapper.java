/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.google.common.io.LineProcessor
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.spongepowered.asm.mixin.refmap;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.refmap.IReferenceMapper;

public final class RemappingReferenceMapper
implements IReferenceMapper {
    private static final String DEFAULT_RESOURCE_PATH_PROPERTY = "net.minecraftforge.gradle.GradleStart.srg.srg-mcp";
    private static final String DEFAULT_MAPPING_ENV = "searge";
    private static final Logger logger = LogManager.getLogger((String)"mixin");
    private static final Map<String, Map<String, String>> srgs = new HashMap<String, Map<String, String>>();
    private final IReferenceMapper refMap;
    private final Map<String, String> mappings;
    private final Map<String, Map<String, String>> cache = new HashMap<String, Map<String, String>>();

    private RemappingReferenceMapper(MixinEnvironment env, IReferenceMapper refMap) {
        this.refMap = refMap;
        this.refMap.setContext(RemappingReferenceMapper.getMappingEnv(env));
        String resource = RemappingReferenceMapper.getResource(env);
        this.mappings = RemappingReferenceMapper.loadSrgs(resource);
        logger.info("Remapping refMap {} using {}", new Object[]{refMap.getResourceName(), resource});
    }

    @Override
    public boolean isDefault() {
        return this.refMap.isDefault();
    }

    @Override
    public String getResourceName() {
        return this.refMap.getResourceName();
    }

    @Override
    public String getStatus() {
        return this.refMap.getStatus();
    }

    @Override
    public String getContext() {
        return this.refMap.getContext();
    }

    @Override
    public void setContext(String context) {
    }

    @Override
    public String remap(String className, String reference) {
        Map<String, String> classCache = this.getCache(className);
        String remapped = classCache.get(reference);
        if (remapped != null) return remapped;
        remapped = this.refMap.remap(className, reference);
        Iterator<Map.Entry<String, String>> iterator = this.mappings.entrySet().iterator();
        do {
            if (!iterator.hasNext()) {
                classCache.put(reference, remapped);
                return remapped;
            }
            Map.Entry<String, String> entry = iterator.next();
            remapped = remapped.replace(entry.getKey(), entry.getValue());
        } while (true);
    }

    private Map<String, String> getCache(String className) {
        Map<String, String> classCache = this.cache.get(className);
        if (classCache != null) return classCache;
        classCache = new HashMap<String, String>();
        this.cache.put(className, classCache);
        return classCache;
    }

    @Override
    public String remapWithContext(String context, String className, String reference) {
        return this.refMap.remapWithContext(context, className, reference);
    }

    private static Map<String, String> loadSrgs(String fileName) {
        if (srgs.containsKey(fileName)) {
            return srgs.get(fileName);
        }
        final HashMap<String, String> map = new HashMap<String, String>();
        srgs.put(fileName, map);
        File file = new File(fileName);
        if (!file.isFile()) {
            return map;
        }
        try {
            Files.readLines((File)file, (Charset)Charsets.UTF_8, (LineProcessor)new LineProcessor<Object>(){

                public Object getResult() {
                    return null;
                }

                public boolean processLine(String line) throws IOException {
                    if (Strings.isNullOrEmpty(line)) return true;
                    if (line.startsWith("#")) {
                        return true;
                    }
                    int fromPos = 0;
                    int toPos = 0;
                    toPos = line.startsWith("MD: ") ? 2 : (line.startsWith("FD: ") ? 1 : 0);
                    if (toPos <= 0) return true;
                    String[] entries = line.substring(4).split(" ", 4);
                    map.put(entries[fromPos].substring(entries[fromPos].lastIndexOf(47) + 1), entries[toPos].substring(entries[toPos].lastIndexOf(47) + 1));
                    return true;
                }
            });
            return map;
        }
        catch (IOException ex) {
            logger.warn("Could not read input SRG file: {}", new Object[]{fileName});
            logger.catching((Throwable)ex);
        }
        return map;
    }

    public static IReferenceMapper of(MixinEnvironment env, IReferenceMapper refMap) {
        if (refMap.isDefault()) return refMap;
        if (!RemappingReferenceMapper.hasData(env)) return refMap;
        return new RemappingReferenceMapper(env, refMap);
    }

    private static boolean hasData(MixinEnvironment env) {
        String fileName = RemappingReferenceMapper.getResource(env);
        if (fileName == null) return false;
        if (!new File(fileName).exists()) return false;
        return true;
    }

    private static String getResource(MixinEnvironment env) {
        String string;
        String resource = env.getOptionValue(MixinEnvironment.Option.REFMAP_REMAP_RESOURCE);
        if (Strings.isNullOrEmpty(resource)) {
            string = System.getProperty(DEFAULT_RESOURCE_PATH_PROPERTY);
            return string;
        }
        string = resource;
        return string;
    }

    private static String getMappingEnv(MixinEnvironment env) {
        String resource = env.getOptionValue(MixinEnvironment.Option.REFMAP_REMAP_SOURCE_ENV);
        if (Strings.isNullOrEmpty(resource)) {
            return DEFAULT_MAPPING_ENV;
        }
        String string = resource;
        return string;
    }

}

