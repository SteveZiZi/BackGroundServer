package com.xtoee.devices.dcu.alarmLog;

import org.omg.CORBA.IntHolder;

import com.xtoee.devices.IDevice;

/**
 * 告警日志（档案参数清零）
 * @author zgm
 *
 */
public class CAlarmLog_ArchiveParamCleared extends CAlarmLogBase
{
    /**
     * 构造函数
     */
    public CAlarmLog_ArchiveParamCleared()
    {
        super(CAlarmLogFactory.AT_ArchiveParamCleared);
    }
    
    /**
     * 获得日志类型字符串
     * @return  日志类型字符串
     */
    public String getLogType()
    {
        return "档案参数清零";
    }

    /**
     * 获得日志内容字符串
     * @param device 设备对象
     * @return  日志内容字符串
     */
    public String getLogContent(IDevice device)
    {
        return "删除档案";
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
        
        // 返回解析的字节数
        if (null != iParseBytes)
        {
            iParseBytes.value = iOffset - iOldOffset;
        }
        
        return true;
    }
}
