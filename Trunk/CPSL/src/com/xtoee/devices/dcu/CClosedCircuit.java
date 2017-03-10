package com.xtoee.devices.dcu;

import java.util.LinkedList;
import java.util.List;

/**
 * ��·��
 * 
 * @author zgm
 *
 */
public class CClosedCircuit
{
    private int                     m_nID;                      // ��·��
    private List<CRectifier>        m_lstRectifiers;            // �������б�
    private List<CRelay>            m_lstRelays;                // �̵����б�

    
    /**
     * ���캯��
     * 
     * @param nID
     *            ��·ID
     */
    public CClosedCircuit(int nID)
    {
        m_nID = nID;
        m_lstRectifiers = new LinkedList<CRectifier>();
        m_lstRelays = new LinkedList<CRelay>();
    }

    /**
     * ��û�·��
     * 
     * @return ��·��
     */
    public int getID()
    {
        return m_nID;
    }

    /**
     * ��õ�ѹֵ
     * 
     * @return ��ѹֵ
     */
    public double getVoltage()
    {
        double dMax = Double.MIN_VALUE;
        double dTemp = Double.MIN_VALUE;

        // ѭ������ÿһ��������
        for (CRectifier rectifier : m_lstRectifiers)
        {
            // ��������������е���ߵ�ѹֵ
            dTemp = rectifier.getVoltage();
            if (dTemp > dMax)
            {
                dMax = dTemp;
            }
        }

        return dMax;
    }

    /**
     * ��õ���ֵ
     * 
     * @return ����ֵ
     */
    public double getCurrent()
    {
        double dTotal = 0;

        // ѭ������ÿһ��������
        for (CRectifier rectifier : m_lstRectifiers)
        {
            // �ۼӵ���ֵ
            dTotal += rectifier.getCurrent();
        }

        return dTotal;
    }

    /**
     * ��ù���ֵ
     * 
     * @return ����
     */
    public double getPower()
    {
        return getVoltage() * getCurrent();
    }

    /**
     * ����¶�ֵ
     * 
     * @return �¶�
     */
    public double getTemperature()
    {
        double dMax = Double.NEGATIVE_INFINITY;
        double dTemp = Double.NEGATIVE_INFINITY;

        // ѭ������ÿһ����·
        for (CRectifier rectifier : m_lstRectifiers)
        {
            // ��������������е�����¶�ֵ
            dTemp = rectifier.getTemperature();
            if (dTemp > dMax)
            {
                dMax = dTemp;
            }
        }

        return dMax;
    }

    /**
     * ��û�·״̬
     * 
     * @return ��·״̬
     */
    public String getStatus()
    {
        // ѭ������ÿһ����·
        for (CRectifier rectifier : m_lstRectifiers)
        {
            if ((rectifier.getAlarmValue() != 0) || (rectifier.getProtectionType() != 0))
            {
                return "�쳣";
            }
        }
        
        return "����";
    }

    /**
     * ����������б�
     * 
     * @return �������б�
     */
    public List<CRectifier> getRectifierList()
    {
        return m_lstRectifiers;
    }
    
    /**
     * ���ָ��ID������������
     * 
     * @param nId
     *            ������ID
     * @return ����������
     */
    public CRectifier getRectifierById(int nId)
    {
        // ѭ������ÿһ��������
        for (CRectifier rectifier : m_lstRectifiers)
        {
            // ���ID�Ƿ�ƥ��
            if (rectifier.getID() == nId)
            {
                return rectifier;
            }
        }

        return null;
    }

    /**
     * �������������������
     * 
     * @param rectifier
     *            ����������
     * @return ��ӳɹ�����true
     */
    public boolean AddRetifier(CRectifier rectifier)
    {
        // ��������������Ƿ��Ѿ�����
        if ((null == rectifier)
                || (null != getRectifierById(rectifier.getID())))
        {
            return false;
        }

        // �������������������
        m_lstRectifiers.add(rectifier);
        return true;
    }

    /**
     * ɾ�����е�����������
     */
    public void DeleteAllRetifiers()
    {
        m_lstRectifiers.clear();
    }
    
    /**
     * ��ü̵����б�
     * @return  �̵����б�
     */
    public List<CRelay> getRelayList()
    {
        return m_lstRelays;
    }
    
    /**
     * ���ָ��ID�ļ̵�������
     * 
     * @param nId
     *            �̵���ID
     * @return �̵�������
     */
    public CRelay getRelayById(int nId)
    {
        // ѭ������ÿһ���̵���
        for (CRelay relay : m_lstRelays)
        {
            // ���ID�Ƿ�ƥ��
            if (relay.getID() == nId)
            {
                return relay;
            }
        }

        return null;
    }
    
    /**
     * ��Ӽ̵�������������
     * 
     * @param relay
     *            �̵�������
     * @return ��ӳɹ�����true
     */
    public boolean AddRelay(CRelay relay)
    {
        // ���̵��������Ƿ��Ѿ�����
        if ((null == relay)
                || (null != getRelayById(relay.getID())))
        {
            return false;
        }

        // ��Ӽ̵�������������
        m_lstRelays.add(relay);
        return true;
    }
    
    /**
     * ɾ�����еļ̵�������
     */
    public void DeleteAllRelay()
    {
        m_lstRelays.clear();
    }
}
