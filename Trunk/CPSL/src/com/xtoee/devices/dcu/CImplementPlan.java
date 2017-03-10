package com.xtoee.devices.dcu;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.omg.CORBA.IntHolder;

import com.xtoee.util.NumberUtil;

/**
 * 执行方案
 * @author zgm
 *
 */
public class CImplementPlan
{
    protected Date                  m_dtStartUp;                // 启动时间
    protected List<CIoctlParam_RemoteControl> m_lstTaskContent; // 任务执行内容项组合
    
    
    /**
     * 构造函数
     * @param dtStartUp 启动时间
     */
    public CImplementPlan(Date dtStartUp)
    {
        m_dtStartUp = dtStartUp;
        m_lstTaskContent = new LinkedList<CIoctlParam_RemoteControl>();
    }
    
    /**
     * 获得启动时间
     * @return  启动时间
     */
    public Date getStartTime()
    {
        return m_dtStartUp;
    }
    
    /**
     * 获得任务内容列表
     * @return  任务内容列表
     */
    public List<CIoctlParam_RemoteControl> getTaskContentList()
    {
        return m_lstTaskContent;
    }
    
    /**
     * 添加任务内容
     * @param taskContent   任务内容
     */
    public void AddTaskContent(CIoctlParam_RemoteControl taskContent)
    {
        m_lstTaskContent.add(taskContent);
    }
    
    /**
     * 编码
     * @param ucBuffer      存放编码结果的缓冲区
     * @param iOffset       存放编码结果的起始索引
     * @param iFrameLen     编码结果的长度
     * @return  成功返回true
     */
    public boolean Encode(byte[] ucBuffer, int iOffset, IntHolder iFrameLen)
    {
        int             iOldOffset = iOffset;
        
        
        // 启动时间
        SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
        System.arraycopy(NumberUtil.StringToBcdArray(sdf.format(m_dtStartUp)), 0, ucBuffer, iOffset, 2);
        iOffset += 2;
        
        // 回路成员数
        ucBuffer[iOffset++] = (byte)m_lstTaskContent.size();
        
        // 循环遍历任务类型列表
        for (CIoctlParam_RemoteControl taskContent : m_lstTaskContent)
        {
            // 设备号（回路号）
            ucBuffer[iOffset++] = (byte)taskContent.getDeviceId();
            
            // 子回路号
            ucBuffer[iOffset++] = (byte)taskContent.getSubLoopId();
            
            // 特殊功能字
            ucBuffer[iOffset++] = (byte)taskContent.getSpecificFunc().getValue();
            
            // 数据项内容（电压值）
            System.arraycopy(NumberUtil.ShortToBcdByte2(taskContent.getDataItem() * 10), 0, ucBuffer, iOffset, 2);
            iOffset += 2;
            
            // 数据项内容（电流值）
            System.arraycopy(NumberUtil.ShortToBcdByte2(0), 0, ucBuffer, iOffset, 2);
            iOffset += 2;
        }
        
        // 编码结果的长度
        iFrameLen.value = iOffset - iOldOffset;
        return true;
    }
    
    /**
     * 解码
     * @param ucBuffer      存放待解码报文的缓冲区
     * @param iOffset       待解码的起始索引
     * @param iFrameLen     解码的数据长度
     * @return  成功返回true
     * @throws ParseException 异常信息
     */
    public boolean Decode(byte[] ucBuffer, int iOffset, IntHolder iFrameLen) throws ParseException
    {
        @SuppressWarnings("unused")
        int             iValue;
        int             iOldOffset = iOffset;
        
        
        // 启动时间
        SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
        m_dtStartUp = sdf.parse(NumberUtil.BcdArrayToString(ucBuffer, iOffset, 2));
        iOffset += 2;
        
        // 回路成员数
        int iCount = ucBuffer[iOffset++];
        
        // 循环遍历任务类型列表
        for (int i = 0; i < iCount; i++)
        {
            // 设备号（回路号）
            int iDeviceId = ucBuffer[iOffset++];
            // 子回路号
            int iSubLoopId = ucBuffer[iOffset++];
            // 特殊功能字
            enumSpecificFunction eSpecificFunc = enumSpecificFunction.getEnumByVaule(ucBuffer[iOffset++]);
            // 电压值
            int iVotage = Integer.parseInt(NumberUtil.BcdArrayToString(ucBuffer, iOffset, 2)) / 10;
            iOffset += 2;
            // 电流值
            iValue = Integer.parseInt(NumberUtil.BcdArrayToString(ucBuffer, iOffset, 2)) / 10;
            iOffset += 2;
     
            // 添加任务到容器中
            m_lstTaskContent.add(new CIoctlParam_RemoteControl(iDeviceId, iSubLoopId, eSpecificFunc, iVotage));
        }
        
        // 解码的长度
        iFrameLen.value = iOffset - iOldOffset;
        return true;
    }
}
