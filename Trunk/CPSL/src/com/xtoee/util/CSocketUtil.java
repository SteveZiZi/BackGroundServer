package com.xtoee.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.Socket;

public class CSocketUtil
{
	/** 
	* 判断socket连接是否已断开
	* @param sockConn 通信连接
	* @return 断开返回true
	*/  
	public static boolean isSocketClosed(Socket sockConn)
	{
		try
		{
			// 发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
			sockConn.sendUrgentData(0);
			return false;
		}
		catch (Exception se)
		{
			return true;
		}
	}
	
	/**
	 * 清空socket的输入缓冲区
	 * @param sockConn	socket连接
	 */
	public static void clearRecvBuffer(Socket sockConn)
	{
		int				i 	= 0;
		InputStream 	in 	= null;
		byte[]			ucBuffer = new byte[1024];
		
		
		// 检查输入参数
		if (null == sockConn)
		{
			return;
		}
		
		do
		{
			try
			{
				// 检查输入缓冲区是否为空
				in = sockConn.getInputStream();
				if (in.available() <= 0)
				{
					break;
				}
				
				// 读取输入缓冲区中的数据
				in.read(ucBuffer);
			}
			catch (Exception e)
			{
				System.out.println(e.getMessage());
				break;
			}
		}
		while (i++ <= 10);
	}
	
	/**
	 * 读取流
	 * @param inStream     输入流
	 * @return 读取的字节数组
	 * @throws Exception   异常信息
	 */
    public static byte[] readStream(InputStream inStream) throws Exception
    {
        int                     len = -1;
        byte[]                  buffer = new byte[1024];
        ByteArrayOutputStream   outSteam = new ByteArrayOutputStream();

        
        // 循环读取缓冲区，直到没有数据为止
        while (inStream.available() != 0)
        {
            if ((len = inStream.read(buffer)) != -1)
            {
                // 将读到的字符保存到输出缓冲
                outSteam.write(buffer, 0, len);
            }
        }

        // 返回读取的字节数组
        outSteam.close();
        return outSteam.toByteArray();
    }
}
