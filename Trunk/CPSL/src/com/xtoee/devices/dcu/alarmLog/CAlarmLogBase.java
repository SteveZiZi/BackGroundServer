package com.xtoee.devices.dcu.alarmLog;

import java.util.Date;
import java.util.Map;

import org.omg.CORBA.IntHolder;

import com.xtoee.devices.IDevice;
import com.xtoee.devices.dcu.CDcuDevice;

/**
 * �澯��־����
 * @author zgm
 *
 */
public abstract class CAlarmLogBase
{
    private Date                    m_dtAlarm;                  // �澯����ʱ��
    private int                     m_iAlarmCode;               // �澯����
    
    
    /**
     * ���캯��
     * @param iAlarmCode    �澯����
     */
    public CAlarmLogBase(int iAlarmCode)
    {
        m_iAlarmCode = iAlarmCode;
    }
    
    /**
     * ��ø澯����ʱ��
     * @return  �澯ʱ��
     */
    public Date getAlarmDate()
    {
        return m_dtAlarm;
    }
    
    /**
     * ���ø澯����ʱ��
     * @param dtAlarm �澯����ʱ��
     */
    public void setAlarmDate(Date dtAlarm)
    {
        m_dtAlarm = dtAlarm;
    }
    
    /**
     * ��ø澯����
     * @return  �澯����
     */
    public int getAlarmCode()
    {
        return m_iAlarmCode;
    }
    
    /**
     * ���ø澯����
     * @param iAlarmCode �澯����
     */
    public void setAlarmCode(int iAlarmCode)
    {
        m_iAlarmCode = iAlarmCode;
    }
    
    /**
     * ����ָ����·�ĵ�ѹֵ��ѯDim-Vdbӳ�����û�·������
     * @param device    �豸����
     * @param iClosedCircuitId  ��·��
     * @param dVoltage  ��·��ѹ
     * @return  ��·������
     */
    protected String getLightPercent(IDevice device, int iClosedCircuitId, double dVoltage)
    {
        // ����������
        if ((null == device))
        {
            return "�����������";
        }
        
        // ���ָ����·��Dim-Vdbӳ���
        CDcuDevice dcuDevice = (CDcuDevice)device;
        Map<String, Integer> mapDimVdb = dcuDevice.getDimVdbMapByCCId(iClosedCircuitId);
        if (null == mapDimVdb)
        {
            return "��ʽ����־��Ϣʧ�ܣ��޷��鵽ָ���Ļ�·�ţ�";
        }
        
        // ��������Dim-Vdb�б�
        String strPercent = "";
        double dMinDiff = Integer.MAX_VALUE;
        for (Map.Entry<String, Integer> entry : mapDimVdb.entrySet())
        {
            // �Ƚϵ�ѹֵ�Ƿ����
            double dAbsDiff = Math.abs(dVoltage - entry.getValue());
            if (dAbsDiff < 0.000001)
            {
                strPercent = entry.getKey();
                break;
            }
            // �����ѹֵ����ȣ���ôѰ�Ҳ�ֵ��С�ļ�¼
            else if(dAbsDiff < dMinDiff)
            {
                dMinDiff = dAbsDiff;
                strPercent = entry.getKey();
            }
        }
        
        return strPercent;
    }
    
    /**
     * �����澯��־����
     * @param ucBuffer      ��ű�����־���ĵ�����
     * @param iOffset       �澯��־���ĵ���ʼ����
     * @param iParseBytes   �������ֽ���
     * @return  �����ɹ�����true
     */
    public abstract boolean parseFrame(byte[] ucBuffer, int iOffset, IntHolder iParseBytes);
    
    /**
     * �����־�����ַ���
     * @return  ��־�����ַ���
     */
    public abstract String getLogType();
    
    /**
     * �����־�����ַ���
     * @param device �豸����
     * @return  ��־�����ַ���
     */
    public abstract String getLogContent(IDevice device);
}
