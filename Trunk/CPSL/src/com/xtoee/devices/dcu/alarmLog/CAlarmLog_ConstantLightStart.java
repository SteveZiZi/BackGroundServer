package com.xtoee.devices.dcu.alarmLog;

import org.omg.CORBA.IntHolder;

import com.xtoee.devices.IDevice;
import com.xtoee.devices.dcu.CDcuDevice;

/**
 * ��������ʼ
 * @author zgm
 *
 */
public class CAlarmLog_ConstantLightStart extends CAlarmLogBase
{
    private int                     m_iTaskId;                  // �����
    private int                     m_iClosedCircuitId;         // ��·��
    
    
    /**
     * ���캯��
     */
    public CAlarmLog_ConstantLightStart()
    {
        super(CAlarmLogFactory.AT_ConstantLightStart);
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
     * �����־�����ַ���
     * @return  ��־�����ַ���
     */
    public String getLogType()
    {
        return "��������ʼ";
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
        sb.append(String.format("��·L#%02d%02d", ((CDcuDevice)device).getID(), m_iClosedCircuitId));
        sb.append("��������ʼ");
        
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
        
        // ���ؽ������ֽ���
        if (null != iParseBytes)
        {
            iParseBytes.value = iOffset - iOldOffset;
        }
        
        return true;
    }
}
