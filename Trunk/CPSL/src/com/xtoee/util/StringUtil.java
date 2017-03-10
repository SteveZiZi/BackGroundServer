package com.xtoee.util;

/**
 * 字符串辅助工具类
 * @author zgm
 *
 */
public class StringUtil
{
    /**
     * 判断指定字符串是否为空或长度为0
     * @param strValue  待检查的字符串
     * @return  如果字符串为空或长度为0，返回true
     */
    public static boolean IsNullOrEmpty(String strValue)
    {
        // 检查对象引用是否为空
        if (null == strValue)
        {
            return true;
        }
        
        // 检查字符串长度
        if (strValue.length() == 0)
        {
            return true;
        }
        
        return false;
    }
    
    /**
     * 检查指定的IP地址字符串是否有效
     * @param strIP IP地址字符串
     * @return  有效返回true
     */
    public static boolean IsValidIP(String strIP)
    {
        // 检查输入参数
        if ((null == strIP) || (strIP.length() < 7))
        {
            return false;
        }
        
        // 使用正则表达式验证IP地址
        String strRegex = "^(0|[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-5])\\.(0|[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-5])\\.(0|[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-5])\\.(0|[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-5])";
        return strIP.matches(strRegex);
    }
}
