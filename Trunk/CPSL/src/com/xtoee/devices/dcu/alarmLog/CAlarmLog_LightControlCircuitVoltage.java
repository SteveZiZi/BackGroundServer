package com.xtoee.devices.dcu.alarmLog;

import org.omg.CORBA.IntHolder;

import com.xtoee.devices.IDevice;
import com.xtoee.devices.dcu.CDcuDevice;
import com.xtoee.util.NumberUtil;

/**
 * 光控任务（回路电压设置）
 * @author zgm
 *
 */
public class CAlarmLog_LightControlCircuitVoltage extends CAlarmLogBase
{
    private int                     m_iTaskId;                  // 任务号
    private int                     m_iClosedCircuitId;         // 回路号
    private double                  m_dVoltage;                 // 电压值
    private int                     m_iAging;                   // 时效（分钟）
    
    
    /**
     * 构造函数
     */
    public CAlarmLog_LightControlCircuitVoltage()
    {
        super(CAlarmLogFactory.AT_LightControlCircuitVoltage);
    }
    
    /**
     * 获得任务号
     * @return  任务号
     */
    public int getTaskId()
    {
        return m_iTaskId;
    }
    
    /**
     * 获得回路号
     * @return  回路号
     */
    public int getClosedCircuitId()
    {
        return m_iClosedCircuitId;
    }
    
    /**
     * 获得电压值
     * @return  电压值
     */
    public double getVoltage()
    {
        return m_dVoltage;
    }
    
    /**
     * 获得日志类型字符串
     * @return  日志类型字符串
     */
    public String getLogType()
    {
        return "光控任务（回路电压设置）";
    }

    /**
     * 获得日志内容字符串
     * @param device 设备对象
     * @return  日志内容字符串
     */
    public String getLogContent(IDevice device)
    {
        StringBuffer    sb = new StringBuffer();
        
        
        // 检查输入参数
        if ((null == device) || !(device instanceof CDcuDevice))
        {
            return "格式化日志信息失败（输入参数错误）";
        }
        
        sb.append(String.format("任务%02d：", m_iTaskId));
        sb.append(String.format("设置L#%02d%02d", device.getID(), m_iClosedCircuitId));
        sb.append("亮度至");
        sb.append(getLightPercent(device, m_iClosedCircuitId, m_dVoltage));
        sb.append("%");
        sb.append("，时效");
        sb.append(m_iAging);
        sb.append("分钟");
        
        return sb.toString();
    }

    /**
     * 解析告警日志报文
     * @param ucBuffer      存放报警日志报文的数组
     * @param iOffset       告警日志报文的起始索引
     * @param iParseBytes   解析的字节数
     * @return  解析成功返回true
     */
    public boolean parseFrame(byte[] ucBuffer, int iOffset, IntHolder iParseBytes)
    {
        int             iOldOffset = iOffset;
        
        
        // 内容长度
        @SuppressWarnings("unused")
        int iContentLen = ucBuffer[iOffset++];
        
        // 任务号
        m_iTaskId = ucBuffer[iOffset++];
        
        // 回路号
        m_iClosedCircuitId = ucBuffer[iOffset++];
        
        // 电压值
        m_dVoltage = NumberUtil.BcdByte2ToShort(ucBuffer, iOffset) / 10.0;
        iOffset += 2;
        
        // 时长
        int iHigh = ucBuffer[iOffset++];
        int iLow = ucBuffer[iOffset++];
        m_iAging = ((iHigh << 8) & 0xff00) | (iLow & 0xff);
        
        // 第8~10字节保留
        @SuppressWarnings("unused")
        int iValue = ucBuffer[iOffset++];
        iValue = ucBuffer[iOffset++];
        iValue = ucBuffer[iOffset++];
        
        // 返回解析的字节数
        if (null != iParseBytes)
        {
            iParseBytes.value = iOffset - iOldOffset;
        }
        
        return true;
    }
}
