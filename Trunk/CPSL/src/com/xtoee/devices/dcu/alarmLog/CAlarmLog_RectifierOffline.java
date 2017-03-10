package com.xtoee.devices.dcu.alarmLog;

import org.omg.CORBA.IntHolder;

import com.xtoee.devices.IDevice;
import com.xtoee.devices.dcu.CDcuDevice;

/**
 * �澯��־�����������ߣ�
 * @author zgm
 *
 */
public class CAlarmLog_RectifierOffline extends CAlarmLogBase
{
	private int                     m_iClosedCircuitId;         // ��·��
    private int                     m_iDeviceId;                // �豸��
    private int                     m_iMsgType;                 // 1�����ߣ�0������
    
    
    /**
     * ���캯��
     */
    public CAlarmLog_RectifierOffline()
    {
        super(CAlarmLogFactory.AT_RectifierOffline);
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
     * �����Ϣ����
     * @return  1�����ߣ�2������
     */
    public int getMsgType()
    {
        return m_iMsgType;
    }
    
    /**
     * �����־�����ַ���
     * @return  ��־�����ַ���
     */
    public String getLogType()
    {
        return "������״̬";
    }

    /**
     * �����־�����ַ���
     * @param device �豸����
     * @return  ��־�����ַ���
     */
    public String getLogContent(IDevice device)
    {
        StringBuffer    sb = new StringBuffer();

        
        sb.append(String.format("R#%02d%02d%02d", ((CDcuDevice)device).getID(), m_iClosedCircuitId, m_iDeviceId));
        sb.append((1 == m_iMsgType)? "����": "����");
        
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
        
        // ����/����
        m_iMsgType = ucBuffer[iOffset++];
        
        // ���ؽ������ֽ���
        if (null != iParseBytes)
        {
            iParseBytes.value = iOffset - iOldOffset;
        }
        
        return true;
    }
}
