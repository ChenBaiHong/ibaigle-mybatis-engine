package org.ibaigle.generator.loader2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;

/**
 * {@link FileObject}和{@link JavaFileObject}的一个实现，它能持有java源代码或编译后的class。这个类可以用于：
 * <ol>
 * <li>存放需要传递给编译器的源码，这时使用的是{@link JavaFileObjectImpl#JavaFileObjectImpl(String, CharSequence)}构造器。</li>
 * <li>存放编译器编译完的byte code，这是使用的是{@link JavaFileObjectImpl#JavaFileObjectImpl(String, JavaFileObject.Kind)}</li>
 * </ol>
 */
final class JavaFileObjectImpl extends SimpleJavaFileObject {
    /**
     * 如果kind == CLASS, 存储byte code，可以通过{@link #openInputStream()}得到
     */
    private ByteArrayOutputStream byteCode;
    /**
     * 如果kind == SOURCE, 存储源码
     */
    private final CharSequence source;

    /**
     * 创建持有源码的实例
     *
     * @param baseName the base name
     * @param source   the source code
     */
    JavaFileObjectImpl(final String baseName, final CharSequence source) {
        super(Utils.toURI(baseName + ".java"), Kind.SOURCE);
        this.source = source;
    }

    /**
     * 创建持有二进制byte code的实例
     *
     * @param name the file name
     * @param kind the kind of file
     */
    JavaFileObjectImpl(final String name, final Kind kind) {
        super(Utils.toURI(name), kind);
        source = null;
    }

    @Override
    public CharSequence getCharContent(final boolean ignoreEncodingErrors)
            throws UnsupportedOperationException {
        if (source == null) {
            throw new UnsupportedOperationException("getCharContent()");
        }
        return source;
    }

    @Override
    public InputStream openInputStream() {
        return new ByteArrayInputStream(byteCode.toByteArray());
    }

    @Override
    public OutputStream openOutputStream() {
        return (byteCode = new ByteArrayOutputStream());
    }

    @Override
    public Writer openWriter() throws IOException {
        return new OutputStreamWriter(openOutputStream(), Utils.ENCODING);
    }
}  