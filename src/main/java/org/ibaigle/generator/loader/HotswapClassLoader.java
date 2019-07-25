package org.ibaigle.generator.loader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class HotswapClassLoader extends URLClassLoader {
	
	private final Map<String, byte[]> classBytes;
	
	public HotswapClassLoader(Map<String, byte[]> classBytes, String classPath, ClassLoader parent) {
		super(toURLs(classPath), parent);
		this.classBytes = classBytes;
	}

	@Override
	protected Class<?> findClass(String className) throws ClassNotFoundException {
		//先从我们编译后的class文件缓存里面找
		byte buf[] = classBytes.get(className);
		if (buf != null) {
			classBytes.put(className, null);
			return defineClass(className, buf, 0, buf.length);
		}
		return super.findClass(className);
	}
	
	private static URL[] toURLs(String classPath) {
		if (classPath == null) {
			return new URL[0];
		}
		List<URL> list = new ArrayList<URL>();
		StringTokenizer st = new StringTokenizer(classPath, File.pathSeparator);
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			File file = new File(token);
			if (file.exists()) {
				try {
					list.add(file.toURI().toURL());
				} catch (MalformedURLException mue) {}
			} else {
				try {
					list.add(new URL(token));
				} catch (MalformedURLException mue) {}
			}
		}
		
		URL res[] = new URL[list.size()];
		list.toArray(res);
		return res;
	}

}
