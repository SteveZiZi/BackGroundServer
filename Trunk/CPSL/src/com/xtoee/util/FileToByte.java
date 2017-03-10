package com.xtoee.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * ���ļ�����ת��Ϊbyte[]
 */
public class FileToByte
{
    public static byte[] getBytesFromFile(File f)
    {
        if (f == null)
        { 
            // ����ļ�Ϊnullֱ�ӷ���һ��null
            return null;
        }
        
        try
        {
            FileInputStream in = new FileInputStream(f);// ��ʼ��һ���ļ�������
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);// ��ʼ��һ���ֽ����������
            byte[] ucBuffer = new byte[1000];// ���û���Ϊ1000
            int iCount;
            
            while ((iCount = in.read(ucBuffer)) != -1)
            {
                // ѭ����ȡ�ļ���Ϣ
                out.write(ucBuffer, 0, iCount);// д�뵽�ֽ�����������С�
            }
            
            in.close();// �ر�������
            out.close();// �ر������
            
            return out.toByteArray();// ����������е��ֽ�����
        }
        catch (IOException e)
        {
        }
        
        return null;
    }
}
