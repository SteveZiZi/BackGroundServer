package com.xtoee.devices;

import java.util.concurrent.ScheduledFuture;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 时频设备接口
 * @author zgm
 *
 */
public interface IDevice extends Runnable
{
    /**
     * 获得时频设备的类型
     * @return  设备类型
     */
    public enumDeviceType GetDeviceType();
    
    /**
     * 设置时频设备的类型
     * @param eType 设备类型
     */
    public void SetDeviceType(enumDeviceType eType);
    
    /**
     * 获得设备ID
     * @return  设备ID
     */
    public int getID();
    
    /**
     * 设置设备ID
     * @param nID 设备ID
     */
    public void setID(int nID);
    
    /**
     * 获得设备名称
     * @return  设备名称
     */
    public String getDeviceName();
    
    /**
     * 设置设备名称
     * @param strName   设备名称
     */
    public void SetDeviceName(String strName);
    
    /**
     * 获得设备IP地址
     * @return  设备IP地址
     */
    public String GetDeviceIP();
    
    /**
     * 设置设备IP地址
     * @param strIP 设备IP地址
     */
    public void SetDeviceIP(String strIP);
    
    /**
     * 获得设备逻辑地址
     * @return  逻辑地址
     */
    public String getLogicAddress();

    /**
     * 设置设备逻辑地址
     * @param strLogicAddress   逻辑地址
     */
    public void setLogicAddress(String strLogicAddress);
    
    /**
     * 获得设备状态
     * @return  设备状态
     */
    public enumDeviceState GetDeviceState();
    
    /**
     * 设置设备状态
     * @param eNewState 设备状态
     */
    public void SetDeviceState(enumDeviceState eNewState);
    
    /**
     * 获得描述字符串
     * @return  描述字符串
     */
    public String toString();
    
    /**
     * 加载配置
     * @param xnDevice  设备信息描述节点
     * @return  接在成功返回true
     */
    public boolean LoadConfig(Element xnDevice);
    
    /**
     * 保存配置
     * @param xmlDoc    XML文档对象
     * @param xnDevice  XML设备节点对象
     * @return  保存成功返回true
     */
    public boolean SaveConfig(Document xmlDoc, Element xnDevice);
    
    /**
     * 获得设备状态轮询任务
     * @return  状态轮询任务
     */
    public ScheduledFuture<?> GetScheduledFuture();
    
    /**
     * 设置设备状态轮询任务
     * @param sf    状态轮询任务
     */
    public void SetScheduledFuture(ScheduledFuture<?> sf);
}
