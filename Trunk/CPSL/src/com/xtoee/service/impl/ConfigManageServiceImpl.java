package com.xtoee.service.impl;

import java.util.Date;

import com.xtoee.devices.CPowerSystem;
import com.xtoee.devices.IDevice;
import com.xtoee.devices.dcu.CDcuDevice;
import com.xtoee.devices.dcu.CIoctlResult;
import com.xtoee.devices.dcu.enumDataItem;
import com.xtoee.devices.dcu.enumErrorCode;
import com.xtoee.service.ConfigManageService;
import com.xtoee.util.StringUtil;

/**
 * 配置管理实现类
 * @author zgm
 *
 */
public class ConfigManageServiceImpl implements ConfigManageService
{
    /**
     * 获得管理员手机号
     * @param logicAddress  设备逻辑地址
     * @return  手机号码
     */
    @Override
    public String getAdminPhone(String logicAddress)
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
        return dcuDevice.getAdminPhone();
    }

    /**
     * 设置管理员手机号
     * @param logicAddress  设备逻辑地址
     * @param adminPhone    管理员手机号
     * @return  成功返回true
     */
    @Override
    public boolean setAdminPhone(String logicAddress, String adminPhone)
    {
        // 检查输入参数
        if (StringUtil.IsNullOrEmpty(logicAddress) 
                || StringUtil.IsNullOrEmpty(adminPhone))
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
        
        // 设置管理员电话
        CIoctlResult result = new CIoctlResult();
        if (cpcDevice.Ioctl(enumDataItem.AdminPhone.toString(), adminPhone, result)
                && (result.getErrorCode() == enumErrorCode.Success))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 设置设备时间
     * @param logicAddress  设备逻辑地址
     * @param newDatetime   设备时间
     * @return  成功返回true
     */
    @Override
    public boolean setDeviceTime(String logicAddress, Date newDatetime)
    {
        // 检查输入参数
        if (StringUtil.IsNullOrEmpty(logicAddress) || (newDatetime == null))
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
        
        // 校时
        CIoctlResult result = new CIoctlResult();
        if (cpcDevice.Ioctl(enumDataItem.Timing.toString(), newDatetime, result)
                && (result.getErrorCode() == enumErrorCode.Success))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
