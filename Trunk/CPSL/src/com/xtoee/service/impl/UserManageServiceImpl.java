package com.xtoee.service.impl;

import java.util.List;

import com.xtoee.dao.BaseDao;
import com.xtoee.po.User;
import com.xtoee.service.UserManageService;
import com.xtoee.util.StringUtil;

/**
 * �û�����ʵ����
 * @author zgm
 *
 */
public class UserManageServiceImpl implements UserManageService
{
    private BaseDao<User>           baseDao;                    // �������ݿ���ʽӿ�

    
    /**
     * ��û������ݿ���ʽӿ�
     * @return �������ݿ���ʽӿ�
     */
    public BaseDao<User> getBaseDao()
    {
        return baseDao;
    }

    /**
     * ���û������ݿ���ʽӿ�
     * @param baseDao   �������ݿ���ʽӿ�
     */
    public void setBaseDao(BaseDao<User> baseDao)
    {
        this.baseDao = baseDao;
    }
    
    /**
     * �û�ע��
     */
    public void saveUser(User user)
    {
        baseDao.save(user);
    }
    
    /**
     * �û���¼
     */
    public boolean login(User user)
    {
        // ����������
        if ((null == user) || StringUtil.IsNullOrEmpty(user.getUserName()))
        {
            return false;
        }
        
        List<User> users = baseDao.find("from User where userName=? and password=?"
                , new Object[]{user.getUserName(), user.getPassword()});
        return users.size() != 0;
    }
    
    /**
     * �޸��û���Ϣ
     * @param user  �µ��û���Ϣ
     */
    public boolean modify(User user)
    {
        // ����������
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
