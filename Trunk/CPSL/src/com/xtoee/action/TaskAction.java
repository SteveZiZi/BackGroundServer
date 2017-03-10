package com.xtoee.action;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.opensymphony.xwork2.ActionSupport;
import com.xtoee.devices.IDevice;
import com.xtoee.devices.dcu.CControlTask;
import com.xtoee.devices.dcu.CDcuDevice;
import com.xtoee.service.DeviceManageService;
import com.xtoee.service.TaskManageService;
import com.xtoee.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * ���������
 * @author zgm
 *
 */
public class TaskAction extends ActionSupport
{
    private static final long       serialVersionUID = 1L;
    private DeviceManageService     deviceManageService;        // �豸����ӿ�
    private TaskManageService       taskManageService;          // �������ӿ�
    private InputStream             responseJson;               // ��Ӧ�����Json��ʽ��
    
    private Integer                 page;                       // ҳ��
    private Integer                 rows;                       // �к�
    
    private String                  logicAddress;               // �豸�߼���ַ
    
    private boolean                 taskEnable;                 // ����ʹ��
    private int                     taskId;                     // ����ID
    private int                     taskPriority;               // �������ȼ�
    private Date                    taskStartTime;              // ��ʼʱ��
    private Date                    taskEndTime;                // ����ʱ��
    private String                  taskCircuitSelect;          // ��·ѡ��
    private String                  taskCycleMode;              // ѭ��ģʽ
    private String                  taskItemStartupTimes;       // ����������Ŀ�ʼʱ��
    private String                  taskItemActions;            // ����������Ķ���
    
    private String                  taskIds;                    // ��ɾ��������ID
    
    
    /**
     * �����豸����ӿ�
     * @param deviceManageService   �豸�������
     */
    public void setDeviceManageService(DeviceManageService deviceManageService)
    {
        this.deviceManageService = deviceManageService;
    }
    
    /**
     * �����������ӿ�
     * @param taskManageService     ����������
     */
    public void setTaskManageService(TaskManageService taskManageService)
    {
        this.taskManageService = taskManageService;
    }
    
    /**
     * ����ҳ��
     * @param page  ҳ��
     */
    public void setPage(Integer page)
    {
        this.page = page;
    }

    /**
     * �����к�
     * @param rows �к�
     */
    public void setRows(Integer rows)
    {
        this.rows = rows;
    }
    
    /**
     * �����豸�߼���ַ
     * @param logicAddress  �豸�߼���ַ
     */
    public void setLogicAddress(String logicAddress)
    {
        this.logicAddress = logicAddress;
    }

    /**
     * �����Ӧ�����Json��ʽ��
     * @return  ��Ӧ�����Json��ʽ��
     */
    public InputStream getResponseJson()
    {
        return responseJson;
    }
    
    /**
     * ������Ӧ�����Json��ʽ��
     * @param responseJson  ��Ӧ�����Json��ʽ��
     */
    public void setResponseJson(InputStream responseJson)
    {
        this.responseJson = responseJson;
    }

    /**
     * ��������ʹ��
     * @param taskEnable    ����ʹ��
     */
    public void setTaskEnable(boolean taskEnable)
    {
        this.taskEnable = taskEnable;
    }

    /**
     * ��������ID
     * @param taskId    ����ID
     */
    public void setTaskId(int taskId)
    {
        this.taskId = taskId;
    }

    /**
     * �����������ȼ�
     * @param taskPriority  �������ȼ�
     */
    public void setTaskPriority(int taskPriority)
    {
        this.taskPriority = taskPriority;
    }

    /**
     * ���ÿ�ʼʱ��
     * @param taskStartTime ��ʼʱ��
     */
    public void setTaskStartTime(Date taskStartTime)
    {
        this.taskStartTime = taskStartTime;
    }

    /**
     * ���ý���ʱ��
     * @param taskEndTime   ����ʱ��
     */
    public void setTaskEndTime(Date taskEndTime)
    {
        this.taskEndTime = taskEndTime;
    }

    /**
     * ���û�·ѡ��
     * @param taskCircuitSelect ��·ѡ��
     */
    public void setTaskCircuitSelect(String taskCircuitSelect)
    {
        this.taskCircuitSelect = taskCircuitSelect;
    }

    /**
     * ����ѭ��ģʽ
     * @param taskCycleMode ѭ��ģʽ
     */
    public void setTaskCycleMode(String taskCycleMode)
    {
        this.taskCycleMode = taskCycleMode;
    }

    /**
     * ��������������Ŀ�ʼʱ��
     * @param taskItemStartupTimes  ����������Ŀ�ʼʱ��
     */
    public void setTaskItemStartupTimes(String taskItemStartupTimes)
    {
        this.taskItemStartupTimes = taskItemStartupTimes;
    }

    /**
     * ��������������Ķ���
     * @param taskItemActions   ����������Ķ���
     */
    public void setTaskItemActions(String taskItemActions)
    {
        this.taskItemActions = taskItemActions;
    }
    
    /**
     * ���ô�ɾ��������ID
     * @param taskIds   ��ɾ��������ID
     */
    public void setTaskIds(String taskIds)
    {
        this.taskIds = taskIds;
    }
    
    /**
     * ��ó̿������б�
     * @return  �ɹ�����success
     * @throws UnsupportedEncodingException
     */
    public String getControlTaskList() throws UnsupportedEncodingException
    {
        // ����������
        if (StringUtil.IsNullOrEmpty(logicAddress) 
                || (page <= 0) 
                || (rows <= 0))
        {
            return ERROR;
        }
        
        // �����豸�߼���ַ������豸����
        IDevice device = deviceManageService.GetDeviceByLogicAddress(logicAddress);
        if ((null == device) || !(device instanceof CDcuDevice))
        {
            return ERROR;
        }
        CDcuDevice cpcDevice = (CDcuDevice)device;
        
        // ��������б�
        List<CControlTask> lstControlTasks = cpcDevice.getContorlTaskList();
        if (null == lstControlTasks)
        {
            return ERROR;
        }

        // ѭ�������澯��־�б�
        int iCount = 0;
        JSONArray jsonArray = new JSONArray();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (CControlTask task : lstControlTasks)
        {
            // ��������
            iCount++;
            
            // ����к��Ƿ���ָ����ҳ�뷶Χ
            if ((iCount > (page - 1) * rows) && (iCount <= page * rows))
            {
                // ��ø澯��־��Ϣ
                JSONObject row = new JSONObject();
                row.put("taskId", task.getTaskId());
                row.put("taskPriority", CControlTask.getPriorityString(task.getPriority()));
                row.put("taskStartDate", sdf.format(task.getStartTime()));
                row.put("taskEndDate", sdf.format(task.getEndTime()));

                // ��Ӹ澯��־��Ϣ���м�����
                jsonArray.add(row);
            }
        }

        // �ϳɷ��ؽ��
        JSONObject result = new JSONObject();
        result.put("total", lstControlTasks.size());
        result.put("rows", jsonArray);
        
        // ������Ӧ�����Json��ʽ��
        setResponseJson(new ByteArrayInputStream(result.toString().getBytes("UTF-8")));
        return SUCCESS;
    }
    
    /**
     * ���ִ������ʱ����ϸ
     * @param strCycleMode  ִ�������ַ��������磺1,3,5,7��
     * @return  ִ������ʱ����ϸ
     */
    protected long getTimeScaleByString(String strCycleMode)
    {
        long            lRet = 0;
        
        
        // ������
        if (strCycleMode.indexOf("7") != -1)
        {
            lRet |= 0x01;
        }
        
        // ����һ
        if (strCycleMode.indexOf("1") != -1)
        {
            lRet |= 0x02;
        }
        
        // ���ڶ�
        if (strCycleMode.indexOf("2") != -1)
        {
            lRet |= 0x04;
        }
        
        // ������
        if (strCycleMode.indexOf("3") != -1)
        {
            lRet |= 0x08;
        }
        
        // ������
        if (strCycleMode.indexOf("4") != -1)
        {
            lRet |= 0x10;
        }
        
        // ������
        if (strCycleMode.indexOf("5") != -1)
        {
            lRet |= 0x20;
        }
        
        // ������
        if (strCycleMode.indexOf("6") != -1)
        {
            lRet |= 0x40;
        }
        
        return lRet;
    }
    
    /**
     * ��ӳ̿�����
     * @return  �ɹ�����success
     * @throws UnsupportedEncodingException
     */
    public String addControlTask() throws UnsupportedEncodingException
    {
        // ����������
        if (StringUtil.IsNullOrEmpty(logicAddress)
                || (null == taskStartTime)
                || (null == taskEndTime))
        {
            return ERROR;
        }
        
        // ��·ѡ��
        String[] strCircuits = taskCircuitSelect.split(",");
        // ѭ��ģʽ
        long iCycleMode = getTimeScaleByString(taskCycleMode);
        // ����������Ŀ�ʼʱ��
        String[] strTaskItemStartupTimes = taskItemStartupTimes.split(",");
        // ����������Ķ���
        String[] strtaskItemActions = taskItemActions.split(",");
        
        // ��ӳ̿�����
        if (taskManageService.addControlTask(logicAddress, taskEnable, taskId, taskPriority, taskStartTime
                , taskEndTime, strCircuits, iCycleMode, strTaskItemStartupTimes, strtaskItemActions))
        {
            // ������Ӧ�����Json��ʽ��
            setResponseJson(new ByteArrayInputStream("success".getBytes("UTF-8")));
        }
        else 
        {
            // ������Ӧ�����Json��ʽ��
            setResponseJson(new ByteArrayInputStream("error".getBytes("UTF-8")));
        }
        
        return SUCCESS;
    }
    
    /**
     * ��ÿ���������Ϣ
     * @return  �ɹ�����success
     * @throws UnsupportedEncodingException
     */
    public String getControlTaskInfo() throws UnsupportedEncodingException
    {
        // ����������
        if (StringUtil.IsNullOrEmpty(logicAddress) 
                || (taskId <= 0))
        {
            return ERROR;
        }
        
        // ���ָ��Id������
        CControlTask task = taskManageService.getControlTask(logicAddress, taskId);
        if (null == task)
        {
            return ERROR;
        }

        // �ϳɷ��ؽ��
        JSONObject result = new JSONObject();
        result.put("isEnable", task.isEnable());
        result.put("id", task.getTaskId());
        result.put("priority", task.getPriority());
        // ��ʼ����������
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        result.put("startTime", sdf.format(task.getStartTime()));
        result.put("endTime", sdf.format(task.getEndTime()));
        // ִ������ʱ����ϸ
        result.put("cycleScale", task.getTimeScale());
        // ��·ѡ��
        result.put("selectedCircuit", task.getSelectedCircuit());
        // �����б�
        result.put("planList", task.getPlans());
        
        // ������Ӧ�����Json��ʽ��
        setResponseJson(new ByteArrayInputStream(result.toString().getBytes("UTF-8")));
        return SUCCESS;
    }
    
    /**
     * ɾ���̿�����
     * @return  �ɹ�����success
     * @throws UnsupportedEncodingException
     */
    public String deleteControlTasks() throws UnsupportedEncodingException
    {
        // ����������
        if (StringUtil.IsNullOrEmpty(logicAddress)
                || StringUtil.IsNullOrEmpty(taskIds))
        {
            return ERROR;
        }
        
        // �������ID
        String[] strSegments = taskIds.split(",");
        
        // �ϳ�����ID�б�
        List<Integer> lstTaskIds = new ArrayList<Integer>();
        for(int i = 0; i < strSegments.length; i++)
        {
            lstTaskIds.add(Integer.parseInt(strSegments[i]));
        }
        
        // ɾ���̿�����
        if (taskManageService.deleteControlTasks(logicAddress, lstTaskIds))
        {
            // ������Ӧ�����Json��ʽ��
            setResponseJson(new ByteArrayInputStream("success".getBytes("UTF-8")));
        }
        else 
        {
            // ������Ӧ�����Json��ʽ��
            setResponseJson(new ByteArrayInputStream("error".getBytes("UTF-8")));
        }
        
        return SUCCESS;
    }
}
