package com.xtoee.service.impl;

import java.util.List;

import com.xtoee.devices.CPowerSystem;
import com.xtoee.devices.IDevice;
import com.xtoee.devices.dcu.CDcuDevice;
import com.xtoee.devices.dcu.CIoctlParam_DeleteArchives;
import com.xtoee.devices.dcu.CIoctlResult;
import com.xtoee.devices.dcu.enumDataItem;
import com.xtoee.devices.dcu.enumErrorCode;
import com.xtoee.devices.dcu.deviceNode.CModuleBase;
import com.xtoee.service.ArchiveManageService;
import com.xtoee.util.StringUtil;

/**
 * 档案管理实现类
 * @author zgm
 *
 */
public class ArchiveManageServiceImpl implements ArchiveManageService
{
    /**
     * 获得档案列表
     * @param logicAddress  设备逻辑地址
     * @return  档案列表
     */
    @Override
    public List<CModuleBase> getArchiveList(String logicAddress)
    {
        // 检查输入参数
        if ((null == logicAddress))
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
        
        CDcuDevice cpcDevice = (CDcuDevice)device;
        return cpcDevice.getArchiveList();
    }

    /**
     * 添加档案列表
     * @param logicAddress  设备逻辑地址
     * @param lstModules    档案列表
     * @return  成功返回true
     */
    @Override
    public boolean addArchives(String logicAddress, List<CModuleBase> lstModules)
    {
        // 检查输入参数
        if (StringUtil.IsNullOrEmpty(logicAddress) 
                || (lstModules == null)
                || (lstModules.size() <= 0))
        {
            return false;
        }
        
        // 获得第一个档案
        CModuleBase moduleBase = lstModules.get(0);
        if (null == moduleBase)
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
        
        // 发送远程控制命令
        CIoctlResult ioctlResult = new CIoctlResult();
        if (cpcDevice.Ioctl(enumDataItem.AddArchives.toString(), lstModules, ioctlResult)
                && (ioctlResult.getErrorCode() == enumErrorCode.Success))
        {
            // 添加档案到档案列表，使页面能及时看到最新的档案信息
            List<CModuleBase> lstArchives = cpcDevice.getArchiveList();
            if (null != lstArchives)
            {
                int iPosition = 0;
                for(iPosition = 0; iPosition < lstArchives.size(); iPosition++)
                {
                    CModuleBase archive = lstArchives.get(iPosition);
                    if (archive.getModuleId() > moduleBase.getModuleId())
                    {
                        break;
                    }
                }
                
                lstArchives.add(iPosition, moduleBase);
            }
            
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 删除档案
     * @param logicAddress  设备逻辑地址
     * @param lstArchiveIds 待删除的档案Id列表
     * @return  成功返回true
     */
    @Override
    public boolean deleteArchives(String logicAddress, List<Integer> lstArchiveIds)
    {
        // 检查输入参数
        if (StringUtil.IsNullOrEmpty(logicAddress)
                || (null == lstArchiveIds)
                || (lstArchiveIds.size() == 0))
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
        CDcuDevice dcuDevice = (CDcuDevice)device;
        
        // 合成档案ID列表
        CIoctlParam_DeleteArchives daParam = new CIoctlParam_DeleteArchives();
        daParam.setDeviceIdList(lstArchiveIds);
        
        // 删除设备档案
        CIoctlResult result = new CIoctlResult();
        if (dcuDevice.Ioctl(enumDataItem.DeleteArchives.toString(), daParam, result)
                && (result.getErrorCode() == enumErrorCode.Success))
        {
            // 从档案列表中删除指定的档案，使页面能及时看到最新的档案信息
            List<CModuleBase> lstArchives = (List<CModuleBase>) dcuDevice.getArchiveList();
            if (null != lstArchives)
            {
                // 遍历待删除的档案ID列表
                for (Integer archiveId : lstArchiveIds)
                {
                    // 从档案列表中删除指定ID的档案
                    for(int i = 0; i < lstArchives.size(); i++)
                    {
                        if (lstArchives.get(i).getModuleId() == archiveId)
                        {
                            lstArchives.remove(i);
                            break;
                        }
                    }
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }
}
