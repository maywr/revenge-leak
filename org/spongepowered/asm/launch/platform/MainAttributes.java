/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.launch.platform;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

final class MainAttributes {
    private static final Map<URI, MainAttributes> instances = new HashMap<URI, MainAttributes>();
    protected final Attributes attributes;

    private MainAttributes() {
        this.attributes = new Attributes();
    }

    private MainAttributes(File jar) {
        this.attributes = MainAttributes.getAttributes(jar);
    }

    public final String get(String name) {
        if (this.attributes == null) return null;
        return this.attributes.getValue(name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Attributes getAttributes(File jar) {
        if (jar == null) {
            return null;
        }
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(jar);
            Manifest manifest = jarFile.getManifest();
            if (manifest == null) return new Attributes();
            Attributes attributes = manifest.getMainAttributes();
            return attributes;
        }
        catch (IOException manifest) {
            return new Attributes();
        }
        finally {
            try {
                if (jarFile != null) {
                    jarFile.close();
                }
            }
            catch (IOException iOException) {}
        }
    }

    public static MainAttributes of(File jar) {
        return MainAttributes.of(jar.toURI());
    }

    public static MainAttributes of(URI uri) {
        MainAttributes attributes = instances.get(uri);
        if (attributes != null) return attributes;
        attributes = new MainAttributes(new File(uri));
        instances.put(uri, attributes);
        return attributes;
    }
}

