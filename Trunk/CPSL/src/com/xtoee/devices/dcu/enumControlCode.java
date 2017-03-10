package com.xtoee.devices.dcu;

/**
 * ������
 * 
 * @author zgm
 *
 */
public enum enumControlCode
{
    ReadRequest(0x01),                                          // ��������
    WriteRequest(0x08),                                         // д������
    ReadAlarmLog(0x19),                                         // ���澯��־
    LoginResponse(0x21),                                        // ���루��Ӧ��
    LogoutResponse(0x22),                                       // �ǳ�����Ӧ��
    HeartBeatResponse(0x24),                                    // ������⣨��Ӧ��
    PadLoginRequest(0xB1);                                      // Pad���루����
    private int m_iValue;                                       // ö�ٱ�����ֵ

    
    /**
     * ���캯��
     * 
     * @param iValue
     *            ö�ٱ�����ֵ
     */
    private enumControlCode(int iValue)
    {
        m_iValue = iValue;
    }

    /**
     * ���ö��ֵ
     * 
     * @return ö�ٱ�����ֵ
     */
    public int getValue()
    {
        return m_iValue;
    }
}
