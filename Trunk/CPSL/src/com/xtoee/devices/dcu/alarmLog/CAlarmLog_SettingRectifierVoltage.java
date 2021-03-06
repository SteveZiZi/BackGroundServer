package com.xtoee.devices.dcu.alarmLog;

import org.omg.CORBA.IntHolder;

import com.xtoee.devices.IDevice;
import com.xtoee.devices.dcu.CDcuDevice;
import com.xtoee.util.NumberUtil;

/**
 * 告警日志（整流器电压设置）
 * @author zgm
 *
 */
public class CAlarmLog_SettingRectifierVoltage extends CAlarmLogBase
{
	private int                     m_iClosedCircuitId;         // 回路号
    private int                     m_iDeviceId;                // 设备号
    private double                  m_dVoltage;                 // 电压值
    
    
    /**
     * 构造函数
     */
    public CAlarmLog_SettingRectifierVoltage()
    {
        super(CAlarmLogFactory.AT_SettingRectifierVoltage);
    }
    
    /**
     * 获得设备号
     * @return  设备号
     */
    public int getDeviceId()
    {
        return m_iDeviceId;
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
        return "整流器电压设置";
    }

    /**
     * 获得日志内容字符串
     * @param device 设备对象
     * @return  日志内容字符串
     */
    public String getLogContent(IDevice device)
    {
        StringBuffer    sb = new StringBuffer();
        
        
        sb.append("设置R#");
        sb.append(String.format("%02d%02d%02d", ((CDcuDevice)device).getID(), m_iClosedCircuitId, m_iDeviceId));
        sb.append("电压至");
        sb.append(String.format("%.1f，", m_dVoltage));
        
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
        
        // 设备号
        m_iDeviceId = ucBuffer[iOffset++];
        
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
