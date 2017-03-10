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
 * 用户控制器
 * @author zgm
 *
 */
public class UserAction extends ActionSupport
{
    private static final long       serialVersionUID = -8520492190028514613L;
    private UserManageService       userManageService;          // 用户管理接口
    private InputStream             responseJson;               // 响应结果（Json格式）
    
    private User                    user;                       // 用户对象
    private String                  error;                      // 错误信息
    
    private String                  userName;                   // 用户名
    private String                  oldPassword;                // 旧密码
    private String                  newPassword;                // 新密码

    
    /**
     * 获得响应结果（Json格式）
     * @return  响应结果（Json格式）
     */
    public InputStream getResponseJson()
    {
        return responseJson;
    }

    /**
     * 设置响应结果（Json格式）
     * @param responseJson  响应结果（Json格式）
     */
    public void setResponseJson(InputStream responseJson)
    {
        this.responseJson = responseJson;
    }
    
    /**
     * 获得用户对象
     * @return  用户对象
     */
    public User getUser()
    {
        return user;
    }

    /**
     * 设置用户对象
     * @param user  用户对象
     */
    public void setUser(User user)
    {
        this.user = user;
    }

    /**
     * 获得错误信息
     * @return  错误信息
     */
    public String getError()
    {
        return error;
    }

    /**
     * 设置错误信息
     * @param error 错误信息
     */
    public void setError(String error)
    {
        this.error = error;
    }

    /**
     * 设置待修改密码的用户名
     * @param userName  用户名
     */
    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    /**
     * 设置旧密码
     * @param oldPassword   旧密码
     */
    public void setOldPassword(String oldPassword)
    {
        this.oldPassword = oldPassword;
    }

    /**
     * 设置新密码
     * @param newPassword   新密码
     */
    public void setNewPassword(String newPassword)
    {
        this.newPassword = newPassword;
    }

    /**
     * 设置用户管理接口
     * @param userManageService
     */
    public void setUserManageService(UserManageService userManageService)
    {
        this.userManageService = userManageService;
    }
    
    /**
     * 用户登录
     * @return  成功返回success
     */
    public String login() throws Exception
    {
        // 检查用户名、密码是否正确
        if (userManageService.login(user))
        {
            // 将用户对象保存到session中
            ServletActionContext.getContext().getSession().put("currentUser", user);
            return SUCCESS;
        }
        else 
        {
            setError("用户名或者密码错误！");
            return LOGIN;
        }
    }
    
    /**
     * 用户注销
     * @return  成功返回login
     */
    public String logout() throws Exception
    {
        // 将用户对象保存到session中
        ServletActionContext.getContext().getSession().put("currentUser", null);
        return LOGIN;
    }
    
    /**
     * 修改用户密码
     * @return  成功返回success
     * @throws UnsupportedEncodingException 
     */
    public String changePassword() throws UnsupportedEncodingException
    {
        JSONObject result = new JSONObject();
        

        do
        {
            // 检查输入参数
            if (StringUtil.IsNullOrEmpty(userName) 
                    || StringUtil.IsNullOrEmpty(oldPassword) 
                    || StringUtil.IsNullOrEmpty(newPassword))
            {
                result.put("isSuccess", "false");
                result.put("errorMsg", "用户名或密码为空！");
                break;
            }

            // 查询指定的用户名、密码是否存在
            if (!userManageService.login(new User(userName, oldPassword)))
            {
                result.put("isSuccess", "false");
                result.put("errorMsg", "原密码输入错误！");
                break;
            }

            // 修改密码
            if (userManageService.modify(new User(userName, newPassword)))
            {
                result.put("isSuccess", "false");
                result.put("errorMsg", "修改密码失败！");
                break;
            }
            
            // 返回成功字符串
            result.put("isSuccess", "true");
            
        } while (false);
        
        // 设置响应结果（Json格式）
        setResponseJson(new ByteArrayInputStream(result.toString().getBytes("UTF-8")));
        return SUCCESS;
    }
}
