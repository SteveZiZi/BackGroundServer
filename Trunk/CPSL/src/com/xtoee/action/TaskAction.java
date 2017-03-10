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
 * 任务控制器
 * @author zgm
 *
 */
public class TaskAction extends ActionSupport
{
    private static final long       serialVersionUID = 1L;
    private DeviceManageService     deviceManageService;        // 设备管理接口
    private TaskManageService       taskManageService;          // 任务管理接口
    private InputStream             responseJson;               // 响应结果（Json格式）
    
    private Integer                 page;                       // 页号
    private Integer                 rows;                       // 行号
    
    private String                  logicAddress;               // 设备逻辑地址
    
    private boolean                 taskEnable;                 // 任务使能
    private int                     taskId;                     // 任务ID
    private int                     taskPriority;               // 任务优先级
    private Date                    taskStartTime;              // 开始时间
    private Date                    taskEndTime;                // 结束时间
    private String                  taskCircuitSelect;          // 回路选择
    private String                  taskCycleMode;              // 循环模式
    private String                  taskItemStartupTimes;       // 所有任务项的开始时间
    private String                  taskItemActions;            // 所有任务项的动作
    
    private String                  taskIds;                    // 待删除的任务ID
    
    
    /**
     * 设置设备管理接口
     * @param deviceManageService   设备管理对象
     */
    public void setDeviceManageService(DeviceManageService deviceManageService)
    {
        this.deviceManageService = deviceManageService;
    }
    
    /**
     * 设置任务管理接口
     * @param taskManageService     任务管理对象
     */
    public void setTaskManageService(TaskManageService taskManageService)
    {
        this.taskManageService = taskManageService;
    }
    
    /**
     * 设置页号
     * @param page  页号
     */
    public void setPage(Integer page)
    {
        this.page = page;
    }

    /**
     * 设置行号
     * @param rows 行号
     */
    public void setRows(Integer rows)
    {
        this.rows = rows;
    }
    
    /**
     * 设置设备逻辑地址
     * @param logicAddress  设备逻辑地址
     */
    public void setLogicAddress(String logicAddress)
    {
        this.logicAddress = logicAddress;
    }

    /**
     * 获得响应结果（Json格式）
     * @return  响应结果（Json格式）
     */
    public InputStream getResponseJson()
    {
        return responseJson;
    }
    
    /**
     * 设置响应结果（Json格式）
     * @param responseJson  响应结果（Json格式）
     */
    public void setResponseJson(InputStream responseJson)
    {
        this.responseJson = responseJson;
    }

    /**
     * 设置任务使能
     * @param taskEnable    任务使能
     */
    public void setTaskEnable(boolean taskEnable)
    {
        this.taskEnable = taskEnable;
    }

    /**
     * 设置任务ID
     * @param taskId    任务ID
     */
    public void setTaskId(int taskId)
    {
        this.taskId = taskId;
    }

    /**
     * 设置任务优先级
     * @param taskPriority  任务优先级
     */
    public void setTaskPriority(int taskPriority)
    {
        this.taskPriority = taskPriority;
    }

    /**
     * 设置开始时间
     * @param taskStartTime 开始时间
     */
    public void setTaskStartTime(Date taskStartTime)
    {
        this.taskStartTime = taskStartTime;
    }

    /**
     * 设置结束时间
     * @param taskEndTime   结束时间
     */
    public void setTaskEndTime(Date taskEndTime)
    {
        this.taskEndTime = taskEndTime;
    }

    /**
     * 设置回路选择
     * @param taskCircuitSelect 回路选择
     */
    public void setTaskCircuitSelect(String taskCircuitSelect)
    {
        this.taskCircuitSelect = taskCircuitSelect;
    }

    /**
     * 设置循环模式
     * @param taskCycleMode 循环模式
     */
    public void setTaskCycleMode(String taskCycleMode)
    {
        this.taskCycleMode = taskCycleMode;
    }

    /**
     * 设置所有任务项的开始时间
     * @param taskItemStartupTimes  所有任务项的开始时间
     */
    public void setTaskItemStartupTimes(String taskItemStartupTimes)
    {
        this.taskItemStartupTimes = taskItemStartupTimes;
    }

    /**
     * 设置所有任务项的动作
     * @param taskItemActions   所有任务项的动作
     */
    public void setTaskItemActions(String taskItemActions)
    {
        this.taskItemActions = taskItemActions;
    }
    
    /**
     * 设置待删除的任务ID
     * @param taskIds   待删除的任务ID
     */
    public void setTaskIds(String taskIds)
    {
        this.taskIds = taskIds;
    }
    
    /**
     * 获得程控任务列表
     * @return  成功返回success
     * @throws UnsupportedEncodingException
     */
    public String getControlTaskList() throws UnsupportedEncodingException
    {
        // 检查输入参数
        if (StringUtil.IsNullOrEmpty(logicAddress) 
                || (page <= 0) 
                || (rows <= 0))
        {
            return ERROR;
        }
        
        // 根据设备逻辑地址，获得设备对象
        IDevice device = deviceManageService.GetDeviceByLogicAddress(logicAddress);
        if ((null == device) || !(device instanceof CDcuDevice))
        {
            return ERROR;
        }
        CDcuDevice cpcDevice = (CDcuDevice)device;
        
        // 获得任务列表
        List<CControlTask> lstControlTasks = cpcDevice.getContorlTaskList();
        if (null == lstControlTasks)
        {
            return ERROR;
        }

        // 循环遍历告警日志列表
        int iCount = 0;
        JSONArray jsonArray = new JSONArray();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (CControlTask task : lstControlTasks)
        {
            // 递增行数
            iCount++;
            
            // 检查行号是否在指定的页码范围
            if ((iCount > (page - 1) * rows) && (iCount <= page * rows))
            {
                // 获得告警日志信息
                JSONObject row = new JSONObject();
                row.put("taskId", task.getTaskId());
                row.put("taskPriority", CControlTask.getPriorityString(task.getPriority()));
                row.put("taskStartDate", sdf.format(task.getStartTime()));
                row.put("taskEndDate", sdf.format(task.getEndTime()));

                // 添加告警日志信息到行集合中
                jsonArray.add(row);
            }
        }

        // 合成返回结果
        JSONObject result = new JSONObject();
        result.put("total", lstControlTasks.size());
        result.put("rows", jsonArray);
        
        // 设置响应结果（Json格式）
        setResponseJson(new ByteArrayInputStream(result.toString().getBytes("UTF-8")));
        return SUCCESS;
    }
    
    /**
     * 获得执行周期时标明细
     * @param strCycleMode  执行周期字符串（例如：1,3,5,7）
     * @return  执行周期时标明细
     */
    protected long getTimeScaleByString(String strCycleMode)
    {
        long            lRet = 0;
        
        
        // 星期天
        if (strCycleMode.indexOf("7") != -1)
        {
            lRet |= 0x01;
        }
        
        // 星期一
        if (strCycleMode.indexOf("1") != -1)
        {
            lRet |= 0x02;
        }
        
        // 星期二
        if (strCycleMode.indexOf("2") != -1)
        {
            lRet |= 0x04;
        }
        
        // 星期三
        if (strCycleMode.indexOf("3") != -1)
        {
            lRet |= 0x08;
        }
        
        // 星期四
        if (strCycleMode.indexOf("4") != -1)
        {
            lRet |= 0x10;
        }
        
        // 星期五
        if (strCycleMode.indexOf("5") != -1)
        {
            lRet |= 0x20;
        }
        
        // 星期六
        if (strCycleMode.indexOf("6") != -1)
        {
            lRet |= 0x40;
        }
        
        return lRet;
    }
    
    /**
     * 添加程控任务
     * @return  成功返回success
     * @throws UnsupportedEncodingException
     */
    public String addControlTask() throws UnsupportedEncodingException
    {
        // 检查输入参数
        if (StringUtil.IsNullOrEmpty(logicAddress)
                || (null == taskStartTime)
                || (null == taskEndTime))
        {
            return ERROR;
        }
        
        // 回路选择
        String[] strCircuits = taskCircuitSelect.split(",");
        // 循环模式
        long iCycleMode = getTimeScaleByString(taskCycleMode);
        // 所有任务项的开始时间
        String[] strTaskItemStartupTimes = taskItemStartupTimes.split(",");
        // 所有任务项的动作
        String[] strtaskItemActions = taskItemActions.split(",");
        
        // 添加程控任务
        if (taskManageService.addControlTask(logicAddress, taskEnable, taskId, taskPriority, taskStartTime
                , taskEndTime, strCircuits, iCycleMode, strTaskItemStartupTimes, strtaskItemActions))
        {
            // 设置响应结果（Json格式）
            setResponseJson(new ByteArrayInputStream("success".getBytes("UTF-8")));
        }
        else 
        {
            // 设置响应结果（Json格式）
            setResponseJson(new ByteArrayInputStream("error".getBytes("UTF-8")));
        }
        
        return SUCCESS;
    }
    
    /**
     * 获得控制任务信息
     * @return  成功返回success
     * @throws UnsupportedEncodingException
     */
    public String getControlTaskInfo() throws UnsupportedEncodingException
    {
        // 检查输入参数
        if (StringUtil.IsNullOrEmpty(logicAddress) 
                || (taskId <= 0))
        {
            return ERROR;
        }
        
        // 获得指定Id的任务
        CControlTask task = taskManageService.getControlTask(logicAddress, taskId);
        if (null == task)
        {
            return ERROR;
        }

        // 合成返回结果
        JSONObject result = new JSONObject();
        result.put("isEnable", task.isEnable());
        result.put("id", task.getTaskId());
        result.put("priority", task.getPriority());
        // 开始、结束日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        result.put("startTime", sdf.format(task.getStartTime()));
        result.put("endTime", sdf.format(task.getEndTime()));
        // 执行周期时标明细
        result.put("cycleScale", task.getTimeScale());
        // 回路选择
        result.put("selectedCircuit", task.getSelectedCircuit());
        // 方案列表
        result.put("planList", task.getPlans());
        
        // 设置响应结果（Json格式）
        setResponseJson(new ByteArrayInputStream(result.toString().getBytes("UTF-8")));
        return SUCCESS;
    }
    
    /**
     * 删除程控任务
     * @return  成功返回success
     * @throws UnsupportedEncodingException
     */
    public String deleteControlTasks() throws UnsupportedEncodingException
    {
        // 检查输入参数
        if (StringUtil.IsNullOrEmpty(logicAddress)
                || StringUtil.IsNullOrEmpty(taskIds))
        {
            return ERROR;
        }
        
        // 拆分任务ID
        String[] strSegments = taskIds.split(",");
        
        // 合成任务ID列表
        List<Integer> lstTaskIds = new ArrayList<Integer>();
        for(int i = 0; i < strSegments.length; i++)
        {
            lstTaskIds.add(Integer.parseInt(strSegments[i]));
        }
        
        // 删除程控任务
        if (taskManageService.deleteControlTasks(logicAddress, lstTaskIds))
        {
            // 设置响应结果（Json格式）
            setResponseJson(new ByteArrayInputStream("success".getBytes("UTF-8")));
        }
        else 
        {
            // 设置响应结果（Json格式）
            setResponseJson(new ByteArrayInputStream("error".getBytes("UTF-8")));
        }
        
        return SUCCESS;
    }
}
