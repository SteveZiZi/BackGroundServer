package com.xtoee.devices.dcu;

/**
 * 远程控制命令的参数
 * 
 * @author zgm
 *
 */
public class CIoctlParam_RemoteControl
{
    private int                     m_iDeviceId;                // 设备号（回路号、继电器号）
    protected int                   m_iSubLoopId;               // 子回路号
    protected enumSpecificFunction  m_eSpecificFunc;            // 特殊功能字
    protected int                   m_iDataItem;                // 数据项内容

    
    /**
     * 构造函数
     * @param iDeviceId         设备号（回路号、继电器号）
     * @param iSubLoopId        子回路号
     * @param eSpecificFunc     特殊功能字
     * @param iDataItem         数据项内容
     */
    public CIoctlParam_RemoteControl(int iDeviceId, int iSubLoopId, enumSpecificFunction eSpecificFunc, int iDataItem)
    {
        m_iDeviceId = iDeviceId;
        m_iSubLoopId = iSubLoopId;
        m_eSpecificFunc = eSpecificFunc;
        m_iDataItem = iDataItem;
    }
    
    /**
     * 获得设备号（回路号、继电器号）
     * @return  设备号
     */
    public int getDeviceId()
    {
        return m_iDeviceId;
    }

    /**
     * 设置设备号（回路号、继电器号）
     * @param iDeviceId 设备号
     */
    public void setDeviceId(int iDeviceId)
    {
        m_iDeviceId = iDeviceId;
    }

    /**
     * 获得子回路号
     * @return  子回路号
     */
    public int getSubLoopId()
    {
        return m_iSubLoopId;
    }
    
    /**
     * 设置子回路号
     * @param iSubLoopId    子回路号
     */
    public void setSubLoopId(int iSubLoopId)
    {
        m_iSubLoopId = iSubLoopId;
    }
    
    /**
     * 获得特殊功能字
     * @return  特殊功能字
     */
    public enumSpecificFunction getSpecificFunc()
    {
        return m_eSpecificFunc;
    }
    
    /**
     * 设置特殊功能字
     * @param eSpecificFunc 特殊功能字
     */
    public void setSpecificFunc(enumSpecificFunction eSpecificFunc)
    {
        m_eSpecificFunc = eSpecificFunc;
    }
    
    /**
     * 获得数据项内容
     * @return  数据项内容
     */
    public int getDataItem()
    {
        return m_iDataItem;
    }
    
    /**
     * 设置数据项内容
     * @param iDataItem 数据项内容
     */
    public void setDataItem(int iDataItem)
    {
        m_iDataItem = iDataItem;
    }
}
