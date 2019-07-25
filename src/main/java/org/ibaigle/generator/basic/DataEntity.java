package org.ibaigle.generator.basic;



import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Data
public class DataEntity {

    private String simpleName;  // 数据库生成实体的名称
    private String name;        // 数据库生成实体的全名称
    private String tableName;   // 实体的表名称
    private String description; // 表说明

    private Map<String , ColumnField> columnFieldMap = new HashMap<>();

    @Getter
    @Setter
    public static class ColumnField{
        private String cName;
        private String sqlType;
        private String fName;
        private String javaType;
        private String description;
        private boolean isPrimaryKey = false;
    }
}
