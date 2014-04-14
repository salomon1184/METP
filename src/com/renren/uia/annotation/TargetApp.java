package com.renren.uia.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TargetApp {
	/**
	 * 运行uiautomator测试用例的命令，不包含“adb shell”部分
	 */
	String value() default "";
}
