package com.xtoee.devices.dcu;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.omg.CORBA.IntHolder;

import com.xtoee.util.NumberUtil;

/**
 * �̿�������
 * @author zgm
 *
 */
public class CControlTask
{
    public static final int         CYCLE_TYPE_DAY      = 0;    // ִ�����ڼ�����գ�
    public static final int         CYCLE_TYPE_WEEK     = 1;    // ִ�����ڼ�����ܣ�
    public static final int         CYCLE_TYPE_MONTH    = 2;    // ִ�����ڼ�����£�
    
    public static final int         PRIORITY_LOW        = 0;    // �����ȼ�
    public static final int         PRIORITY_MIDDLE     = 1;    // �����ȼ�
    public static final int         PRIORITY_HIGH       = 2;    // �����ȼ�
    
    protected CDcuDevice            m_dcuDevice;                // �����������豸����
    
    protected boolean               m_bEnable;                  // �Ƿ�����
    protected int                   m_iTaskId;                  // ����ID
    protected int                   m_iPriority;                // ���ȼ�
    protected Date                  m_dtStart;                  // ��ʼʱ��
    protected Date                  m_dtEnd;                    // ����ʱ��
    protected int                   m_iCycleType;               // ִ�����ڼ����0���գ�1���ܣ�2���£�
    protected long                  m_lTimeScale;               // ִ������ʱ����ϸ
    protected List<CImplementPlan>  m_lstImplementPlans;        // �����б�
    
    
    /**
     * ���캯��
     * @param dcuDevice �̿������������豸����
     */
    public CControlTask(CDcuDevice dcuDevice)
    {
        m_dcuDevice = dcuDevice;
        m_lstImplementPlans = new LinkedList<CImplementPlan>();
    }
    
    /**
     * ���캯��
     * @param dcuDevice     �̿������������豸����
     * @param bEnable       �Ƿ�ʹ��
     * @param iTaskId       ����ID
     * @param iPriority     ���ȼ�
     * @param dtStart       ��ʼʱ��
     * @param dtEnd         ����ʱ��
     * @param iCycleType    ִ����������
     * @param lTimeScale    ִ������ʱ����ϸ
     */
    public CControlTask(CDcuDevice dcuDevice, boolean bEnable, int iTaskId, int iPriority, Date dtStart, Date dtEnd, int iCycleType, long lTimeScale)
    {
        m_dcuDevice = dcuDevice;
        m_bEnable = bEnable;
        m_iTaskId = iTaskId;
        m_iPriority = iPriority;
        m_dtStart = dtStart;
        m_dtEnd = dtEnd;
        m_iCycleType = iCycleType;
        m_lTimeScale = lTimeScale;
        m_lstImplementPlans = new LinkedList<CImplementPlan>();
    }
    
    /**
     * �Ƿ�����
     * @return  �Ƿ�ʹ��
     */
    public boolean isEnable()
    {
        return m_bEnable;
    }
    
    /**
     * ����ʹ��״̬
     * @param bEnable   �Ƿ�ʹ��
     */
    public void setEnable(boolean bEnable)
    {
        m_bEnable = bEnable;
    }
    
    /**
     * �������ID
     * @return  ����ID
     */
    public int getTaskId()
    {
        return m_iTaskId;
    }
    
    /**
     * ��������ID
     * @param iTaskId   ����ID
     */
    public void setTaskId(int iTaskId)
    {
        m_iTaskId = iTaskId;
    }
    
    /**
     * ������ȼ�
     * @return  ���ȼ�
     */
    public int getPriority()
    {
        return m_iPriority;
    }
    
    /**
     * �������ȼ�����ö�Ӧ�������ַ���
     * @param iPriority ���ȼ�
     * @return  ���ȼ������ַ���
     */
    public static String getPriorityString(int iPriority)
    {
        String          strRet = "δ֪";
        
        
        if (PRIORITY_LOW == iPriority)
        {
            strRet = "��";
        }
        else if (PRIORITY_MIDDLE == iPriority)
        {
            strRet = "��";
        }
        else if (PRIORITY_HIGH == iPriority)
        {
            strRet = "��";
        }
        
        return strRet;
    }
    
    /**
     * �������ȼ�
     * @param iPriority ���ȼ�
     */
    public void setPriority(int iPriority)
    {
        m_iPriority = iPriority;
    }
    
    /**
     * �����ʼʱ��
     * @return  ��ʼʱ��
     */
    public Date getStartTime()
    {
        return m_dtStart;
    }
    
    /**
     * ������ʼʱ��
     * @param dtStart ��ʼʱ��
     */
    public void setStartTime(Date dtStart)
    {
        m_dtStart = dtStart;
    }
    
    /**
     * ��ý���ʱ��
     * @return  ����ʱ��
     */
    public Date getEndTime()
    {
        return m_dtEnd;
    }
    
    /**
     * ���ý���ʱ��
     * @param dtEnd ����ʱ��
     */
    public void setEndTime(Date dtEnd)
    {
        m_dtEnd = dtEnd;
    }
    
    /**
     * ���ִ����������
     * @return  ִ����������
     */
    public int getCycleType()
    {
        return m_iCycleType;
    }
    
    /**
     * ����ִ����������
     * @param iCycleType ִ����������
     */
    public void setCycleType(int iCycleType)
    {
        m_iCycleType = iCycleType;
    }
    
    /**
     * ���ִ������ʱ����ϸ
     * @return  ִ������ʱ����ϸ
     */
    public long getTimeScale()
    {
        return m_lTimeScale;
    }
    
    /**
     * ����ִ������ʱ����ϸ
     * @param lTimeScale ִ������ʱ����ϸ
     */
    public void setTimeScale(long lTimeScale)
    {
        m_lTimeScale = lTimeScale;
    }
    
    /**
     * ����������漰�Ļ�·
     * @return  32λ������bit0��ʾ��·1�Ƿ�ѡ��bit1��ʾ��·2�Ƿ�ѡ��...��bit15��ʾ��·16�Ƿ�ѡ
     */
    public int getSelectedCircuit()
    {
        int             iRet = 0;
        
        
        // ��鷽���б�
        if ((null == m_lstImplementPlans) || (m_lstImplementPlans.size() == 0))
        {
            return iRet;
        }
        
        // ��õ�һ������
        CImplementPlan plan = m_lstImplementPlans.get(0);
        if (null == plan)
        {
            return iRet;
        }
        
        // ��÷����е������б�
        List<CIoctlParam_RemoteControl> lstContent = plan.getTaskContentList();
        if ((null == lstContent) || (lstContent.size() == 0))
        {
            return iRet;
        }
        
        // �������������б�
        for (CIoctlParam_RemoteControl taskContent : lstContent)
        {
            // ��û�·��
            int iCircuitId = taskContent.getDeviceId();
            if ((iCircuitId < 1) || (iCircuitId > 16))
            {
                continue;
            }
            
            // ��¼��·��
            iRet |= (1 << (iCircuitId - 1));
        }

        return iRet;
    }
    
    /**
     * ������񷽰�
     * @return
     */
    public JSONArray getPlans()
    {
        JSONArray jsonPlans = new JSONArray();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        
        
        // ѭ������ÿһ������
        for (CImplementPlan plan : m_lstImplementPlans)
        {
            JSONObject jsonPlan = new JSONObject();
            
            // ��ʼʱ��
            jsonPlan.put("startTime", sdf.format(plan.getStartTime()));
            
            // ������
            CIoctlParam_RemoteControl task = plan.getTaskContentList().get(0);
            jsonPlan.put("funCode", task.getSpecificFunc().getValue());
            
            // ����رջ�·����ô��������Ϊ-1
            if (task.getSpecificFunc() == enumSpecificFunction.ClosedCircuitClose)
			{
            	jsonPlan.put("brightness", "-1");
			}
            else
            {
            	// ����
            	int iCircuitId = task.getDeviceId();
                int iVoltage = task.getDataItem();
                jsonPlan.put("brightness", m_dcuDevice.getDimByCircuitVoltage(iCircuitId, iVoltage));
			}

            // ��ӿ�������
            jsonPlans.add(jsonPlan);
        }
        
        return jsonPlans;
    }
    
    /**
     * ���ִ�з����б�
     * @return  �����б�
     */
    public List<CImplementPlan> getImplementPlans()
    {
        return m_lstImplementPlans;
    }
    
    /**
     * ���ִ�з���
     * @param implPlan ִ�з���
     */
    public void AddImplementPlan(CImplementPlan implPlan)
    {
        m_lstImplementPlans.add(implPlan);
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
        
        
        // ����ʹ��
        ucBuffer[iOffset++] = (byte)(m_bEnable? 1: 0);
        
        // �����
        ucBuffer[iOffset++] = (byte)m_iTaskId;
        
        // ���ȼ�
        ucBuffer[iOffset++] = (byte)m_iPriority;
        
        // ��ʼʱ��
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        System.arraycopy(NumberUtil.StringToBcdArray(sdf.format(m_dtStart)), 0, ucBuffer, iOffset, 6);
        iOffset += 6;
        
        // ����ʱ��
        System.arraycopy(NumberUtil.StringToBcdArray(sdf.format(m_dtEnd)), 0, ucBuffer, iOffset, 6);
        iOffset += 6;
        
        // ִ�����ڼ��
        ucBuffer[iOffset++] = (byte)m_iCycleType;
        
        // ִ������ʱ����ϸ
        for (int i = 0; i < 6; i++)
        {
            ucBuffer[iOffset++] = (byte)((m_lTimeScale >> (i * 8)) & 0xff);
        }
        
        // ִ�з�����
        ucBuffer[iOffset++] = (byte)m_lstImplementPlans.size();
        
        // ѭ������ÿһ��ִ�з���
        IntHolder iImplPlanLen = new IntHolder();
        for (CImplementPlan implPlan : m_lstImplementPlans)
        {
            // ����ִ�з���
            if (!implPlan.Encode(ucBuffer, iOffset, iImplPlanLen))
            {
                return false;
            }
            
            iOffset += iImplPlanLen.value;
        }
        
        // �������ĳ���
        iFrameLen.value = iOffset - iOldOffset;
        return true;
    }
    
    /**
     * ����
     * @param ucBuffer      �����Ӧ���ĵĻ�����
     * @param iOffset       �������ʼ����
     * @param iFrameLen     ����ĳ���
     * @return  �ɹ�����true
     * @throws ParseException �쳣��Ϣ
     */
    public boolean Decode(byte[] ucBuffer, int iOffset, IntHolder iFrameLen) throws ParseException
    {
        int             iValue;
        int             iOldOffset = iOffset;
        
        
        // ����ʹ��״̬
        m_bEnable = (1 == ucBuffer[iOffset++]);
        
        // �����
        m_iTaskId = ucBuffer[iOffset++];
        
        // ���ȼ�
        m_iPriority = ucBuffer[iOffset++];
        
        // ��ʼʱ��
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        m_dtStart = sdf.parse(NumberUtil.BcdArrayToString(ucBuffer, iOffset, 6));
        iOffset += 6;
        
        // ����ʱ��
        m_dtEnd = sdf.parse(NumberUtil.BcdArrayToString(ucBuffer, iOffset, 6));
        iOffset += 6;
        
        // ִ�����ڼ��
        m_iCycleType = ucBuffer[iOffset++];
        
        // ִ������ʱ����ϸ
        m_lTimeScale = ucBuffer[iOffset++];
        iOffset += 5;
        
        // ִ�з�����
        iValue = ucBuffer[iOffset++];
        
        // ѭ������ÿһ��ִ�з���
        IntHolder iImplPlanLen = new IntHolder();
        for (int i = 0; i < iValue; i++)
        {
            // ����ִ�з���
            CImplementPlan implPlan = new CImplementPlan(null);
            if (!implPlan.Decode(ucBuffer, iOffset, iImplPlanLen))
            {
                return false;
            }
            
            // ������񵽼�����
            m_lstImplementPlans.add(implPlan);
            iOffset += iImplPlanLen.value;
        }
        
        // �������ĳ���
        iFrameLen.value = iOffset - iOldOffset;
        return true;
    }
}
