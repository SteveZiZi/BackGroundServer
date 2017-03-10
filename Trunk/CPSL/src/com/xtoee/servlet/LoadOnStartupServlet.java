package com.xtoee.servlet;

import javax.servlet.http.HttpServlet;

/**
 * Web初始化后自动运行的类
 * @author zgm
 *
 */
public class LoadOnStartupServlet extends HttpServlet
{
	private static final long serialVersionUID = 7125579936278095024L;
	
	
	/**
	 * 初始化
	 */
	public void init()
	{
		String 			strWebRoot = null;
		
		
		// 获得Web应用的发布路径
		strWebRoot = getServletContext().getRealPath("");
		
		// 开启后台轮训线程
		new LoadOnStartupThread(strWebRoot).start();
	}
}
