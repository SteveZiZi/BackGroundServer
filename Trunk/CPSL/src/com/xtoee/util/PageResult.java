package com.xtoee.util;

import java.util.List;

/**
 * ÿҳ�ļ�¼��Ϣ
 * @author zgm
 *
 */
public class PageResult
{
    private Page                    page;                       // ��ҳ��Ϣ
    private List<?>                    list;                       // ��¼��Ϣ

    
    /**
     * ���캯��
     * @param page  ��ҳ��Ϣ
     * @param list  ��¼��Ϣ
     */
    public PageResult(Page page, List<?> list)
    {
        this.page = page;
        this.list = list;
    }

    /**
     * ��÷�ҳ��Ϣ
     * @return  ��ҳ��Ϣ
     */
    public Page getPage()
    {
        return page;
    }

    /**
     * ���÷�ҳ��Ϣ
     * @param page  ��ҳ��Ϣ
     */
    public void setPage(Page page)
    {
        this.page = page;
    }

    /**
     * ��ü�¼��Ϣ
     * @return  ��¼��Ϣ
     */
    public List<?> getList()
    {
        return list;
    }

    /**
     * ���ü�¼��Ϣ
     * @param list  ��¼��Ϣ
     */
    public void setList(List<?> list)
    {
        this.list = list;
    }
}
