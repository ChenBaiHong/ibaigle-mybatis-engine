package org.ibaigle.generator.tools;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.*;

public class CompilerUtil {
    private static String KEY_JAVA_FILE = "javaFile";
    private static String KEY_CLASSES_FILE = "classesFile";
    private static String classesPath = "/target/classes/";
    private static String javaPath = "/src/main/java/";
    public static Class<?> genJavaCompiler(String currentDir , String filename) throws Exception {

        try {
            // 1. 当前项目 目录
            currentDir = currentDir.replaceFirst("\\\\","/");
            // 2. 项目目录下的资源 Java 目录
            String srcJava = currentDir+javaPath;
            // 3. Java 目录下的Java文件
            String javaName=filename.replace(".","/");
            // 4. Java绝对文件路径
            String javaFile = srcJava+javaName+".java";

            // 5. 使用JavaCompiler 编译java文件
            JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileManager = jc.getStandardFileManager(null,
                    Locale.CHINESE,
                    Charset.forName("UTF-8"));
            Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjects(javaFile);

            List<String> options = new ArrayList<String>();
            options.addAll(Arrays.asList("-classpath", System.getProperty("java.class.path")));

            CompilationTask cTask = jc.getTask(null, fileManager, null, options, null, fileObjects);
            cTask.call();
            fileManager.close();

            // 6. 生成 class 文件的原始相对目录
            String dir = javaName.substring(0,javaName.lastIndexOf("/"));

            String srcJavaDir = srcJava+dir;
            Map<String, List<File>> allFiles = getFiles(srcJavaDir);
            List<File> javaFiles = allFiles.get(KEY_JAVA_FILE);
            List<File> classesFiles = allFiles.get(KEY_CLASSES_FILE);

            String targetClass = currentDir+classesPath;
            String targetClassesDir = targetClass+dir;
            FileUtil.mkDirs(targetClassesDir);
            moveOtherFiles(classesFiles,srcJavaDir, targetClassesDir);

            // 使用URLClassLoader加载classes中去
            URL[] urls = new URL[] { new URL("file:/" + targetClass) };
            URLClassLoader cLoader = new URLClassLoader(urls);
            Class<?> c = cLoader.loadClass(filename);
            cLoader.close();
            /*Class<?> c = Class.forName(filename);*/
            return c;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 复制文件到其他地方
     * @param files
     * @param src
     * @param dest
     * @throws Exception
     */
    private static void copyOtherFiles(List<File> files, String src, String dest) throws Exception{
        for (File file : files) {
            String fileAbsolutePath = file.getAbsolutePath();
            String destPath = dest + fileAbsolutePath.substring(src.length(), fileAbsolutePath.length());
            FileUtil.copy(fileAbsolutePath, destPath);
        }
    }

    /**
     * 移动文件到其他地方
     * @param files
     * @param src
     * @param dest
     * @throws Exception
     */
    private static void moveOtherFiles(List<File> files, String src, String dest) throws Exception{
        for (File file : files) {
            String fileAbsolutePath = file.getAbsolutePath();
            String destPath = dest + fileAbsolutePath.substring(src.length(), fileAbsolutePath.length());
            FileUtil.move(fileAbsolutePath, destPath);
        }
    }


    private static Map<String, List<File>> getFiles(String src) {
        List<File> files = FileUtil.getAllFiles(src);
        String suffix = ".java";
        String suffix2 = ".class";
        Map<String, List<File>> map = new HashMap<String, List<File>>();
        List<File> javaFile = new ArrayList<File>();
        List<File> classesFile = new ArrayList<File>();
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.endsWith(suffix)) {
                javaFile.add(file);
            } else if (fileName.endsWith(suffix2)){
                classesFile.add(file);
            }
        }
        map.put(KEY_JAVA_FILE, javaFile);
        map.put(KEY_CLASSES_FILE, classesFile);
        return map;
    }
}
