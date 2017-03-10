package com.xtoee.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.xtoee.devices.CPowerSystem;
import com.xtoee.devices.IDevice;
import com.xtoee.devices.dcu.CDcuDevice;
import com.xtoee.devices.dcu.CIoctlParam_RemoteControl;
import com.xtoee.devices.dcu.CIoctlResult;
import com.xtoee.devices.dcu.enumDataItem;
import com.xtoee.devices.dcu.enumErrorCode;
import com.xtoee.devices.dcu.enumSpecificFunction;
import com.xtoee.service.DeviceManageService;
import com.xtoee.util.StringUtil;

/**
 * 设备管理实现类
 * @author zgm
 *
 */
public class DeviceManageServiceImpl implements DeviceManageService
{
    /**
     * 获得设备列表
     * @return  设备列表
     */
    @Override
    public List<IDevice> getDeviceList()
    {
        // 获得供电系统对象
        CPowerSystem powerSystem = CPowerSystem.GetInstance();
        if (null == powerSystem)
        {
            return null;
        }

        // 获得供电系统管理的设备列表
        return powerSystem.getDeviceList();
    }

    /**
     * 根据设备逻辑地址，获得设备对象
     * @param strLogicAddress   设备逻辑地址
     * @return  设备对象
     */
    @Override
    public IDevice GetDeviceByLogicAddress(String strLogicAddress)
    {
        // 检查输入参数
        if (StringUtil.IsNullOrEmpty(strLogicAddress))
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
        return powerSystem.GetDeviceByLogicAddress(strLogicAddress);
    }

    /**
     * 修改设备指定回路的亮度
     * @param strLogicAddress       设备逻辑地址
     * @param closedCircuitName     回路名称  
     * @param closedCircuitLight    回路亮度
     * @return  成功返回true
     */
    @Override
    public boolean changeClosedCircuitLight(String logicAddress, String closedCircuitName, String closedCircuitLight)
    {
        // 检查输入参数
        if (StringUtil.IsNullOrEmpty(logicAddress)
                || StringUtil.IsNullOrEmpty(closedCircuitName)
                || StringUtil.IsNullOrEmpty(closedCircuitLight))
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
        CDcuDevice dcuDevice = (CDcuDevice) device;

        // 获得指定回路的Dim-Vdb表
        int iClosedCircuitId = Integer.parseInt(closedCircuitName.substring(4, 6));
        Map<String, Integer> mapDimVdb = dcuDevice.getDimVdbMapByCCId(iClosedCircuitId);
        if (null == mapDimVdb)
        {
            return false;
        }

        // 合成控制参数
        List<CIoctlParam_RemoteControl> lstParams = new LinkedList<CIoctlParam_RemoteControl>();
        if (Integer.parseInt(closedCircuitLight) < 0)
        {
            // 回路整流器关
            lstParams.add(new CIoctlParam_RemoteControl(iClosedCircuitId, 0
                    , enumSpecificFunction.ClosedCircuitClose, 0));
        }
        else
        {
            // 回路电压设置
            lstParams.add(new CIoctlParam_RemoteControl(iClosedCircuitId, 0
                    , enumSpecificFunction.ClosedCircuitVoltage, mapDimVdb.get(closedCircuitLight)));
        }

        // 发送远程控制命令
        CIoctlResult ioctlResult = new CIoctlResult();
        if (dcuDevice.Ioctl(enumDataItem.RemoteControl.toString(), lstParams, ioctlResult) 
                && (ioctlResult.getErrorCode() == enumErrorCode.Success))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 回路开关
     * @param strLogicAddress       设备逻辑地址
     * @param closedCircuitName     回路名称
     * @param closedCircuitSwitch   回路开关
     * @return  成功返回true
     */
    @Override
    public boolean closedCircuitSwitch(String strLogicAddress, String closedCircuitName, String closedCircuitSwitch)
    {
        // 检查输入参数
        if (StringUtil.IsNullOrEmpty(strLogicAddress)
                || StringUtil.IsNullOrEmpty(closedCircuitName)
                || StringUtil.IsNullOrEmpty(closedCircuitSwitch))
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
        CDcuDevice cpcDevice = (CDcuDevice)device;
        
        // 合成控制参数
        int iClosedCircuitId = Integer.parseInt(closedCircuitName.substring(4, 6));
        List<CIoctlParam_RemoteControl> lstParams = new LinkedList<CIoctlParam_RemoteControl>();
        lstParams.add(new CIoctlParam_RemoteControl(iClosedCircuitId, 0
                , ("on".equalsIgnoreCase(closedCircuitSwitch)
                        ? enumSpecificFunction.ClosedCircuitOpen
                        : enumSpecificFunction.ClosedCircuitClose)
                , 0));
        
        // 发送远程控制命令
        CIoctlResult ioctlResult = new CIoctlResult();
        if (cpcDevice.Ioctl(enumDataItem.RemoteControl.toString(), lstParams, ioctlResult)
                && (ioctlResult.getErrorCode() == enumErrorCode.Success))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
