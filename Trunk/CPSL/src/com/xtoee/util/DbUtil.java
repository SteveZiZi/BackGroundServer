package com.xtoee.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbUtil
{
    private String                  m_strDbUrl;                 // ���ݿ�Url
    private String                  m_strDbUserName;            // ���ݿ��½�û���
    private String                  m_strDbPassword;            // ���ݿ��½����
    private String                  m_strJdbcName;              // ���ݿ���������

    
    /**
     * ���캯��
     */
    public DbUtil()
    {
        m_strDbUrl      = "jdbc:mysql://localhost:3306/db_cpsl";
        m_strDbUserName = "root";
        m_strDbPassword = "admin";
        m_strJdbcName   = "com.mysql.jdbc.Driver";
    }
    
    /**
     * ���캯��
     * @param strDbUrl          ���ݿ�Url
     * @param strDbUserName     ���ݿ��½�û���
     * @param strDbPassword     ���ݿ��½����
     * @param strJdbcName       ���ݿ���������
     */
    public DbUtil(String strDbUrl, String strDbUserName, String strDbPassword, String strJdbcName)
    {
        m_strDbUrl = strDbUrl;
        m_strDbUserName = strDbUserName;
        m_strDbPassword = strDbPassword;
        m_strJdbcName = strJdbcName;
    }
    
    /**
     * ������ݿ�Url
     * 
     * @return ���ݿ�Url
     */
    public String getDbUrl()
    {
        return m_strDbUrl;
    }

    /**
     * �������ݿ�Url
     * 
     * @param dbUrl ���ݿ�Url
     */
    public void setDbUrl(String dbUrl)
    {
        m_strDbUrl = dbUrl;
    }

    /**
     * ������ݿ��½�û���
     * 
     * @return ���ݿ��½�û���
     */
    public String getDbUserName()
    {
        return m_strDbUserName;
    }

    /**
     * �������ݿ��½�û���
     * 
     * @param dbUserName ���ݿ��½�û���
     */
    public void setDbUserName(String dbUserName)
    {
        m_strDbUserName = dbUserName;
    }

    /**
     * ������ݿ��½����
     * 
     * @return ���ݿ��½����
     */
    public String getDbPassword()
    {
        return m_strDbPassword;
    }

    /**
     * �������ݿ��½����
     * 
     * @param dbPassword ���ݿ��½����
     */
    public void setDbPassword(String dbPassword)
    {
        m_strDbPassword = dbPassword;
    }
    
    /**
     * ������ݿ���������
     * 
     * @return ���ݿ���������
     */
    public String getJdbcName()
    {
        return m_strJdbcName;
    }

    /**
     * �������ݿ���������
     * 
     * @param jdbcName ���ݿ���������
     */
    public void setJdbcName(String jdbcName)
    {
        this.m_strJdbcName = jdbcName;
    }
    
    /**
     * ��ȡ���ݿ�����
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
     * �ر����ݿ�����
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
     * ���Ժ���
     * @param args
     */
    public static void main(String[] args)
    {
        DbUtil dbUtil = new DbUtil();

        try
        {
            dbUtil.getCon();
            System.out.println("���ݿ����ӳɹ�");
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
