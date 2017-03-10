package com.xtoee.devices.dcu;

/**
 * 特殊功能
 * 
 * @author zgm
 *
 */
public enum enumSpecificFunction
{
    NoRequirement(0x00),                                        // 无特殊要求
    RelayOpen(0x09),                                            // 继电器打开
    RelayClose(0x0A),                                           // 继电器关闭
    RectifierVoltage(0x28),                                     // 整流器电压
    RectifierOpen(0x29),                                        // 整流器开
    RectifierClose(0x2A),                                       // 整流器关
    ClosedCircuitVoltage(0x2B),                                 // 回路电压
    ClosedCircuitOpen(0x2C),                                    // 回路开
    ClosedCircuitClose(0x2D);                                   // 回路关
    private int                     m_iValue;                   // 枚举变量的值
    

    /**
     * 构造函数
     * 
     * @param iValue
     *            枚举变量的值
     */
    private enumSpecificFunction(int iValue)
    {
        m_iValue = iValue;
    }

    /**
     * 获得枚举值
     * 
     * @return 枚举变量的值
     */
    public int getValue()
    {
        return m_iValue;
    }
    
    /**
     * 根据枚举值，获得枚举对象
     * @param iValue    枚举值
     * @return  枚举对象
     */
    public static enumSpecificFunction getEnumByVaule(int iValue)
    {
        switch (iValue)
        {
        case 0x09:
            return RelayOpen;
            
        case 0x0A:
            return RelayClose;
            
        case 0x28:
            return RectifierVoltage;
            
        case 0x29:
            return RectifierOpen;
            
        case 0x2A:
            return RectifierClose;
        
        case 0x2B:
            return ClosedCircuitVoltage;
            
        case 0x2C:
            return ClosedCircuitOpen;
            
        case 0x2D:
            return ClosedCircuitClose;

        default:
            break;
        }
        
        return NoRequirement;
    }
}
