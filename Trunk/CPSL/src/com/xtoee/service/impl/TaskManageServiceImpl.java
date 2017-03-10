package com.xtoee.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.xtoee.devices.CPowerSystem;
import com.xtoee.devices.IDevice;
import com.xtoee.devices.dcu.CControlTask;
import com.xtoee.devices.dcu.CDcuDevice;
import com.xtoee.devices.dcu.CImplementPlan;
import com.xtoee.devices.dcu.CIoctlParam_RemoteControl;
import com.xtoee.devices.dcu.CIoctlResult;
import com.xtoee.devices.dcu.enumDataItem;
import com.xtoee.devices.dcu.enumErrorCode;
import com.xtoee.devices.dcu.enumSpecificFunction;
import com.xtoee.service.TaskManageService;
import com.xtoee.util.StringUtil;

/**
 * 任务管理实现类
 * @author zgm
 *
 */
public class TaskManageServiceImpl implements TaskManageService
{
    /**
     * 添加程控任务
     * @param strLogicAddress           设备逻辑地址
     * @param bTaskEnable               任务使能
     * @param iTaskId                   任务ID
     * @param iTaskPriority             任务优先级
     * @param dtStart                   开始时间
     * @param dtEnd                     结束时间
     * @param strCircuits               回路选择
     * @param iCycleMode                循环模式
     * @param strTaskItemStartupTimes   所有任务项的开始时间
     * @param strtaskItemActions        所有任务项的动作
     * @return  成功返回true 
     */
    @Override
    public boolean addControlTask(String strLogicAddress, boolean bTaskEnable, int iTaskId
            , int iTaskPriority, Date dtStart, Date dtEnd, String[] strCircuits, long iCycleMode
            , String[] strTaskItemStartupTimes, String[] strtaskItemActions)
    {
        // 检查输入参数
        if (StringUtil.IsNullOrEmpty(strLogicAddress)
                || (null == dtStart)
                || (null == dtEnd)
                || (null == strCircuits)
                || (strCircuits.length == 0)
                || (null == strTaskItemStartupTimes)
                || (null == strtaskItemActions)
                || (strTaskItemStartupTimes.length == 0)
                || (strTaskItemStartupTimes.length != strtaskItemActions.length))
        {
            return false;
        }
        
        // 获得供电系统对象
        CPowerSystem powerSystem = CPowerSystem.GetInstance();
        if (null == powerSystem)
        {
            return false;
        }

        // 获得指定逻辑地址的设备对象
        IDevice device = powerSystem.GetDeviceByLogicAddress(strLogicAddress);
        if ((null == device) || !(device instanceof CDcuDevice))
        {
            return false;
        }
        CDcuDevice dcuDevice = (CDcuDevice)device;

        // 先删除指定ID的控制任务
        List<Integer> lstTaskIds = new ArrayList<Integer>();
        lstTaskIds.add(iTaskId);
        CIoctlResult result = new CIoctlResult();
        if (dcuDevice.Ioctl(enumDataItem.DeleteControlTasks.toString(), lstTaskIds, result)
                && (result.getErrorCode() == enumErrorCode.Success))
        {
            // 删除任务列表中指定编号的任务
            List<CControlTask> lstControlTasks = dcuDevice.getContorlTaskList();
            if (null != lstControlTasks)
            {
                int iPosition = 0;
                for(iPosition = 0; iPosition < lstControlTasks.size(); iPosition++)
                {
                    CControlTask controlTask = lstControlTasks.get(iPosition);
                    if (controlTask.getTaskId() == iTaskId)
                    {
                        lstControlTasks.remove(iPosition);
                        break;
                    }
                }
            }
        }
        
        // 创建程控任务对象
        CControlTask newControlTask = new CControlTask(dcuDevice, bTaskEnable, iTaskId, iTaskPriority, dtStart, dtEnd, 
                CControlTask.CYCLE_TYPE_WEEK, iCycleMode);

        // 遍历任务项的开始时间数组
        CImplementPlan plan = null;
        CIoctlParam_RemoteControl rc = null;
        SimpleDateFormat sdtTime = new SimpleDateFormat("HH:mm");
        for (int i = 0; i < strTaskItemStartupTimes.length; i++)
        {
            // 创建执行方案
            try
            {
                plan = new CImplementPlan(sdtTime.parse(strTaskItemStartupTimes[i]));
            }
            catch (ParseException e)
            {
                return false;
            }
            
            // 遍历回路数组
            for (String strCircuit : strCircuits)
            {
                // 获得回路的Dim-Vdb表
                int iCircuitId = Integer.parseInt(strCircuit);
                Map<String, Integer> mapDimVdb = dcuDevice.getDimVdbMapByCCId(iCircuitId);
                if (null == mapDimVdb)
                {
                    return false;
                }

                // 如果是关闭回路
                if ("-1".equals(strtaskItemActions[i]))
                {
                    rc = new CIoctlParam_RemoteControl(
                            iCircuitId, 0,
                            enumSpecificFunction.ClosedCircuitClose, 0);
                }
                // 如果是设置回路电压
                else
                {
                    rc = new CIoctlParam_RemoteControl(
                            iCircuitId, 0,
                            enumSpecificFunction.ClosedCircuitVoltage, mapDimVdb.get(strtaskItemActions[i]));
                }
                
                // 添加任务内容
                plan.AddTaskContent(rc);
            }

            // 添加执行方案
            newControlTask.AddImplementPlan(plan);
        }

        // 设置远程控制任务
        if (dcuDevice.Ioctl(enumDataItem.WriteControlTask.toString(), newControlTask, result)
                && (result.getErrorCode() == enumErrorCode.Success))
        {
            // 添加任务到任务列表，使页面能及时看到最新的任务信息
            List<CControlTask> lstControlTasks = dcuDevice.getContorlTaskList();
            if (null != lstControlTasks)
            {
                int iPosition = 0;
                for(iPosition = 0; iPosition < lstControlTasks.size(); iPosition++)
                {
                    CControlTask controlTask = lstControlTasks.get(iPosition);
                    if (controlTask.getTaskId() > newControlTask.getTaskId())
                    {
                        break;
                    }
                }
                
                lstControlTasks.add(iPosition, newControlTask);
            }
            
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 获得程控任务
     * @param strLogicAddress   设备逻辑地址
     * @param iTaskId           任务ID
     * @return  程控任务
     */
    @Override
    public CControlTask getControlTask(String strLogicAddress, int iTaskId)
    {
        // 检查输入参数
        if ((StringUtil.IsNullOrEmpty(strLogicAddress)) || (iTaskId < 0))
        {
            return null;
        }
        
        // 获得供电系统对象
        CPowerSystem powerSystem = CPowerSystem.GetInstance();
        if (null == powerSystem)
        {
            return null;
        }

        // 获得指定逻辑地址的设备对象
        IDevice device = powerSystem.GetDeviceByLogicAddress(strLogicAddress);
        if ((null == device) || !(device instanceof CDcuDevice))
        {
            return null;
        }
        CDcuDevice cpcDevice = (CDcuDevice)device;
        
        // 获得任务列表
        List<CControlTask> lstControlTasks = cpcDevice.getContorlTaskList();
        if (null == lstControlTasks)
        {
            return null;
        }
        
        // 获得指定Id的控制任务
        for (CControlTask task : lstControlTasks)
        {
            if (task.getTaskId() == iTaskId)
            {
                return task;
            }
        }
        
        return null;
    }

    /**
     * 删除控制任务
     * @param logicAddress      设备逻辑地址
     * @param lstTaskIds        待删除的任务ID列表
     * @return  成功返回true
     */
    @Override
    public boolean deleteControlTasks(String logicAddress, List<Integer> lstTaskIds)
    {
        // 检查输入参数
        if (StringUtil.IsNullOrEmpty(logicAddress)
                || (null == lstTaskIds)
                || (lstTaskIds.size() == 0))
        {
            return false;
        }
        
        // 获得供电系统对象
        CPowerSystem powerSystem = CPowerSystem.GetInstance();
        if (null == powerSystem)
        {
            return false;
        }
        
        // 获得指定逻辑地址的设备对象
        IDevice device = powerSystem.GetDeviceByLogicAddress(logicAddress);
        if ((null == device) || !(device instanceof CDcuDevice))
        {
            return false;
        }
        CDcuDevice cpcDevice = (CDcuDevice)device;
        
        // 删除控制任务
        CIoctlResult result = new CIoctlResult();
        if (cpcDevice.Ioctl(enumDataItem.DeleteControlTasks.toString(), lstTaskIds, result)
                && (result.getErrorCode() == enumErrorCode.Success))
        {
            // 从任务列表中删除指定的任务，使页面能及时看到最新的任务信息
            List<CControlTask> lstControlTasks = cpcDevice.getContorlTaskList();
            if (null != lstControlTasks)
            {
                // 遍历待删除的任务ID列表
                for (Integer taskId : lstTaskIds)
                {
                    // 从任务列表中删除指定ID的任务
                    for(int i = 0; i < lstControlTasks.size(); i++)
                    {
                        if (lstControlTasks.get(i).getTaskId() == taskId)
                        {
                            lstControlTasks.remove(i);
                            break;
                        }
                    }
                }
            }
            
            return true;
        }

        return false;
    }
}
