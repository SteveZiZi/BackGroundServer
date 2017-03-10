package com.xtoee.devices.dcu.alarmLog;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.omg.CORBA.IntHolder;

import com.xtoee.devices.IDevice;
import com.xtoee.devices.dcu.CDcuDevice;
import com.xtoee.util.NumberUtil;

/**
 * �澯��־��������ƻ�·ͨ�ϣ�
 * @author zgm
 *
 */
public class CAlarmLog_TaskSettingCircuitSwitch extends CAlarmLogBase
{
    private int                     m_iTaskId;                  // �����
    private int                     m_iClosedCircuitId;         // ��·��
    private boolean                 m_bIsOpen;                  // �Ƿ��
    private Date                    m_dtEndTime;                // �������ʱ��
    
    
    /**
     * ���캯��
     */
    public CAlarmLog_TaskSettingCircuitSwitch()
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
        return "������ƣ���·ͨ�ϣ�";
    }

    /**
     * �����־�����ַ���
     * @param device �豸����
     * @return  ��־�����ַ���
     */
    public String getLogContent(IDevice device)
    {
        StringBuffer    sb = new StringBuffer();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        
        sb.append(String.format("����%02d��", m_iTaskId));
        sb.append(m_bIsOpen? "��": "�ر�");
        sb.append(String.format("L#%02d%02d��", ((CDcuDevice)device).getID(), m_iClosedCircuitId));
        sb.append(sdf.format(m_dtEndTime));
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
        
        // �������ʱ��
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
            String strDatetime = NumberUtil.BcdArrayToString(ucBuffer, iOffset, 5);
            m_dtEndTime = sdf.parse(strDatetime);
            iOffset += 5;
        }
        catch (java.text.ParseException e)
        {
            System.err.println(e.toString());
            return false;
        }
        
        // ���ؽ������ֽ���
        if (null != iParseBytes)
        {
            iParseBytes.value = iOffset - iOldOffset;
        }
        
        return true;
    }
}
