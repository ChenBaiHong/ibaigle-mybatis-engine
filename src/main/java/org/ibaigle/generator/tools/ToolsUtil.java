package org.ibaigle.generator.tools;


import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class ToolsUtil {

    /**
     * @param str 传入字符串
     * @return
     * @description 将传入字符串的首字母转成大写
     * @author baiHoo.chen
     * @date 2018年3月21日 上午9:16:17
     * @update 2018年3月21日 上午9:16:17
     * @version V1.0
     */
    public static String initalCap(String str) {
        if (str != null && !str.trim().equals("")) {
            char[] ch = str.toCharArray();
            if (ch[0] >= 'a' && ch[0] <= 'z')
                ch[0] = (char) (ch[0] - 32);
            return new String(ch);
        } else {
            return "";
        }
    }
    /**
     * 将传入字符串的首字母转成小写
     *
     * @param str
     * @return
     */
    public static String initalLowercase(String str) {
        if (str != null && !str.trim().equals("")) {
            char[] ch = str.toCharArray();
            if (ch[0] >= 'A' && ch[0] <= 'Z')
                ch[0] = (char) (ch[0] + 32);
            return new String(ch);
        } else {
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
    public static String getTransStr(String before, boolean firstChar2Upper) {
        before = before.toLowerCase();
        //不带"_"的字符串,则直接首字母大写后返回
        if (!before.contains("_"))
            return firstChar2Upper ? initalCap(before) : before;
        String[] strs = before.split("_");
        StringBuffer after = null;
        if (firstChar2Upper) {
            after = new StringBuffer(initalCap(strs[0]));
        } else {
            after = new StringBuffer(strs[0]);
        }
        if (strs.length > 1) {

            for (int i=1; i<strs.length; i++)
                after.append(initalCap(strs[i]));
        }
        return after.toString();
    }
    /**
     * java生成指定范围的随机数
     * 要生成在[min,max]之间的随机整数，
     * @return
     */
    public static  String randDomGeneStr() {
        String myStr = "ABCDEFGHIJKLMNOPQRSTWUVXYZabcdefghijklmnopqrstwuvxyz";
        int max=myStr.length();
        int min=1;
        Random random = new Random();
        int s = random.nextInt(max)%(max-min+1) + min;
        System.out.println(s);
        return myStr.substring(s-1, s);

    }
}
