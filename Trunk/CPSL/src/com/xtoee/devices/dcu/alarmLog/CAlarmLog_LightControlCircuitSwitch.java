package com.xtoee.devices.dcu.alarmLog;

import org.omg.CORBA.IntHolder;

import com.xtoee.devices.IDevice;
import com.xtoee.devices.dcu.CDcuDevice;

/**
 * �澯��־����������·���أ�
 * @author zgm
 *
 */
public class CAlarmLog_LightControlCircuitSwitch extends CAlarmLogBase
{
    private int                     m_iTaskId;                  // �����
    private int                     m_iClosedCircuitId;         // ��·��
    private boolean                 m_bIsOpen;                  // �Ƿ��
    private int                     m_iAging;                   // ʱЧ�����ӣ�
    
    
    /**
     * ���캯��
     */
    public CAlarmLog_LightControlCircuitSwitch()
    {
        super(CAlarmLogFactory.AT_TaskSettingCircuitSwitch);
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
        return "������񣨻�·���أ�";
    }

    /**
     * �����־�����ַ���
     * @param device �豸����
     * @return  ��־�����ַ���
     */
    public String getLogContent(IDevice device)
    {
        StringBuffer    sb = new StringBuffer();
        
        
        sb.append(String.format("����%02d��", m_iTaskId));
        sb.append(m_bIsOpen? "��": "�ر�");
        sb.append(String.format("L#%02d%02d��", ((CDcuDevice)device).getID(), m_iClosedCircuitId));
        sb.append("������");
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
        
        // ����״̬
        m_bIsOpen = (ucBuffer[iOffset++] == 1);
        
        // ʱ��
        int iHigh = ucBuffer[iOffset++];
        int iLow = ucBuffer[iOffset++];
        m_iAging = ((iHigh << 8) & 0xff00) | (iLow & 0xff);
        
        // ��6~8�ֽڱ���
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
