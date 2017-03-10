package com.xtoee.devices.dcu.alarmLog;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.omg.CORBA.IntHolder;

import com.xtoee.devices.IDevice;
import com.xtoee.devices.dcu.CDcuDevice;
import com.xtoee.util.NumberUtil;

/**
 * 告警日志（任务控制回路通断）
 * @author zgm
 *
 */
public class CAlarmLog_TaskSettingCircuitSwitch extends CAlarmLogBase
{
    private int                     m_iTaskId;                  // 任务号
    private int                     m_iClosedCircuitId;         // 回路号
    private boolean                 m_bIsOpen;                  // 是否打开
    private Date                    m_dtEndTime;                // 任务结束时间
    
    
    /**
     * 构造函数
     */
    public CAlarmLog_TaskSettingCircuitSwitch()
    {
        super(CAlarmLogFactory.AT_TaskSettingCircuitSwitch);
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
        return "任务控制（回路通断）";
    }

    /**
     * 获得日志内容字符串
     * @param device 设备对象
     * @return  日志内容字符串
     */
    public String getLogContent(IDevice device)
    {
        StringBuffer    sb = new StringBuffer();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        
        sb.append(String.format("任务%02d：", m_iTaskId));
        sb.append(m_bIsOpen? "打开": "关闭");
        sb.append(String.format("L#%02d%02d，", ((CDcuDevice)device).getID(), m_iClosedCircuitId));
        sb.append(sdf.format(m_dtEndTime));
        sb.append("结束");
        
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
        
        // 开关状态
        m_bIsOpen = (ucBuffer[iOffset++] == 1);
        
        // 任务结束时间
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
            String strDatetime = NumberUtil.BcdArrayToString(ucBuffer, iOffset, 5);
            m_dtEndTime = sdf.parse(strDatetime);
            iOffset += 5;
        }
        catch (java.text.ParseException e)
        {
            System.err.println(e.toString());
            return false;
        }
        
        // 返回解析的字节数
        if (null != iParseBytes)
        {
            iParseBytes.value = iOffset - iOldOffset;
        }
        
        return true;
    }
}
