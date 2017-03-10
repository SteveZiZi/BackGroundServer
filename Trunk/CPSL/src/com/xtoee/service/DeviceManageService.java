package com.xtoee.service;

import java.util.List;

import com.xtoee.devices.IDevice;

/**
 * �豸����ӿ�
 * @author zgm
 *
 */
public interface DeviceManageService
{
    /**
     * ����豸�б�
     * @return  �豸�б�
     */
    public List<IDevice> getDeviceList();
    
    /**
     * �����豸�߼���ַ������豸����
     * @param strLogicAddress   �豸�߼���ַ
     * @return  �豸����
     */
    public IDevice GetDeviceByLogicAddress(String strLogicAddress);
    
    /**
     * �޸��豸ָ����·������
     * @param strLogicAddress       �豸�߼���ַ
     * @param closedCircuitName     ��·����  
     * @param closedCircuitLight    ��·����
     * @return  �ɹ�����true
     */
    public boolean changeClosedCircuitLight(String strLogicAddress, String closedCircuitName, String closedCircuitLight);
    
    /**
     * ��·����
     * @param strLogicAddress       �豸�߼���ַ
     * @param closedCircuitName     ��·����
     * @param closedCircuitSwitch   ��·����
     * @return  �ɹ�����true
     */
    public boolean closedCircuitSwitch(String strLogicAddress, String closedCircuitName, String closedCircuitSwitch);
}
