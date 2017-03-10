package com.xtoee.servlet;

import com.xtoee.devices.CPowerSystem;

/**
 * 业务初始化线程
 * @author zgm
 *
 */
public class LoadOnStartupThread extends Thread
{
	private String					m_strWebRoot;				// Web应用根路径
	
	
	/**
	 * 构造函数
	 * @param strWebRoot
	 */
	public LoadOnStartupThread(String strWebRoot)
	{
		m_strWebRoot = strWebRoot;
	}
	
	/**
	 * 线程主函数
	 */
	public void run()
	{
		// 获得供电管理系统对象
		CPowerSystem pSystem = CPowerSystem.GetInstance();
		if ((pSystem == null) || !pSystem.LoadConfig(m_strWebRoot + "runtime_config\\powerSystem.xml"))
        {
            return;
        }
		
		// 开始接收客户端的连接
		pSystem.StartAcceptConn();
	}
}
