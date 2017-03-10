package com.xtoee.devices.dcu.alarmLog;

import org.omg.CORBA.IntHolder;

import com.xtoee.devices.IDevice;
import com.xtoee.devices.dcu.CDcuDevice;

/**
 * 告警日志（子回路通断控制）
 * @author zgm
 *
 */
public class CAlarmLog_SwitchSubLoop extends CAlarmLogBase
{
    private int                     m_iDeviceId;                // 设备号
    private int                     m_iSubLoopId;               // 子路号
    private boolean                 m_bIsOpen;                  // 是否打开
    
    
    /**
     * 构造函数
     */
    public CAlarmLog_SwitchSubLoop()
    {
        super(CAlarmLogFactory.AT_SwitchSubLoop);
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
     * 获得子路号
     * @return  子路号
     */
    public int getSubLoopId()
    {
        return m_iSubLoopId;
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
        return "子回路通断控制";
    }

    /**
     * 获得日志内容字符串
     * @param device 设备对象
     * @return  日志内容字符串
     */
    public String getLogContent(IDevice device)
    {
        StringBuffer    sb = new StringBuffer();

        
        sb.append(m_bIsOpen? "打开": "关闭");
        sb.append(String.format("L#%02d%02d下的%04d号子回路", ((CDcuDevice)device).getID(), m_iDeviceId, m_iSubLoopId));
        
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
        
        // 设备号
        m_iDeviceId = ucBuffer[iOffset++];
        
        // 子路号
        m_iSubLoopId = ucBuffer[iOffset++];
        
        // 开关状态
        m_bIsOpen = (ucBuffer[iOffset++] == 1);
        
        // 返回解析的字节数
        if (null != iParseBytes)
        {
            iParseBytes.value = iOffset - iOldOffset;
        }
        
        return true;
    }
}
