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
 * ���ù���ʵ����
 * @author zgm
 *
 */
public class ConfigManageServiceImpl implements ConfigManageService
{
    /**
     * ��ù���Ա�ֻ���
     * @param logicAddress  �豸�߼���ַ
     * @return  �ֻ�����
     */
    @Override
    public String getAdminPhone(String logicAddress)
    {
        // ����������
        if (StringUtil.IsNullOrEmpty(logicAddress))
        {
            return null;
        }
        
        // ��ù���ϵͳ����
        CPowerSystem powerSystem = CPowerSystem.GetInstance();
        if (null == powerSystem)
        {
            return null;
        }

        // ���ָ���߼���ַ���豸����
        IDevice device = powerSystem.GetDeviceByLogicAddress(logicAddress);
        if ((null == device) || !(device instanceof CDcuDevice))
        {
            return null;
        }
        
        CDcuDevice dcuDevice = (CDcuDevice) device;
        return dcuDevice.getAdminPhone();
    }

    /**
     * ���ù���Ա�ֻ���
     * @param logicAddress  �豸�߼���ַ
     * @param adminPhone    ����Ա�ֻ���
     * @return  �ɹ�����true
     */
    @Override
    public boolean setAdminPhone(String logicAddress, String adminPhone)
    {
        // ����������
        if (StringUtil.IsNullOrEmpty(logicAddress) 
                || StringUtil.IsNullOrEmpty(adminPhone))
        {
            return false;
        }
        
        // ��ù���ϵͳ����
        CPowerSystem powerSystem = CPowerSystem.GetInstance();
        if (null == powerSystem)
        {
            return false;
        }

        // ���ָ���߼���ַ���豸����
        IDevice device = powerSystem.GetDeviceByLogicAddress(logicAddress);
        if ((null == device) || !(device instanceof CDcuDevice))
        {
            return false;
        }
        CDcuDevice cpcDevice = (CDcuDevice)device;
        
        // ���ù���Ա�绰
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
     * �����豸ʱ��
     * @param logicAddress  �豸�߼���ַ
     * @param newDatetime   �豸ʱ��
     * @return  �ɹ�����true
     */
    @Override
    public boolean setDeviceTime(String logicAddress, Date newDatetime)
    {
        // ����������
        if (StringUtil.IsNullOrEmpty(logicAddress) || (newDatetime == null))
        {
            return false;
        }
        
        // ��ù���ϵͳ����
        CPowerSystem powerSystem = CPowerSystem.GetInstance();
        if (null == powerSystem)
        {
            return false;
        }

        // ���ָ���߼���ַ���豸����
        IDevice device = powerSystem.GetDeviceByLogicAddress(logicAddress);
        if ((null == device) || !(device instanceof CDcuDevice))
        {
            return false;
        }
        CDcuDevice cpcDevice = (CDcuDevice)device;
        
        // Уʱ
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
