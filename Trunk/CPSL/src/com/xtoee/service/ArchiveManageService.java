package com.xtoee.service;

import java.util.List;

import com.xtoee.devices.dcu.deviceNode.CModuleBase;

/**
 * ��������ӿ�
 * @author zgm
 *
 */
public interface ArchiveManageService
{
    /**
     * ��õ����б�
     * @param logicAddress  �豸�߼���ַ
     * @return  �����б�
     */
    public List<CModuleBase> getArchiveList(String logicAddress);
    
    /**
     * ��ӵ����б�
     * @param logicAddress  �豸�߼���ַ
     * @param lstModules    �����б�
     * @return  �ɹ�����true
     */
    public boolean addArchives(String logicAddress, List<CModuleBase> lstModules);
    
    /**
     * ɾ������
     * @param logicAddress  �豸�߼���ַ
     * @param lstArchiveIds ��ɾ���ĵ���Id�б�
     * @return  �ɹ�����true
     */
    public boolean deleteArchives(String logicAddress, List<Integer> lstArchiveIds);
}
