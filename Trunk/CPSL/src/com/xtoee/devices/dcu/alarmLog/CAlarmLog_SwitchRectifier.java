package com.xtoee.devices.dcu.alarmLog;

import org.omg.CORBA.IntHolder;

import com.xtoee.devices.IDevice;
import com.xtoee.devices.dcu.CDcuDevice;

/**
 * �澯��־�����������ؿ��ƣ�
 * @author zgm
 *
 */
public class CAlarmLog_SwitchRectifier extends CAlarmLogBase
{
	private int                     m_iClosedCircuitId;         // ��·��
    private int                     m_iDeviceId;                // �豸��
    private boolean                 m_bIsOpen;                  // �Ƿ��
    
    
    /**
     * ���캯��
     */
    public CAlarmLog_SwitchRectifier()
    {
        super(CAlarmLogFactory.AT_SwitchRectifier);
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
        return "���������ؿ���";
    }

    /**
     * �����־�����ַ���
     * @param device �豸����
     * @return  ��־�����ַ���
     */
    public String getLogContent(IDevice device)
    {
        StringBuffer    sb = new StringBuffer();

        
        sb.append(m_bIsOpen? "��": "�ر�");
        sb.append(String.format("R#%02d%02d%02d", ((CDcuDevice)device).getID(), m_iClosedCircuitId, m_iDeviceId));
        
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
        
        // ����״̬
        m_bIsOpen = (ucBuffer[iOffset++] == 1);
        
        // ���ؽ������ֽ���
        if (null != iParseBytes)
        {
            iParseBytes.value = iOffset - iOldOffset;
        }
        
        return true;
    }
}
