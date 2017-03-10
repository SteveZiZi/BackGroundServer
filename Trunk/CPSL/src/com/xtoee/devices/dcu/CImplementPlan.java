package com.xtoee.devices.dcu;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.omg.CORBA.IntHolder;

import com.xtoee.util.NumberUtil;

/**
 * ִ�з���
 * @author zgm
 *
 */
public class CImplementPlan
{
    protected Date                  m_dtStartUp;                // ����ʱ��
    protected List<CIoctlParam_RemoteControl> m_lstTaskContent; // ����ִ�����������
    
    
    /**
     * ���캯��
     * @param dtStartUp ����ʱ��
     */
    public CImplementPlan(Date dtStartUp)
    {
        m_dtStartUp = dtStartUp;
        m_lstTaskContent = new LinkedList<CIoctlParam_RemoteControl>();
    }
    
    /**
     * �������ʱ��
     * @return  ����ʱ��
     */
    public Date getStartTime()
    {
        return m_dtStartUp;
    }
    
    /**
     * ������������б�
     * @return  ���������б�
     */
    public List<CIoctlParam_RemoteControl> getTaskContentList()
    {
        return m_lstTaskContent;
    }
    
    /**
     * �����������
     * @param taskContent   ��������
     */
    public void AddTaskContent(CIoctlParam_RemoteControl taskContent)
    {
        m_lstTaskContent.add(taskContent);
    }
    
    /**
     * ����
     * @param ucBuffer      ��ű������Ļ�����
     * @param iOffset       ��ű���������ʼ����
     * @param iFrameLen     �������ĳ���
     * @return  �ɹ�����true
     */
    public boolean Encode(byte[] ucBuffer, int iOffset, IntHolder iFrameLen)
    {
        int             iOldOffset = iOffset;
        
        
        // ����ʱ��
        SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
        System.arraycopy(NumberUtil.StringToBcdArray(sdf.format(m_dtStartUp)), 0, ucBuffer, iOffset, 2);
        iOffset += 2;
        
        // ��·��Ա��
        ucBuffer[iOffset++] = (byte)m_lstTaskContent.size();
        
        // ѭ���������������б�
        for (CIoctlParam_RemoteControl taskContent : m_lstTaskContent)
        {
            // �豸�ţ���·�ţ�
            ucBuffer[iOffset++] = (byte)taskContent.getDeviceId();
            
            // �ӻ�·��
            ucBuffer[iOffset++] = (byte)taskContent.getSubLoopId();
            
            // ���⹦����
            ucBuffer[iOffset++] = (byte)taskContent.getSpecificFunc().getValue();
            
            // ���������ݣ���ѹֵ��
            System.arraycopy(NumberUtil.ShortToBcdByte2(taskContent.getDataItem() * 10), 0, ucBuffer, iOffset, 2);
            iOffset += 2;
            
            // ���������ݣ�����ֵ��
            System.arraycopy(NumberUtil.ShortToBcdByte2(0), 0, ucBuffer, iOffset, 2);
            iOffset += 2;
        }
        
        // �������ĳ���
        iFrameLen.value = iOffset - iOldOffset;
        return true;
    }
    
    /**
     * ����
     * @param ucBuffer      ��Ŵ����뱨�ĵĻ�����
     * @param iOffset       ���������ʼ����
     * @param iFrameLen     ��������ݳ���
     * @return  �ɹ�����true
     * @throws ParseException �쳣��Ϣ
     */
    public boolean Decode(byte[] ucBuffer, int iOffset, IntHolder iFrameLen) throws ParseException
    {
        @SuppressWarnings("unused")
        int             iValue;
        int             iOldOffset = iOffset;
        
        
        // ����ʱ��
        SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
        m_dtStartUp = sdf.parse(NumberUtil.BcdArrayToString(ucBuffer, iOffset, 2));
        iOffset += 2;
        
        // ��·��Ա��
        int iCount = ucBuffer[iOffset++];
        
        // ѭ���������������б�
        for (int i = 0; i < iCount; i++)
        {
            // �豸�ţ���·�ţ�
            int iDeviceId = ucBuffer[iOffset++];
            // �ӻ�·��
            int iSubLoopId = ucBuffer[iOffset++];
            // ���⹦����
            enumSpecificFunction eSpecificFunc = enumSpecificFunction.getEnumByVaule(ucBuffer[iOffset++]);
            // ��ѹֵ
            int iVotage = Integer.parseInt(NumberUtil.BcdArrayToString(ucBuffer, iOffset, 2)) / 10;
            iOffset += 2;
            // ����ֵ
            iValue = Integer.parseInt(NumberUtil.BcdArrayToString(ucBuffer, iOffset, 2)) / 10;
            iOffset += 2;
     
            // �������������
            m_lstTaskContent.add(new CIoctlParam_RemoteControl(iDeviceId, iSubLoopId, eSpecificFunc, iVotage));
        }
        
        // ����ĳ���
        iFrameLen.value = iOffset - iOldOffset;
        return true;
    }
}
