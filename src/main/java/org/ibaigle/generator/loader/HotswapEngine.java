package org.ibaigle.generator.loader;

import org.ibaigle.generator.loader.JavaFileManager.JavaSourceObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.JavaCompiler.CompilationTask;
import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;

public class HotswapEngine {

	private final JavaCompiler compiler = new EclipseCompiler();

	public Class<?> reload(String classPath, String javaFilePath) {
		try {
			File javaFile = new File(javaFilePath);
			if (!javaFile.exists() || !javaFile.isFile())
				return null;
			//文件名
			String fileName = javaFile.getName();
			//完全类名
			String classFullName = javaFilePath.substring(classPath.length() + 1, javaFilePath.lastIndexOf('.'));
			classFullName = classFullName.replace('/', '.').replace('\\', '.');

			//编译选项
			List<String> options = new ArrayList<String>();
			options.add("-warn:-enumSwitch");
			options.add("-g");
			options.add("-deprecation");
			options.add("-1.7");
			options.add("-encoding");
			options.add("UTF-8");
			//options.add("-sourcepath");
			//options.add(classPath);
			options.add("-classpath");
			options.add(classPath);

			//java文件管理器
			JavaFileManager fileManager = new JavaFileManager();

			//编译单元集合
			List<JavaFileObject> compilationUnits = new ArrayList<JavaFileObject>(1);
			//加载java文件源码
			String sourceCode = loadJavaSourceCode(javaFile);
			//生成java源码对象
			JavaSourceObject sourceObj = JavaFileManager.newJavaSourceObject(fileName, sourceCode);
			compilationUnits.add(sourceObj);

			//执行编译任务
			CompilationTask task = compiler.getTask(null, fileManager, null, options, null, compilationUnits);
			if (!task.call()) {
				//编译失败
				return null;
			}

			//获取编译后的class文件
			Map<String, byte[]> classBytes = fileManager.getClassBytes();
			fileManager.close();

			//加载class文件
			HotswapClassLoader loader = new HotswapClassLoader(classBytes, classPath, ClassLoader.getSystemClassLoader());
			Class<?> clazz = loader.findClass(classFullName);
			return clazz;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String loadJavaSourceCode(File javaFile) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(javaFile)));
			char[] arr = new char[8 * 1024];
			StringBuilder buf = new StringBuilder();
			int numChars;
			while ((numChars = reader.read(arr, 0, arr.length)) > 0) {
				buf.append(arr, 0, numChars);
			}
			return buf.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}