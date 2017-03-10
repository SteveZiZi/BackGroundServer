package com.xtoee.service;

import java.util.Date;
import java.util.List;

import com.xtoee.devices.dcu.CControlTask;

/**
 * �������ӿ�
 * @author zgm
 *
 */
public interface TaskManageService
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
    public boolean addControlTask(String strLogicAddress, boolean bTaskEnable, int iTaskId, int iTaskPriority, Date dtStart, Date dtEnd, String[] strCircuits, long iCycleMode, String[] strTaskItemStartupTimes, String[] strtaskItemActions);
    
    /**
     * ��ó̿�����
     * @param strLogicAddress   �豸�߼���ַ
     * @param iTaskId           ����ID
     * @return  �̿�����
     */
    public CControlTask getControlTask(String strLogicAddress, int iTaskId);
    
    /**
     * ɾ����������
     * @param logicAddress      �豸�߼���ַ
     * @param lstTaskIds        ��ɾ��������ID�б�
     * @return  �ɹ�����true
     */
    public boolean deleteControlTasks(String logicAddress, List<Integer> lstTaskIds);
}
