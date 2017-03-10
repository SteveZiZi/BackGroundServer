package com.xtoee.service;

import com.xtoee.po.User;

/**
 * �û�����ӿ�
 * @author zgm
 *
 */
public interface UserManageService
{
    /**
     * �û�ע��
     * @param user  �û�����
     */
    public void saveUser(User user);

    /**
     * �û���¼
     * @param user  �û�����
     * @return  �ɹ�����true
     */
    public boolean login(User user);
    
    /**
     * �޸��û���Ϣ
     * @param user  �µ��û���Ϣ
     */
    public boolean modify(User user);
}
