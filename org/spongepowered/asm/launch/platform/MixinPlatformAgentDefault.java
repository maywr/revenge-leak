/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.launch.platform;

import java.net.URI;
import org.spongepowered.asm.launch.platform.MainAttributes;
import org.spongepowered.asm.launch.platform.MixinPlatformAgentAbstract;
import org.spongepowered.asm.launch.platform.MixinPlatformManager;

public class MixinPlatformAgentDefault
extends MixinPlatformAgentAbstract {
    public MixinPlatformAgentDefault(MixinPlatformManager manager, URI uri) {
        super(manager, uri);
    }

    @Override
    public void prepare() {
        String mixinConfigs;
        String tokenProviders;
        String compatibilityLevel = this.attributes.get("MixinCompatibilityLevel");
        if (compatibilityLevel != null) {
            this.manager.setCompatibilityLevel(compatibilityLevel);
        }
        if ((mixinConfigs = this.attributes.get("MixinConfigs")) != null) {
            for (String config : mixinConfigs.split(",")) {
                this.manager.addConfig(config.trim());
            }
        }
        if ((tokenProviders = this.attributes.get("MixinTokenProviders")) == null) return;
        String[] arrstring = tokenProviders.split(",");
        int n = arrstring.length;
        int config = 0;
        while (config < n) {
            String provider = arrstring[config];
            this.manager.addTokenProvider(provider.trim());
            ++config;
        }
    }

    @Override
    public void initPrimaryContainer() {
    }

    @Override
    public void inject() {
    }

    @Override
    public String getLaunchTarget() {
        return this.attributes.get("Main-Class");
    }
}

