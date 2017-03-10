package com.xtoee.devices.dcu.alarmLog;

import org.omg.CORBA.IntHolder;

import com.xtoee.devices.IDevice;
import com.xtoee.devices.dcu.CDcuDevice;
import com.xtoee.util.NumberUtil;

/**
 * �澯��־����������ѹ���ã�
 * @author zgm
 *
 */
public class CAlarmLog_SettingRectifierVoltage extends CAlarmLogBase
{
	private int                     m_iClosedCircuitId;         // ��·��
    private int                     m_iDeviceId;                // �豸��
    private double                  m_dVoltage;                 // ��ѹֵ
    
    
    /**
     * ���캯��
     */
    public CAlarmLog_SettingRectifierVoltage()
    {
        super(CAlarmLogFactory.AT_SettingRectifierVoltage);
    }
    
    /**
     * ����豸��
     * @return  �豸��
     */
    public int getDeviceId()
    {
        return m_iDeviceId;
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
        return "��������ѹ����";
    }

    /**
     * �����־�����ַ���
     * @param device �豸����
     * @return  ��־�����ַ���
     */
    public String getLogContent(IDevice device)
    {
        StringBuffer    sb = new StringBuffer();
        
        
        sb.append("����R#");
        sb.append(String.format("%02d%02d%02d", ((CDcuDevice)device).getID(), m_iClosedCircuitId, m_iDeviceId));
        sb.append("��ѹ��");
        sb.append(String.format("%.1f��", m_dVoltage));
        
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
        
        // ��·��
        m_iClosedCircuitId = ucBuffer[iOffset++];
        
        // �豸��
        m_iDeviceId = ucBuffer[iOffset++];
        
        // ��ѹֵ
        m_dVoltage = NumberUtil.BcdByte2ToShort(ucBuffer, iOffset) / 10.0;
        iOffset += 2;
        
        // ���ؽ������ֽ���
        if (null != iParseBytes)
        {
            iParseBytes.value = iOffset - iOldOffset;
        }
        
        return true;
    }
}
