package com.xtoee.devices.dcu.deviceNode;

import org.omg.CORBA.IntHolder;

import com.xtoee.devices.dcu.enumSpecificFunction;
import com.xtoee.util.NumberUtil;

/**
 * 模块关联设备
 * @author zgm
 *
 */
public class CSubRoad
{
    private int                     m_iSubRoadId;               // 设备子路号（1~32）
    private enumSpecificFunction    m_eSpecialFun;              // 特殊功能定义
    private int                     m_iRelateModuleId;          // 子路关联的设备节点编号（1~16）
    private int                     m_iRelateModuleSubRoad;     // 子路关联的设备子路号
    
    
    /**
     * 构造函数
     */
    public CSubRoad()
    {
        super();
    }

    /**
     * 构造函数
     * @param subRoadId             设备子路号（1~32）
     * @param specialFun            特殊功能定义
     * @param relateModuleId        子路关联的设备节点编号（1~16）
     * @param relateModuleSubRoad   子路关联的设备子路号
     */
    public CSubRoad(int subRoadId, enumSpecificFunction specialFun, int relateModuleId, int relateModuleSubRoad)
    {
        super();
        m_iSubRoadId = subRoadId;
        m_eSpecialFun = specialFun;
        m_iRelateModuleId = relateModuleId;
        m_iRelateModuleSubRoad = relateModuleSubRoad;
    }

    /**
     * 获得设备子路号（1~32）
     * @return  设备子路号（1~32）
     */
    public int getSubRoadId()
    {
        return m_iSubRoadId;
    }
    
    /**
     * 设置设备子路号（1~32）
     * @param subRoadId 设备子路号（1~32）
     */
    public void setSubRoadId(int subRoadId)
    {
        m_iSubRoadId = subRoadId;
    }
    
    /**
     * 获得特殊功能定义
     * @return  特殊功能定义
     */
    public enumSpecificFunction getSpecialFun()
    {
        return m_eSpecialFun;
    }
    
    /**
     * 设置特殊功能定义
     * @param specialFun    特殊功能定义
     */
    public void setSpecialFun(enumSpecificFunction specialFun)
    {
        m_eSpecialFun = specialFun;
    }
    
    /**
     * 获得子路关联的设备节点编号（1~16）
     * @return  子路关联的设备节点编号（1~16）
     */
    public int getRelateModuleId()
    {
        return m_iRelateModuleId;
    }
    
    /**
     * 设置子路关联的设备节点编号（1~16）
     * @param relateModuleId    子路关联的设备节点编号（1~16）
     */
    public void setRelateModuleId(int relateModuleId)
    {
        m_iRelateModuleId = relateModuleId;
    }
    
    /**
     * 获得子路关联的设备子路号
     * @return  子路关联的设备子路号
     */
    public int getRelateModuleSubRoad()
    {
        return m_iRelateModuleSubRoad;
    }
    
    /**
     * 设置子路关联的设备子路号
     * @param relateModuleSubRoad   子路关联的设备子路号
     */
    public void setRelateModuleSubRoad(int relateModuleSubRoad)
    {
        m_iRelateModuleSubRoad = relateModuleSubRoad;
    }
    
    /**
     * 编码
     * @param ucBuffer      存放编码结果的缓冲区
     * @param iOffset       存放编码结果的起始索引
     * @param iEncodeBytes  编码结果的长度
     * @return  成功返回true
     */
    public boolean Encode(byte[] ucBuffer, int iOffset, IntHolder iEncodeBytes)
    {
        int             iOldOffset = iOffset;
        
        
        // 设备子路号（1~32）
        ucBuffer[iOffset++] = (byte)m_iSubRoadId;
        
        // 特殊功能定义
        ucBuffer[iOffset++] = (byte)m_eSpecialFun.getValue();
        
        // 子路关联的设备节点编号（1~16）
        ucBuffer[iOffset++] = (byte)m_iRelateModuleId;
        
        // 子路关联的设备子路号
        System.arraycopy(NumberUtil.intToByte4(m_iRelateModuleSubRoad), 0, ucBuffer, iOffset, 4);
        iOffset += 4;
        
        // 返回编码的字节数
        if (null != iEncodeBytes)
        {
            iEncodeBytes.value = iOffset - iOldOffset;
        }
        
        return true;
    }
    
    /**
     * 解析子路报文
     * @param ucBuffer      存放子路报文的数组
     * @param iOffset       子路报文的起始索引
     * @param iParseBytes   解析的字节数
     * @return  解析成功返回true
     */
    public boolean parseFrame(byte[] ucBuffer, int iOffset, IntHolder iParseBytes)
    {
        int             iOldOffset = iOffset;
        
        
        // 设备子路号（1~32）
        m_iSubRoadId = ucBuffer[iOffset++];
        
        // 特殊功能定义
        m_eSpecialFun = enumSpecificFunction.getEnumByVaule(ucBuffer[iOffset++]);
        
        // 子路关联的设备节点编号（1~16）
        m_iRelateModuleId = ucBuffer[iOffset++];
        
        // 子路关联的设备子路号
        m_iRelateModuleSubRoad = NumberUtil.byte4ToInt(ucBuffer, iOffset);
        iOffset += 4;
        
        // 返回解析的字节数
        if (null != iParseBytes)
        {
            iParseBytes.value = iOffset - iOldOffset;
        }
        
        return true;
    }
}
