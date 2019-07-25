package org.ibaigle.generator.tools;

import org.ibaigle.generator.basic.DataEntity;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
public class GenEntityOracle {
	
	private static final GenEntityOracle INSTANCE = new GenEntityOracle();

    private String tableName;// 表名
    private String[] colNames; // 列名数组
    private String[] colTypes; // 列名类型数组
    private HashMap<String , String> colCommentMap;// 获取表和字段注释
    /*private HashMap<String , String> colNameAndTypeMap;*/ // 获取列和类型
    private HashMap<String , DataEntity> nameEntityMap = new HashMap<>();
    private String tableComment; //表注释
    private int[] colSizes; // 列名大小数组
    private boolean needUtil = false; // 是否需要导入包java.util.*
    private boolean needSql = false; // 是否需要导入包java.sql.*
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String SQL = "SELECT * FROM ";// 数据库操作

    // TODO 需要修改的地方
    private  String URL ="jdbc:oracle:thin:@10.35.112.205:1521/HPOC";
    private  String NAME = "lgpoc";
    private  String PASS = "lgpoc123456";
    private  String DRIVER ="oracle.jdbc.driver.OracleDriver";
    private String packageOutPath = "org.baigle.bean";// 指定实体生成所在包的路径
    private String authorName = "baiHoo";// 作者名字
 
   /**
    *  	
    * @param uRL
    * @param nAME
    * @param pASS
    * @param packageOutPath
    * @param authorName
    */
    public GenEntityOracle(String uRL, String nAME, String pASS, String packageOutPath, String authorName) {
		super();
		URL = uRL;
		NAME = nAME;
		PASS = pASS;
		this.packageOutPath = packageOutPath;
		this.authorName = authorName;
	}

	/**
     * 类的构造方法
     */
    private GenEntityOracle() {
    }

    /**
     * @return
     * @description 生成class的所有内容
     * @author baiHoo.chen
     * @date 2018年3月21日 上午9:16:17
     * @update 2018年3月21日 上午9:16:17 
     * @version V1.0
     */
    /**
     * @return
     * @description 生成class的所有内容
     * @author baiHoo.chen
     * @date 2018年3月21日 上午9:16:17
     * @update 2018年3月21日 上午9:16:17
     * @version V1.0
     */
    private String parse() {
        StringBuffer sb = new StringBuffer();
        sb.append("package " + packageOutPath + ";\r\n");
        sb.append("\r\n");

        sb.append("import lombok.*;\r\n");
        sb.append("import java.io.Serializable;\r\n");
        //sb.append("import org.ibaigle.generator.anno.BeanDescription;\r\n");
        // 判断是否导入工具包1
        if (needUtil) {
            sb.append("import java.util.Date;\r\n");
        }
        if (needSql) {
            sb.append("import java.sql.*;\r\n");
        }
        sb.append("\r\n");
        // 注释部分
        sb.append("/**\n" +
                " *  \n" +
                " * @ClassName "+ToolsUtil.getTransStr(tableName, true)+"\n" +
                " * @Description TODO "+tableComment+"\n" +
                " * @Author "+authorName+"\n" +
                " * @Date "+SDF.format(new Date())+"\n" +
                " */\n");
        //需要继承BaseBean，同目录已有
        sb.append("@Getter\r\n");
        sb.append("@Setter\r\n");
        sb.append("@NoArgsConstructor\r\n");
        sb.append("@AllArgsConstructor\r\n");
        //sb.append("@BeanDescription(\""+tableComment+"\")\r\n");
        sb.append("public class " + ToolsUtil.getTransStr(tableName, true) + "  implements Serializable {\r\n\r\n");
        processAllAttrs(sb);// 属性
        sb.append("\r\n");
        sb.append("}\r\n");
        return sb.toString();
    }

    public String getConvertUpperCase(String str) {
    	return str.toUpperCase();
    }
    /**
     * @param sb
     * @description 生成所有成员变量
     * @author baiHoo.chen
     * @date 2018年3月21日 上午9:16:17
     * @update 2018年3月21日 上午9:16:17 
     * @version V1.0
     */
    private void processAllAttrs(StringBuffer sb) {
        for (int i = 0; i < colNames.length; i++) {
            String colComment = colCommentMap.get(colNames[i]);
            String colName = colNames[i].toLowerCase();
            if(colComment!=null && !colComment.equals("")) {
                sb.append("\tprivate "
                        + sqlType2JavaType(colTypes[i])
                        + " " + ToolsUtil.getTransStr(colName, false)
                        + ";	//"+colComment+"\r\n");
            }else {
                sb.append("\tprivate "
                        + sqlType2JavaType(colTypes[i])
                        + " " + ToolsUtil.getTransStr(colName, false) + ";\r\n");
            }
        }
    }

    /**
     * @param sb
     * @description 生成所有get/set方法
     * @author baiHoo.chen
     * @date 2018年3月21日 上午9:16:17
     * @update 2018年3月21日 上午9:16:17 
     * @version V1.0
     */
    private void processAllMethod(StringBuffer sb) {
        for (int i = 0; i < colNames.length; i++) {
        	String colName = colNames[i].toLowerCase();
            sb.append("\tpublic void set" + getTransStr(colName, true) + "(" + sqlType2JavaType(colTypes[i]) + " "
                    + getTransStr(colName, false) + "){\r\n");
            sb.append("\t\tthis." + getTransStr(colName, false) + "=" + getTransStr(colName, false) + ";\r\n");
            sb.append("\t}\r\n");
            sb.append("\tpublic " + sqlType2JavaType(colTypes[i]) + " get" + getTransStr(colName, true) + "(){\r\n");
            sb.append("\t\treturn " + getTransStr(colName, false) + ";\r\n");
            sb.append("\t}\r\n");
        }
    }

    /**
     * @param str 传入字符串
     * @return
     * @description 将传入字符串的首字母转成大写
     * @author baiHoo.chen
     * @date 2018年3月21日 上午9:16:17
     * @update 2018年3月21日 上午9:16:17 
     * @version V1.0
     */
    private String initCap(String str) {
    	if(str!=null && !str.trim().equals("")) {
    		char[] ch = str.toCharArray();
            if (ch[0] >= 'a' && ch[0] <= 'z')
                ch[0] = (char) (ch[0] - 32);
            return new String(ch);
    	}else {
    		return "";
    	}
    }

    /**
     * @return
     * @description 将mysql中表名和字段名转换成驼峰形式
     * @author baiHoo.chen
     * @date 2018年3月21日 上午9:16:17
     * @update 2018年3月21日 上午9:16:17 
     * @version V1.0
     */
    private String getTransStr(String before, boolean firstChar2Upper) {
        //不带"_"的字符串,则直接首字母大写后返回
        if (!before.contains("_"))
            return firstChar2Upper ? initCap(before) : before;
        String[] strs = before.split("_");
        StringBuffer after = null;
        if (firstChar2Upper) {
            after = new StringBuffer(initCap(strs[0]));
        } else {
            after = new StringBuffer(strs[0]);
        }
        if (strs.length > 1) {
            for (int i=1; i<strs.length; i++)
                after.append(initCap(strs[i]));
        }
        return after.toString();
    }

    /**
     * @return
     * @description 查找sql字段类型所对应的Java类型
     * @author baiHoo.chen
     * @date 2018年3月21日 上午9:16:17
     * @update 2018年3月21日 上午9:16:17 
     * @version V1.0
     */
    private String sqlType2JavaType(String sqlType) {
        if(sqlType.equalsIgnoreCase("binary_double")){
            return "double";
        }else if(sqlType.equalsIgnoreCase("binary_float")){
            return "float";
        }else if(sqlType.equalsIgnoreCase("blob")){
            return "byte[]";
        }else if(sqlType.equalsIgnoreCase("blob")){
            return "byte[]";
        }else if(sqlType.equalsIgnoreCase("char")
                || sqlType.equalsIgnoreCase("nvarchar2")
                || sqlType.equalsIgnoreCase("varchar2")
                || sqlType.equalsIgnoreCase("varchar")){
            return "String";
        }else if(sqlType.equalsIgnoreCase("date")
                || sqlType.equalsIgnoreCase("timestamp")
                || sqlType.equalsIgnoreCase("datetime2")
                || sqlType.equalsIgnoreCase("timestamp with local time zone")
                || sqlType.equalsIgnoreCase("timestamp with time zone")){
            return "Date";
        }else if (sqlType.equalsIgnoreCase("integer")
                || sqlType.equalsIgnoreCase("int") ) {
            return "Integer";
        } else if (sqlType.equalsIgnoreCase("long")
                || sqlType.equalsIgnoreCase("bigint")) {
            return "Long";
        } else if (sqlType.equalsIgnoreCase("float")
                || sqlType.equalsIgnoreCase("float precision")
                || sqlType.equalsIgnoreCase("double")
                || sqlType.equalsIgnoreCase("double precision")
                || sqlType.equalsIgnoreCase("decimal")
                || sqlType.equalsIgnoreCase("bigDecimal")
        ) {
            return "Double";
        }
        return "String";
    }
   /**
    * 
    * @throws Exception
    */
   public  void generate(String ... tables) throws Exception {
       if(tables !=null && tables.length>0){
           Arrays.stream(tables).forEach(e->{
               try{
                   generateX(e);
               }catch (Exception ex){
                   ex.printStackTrace();
               }
           });
       }else{
           generateX(null);
       }
   }

    /**
     * 
     * @param TABLE_NAME 根据某个具体表名生成实体对象，表名必须是大写，否则数据库不识别
     * @throws Exception
     * @description 生成方法
     * @author baiHoo.chen
     * @date 2018年3月21日 上午9:16:17
     * @update 2018年3月21日 上午9:16:17 
     * @version V1.0
     */
    public void generateX(String TABLE_NAME) throws Exception {
        //与数据库的连接
        Connection con;
        PreparedStatement pStemt = null;
        Class.forName(DRIVER);
        con = DriverManager.getConnection(URL, NAME, PASS);
        System.out.println("connect database success...");
        
        if(TABLE_NAME!=null) {
        	//获取Oracle数据库的所有的表名
            pStemt = con.prepareStatement("SELECT TABLE_NAME FROM USER_TABLES WHERE TABLE_NAME = '"+TABLE_NAME+"'");
        }else {
        	//获取Oracle数据库的所有的表名
            pStemt = con.prepareStatement("SELECT TABLE_NAME FROM USER_TABLES");
        }
        
        ResultSet rs = pStemt.executeQuery();
        String tableSql;
        PrintWriter pw = null;
        DatabaseMetaData metaData = con.getMetaData();
        while (rs.next()) {
            // 获取数据实体详情
            DataEntity dataEntity = new DataEntity();

            tableName = rs.getString("TABLE_NAME");
            ResultSet pkRs = metaData.getPrimaryKeys(con.getCatalog(),null,tableName);

            Map<String , String> pkMap = new HashMap<>();
            while (pkRs.next()){
                pkRs.getString("COLUMN_NAME");
                pkMap.put(pkRs.getString("COLUMN_NAME"),pkRs.getString("COLUMN_NAME"));
            }

            dataEntity.setTableName(tableName);

            tableSql = SQL + tableName;
            pStemt = con.prepareStatement(tableSql);
            pStemt.executeQuery();
            ResultSetMetaData rsmd = pStemt.getMetaData();
            int size = rsmd.getColumnCount();
            colNames = new String[size];
            colTypes = new String[size];
            colSizes = new int[size];

            //获取所需的信息
            Map<String , DataEntity.ColumnField> columnFieldMap = new HashMap<>();
            for (int i = 0; i < size; i++) {
                colNames[i] = rsmd.getColumnName(i + 1);
                colTypes[i] = rsmd.getColumnTypeName(i + 1);
                if (colTypes[i].equalsIgnoreCase("date") || colTypes[i].equalsIgnoreCase("timestamp") ) {
                    needUtil = true;
                }
                if (colTypes[i].equalsIgnoreCase("image") || colTypes[i].equalsIgnoreCase("text")) {
                    needSql = true;
                }
                colSizes[i] = rsmd.getColumnDisplaySize(i + 1);

                // -----------------------------------------------------------------------------------------------------
                DataEntity.ColumnField columnField = new DataEntity.ColumnField();
                String fieldName = ToolsUtil.getTransStr(colNames[i], false);
                columnField.setCName(colNames[i]);
                columnField.setFName(fieldName);

                columnField.setSqlType(colTypes[i]);
                columnField.setJavaType(sqlType2JavaType(colTypes[i]));

                if(pkMap.get(colNames[i])!=null){
                    columnField.setPrimaryKey(true);
                }else{
                    columnField.setPrimaryKey(false);
                }

                columnFieldMap.put(fieldName,columnField);

            }
            //从数据库获取列注释 ，注意Oracle查询表，大小写敏感
            ResultSet rst = pStemt.executeQuery("SELECT COLUMN_NAME , COMMENTS FROM USER_COL_COMMENTS WHERE TABLE_NAME='"+tableName+"'");
            colCommentMap = new HashMap<String , String>();
            while (rst.next()) {
            	colCommentMap.put(rst.getString("COLUMN_NAME"), rst.getString("COMMENTS")==null?"":rst.getString("COMMENTS"));
            }
          //从数据库获取表注释
            ResultSet rst2 = pStemt.executeQuery("SELECT COMMENTS FROM USER_TAB_COMMENTS WHERE TABLE_NAME='"+tableName+"'");
            while (rst2.next()) {
                tableComment = rst2.getString("COMMENTS")==null?"":rst2.getString("COMMENTS");
                dataEntity.setDescription(tableComment);
            }

            //解析生成class的所有内容
            tableName = tableName.toLowerCase();//表名全部转化成小写，才能匹配下划线，做到首字母大写

            //解析生成class的所有内容
            tableName = tableName.toLowerCase();//表名全部转化成小写，才能匹配下划线，做到首字母大写
            String content = parse();
            dataEntity.setName(packageOutPath+"."+ToolsUtil.getTransStr(tableName, true));
            dataEntity.setSimpleName(ToolsUtil.getTransStr(tableName, true));
            dataEntity.setColumnFieldMap(columnFieldMap);
            nameEntityMap.put(packageOutPath+"."+ToolsUtil.getTransStr(tableName, true),dataEntity);


            //输出生成文件
            File directory = new File("");
            String outputPath = directory.getAbsolutePath() + "/src/main/java/" + packageOutPath.replace(".", "/");
            if(!new File(outputPath).exists()) {
            	new File(outputPath).mkdirs();
            }
            outputPath = outputPath + "/" + getTransStr(tableName, true) + ".java";
            FileWriter fw = new FileWriter(outputPath);
            pw = new PrintWriter(fw);
            pw.println(content);
            pw.flush();
            //System.out.println(""+getTransStr(tableName, true)+"  >>>>>  " + tableName);
            geSelectedSQL(true);
        }
        if (pw != null)
            pw.close();
    }
    private  void geSelectedSQL(boolean isAlisa) {
        StringBuffer sb = new StringBuffer();
        String cap = ToolsUtil.randDomGeneStr();
        sb.append("\r\n");
        sb.append("\t" +" SELECT " + "\r\n");
        for (int i = 0; i < colNames.length; i++) {
            String colName = colNames[i];
            colName = colName.toLowerCase();
            if(isAlisa){
                sb.append("\t\t " +cap+"."+colName+""+ " as "+ToolsUtil.getTransStr(colName, false)+",\r\n");
            }else{
                sb.append("\t\t " +cap+"."+colName+""+",\r\n");
            }
        }
        sb.append("\t" + "FROM " +tableName+" "+cap+ "\r\n");
        sb.append("\r\n");
        System.out.println(sb.toString());
    }

    public HashMap<String, DataEntity> getNameEntityMap() {
        return nameEntityMap;
    }
    /**
     * @param args
     * @description 执行方法
     * @author baiHoo.chen
     * @date 2018年3月21日 上午9:16:17
     * @update 2018年3月21日 上午9:16:17 
     * @version V1.0
     */
    public static void main(String[] args) {
        try {
            GenEntityOracle genEntitySqlserver = new GenEntityOracle(
                    "jdbc:oracle:thin:@10.15.2.11:1521:HPOC",
                    "LGPOC",
                    "LGPOC123456",
                    "org.baigle.bean",
                    "baiHoo.chen");
            genEntitySqlserver.generate("IM_USER");
            genEntitySqlserver.geSelectedSQL(false);
            System.out.println("generate classes success!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
