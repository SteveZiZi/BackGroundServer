package com.xtoee.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * ����ת����
 * @author zgm
 *
 */
public class DateUtil
{
    public static final String      yyMMdd = "yy-MM-dd";        // �����ڸ�ʽ
    public static final String      yyyyMMdd = "yyyy-MM-dd";    // �����ڸ�ʽ
    public static final String      HHmmss = "HH:mm:ss";        // ʱ���ʽ
    public static final String      yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss"; // ������ʱ���ʽ
    public static final String      yyMMddHHmmss = "yy-MM-dd HH:mm:ss";     // ������ʱ���ʽ

    
    /**
     * �ַ���ת��������ʱ��
     * @param s         �ַ���
     * @param style     ���ڸ�ʽ
     * @return  ����ʱ��
     */
    public static Date parseToDate(String s, String style)
    {
        // ʵ�������ڸ�ʽ����
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        // �ύת����ʽ
        simpleDateFormat.applyPattern(style); 
        
        // �ַ���Ϊ�ջ򳤶�С��8
        if (s == null || s.length() < 8) 
        {
            return null;
        }
        
        Date date = null;
        try
        {
            // ����ת��
            date = simpleDateFormat.parse(s); 
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        
        return date;
    }

    /**
     * ��ʽ�������ַ���
     * @param s     ʱ���ַ���
     * @param style ���ڸ�ʽ
     * @return  ʱ���ַ���
     */
    public static String parseToString(String s, String style)
    {
        // ʵ�������ڸ�ʽ����
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(); 
        // �ύת����ʽ
        simpleDateFormat.applyPattern(style); 
        
        // �ַ���Ϊ�ջ򳤶�С��8
        if (s == null || s.length() < 8) 
        {
            return null;
        }
        
        Date date = null;
        String str = null;
        try
        {
            // ���ַ���ת�������ڸ�ʽ
            date = simpleDateFormat.parse(s); 
            // �����ڸ�ʽ��Ϊ�ַ���
            str = simpleDateFormat.format(date); 
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        
        return str;
    }

    /**
     * ����ʱ��ת�����ַ���
     * @param date  ���ڶ���
     * @param style ���ڸ�ʽ
     * @return  ʱ���ַ���
     */
    public static String parseToString(Date date, String style)
    {
        // ʵ�������ڸ�ʽ����
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(); 
        // �ύת����ʽ
        simpleDateFormat.applyPattern(style); 
        
        // �������ʱ��Ϊnull
        if (date == null) 
        {
            return null;
        }
        
        // �����ڸ�ʽ��Ϊ�ַ���
        return simpleDateFormat.format(date);
    }
    
    /**
     * ��ָ����GMTʱ���ַ�����ʽ����ָ��ʱ����ʱ��
     * @param strGmtDateTime    GMTʱ�䣬���硰2015-08-14 11:23:35��
     * @param strTimeZone       ʱ�������硰+8��
     * @return  ָ��ʱ����ʱ��
     */
    public static String GMT2SpecialTimeZone(String strGmtDateTime, String strTimeZone)
    {
        String          strSpecialDateTime = "";
        
        
        // ����������
        if ((null == strGmtDateTime) || (null == strTimeZone))
        {
            return null;
        }
        
        try
        {
            // ���GMTʱ�����
            SimpleDateFormat dfGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dfGmt.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            Date dtGmt = dfGmt.parse(strGmtDateTime);
            
            // ��GMTʱ�����ת����ָ��ʱ����ʱ��
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
