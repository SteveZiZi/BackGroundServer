package com.xtoee.service;

import com.xtoee.po.User;

/**
 * 用户管理接口
 * @author zgm
 *
 */
public interface UserManageService
{
    /**
     * 用户注册
     * @param user  用户对象
     */
    public void saveUser(User user);

    /**
     * 用户登录
     * @param user  用户对象
     * @return  成功返回true
     */
    public boolean login(User user);
    
    /**
     * 修改用户信息
     * @param user  新的用户信息
     */
    public boolean modify(User user);
}
