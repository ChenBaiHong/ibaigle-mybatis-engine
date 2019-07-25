package org.ibaigle.generator;

import org.ibaigle.generator.basic.DataEntity;
import org.ibaigle.generator.basic.ResourcesPathEnv;
import org.ibaigle.generator.tools.CompilerUtil;
import org.ibaigle.generator.tools.GenEntityOracle;
import org.ibaigle.generator.tools.GenEntitySqlserver;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class IGenerateTest {

    @Test
    public void genZComponentXML() throws Exception {
        /*GenEntitySqlserver genEntitySqlserver = new GenEntitySqlserver(
                "jdbc:sqlserver://localhost\\DESKTOP-FFUL7C4:1433;database=infhis",
                 "sa",
                "root@123456",
                "org.yyzh.hos.sys",
                "baiHoo.chen");*/
        GenEntityOracle genEntitySqlserver = new GenEntityOracle(
                "jdbc:oracle:thin:@10.35.112.205:1521/HPOC",
                "lgpoc",
                "lgpoc123456",
                "org.baigle.bean",
                "baiHoo.chen");
        genEntitySqlserver.generate("IM_USER");
        HashMap<String,DataEntity> map = genEntitySqlserver.getNameEntityMap();
        List<Class> classList = new ArrayList<>();
        String currentDir = System.getProperty("user.dir");
        map.forEach((k,v)->{
            try{
                Class clazz = CompilerUtil.genJavaCompiler(currentDir,k);
                classList.add(clazz);
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        ResourcesPathEnv resourcesPathEnv = new ResourcesPathEnv(
                "org.baigle.hos.sys",
                "contr");
        resourcesPathEnv.setClasses(classList);
        resourcesPathEnv.setDataEntityMap(map);
        System.out.print(IGenerate.genZComponentXML(resourcesPathEnv));
    }
}
