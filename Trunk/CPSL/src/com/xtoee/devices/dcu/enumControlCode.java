package com.xtoee.devices.dcu;

/**
 * 控制码
 * 
 * @author zgm
 *
 */
public enum enumControlCode
{
    ReadRequest(0x01),                                          // 读（请求）
    WriteRequest(0x08),                                         // 写（请求）
    ReadAlarmLog(0x19),                                         // 读告警日志
    LoginResponse(0x21),                                        // 登入（响应）
    LogoutResponse(0x22),                                       // 登出（响应）
    HeartBeatResponse(0x24),                                    // 心跳检测（响应）
    PadLoginRequest(0xB1);                                      // Pad登入（请求）
    private int m_iValue;                                       // 枚举变量的值

    
    /**
     * 构造函数
     * 
     * @param iValue
     *            枚举变量的值
     */
    private enumControlCode(int iValue)
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
}
