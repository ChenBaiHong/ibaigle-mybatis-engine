package org.ibaigle.generator.loader2.test;

import java.io.IOException;
import java.text.DecimalFormat;  
import java.util.Arrays;  
import java.util.Random;  
import javax.script.Bindings;  
import javax.script.Compilable;  
import javax.script.CompiledScript;  
import javax.script.ScriptEngine;  
import javax.script.ScriptEngineManager;  
import javax.script.ScriptException;  
import javax.script.SimpleBindings;  
import javax.tools.Diagnostic;  
import javax.tools.DiagnosticCollector;  
import javax.tools.JavaFileObject;  
import org.apache.commons.io.IOUtils;  
import org.apache.commons.lang.exception.ExceptionUtils;
import org.ibaigle.generator.loader2.CharSequenceCompiler;
import org.ibaigle.generator.loader2.CharSequenceCompilerException;
import org.ibaigle.generator.loader2.Utils;

public class ExpressionCal {  
    /** 包名前缀 */  
    private static final String PACKAGE_NAME = "javaxtools.compiler.test.runtime";  
    public static class Tester {  
        public static void main(String[] args) throws ScriptException {  
            //第一遍测试  
            test();  
            System.out.println("------Test Twice------/n");  
            test();  
        }  
        public static void test() throws ScriptException {  
            DecimalFormat df = new DecimalFormat("0.00");  
            int loop = 10 * 10000 - 1;  
            String exp = "x*x+x";  
            double d = new Random().nextDouble() * 100;  
            long start;  
            //直接计算  
            start = System.nanoTime();  
            for (int i = 0; i < loop; i++) {  
                @SuppressWarnings("unused")  
                double a = d * d + d;  
            }  
            System.out.printf(exp.replace("x", df.format(d)) + "=%2.4f/n", d * d + d);  
            System.out.printf("Time of direct cal %d loops: %10.2f微秒./n/n", loop + 1,  
                    (System.nanoTime() - start) / 1000d);  
            //编译源码并计算  
            start = System.nanoTime();  
            Function func = new ExpressionCal().newFunction(exp);  
            System.out.printf("Java src complain time: %10.2f微秒, /t", (System.nanoTime() - start) / 1000d);  
            start = System.nanoTime();  
            for (int i = 0; i < loop; i++) {  
                func.doFunction(d);  
            }  
            System.out.printf(exp.replace("x", df.format(d)) + "=%2.4f/n", func.doFunction(d));  
            System.out.printf("Complained source %d loops: %10.2f微秒./n/n", loop + 1,  
                    (System.nanoTime() - start) / 1000d);  
            //内置Javascript计算  
            start = System.nanoTime();  
            ScriptEngine se = new ScriptEngineManager().getEngineByName("ECMAScript");  
            CompiledScript script = ((Compilable) se).compile("var x;" + exp);  
            System.out.printf("JS complain time: %10.2f微秒, /t", (System.nanoTime() - start) / 1000d);  
            start = System.nanoTime();  
            Bindings binding = new SimpleBindings();  
            for (int i = 0; i < loop; i++) {  
                binding.put("x", d);  
                script.eval(binding);  
            }  
            binding.put("x", d);  
            System.out.printf(exp.replace("x", df.format(d)) + "=%2.4f/n", script.eval(binding));  
            System.out.printf("Javascript %d loops: %10.2f微秒./n", loop + 1,  
                    (System.nanoTime() - start) / 1000d);  
        }  
    }  
    /** 类名后缀 */  
    private int classNameSuffix = 0;  
    /** 随机数生成器，用于生成随机的包名和类名 */  
    private static final Random random = new Random();  
    /** 字符串形式的Java源文件内容 */  
    private String template;  
    private static final String TEMPLATE_NAME = "Function.java.template";  
    private static final String TARGET_VERSION = "1.6";  
    private final CharSequenceCompiler<Function> compiler = new CharSequenceCompiler<Function>(getClass()
            .getClassLoader(), Arrays.asList(new String[] { "-target", TARGET_VERSION, "-encoding",  
            Utils.ENCODING }));
    public Function newFunction(String expr) {  
        StringBuilder errStr = new StringBuilder();  
        Function result = null;  
        try {  
            //生成唯一的包名和类名  
            final String packageName = PACKAGE_NAME + digits();  
            final String className = "C_" + (classNameSuffix++) + digits();  
            final String qName = packageName + '.' + className;  
            //生成类的源码  
            final String source = fillTemplate(packageName, className, expr);  
            //编译源码  
            Class<Function> compiledFunction = compiler.compile(qName, source,  
                    new Class<?>[] { Function.class });  
            result = compiledFunction.newInstance();  
        } catch (CharSequenceCompilerException ex) {
            errStr.append(log(ex.getDiagnostics()));  
            ex.printStackTrace();  
        } catch (InstantiationException ex) {  
            errStr.append(ExceptionUtils.getFullStackTrace(ex)).append("/n");  
            ex.printStackTrace();  
        } catch (IllegalAccessException ex) {  
            errStr.append(ExceptionUtils.getFullStackTrace(ex)).append("/n");  
            ex.printStackTrace();  
        } catch (IOException ex) {  
            errStr.append(ExceptionUtils.getFullStackTrace(ex)).append("/n");  
            ex.printStackTrace();  
        }  
        if (errStr.toString().trim().length() > 0) {  
            System.err.println(errStr.toString());  
        }  
        return result;  
    }  
    /** @return 返回以'_'开头的随机16进制字符串 */  
    private String digits() {  
        return '_' + Long.toHexString(random.nextLong());  
    }  
    /** 
     * 生成字符串形式的java源文件内容 
     *  
     * @param packageName 
     *            包名 
     * @param className 
     *            类名 
     * @param expression 
     *            表达式 
     * @return 字符串形式的java源文件内容 
     * @throws IOException 
     */  
    private String fillTemplate(String packageName, String className, String expression) throws IOException {  
        if (template == null) {  
            template = IOUtils.toString(Function.class.getResourceAsStream(TEMPLATE_NAME), Utils.ENCODING);  
        }  
        String source = template.replace("$packageName", packageName)//  
                .replace("$className", className)//  
                .replace("$expression", expression);  
        return source;  
    }  
    /** 记录{@link DiagnosticCollector}中的错误内容 */  
    private CharSequence log(final DiagnosticCollector<JavaFileObject> diagnostics) {  
        final StringBuilder msgs = new StringBuilder();  
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {  
            msgs.append(diagnostic.getMessage(null)).append("/n");  
        }  
        return msgs;  
    }  
}  