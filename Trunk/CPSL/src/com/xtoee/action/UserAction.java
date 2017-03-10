package com.xtoee.action;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;
import com.xtoee.util.StringUtil;

import net.sf.json.JSONObject;

import com.xtoee.po.User;
import com.xtoee.service.UserManageService;

/**
 * �û�������
 * @author zgm
 *
 */
public class UserAction extends ActionSupport
{
    private static final long       serialVersionUID = -8520492190028514613L;
    private UserManageService       userManageService;          // �û�����ӿ�
    private InputStream             responseJson;               // ��Ӧ�����Json��ʽ��
    
    private User                    user;                       // �û�����
    private String                  error;                      // ������Ϣ
    
    private String                  userName;                   // �û���
    private String                  oldPassword;                // ������
    private String                  newPassword;                // ������

    
    /**
     * �����Ӧ�����Json��ʽ��
     * @return  ��Ӧ�����Json��ʽ��
     */
    public InputStream getResponseJson()
    {
        return responseJson;
    }

    /**
     * ������Ӧ�����Json��ʽ��
     * @param responseJson  ��Ӧ�����Json��ʽ��
     */
    public void setResponseJson(InputStream responseJson)
    {
        this.responseJson = responseJson;
    }
    
    /**
     * ����û�����
     * @return  �û�����
     */
    public User getUser()
    {
        return user;
    }

    /**
     * �����û�����
     * @param user  �û�����
     */
    public void setUser(User user)
    {
        this.user = user;
    }

    /**
     * ��ô�����Ϣ
     * @return  ������Ϣ
     */
    public String getError()
    {
        return error;
    }

    /**
     * ���ô�����Ϣ
     * @param error ������Ϣ
     */
    public void setError(String error)
    {
        this.error = error;
    }

    /**
     * ���ô��޸�������û���
     * @param userName  �û���
     */
    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    /**
     * ���þ�����
     * @param oldPassword   ������
     */
    public void setOldPassword(String oldPassword)
    {
        this.oldPassword = oldPassword;
    }

    /**
     * ����������
     * @param newPassword   ������
     */
    public void setNewPassword(String newPassword)
    {
        this.newPassword = newPassword;
    }

    /**
     * �����û�����ӿ�
     * @param userManageService
     */
    public void setUserManageService(UserManageService userManageService)
    {
        this.userManageService = userManageService;
    }
    
    /**
     * �û���¼
     * @return  �ɹ�����success
     */
    public String login() throws Exception
    {
        // ����û����������Ƿ���ȷ
        if (userManageService.login(user))
        {
            // ���û����󱣴浽session��
            ServletActionContext.getContext().getSession().put("currentUser", user);
            return SUCCESS;
        }
        else 
        {
            setError("�û��������������");
            return LOGIN;
        }
    }
    
    /**
     * �û�ע��
     * @return  �ɹ�����login
     */
    public String logout() throws Exception
    {
        // ���û����󱣴浽session��
        ServletActionContext.getContext().getSession().put("currentUser", null);
        return LOGIN;
    }
    
    /**
     * �޸��û�����
     * @return  �ɹ�����success
     * @throws UnsupportedEncodingException 
     */
    public String changePassword() throws UnsupportedEncodingException
    {
        JSONObject result = new JSONObject();
        

        do
        {
            // ����������
            if (StringUtil.IsNullOrEmpty(userName) 
                    || StringUtil.IsNullOrEmpty(oldPassword) 
                    || StringUtil.IsNullOrEmpty(newPassword))
            {
                result.put("isSuccess", "false");
                result.put("errorMsg", "�û���������Ϊ�գ�");
                break;
            }

            // ��ѯָ�����û����������Ƿ����
            if (!userManageService.login(new User(userName, oldPassword)))
            {
                result.put("isSuccess", "false");
                result.put("errorMsg", "ԭ�����������");
                break;
            }

            // �޸�����
            if (userManageService.modify(new User(userName, newPassword)))
            {
                result.put("isSuccess", "false");
                result.put("errorMsg", "�޸�����ʧ�ܣ�");
                break;
            }
            
            // ���سɹ��ַ���
            result.put("isSuccess", "true");
            
        } while (false);
        
        // ������Ӧ�����Json��ʽ��
        setResponseJson(new ByteArrayInputStream(result.toString().getBytes("UTF-8")));
        return SUCCESS;
    }
}
