package com.xtoee.devices.dcu;

/**
 * 子回路类
 * @author zgm
 *
 */
public class CSubLoop
{
	private double					m_dVoltage;					// 电压
	private double 					m_dCurrent;					// 电流
	private boolean					m_bIsConnect;				// 是否连通
	
	
	/**
	 * 获得电压值
	 * @return	电压值
	 */
	public double getVoltage()
	{
		return m_dVoltage;
	}

	/**
	 * 获得电流值
	 * @return	电流值
	 */
	public double getCurrent()
	{
		return m_dCurrent;
	}
	
	/**
	 * 获得功率
	 * @return	功率
	 */
	public double getPower()
	{
		return m_dVoltage * m_dCurrent;
	}

	/**
	 * 获得通断状态
	 * @return	是否连通
	 */
	public boolean IsConnect()
	{
		return m_bIsConnect;
	}
	
	/**
	 * 设置通断状态
	 * @param bIsConnect   是否连通
	 */
	public void SetConnect(boolean bIsConnect)
    {
        m_bIsConnect = bIsConnect;
    }
}
