package com.xtoee.servlet;

import javax.servlet.http.HttpServlet;

/**
 * Web��ʼ�����Զ����е���
 * @author zgm
 *
 */
public class LoadOnStartupServlet extends HttpServlet
{
	private static final long serialVersionUID = 7125579936278095024L;
	
	
	/**
	 * ��ʼ��
	 */
	public void init()
	{
		String 			strWebRoot = null;
		
		
		// ���WebӦ�õķ���·��
		strWebRoot = getServletContext().getRealPath("");
		
		// ������̨��ѵ�߳�
		new LoadOnStartupThread(strWebRoot).start();
	}
}
