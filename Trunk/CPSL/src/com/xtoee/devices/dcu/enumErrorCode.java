package com.xtoee.devices.dcu;

/**
 * ������
 * 
 * @author zgm
 *
 */
public enum enumErrorCode
{
    Success(0x00),                                              // ��ȷ
    NoReturn(0x01),                                             // �м�����û�з���
    Illegal(0x02),                                              // �������ݷǷ�
    NoPermission(0x03),                                         // ����Ȩ�޲���
    ItemNotExist(0x04),                                         // �޴�������
    TimeLapse(0x05),                                            // ����ʱ��ʧЧ
    TargetNotExist(0x11),                                       // Ŀ���ַ������
    FailedToSent(0x12),                                         // ����ʧ��
    FrameTooLong(0x13),                                         // ����Ϣ̫֡��
    Unknown(0xff);                                              // δ֪����
    private int m_iValue; // ö�ٱ�����ֵ

    
    /**
     * ���캯��
     * 
     * @param iValue
     *            ö�ٱ�����ֵ
     */
    private enumErrorCode(int iValue)
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
     * ���ݴ�������ֵ�����ش��������
     * @param iValue    ��������ֵ
     * @return  ���������
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
