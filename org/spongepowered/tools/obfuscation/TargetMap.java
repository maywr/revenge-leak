/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.tools.obfuscation;

import com.google.common.io.Files;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.TypeElement;
import org.spongepowered.tools.obfuscation.AnnotatedMixin;
import org.spongepowered.tools.obfuscation.mirror.TypeHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeReference;

public final class TargetMap
extends HashMap<TypeReference, Set<TypeReference>> {
    private static final long serialVersionUID = 1L;
    private final String sessionId;

    private TargetMap() {
        this(String.valueOf(System.currentTimeMillis()));
    }

    private TargetMap(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public void registerTargets(AnnotatedMixin mixin) {
        this.registerTargets(mixin.getTargets(), mixin.getHandle());
    }

    public void registerTargets(List<TypeHandle> targets, TypeHandle mixin) {
        Iterator<TypeHandle> iterator = targets.iterator();
        while (iterator.hasNext()) {
            TypeHandle target = iterator.next();
            this.addMixin(target, mixin);
        }
    }

    public void addMixin(TypeHandle target, TypeHandle mixin) {
        this.addMixin(target.getReference(), mixin.getReference());
    }

    public void addMixin(String target, String mixin) {
        this.addMixin(new TypeReference(target), new TypeReference(mixin));
    }

    public void addMixin(TypeReference target, TypeReference mixin) {
        Set<TypeReference> mixins = this.getMixinsFor(target);
        mixins.add(mixin);
    }

    public Collection<TypeReference> getMixinsTargeting(TypeElement target) {
        return this.getMixinsTargeting(new TypeHandle(target));
    }

    public Collection<TypeReference> getMixinsTargeting(TypeHandle target) {
        return this.getMixinsTargeting(target.getReference());
    }

    public Collection<TypeReference> getMixinsTargeting(TypeReference target) {
        return Collections.unmodifiableCollection(this.getMixinsFor(target));
    }

    private Set<TypeReference> getMixinsFor(TypeReference target) {
        HashSet mixins = (HashSet)this.get(target);
        if (mixins != null) return mixins;
        mixins = new HashSet();
        this.put(target, mixins);
        return mixins;
    }

    public void readImports(File file) throws IOException {
        if (!file.isFile()) {
            return;
        }
        Iterator iterator = Files.readLines((File)file, (Charset)Charset.defaultCharset()).iterator();
        while (iterator.hasNext()) {
            String line = (String)iterator.next();
            String[] parts = line.split("\t");
            if (parts.length != 2) continue;
            this.addMixin(parts[1], parts[0]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void write(boolean temp) {
        ObjectOutputStream oos = null;
        FileOutputStream fout = null;
        try {
            File sessionFile = TargetMap.getSessionFile(this.sessionId);
            if (temp) {
                sessionFile.deleteOnExit();
            }
            fout = new FileOutputStream(sessionFile, true);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(this);
            return;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
        finally {
            if (oos != null) {
                try {
                    oos.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static TargetMap read(File sessionFile) {
        ObjectInputStream objectinputstream = null;
        FileInputStream streamIn = null;
        try {
            streamIn = new FileInputStream(sessionFile);
            objectinputstream = new ObjectInputStream(streamIn);
            TargetMap targetMap = (TargetMap)objectinputstream.readObject();
            return targetMap;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if (objectinputstream != null) {
                try {
                    objectinputstream.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static TargetMap create(String sessionId) {
        if (sessionId == null) return new TargetMap();
        File sessionFile = TargetMap.getSessionFile(sessionId);
        if (!sessionFile.exists()) return new TargetMap();
        TargetMap map = TargetMap.read(sessionFile);
        if (map == null) return new TargetMap();
        return map;
    }

    private static File getSessionFile(String sessionId) {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        return new File(tempDir, String.format("mixin-targetdb-%s.tmp", sessionId));
    }
}

