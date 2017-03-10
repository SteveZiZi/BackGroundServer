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
 * �豸����ʵ����
 * @author zgm
 *
 */
public class DeviceManageServiceImpl implements DeviceManageService
{
    /**
     * ����豸�б�
     * @return  �豸�б�
     */
    @Override
    public List<IDevice> getDeviceList()
    {
        // ��ù���ϵͳ����
        CPowerSystem powerSystem = CPowerSystem.GetInstance();
        if (null == powerSystem)
        {
            return null;
        }

        // ��ù���ϵͳ������豸�б�
        return powerSystem.getDeviceList();
    }

    /**
     * �����豸�߼���ַ������豸����
     * @param strLogicAddress   �豸�߼���ַ
     * @return  �豸����
     */
    @Override
    public IDevice GetDeviceByLogicAddress(String strLogicAddress)
    {
        // ����������
        if (StringUtil.IsNullOrEmpty(strLogicAddress))
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
        return powerSystem.GetDeviceByLogicAddress(strLogicAddress);
    }

    /**
     * �޸��豸ָ����·������
     * @param strLogicAddress       �豸�߼���ַ
     * @param closedCircuitName     ��·����  
     * @param closedCircuitLight    ��·����
     * @return  �ɹ�����true
     */
    @Override
    public boolean changeClosedCircuitLight(String logicAddress, String closedCircuitName, String closedCircuitLight)
    {
        // ����������
        if (StringUtil.IsNullOrEmpty(logicAddress)
                || StringUtil.IsNullOrEmpty(closedCircuitName)
                || StringUtil.IsNullOrEmpty(closedCircuitLight))
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
        CDcuDevice dcuDevice = (CDcuDevice) device;

        // ���ָ����·��Dim-Vdb��
        int iClosedCircuitId = Integer.parseInt(closedCircuitName.substring(4, 6));
        Map<String, Integer> mapDimVdb = dcuDevice.getDimVdbMapByCCId(iClosedCircuitId);
        if (null == mapDimVdb)
        {
            return false;
        }

        // �ϳɿ��Ʋ���
        List<CIoctlParam_RemoteControl> lstParams = new LinkedList<CIoctlParam_RemoteControl>();
        if (Integer.parseInt(closedCircuitLight) < 0)
        {
            // ��·��������
            lstParams.add(new CIoctlParam_RemoteControl(iClosedCircuitId, 0
                    , enumSpecificFunction.ClosedCircuitClose, 0));
        }
        else
        {
            // ��·��ѹ����
            lstParams.add(new CIoctlParam_RemoteControl(iClosedCircuitId, 0
                    , enumSpecificFunction.ClosedCircuitVoltage, mapDimVdb.get(closedCircuitLight)));
        }

        // ����Զ�̿�������
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
     * ��·����
     * @param strLogicAddress       �豸�߼���ַ
     * @param closedCircuitName     ��·����
     * @param closedCircuitSwitch   ��·����
     * @return  �ɹ�����true
     */
    @Override
    public boolean closedCircuitSwitch(String strLogicAddress, String closedCircuitName, String closedCircuitSwitch)
    {
        // ����������
        if (StringUtil.IsNullOrEmpty(strLogicAddress)
                || StringUtil.IsNullOrEmpty(closedCircuitName)
                || StringUtil.IsNullOrEmpty(closedCircuitSwitch))
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
        IDevice device = powerSystem.GetDeviceByLogicAddress(strLogicAddress);
        if ((null == device) || !(device instanceof CDcuDevice))
        {
            return false;
        }
        CDcuDevice cpcDevice = (CDcuDevice)device;
        
        // �ϳɿ��Ʋ���
        int iClosedCircuitId = Integer.parseInt(closedCircuitName.substring(4, 6));
        List<CIoctlParam_RemoteControl> lstParams = new LinkedList<CIoctlParam_RemoteControl>();
        lstParams.add(new CIoctlParam_RemoteControl(iClosedCircuitId, 0
                , ("on".equalsIgnoreCase(closedCircuitSwitch)
                        ? enumSpecificFunction.ClosedCircuitOpen
                        : enumSpecificFunction.ClosedCircuitClose)
                , 0));
        
        // ����Զ�̿�������
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
