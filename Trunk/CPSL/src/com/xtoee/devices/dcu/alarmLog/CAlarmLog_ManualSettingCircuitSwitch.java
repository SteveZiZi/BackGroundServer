package com.xtoee.devices.dcu.alarmLog;

import org.omg.CORBA.IntHolder;

import com.xtoee.devices.IDevice;
import com.xtoee.devices.dcu.CDcuDevice;
import com.xtoee.util.NumberUtil;

/**
 * �澯��־���ֶ����ƻ�·ͨ�ϣ�
 * @author zgm
 *
 */
public class CAlarmLog_ManualSettingCircuitSwitch extends CAlarmLogBase
{
    private int                     m_iClosedCircuitId;         // ��·��
    private boolean                 m_bIsOpen;                  // �Ƿ��
    private int                     m_iAging;                   // ʱЧ�����ӣ�
    private double                  m_dVoltage;                 // ��ѹֵ
    
    
    /**
     * ���캯��
     */
    public CAlarmLog_ManualSettingCircuitSwitch()
    {
        super(CAlarmLogFactory.AT_ManualSettingCircuitSwitch);
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
     * �Ƿ��
     * @return  ��/�ر�
     */
    public boolean isOpened()
    {
        return m_bIsOpen;
    }
    
    /**
     * �����־�����ַ���
     * @return  ��־�����ַ���
     */
    public String getLogType()
    {
        return "��·ͨ�Ͽ���";
    }

    /**
     * �����־�����ַ���
     * @param device �豸����
     * @return  ��־�����ַ���
     */
    public String getLogContent(IDevice device)
    {
        StringBuffer    sb = new StringBuffer();
        
        
        if (m_bIsOpen)
        {
            sb.append("��");
            sb.append(String.format("L#%02d%02d", ((CDcuDevice)device).getID(), m_iClosedCircuitId));
            sb.append("������Ϊ��");
            sb.append(getLightPercent(device, m_iClosedCircuitId, m_dVoltage));
            sb.append("%��ʱЧ");
            sb.append(m_iAging);
            sb.append("����");
        }
        else 
        {
            sb.append("�ر�");
            sb.append(String.format("L#%02d%02d", ((CDcuDevice)device).getID(), m_iClosedCircuitId));
        }
        
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
        
        // ����״̬
        m_bIsOpen = (ucBuffer[iOffset++] == 1);
        
        // ʱЧ�����ӣ�
        m_iAging = ucBuffer[iOffset++];
        
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
