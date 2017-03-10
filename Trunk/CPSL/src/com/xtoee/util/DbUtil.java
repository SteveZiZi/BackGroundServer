package com.xtoee.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbUtil
{
    private String                  m_strDbUrl;                 // 数据库Url
    private String                  m_strDbUserName;            // 数据库登陆用户名
    private String                  m_strDbPassword;            // 数据库登陆密码
    private String                  m_strJdbcName;              // 数据库驱动类名

    
    /**
     * 构造函数
     */
    public DbUtil()
    {
        m_strDbUrl      = "jdbc:mysql://localhost:3306/db_cpsl";
        m_strDbUserName = "root";
        m_strDbPassword = "admin";
        m_strJdbcName   = "com.mysql.jdbc.Driver";
    }
    
    /**
     * 构造函数
     * @param strDbUrl          数据库Url
     * @param strDbUserName     数据库登陆用户名
     * @param strDbPassword     数据库登陆密码
     * @param strJdbcName       数据库驱动类名
     */
    public DbUtil(String strDbUrl, String strDbUserName, String strDbPassword, String strJdbcName)
    {
        m_strDbUrl = strDbUrl;
        m_strDbUserName = strDbUserName;
        m_strDbPassword = strDbPassword;
        m_strJdbcName = strJdbcName;
    }
    
    /**
     * 获得数据库Url
     * 
     * @return 数据库Url
     */
    public String getDbUrl()
    {
        return m_strDbUrl;
    }

    /**
     * 设置数据库Url
     * 
     * @param dbUrl 数据库Url
     */
    public void setDbUrl(String dbUrl)
    {
        m_strDbUrl = dbUrl;
    }

    /**
     * 获得数据库登陆用户名
     * 
     * @return 数据库登陆用户名
     */
    public String getDbUserName()
    {
        return m_strDbUserName;
    }

    /**
     * 设置数据库登陆用户名
     * 
     * @param dbUserName 数据库登陆用户名
     */
    public void setDbUserName(String dbUserName)
    {
        m_strDbUserName = dbUserName;
    }

    /**
     * 获得数据库登陆密码
     * 
     * @return 数据库登陆密码
     */
    public String getDbPassword()
    {
        return m_strDbPassword;
    }

    /**
     * 设置数据库登陆密码
     * 
     * @param dbPassword 数据库登陆密码
     */
    public void setDbPassword(String dbPassword)
    {
        m_strDbPassword = dbPassword;
    }
    
    /**
     * 获得数据库驱动类名
     * 
     * @return 数据库驱动类名
     */
    public String getJdbcName()
    {
        return m_strJdbcName;
    }

    /**
     * 设置数据库驱动类名
     * 
     * @param jdbcName 数据库驱动类名
     */
    public void setJdbcName(String jdbcName)
    {
        this.m_strJdbcName = jdbcName;
    }
    
    /**
     * 获取数据库连接
     * 
     * @return
     * @throws Exception
     */
    public Connection getCon() throws Exception
    {
        Class.forName(m_strJdbcName);
        Connection con = DriverManager.getConnection(m_strDbUrl, m_strDbUserName, m_strDbPassword);
        return con;
    }

    /**
     * 关闭数据库连接
     * 
     * @param con
     * @throws Exception
     */
    public void closeCon(Connection con) throws Exception
    {
        if (con != null)
        {
            con.close();
        }
    }

    /**
     * 测试函数
     * @param args
     */
    public static void main(String[] args)
    {
        DbUtil dbUtil = new DbUtil();

        try
        {
            dbUtil.getCon();
            System.out.println("数据库连接成功");
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
