package com.xtoee.devices.dcu;

import java.util.Date;

/**
 * 告警日志抄读命令的参数
 * @author zgm
 *
 */
public class CQueryParam_ReadAlarmLog
{
    private Date                    m_dtStart;                  // 告警起始时间
    private int                     m_iLogCount;                // 告警数据点数
    
    
    /**
     * 构造函数
     * @param dtStart   告警起始时间
     * @param iLogCount 告警数据点数
     */
    public CQueryParam_ReadAlarmLog(Date dtStart, int iLogCount)
    {
        m_dtStart = dtStart;
        m_iLogCount = iLogCount;
    }
    
    /**
     * 获得告警起始时间
     * @return  起始时间
     */
    public Date getStartTime()
    {
        return m_dtStart;
    }
    
    /**
     * 设置告警起始时间
     * @param dtStart   起始时间
     */
    public void setStartTime(Date dtStart)
    {
        m_dtStart = dtStart;
    }
    
    /**
     * 获得告警数据点数
     * @return  告警数据点数
     */
    public int getLogCount()
    {
        return m_iLogCount;
    }
    
    /**
     * 设置告警数据点数
     * @param iLogCount 告警数据点数
     */
    public void setLogCount(int iLogCount)
    {
        m_iLogCount = iLogCount;
    }
}
