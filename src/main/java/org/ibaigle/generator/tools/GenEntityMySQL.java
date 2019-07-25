package org.ibaigle.generator.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;

public class GenEntityMySQL {
	
	private static final GenEntityMySQL INSTANCE = new GenEntityMySQL();
	
    private String tableName;// 表名
    private String[] colNames; // 列名数组
    private String[] colTypes; // 列名类型数组
    private HashMap<String , String> colCommentMap;// 获取表和字段注释
    private String tableComment; //表注释
    private boolean needUtil = false; // 是否需要导入包java.util.*
    private boolean needSql = false; // 是否需要导入包java.sql.*
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // TODO 需要修改的地方
    private  String URL ="jdbc:mysql://192.168.1.15:3306/MybatisStuding";    
    private  String NAME = "root";    
    private  String PASS = "root";    
    private  String DRIVER ="com.mysql.jdbc.Driver";   
    private String packageOutPath = "org.baiHoo.bean";// 指定实体生成所在包的路径
    private String authorName = "baiHoo.chen";// 作者名字
 
   /**
    *  	
    * @param uRL
    * @param nAME
    * @param pASS
    * @param packageOutPath
    * @param authorName
    */
    public GenEntityMySQL(String uRL, String nAME, String pASS, String packageOutPath, String authorName) {
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
    private GenEntityMySQL() {
    }

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
        // 判断是否导入工具包
        if (needUtil) {
            sb.append("import java.util.Date;\r\n");
        }
        if (needSql) {
            sb.append("import java.sql.*;\r\n");
        }
        // 注释部分
		sb.append("/**\r\n");
		sb.append(" *\r\n");
		sb.append(" *\r\n");
		sb.append(" * <p> 项目名称：baiHoo 出品系列</p>\r\n");
		sb.append(" * <p>表名称: " + tableName + " </p>\n");
		sb.append(" * <p>表说明: \r\n");
		sb.append(" *\r\n");
		sb.append(" *		"+tableComment+"\r\n");
		sb.append(" *\r\n");
		sb.append(" * </p>\r\n");
		sb.append(" * <p>类名称: "+getTransStr(tableName, true)+".java </p>\r\n");
		sb.append(" * <p>类说明: \r\n");
		sb.append(" *\r\n");
		sb.append(" *\r\n");
		sb.append(" * </p>\r\n");
		sb.append(" *\r\n");
		sb.append(" * @author " + authorName + "\r\n");
		sb.append(" * @date " + SDF.format(new Date()) + "\r\n");
		sb.append(" */\r\n");
		// 实体部分
		//sb.append("@MyTableName(tableName = \""+getConvertUpperCase(tableName)+"\")\r\n");
		//需要继承BaseBean，同目录已有
		//sb.append("public class " + getTransStr(tableName, true) + "  extends BaseBean {\r\n\r\n");
		sb.append("public class " + getTransStr(tableName, true) + " {\r\n\r\n");
		processAllTableColumn( sb);//头注释
		//processAllTableColumn3( sb);
		processAllAttrs(sb);// 属性
		sb.append("\r\n");
		processAllMethod(sb);// get set方法
		sb.append("}\r\n");
		return sb.toString();
    }

    public String getConvertUpperCase(String str) {
    	return str.toUpperCase();
    }
   /* 
    private void processAllTableColumn3(StringBuffer sb) {
       
    	String cap = randDomGeneStr();
    	sb.append("\r\n");
		sb.append("\t" +" SELECT " + "\r\n");
		for (int i = 0; i < colNames.length; i++) {
			String colName = colNames[i];
			sb.append("\t\t " +cap+"."+colName+","+ "\r\n");
		}
		sb.append("\t" + "FROM " +tableName+" "+cap+ "\r\n");
		sb.append("\r\n");
	}
    
	private void processAllTableColumn2(StringBuffer sb) {
		sb.append("\r\n");
		sb.append("\t" + "<resultMap type=\"" + getTransStr(tableName, true) + "\" id=\""
				+ getTransStr(tableName, true) + "ResultMap\">" + "\r\n");
		for (int i = 0; i < colNames.length; i++) {
			String colName = colNames[i];
			sb.append("\t " + "<result column=\"" + colName + "\" property=\"" + getTransStr(colName, false) + "\"/>"
					+ "\r\n");
		}
		sb.append("\t" + "</resultMap>" + "\r\n");
		sb.append("\r\n");
	}
	*/
	private void processAllTableColumn(StringBuffer sb) {
		sb.append("\t/**" + "\r\n");
		sb.append("\t *" + "\r\n");
		for (int i = 0; i < colNames.length; i++) {
			String colName = colNames[i].toLowerCase();
			String colComment = colCommentMap.get(colNames[i]);
			if (colComment != null && !colComment.equals("")) {
				sb.append("\t * @param " + colName + "		");
				sb.append(colComment + "\r\n");
			} else {
				sb.append("\t * @param " + colName + "		" + "\r\n");
			}
		}
		sb.append("\t */" + "\r\n");
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
        		//sb.append("\t@MyFieldName(fieldName = \""+getConvertUpperCase(colName)+"\")\r\n");
        		sb.append("\tprivate " + sqlType2JavaType(colTypes[i]) + " " + getTransStr(colName, false) + ";	//"+colComment+"\r\n");
        	}else {
        		//sb.append("\t@MyFieldName(fieldName = \""+getConvertUpperCase(colName)+"\")\r\n");
        		sb.append("\tprivate " + sqlType2JavaType(colTypes[i]) + " " + getTransStr(colName, false) + ";\r\n");
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
        }else if(sqlType.equalsIgnoreCase("char") || sqlType.equalsIgnoreCase("nvarchar2")     
                || sqlType.equalsIgnoreCase("varchar2")){    
            return "String";    
        }else if(sqlType.equalsIgnoreCase("date") || sqlType.equalsIgnoreCase("timestamp")    
                 || sqlType.equalsIgnoreCase("timestamp with local time zone")     
                 || sqlType.equalsIgnoreCase("timestamp with time zone")){    
            return "Date";    
        }else if(sqlType.equalsIgnoreCase("number")){    
        	return "String";
        	//return "Long";    
        }else if (sqlType.equalsIgnoreCase("integer") || sqlType.equalsIgnoreCase("int")) {
            return "Integer";
        } else if (sqlType.equalsIgnoreCase("long") || sqlType.equalsIgnoreCase("long")) {
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
    public  void generate( ) throws Exception {
    	generate(null);
    }
    /**
     * 
     * @description 生成方法
     * @author baiHoo.chen
     * @date 2018年3月21日 上午9:16:17
     * @update 2018年3月21日 上午9:16:17 
     * @version V1.0
     */
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
    public void generate(String TABLE_NAME) throws Exception {
        //与数据库的连接
        Connection con;
        Class.forName(DRIVER);
        con = DriverManager.getConnection(URL, NAME, PASS);
        System.out.println("connect database success...");
        //获取MySQL数据表元数据
        DatabaseMetaData metaData = con.getMetaData();
        //表结果集
        ResultSet rsTable = null;
        if(TABLE_NAME!=null) {
        	//获取MySQL数据库指定的表
        	rsTable = metaData.getTables(null, "%", TABLE_NAME, new String[] { "TABLE" });
        }else {
        	//获取MySQL数据库的所有的表名
        	rsTable = metaData.getTables(null, "%", null, new String[] { "TABLE" });
        }
        PrintWriter pw = null;
        while (rsTable.next()) {
        	//从数据库获取表名称
            tableName = rsTable.getString("TABLE_NAME");
            //从数据库获取表注释
            tableComment = rsTable.getString("REMARKS"); 
            //获取MySQL数据库表的列名
            ResultSet rsColumn= metaData.getColumns(null, "%", tableName, "%");
            List<String> columnNames = new ArrayList<String>();
            List<String> columnTypes = new ArrayList<String>();
            colCommentMap = new HashMap<String , String>();
            
            while(rsColumn.next()){
            	//从数据库获取表的列名
				String columnName = rsColumn.getString("COLUMN_NAME");
				columnNames.add(columnName);
				//获取MySQL数据库表列的类型
				String columnType = rsColumn.getString("TYPE_NAME");
				columnTypes.add(columnType);
				 //获取MySQL数据库表列列的注释 
				String remarks = rsColumn.getString("REMARKS");
				colCommentMap.put(columnName, remarks ==null?"":remarks);
			}
            int size = columnTypes.size();
            colNames = new String[size];
            colNames =  columnNames.toArray(colNames);
            colTypes = new String[size];
            colTypes = columnTypes.toArray(colTypes);
            //解析生成class的所有内容
            tableName = tableName.toLowerCase();//表名全部转化成小写，才能匹配下划线，做到首字母大写
            String content = parse();
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
            System.out.println("create class >>>>> " + tableName);
        }
        if (pw != null)
            pw.close();
    }
    /**
     * java生成指定范围的随机数
     * 要生成在[min,max]之间的随机整数，
     * @return
     */
    private String randDomGeneStr() {
    	String myStr = "ABCDEFGHIJKLMNOPQRSTWUVXYZabcdefghijklmnopqrstwuvxyz";
    	int max=myStr.length();
        int min=1;
        Random random = new Random();
        int s = random.nextInt(max)%(max-min+1) + min;
        System.out.println(s);
        return myStr.substring(s-1, s);
        
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
            INSTANCE.generate();
           System.out.println("generate classes success!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
