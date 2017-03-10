package com.xtoee.util;

/**
 * ��ҳ��Ϣ��
 * @author zgm
 *
 */
public class Page
{
    private int                     everyPage;                  // ÿҳ��ʾ��¼��
    private int                     totalCount;                 // �ܼ�¼��
    private int                     totalPage;                  // ��ҳ��
    private int                     currentPage;                // ��ǰҳ
    private int                     beginIndex;                 // ��ѯ��ʼ��
    private boolean                 hasPrePage;                 // �Ƿ�����һҳ
    private boolean                 hasNextPage;                // �Ƿ�����һҳ

    
    /**
     * Ĭ�Ϲ��캯��
     */
    public Page()
    {
    }
    
    /**
     * ���캯��
     * @param everyPage     ÿҳ��ʾ��¼��
     * @param totalCount    �ܼ�¼��
     * @param totalPage     ��ҳ��
     * @param currentPage   ��ǰҳ
     * @param beginIndex    ��ѯ��ʼ��
     * @param hasPrePage    �Ƿ�����һҳ
     * @param hasNextPage   �Ƿ�����һҳ
     */
    public Page(int everyPage, int totalCount, int totalPage, int currentPage, int beginIndex, boolean hasPrePage, boolean hasNextPage)
    { 
        this.everyPage = everyPage;
        this.totalCount = totalCount;
        this.totalPage = totalPage;
        this.currentPage = currentPage;
        this.beginIndex = beginIndex;
        this.hasPrePage = hasPrePage;
        this.hasNextPage = hasNextPage;
    }

    /**
     * ���ÿҳ��ʾ��¼��
     * @return  ÿҳ��ʾ��¼��
     */
    public int getEveryPage()
    {
        return everyPage;
    }

    /**
     * ����ÿҳ��ʾ��¼��
     * @param everyPage ÿҳ��ʾ��¼��
     */
    public void setEveryPage(int everyPage)
    {
        this.everyPage = everyPage;
    }

    /**
     * ����ܼ�¼��
     * @return  �ܼ�¼��
     */
    public int getTotalCount()
    {
        return totalCount;
    }

    /**
     * �����ܼ�¼��
     * @param totalCount    �ܼ�¼��
     */
    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }

    /**
     * �����ҳ��
     * @return  ��ҳ��
     */
    public int getTotalPage()
    {
        return totalPage;
    }

    /**
     * ������ҳ��
     * @param totalPage ��ҳ��
     */
    public void setTotalPage(int totalPage)
    {
        this.totalPage = totalPage;
    }

    /**
     * ��õ�ǰҳ
     * @return  ��ǰҳ
     */
    public int getCurrentPage()
    {
        return currentPage;
    }

    /**
     * ���õ�ǰҳ
     * @param currentPage   ��ǰҳ
     */
    public void setCurrentPage(int currentPage)
    {
        this.currentPage = currentPage;
    }

    /**
     * ��ò�ѯ��ʼ��
     * @return  ��ѯ��ʼ��
     */
    public int getBeginIndex()
    {
        return beginIndex;
    }

    /**
     * ���ò�ѯ��ʼ��
     * @param beginIndex    ��ѯ��ʼ��
     */
    public void setBeginIndex(int beginIndex)
    {
        this.beginIndex = beginIndex;
    }

    /**
     * ����Ƿ�����һҳ
     * @return  �Ƿ�����һҳ
     */
    public boolean isHasPrePage()
    {
        return hasPrePage;
    }

    /**
     * �����Ƿ�����һҳ
     * @param hasPrePage    �Ƿ�����һҳ
     */
    public void setHasPrePage(boolean hasPrePage)
    {
        this.hasPrePage = hasPrePage;
    }

    /**
     * ����Ƿ�����һҳ
     * @return  �Ƿ�����һҳ
     */
    public boolean isHasNextPage()
    {
        return hasNextPage;
    }

    /**
     * �����Ƿ�����һҳ
     * @param hasNextPage   �Ƿ�����һҳ
     */
    public void setHasNextPage(boolean hasNextPage)
    {
        this.hasNextPage = hasNextPage;
    }
}
