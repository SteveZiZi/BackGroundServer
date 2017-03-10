package com.xtoee.devices.dcu;

import java.util.ArrayList;
import java.util.List;

/**
 * ɾ���豸��������Ĳ���
 * @author zgm
 *
 */
public class CIoctlParam_DeleteArchives
{
    public static final int         DT_DeleteAll    = 0;        // ɾ������
    public static final int         DT_DeletePart   = 1;        // ɾ������
    
    private int                     m_iDeleteType;              // ɾ���������ͣ�ɾ�����С�ɾ�����֣�
    private List<Integer>           m_lstDeviceIds;             // ��ɾ�����豸���б�
    
    
    /**
     * ���캯��
     */
    public CIoctlParam_DeleteArchives()
    {
        m_iDeleteType   = DT_DeletePart;
        m_lstDeviceIds  = new ArrayList<Integer>();
    }
    
    /**
     * ���캯��
     * @param m_iDeleteType ɾ����������
     */
    public CIoctlParam_DeleteArchives(int iDeleteType)
    {
        m_iDeleteType = iDeleteType;
    }

    /**
     * ���ɾ����������
     * @return  ɾ���������ͣ�ɾ�����С�ɾ�����֣�
     */
    public int getDeleteType()
    {
        return m_iDeleteType;
    }
    
    /**
     * ����ɾ���������ͣ�ɾ�����С�ɾ�����֣�
     * @param deleteType    ɾ���������ͣ�ɾ�����С�ɾ�����֣�
     */
    public void setDeleteType(int deleteType)
    {
        m_iDeleteType = deleteType;
    }
    
    /**
     * ��ô�ɾ�����豸���б�
     * @return  ��ɾ�����豸���б�
     */
    public List<Integer> getDeviceIdList()
    {
        return m_lstDeviceIds;
    }
    
    /**
     * ���ô�ɾ�����豸���б�
     * @param deviceIdList  ��ɾ�����豸���б�
     */
    public void setDeviceIdList(List<Integer> deviceIdList)
    {
        m_lstDeviceIds = deviceIdList;
    }
}
