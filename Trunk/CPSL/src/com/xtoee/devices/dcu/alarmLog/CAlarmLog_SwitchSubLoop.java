package com.xtoee.devices.dcu.alarmLog;

import org.omg.CORBA.IntHolder;

import com.xtoee.devices.IDevice;
import com.xtoee.devices.dcu.CDcuDevice;

/**
 * �澯��־���ӻ�·ͨ�Ͽ��ƣ�
 * @author zgm
 *
 */
public class CAlarmLog_SwitchSubLoop extends CAlarmLogBase
{
    private int                     m_iDeviceId;                // �豸��
    private int                     m_iSubLoopId;               // ��·��
    private boolean                 m_bIsOpen;                  // �Ƿ��
    
    
    /**
     * ���캯��
     */
    public CAlarmLog_SwitchSubLoop()
    {
        super(CAlarmLogFactory.AT_SwitchSubLoop);
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
     * �����·��
     * @return  ��·��
     */
    public int getSubLoopId()
    {
        return m_iSubLoopId;
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
        return "�ӻ�·ͨ�Ͽ���";
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
        sb.append(String.format("L#%02d%02d�µ�%04d���ӻ�·", ((CDcuDevice)device).getID(), m_iDeviceId, m_iSubLoopId));
        
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
        
        // �豸��
        m_iDeviceId = ucBuffer[iOffset++];
        
        // ��·��
        m_iSubLoopId = ucBuffer[iOffset++];
        
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
