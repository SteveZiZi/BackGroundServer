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
 * ��������ʵ����
 * @author zgm
 *
 */
public class ArchiveManageServiceImpl implements ArchiveManageService
{
    /**
     * ��õ����б�
     * @param logicAddress  �豸�߼���ַ
     * @return  �����б�
     */
    @Override
    public List<CModuleBase> getArchiveList(String logicAddress)
    {
        // ����������
        if ((null == logicAddress))
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
        
        CDcuDevice cpcDevice = (CDcuDevice)device;
        return cpcDevice.getArchiveList();
    }

    /**
     * ��ӵ����б�
     * @param logicAddress  �豸�߼���ַ
     * @param lstModules    �����б�
     * @return  �ɹ�����true
     */
    @Override
    public boolean addArchives(String logicAddress, List<CModuleBase> lstModules)
    {
        // ����������
        if (StringUtil.IsNullOrEmpty(logicAddress) 
                || (lstModules == null)
                || (lstModules.size() <= 0))
        {
            return false;
        }
        
        // ��õ�һ������
        CModuleBase moduleBase = lstModules.get(0);
        if (null == moduleBase)
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
        
        // ����Զ�̿�������
        CIoctlResult ioctlResult = new CIoctlResult();
        if (cpcDevice.Ioctl(enumDataItem.AddArchives.toString(), lstModules, ioctlResult)
                && (ioctlResult.getErrorCode() == enumErrorCode.Success))
        {
            // ��ӵ����������б�ʹҳ���ܼ�ʱ�������µĵ�����Ϣ
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
     * ɾ������
     * @param logicAddress  �豸�߼���ַ
     * @param lstArchiveIds ��ɾ���ĵ���Id�б�
     * @return  �ɹ�����true
     */
    @Override
    public boolean deleteArchives(String logicAddress, List<Integer> lstArchiveIds)
    {
        // ����������
        if (StringUtil.IsNullOrEmpty(logicAddress)
                || (null == lstArchiveIds)
                || (lstArchiveIds.size() == 0))
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
        CDcuDevice dcuDevice = (CDcuDevice)device;
        
        // �ϳɵ���ID�б�
        CIoctlParam_DeleteArchives daParam = new CIoctlParam_DeleteArchives();
        daParam.setDeviceIdList(lstArchiveIds);
        
        // ɾ���豸����
        CIoctlResult result = new CIoctlResult();
        if (dcuDevice.Ioctl(enumDataItem.DeleteArchives.toString(), daParam, result)
                && (result.getErrorCode() == enumErrorCode.Success))
        {
            // �ӵ����б���ɾ��ָ���ĵ�����ʹҳ���ܼ�ʱ�������µĵ�����Ϣ
            List<CModuleBase> lstArchives = (List<CModuleBase>) dcuDevice.getArchiveList();
            if (null != lstArchives)
            {
                // ������ɾ���ĵ���ID�б�
                for (Integer archiveId : lstArchiveIds)
                {
                    // �ӵ����б���ɾ��ָ��ID�ĵ���
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
