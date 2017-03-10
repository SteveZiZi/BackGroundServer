package com.xtoee.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.xtoee.devices.CPowerSystem;
import com.xtoee.devices.IDevice;
import com.xtoee.devices.dcu.CControlTask;
import com.xtoee.devices.dcu.CDcuDevice;
import com.xtoee.devices.dcu.CImplementPlan;
import com.xtoee.devices.dcu.CIoctlParam_RemoteControl;
import com.xtoee.devices.dcu.CIoctlResult;
import com.xtoee.devices.dcu.enumDataItem;
import com.xtoee.devices.dcu.enumErrorCode;
import com.xtoee.devices.dcu.enumSpecificFunction;
import com.xtoee.service.TaskManageService;
import com.xtoee.util.StringUtil;

/**
 * �������ʵ����
 * @author zgm
 *
 */
public class TaskManageServiceImpl implements TaskManageService
{
    /**
     * ��ӳ̿�����
     * @param strLogicAddress           �豸�߼���ַ
     * @param bTaskEnable               ����ʹ��
     * @param iTaskId                   ����ID
     * @param iTaskPriority             �������ȼ�
     * @param dtStart                   ��ʼʱ��
     * @param dtEnd                     ����ʱ��
     * @param strCircuits               ��·ѡ��
     * @param iCycleMode                ѭ��ģʽ
     * @param strTaskItemStartupTimes   ����������Ŀ�ʼʱ��
     * @param strtaskItemActions        ����������Ķ���
     * @return  �ɹ�����true 
     */
    @Override
    public boolean addControlTask(String strLogicAddress, boolean bTaskEnable, int iTaskId
            , int iTaskPriority, Date dtStart, Date dtEnd, String[] strCircuits, long iCycleMode
            , String[] strTaskItemStartupTimes, String[] strtaskItemActions)
    {
        // ����������
        if (StringUtil.IsNullOrEmpty(strLogicAddress)
                || (null == dtStart)
                || (null == dtEnd)
                || (null == strCircuits)
                || (strCircuits.length == 0)
                || (null == strTaskItemStartupTimes)
                || (null == strtaskItemActions)
                || (strTaskItemStartupTimes.length == 0)
                || (strTaskItemStartupTimes.length != strtaskItemActions.length))
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
        CDcuDevice dcuDevice = (CDcuDevice)device;

        // ��ɾ��ָ��ID�Ŀ�������
        List<Integer> lstTaskIds = new ArrayList<Integer>();
        lstTaskIds.add(iTaskId);
        CIoctlResult result = new CIoctlResult();
        if (dcuDevice.Ioctl(enumDataItem.DeleteControlTasks.toString(), lstTaskIds, result)
                && (result.getErrorCode() == enumErrorCode.Success))
        {
            // ɾ�������б���ָ����ŵ�����
            List<CControlTask> lstControlTasks = dcuDevice.getContorlTaskList();
            if (null != lstControlTasks)
            {
                int iPosition = 0;
                for(iPosition = 0; iPosition < lstControlTasks.size(); iPosition++)
                {
                    CControlTask controlTask = lstControlTasks.get(iPosition);
                    if (controlTask.getTaskId() == iTaskId)
                    {
                        lstControlTasks.remove(iPosition);
                        break;
                    }
                }
            }
        }
        
        // �����̿��������
        CControlTask newControlTask = new CControlTask(dcuDevice, bTaskEnable, iTaskId, iTaskPriority, dtStart, dtEnd, 
                CControlTask.CYCLE_TYPE_WEEK, iCycleMode);

        // ����������Ŀ�ʼʱ������
        CImplementPlan plan = null;
        CIoctlParam_RemoteControl rc = null;
        SimpleDateFormat sdtTime = new SimpleDateFormat("HH:mm");
        for (int i = 0; i < strTaskItemStartupTimes.length; i++)
        {
            // ����ִ�з���
            try
            {
                plan = new CImplementPlan(sdtTime.parse(strTaskItemStartupTimes[i]));
            }
            catch (ParseException e)
            {
                return false;
            }
            
            // ������·����
            for (String strCircuit : strCircuits)
            {
                // ��û�·��Dim-Vdb��
                int iCircuitId = Integer.parseInt(strCircuit);
                Map<String, Integer> mapDimVdb = dcuDevice.getDimVdbMapByCCId(iCircuitId);
                if (null == mapDimVdb)
                {
                    return false;
                }

                // ����ǹرջ�·
                if ("-1".equals(strtaskItemActions[i]))
                {
                    rc = new CIoctlParam_RemoteControl(
                            iCircuitId, 0,
                            enumSpecificFunction.ClosedCircuitClose, 0);
                }
                // ��������û�·��ѹ
                else
                {
                    rc = new CIoctlParam_RemoteControl(
                            iCircuitId, 0,
                            enumSpecificFunction.ClosedCircuitVoltage, mapDimVdb.get(strtaskItemActions[i]));
                }
                
                // �����������
                plan.AddTaskContent(rc);
            }

            // ���ִ�з���
            newControlTask.AddImplementPlan(plan);
        }

        // ����Զ�̿�������
        if (dcuDevice.Ioctl(enumDataItem.WriteControlTask.toString(), newControlTask, result)
                && (result.getErrorCode() == enumErrorCode.Success))
        {
            // ������������б�ʹҳ���ܼ�ʱ�������µ�������Ϣ
            List<CControlTask> lstControlTasks = dcuDevice.getContorlTaskList();
            if (null != lstControlTasks)
            {
                int iPosition = 0;
                for(iPosition = 0; iPosition < lstControlTasks.size(); iPosition++)
                {
                    CControlTask controlTask = lstControlTasks.get(iPosition);
                    if (controlTask.getTaskId() > newControlTask.getTaskId())
                    {
                        break;
                    }
                }
                
                lstControlTasks.add(iPosition, newControlTask);
            }
            
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * ��ó̿�����
     * @param strLogicAddress   �豸�߼���ַ
     * @param iTaskId           ����ID
     * @return  �̿�����
     */
    @Override
    public CControlTask getControlTask(String strLogicAddress, int iTaskId)
    {
        // ����������
        if ((StringUtil.IsNullOrEmpty(strLogicAddress)) || (iTaskId < 0))
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
        IDevice device = powerSystem.GetDeviceByLogicAddress(strLogicAddress);
        if ((null == device) || !(device instanceof CDcuDevice))
        {
            return null;
        }
        CDcuDevice cpcDevice = (CDcuDevice)device;
        
        // ��������б�
        List<CControlTask> lstControlTasks = cpcDevice.getContorlTaskList();
        if (null == lstControlTasks)
        {
            return null;
        }
        
        // ���ָ��Id�Ŀ�������
        for (CControlTask task : lstControlTasks)
        {
            if (task.getTaskId() == iTaskId)
            {
                return task;
            }
        }
        
        return null;
    }

    /**
     * ɾ����������
     * @param logicAddress      �豸�߼���ַ
     * @param lstTaskIds        ��ɾ��������ID�б�
     * @return  �ɹ�����true
     */
    @Override
    public boolean deleteControlTasks(String logicAddress, List<Integer> lstTaskIds)
    {
        // ����������
        if (StringUtil.IsNullOrEmpty(logicAddress)
                || (null == lstTaskIds)
                || (lstTaskIds.size() == 0))
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
        
        // ɾ����������
        CIoctlResult result = new CIoctlResult();
        if (cpcDevice.Ioctl(enumDataItem.DeleteControlTasks.toString(), lstTaskIds, result)
                && (result.getErrorCode() == enumErrorCode.Success))
        {
            // �������б���ɾ��ָ��������ʹҳ���ܼ�ʱ�������µ�������Ϣ
            List<CControlTask> lstControlTasks = cpcDevice.getContorlTaskList();
            if (null != lstControlTasks)
            {
                // ������ɾ��������ID�б�
                for (Integer taskId : lstTaskIds)
                {
                    // �������б���ɾ��ָ��ID������
                    for(int i = 0; i < lstControlTasks.size(); i++)
                    {
                        if (lstControlTasks.get(i).getTaskId() == taskId)
                        {
                            lstControlTasks.remove(i);
                            break;
                        }
                    }
                }
            }
            
            return true;
        }

        return false;
    }
}
