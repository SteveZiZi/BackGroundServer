package com.xtoee.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 把文件类型转化为byte[]
 */
public class FileToByte
{
    public static byte[] getBytesFromFile(File f)
    {
        if (f == null)
        { 
            // 如果文件为null直接返回一个null
            return null;
        }
        
        try
        {
            FileInputStream in = new FileInputStream(f);// 初始化一个文件输入流
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);// 初始化一个字节数组输出流
            byte[] ucBuffer = new byte[1000];// 设置缓存为1000
            int iCount;
            
            while ((iCount = in.read(ucBuffer)) != -1)
            {
                // 循环读取文件信息
                out.write(ucBuffer, 0, iCount);// 写入到字节数组输出流中。
            }
            
            in.close();// 关闭输入流
            out.close();// 关闭输出流
            
            return out.toByteArray();// 返回输出流中的字节数组
        }
        catch (IOException e)
        {
        }
        
        return null;
    }
}
