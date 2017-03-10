package com.xtoee.interceptor;

import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.xtoee.po.User;

/**
 * �û�Ȩ����֤������
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
        
        // ���session�е��û�����
        User user = (User) session.get("currentUser");
        if (user == null)
        {
            return "login";
        }
        
        return invocation.invoke();
    }
}
