package com.xtoee.devices.dcu;

/**
 * ��������ִ�н��
 * @author zgm
 *
 */
public class CIoctlResult
{
    protected enumErrorCode         m_eErrorCode;               // ������
    
    
    /**
     * ��ô�����
     * @return  ������
     */
    public enumErrorCode getErrorCode()
    {
        return m_eErrorCode;
    }
    
    /**
     * ���ô�����
     * @param eErrorCode    ������
     */
    public void setErrorCode(enumErrorCode eErrorCode)
    {
        m_eErrorCode = eErrorCode;
    }
}
