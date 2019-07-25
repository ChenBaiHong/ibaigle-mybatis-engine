package org.ibaigle.generator;

import lombok.extern.slf4j.Slf4j;
import org.ibaigle.generator.basic.DataEntity;
import org.ibaigle.generator.basic.DataEntity.ColumnField;
import org.ibaigle.generator.basic.ResourcesPathEnv;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
@Slf4j
public class IGenerate {

    public static String genZComponentXML(ResourcesPathEnv resourcesPathEnv) throws NoSuchMethodException,
            SecurityException ,
            IllegalArgumentException {
        try {
            List<Class> classes = resourcesPathEnv.getClasses();
            HashMap<String,DataEntity> entityColTypeMap = resourcesPathEnv.getDataEntityMap();
            // 1. 定义 mapper 的命名空间
            StringBuffer namespace = new StringBuffer();
            namespace.append(resourcesPathEnv.groupDir).append(".");
            namespace.append(resourcesPathEnv.artifactDir).append(".");
            namespace.append(resourcesPathEnv.mapperDir).append(".");
            namespace.append(resourcesPathEnv.mapperName);
            // 2. 定义 mapper.xml 文件格式
            StringBuffer classMapperMap = new StringBuffer();
            classMapperMap.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<!DOCTYPE\n" +
                    "        mapper\n" +
                    "        PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\"\n" +
                    "        \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                    "<mapper namespace=\"" + namespace + "\">\n");
            classes.forEach(clazz -> {
                // 3. 如果包含内部类,不做内部类还有内部类添加
                Class[] innerClass = clazz.getDeclaredClasses();
                //BeanDescription beanDesc = (BeanDescription) clazz.getDeclaredAnnotation(BeanDescription.class);

                //Method method = null;
                DataEntity dataEntity = entityColTypeMap.get(clazz.getName());
                String description = dataEntity.getDescription();
                /*if(beanDesc !=null){
                    try {
                        method= beanDesc.getClass().getDeclaredMethod("value", new Class[]{});
                        description = (String) method.invoke(beanDesc, new Object[]{});
                    }catch (Exception e){
                        e.printStackTrace();
                        return;
                    }
                }else{
                    description = "TODO";
                }*/
                classMapperMap.append(
                        "    <!--" + description + "-->\n");
                if (innerClass != null && innerClass.length > 0) {
                    Arrays.stream(innerClass).forEach(e -> {
                        classMapperMap.append("\n");
                        classMapperMap.append("         <resultMap id=\"" + e.getSimpleName() + "Map\"\n");
                        classMapperMap.append("                     type=\"" + clazz.getName() + "$" + e.getSimpleName() + "\">\n");
                        Field[] fields = e.getDeclaredFields();
                        Arrays.stream(fields).forEach(field -> {
                            field.setAccessible(true);
                            classMapperMap.append("             <result column=\"" + field.getName() + "\" property=\"" + field.getName() + "\"/>\n");
                        });
                        classMapperMap.append("         </resultMap>\n");
                        classMapperMap.append("\n");
                    });
                }
                // 4.主类mapper映射添加
                classMapperMap.append("         <resultMap id=\"" + clazz.getSimpleName() + "Map\" type=\"" + clazz.getSimpleName() + "\">\n");
                // 4.1 获取字段名称
                StringBuffer argCols = new StringBuffer();
                Field[] fields = clazz.getDeclaredFields();
                Arrays.stream(fields).forEach(field-> {
                    field.setAccessible(true);
                    if (field.getGenericType() instanceof ParameterizedType) {
                        ParameterizedType pt = (ParameterizedType) field.getGenericType();
                        // 4.1.1 判断具体类的类型
                        if (pt.getRawType().equals(List.class)) {
                            // 4.1.1. 获取泛型类型
                            try {
                                String typeName = pt.getActualTypeArguments()[0].getTypeName();
                                String simpleName = Class.forName(typeName).getSimpleName();
                                classMapperMap.append("             <collection property=\"" + field.getName() + "\"\n");
                                classMapperMap.append("                         ofType=\"" + typeName + "\"\n");
                                classMapperMap.append("                         resultMap=\"" + simpleName + "Map\">\n");
                                classMapperMap.append("             </collection>\n");
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                                throw new RuntimeException();
                            }
                        }
                    } else {
                        ColumnField columnField = dataEntity.getColumnFieldMap().get(field.getName());
                        if(columnField.isPrimaryKey()){
                            classMapperMap.append("             <id column=\"" + columnField.getCName() + "\" property=\"" + field.getName() + "\"/>\n");
                            argCols.append("                <idArg column=\"" + columnField.getCName() + "\"" +
                                    " javaType=\""+field.getType().getName()+"\"" +
                                    " jdbcType=\""+columnField.getSqlType().toUpperCase()+"\" />\n");
                        }else{
                            classMapperMap.append("             <result column=\"" + columnField.getCName() + "\" property=\"" + field.getName() + "\"/>\n");
                            argCols.append("                <arg column=\"" + columnField.getCName() + "\"" +
                                    " javaType=\""+field.getType().getName()+"\"" +
                                    " jdbcType=\""+columnField.getSqlType().toUpperCase()+"\" />\n");
                        }
                    }
                });
                classMapperMap.append("         </resultMap>\n");
                // 4.主类mapper映射添加
                classMapperMap.append("         <resultMap id=\"" + clazz.getSimpleName() + "ConstructorMap\" type=\"" + clazz.getSimpleName() + "\">\n");
                classMapperMap.append("            <constructor>\n");
                classMapperMap.append(argCols);
                classMapperMap.append("            </constructor>\n");

                classMapperMap.append("         </resultMap>\n");
                classMapperMap.append("    <!-- //" + description + "-->\n");
            });
            classMapperMap.append("\n");
            classMapperMap.append("</mapper>");
            return classMapperMap.toString();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
