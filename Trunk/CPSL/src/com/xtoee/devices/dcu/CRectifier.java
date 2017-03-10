package com.xtoee.devices.dcu;

import com.xtoee.util.NumberUtil;

/**
 * ��������
 * 
 * @author zgm
 *
 */
public class CRectifier
{
    private int                     m_nID;                      // ��������
    private double                  m_dVoltage;                 // ��ѹ
    private double                  m_dCurrent;                 // ����
    private double                  m_dTemperature;             // �¶�
    private int                     m_iRotationRate;            // ת��
    private byte                    m_ucAlarmValue;             // ģ��澯��
    private byte                    m_ucProtectionType;         // ģ�鱣������

    
    /**
     * ���캯��
     * 
     * @param nId
     *            ��������
     */
    public CRectifier(int nId)
    {
        m_nID = nId;
    }

    /**
     * �����������
     * 
     * @return ��������
     */
    public int getID()
    {
        return m_nID;
    }

    /**
     * ��õ�ѹֵ
     * 
     * @return ��ѹֵ
     */
    public double getVoltage()
    {
        return m_dVoltage;
    }

    /**
     * ��õ���ֵ
     * 
     * @return ����ֵ
     */
    public double getCurrent()
    {
        return m_dCurrent;
    }

    /**
     * ��ù���
     * 
     * @return ����
     */
    public double getPower()
    {
        return m_dVoltage * m_dCurrent;
    }

    /**
     * ����¶�ֵ
     * 
     * @return �¶�ֵ
     */
    public double getTemperature()
    {
        return m_dTemperature;
    }

    /**
     * ���ת��
     * 
     * @return ת��
     */
    public int getRotationRate()
    {
        return m_iRotationRate;
    }

    /**
     * ���ģ��澯ֵ
     * 
     * @return ģ��澯ֵ
     */
    public byte getAlarmValue()
    {
        return m_ucAlarmValue;
    }

    /**
     * ���ģ�鱣������
     * 
     * @return ģ�鱣������
     */
    public byte getProtectionType()
    {
        return m_ucProtectionType;
    }

    /**
     * ���������״̬
     * 
     * @return ������״̬
     */
    public String getStatus()
    {
        String strRet = "����";
        StringBuffer sb = new StringBuffer();

        
        // ģ��澯��
        sb.append(((m_ucAlarmValue & 0x01) == 0) ? "" : "������־��������");
        sb.append(((m_ucAlarmValue & 0x02) == 0) ? "" : "ģ����ϣ�������");
        sb.append(((m_ucAlarmValue & 0x04) == 0) ? "" : "ģ�鿪�ػ����ػ���");
        sb.append(((m_ucAlarmValue & 0x08) == 0) ? "" : "�ڲ��������ϣ�������");
        sb.append(((m_ucAlarmValue & 0x10) == 0) ? "" : "ģ����ȹ��ϣ�������");
        sb.append(((m_ucAlarmValue & 0x20) == 0) ? "" : "�������ϣ�������");
        sb.append(((m_ucAlarmValue & 0x40) == 0) ? "" : "ģ�鱣����������");
        sb.append(((m_ucAlarmValue & 0x80) == 0) ? "" : "ͨѶ���ϣ��澯��");

        // ģ�鱣������
        sb.append(((m_ucProtectionType & 0x01) == 0) ? "" : "��������·������������");
        sb.append(((m_ucProtectionType & 0x02) == 0) ? "" : "����������������");
        sb.append(((m_ucProtectionType & 0x04) == 0) ? "" : "���Ƿѹ��������");
        sb.append(((m_ucProtectionType & 0x08) == 0) ? "" : "�����ѹ��������");
        sb.append(((m_ucProtectionType & 0x10) == 0) ? "" : "ĸ�߲�ƽ�⣺������");
        sb.append(((m_ucProtectionType & 0x20) == 0) ? "" : "�����ѹ��������");
        sb.append(((m_ucProtectionType & 0x40) == 0) ? "" : "����Ƿѹ��������");
        sb.append(((m_ucProtectionType & 0x80) == 0) ? "" : "���£�������");

        // �Ƴ�ĩβ�ģ���
        if (sb.length() > 0)
        {
            strRet = sb.substring(0, sb.length() - 1);
        }

        return strRet;
    }

    /**
     * ����״̬��Ϣ
     * 
     * @param ucBuffer
     *            ���״̬���ĵ�����
     * @param iStartIdx
     *            ״̬���ĵ���ʼ����
     * @return �����ɹ�����true
     */
    public boolean parseFrame(byte[] ucBuffer, int iStartIdx)
    {
        // ����������
        if ((null == ucBuffer) || (iStartIdx < 0) || (iStartIdx + 12 > ucBuffer.length))
        {
            return false;
        }

        // ���������ID��
        if (m_nID != NumberUtil.byte2ToUnsignedShort(ucBuffer, iStartIdx))
        {
            return false;
        }
        iStartIdx += 2;

        // ��·��
        @SuppressWarnings("unused")
        int iValue = ucBuffer[iStartIdx++];

        // ��ѹ
        m_dVoltage = NumberUtil.BcdByte2ToShort(ucBuffer, iStartIdx) / 10.0;
        iStartIdx += 2;

        // ����
        m_dCurrent = NumberUtil.BcdByte2ToShort(ucBuffer, iStartIdx) / 10.0;
        iStartIdx += 2;

        // �¶�
        m_dTemperature = ucBuffer[iStartIdx++];

        // ת��
        m_iRotationRate = NumberUtil.byte2ToUnsignedShort(ucBuffer, iStartIdx);
        iStartIdx += 2;

        // ģ��澯��
        m_ucAlarmValue = ucBuffer[iStartIdx++];

        // ģ�鱣������
        m_ucProtectionType = ucBuffer[iStartIdx++];

        return true;
    }
}
