package org.ibaigle.generator.basic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.ibaigle.generator.tools.ToolsUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
public class ResourcesPathEnv {

    // 组目录
    public String groupDir;
    // 工件目录
    public String artifactDir;
    // 映射层
    public String mapperDir = "mapper";
    // xml文件名
    public String mapperName = "ZComponentMapper";

    private List<Class> classes;

    private HashMap<String,DataEntity> dataEntityMap;


    public ResourcesPathEnv(String groupDir, String artifactDir) {
        this.groupDir = groupDir;
        this.artifactDir = artifactDir;
    }
    public ResourcesPathEnv(String groupDir, String artifactDir , String mapperName) {
        this(groupDir,artifactDir);
        this.mapperName = mapperName;
    }
}