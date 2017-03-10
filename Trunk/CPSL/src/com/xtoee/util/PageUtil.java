package com.xtoee.util;

/**
 * ��ҳ��Ϣ������
 * @author zgm
 *
 */
public class PageUtil
{
    /**
     * ������ҳ��Ϣ����
     * @param everyPage     ÿҳ��ʾ��¼��
     * @param totalCount    �ܼ�¼��
     * @param currentPage   ��ǰҳ
     * @return  ��ҳ��Ϣ����
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
     * ���ÿҳ��ʾ��¼��
     * @param everyPage ָ����ÿҳ��ʾ��¼��
     * @return ÿҳ��ʾ��¼��
     */
    public static int getEveryPage(int everyPage)
    {
        return everyPage == 0 ? 10 : everyPage;
    }

    /**
     * ��õ�ǰҳ
     * @param currentPage   ָ���ĵ�ǰҳ
     * @return  ��ǰҳ
     */
    public static int getCurrentPage(int currentPage)
    {
        return currentPage == 0 ? 1 : currentPage;
    }

    /**
     * �����ҳ��
     * @param everyPage     ÿҳ��ʾ��¼��
     * @param totalCount    �ܼ�¼��
     * @return  ��ҳ��
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
     * �����ʼλ��
     * @param everyPage     ÿҳ��ʾ��¼��
     * @param currentPage   ��ǰҳ
     * @return  ��ʼλ��
     */
    public static int getBeginIndex(int everyPage, int currentPage)
    {
        return (currentPage - 1) * everyPage;
    }

    /**
     * ����Ƿ�����һҳ
     * @param currentPage   ��ǰҳ
     * @return  �Ƿ�����һҳ
     */
    public static boolean getHasPrePage(int currentPage)
    {
        return currentPage == 1 ? false : true;
    }

    /**
     * ����Ƿ�����һҳ
     * @param totalPage     ��ҳ��
     * @param currentPage   ��ǰҳ
     * @return  �Ƿ�����һҳ
     */
    public static boolean getHasNextPage(int totalPage, int currentPage)
    {
        return currentPage == totalPage || totalPage == 0 ? false : true;
    }
}
