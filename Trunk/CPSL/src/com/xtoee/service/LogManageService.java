package com.xtoee.service;

import java.util.List;

import com.xtoee.devices.dcu.alarmLog.CAlarmLogBase;

/**
 * 日志管理接口
 * @author zgm
 *
 */
public interface LogManageService
{
    /**
     * 获得告警任务列表
     * @param logicAddress  设备逻辑地址
     * @return  告警任务列表
     */
    public List<CAlarmLogBase> getAlarmLogList(String logicAddress);
}
