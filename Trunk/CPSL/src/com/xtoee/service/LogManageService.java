package com.xtoee.service;

import java.util.List;

import com.xtoee.devices.dcu.alarmLog.CAlarmLogBase;

/**
 * ��־����ӿ�
 * @author zgm
 *
 */
public interface LogManageService
{
    /**
     * ��ø澯�����б�
     * @param logicAddress  �豸�߼���ַ
     * @return  �澯�����б�
     */
    public List<CAlarmLogBase> getAlarmLogList(String logicAddress);
}
