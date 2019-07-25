package org.ibaigle.generator.basic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class JavaPathEnv {
    // 组目录
    public String groupDir;
    // 工件目录
    public String artifactDir;
    // web 层目录
    public String webDir = "contr";
    // 服务层
    public String serviceDir = "service";
    // 映射层
    public String mapperDir = "mapper";
    // 实体 注释
    public String beanContent;
    // 作者
    public String author;

    public JavaPathEnv(String groupDir, String artifactDir, String beanContent, String author) {
        this.groupDir = groupDir;
        this.artifactDir = artifactDir;
        this.beanContent = beanContent;
        this.author = author;
    }
}