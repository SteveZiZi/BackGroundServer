package com.xtoee.devices.dcu.alarmLog;

import org.omg.CORBA.IntHolder;

import com.xtoee.devices.IDevice;

/**
 * �澯��־���������������
 * @author zgm
 *
 */
public class CAlarmLog_ArchiveParamModified extends CAlarmLogBase
{
    /**
     * ���캯��
     */
    public CAlarmLog_ArchiveParamModified()
    {
        super(CAlarmLogFactory.AT_ArchiveParamModified);
    }
    
    /**
     * �����־�����ַ���
     * @return  ��־�����ַ���
     */
    public String getLogType()
    {
        return "�����������";
    }

    /**
     * �����־�����ַ���
     * @param device �豸����
     * @return  ��־�����ַ���
     */
    public String getLogContent(IDevice device)
    {
        return "���ص���";
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
        
        // ���ؽ������ֽ���
        if (null != iParseBytes)
        {
            iParseBytes.value = iOffset - iOldOffset;
        }
        
        return true;
    }
}
