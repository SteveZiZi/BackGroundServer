package com.xtoee.devices.dcu;

/**
 * Ȩ�޵ȼ�
 * @author zgm
 *
 */
public enum enumPrivilegeLevel
{
	Low(0x00),													// �ͼ�Ȩ��
	Hight(0x11);												// �߼�Ȩ��
    private int                     m_iValue;                   // ö�ٱ�����ֵ
    
    
    /**
     * ���캯��
     * @param iValue    ö�ٱ�����ֵ
     */
    private enumPrivilegeLevel(int iValue)
    {
        m_iValue = iValue;
    }
    
    /**
     * ���ö��ֵ
     * @return  ö�ٱ�����ֵ
     */
    public int getValue()
    {
        return m_iValue;
    }
}
