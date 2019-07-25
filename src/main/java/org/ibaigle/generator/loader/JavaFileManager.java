package org.ibaigle.generator.loader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.JavaFileObject.Kind;
import org.eclipse.jdt.internal.compiler.tool.EclipseFileManager;

public class JavaFileManager extends EclipseFileManager {
	//保存已经重新编译的class文件
	protected Map<String, byte[]> classBytes;

	public JavaFileManager() {
		super(null, null);
		classBytes = new HashMap<String, byte[]>();
	}

	public Map<String, byte[]> getClassBytes() {
		return classBytes;
	}

	@Override
	public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling)
			throws IOException {
		if (kind == Kind.CLASS) {
			return new JavaClassObject(className.replace('/', '.'));
		}
		return super.getJavaFileForOutput(location, className, kind, sibling);
	}

	/**
	 * 新建一个java源文件对象
	 *
	 * @author Nate
	 * @param name 文件名
	 * @param source java源码字符串
	 *
	 * @date 2013-11-21
	 */
	public static JavaSourceObject newJavaSourceObject(String name, String source) {
		return new JavaSourceObject(name, source);
	}

	/**
	 * java class文件对象(.class)
	 *
	 * @author Nate
	 * @date 2013-11-21
	 */
	public class JavaClassObject extends SimpleJavaFileObject {
		protected final String name;

		protected JavaClassObject(String name) {
			super(toURI(name), Kind.CLASS);
			this.name = name;
		}

		/**
		 * 编译器编译时回调， 这里我们用自己的输出流来接受编译后的class文件
		 */
		@Override
		public OutputStream openOutputStream() throws IOException {

			return new FilterOutputStream(new ByteArrayOutputStream()) {
				@Override
				public void close() throws IOException {
					out.close();
					ByteArrayOutputStream bos = (ByteArrayOutputStream)out;
					classBytes.put(name, bos.toByteArray());
				}
			};
		}
	}

	/**
	 * java 源文件对象(.java)
	 *
	 * @author Nate
	 * @date 2013-11-21
	 */
	public static class JavaSourceObject extends SimpleJavaFileObject {
		final String source;

		JavaSourceObject(String name, String source) {
			super(toURI(name), Kind.SOURCE);
			this.source = source;
		}

		@Override
		public CharBuffer getCharContent(boolean ignoreEncodingErrors) {
			return CharBuffer.wrap(source);
		}
	}

	static final String JAVA_FILE_EXT = ".java";
	static URI toURI(String name) {
		File file = new File(name);
		if (file.exists()) {
			return file.toURI();
		}
		try {
			final StringBuilder newUri = new StringBuilder();
			newUri.append("file:///");
			newUri.append(name.replace('.', '/'));
			if (name.endsWith(JAVA_FILE_EXT)) {
				newUri.replace(newUri.length() - JAVA_FILE_EXT.length(), newUri.length(), JAVA_FILE_EXT);
			}
			return URI.create(newUri.toString());
		} catch (Exception exp) {
			return null;
		}
	}

}