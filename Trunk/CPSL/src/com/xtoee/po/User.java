package com.xtoee.po;

/**
 * �û���
 * @author zgm
 *
 */
public class User
{
    private Long                    id;                         // �û�Id
    private String                  userName;                   // �û���
    private String                  password;                   // ��  ��

    
    /**
     * Ĭ�Ϲ��캯��
     */
    public User()
    {
        super();
    }

    /**
     * ���캯��
     * @param userName  �û���
     * @param password  ����
     */
    public User(String userName, String password)
    {
        super();
        this.userName = userName;
        this.password = password;
    }

    /**
     * ����û�Id
     * @return  �û�Id
     */
    public Long getId()
    {
        return id;
    }

    /**
     * �����û�Id
     * @param id    �û�Id
     */
    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * ����û���
     * @return  �û���
     */
    public String getUserName()
    {
        return userName;
    }

    /**
     * �����û���
     * @param userName  �û���
     */
    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    /**
     * ����û�����
     * @return  �û�����
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * �����û�����
     * @param password  �û�����
     */
    public void setPassword(String password)
    {
        this.password = password;
    }
}
