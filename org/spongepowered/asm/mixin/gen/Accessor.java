/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.mixin.gen;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Accessor {
    public String value() default "";

    public boolean remap() default true;
}

