package com.xtoee.devices.dcu;

/**
 * 权限等级
 * @author zgm
 *
 */
public enum enumPrivilegeLevel
{
	Low(0x00),													// 低级权限
	Hight(0x11);												// 高级权限
    private int                     m_iValue;                   // 枚举变量的值
    
    
    /**
     * 构造函数
     * @param iValue    枚举变量的值
     */
    private enumPrivilegeLevel(int iValue)
    {
        m_iValue = iValue;
    }
    
    /**
     * 获得枚举值
     * @return  枚举变量的值
     */
    public int getValue()
    {
        return m_iValue;
    }
}
