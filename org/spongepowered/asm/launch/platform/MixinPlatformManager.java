/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.spongepowered.asm.launch.platform;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.platform.MainAttributes;
import org.spongepowered.asm.launch.platform.MixinContainer;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.service.IClassProvider;
import org.spongepowered.asm.service.MixinService;

public class MixinPlatformManager {
    private static final String DEFAULT_MAIN_CLASS = "net.minecraft.client.main.Main";
    private static final String MIXIN_TWEAKER_CLASS = "org.spongepowered.asm.launch.MixinTweaker";
    private static final Logger logger = LogManager.getLogger((String)"mixin");
    private final Map<URI, MixinContainer> containers = new LinkedHashMap<URI, MixinContainer>();
    private MixinContainer primaryContainer;
    private boolean prepared = false;
    private boolean injected;

    public void init() {
        logger.debug("Initialising Mixin Platform Manager");
        URI uri = null;
        try {
            uri = this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
            if (uri != null) {
                logger.debug("Mixin platform: primary container is {}", new Object[]{uri});
                this.primaryContainer = this.addContainer(uri);
            }
        }
        catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
        this.scanClasspath();
    }

    public Collection<String> getPhaseProviderClasses() {
        Collection<String> phaseProviders = this.primaryContainer.getPhaseProviders();
        if (phaseProviders == null) return Collections.emptyList();
        return Collections.unmodifiableCollection(phaseProviders);
    }

    public final MixinContainer addContainer(URI uri) {
        MixinContainer existingContainer = this.containers.get(uri);
        if (existingContainer != null) {
            return existingContainer;
        }
        logger.debug("Adding mixin platform agents for container {}", new Object[]{uri});
        MixinContainer container = new MixinContainer(this, uri);
        this.containers.put(uri, container);
        if (!this.prepared) return container;
        container.prepare();
        return container;
    }

    public final void prepare(List<String> args) {
        this.prepared = true;
        for (MixinContainer container : this.containers.values()) {
            container.prepare();
        }
        if (args != null) {
            this.parseArgs(args);
            return;
        }
        String argv = System.getProperty("sun.java.command");
        if (argv == null) return;
        this.parseArgs(Arrays.asList(argv.split(" ")));
    }

    private void parseArgs(List<String> args) {
        boolean captureNext = false;
        Iterator<String> iterator = args.iterator();
        while (iterator.hasNext()) {
            String arg = iterator.next();
            if (captureNext) {
                this.addConfig(arg);
            }
            captureNext = "--mixin".equals(arg);
        }
    }

    public final void inject() {
        if (this.injected) {
            return;
        }
        this.injected = true;
        if (this.primaryContainer != null) {
            this.primaryContainer.initPrimaryContainer();
        }
        this.scanClasspath();
        logger.debug("inject() running with {} agents", new Object[]{this.containers.size()});
        Iterator<MixinContainer> iterator = this.containers.values().iterator();
        while (iterator.hasNext()) {
            MixinContainer container = iterator.next();
            try {
                container.inject();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void scanClasspath() {
        URL[] sources;
        URL[] arruRL = sources = MixinService.getService().getClassProvider().getClassPath();
        int n = arruRL.length;
        int n2 = 0;
        while (n2 < n) {
            URL url = arruRL[n2];
            try {
                URI uri = url.toURI();
                if (!this.containers.containsKey(uri)) {
                    String tweaker;
                    MainAttributes attributes;
                    logger.debug("Scanning {} for mixin tweaker", new Object[]{uri});
                    if ("file".equals(uri.getScheme()) && new File(uri).exists() && MIXIN_TWEAKER_CLASS.equals(tweaker = (attributes = MainAttributes.of(uri)).get("TweakClass"))) {
                        logger.debug("{} contains a mixin tweaker, adding agents", new Object[]{uri});
                        this.addContainer(uri);
                    }
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            ++n2;
        }
    }

    public String getLaunchTarget() {
        String mainClass;
        MixinContainer container;
        Iterator<MixinContainer> iterator = this.containers.values().iterator();
        do {
            if (!iterator.hasNext()) return DEFAULT_MAIN_CLASS;
        } while ((mainClass = (container = iterator.next()).getLaunchTarget()) == null);
        return mainClass;
    }

    final void setCompatibilityLevel(String level) {
        try {
            MixinEnvironment.CompatibilityLevel value = MixinEnvironment.CompatibilityLevel.valueOf(level.toUpperCase());
            logger.debug("Setting mixin compatibility level: {}", new Object[]{value});
            MixinEnvironment.setCompatibilityLevel(value);
            return;
        }
        catch (IllegalArgumentException ex) {
            logger.warn("Invalid compatibility level specified: {}", new Object[]{level});
        }
    }

    final void addConfig(String config) {
        if (config.endsWith(".json")) {
            logger.debug("Registering mixin config: {}", new Object[]{config});
            Mixins.addConfiguration(config);
            return;
        }
        if (!config.contains(".json@")) return;
        int pos = config.indexOf(".json@");
        String phaseName = config.substring(pos + 6);
        config = config.substring(0, pos + 5);
        MixinEnvironment.Phase phase = MixinEnvironment.Phase.forName(phaseName);
        if (phase == null) return;
        logger.warn("Setting config phase via manifest is deprecated: {}. Specify target in config instead", new Object[]{config});
        logger.debug("Registering mixin config: {}", new Object[]{config});
        MixinEnvironment.getEnvironment(phase).addConfiguration(config);
    }

    final void addTokenProvider(String provider) {
        if (provider.contains("@")) {
            String[] parts = provider.split("@", 2);
            MixinEnvironment.Phase phase = MixinEnvironment.Phase.forName(parts[1]);
            if (phase == null) return;
            logger.debug("Registering token provider class: {}", new Object[]{parts[0]});
            MixinEnvironment.getEnvironment(phase).registerTokenProviderClass(parts[0]);
            return;
        }
        MixinEnvironment.getDefaultEnvironment().registerTokenProviderClass(provider);
    }
}

