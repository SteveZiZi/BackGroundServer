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
 * 程控任务类
 * @author zgm
 *
 */
public class CControlTask
{
    public static final int         CYCLE_TYPE_DAY      = 0;    // 执行周期间隔（日）
    public static final int         CYCLE_TYPE_WEEK     = 1;    // 执行周期间隔（周）
    public static final int         CYCLE_TYPE_MONTH    = 2;    // 执行周期间隔（月）
    
    public static final int         PRIORITY_LOW        = 0;    // 低优先级
    public static final int         PRIORITY_MIDDLE     = 1;    // 中优先级
    public static final int         PRIORITY_HIGH       = 2;    // 高优先级
    
    protected CDcuDevice            m_dcuDevice;                // 任务所属的设备对象
    
    protected boolean               m_bEnable;                  // 是否是能
    protected int                   m_iTaskId;                  // 任务ID
    protected int                   m_iPriority;                // 优先级
    protected Date                  m_dtStart;                  // 起始时间
    protected Date                  m_dtEnd;                    // 结束时间
    protected int                   m_iCycleType;               // 执行周期间隔（0：日，1：周，2：月）
    protected long                  m_lTimeScale;               // 执行周期时标明细
    protected List<CImplementPlan>  m_lstImplementPlans;        // 方案列表
    
    
    /**
     * 构造函数
     * @param dcuDevice 程控任务所属的设备对象
     */
    public CControlTask(CDcuDevice dcuDevice)
    {
        m_dcuDevice = dcuDevice;
        m_lstImplementPlans = new LinkedList<CImplementPlan>();
    }
    
    /**
     * 构造函数
     * @param dcuDevice     程控任务所属的设备对象
     * @param bEnable       是否使能
     * @param iTaskId       任务ID
     * @param iPriority     优先级
     * @param dtStart       起始时间
     * @param dtEnd         结束时间
     * @param iCycleType    执行周期类型
     * @param lTimeScale    执行周期时标明细
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
     * 是否是能
     * @return  是否使能
     */
    public boolean isEnable()
    {
        return m_bEnable;
    }
    
    /**
     * 设置使能状态
     * @param bEnable   是否使能
     */
    public void setEnable(boolean bEnable)
    {
        m_bEnable = bEnable;
    }
    
    /**
     * 获得任务ID
     * @return  任务ID
     */
    public int getTaskId()
    {
        return m_iTaskId;
    }
    
    /**
     * 设置任务ID
     * @param iTaskId   任务ID
     */
    public void setTaskId(int iTaskId)
    {
        m_iTaskId = iTaskId;
    }
    
    /**
     * 获得优先级
     * @return  优先级
     */
    public int getPriority()
    {
        return m_iPriority;
    }
    
    /**
     * 根据优先级，获得对应的描述字符串
     * @param iPriority 优先级
     * @return  优先级描述字符串
     */
    public static String getPriorityString(int iPriority)
    {
        String          strRet = "未知";
        
        
        if (PRIORITY_LOW == iPriority)
        {
            strRet = "低";
        }
        else if (PRIORITY_MIDDLE == iPriority)
        {
            strRet = "中";
        }
        else if (PRIORITY_HIGH == iPriority)
        {
            strRet = "高";
        }
        
        return strRet;
    }
    
    /**
     * 设置优先级
     * @param iPriority 优先级
     */
    public void setPriority(int iPriority)
    {
        m_iPriority = iPriority;
    }
    
    /**
     * 获得起始时间
     * @return  起始时间
     */
    public Date getStartTime()
    {
        return m_dtStart;
    }
    
    /**
     * 设置起始时间
     * @param dtStart 起始时间
     */
    public void setStartTime(Date dtStart)
    {
        m_dtStart = dtStart;
    }
    
    /**
     * 获得结束时间
     * @return  结束时间
     */
    public Date getEndTime()
    {
        return m_dtEnd;
    }
    
    /**
     * 设置结束时间
     * @param dtEnd 结束时间
     */
    public void setEndTime(Date dtEnd)
    {
        m_dtEnd = dtEnd;
    }
    
    /**
     * 获得执行周期类型
     * @return  执行周期类型
     */
    public int getCycleType()
    {
        return m_iCycleType;
    }
    
    /**
     * 设置执行周期类型
     * @param iCycleType 执行周期类型
     */
    public void setCycleType(int iCycleType)
    {
        m_iCycleType = iCycleType;
    }
    
    /**
     * 获得执行周期时标明细
     * @return  执行周期时标明细
     */
    public long getTimeScale()
    {
        return m_lTimeScale;
    }
    
    /**
     * 设置执行周期时标明细
     * @param lTimeScale 执行周期时标明细
     */
    public void setTimeScale(long lTimeScale)
    {
        m_lTimeScale = lTimeScale;
    }
    
    /**
     * 获得任务中涉及的回路
     * @return  32位整数，bit0表示回路1是否勾选，bit1表示回路2是否勾选，...，bit15表示回路16是否勾选
     */
    public int getSelectedCircuit()
    {
        int             iRet = 0;
        
        
        // 检查方案列表
        if ((null == m_lstImplementPlans) || (m_lstImplementPlans.size() == 0))
        {
            return iRet;
        }
        
        // 获得第一个方案
        CImplementPlan plan = m_lstImplementPlans.get(0);
        if (null == plan)
        {
            return iRet;
        }
        
        // 获得方案中的内容列表
        List<CIoctlParam_RemoteControl> lstContent = plan.getTaskContentList();
        if ((null == lstContent) || (lstContent.size() == 0))
        {
            return iRet;
        }
        
        // 遍历方案内容列表
        for (CIoctlParam_RemoteControl taskContent : lstContent)
        {
            // 获得回路号
            int iCircuitId = taskContent.getDeviceId();
            if ((iCircuitId < 1) || (iCircuitId > 16))
            {
                continue;
            }
            
            // 记录回路号
            iRet |= (1 << (iCircuitId - 1));
        }

        return iRet;
    }
    
    /**
     * 获得任务方案
     * @return
     */
    public JSONArray getPlans()
    {
        JSONArray jsonPlans = new JSONArray();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        
        
        // 循环遍历每一个任务
        for (CImplementPlan plan : m_lstImplementPlans)
        {
            JSONObject jsonPlan = new JSONObject();
            
            // 开始时间
            jsonPlan.put("startTime", sdf.format(plan.getStartTime()));
            
            // 功能码
            CIoctlParam_RemoteControl task = plan.getTaskContentList().get(0);
            jsonPlan.put("funCode", task.getSpecificFunc().getValue());
            
            // 如果关闭回路，那么设置亮度为-1
            if (task.getSpecificFunc() == enumSpecificFunction.ClosedCircuitClose)
			{
            	jsonPlan.put("brightness", "-1");
			}
            else
            {
            	// 亮度
            	int iCircuitId = task.getDeviceId();
                int iVoltage = task.getDataItem();
                jsonPlan.put("brightness", m_dcuDevice.getDimByCircuitVoltage(iCircuitId, iVoltage));
			}

            // 添加控制任务
            jsonPlans.add(jsonPlan);
        }
        
        return jsonPlans;
    }
    
    /**
     * 获得执行方案列表
     * @return  方案列表
     */
    public List<CImplementPlan> getImplementPlans()
    {
        return m_lstImplementPlans;
    }
    
    /**
     * 添加执行方案
     * @param implPlan 执行方案
     */
    public void AddImplementPlan(CImplementPlan implPlan)
    {
        m_lstImplementPlans.add(implPlan);
    }
    
    /**
     * 编码
     * @param ucBuffer      存放编码结果的缓冲区
     * @param iOffset       存放编码结果的起始索引
     * @param iFrameLen     编码结果的长度
     * @return  成功返回true
     */
    public boolean Encode(byte[] ucBuffer, int iOffset, IntHolder iFrameLen)
    {
        int             iOldOffset = iOffset;
        
        
        // 任务使能
        ucBuffer[iOffset++] = (byte)(m_bEnable? 1: 0);
        
        // 任务号
        ucBuffer[iOffset++] = (byte)m_iTaskId;
        
        // 优先级
        ucBuffer[iOffset++] = (byte)m_iPriority;
        
        // 起始时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        System.arraycopy(NumberUtil.StringToBcdArray(sdf.format(m_dtStart)), 0, ucBuffer, iOffset, 6);
        iOffset += 6;
        
        // 结束时间
        System.arraycopy(NumberUtil.StringToBcdArray(sdf.format(m_dtEnd)), 0, ucBuffer, iOffset, 6);
        iOffset += 6;
        
        // 执行周期间隔
        ucBuffer[iOffset++] = (byte)m_iCycleType;
        
        // 执行周期时标明细
        for (int i = 0; i < 6; i++)
        {
            ucBuffer[iOffset++] = (byte)((m_lTimeScale >> (i * 8)) & 0xff);
        }
        
        // 执行方案数
        ucBuffer[iOffset++] = (byte)m_lstImplementPlans.size();
        
        // 循环遍历每一个执行方案
        IntHolder iImplPlanLen = new IntHolder();
        for (CImplementPlan implPlan : m_lstImplementPlans)
        {
            // 编码执行方案
            if (!implPlan.Encode(ucBuffer, iOffset, iImplPlanLen))
            {
                return false;
            }
            
            iOffset += iImplPlanLen.value;
        }
        
        // 编码结果的长度
        iFrameLen.value = iOffset - iOldOffset;
        return true;
    }
    
    /**
     * 解码
     * @param ucBuffer      存放响应报文的缓冲区
     * @param iOffset       解码的起始索引
     * @param iFrameLen     解码的长度
     * @return  成功返回true
     * @throws ParseException 异常信息
     */
    public boolean Decode(byte[] ucBuffer, int iOffset, IntHolder iFrameLen) throws ParseException
    {
        int             iValue;
        int             iOldOffset = iOffset;
        
        
        // 任务使能状态
        m_bEnable = (1 == ucBuffer[iOffset++]);
        
        // 任务号
        m_iTaskId = ucBuffer[iOffset++];
        
        // 优先级
        m_iPriority = ucBuffer[iOffset++];
        
        // 起始时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        m_dtStart = sdf.parse(NumberUtil.BcdArrayToString(ucBuffer, iOffset, 6));
        iOffset += 6;
        
        // 结束时间
        m_dtEnd = sdf.parse(NumberUtil.BcdArrayToString(ucBuffer, iOffset, 6));
        iOffset += 6;
        
        // 执行周期间隔
        m_iCycleType = ucBuffer[iOffset++];
        
        // 执行周期时标明细
        m_lTimeScale = ucBuffer[iOffset++];
        iOffset += 5;
        
        // 执行方案数
        iValue = ucBuffer[iOffset++];
        
        // 循环遍历每一个执行方案
        IntHolder iImplPlanLen = new IntHolder();
        for (int i = 0; i < iValue; i++)
        {
            // 编码执行方案
            CImplementPlan implPlan = new CImplementPlan(null);
            if (!implPlan.Decode(ucBuffer, iOffset, iImplPlanLen))
            {
                return false;
            }
            
            // 添加任务到集合中
            m_lstImplementPlans.add(implPlan);
            iOffset += iImplPlanLen.value;
        }
        
        // 解码结果的长度
        iFrameLen.value = iOffset - iOldOffset;
        return true;
    }
}
