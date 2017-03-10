package com.xtoee.service;

import java.util.List;

import com.xtoee.devices.dcu.deviceNode.CModuleBase;

/**
 * 档案管理接口
 * @author zgm
 *
 */
public interface ArchiveManageService
{
    /**
     * 获得档案列表
     * @param logicAddress  设备逻辑地址
     * @return  档案列表
     */
    public List<CModuleBase> getArchiveList(String logicAddress);
    
    /**
     * 添加档案列表
     * @param logicAddress  设备逻辑地址
     * @param lstModules    档案列表
     * @return  成功返回true
     */
    public boolean addArchives(String logicAddress, List<CModuleBase> lstModules);
    
    /**
     * 删除档案
     * @param logicAddress  设备逻辑地址
     * @param lstArchiveIds 待删除的档案Id列表
     * @return  成功返回true
     */
    public boolean deleteArchives(String logicAddress, List<Integer> lstArchiveIds);
}
