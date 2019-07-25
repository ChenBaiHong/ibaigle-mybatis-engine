package org.ibaigle.generator.loader2;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.JavaFileObject.Kind;

/**
 * {@link JavaFileManager}的一个实例，用于管理Java源代码和byte code。<br>
 * 所有的源码以{@link CharSequence}的形式保存在内存中，byte code以byte数组形式存放在内存中。
 */
final class FileManagerImpl extends ForwardingJavaFileManager<JavaFileManager> {
    private final ClassLoaderImpl classLoader;
    private final Map<URI, JavaFileObject> fileObjects = new HashMap<URI, JavaFileObject>();

    FileManagerImpl(JavaFileManager fileManager, ClassLoaderImpl classLoader) {
        super(fileManager);
        this.classLoader = classLoader;
    }

    @Override
    public ClassLoader getClassLoader(JavaFileManager.Location location) {
        return classLoader;
    }

    @Override
    public FileObject getFileForInput(Location location, String packageName, String relativeName)
            throws IOException {
        FileObject o = fileObjects.get(uri(location, packageName, relativeName));
        if (o != null) {
            return o;
        }
        return super.getFileForInput(location, packageName, relativeName);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String qualifiedName, Kind kind,
                                               FileObject outputFile) throws IOException {
        JavaFileObjectImpl file = new JavaFileObjectImpl(qualifiedName, kind);
        classLoader.add(qualifiedName, file);
        return file;
    }

    @Override
    public String inferBinaryName(Location loc, JavaFileObject file) {
        String result;
        if (file instanceof JavaFileObjectImpl) {
            result = file.getName();
        } else {
            result = super.inferBinaryName(loc, file);
        }
        return result;
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds,
                                         boolean recurse) throws IOException {
        Iterable<JavaFileObject> result = super.list(location, packageName, kinds, recurse);
        ArrayList<JavaFileObject> files = new ArrayList<JavaFileObject>();
        if ((location == StandardLocation.CLASS_PATH) && kinds.contains(JavaFileObject.Kind.CLASS)) {
            for (JavaFileObject file : fileObjects.values()) {
                if ((file.getKind() == Kind.CLASS) && file.getName().startsWith(packageName)) {
                    files.add(file);
                }
            }
            files.addAll(classLoader.files());
        } else if ((location == StandardLocation.SOURCE_PATH) && kinds.contains(JavaFileObject.Kind.SOURCE)) {
            for (JavaFileObject file : fileObjects.values()) {
                if ((file.getKind() == Kind.SOURCE) && file.getName().startsWith(packageName)) {
                    files.add(file);
                }
            }
        }
        for (JavaFileObject file : result) {
            files.add(file);
        }
        return files;
    }

    void putFileForInput(StandardLocation location, String packageName, String relativeName,
                         JavaFileObject file) {
        fileObjects.put(uri(location, packageName, relativeName), file);
    }

    private URI uri(Location location, String packageName, String relativeName) {
        return Utils.toURI(new StringBuilder(location.getName()).append('/').append(packageName).append('/')
                .append(relativeName).toString());
    }
}  