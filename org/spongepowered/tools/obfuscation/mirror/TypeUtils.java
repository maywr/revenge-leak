/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.tools.obfuscation.mirror;

import java.util.Iterator;
import java.util.List;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import org.spongepowered.asm.util.SignaturePrinter;
import org.spongepowered.tools.obfuscation.mirror.Visibility;

public abstract class TypeUtils {
    private static final int MAX_GENERIC_RECURSION_DEPTH = 5;
    private static final String OBJECT_SIG = "java.lang.Object";
    private static final String OBJECT_REF = "java/lang/Object";

    private TypeUtils() {
    }

    public static PackageElement getPackage(TypeMirror type) {
        if (type instanceof DeclaredType) return TypeUtils.getPackage((TypeElement)((DeclaredType)type).asElement());
        return null;
    }

    public static PackageElement getPackage(TypeElement type) {
        Element parent = type.getEnclosingElement();
        while (parent != null) {
            if (parent instanceof PackageElement) return (PackageElement)parent;
            parent = parent.getEnclosingElement();
        }
        return (PackageElement)parent;
    }

    public static String getElementType(Element element) {
        if (element instanceof TypeElement) {
            return "TypeElement";
        }
        if (element instanceof ExecutableElement) {
            return "ExecutableElement";
        }
        if (element instanceof VariableElement) {
            return "VariableElement";
        }
        if (element instanceof PackageElement) {
            return "PackageElement";
        }
        if (!(element instanceof TypeParameterElement)) return element.getClass().getSimpleName();
        return "TypeParameterElement";
    }

    public static String stripGenerics(String type) {
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        int depth = 0;
        while (pos < type.length()) {
            char c = type.charAt(pos);
            if (c == '<') {
                ++depth;
            }
            if (depth == 0) {
                sb.append(c);
            } else if (c == '>') {
                --depth;
            }
            ++pos;
        }
        return sb.toString();
    }

    public static String getName(VariableElement field) {
        if (field == null) return null;
        String string = field.getSimpleName().toString();
        return string;
    }

    public static String getName(ExecutableElement method) {
        if (method == null) return null;
        String string = method.getSimpleName().toString();
        return string;
    }

    public static String getJavaSignature(Element element) {
        if (!(element instanceof ExecutableElement)) return TypeUtils.getTypeName(element.asType());
        ExecutableElement method = (ExecutableElement)element;
        StringBuilder desc = new StringBuilder().append("(");
        boolean extra = false;
        Iterator<? extends VariableElement> iterator = method.getParameters().iterator();
        do {
            if (!iterator.hasNext()) {
                desc.append(')').append(TypeUtils.getTypeName(method.getReturnType()));
                return desc.toString();
            }
            VariableElement arg = iterator.next();
            if (extra) {
                desc.append(',');
            }
            desc.append(TypeUtils.getTypeName(arg.asType()));
            extra = true;
        } while (true);
    }

    public static String getJavaSignature(String descriptor) {
        return new SignaturePrinter("", descriptor).setFullyQualified(true).toDescriptor();
    }

    public static String getTypeName(TypeMirror type) {
        switch (type.getKind()) {
            case ARRAY: {
                return TypeUtils.getTypeName(((ArrayType)type).getComponentType()) + "[]";
            }
            case DECLARED: {
                return TypeUtils.getTypeName((DeclaredType)type);
            }
            case TYPEVAR: {
                return TypeUtils.getTypeName(TypeUtils.getUpperBound(type));
            }
            case ERROR: {
                return OBJECT_SIG;
            }
        }
        return type.toString();
    }

    public static String getTypeName(DeclaredType type) {
        if (type != null) return TypeUtils.getInternalName((TypeElement)type.asElement()).replace('/', '.');
        return OBJECT_SIG;
    }

    public static String getDescriptor(Element element) {
        if (element instanceof ExecutableElement) {
            return TypeUtils.getDescriptor((ExecutableElement)element);
        }
        if (!(element instanceof VariableElement)) return TypeUtils.getInternalName(element.asType());
        return TypeUtils.getInternalName((VariableElement)element);
    }

    public static String getDescriptor(ExecutableElement method) {
        if (method == null) {
            return null;
        }
        StringBuilder signature = new StringBuilder();
        Iterator<? extends VariableElement> iterator = method.getParameters().iterator();
        do {
            if (!iterator.hasNext()) {
                String returnType = TypeUtils.getInternalName(method.getReturnType());
                return String.format("(%s)%s", signature, returnType);
            }
            VariableElement var = iterator.next();
            signature.append(TypeUtils.getInternalName(var));
        } while (true);
    }

    public static String getInternalName(VariableElement field) {
        if (field != null) return TypeUtils.getInternalName(field.asType());
        return null;
    }

    public static String getInternalName(TypeMirror type) {
        switch (1.$SwitchMap$javax$lang$model$type$TypeKind[type.getKind().ordinal()]) {
            case 1: {
                return "[" + TypeUtils.getInternalName(((ArrayType)type).getComponentType());
            }
            case 2: {
                return "L" + TypeUtils.getInternalName((DeclaredType)type) + ";";
            }
            case 3: {
                return "L" + TypeUtils.getInternalName(TypeUtils.getUpperBound(type)) + ";";
            }
            case 5: {
                return "Z";
            }
            case 6: {
                return "B";
            }
            case 7: {
                return "C";
            }
            case 8: {
                return "D";
            }
            case 9: {
                return "F";
            }
            case 10: {
                return "I";
            }
            case 11: {
                return "J";
            }
            case 12: {
                return "S";
            }
            case 13: {
                return "V";
            }
            case 4: {
                return "Ljava/lang/Object;";
            }
        }
        throw new IllegalArgumentException("Unable to parse type symbol " + type + " with " + (Object)((Object)type.getKind()) + " to equivalent bytecode type");
    }

    public static String getInternalName(DeclaredType type) {
        if (type != null) return TypeUtils.getInternalName((TypeElement)type.asElement());
        return OBJECT_REF;
    }

    public static String getInternalName(TypeElement element) {
        if (element == null) {
            return null;
        }
        StringBuilder reference = new StringBuilder();
        reference.append(element.getSimpleName());
        Element parent = element.getEnclosingElement();
        while (parent != null) {
            if (parent instanceof TypeElement) {
                reference.insert(0, "$").insert(0, parent.getSimpleName());
            } else if (parent instanceof PackageElement) {
                reference.insert(0, "/").insert(0, ((PackageElement)parent).getQualifiedName().toString().replace('.', '/'));
            }
            parent = parent.getEnclosingElement();
        }
        return reference.toString();
    }

    private static DeclaredType getUpperBound(TypeMirror type) {
        try {
            return TypeUtils.getUpperBound0(type, 5);
        }
        catch (IllegalStateException ex) {
            throw new IllegalArgumentException("Type symbol \"" + type + "\" is too complex", ex);
        }
        catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unable to compute upper bound of type symbol " + type, ex);
        }
    }

    private static DeclaredType getUpperBound0(TypeMirror type, int depth) {
        if (depth == 0) {
            throw new IllegalStateException("Generic symbol \"" + type + "\" is too complex, exceeded " + 5 + " iterations attempting to determine upper bound");
        }
        if (type instanceof DeclaredType) {
            return (DeclaredType)type;
        }
        if (!(type instanceof TypeVariable)) return null;
        try {
            TypeMirror upper = ((TypeVariable)type).getUpperBound();
            return TypeUtils.getUpperBound0(upper, --depth);
        }
        catch (IllegalStateException ex) {
            throw ex;
        }
        catch (IllegalArgumentException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("Unable to compute upper bound of type symbol " + type);
        }
    }

    public static boolean isAssignable(ProcessingEnvironment processingEnv, TypeMirror targetType, TypeMirror superClass) {
        boolean assignable = processingEnv.getTypeUtils().isAssignable(targetType, superClass);
        if (assignable) return assignable;
        if (!(targetType instanceof DeclaredType)) return assignable;
        if (!(superClass instanceof DeclaredType)) return assignable;
        TypeMirror rawTargetType = TypeUtils.toRawType(processingEnv, (DeclaredType)targetType);
        TypeMirror rawSuperType = TypeUtils.toRawType(processingEnv, (DeclaredType)superClass);
        return processingEnv.getTypeUtils().isAssignable(rawTargetType, rawSuperType);
    }

    private static TypeMirror toRawType(ProcessingEnvironment processingEnv, DeclaredType targetType) {
        return processingEnv.getElementUtils().getTypeElement(((TypeElement)targetType.asElement()).getQualifiedName()).asType();
    }

    /*
     * Exception decompiling
     */
    public static Visibility getVisibility(Element element) {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:404)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:482)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:607)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:696)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:184)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:129)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:397)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:906)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:797)
        // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:225)
        // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
        // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
        // org.benf.cfr.reader.Main.main(Main.java:48)
        // the.bytecode.club.bytecodeviewer.decompilers.CFRDecompiler.decompileToZip(CFRDecompiler.java:311)
        // the.bytecode.club.bytecodeviewer.gui.MainViewerGUI$14$1$7.run(MainViewerGUI.java:1287)
        throw new IllegalStateException("Decompilation failed");
    }

}

