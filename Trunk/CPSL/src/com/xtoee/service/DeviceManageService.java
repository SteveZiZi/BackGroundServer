package com.xtoee.service;

import java.util.List;

import com.xtoee.devices.IDevice;

/**
 * 设备管理接口
 * @author zgm
 *
 */
public interface DeviceManageService
{
    /**
     * 获得设备列表
     * @return  设备列表
     */
    public List<IDevice> getDeviceList();
    
    /**
     * 根据设备逻辑地址，获得设备对象
     * @param strLogicAddress   设备逻辑地址
     * @return  设备对象
     */
    public IDevice GetDeviceByLogicAddress(String strLogicAddress);
    
    /**
     * 修改设备指定回路的亮度
     * @param strLogicAddress       设备逻辑地址
     * @param closedCircuitName     回路名称  
     * @param closedCircuitLight    回路亮度
     * @return  成功返回true
     */
    public boolean changeClosedCircuitLight(String strLogicAddress, String closedCircuitName, String closedCircuitLight);
    
    /**
     * 回路开关
     * @param strLogicAddress       设备逻辑地址
     * @param closedCircuitName     回路名称
     * @param closedCircuitSwitch   回路开关
     * @return  成功返回true
     */
    public boolean closedCircuitSwitch(String strLogicAddress, String closedCircuitName, String closedCircuitSwitch);
}
