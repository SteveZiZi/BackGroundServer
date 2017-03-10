package com.xtoee.po;

/**
 * 用户类
 * @author zgm
 *
 */
public class User
{
    private Long                    id;                         // 用户Id
    private String                  userName;                   // 用户名
    private String                  password;                   // 密  码

    
    /**
     * 默认构造函数
     */
    public User()
    {
        super();
    }

    /**
     * 构造函数
     * @param userName  用户名
     * @param password  密码
     */
    public User(String userName, String password)
    {
        super();
        this.userName = userName;
        this.password = password;
    }

    /**
     * 获得用户Id
     * @return  用户Id
     */
    public Long getId()
    {
        return id;
    }

    /**
     * 设置用户Id
     * @param id    用户Id
     */
    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * 获得用户名
     * @return  用户名
     */
    public String getUserName()
    {
        return userName;
    }

    /**
     * 设置用户名
     * @param userName  用户名
     */
    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    /**
     * 获得用户密码
     * @return  用户密码
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * 设置用户密码
     * @param password  用户密码
     */
    public void setPassword(String password)
    {
        this.password = password;
    }
}
