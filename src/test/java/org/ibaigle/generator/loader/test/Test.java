package org.ibaigle.generator.loader.test;

import org.ibaigle.generator.loader.HotswapEngine;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Test {

	/**
	 * @author Nate
	 * @date 2013-11-19
	 */
	public static void main(String[] args) throws Exception {
		//需要热加载的类路径
		final String classPath = System.getProperty("user.dir") + "/hotcodes";
		//需要热加载执行的类源文件路径
		final String javaFilePath = classPath + "/org/baiHoo/bean/AppMenu.java";

		HotswapEngine engine = new HotswapEngine();

		//第一次加载执行
		Class<?> clazz1 = engine.reload(classPath, javaFilePath);
		evalMainMethod(clazz1);

		System.err.println("==> 请修改文件，然后按任意键重新加载");
		System.in.read();

		//修改代代码后，再次加载执行
		Class<?> clazz2 = engine.reload(classPath, javaFilePath);
		evalMainMethod(clazz2);
	}
	
	/**
	 * 执行指定类的main方法
	 * 
	 * @author Nate
	 * @date 2013-11-19
	 */
	public static void evalMainMethod(Class<?> clazz, String...args) {
		try {
			Method mainMethod = clazz.getMethod("main",
					new Class[] { String[].class });
			
			if (mainMethod == null) return;
			
			int modifiers = mainMethod.getModifiers();
			if (!Modifier.isPublic(modifiers) ||
					!Modifier.isStatic(modifiers))
				return;
			
			mainMethod.invoke(null, new Object[]{args});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}