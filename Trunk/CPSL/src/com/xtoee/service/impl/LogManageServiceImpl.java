package com.xtoee.service.impl;

import java.util.List;

import com.xtoee.devices.CPowerSystem;
import com.xtoee.devices.IDevice;
import com.xtoee.devices.dcu.CDcuDevice;
import com.xtoee.devices.dcu.alarmLog.CAlarmLogBase;
import com.xtoee.service.LogManageService;
import com.xtoee.util.StringUtil;

/**
 * ��־����ʵ����
 * @author zgm
 *
 */
public class LogManageServiceImpl implements LogManageService
{
    /**
     * ��ø澯�����б�
     * @param logicAddress  �豸�߼���ַ
     * @return  �澯�����б�
     */
    @Override
    public List<CAlarmLogBase> getAlarmLogList(String logicAddress)
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
        return dcuDevice.getAlarmLogList();
    }

}
