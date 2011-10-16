package com.ecyrd.simplejmx;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ManagedOperation
{
    String name() default "";
    String description() default "";
}
