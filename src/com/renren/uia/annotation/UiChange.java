package com.renren.uia.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UiChange {

	/**
	 * 被测应用的版本号
	 * 
	 * @return
	 */
	String version() default "";

	/**
	 * UI 改动描述
	 */
	String change() default "";
}
