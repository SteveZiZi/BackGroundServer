package com.xtoee.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期转换类
 * @author zgm
 *
 */
public class DateUtil
{
    public static final String      yyMMdd = "yy-MM-dd";        // 短日期格式
    public static final String      yyyyMMdd = "yyyy-MM-dd";    // 长日期格式
    public static final String      HHmmss = "HH:mm:ss";        // 时间格式
    public static final String      yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss"; // 长日期时间格式
    public static final String      yyMMddHHmmss = "yy-MM-dd HH:mm:ss";     // 短日期时间格式

    
    /**
     * 字符串转换成日期时间
     * @param s         字符串
     * @param style     日期格式
     * @return  日期时间
     */
    public static Date parseToDate(String s, String style)
    {
        // 实例化日期格式化类
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        // 提交转换格式
        simpleDateFormat.applyPattern(style); 
        
        // 字符串为空或长度小于8
        if (s == null || s.length() < 8) 
        {
            return null;
        }
        
        Date date = null;
        try
        {
            // 进行转换
            date = simpleDateFormat.parse(s); 
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        
        return date;
    }

    /**
     * 格式化日期字符串
     * @param s     时间字符串
     * @param style 日期格式
     * @return  时间字符串
     */
    public static String parseToString(String s, String style)
    {
        // 实例化日期格式化类
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(); 
        // 提交转换格式
        simpleDateFormat.applyPattern(style); 
        
        // 字符串为空或长度小于8
        if (s == null || s.length() < 8) 
        {
            return null;
        }
        
        Date date = null;
        String str = null;
        try
        {
            // 将字符串转换成日期格式
            date = simpleDateFormat.parse(s); 
            // 将日期格式化为字符串
            str = simpleDateFormat.format(date); 
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        
        return str;
    }

    /**
     * 日期时间转换成字符串
     * @param date  日期对象
     * @param style 日期格式
     * @return  时间字符串
     */
    public static String parseToString(Date date, String style)
    {
        // 实例化日期格式化类
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(); 
        // 提交转换格式
        simpleDateFormat.applyPattern(style); 
        
        // 如果日期时间为null
        if (date == null) 
        {
            return null;
        }
        
        // 将日期格式化为字符串
        return simpleDateFormat.format(date);
    }
    
    /**
     * 将指定的GMT时间字符串格式化成指定时区的时间
     * @param strGmtDateTime    GMT时间，例如“2015-08-14 11:23:35”
     * @param strTimeZone       时区，例如“+8”
     * @return  指定时区的时间
     */
    public static String GMT2SpecialTimeZone(String strGmtDateTime, String strTimeZone)
    {
        String          strSpecialDateTime = "";
        
        
        // 检查输入参数
        if ((null == strGmtDateTime) || (null == strTimeZone))
        {
            return null;
        }
        
        try
        {
            // 获得GMT时间对象
            SimpleDateFormat dfGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dfGmt.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            Date dtGmt = dfGmt.parse(strGmtDateTime);
            
            // 将GMT时间对象转换成指定时区的时间
            SimpleDateFormat dfSpecial = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dfSpecial.setTimeZone(TimeZone.getTimeZone("GMT" + strTimeZone));
            strSpecialDateTime = dfSpecial.format(dtGmt);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return strSpecialDateTime;
    }
}
