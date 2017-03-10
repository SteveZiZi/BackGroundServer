package com.xtoee.util;

/**
 * 分页信息辅助类
 * @author zgm
 *
 */
public class PageUtil
{
    /**
     * 创建分页信息对象
     * @param everyPage     每页显示记录数
     * @param totalCount    总记录数
     * @param currentPage   当前页
     * @return  分页信息对象
     */
    public static Page createPage(int everyPage, int totalCount, int currentPage)
    {
        everyPage = getEveryPage(everyPage);
        currentPage = getCurrentPage(currentPage);
        int totalPage = getTotalPage(everyPage, totalCount);
        int beginIndex = getBeginIndex(everyPage, currentPage);
        boolean hasPrePage = getHasPrePage(currentPage);
        boolean hasNextPage = getHasNextPage(totalPage, currentPage);
        return new Page(everyPage, totalCount, totalPage, currentPage, beginIndex, hasPrePage, hasNextPage);
    }

    /**
     * 获得每页显示记录数
     * @param everyPage 指定的每页显示记录数
     * @return 每页显示记录数
     */
    public static int getEveryPage(int everyPage)
    {
        return everyPage == 0 ? 10 : everyPage;
    }

    /**
     * 获得当前页
     * @param currentPage   指定的当前页
     * @return  当前页
     */
    public static int getCurrentPage(int currentPage)
    {
        return currentPage == 0 ? 1 : currentPage;
    }

    /**
     * 获得总页数
     * @param everyPage     每页显示记录数
     * @param totalCount    总记录数
     * @return  总页数
     */
    public static int getTotalPage(int everyPage, int totalCount)
    {
        int totalPage = 0;
        
        if (totalCount != 0 && totalCount % everyPage == 0)
        {
            totalPage = totalCount / everyPage;
        }
        else
        {
            totalPage = totalCount / everyPage + 1;
        }
        
        return totalPage;
    }

    /**
     * 获得起始位置
     * @param everyPage     每页显示记录数
     * @param currentPage   当前页
     * @return  起始位置
     */
    public static int getBeginIndex(int everyPage, int currentPage)
    {
        return (currentPage - 1) * everyPage;
    }

    /**
     * 获得是否有上一页
     * @param currentPage   当前页
     * @return  是否有上一页
     */
    public static boolean getHasPrePage(int currentPage)
    {
        return currentPage == 1 ? false : true;
    }

    /**
     * 获得是否有下一页
     * @param totalPage     总页数
     * @param currentPage   当前页
     * @return  是否有下一页
     */
    public static boolean getHasNextPage(int totalPage, int currentPage)
    {
        return currentPage == totalPage || totalPage == 0 ? false : true;
    }
}
