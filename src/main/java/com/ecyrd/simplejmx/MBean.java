package com.ecyrd.simplejmx;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MBean
{
    String description() default "";
    String name();
}
