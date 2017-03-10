package com.xtoee.service;

import java.util.Date;
import java.util.List;

import com.xtoee.devices.dcu.CControlTask;

/**
 * 任务管理接口
 * @author zgm
 *
 */
public interface TaskManageService
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
    public boolean addControlTask(String strLogicAddress, boolean bTaskEnable, int iTaskId, int iTaskPriority, Date dtStart, Date dtEnd, String[] strCircuits, long iCycleMode, String[] strTaskItemStartupTimes, String[] strtaskItemActions);
    
    /**
     * 获得程控任务
     * @param strLogicAddress   设备逻辑地址
     * @param iTaskId           任务ID
     * @return  程控任务
     */
    public CControlTask getControlTask(String strLogicAddress, int iTaskId);
    
    /**
     * 删除控制任务
     * @param logicAddress      设备逻辑地址
     * @param lstTaskIds        待删除的任务ID列表
     * @return  成功返回true
     */
    public boolean deleteControlTasks(String logicAddress, List<Integer> lstTaskIds);
}
