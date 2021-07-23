/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.spongepowered.asm.launch.platform;

import java.lang.reflect.Constructor;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.GlobalProperties;
import org.spongepowered.asm.launch.platform.IMixinPlatformAgent;
import org.spongepowered.asm.launch.platform.MixinPlatformManager;
import org.spongepowered.asm.service.MixinService;

public class MixinContainer {
    private static final List<String> agentClasses = new ArrayList<String>();
    private final Logger logger = LogManager.getLogger((String)"mixin");
    private final URI uri;
    private final List<IMixinPlatformAgent> agents = new ArrayList<IMixinPlatformAgent>();

    public MixinContainer(MixinPlatformManager manager, URI uri) {
        this.uri = uri;
        Iterator<String> iterator = agentClasses.iterator();
        while (iterator.hasNext()) {
            String agentClass = iterator.next();
            try {
                Class<?> clazz = Class.forName(agentClass);
                Constructor<?> ctor = clazz.getDeclaredConstructor(MixinPlatformManager.class, URI.class);
                this.logger.debug("Instancing new {} for {}", new Object[]{clazz.getSimpleName(), this.uri});
                IMixinPlatformAgent agent = (IMixinPlatformAgent)ctor.newInstance(manager, uri);
                this.agents.add(agent);
            }
            catch (Exception ex) {
                this.logger.catching((Throwable)ex);
            }
        }
    }

    public URI getURI() {
        return this.uri;
    }

    public Collection<String> getPhaseProviders() {
        ArrayList<String> phaseProviders = new ArrayList<String>();
        Iterator<IMixinPlatformAgent> iterator = this.agents.iterator();
        while (iterator.hasNext()) {
            IMixinPlatformAgent agent = iterator.next();
            String phaseProvider = agent.getPhaseProvider();
            if (phaseProvider == null) continue;
            phaseProviders.add(phaseProvider);
        }
        return phaseProviders;
    }

    public void prepare() {
        Iterator<IMixinPlatformAgent> iterator = this.agents.iterator();
        while (iterator.hasNext()) {
            IMixinPlatformAgent agent = iterator.next();
            this.logger.debug("Processing prepare() for {}", new Object[]{agent});
            agent.prepare();
        }
    }

    public void initPrimaryContainer() {
        Iterator<IMixinPlatformAgent> iterator = this.agents.iterator();
        while (iterator.hasNext()) {
            IMixinPlatformAgent agent = iterator.next();
            this.logger.debug("Processing launch tasks for {}", new Object[]{agent});
            agent.initPrimaryContainer();
        }
    }

    public void inject() {
        Iterator<IMixinPlatformAgent> iterator = this.agents.iterator();
        while (iterator.hasNext()) {
            IMixinPlatformAgent agent = iterator.next();
            this.logger.debug("Processing inject() for {}", new Object[]{agent});
            agent.inject();
        }
    }

    public String getLaunchTarget() {
        String launchTarget;
        IMixinPlatformAgent agent;
        Iterator<IMixinPlatformAgent> iterator = this.agents.iterator();
        do {
            if (!iterator.hasNext()) return null;
        } while ((launchTarget = (agent = iterator.next()).getLaunchTarget()) == null);
        return launchTarget;
    }

    static {
        GlobalProperties.put("mixin.agents", agentClasses);
        Iterator<String> iterator = MixinService.getService().getPlatformAgents().iterator();
        do {
            if (!iterator.hasNext()) {
                agentClasses.add("org.spongepowered.asm.launch.platform.MixinPlatformAgentDefault");
                return;
            }
            String agent = iterator.next();
            agentClasses.add(agent);
        } while (true);
    }
}

