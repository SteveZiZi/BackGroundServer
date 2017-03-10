package com.xtoee.util;

/**
 * �ַ�������������
 * @author zgm
 *
 */
public class StringUtil
{
    /**
     * �ж�ָ���ַ����Ƿ�Ϊ�ջ򳤶�Ϊ0
     * @param strValue  �������ַ���
     * @return  ����ַ���Ϊ�ջ򳤶�Ϊ0������true
     */
    public static boolean IsNullOrEmpty(String strValue)
    {
        // �����������Ƿ�Ϊ��
        if (null == strValue)
        {
            return true;
        }
        
        // ����ַ�������
        if (strValue.length() == 0)
        {
            return true;
        }
        
        return false;
    }
    
    /**
     * ���ָ����IP��ַ�ַ����Ƿ���Ч
     * @param strIP IP��ַ�ַ���
     * @return  ��Ч����true
     */
    public static boolean IsValidIP(String strIP)
    {
        // ����������
        if ((null == strIP) || (strIP.length() < 7))
        {
            return false;
        }
        
        // ʹ��������ʽ��֤IP��ַ
        String strRegex = "^(0|[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-5])\\.(0|[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-5])\\.(0|[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-5])\\.(0|[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-5])";
        return strIP.matches(strRegex);
    }
}
