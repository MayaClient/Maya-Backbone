package com.codingforcookies.mayaclientapi.src;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MayaModule {
	String ID() default "";
	String name() default "";
	String description() default "";
	String version() default "";
	String creator() default "";
	String homepage() default "";
}