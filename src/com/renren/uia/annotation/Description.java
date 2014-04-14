package com.renren.uia.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Description {

	// TODO 时间戳
	/**
	 * 用例编写者
	 * 
	 * @return
	 */
	String author() default "";

	/**
	 * 测试用例的简单描述
	 * 
	 * @return
	 */
	String summary() default "";

	/**
	 * 被测应用的版本号
	 * 
	 * @return
	 */
	String version() default "";

	/**
	 * 测试用例运行的condition
	 * 
	 * @return
	 */
	String condition() default "";
}
