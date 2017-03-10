package com.xtoee.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.Socket;

public class CSocketUtil
{
	/** 
	* �ж�socket�����Ƿ��ѶϿ�
	* @param sockConn ͨ������
	* @return �Ͽ�����true
	*/  
	public static boolean isSocketClosed(Socket sockConn)
	{
		try
		{
			// ����1���ֽڵĽ������ݣ�Ĭ������£���������û�п����������ݴ�����Ӱ������ͨ��
			sockConn.sendUrgentData(0);
			return false;
		}
		catch (Exception se)
		{
			return true;
		}
	}
	
	/**
	 * ���socket�����뻺����
	 * @param sockConn	socket����
	 */
	public static void clearRecvBuffer(Socket sockConn)
	{
		int				i 	= 0;
		InputStream 	in 	= null;
		byte[]			ucBuffer = new byte[1024];
		
		
		// ����������
		if (null == sockConn)
		{
			return;
		}
		
		do
		{
			try
			{
				// ������뻺�����Ƿ�Ϊ��
				in = sockConn.getInputStream();
				if (in.available() <= 0)
				{
					break;
				}
				
				// ��ȡ���뻺�����е�����
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
	 * ��ȡ��
	 * @param inStream     ������
	 * @return ��ȡ���ֽ�����
	 * @throws Exception   �쳣��Ϣ
	 */
    public static byte[] readStream(InputStream inStream) throws Exception
    {
        int                     len = -1;
        byte[]                  buffer = new byte[1024];
        ByteArrayOutputStream   outSteam = new ByteArrayOutputStream();

        
        // ѭ����ȡ��������ֱ��û������Ϊֹ
        while (inStream.available() != 0)
        {
            if ((len = inStream.read(buffer)) != -1)
            {
                // ���������ַ����浽�������
                outSteam.write(buffer, 0, len);
            }
        }

        // ���ض�ȡ���ֽ�����
        outSteam.close();
        return outSteam.toByteArray();
    }
}
