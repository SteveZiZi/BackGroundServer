package com.xtoee.devices.dcu;

import com.xtoee.util.NumberUtil;

/**
 * 整流器类
 * 
 * @author zgm
 *
 */
public class CRectifier
{
    private int                     m_nID;                      // 整流器号
    private double                  m_dVoltage;                 // 电压
    private double                  m_dCurrent;                 // 电流
    private double                  m_dTemperature;             // 温度
    private int                     m_iRotationRate;            // 转速
    private byte                    m_ucAlarmValue;             // 模块告警量
    private byte                    m_ucProtectionType;         // 模块保护类型

    
    /**
     * 构造函数
     * 
     * @param nId
     *            整流器号
     */
    public CRectifier(int nId)
    {
        m_nID = nId;
    }

    /**
     * 获得整流器号
     * 
     * @return 整流器号
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
        return m_dVoltage;
    }

    /**
     * 获得电流值
     * 
     * @return 电流值
     */
    public double getCurrent()
    {
        return m_dCurrent;
    }

    /**
     * 获得功率
     * 
     * @return 功率
     */
    public double getPower()
    {
        return m_dVoltage * m_dCurrent;
    }

    /**
     * 获得温度值
     * 
     * @return 温度值
     */
    public double getTemperature()
    {
        return m_dTemperature;
    }

    /**
     * 获得转速
     * 
     * @return 转速
     */
    public int getRotationRate()
    {
        return m_iRotationRate;
    }

    /**
     * 获得模块告警值
     * 
     * @return 模块告警值
     */
    public byte getAlarmValue()
    {
        return m_ucAlarmValue;
    }

    /**
     * 获得模块保护类型
     * 
     * @return 模块保护类型
     */
    public byte getProtectionType()
    {
        return m_ucProtectionType;
    }

    /**
     * 获得整流器状态
     * 
     * @return 整流器状态
     */
    public String getStatus()
    {
        String strRet = "正常";
        StringBuffer sb = new StringBuffer();

        
        // 模块告警量
        sb.append(((m_ucAlarmValue & 0x01) == 0) ? "" : "限流标志：产生，");
        sb.append(((m_ucAlarmValue & 0x02) == 0) ? "" : "模块故障：产生，");
        sb.append(((m_ucAlarmValue & 0x04) == 0) ? "" : "模块开关机：关机，");
        sb.append(((m_ucAlarmValue & 0x08) == 0) ? "" : "内部均流故障：产生，");
        sb.append(((m_ucAlarmValue & 0x10) == 0) ? "" : "模块风扇故障：产生，");
        sb.append(((m_ucAlarmValue & 0x20) == 0) ? "" : "交流故障；产生，");
        sb.append(((m_ucAlarmValue & 0x40) == 0) ? "" : "模块保护：产生，");
        sb.append(((m_ucAlarmValue & 0x80) == 0) ? "" : "通讯故障：告警，");

        // 模块保护类型
        sb.append(((m_ucProtectionType & 0x01) == 0) ? "" : "过流、短路保护：产生，");
        sb.append(((m_ucProtectionType & 0x02) == 0) ? "" : "过流保护：产生，");
        sb.append(((m_ucProtectionType & 0x04) == 0) ? "" : "输出欠压：产生，");
        sb.append(((m_ucProtectionType & 0x08) == 0) ? "" : "输出过压：产生，");
        sb.append(((m_ucProtectionType & 0x10) == 0) ? "" : "母线不平衡：产生，");
        sb.append(((m_ucProtectionType & 0x20) == 0) ? "" : "输入过压：产生，");
        sb.append(((m_ucProtectionType & 0x40) == 0) ? "" : "输入欠压：产生，");
        sb.append(((m_ucProtectionType & 0x80) == 0) ? "" : "过温：产生，");

        // 移除末尾的，号
        if (sb.length() > 0)
        {
            strRet = sb.substring(0, sb.length() - 1);
        }

        return strRet;
    }

    /**
     * 解析状态信息
     * 
     * @param ucBuffer
     *            存放状态报文的数组
     * @param iStartIdx
     *            状态报文的起始索引
     * @return 解析成功返回true
     */
    public boolean parseFrame(byte[] ucBuffer, int iStartIdx)
    {
        // 检查输入参数
        if ((null == ucBuffer) || (iStartIdx < 0) || (iStartIdx + 12 > ucBuffer.length))
        {
            return false;
        }

        // 检查整流器ID号
        if (m_nID != NumberUtil.byte2ToUnsignedShort(ucBuffer, iStartIdx))
        {
            return false;
        }
        iStartIdx += 2;

        // 回路号
        @SuppressWarnings("unused")
        int iValue = ucBuffer[iStartIdx++];

        // 电压
        m_dVoltage = NumberUtil.BcdByte2ToShort(ucBuffer, iStartIdx) / 10.0;
        iStartIdx += 2;

        // 电流
        m_dCurrent = NumberUtil.BcdByte2ToShort(ucBuffer, iStartIdx) / 10.0;
        iStartIdx += 2;

        // 温度
        m_dTemperature = ucBuffer[iStartIdx++];

        // 转速
        m_iRotationRate = NumberUtil.byte2ToUnsignedShort(ucBuffer, iStartIdx);
        iStartIdx += 2;

        // 模块告警量
        m_ucAlarmValue = ucBuffer[iStartIdx++];

        // 模块保护类型
        m_ucProtectionType = ucBuffer[iStartIdx++];

        return true;
    }
}
