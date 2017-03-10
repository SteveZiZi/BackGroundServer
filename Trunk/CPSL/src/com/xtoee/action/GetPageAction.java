package com.xtoee.action;

import com.opensymphony.xwork2.ActionSupport;

/**
 * ҳ�������
 * @author zgm
 *
 */
public class GetPageAction extends ActionSupport
{
    private static final long       serialVersionUID = 1L;
    private String                  logicAddress;               // �豸�߼���ַ
    private String                  deviceID;                   // �豸ID��
    
    
    /**
     * ����豸�߼���ַ
     * @return  �豸�߼���ַ
     */
    public String getLogicAddress()
    {
        return logicAddress;
    }
    
    /**
     * �����豸�߼���ַ
     * @param logicAddress  �豸�߼���ַ
     */
    public void setLogicAddress(String logicAddress)
    {
        this.logicAddress = logicAddress;
    }

    /**
     * ����豸ID
     * @return  �豸ID
     */
    public String getDeviceID()
    {
        return deviceID;
    }

    /**
     * �����豸ID
     * @param deviceID  �豸ID
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
