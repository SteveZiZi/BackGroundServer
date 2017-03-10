package com.xtoee.devices.dcu;

/**
 * 控制码
 * 
 * @author zgm
 *
 */
public enum enumErrorCode
{
    Success(0x00),                                              // 正确
    NoReturn(0x01),                                             // 中继命令没有返回
    Illegal(0x02),                                              // 设置内容非法
    NoPermission(0x03),                                         // 密码权限不足
    ItemNotExist(0x04),                                         // 无此项数据
    TimeLapse(0x05),                                            // 命令时间失效
    TargetNotExist(0x11),                                       // 目标地址不存在
    FailedToSent(0x12),                                         // 发送失败
    FrameTooLong(0x13),                                         // 短消息帧太长
    Unknown(0xff);                                              // 未知错误
    private int m_iValue; // 枚举变量的值

    
    /**
     * 构造函数
     * 
     * @param iValue
     *            枚举变量的值
     */
    private enumErrorCode(int iValue)
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
     * 根据错误码数值，返回错误码对象
     * @param iValue    错误码数值
     * @return  错误码对象
     */
    public static enumErrorCode getErrorByInt(int iValue)
    {
        if (iValue == Success.getValue())
        {
            return Success;
        }
        else if (iValue == NoReturn.getValue())
        {
            return NoReturn;
        }
        else if (iValue == Illegal.getValue())
        {
            return Illegal;
        }
        else if (iValue == NoPermission.getValue())
        {
            return NoPermission;
        }
        else if (iValue == ItemNotExist.getValue())
        {
            return ItemNotExist;
        }
        else if (iValue == TimeLapse.getValue())
        {
            return TimeLapse;
        }
        else if (iValue == TargetNotExist.getValue())
        {
            return TargetNotExist;
        }
        else if (iValue == FailedToSent.getValue()) 
        {
            return FailedToSent;
        }
        else if (iValue == FrameTooLong.getValue())
        {
            return FrameTooLong;
        }
        
        return enumErrorCode.Unknown;
    }
}
