/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.spongepowered.asm.mixin.transformer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.lib.tree.FieldInsnNode;
import org.spongepowered.asm.lib.tree.FieldNode;
import org.spongepowered.asm.lib.tree.FrameNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.lib.tree.MethodInsnNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.transformer.MethodMapper;
import org.spongepowered.asm.mixin.transformer.MixinInfo;
import org.spongepowered.asm.service.IClassBytecodeProvider;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;
import org.spongepowered.asm.util.ClassSignature;
import org.spongepowered.asm.util.perf.Profiler;

public final class ClassInfo {
    public static final int INCLUDE_PRIVATE = 2;
    public static final int INCLUDE_STATIC = 8;
    public static final int INCLUDE_ALL = 10;
    private static final Logger logger = LogManager.getLogger((String)"mixin");
    private static final Profiler profiler = MixinEnvironment.getProfiler();
    private static final String JAVA_LANG_OBJECT = "java/lang/Object";
    private static final Map<String, ClassInfo> cache = new HashMap<String, ClassInfo>();
    private static final ClassInfo OBJECT = new ClassInfo();
    private final String name;
    private final String superName;
    private final String outerName;
    private final boolean isProbablyStatic;
    private final Set<String> interfaces;
    private final Set<Method> methods;
    private final Set<Field> fields;
    private final Set<MixinInfo> mixins = new HashSet<MixinInfo>();
    private final Map<ClassInfo, ClassInfo> correspondingTypes = new HashMap<ClassInfo, ClassInfo>();
    private final MixinInfo mixin;
    private final MethodMapper methodMapper;
    private final boolean isMixin;
    private final boolean isInterface;
    private final int access;
    private ClassInfo superClass;
    private ClassInfo outerClass;
    private ClassSignature signature;

    private ClassInfo() {
        this.name = JAVA_LANG_OBJECT;
        this.superName = null;
        this.outerName = null;
        this.isProbablyStatic = true;
        this.methods = ImmutableSet.of((Object)new Method("getClass", "()Ljava/lang/Class;"), (Object)new Method("hashCode", "()I"), (Object)new Method("equals", "(Ljava/lang/Object;)Z"), (Object)new Method("clone", "()Ljava/lang/Object;"), (Object)new Method("toString", "()Ljava/lang/String;"), (Object)new Method("notify", "()V"), (Object[])new Method[]{new Method("notifyAll", "()V"), new Method("wait", "(J)V"), new Method("wait", "(JI)V"), new Method("wait", "()V"), new Method("finalize", "()V")});
        this.fields = Collections.emptySet();
        this.isInterface = false;
        this.interfaces = Collections.emptySet();
        this.access = 1;
        this.isMixin = false;
        this.mixin = null;
        this.methodMapper = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ClassInfo(ClassNode classNode) {
        Profiler.Section timer = profiler.begin(1, "class.meta");
        try {
            this.name = classNode.name;
            this.superName = classNode.superName != null ? classNode.superName : JAVA_LANG_OBJECT;
            this.methods = new HashSet<Method>();
            this.fields = new HashSet<Field>();
            this.isInterface = (classNode.access & 512) != 0;
            this.interfaces = new HashSet<String>();
            this.access = classNode.access;
            this.isMixin = classNode instanceof MixinInfo.MixinClassNode;
            this.mixin = this.isMixin ? ((MixinInfo.MixinClassNode)classNode).getMixin() : null;
            this.interfaces.addAll(classNode.interfaces);
            for (MethodNode method : classNode.methods) {
                this.addMethod(method, this.isMixin);
            }
            boolean isProbablyStatic = true;
            String outerName = classNode.outerClass;
            for (FieldNode field : classNode.fields) {
                if ((field.access & 4096) != 0 && field.name.startsWith("this$")) {
                    isProbablyStatic = false;
                    if (outerName == null && (outerName = field.desc) != null && outerName.startsWith("L")) {
                        outerName = outerName.substring(1, outerName.length() - 1);
                    }
                }
                this.fields.add(new Field(field, this.isMixin));
            }
            this.isProbablyStatic = isProbablyStatic;
            this.outerName = outerName;
            this.methodMapper = new MethodMapper(MixinEnvironment.getCurrentEnvironment(), this);
            this.signature = ClassSignature.ofLazy(classNode);
            return;
        }
        finally {
            timer.end();
        }
    }

    void addInterface(String iface) {
        this.interfaces.add(iface);
        this.getSignature().addInterface(iface);
    }

    void addMethod(MethodNode method) {
        this.addMethod(method, true);
    }

    private void addMethod(MethodNode method, boolean injected) {
        if (method.name.startsWith("<")) return;
        this.methods.add(new Method(method, injected));
    }

    void addMixin(MixinInfo mixin) {
        if (this.isMixin) {
            throw new IllegalArgumentException("Cannot add target " + this.name + " for " + mixin.getClassName() + " because the target is a mixin");
        }
        this.mixins.add(mixin);
    }

    public Set<MixinInfo> getMixins() {
        return Collections.unmodifiableSet(this.mixins);
    }

    public boolean isMixin() {
        return this.isMixin;
    }

    public boolean isPublic() {
        if ((this.access & 1) == 0) return false;
        return true;
    }

    public boolean isAbstract() {
        if ((this.access & 1024) == 0) return false;
        return true;
    }

    public boolean isSynthetic() {
        if ((this.access & 4096) == 0) return false;
        return true;
    }

    public boolean isProbablyStatic() {
        return this.isProbablyStatic;
    }

    public boolean isInner() {
        if (this.outerName == null) return false;
        return true;
    }

    public boolean isInterface() {
        return this.isInterface;
    }

    public Set<String> getInterfaces() {
        return Collections.unmodifiableSet(this.interfaces);
    }

    public String toString() {
        return this.name;
    }

    public MethodMapper getMethodMapper() {
        return this.methodMapper;
    }

    public int getAccess() {
        return this.access;
    }

    public String getName() {
        return this.name;
    }

    public String getClassName() {
        return this.name.replace('/', '.');
    }

    public String getSuperName() {
        return this.superName;
    }

    public ClassInfo getSuperClass() {
        if (this.superClass != null) return this.superClass;
        if (this.superName == null) return this.superClass;
        this.superClass = ClassInfo.forName(this.superName);
        return this.superClass;
    }

    public String getOuterName() {
        return this.outerName;
    }

    public ClassInfo getOuterClass() {
        if (this.outerClass != null) return this.outerClass;
        if (this.outerName == null) return this.outerClass;
        this.outerClass = ClassInfo.forName(this.outerName);
        return this.outerClass;
    }

    public ClassSignature getSignature() {
        return this.signature.wake();
    }

    List<ClassInfo> getTargets() {
        if (this.mixin == null) return ImmutableList.of(this);
        ArrayList<ClassInfo> targets = new ArrayList<ClassInfo>();
        targets.add(this);
        targets.addAll(this.mixin.getTargets());
        return targets;
    }

    public Set<Method> getMethods() {
        return Collections.unmodifiableSet(this.methods);
    }

    public Set<Method> getInterfaceMethods(boolean includeMixins) {
        HashSet<Method> methods = new HashSet<Method>();
        if (!this.isInterface) {
            for (ClassInfo supClass = this.addMethodsRecursive(methods, (boolean)includeMixins); supClass != null && supClass != OBJECT; supClass = supClass.addMethodsRecursive(methods, (boolean)includeMixins)) {
            }
        }
        Iterator it = methods.iterator();
        while (it.hasNext()) {
            if (((Method)it.next()).isAbstract()) continue;
            it.remove();
        }
        return Collections.unmodifiableSet(methods);
    }

    private ClassInfo addMethodsRecursive(Set<Method> methods, boolean includeMixins) {
        if (this.isInterface) {
            for (Method method : this.methods) {
                if (!method.isAbstract()) {
                    methods.remove(method);
                }
                methods.add(method);
            }
        } else if (!this.isMixin && includeMixins) {
            for (MixinInfo mixin : this.mixins) {
                mixin.getClassInfo().addMethodsRecursive(methods, includeMixins);
            }
        }
        Iterator<Object> iterator = this.interfaces.iterator();
        while (iterator.hasNext()) {
            String iface = (String)iterator.next();
            ClassInfo.forName(iface).addMethodsRecursive(methods, includeMixins);
        }
        return this.getSuperClass();
    }

    public boolean hasSuperClass(String superClass) {
        return this.hasSuperClass(superClass, Traversal.NONE);
    }

    public boolean hasSuperClass(String superClass, Traversal traversal) {
        if (JAVA_LANG_OBJECT.equals(superClass)) {
            return true;
        }
        if (this.findSuperClass(superClass, traversal) == null) return false;
        return true;
    }

    public boolean hasSuperClass(ClassInfo superClass) {
        return this.hasSuperClass(superClass, Traversal.NONE, false);
    }

    public boolean hasSuperClass(ClassInfo superClass, Traversal traversal) {
        return this.hasSuperClass(superClass, traversal, false);
    }

    public boolean hasSuperClass(ClassInfo superClass, Traversal traversal, boolean includeInterfaces) {
        if (OBJECT == superClass) {
            return true;
        }
        if (this.findSuperClass(superClass.name, traversal, includeInterfaces) == null) return false;
        return true;
    }

    public ClassInfo findSuperClass(String superClass) {
        return this.findSuperClass(superClass, Traversal.NONE);
    }

    public ClassInfo findSuperClass(String superClass, Traversal traversal) {
        return this.findSuperClass(superClass, traversal, false, new HashSet<String>());
    }

    public ClassInfo findSuperClass(String superClass, Traversal traversal, boolean includeInterfaces) {
        if (!ClassInfo.OBJECT.name.equals(superClass)) return this.findSuperClass(superClass, traversal, includeInterfaces, new HashSet<String>());
        return null;
    }

    /*
     * Unable to fully structure code
     */
    private ClassInfo findSuperClass(String superClass, Traversal traversal, boolean includeInterfaces, Set<String> traversed) {
        superClassInfo = this.getSuperClass();
        if (superClassInfo != null) {
            for (ClassInfo superTarget : superClassInfo.getTargets()) {
                if (superClass.equals(superTarget.getName())) {
                    return superClassInfo;
                }
                found = superTarget.findSuperClass(superClass, traversal.next(), includeInterfaces, traversed);
                if (found == null) continue;
                return found;
            }
        }
        if (includeInterfaces && (iface = this.findInterface(superClass)) != null) {
            return iface;
        }
        if (traversal.canTraverse() == false) return null;
        iface = this.mixins.iterator();
        do lbl-1000: // 3 sources:
        {
            if (iface.hasNext() == false) return null;
            mixin = (MixinInfo)iface.next();
            mixinClassName = mixin.getClassName();
            if (traversed.contains(mixinClassName)) ** GOTO lbl-1000
            traversed.add(mixinClassName);
            mixinClass = mixin.getClassInfo();
            if (!superClass.equals(mixinClass.getName())) continue;
            return mixinClass;
        } while ((targetSuper = mixinClass.findSuperClass(superClass, Traversal.ALL, includeInterfaces, traversed)) == null);
        return targetSuper;
    }

    private ClassInfo findInterface(String superClass) {
        ClassInfo superIface;
        ClassInfo iface;
        Iterator<String> iterator = this.getInterfaces().iterator();
        do {
            if (!iterator.hasNext()) return null;
            String ifaceName = iterator.next();
            iface = ClassInfo.forName(ifaceName);
            if (!superClass.equals(ifaceName)) continue;
            return iface;
        } while ((superIface = iface.findInterface(superClass)) == null);
        return superIface;
    }

    ClassInfo findCorrespondingType(ClassInfo mixin) {
        if (mixin == null) return null;
        if (!mixin.isMixin) return null;
        if (this.isMixin) {
            return null;
        }
        ClassInfo correspondingType = this.correspondingTypes.get(mixin);
        if (correspondingType != null) return correspondingType;
        correspondingType = this.findSuperTypeForMixin(mixin);
        this.correspondingTypes.put(mixin, correspondingType);
        return correspondingType;
    }

    private ClassInfo findSuperTypeForMixin(ClassInfo mixin) {
        ClassInfo superClass = this;
        while (superClass != null) {
            if (superClass == OBJECT) return null;
            for (MixinInfo minion : superClass.mixins) {
                if (!minion.getClassInfo().equals(mixin)) continue;
                return superClass;
            }
            superClass = superClass.getSuperClass();
        }
        return null;
    }

    public boolean hasMixinInHierarchy() {
        if (!this.isMixin) {
            return false;
        }
        ClassInfo supClass = this.getSuperClass();
        while (supClass != null) {
            if (supClass == OBJECT) return false;
            if (supClass.isMixin) {
                return true;
            }
            supClass = supClass.getSuperClass();
        }
        return false;
    }

    public boolean hasMixinTargetInHierarchy() {
        if (this.isMixin) {
            return false;
        }
        ClassInfo supClass = this.getSuperClass();
        while (supClass != null) {
            if (supClass == OBJECT) return false;
            if (supClass.mixins.size() > 0) {
                return true;
            }
            supClass = supClass.getSuperClass();
        }
        return false;
    }

    public Method findMethodInHierarchy(MethodNode method, SearchType searchType) {
        return this.findMethodInHierarchy(method.name, method.desc, searchType, Traversal.NONE);
    }

    public Method findMethodInHierarchy(MethodNode method, SearchType searchType, int flags) {
        return this.findMethodInHierarchy(method.name, method.desc, searchType, Traversal.NONE, flags);
    }

    public Method findMethodInHierarchy(MethodInsnNode method, SearchType searchType) {
        return this.findMethodInHierarchy(method.name, method.desc, searchType, Traversal.NONE);
    }

    public Method findMethodInHierarchy(MethodInsnNode method, SearchType searchType, int flags) {
        return this.findMethodInHierarchy(method.name, method.desc, searchType, Traversal.NONE, flags);
    }

    public Method findMethodInHierarchy(String name, String desc, SearchType searchType) {
        return this.findMethodInHierarchy(name, desc, searchType, Traversal.NONE);
    }

    public Method findMethodInHierarchy(String name, String desc, SearchType searchType, Traversal traversal) {
        return this.findMethodInHierarchy(name, desc, searchType, traversal, 0);
    }

    public Method findMethodInHierarchy(String name, String desc, SearchType searchType, Traversal traversal, int flags) {
        return (Method)this.findInHierarchy(name, desc, searchType, traversal, flags, Member.Type.METHOD);
    }

    public Field findFieldInHierarchy(FieldNode field, SearchType searchType) {
        return this.findFieldInHierarchy(field.name, field.desc, searchType, Traversal.NONE);
    }

    public Field findFieldInHierarchy(FieldNode field, SearchType searchType, int flags) {
        return this.findFieldInHierarchy(field.name, field.desc, searchType, Traversal.NONE, flags);
    }

    public Field findFieldInHierarchy(FieldInsnNode field, SearchType searchType) {
        return this.findFieldInHierarchy(field.name, field.desc, searchType, Traversal.NONE);
    }

    public Field findFieldInHierarchy(FieldInsnNode field, SearchType searchType, int flags) {
        return this.findFieldInHierarchy(field.name, field.desc, searchType, Traversal.NONE, flags);
    }

    public Field findFieldInHierarchy(String name, String desc, SearchType searchType) {
        return this.findFieldInHierarchy(name, desc, searchType, Traversal.NONE);
    }

    public Field findFieldInHierarchy(String name, String desc, SearchType searchType, Traversal traversal) {
        return this.findFieldInHierarchy(name, desc, searchType, traversal, 0);
    }

    public Field findFieldInHierarchy(String name, String desc, SearchType searchType, Traversal traversal, int flags) {
        return (Field)this.findInHierarchy(name, desc, searchType, traversal, flags, Member.Type.FIELD);
    }

    private <M extends Member> M findInHierarchy(String name, String desc, SearchType searchType, Traversal traversal, int flags, Member.Type type) {
        Object object;
        M member;
        block11 : {
            ClassInfo superClassInfo;
            if (searchType == SearchType.ALL_CLASSES) {
                M member2 = this.findMember(name, desc, flags, type);
                if (member2 != null) {
                    return member2;
                }
                if (traversal.canTraverse()) {
                    for (MixinInfo mixin : this.mixins) {
                        M mixinMember = mixin.getClassInfo().findMember(name, desc, flags, type);
                        if (mixinMember == null) continue;
                        return this.cloneMember(mixinMember);
                    }
                }
            }
            if ((superClassInfo = this.getSuperClass()) != null) {
                for (ClassInfo superTarget : superClassInfo.getTargets()) {
                    M member3 = superTarget.findInHierarchy(name, desc, SearchType.ALL_CLASSES, traversal.next(), flags & -3, type);
                    if (member3 == null) continue;
                    return member3;
                }
            }
            if (type != Member.Type.METHOD) return null;
            if (!this.isInterface) {
                if (!MixinEnvironment.getCompatibilityLevel().supportsMethodsInInterfaces()) return null;
            }
            Iterator<Object> iterator = this.interfaces.iterator();
            while (iterator.hasNext()) {
                String implemented = (String)iterator.next();
                ClassInfo iface = ClassInfo.forName(implemented);
                if (iface == null) {
                    logger.debug("Failed to resolve declared interface {} on {}", new Object[]{implemented, this.name});
                    continue;
                }
                member = iface.findInHierarchy(name, desc, SearchType.ALL_CLASSES, traversal.next(), flags & -3, type);
                if (member == null) {
                    continue;
                }
                break block11;
            }
            return null;
        }
        if (this.isInterface) {
            object = member;
            return object;
        }
        object = new InterfaceMethod((Member)member);
        return object;
    }

    private <M extends Member> M cloneMember(M member) {
        if (!(member instanceof Method)) return (M)new Field(member);
        return (M)new Method(member);
    }

    public Method findMethod(MethodNode method) {
        return this.findMethod(method.name, method.desc, method.access);
    }

    public Method findMethod(MethodNode method, int flags) {
        return this.findMethod(method.name, method.desc, flags);
    }

    public Method findMethod(MethodInsnNode method) {
        return this.findMethod(method.name, method.desc, 0);
    }

    public Method findMethod(MethodInsnNode method, int flags) {
        return this.findMethod(method.name, method.desc, flags);
    }

    public Method findMethod(String name, String desc, int flags) {
        return (Method)this.findMember(name, desc, flags, Member.Type.METHOD);
    }

    public Field findField(FieldNode field) {
        return this.findField(field.name, field.desc, field.access);
    }

    public Field findField(FieldInsnNode field, int flags) {
        return this.findField(field.name, field.desc, flags);
    }

    public Field findField(String name, String desc, int flags) {
        return (Field)this.findMember(name, desc, flags, Member.Type.FIELD);
    }

    private <M extends Member> M findMember(String name, String desc, int flags, Member.Type memberType) {
        Member member;
        Set<Member> members = memberType == Member.Type.METHOD ? this.methods : this.fields;
        Iterator<Member> iterator = members.iterator();
        do {
            if (!iterator.hasNext()) return null;
        } while (!(member = iterator.next()).equals(name, desc) || !member.matchesFlags(flags));
        return (M)member;
    }

    public boolean equals(Object other) {
        if (other instanceof ClassInfo) return ((ClassInfo)other).name.equals(this.name);
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    static ClassInfo fromClassNode(ClassNode classNode) {
        ClassInfo info = cache.get(classNode.name);
        if (info != null) return info;
        info = new ClassInfo(classNode);
        cache.put(classNode.name, info);
        return info;
    }

    public static ClassInfo forName(String className) {
        ClassInfo info = cache.get(className = className.replace('.', '/'));
        if (info != null) return info;
        try {
            ClassNode classNode = MixinService.getService().getBytecodeProvider().getClassNode(className);
            info = new ClassInfo(classNode);
        }
        catch (Exception ex) {
            logger.catching(Level.TRACE, (Throwable)ex);
            logger.warn("Error loading class: {} ({}: {})", new Object[]{className, ex.getClass().getName(), ex.getMessage()});
        }
        cache.put(className, info);
        logger.trace("Added class metadata for {} to metadata cache", new Object[]{className});
        return info;
    }

    public static ClassInfo forType(Type type) {
        if (type.getSort() == 9) {
            return ClassInfo.forType(type.getElementType());
        }
        if (type.getSort() >= 9) return ClassInfo.forName(type.getClassName().replace('.', '/'));
        return null;
    }

    public static ClassInfo getCommonSuperClass(String type1, String type2) {
        if (type1 == null) return OBJECT;
        if (type2 != null) return ClassInfo.getCommonSuperClass(ClassInfo.forName(type1), ClassInfo.forName(type2));
        return OBJECT;
    }

    public static ClassInfo getCommonSuperClass(Type type1, Type type2) {
        if (type1 == null) return OBJECT;
        if (type2 == null) return OBJECT;
        if (type1.getSort() != 10) return OBJECT;
        if (type2.getSort() == 10) return ClassInfo.getCommonSuperClass(ClassInfo.forType(type1), ClassInfo.forType(type2));
        return OBJECT;
    }

    private static ClassInfo getCommonSuperClass(ClassInfo type1, ClassInfo type2) {
        return ClassInfo.getCommonSuperClass(type1, type2, false);
    }

    public static ClassInfo getCommonSuperClassOrInterface(String type1, String type2) {
        if (type1 == null) return OBJECT;
        if (type2 != null) return ClassInfo.getCommonSuperClassOrInterface(ClassInfo.forName(type1), ClassInfo.forName(type2));
        return OBJECT;
    }

    public static ClassInfo getCommonSuperClassOrInterface(Type type1, Type type2) {
        if (type1 == null) return OBJECT;
        if (type2 == null) return OBJECT;
        if (type1.getSort() != 10) return OBJECT;
        if (type2.getSort() == 10) return ClassInfo.getCommonSuperClassOrInterface(ClassInfo.forType(type1), ClassInfo.forType(type2));
        return OBJECT;
    }

    public static ClassInfo getCommonSuperClassOrInterface(ClassInfo type1, ClassInfo type2) {
        return ClassInfo.getCommonSuperClass(type1, type2, true);
    }

    private static ClassInfo getCommonSuperClass(ClassInfo type1, ClassInfo type2, boolean includeInterfaces) {
        if (type1.hasSuperClass(type2, Traversal.NONE, includeInterfaces)) {
            return type2;
        }
        if (type2.hasSuperClass(type1, Traversal.NONE, includeInterfaces)) {
            return type1;
        }
        if (type1.isInterface()) return OBJECT;
        if (type2.isInterface()) {
            return OBJECT;
        }
        do {
            if ((type1 = type1.getSuperClass()) != null) continue;
            return OBJECT;
        } while (!type2.hasSuperClass(type1, Traversal.NONE, includeInterfaces));
        return type1;
    }

    static {
        cache.put(JAVA_LANG_OBJECT, OBJECT);
    }

    class Field
    extends Member {
        public Field(Member member) {
            super(member);
        }

        public Field(FieldNode field) {
            this(field, false);
        }

        public Field(FieldNode field, boolean injected) {
            super(Member.Type.FIELD, field.name, field.desc, field.access, injected);
            this.setUnique(Annotations.getVisible(field, Unique.class) != null);
            if (Annotations.getVisible(field, Shadow.class) == null) return;
            boolean decoratedFinal = Annotations.getVisible(field, Final.class) != null;
            boolean decoratedMutable = Annotations.getVisible(field, Mutable.class) != null;
            this.setDecoratedFinal(decoratedFinal, decoratedMutable);
        }

        public Field(String name, String desc, int access) {
            super(Member.Type.FIELD, name, desc, access, false);
        }

        public Field(String name, String desc, int access, boolean injected) {
            super(Member.Type.FIELD, name, desc, access, injected);
        }

        @Override
        public ClassInfo getOwner() {
            return ClassInfo.this;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Field) return super.equals(obj);
            return false;
        }

        @Override
        protected String getDisplayFormat() {
            return "%s:%s";
        }
    }

    public class InterfaceMethod
    extends Method {
        private final ClassInfo owner;

        public InterfaceMethod(Member member) {
            super(member);
            this.owner = member.getOwner();
        }

        @Override
        public ClassInfo getOwner() {
            return this.owner;
        }

        @Override
        public ClassInfo getImplementor() {
            return ClassInfo.this;
        }
    }

    public class Method
    extends Member {
        private final List<FrameData> frames;
        private boolean isAccessor;

        public Method(Member member) {
            super(member);
            this.frames = member instanceof Method ? ((Method)member).frames : null;
        }

        public Method(MethodNode method) {
            this(method, false);
            this.setUnique(Annotations.getVisible(method, Unique.class) != null);
            this.isAccessor = Annotations.getSingleVisible(method, Accessor.class, Invoker.class) != null;
        }

        public Method(MethodNode method, boolean injected) {
            super(Member.Type.METHOD, method.name, method.desc, method.access, injected);
            this.frames = this.gatherFrames(method);
            this.setUnique(Annotations.getVisible(method, Unique.class) != null);
            this.isAccessor = Annotations.getSingleVisible(method, Accessor.class, Invoker.class) != null;
        }

        public Method(String name, String desc) {
            super(Member.Type.METHOD, name, desc, 1, false);
            this.frames = null;
        }

        public Method(String name, String desc, int access) {
            super(Member.Type.METHOD, name, desc, access, false);
            this.frames = null;
        }

        public Method(String name, String desc, int access, boolean injected) {
            super(Member.Type.METHOD, name, desc, access, injected);
            this.frames = null;
        }

        private List<FrameData> gatherFrames(MethodNode method) {
            ArrayList<FrameData> frames = new ArrayList<FrameData>();
            ListIterator<AbstractInsnNode> iter = method.instructions.iterator();
            while (iter.hasNext()) {
                AbstractInsnNode insn = (AbstractInsnNode)iter.next();
                if (!(insn instanceof FrameNode)) continue;
                frames.add(new FrameData(method.instructions.indexOf(insn), (FrameNode)insn));
            }
            return frames;
        }

        public List<FrameData> getFrames() {
            return this.frames;
        }

        @Override
        public ClassInfo getOwner() {
            return ClassInfo.this;
        }

        public boolean isAccessor() {
            return this.isAccessor;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Method) return super.equals(obj);
            return false;
        }
    }

    static abstract class Member {
        private final Type type;
        private final String memberName;
        private final String memberDesc;
        private final boolean isInjected;
        private final int modifiers;
        private String currentName;
        private String currentDesc;
        private boolean decoratedFinal;
        private boolean decoratedMutable;
        private boolean unique;

        protected Member(Member member) {
            this(member.type, member.memberName, member.memberDesc, member.modifiers, member.isInjected);
            this.currentName = member.currentName;
            this.currentDesc = member.currentDesc;
            this.unique = member.unique;
        }

        protected Member(Type type, String name, String desc, int access) {
            this(type, name, desc, access, false);
        }

        protected Member(Type type, String name, String desc, int access, boolean injected) {
            this.type = type;
            this.memberName = name;
            this.memberDesc = desc;
            this.isInjected = injected;
            this.currentName = name;
            this.currentDesc = desc;
            this.modifiers = access;
        }

        public String getOriginalName() {
            return this.memberName;
        }

        public String getName() {
            return this.currentName;
        }

        public String getOriginalDesc() {
            return this.memberDesc;
        }

        public String getDesc() {
            return this.currentDesc;
        }

        public boolean isInjected() {
            return this.isInjected;
        }

        public boolean isRenamed() {
            if (this.currentName.equals(this.memberName)) return false;
            return true;
        }

        public boolean isRemapped() {
            if (this.currentDesc.equals(this.memberDesc)) return false;
            return true;
        }

        public boolean isPrivate() {
            if ((this.modifiers & 2) == 0) return false;
            return true;
        }

        public boolean isStatic() {
            if ((this.modifiers & 8) == 0) return false;
            return true;
        }

        public boolean isAbstract() {
            if ((this.modifiers & 1024) == 0) return false;
            return true;
        }

        public boolean isFinal() {
            if ((this.modifiers & 16) == 0) return false;
            return true;
        }

        public boolean isSynthetic() {
            if ((this.modifiers & 4096) == 0) return false;
            return true;
        }

        public boolean isUnique() {
            return this.unique;
        }

        public void setUnique(boolean unique) {
            this.unique = unique;
        }

        public boolean isDecoratedFinal() {
            return this.decoratedFinal;
        }

        public boolean isDecoratedMutable() {
            return this.decoratedMutable;
        }

        public void setDecoratedFinal(boolean decoratedFinal, boolean decoratedMutable) {
            this.decoratedFinal = decoratedFinal;
            this.decoratedMutable = decoratedMutable;
        }

        public boolean matchesFlags(int flags) {
            if (((~this.modifiers | flags & 2) & 2) == 0) return false;
            if (((~this.modifiers | flags & 8) & 8) == 0) return false;
            return true;
        }

        public abstract ClassInfo getOwner();

        public ClassInfo getImplementor() {
            return this.getOwner();
        }

        public int getAccess() {
            return this.modifiers;
        }

        public String renameTo(String name) {
            this.currentName = name;
            return name;
        }

        public String remapTo(String desc) {
            this.currentDesc = desc;
            return desc;
        }

        public boolean equals(String name, String desc) {
            if (!this.memberName.equals(name)) {
                if (!this.currentName.equals(name)) return false;
            }
            if (this.memberDesc.equals(desc)) return true;
            if (!this.currentDesc.equals(desc)) return false;
            return true;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Member)) {
                return false;
            }
            Member other = (Member)obj;
            if (!other.memberName.equals(this.memberName)) {
                if (!other.currentName.equals(this.currentName)) return false;
            }
            if (other.memberDesc.equals(this.memberDesc)) return true;
            if (!other.currentDesc.equals(this.currentDesc)) return false;
            return true;
        }

        public int hashCode() {
            return this.toString().hashCode();
        }

        public String toString() {
            return String.format(this.getDisplayFormat(), this.memberName, this.memberDesc);
        }

        protected String getDisplayFormat() {
            return "%s%s";
        }

        static enum Type {
            METHOD,
            FIELD;
            
        }

    }

    public static class FrameData {
        private static final String[] FRAMETYPES = new String[]{"NEW", "FULL", "APPEND", "CHOP", "SAME", "SAME1"};
        public final int index;
        public final int type;
        public final int locals;

        FrameData(int index, int type, int locals) {
            this.index = index;
            this.type = type;
            this.locals = locals;
        }

        FrameData(int index, FrameNode frameNode) {
            this.index = index;
            this.type = frameNode.type;
            this.locals = frameNode.local != null ? frameNode.local.size() : 0;
        }

        public String toString() {
            return String.format("FrameData[index=%d, type=%s, locals=%d]", this.index, FRAMETYPES[this.type + 1], this.locals);
        }
    }

    public static enum Traversal {
        NONE(null, false, SearchType.SUPER_CLASSES_ONLY),
        ALL(null, true, SearchType.ALL_CLASSES),
        IMMEDIATE(NONE, true, SearchType.SUPER_CLASSES_ONLY),
        SUPER(ALL, false, SearchType.SUPER_CLASSES_ONLY);
        
        private final Traversal next;
        private final boolean traverse;
        private final SearchType searchType;

        private Traversal(Traversal next, boolean traverse, SearchType searchType) {
            this.next = next != null ? next : this;
            this.traverse = traverse;
            this.searchType = searchType;
        }

        public Traversal next() {
            return this.next;
        }

        public boolean canTraverse() {
            return this.traverse;
        }

        public SearchType getSearchType() {
            return this.searchType;
        }
    }

    public static enum SearchType {
        ALL_CLASSES,
        SUPER_CLASSES_ONLY;
        
    }

}

