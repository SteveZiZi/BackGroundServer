package com.xtoee.devices.dcu;

import java.util.LinkedList;
import java.util.List;

/**
 * 回路类
 * 
 * @author zgm
 *
 */
public class CClosedCircuit
{
    private int                     m_nID;                      // 回路号
    private List<CRectifier>        m_lstRectifiers;            // 整流器列表
    private List<CRelay>            m_lstRelays;                // 继电器列表

    
    /**
     * 构造函数
     * 
     * @param nID
     *            回路ID
     */
    public CClosedCircuit(int nID)
    {
        m_nID = nID;
        m_lstRectifiers = new LinkedList<CRectifier>();
        m_lstRelays = new LinkedList<CRelay>();
    }

    /**
     * 获得回路号
     * 
     * @return 回路号
     */
    public int getID()
    {
        return m_nID;
    }

    /**
     * 获得电压值
     * 
     * @return 电压值
     */
    public double getVoltage()
    {
        double dMax = Double.MIN_VALUE;
        double dTemp = Double.MIN_VALUE;

        // 循环遍历每一个整流器
        for (CRectifier rectifier : m_lstRectifiers)
        {
            // 获得所有整流器中的最高电压值
            dTemp = rectifier.getVoltage();
            if (dTemp > dMax)
            {
                dMax = dTemp;
            }
        }

        return dMax;
    }

    /**
     * 获得电流值
     * 
     * @return 电流值
     */
    public double getCurrent()
    {
        double dTotal = 0;

        // 循环遍历每一个整流器
        for (CRectifier rectifier : m_lstRectifiers)
        {
            // 累加电流值
            dTotal += rectifier.getCurrent();
        }

        return dTotal;
    }

    /**
     * 获得功率值
     * 
     * @return 功率
     */
    public double getPower()
    {
        return getVoltage() * getCurrent();
    }

    /**
     * 获得温度值
     * 
     * @return 温度
     */
    public double getTemperature()
    {
        double dMax = Double.NEGATIVE_INFINITY;
        double dTemp = Double.NEGATIVE_INFINITY;

        // 循环遍历每一个回路
        for (CRectifier rectifier : m_lstRectifiers)
        {
            // 获得所有整流器中的最高温度值
            dTemp = rectifier.getTemperature();
            if (dTemp > dMax)
            {
                dMax = dTemp;
            }
        }

        return dMax;
    }

    /**
     * 获得回路状态
     * 
     * @return 回路状态
     */
    public String getStatus()
    {
        // 循环遍历每一个回路
        for (CRectifier rectifier : m_lstRectifiers)
        {
            if ((rectifier.getAlarmValue() != 0) || (rectifier.getProtectionType() != 0))
            {
                return "异常";
            }
        }
        
        return "正常";
    }

    /**
     * 获得整流器列表
     * 
     * @return 整流器列表
     */
    public List<CRectifier> getRectifierList()
    {
        return m_lstRectifiers;
    }
    
    /**
     * 获得指定ID的整流器对象
     * 
     * @param nId
     *            整流器ID
     * @return 整流器对象
     */
    public CRectifier getRectifierById(int nId)
    {
        // 循环遍历每一个整流器
        for (CRectifier rectifier : m_lstRectifiers)
        {
            // 检查ID是否匹配
            if (rectifier.getID() == nId)
            {
                return rectifier;
            }
        }

        return null;
    }

    /**
     * 添加整流器对象到容器中
     * 
     * @param rectifier
     *            整流器对象
     * @return 添加成功返回true
     */
    public boolean AddRetifier(CRectifier rectifier)
    {
        // 检查整流器对象是否已经存在
        if ((null == rectifier)
                || (null != getRectifierById(rectifier.getID())))
        {
            return false;
        }

        // 添加整流器对象到容器中
        m_lstRectifiers.add(rectifier);
        return true;
    }

    /**
     * 删除所有的整流器对象
     */
    public void DeleteAllRetifiers()
    {
        m_lstRectifiers.clear();
    }
    
    /**
     * 获得继电器列表
     * @return  继电器列表
     */
    public List<CRelay> getRelayList()
    {
        return m_lstRelays;
    }
    
    /**
     * 获得指定ID的继电器对象
     * 
     * @param nId
     *            继电器ID
     * @return 继电器对象
     */
    public CRelay getRelayById(int nId)
    {
        // 循环遍历每一个继电器
        for (CRelay relay : m_lstRelays)
        {
            // 检查ID是否匹配
            if (relay.getID() == nId)
            {
                return relay;
            }
        }

        return null;
    }
    
    /**
     * 添加继电器对象到容器中
     * 
     * @param relay
     *            继电器对象
     * @return 添加成功返回true
     */
    public boolean AddRelay(CRelay relay)
    {
        // 检查继电器对象是否已经存在
        if ((null == relay)
                || (null != getRelayById(relay.getID())))
        {
            return false;
        }

        // 添加继电器对象到容器中
        m_lstRelays.add(relay);
        return true;
    }
    
    /**
     * 删除所有的继电器对象
     */
    public void DeleteAllRelay()
    {
        m_lstRelays.clear();
    }
}
