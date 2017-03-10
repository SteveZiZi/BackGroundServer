package com.xtoee.devices.dcu;

/**
 * ���⹦��
 * 
 * @author zgm
 *
 */
public enum enumSpecificFunction
{
    NoRequirement(0x00),                                        // ������Ҫ��
    RelayOpen(0x09),                                            // �̵�����
    RelayClose(0x0A),                                           // �̵����ر�
    RectifierVoltage(0x28),                                     // ��������ѹ
    RectifierOpen(0x29),                                        // ��������
    RectifierClose(0x2A),                                       // ��������
    ClosedCircuitVoltage(0x2B),                                 // ��·��ѹ
    ClosedCircuitOpen(0x2C),                                    // ��·��
    ClosedCircuitClose(0x2D);                                   // ��·��
    private int                     m_iValue;                   // ö�ٱ�����ֵ
    

    /**
     * ���캯��
     * 
     * @param iValue
     *            ö�ٱ�����ֵ
     */
    private enumSpecificFunction(int iValue)
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
    
    /**
     * ����ö��ֵ�����ö�ٶ���
     * @param iValue    ö��ֵ
     * @return  ö�ٶ���
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
