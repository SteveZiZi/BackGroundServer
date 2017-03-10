package com.xtoee.service.impl;

import java.util.List;

import com.xtoee.dao.BaseDao;
import com.xtoee.po.User;
import com.xtoee.service.UserManageService;
import com.xtoee.util.StringUtil;

/**
 * 用户管理实现类
 * @author zgm
 *
 */
public class UserManageServiceImpl implements UserManageService
{
    private BaseDao<User>           baseDao;                    // 基础数据库访问接口

    
    /**
     * 获得基础数据库访问接口
     * @return 基础数据库访问接口
     */
    public BaseDao<User> getBaseDao()
    {
        return baseDao;
    }

    /**
     * 设置基础数据库访问接口
     * @param baseDao   基础数据库访问接口
     */
    public void setBaseDao(BaseDao<User> baseDao)
    {
        this.baseDao = baseDao;
    }
    
    /**
     * 用户注册
     */
    public void saveUser(User user)
    {
        baseDao.save(user);
    }
    
    /**
     * 用户登录
     */
    public boolean login(User user)
    {
        // 检查输入参数
        if ((null == user) || StringUtil.IsNullOrEmpty(user.getUserName()))
        {
            return false;
        }
        
        List<User> users = baseDao.find("from User where userName=? and password=?"
                , new Object[]{user.getUserName(), user.getPassword()});
        return users.size() != 0;
    }
    
    /**
     * 修改用户信息
     * @param user  新的用户信息
     */
    public boolean modify(User user)
    {
        // 检查输入参数
        if ((null == user))
        {
            return false;
        }
        
        try
        {
            baseDao.update(user);
        }
        catch (Exception e)
        {
            return false;
        }
        
        return true;
    }
}
