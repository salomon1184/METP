/**
 * 
 */
package com.renren.uia.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 测试用例的运行命令
 * 
 * @author jiebo.wang Aug 19, 2013 10:38:04 AM
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

	/**
	 * 运行uiautomator测试用例的命令，不包含“adb shell”部分
	 */
	String value();
}
