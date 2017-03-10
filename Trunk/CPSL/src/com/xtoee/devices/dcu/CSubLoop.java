package com.xtoee.devices.dcu;

/**
 * �ӻ�·��
 * @author zgm
 *
 */
public class CSubLoop
{
	private double					m_dVoltage;					// ��ѹ
	private double 					m_dCurrent;					// ����
	private boolean					m_bIsConnect;				// �Ƿ���ͨ
	
	
	/**
	 * ��õ�ѹֵ
	 * @return	��ѹֵ
	 */
	public double getVoltage()
	{
		return m_dVoltage;
	}

	/**
	 * ��õ���ֵ
	 * @return	����ֵ
	 */
	public double getCurrent()
	{
		return m_dCurrent;
	}
	
	/**
	 * ��ù���
	 * @return	����
	 */
	public double getPower()
	{
		return m_dVoltage * m_dCurrent;
	}

	/**
	 * ���ͨ��״̬
	 * @return	�Ƿ���ͨ
	 */
	public boolean IsConnect()
	{
		return m_bIsConnect;
	}
	
	/**
	 * ����ͨ��״̬
	 * @param bIsConnect   �Ƿ���ͨ
	 */
	public void SetConnect(boolean bIsConnect)
    {
        m_bIsConnect = bIsConnect;
    }
}
