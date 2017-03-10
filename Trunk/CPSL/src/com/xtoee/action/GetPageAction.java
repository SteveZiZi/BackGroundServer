package com.xtoee.action;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 页面控制器
 * @author zgm
 *
 */
public class GetPageAction extends ActionSupport
{
    private static final long       serialVersionUID = 1L;
    private String                  logicAddress;               // 设备逻辑地址
    private String                  deviceID;                   // 设备ID号
    
    
    /**
     * 获得设备逻辑地址
     * @return  设备逻辑地址
     */
    public String getLogicAddress()
    {
        return logicAddress;
    }
    
    /**
     * 设置设备逻辑地址
     * @param logicAddress  设备逻辑地址
     */
    public void setLogicAddress(String logicAddress)
    {
        this.logicAddress = logicAddress;
    }

    /**
     * 获得设备ID
     * @return  设备ID
     */
    public String getDeviceID()
    {
        return deviceID;
    }

    /**
     * 设置设备ID
     * @param deviceID  设备ID
     */
    public void setDeviceID(String deviceID)
    {
        this.deviceID = deviceID;
    }
    
    @Override
    public String execute() throws Exception
    {
        return SUCCESS;
    }
}
