package com.ecyrd.simplejmx;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ManagedAttribute
{
    String name() default "";
    String description() default "";
}
