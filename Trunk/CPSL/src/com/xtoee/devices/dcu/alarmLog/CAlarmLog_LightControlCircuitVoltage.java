package com.xtoee.devices.dcu.alarmLog;

import org.omg.CORBA.IntHolder;

import com.xtoee.devices.IDevice;
import com.xtoee.devices.dcu.CDcuDevice;
import com.xtoee.util.NumberUtil;

/**
 * ������񣨻�·��ѹ���ã�
 * @author zgm
 *
 */
public class CAlarmLog_LightControlCircuitVoltage extends CAlarmLogBase
{
    private int                     m_iTaskId;                  // �����
    private int                     m_iClosedCircuitId;         // ��·��
    private double                  m_dVoltage;                 // ��ѹֵ
    private int                     m_iAging;                   // ʱЧ�����ӣ�
    
    
    /**
     * ���캯��
     */
    public CAlarmLog_LightControlCircuitVoltage()
    {
        super(CAlarmLogFactory.AT_LightControlCircuitVoltage);
    }
    
    /**
     * ��������
     * @return  �����
     */
    public int getTaskId()
    {
        return m_iTaskId;
    }
    
    /**
     * ��û�·��
     * @return  ��·��
     */
    public int getClosedCircuitId()
    {
        return m_iClosedCircuitId;
    }
    
    /**
     * ��õ�ѹֵ
     * @return  ��ѹֵ
     */
    public double getVoltage()
    {
        return m_dVoltage;
    }
    
    /**
     * �����־�����ַ���
     * @return  ��־�����ַ���
     */
    public String getLogType()
    {
        return "������񣨻�·��ѹ���ã�";
    }

    /**
     * �����־�����ַ���
     * @param device �豸����
     * @return  ��־�����ַ���
     */
    public String getLogContent(IDevice device)
    {
        StringBuffer    sb = new StringBuffer();
        
        
        // ����������
        if ((null == device) || !(device instanceof CDcuDevice))
        {
            return "��ʽ����־��Ϣʧ�ܣ������������";
        }
        
        sb.append(String.format("����%02d��", m_iTaskId));
        sb.append(String.format("����L#%02d%02d", device.getID(), m_iClosedCircuitId));
        sb.append("������");
        sb.append(getLightPercent(device, m_iClosedCircuitId, m_dVoltage));
        sb.append("%");
        sb.append("��ʱЧ");
        sb.append(m_iAging);
        sb.append("����");
        
        return sb.toString();
    }

    /**
     * �����澯��־����
     * @param ucBuffer      ��ű�����־���ĵ�����
     * @param iOffset       �澯��־���ĵ���ʼ����
     * @param iParseBytes   �������ֽ���
     * @return  �����ɹ�����true
     */
    public boolean parseFrame(byte[] ucBuffer, int iOffset, IntHolder iParseBytes)
    {
        int             iOldOffset = iOffset;
        
        
        // ���ݳ���
        @SuppressWarnings("unused")
        int iContentLen = ucBuffer[iOffset++];
        
        // �����
        m_iTaskId = ucBuffer[iOffset++];
        
        // ��·��
        m_iClosedCircuitId = ucBuffer[iOffset++];
        
        // ��ѹֵ
        m_dVoltage = NumberUtil.BcdByte2ToShort(ucBuffer, iOffset) / 10.0;
        iOffset += 2;
        
        // ʱ��
        int iHigh = ucBuffer[iOffset++];
        int iLow = ucBuffer[iOffset++];
        m_iAging = ((iHigh << 8) & 0xff00) | (iLow & 0xff);
        
        // ��8~10�ֽڱ���
        @SuppressWarnings("unused")
        int iValue = ucBuffer[iOffset++];
        iValue = ucBuffer[iOffset++];
        iValue = ucBuffer[iOffset++];
        
        // ���ؽ������ֽ���
        if (null != iParseBytes)
        {
            iParseBytes.value = iOffset - iOldOffset;
        }
        
        return true;
    }
}
