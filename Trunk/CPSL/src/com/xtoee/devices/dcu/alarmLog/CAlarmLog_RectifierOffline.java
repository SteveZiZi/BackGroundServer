package com.xtoee.devices.dcu.alarmLog;

import org.omg.CORBA.IntHolder;

import com.xtoee.devices.IDevice;
import com.xtoee.devices.dcu.CDcuDevice;

/**
 * 告警日志（整流器掉线）
 * @author zgm
 *
 */
public class CAlarmLog_RectifierOffline extends CAlarmLogBase
{
	private int                     m_iClosedCircuitId;         // 回路号
    private int                     m_iDeviceId;                // 设备号
    private int                     m_iMsgType;                 // 1：掉线；0：上线
    
    
    /**
     * 构造函数
     */
    public CAlarmLog_RectifierOffline()
    {
        super(CAlarmLogFactory.AT_RectifierOffline);
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
     * 获得消息类型
     * @return  1：掉线；2：上线
     */
    public int getMsgType()
    {
        return m_iMsgType;
    }
    
    /**
     * 获得日志类型字符串
     * @return  日志类型字符串
     */
    public String getLogType()
    {
        return "整流器状态";
    }

    /**
     * 获得日志内容字符串
     * @param device 设备对象
     * @return  日志内容字符串
     */
    public String getLogContent(IDevice device)
    {
        StringBuffer    sb = new StringBuffer();

        
        sb.append(String.format("R#%02d%02d%02d", ((CDcuDevice)device).getID(), m_iClosedCircuitId, m_iDeviceId));
        sb.append((1 == m_iMsgType)? "掉线": "上线");
        
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
        
        // 掉线/上线
        m_iMsgType = ucBuffer[iOffset++];
        
        // 返回解析的字节数
        if (null != iParseBytes)
        {
            iParseBytes.value = iOffset - iOldOffset;
        }
        
        return true;
    }
}
