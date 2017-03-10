package com.xtoee.service.impl;

import java.util.List;

import com.xtoee.devices.CPowerSystem;
import com.xtoee.devices.IDevice;
import com.xtoee.devices.dcu.CDcuDevice;
import com.xtoee.devices.dcu.alarmLog.CAlarmLogBase;
import com.xtoee.service.LogManageService;
import com.xtoee.util.StringUtil;

/**
 * 日志管理实现类
 * @author zgm
 *
 */
public class LogManageServiceImpl implements LogManageService
{
    /**
     * 获得告警任务列表
     * @param logicAddress  设备逻辑地址
     * @return  告警任务列表
     */
    @Override
    public List<CAlarmLogBase> getAlarmLogList(String logicAddress)
    {
        // 检查输入参数
        if (StringUtil.IsNullOrEmpty(logicAddress))
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
        IDevice device = powerSystem.GetDeviceByLogicAddress(logicAddress);
        if ((null == device) || !(device instanceof CDcuDevice))
        {
            return null;
        }
        
        CDcuDevice dcuDevice = (CDcuDevice) device;
        return dcuDevice.getAlarmLogList();
    }

}
