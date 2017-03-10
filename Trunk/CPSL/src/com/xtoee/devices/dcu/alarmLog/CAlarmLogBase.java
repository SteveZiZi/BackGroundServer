package com.xtoee.devices.dcu.alarmLog;

import java.util.Date;
import java.util.Map;

import org.omg.CORBA.IntHolder;

import com.xtoee.devices.IDevice;
import com.xtoee.devices.dcu.CDcuDevice;

/**
 * 告警日志基类
 * @author zgm
 *
 */
public abstract class CAlarmLogBase
{
    private Date                    m_dtAlarm;                  // 告警发生时间
    private int                     m_iAlarmCode;               // 告警编码
    
    
    /**
     * 构造函数
     * @param iAlarmCode    告警编码
     */
    public CAlarmLogBase(int iAlarmCode)
    {
        m_iAlarmCode = iAlarmCode;
    }
    
    /**
     * 获得告警发生时间
     * @return  告警时间
     */
    public Date getAlarmDate()
    {
        return m_dtAlarm;
    }
    
    /**
     * 设置告警发生时间
     * @param dtAlarm 告警发生时间
     */
    public void setAlarmDate(Date dtAlarm)
    {
        m_dtAlarm = dtAlarm;
    }
    
    /**
     * 获得告警编码
     * @return  告警编码
     */
    public int getAlarmCode()
    {
        return m_iAlarmCode;
    }
    
    /**
     * 设置告警编码
     * @param iAlarmCode 告警编码
     */
    public void setAlarmCode(int iAlarmCode)
    {
        m_iAlarmCode = iAlarmCode;
    }
    
    /**
     * 根据指定回路的电压值查询Dim-Vdb映射表，获得回路的亮度
     * @param device    设备对象
     * @param iClosedCircuitId  回路号
     * @param dVoltage  回路电压
     * @return  回路的亮度
     */
    protected String getLightPercent(IDevice device, int iClosedCircuitId, double dVoltage)
    {
        // 检查输入参数
        if ((null == device))
        {
            return "输入参数错误";
        }
        
        // 获得指定回路的Dim-Vdb映射表
        CDcuDevice dcuDevice = (CDcuDevice)device;
        Map<String, Integer> mapDimVdb = dcuDevice.getDimVdbMapByCCId(iClosedCircuitId);
        if (null == mapDimVdb)
        {
            return "格式化日志信息失败（无法查到指定的回路号）";
        }
        
        // 遍历整个Dim-Vdb列表
        String strPercent = "";
        double dMinDiff = Integer.MAX_VALUE;
        for (Map.Entry<String, Integer> entry : mapDimVdb.entrySet())
        {
            // 比较电压值是否相等
            double dAbsDiff = Math.abs(dVoltage - entry.getValue());
            if (dAbsDiff < 0.000001)
            {
                strPercent = entry.getKey();
                break;
            }
            // 如果电压值不相等，那么寻找差值最小的记录
            else if(dAbsDiff < dMinDiff)
            {
                dMinDiff = dAbsDiff;
                strPercent = entry.getKey();
            }
        }
        
        return strPercent;
    }
    
    /**
     * 解析告警日志报文
     * @param ucBuffer      存放报警日志报文的数组
     * @param iOffset       告警日志报文的起始索引
     * @param iParseBytes   解析的字节数
     * @return  解析成功返回true
     */
    public abstract boolean parseFrame(byte[] ucBuffer, int iOffset, IntHolder iParseBytes);
    
    /**
     * 获得日志类型字符串
     * @return  日志类型字符串
     */
    public abstract String getLogType();
    
    /**
     * 获得日志内容字符串
     * @param device 设备对象
     * @return  日志内容字符串
     */
    public abstract String getLogContent(IDevice device);
}
