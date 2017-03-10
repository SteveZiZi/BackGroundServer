package com.xtoee.servlet;

import com.xtoee.devices.CPowerSystem;

/**
 * ҵ���ʼ���߳�
 * @author zgm
 *
 */
public class LoadOnStartupThread extends Thread
{
	private String					m_strWebRoot;				// WebӦ�ø�·��
	
	
	/**
	 * ���캯��
	 * @param strWebRoot
	 */
	public LoadOnStartupThread(String strWebRoot)
	{
		m_strWebRoot = strWebRoot;
	}
	
	/**
	 * �߳�������
	 */
	public void run()
	{
		// ��ù������ϵͳ����
		CPowerSystem pSystem = CPowerSystem.GetInstance();
		if ((pSystem == null) || !pSystem.LoadConfig(m_strWebRoot + "runtime_config\\powerSystem.xml"))
        {
            return;
        }
		
		// ��ʼ���տͻ��˵�����
		pSystem.StartAcceptConn();
	}
}
