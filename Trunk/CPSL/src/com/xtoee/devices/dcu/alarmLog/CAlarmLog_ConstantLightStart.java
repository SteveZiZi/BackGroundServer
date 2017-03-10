package com.xtoee.devices.dcu.alarmLog;

import org.omg.CORBA.IntHolder;

import com.xtoee.devices.IDevice;
import com.xtoee.devices.dcu.CDcuDevice;

/**
 * 恒照任务开始
 * @author zgm
 *
 */
public class CAlarmLog_ConstantLightStart extends CAlarmLogBase
{
    private int                     m_iTaskId;                  // 任务号
    private int                     m_iClosedCircuitId;         // 回路号
    
    
    /**
     * 构造函数
     */
    public CAlarmLog_ConstantLightStart()
    {
        super(CAlarmLogFactory.AT_ConstantLightStart);
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
     * 获得日志类型字符串
     * @return  日志类型字符串
     */
    public String getLogType()
    {
        return "恒照任务开始";
    }

    /**
     * 获得日志内容字符串
     * @param device 设备对象
     * @return  日志内容字符串
     */
    public String getLogContent(IDevice device)
    {
        StringBuffer    sb = new StringBuffer();
        
        
        sb.append(String.format("任务%02d：", m_iTaskId));
        sb.append(String.format("回路L#%02d%02d", ((CDcuDevice)device).getID(), m_iClosedCircuitId));
        sb.append("恒照任务开始");
        
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
        
        // 返回解析的字节数
        if (null != iParseBytes)
        {
            iParseBytes.value = iOffset - iOldOffset;
        }
        
        return true;
    }
}
