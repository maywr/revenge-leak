/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.mixin;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.CLASS)
public @interface Intrinsic {
    public boolean displace() default false;
}

