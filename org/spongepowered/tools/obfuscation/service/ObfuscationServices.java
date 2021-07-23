/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.tools.obfuscation.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import javax.tools.Diagnostic;
import org.spongepowered.tools.obfuscation.ObfuscationType;
import org.spongepowered.tools.obfuscation.interfaces.IMixinAnnotationProcessor;
import org.spongepowered.tools.obfuscation.service.IObfuscationService;
import org.spongepowered.tools.obfuscation.service.ObfuscationTypeDescriptor;

public final class ObfuscationServices {
    private static ObfuscationServices instance;
    private final ServiceLoader<IObfuscationService> serviceLoader = ServiceLoader.load(IObfuscationService.class, this.getClass().getClassLoader());
    private final Set<IObfuscationService> services = new HashSet<IObfuscationService>();

    private ObfuscationServices() {
    }

    public static ObfuscationServices getInstance() {
        if (instance != null) return instance;
        instance = new ObfuscationServices();
        return instance;
    }

    /*
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    public void initProviders(IMixinAnnotationProcessor ap) {
        try {
            var2_2 = this.serviceLoader.iterator();
            block4 : do lbl-1000: // 4 sources:
            {
                if (var2_2.hasNext() == false) return;
                service = var2_2.next();
                if (this.services.contains(service)) ** GOTO lbl-1000
                this.services.add(service);
                serviceName = service.getClass().getSimpleName();
                obfTypes = service.getObfuscationTypes();
                if (obfTypes == null) ** GOTO lbl-1000
                var6_7 = obfTypes.iterator();
                do {
                    if (!var6_7.hasNext()) continue block4;
                    obfType = var6_7.next();
                    try {
                        type = ObfuscationType.create(obfType, ap);
                        ap.printMessage(Diagnostic.Kind.NOTE, serviceName + " supports type: \"" + type + "\"");
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } while (true);
                break;
            } while (true);
        }
        catch (ServiceConfigurationError serviceError) {
            ap.printMessage(Diagnostic.Kind.ERROR, serviceError.getClass().getSimpleName() + ": " + serviceError.getMessage());
            serviceError.printStackTrace();
        }
    }

    public Set<String> getSupportedOptions() {
        HashSet<String> supportedOptions = new HashSet<String>();
        Iterator<IObfuscationService> iterator = this.serviceLoader.iterator();
        while (iterator.hasNext()) {
            IObfuscationService provider = iterator.next();
            Set<String> options = provider.getSupportedOptions();
            if (options == null) continue;
            supportedOptions.addAll(options);
        }
        return supportedOptions;
    }

    public IObfuscationService getService(Class<? extends IObfuscationService> serviceClass) {
        IObfuscationService service;
        Iterator<IObfuscationService> iterator = this.serviceLoader.iterator();
        do {
            if (!iterator.hasNext()) return null;
            service = iterator.next();
        } while (!serviceClass.getName().equals(service.getClass().getName()));
        return service;
    }
}

