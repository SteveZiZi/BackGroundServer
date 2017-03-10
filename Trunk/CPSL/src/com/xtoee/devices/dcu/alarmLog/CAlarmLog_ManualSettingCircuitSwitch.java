package com.xtoee.devices.dcu.alarmLog;

import org.omg.CORBA.IntHolder;

import com.xtoee.devices.IDevice;
import com.xtoee.devices.dcu.CDcuDevice;
import com.xtoee.util.NumberUtil;

/**
 * 告警日志（手动控制回路通断）
 * @author zgm
 *
 */
public class CAlarmLog_ManualSettingCircuitSwitch extends CAlarmLogBase
{
    private int                     m_iClosedCircuitId;         // 回路号
    private boolean                 m_bIsOpen;                  // 是否打开
    private int                     m_iAging;                   // 时效（分钟）
    private double                  m_dVoltage;                 // 电压值
    
    
    /**
     * 构造函数
     */
    public CAlarmLog_ManualSettingCircuitSwitch()
    {
        super(CAlarmLogFactory.AT_ManualSettingCircuitSwitch);
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
     * 是否打开
     * @return  打开/关闭
     */
    public boolean isOpened()
    {
        return m_bIsOpen;
    }
    
    /**
     * 获得日志类型字符串
     * @return  日志类型字符串
     */
    public String getLogType()
    {
        return "回路通断控制";
    }

    /**
     * 获得日志内容字符串
     * @param device 设备对象
     * @return  日志内容字符串
     */
    public String getLogContent(IDevice device)
    {
        StringBuffer    sb = new StringBuffer();
        
        
        if (m_bIsOpen)
        {
            sb.append("打开");
            sb.append(String.format("L#%02d%02d", ((CDcuDevice)device).getID(), m_iClosedCircuitId));
            sb.append("，亮度为：");
            sb.append(getLightPercent(device, m_iClosedCircuitId, m_dVoltage));
            sb.append("%，时效");
            sb.append(m_iAging);
            sb.append("分钟");
        }
        else 
        {
            sb.append("关闭");
            sb.append(String.format("L#%02d%02d", ((CDcuDevice)device).getID(), m_iClosedCircuitId));
        }
        
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
        
        // 回路号
        m_iClosedCircuitId = ucBuffer[iOffset++];
        
        // 开关状态
        m_bIsOpen = (ucBuffer[iOffset++] == 1);
        
        // 时效（分钟）
        m_iAging = ucBuffer[iOffset++];
        
        // 电压值
        m_dVoltage = NumberUtil.BcdByte2ToShort(ucBuffer, iOffset) / 10.0;
        iOffset += 2;
        
        // 返回解析的字节数
        if (null != iParseBytes)
        {
            iParseBytes.value = iOffset - iOldOffset;
        }
        
        return true;
    }
}
