package com.xtoee.interceptor;

import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.xtoee.po.User;

/**
 * 用户权限验证拦截器
 * @author zgm
 *
 */
public class AuthorityInterceptor extends AbstractInterceptor
{
    private static final long serialVersionUID = 6203506362291764836L;

    @Override
    public String intercept(ActionInvocation invocation) throws Exception
    {
        ActionContext ctx = invocation.getInvocationContext();
        Map<?, ?> session = ctx.getSession();
        
        // 获得session中的用户对象
        User user = (User) session.get("currentUser");
        if (user == null)
        {
            return "login";
        }
        
        return invocation.invoke();
    }
}
