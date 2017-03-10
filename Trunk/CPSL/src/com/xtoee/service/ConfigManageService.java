package com.xtoee.service;

import java.util.Date;

/**
 * 配置管理接口
 * @author zgm
 *
 */
public interface ConfigManageService
{
    /**
     * 获得管理员手机号
     * @param logicAddress  设备逻辑地址
     * @return  手机号码
     */
    public String getAdminPhone(String logicAddress);
    
    /**
     * 设置管理员手机号
     * @param logicAddress  设备逻辑地址
     * @param adminPhone    管理员手机号
     * @return  成功返回true
     */
    public boolean setAdminPhone(String logicAddress, String adminPhone);
    
    /**
     * 设置设备时间
     * @param logicAddress  设备逻辑地址
     * @param newDatetime   设备时间
     * @return  成功返回true
     */
    public boolean setDeviceTime(String logicAddress, Date newDatetime);
}
