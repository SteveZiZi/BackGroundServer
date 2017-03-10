package com.xtoee.devices.dcu;

/**
 * Զ�̿�������Ĳ���
 * 
 * @author zgm
 *
 */
public class CIoctlParam_RemoteControl
{
    private int                     m_iDeviceId;                // �豸�ţ���·�š��̵����ţ�
    protected int                   m_iSubLoopId;               // �ӻ�·��
    protected enumSpecificFunction  m_eSpecificFunc;            // ���⹦����
    protected int                   m_iDataItem;                // ����������

    
    /**
     * ���캯��
     * @param iDeviceId         �豸�ţ���·�š��̵����ţ�
     * @param iSubLoopId        �ӻ�·��
     * @param eSpecificFunc     ���⹦����
     * @param iDataItem         ����������
     */
    public CIoctlParam_RemoteControl(int iDeviceId, int iSubLoopId, enumSpecificFunction eSpecificFunc, int iDataItem)
    {
        m_iDeviceId = iDeviceId;
        m_iSubLoopId = iSubLoopId;
        m_eSpecificFunc = eSpecificFunc;
        m_iDataItem = iDataItem;
    }
    
    /**
     * ����豸�ţ���·�š��̵����ţ�
     * @return  �豸��
     */
    public int getDeviceId()
    {
        return m_iDeviceId;
    }

    /**
     * �����豸�ţ���·�š��̵����ţ�
     * @param iDeviceId �豸��
     */
    public void setDeviceId(int iDeviceId)
    {
        m_iDeviceId = iDeviceId;
    }

    /**
     * ����ӻ�·��
     * @return  �ӻ�·��
     */
    public int getSubLoopId()
    {
        return m_iSubLoopId;
    }
    
    /**
     * �����ӻ�·��
     * @param iSubLoopId    �ӻ�·��
     */
    public void setSubLoopId(int iSubLoopId)
    {
        m_iSubLoopId = iSubLoopId;
    }
    
    /**
     * ������⹦����
     * @return  ���⹦����
     */
    public enumSpecificFunction getSpecificFunc()
    {
        return m_eSpecificFunc;
    }
    
    /**
     * �������⹦����
     * @param eSpecificFunc ���⹦����
     */
    public void setSpecificFunc(enumSpecificFunction eSpecificFunc)
    {
        m_eSpecificFunc = eSpecificFunc;
    }
    
    /**
     * �������������
     * @return  ����������
     */
    public int getDataItem()
    {
        return m_iDataItem;
    }
    
    /**
     * ��������������
     * @param iDataItem ����������
     */
    public void setDataItem(int iDataItem)
    {
        m_iDataItem = iDataItem;
    }
}
