package com.xtoee.util;

import java.util.List;

/**
 * 每页的记录信息
 * @author zgm
 *
 */
public class PageResult
{
    private Page                    page;                       // 分页信息
    private List<?>                    list;                       // 记录信息

    
    /**
     * 构造函数
     * @param page  分页信息
     * @param list  记录信息
     */
    public PageResult(Page page, List<?> list)
    {
        this.page = page;
        this.list = list;
    }

    /**
     * 获得分页信息
     * @return  分页信息
     */
    public Page getPage()
    {
        return page;
    }

    /**
     * 设置分页信息
     * @param page  分页信息
     */
    public void setPage(Page page)
    {
        this.page = page;
    }

    /**
     * 获得记录信息
     * @return  记录信息
     */
    public List<?> getList()
    {
        return list;
    }

    /**
     * 设置记录信息
     * @param list  记录信息
     */
    public void setList(List<?> list)
    {
        this.list = list;
    }
}
