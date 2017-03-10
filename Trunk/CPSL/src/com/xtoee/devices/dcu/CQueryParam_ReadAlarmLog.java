package com.xtoee.devices.dcu;

import java.util.Date;

/**
 * �澯��־��������Ĳ���
 * @author zgm
 *
 */
public class CQueryParam_ReadAlarmLog
{
    private Date                    m_dtStart;                  // �澯��ʼʱ��
    private int                     m_iLogCount;                // �澯���ݵ���
    
    
    /**
     * ���캯��
     * @param dtStart   �澯��ʼʱ��
     * @param iLogCount �澯���ݵ���
     */
    public CQueryParam_ReadAlarmLog(Date dtStart, int iLogCount)
    {
        m_dtStart = dtStart;
        m_iLogCount = iLogCount;
    }
    
    /**
     * ��ø澯��ʼʱ��
     * @return  ��ʼʱ��
     */
    public Date getStartTime()
    {
        return m_dtStart;
    }
    
    /**
     * ���ø澯��ʼʱ��
     * @param dtStart   ��ʼʱ��
     */
    public void setStartTime(Date dtStart)
    {
        m_dtStart = dtStart;
    }
    
    /**
     * ��ø澯���ݵ���
     * @return  �澯���ݵ���
     */
    public int getLogCount()
    {
        return m_iLogCount;
    }
    
    /**
     * ���ø澯���ݵ���
     * @param iLogCount �澯���ݵ���
     */
    public void setLogCount(int iLogCount)
    {
        m_iLogCount = iLogCount;
    }
}
