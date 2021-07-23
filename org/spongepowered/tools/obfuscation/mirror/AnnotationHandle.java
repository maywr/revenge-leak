/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.tools.obfuscation.mirror;

import com.google.common.collect.ImmutableList;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;

public final class AnnotationHandle {
    public static final AnnotationHandle MISSING = new AnnotationHandle(null);
    private final AnnotationMirror annotation;

    private AnnotationHandle(AnnotationMirror annotation) {
        this.annotation = annotation;
    }

    public AnnotationMirror asMirror() {
        return this.annotation;
    }

    public boolean exists() {
        if (this.annotation == null) return false;
        return true;
    }

    public String toString() {
        if (this.annotation != null) return "@" + this.annotation.getAnnotationType().asElement().getSimpleName();
        return "@{UnknownAnnotation}";
    }

    public <T> T getValue(String key, T defaultValue) {
        Object object;
        if (this.annotation == null) {
            return defaultValue;
        }
        AnnotationValue value = this.getAnnotationValue(key);
        if (defaultValue instanceof Enum && value != null) {
            VariableElement varValue = (VariableElement)value.getValue();
            if (varValue != null) return (T)Enum.valueOf(defaultValue.getClass(), varValue.getSimpleName().toString());
            return defaultValue;
        }
        if (value != null) {
            object = value.getValue();
            return object;
        }
        object = defaultValue;
        return object;
    }

    public <T> T getValue() {
        return this.getValue("value", null);
    }

    public <T> T getValue(String key) {
        return this.getValue(key, null);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return this.getValue(key, defaultValue);
    }

    public AnnotationHandle getAnnotation(String key) {
        T value = this.getValue(key);
        if (value instanceof AnnotationMirror) {
            return AnnotationHandle.of((AnnotationMirror)value);
        }
        if (!(value instanceof AnnotationValue)) return null;
        Object mirror = ((AnnotationValue)value).getValue();
        if (!(mirror instanceof AnnotationMirror)) return null;
        return AnnotationHandle.of((AnnotationMirror)mirror);
    }

    public <T> List<T> getList() {
        return this.getList("value");
    }

    public <T> List<T> getList(String key) {
        List<AnnotationValue> list = this.getValue(key, Collections.emptyList());
        return AnnotationHandle.unwrapAnnotationValueList(list);
    }

    public List<AnnotationHandle> getAnnotationList(String key) {
        Object val = this.getValue(key, null);
        if (val == null) {
            return Collections.emptyList();
        }
        if (val instanceof AnnotationMirror) {
            return ImmutableList.of(AnnotationHandle.of(val));
        }
        List list = val;
        ArrayList<AnnotationHandle> annotations = new ArrayList<AnnotationHandle>(list.size());
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            AnnotationValue value = (AnnotationValue)iterator.next();
            annotations.add(new AnnotationHandle((AnnotationMirror)value.getValue()));
        }
        return Collections.unmodifiableList(annotations);
    }

    protected AnnotationValue getAnnotationValue(String key) {
        ExecutableElement elem;
        Iterator<? extends ExecutableElement> iterator = this.annotation.getElementValues().keySet().iterator();
        do {
            if (!iterator.hasNext()) return null;
        } while (!(elem = iterator.next()).getSimpleName().contentEquals(key));
        return this.annotation.getElementValues().get(elem);
    }

    protected static <T> List<T> unwrapAnnotationValueList(List<AnnotationValue> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        ArrayList<Object> unfolded = new ArrayList<Object>(list.size());
        Iterator<AnnotationValue> iterator = list.iterator();
        while (iterator.hasNext()) {
            AnnotationValue value = iterator.next();
            unfolded.add(value.getValue());
        }
        return unfolded;
    }

    protected static AnnotationMirror getAnnotation(Element elem, Class<? extends Annotation> annotationClass) {
        Element element;
        TypeElement annotationElement;
        AnnotationMirror annotation;
        if (elem == null) {
            return null;
        }
        List<? extends AnnotationMirror> annotations = elem.getAnnotationMirrors();
        if (annotations == null) {
            return null;
        }
        Iterator<? extends AnnotationMirror> iterator = annotations.iterator();
        do {
            if (!iterator.hasNext()) return null;
        } while (!((element = (annotation = iterator.next()).getAnnotationType().asElement()) instanceof TypeElement) || !(annotationElement = (TypeElement)element).getQualifiedName().contentEquals(annotationClass.getName()));
        return annotation;
    }

    public static AnnotationHandle of(AnnotationMirror annotation) {
        return new AnnotationHandle(annotation);
    }

    public static AnnotationHandle of(Element elem, Class<? extends Annotation> annotationClass) {
        return new AnnotationHandle(AnnotationHandle.getAnnotation(elem, annotationClass));
    }
}

