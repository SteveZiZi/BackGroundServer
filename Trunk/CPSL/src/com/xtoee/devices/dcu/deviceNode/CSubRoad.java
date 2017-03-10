package com.xtoee.devices.dcu.deviceNode;

import org.omg.CORBA.IntHolder;

import com.xtoee.devices.dcu.enumSpecificFunction;
import com.xtoee.util.NumberUtil;

/**
 * ģ������豸
 * @author zgm
 *
 */
public class CSubRoad
{
    private int                     m_iSubRoadId;               // �豸��·�ţ�1~32��
    private enumSpecificFunction    m_eSpecialFun;              // ���⹦�ܶ���
    private int                     m_iRelateModuleId;          // ��·�������豸�ڵ��ţ�1~16��
    private int                     m_iRelateModuleSubRoad;     // ��·�������豸��·��
    
    
    /**
     * ���캯��
     */
    public CSubRoad()
    {
        super();
    }

    /**
     * ���캯��
     * @param subRoadId             �豸��·�ţ�1~32��
     * @param specialFun            ���⹦�ܶ���
     * @param relateModuleId        ��·�������豸�ڵ��ţ�1~16��
     * @param relateModuleSubRoad   ��·�������豸��·��
     */
    public CSubRoad(int subRoadId, enumSpecificFunction specialFun, int relateModuleId, int relateModuleSubRoad)
    {
        super();
        m_iSubRoadId = subRoadId;
        m_eSpecialFun = specialFun;
        m_iRelateModuleId = relateModuleId;
        m_iRelateModuleSubRoad = relateModuleSubRoad;
    }

    /**
     * ����豸��·�ţ�1~32��
     * @return  �豸��·�ţ�1~32��
     */
    public int getSubRoadId()
    {
        return m_iSubRoadId;
    }
    
    /**
     * �����豸��·�ţ�1~32��
     * @param subRoadId �豸��·�ţ�1~32��
     */
    public void setSubRoadId(int subRoadId)
    {
        m_iSubRoadId = subRoadId;
    }
    
    /**
     * ������⹦�ܶ���
     * @return  ���⹦�ܶ���
     */
    public enumSpecificFunction getSpecialFun()
    {
        return m_eSpecialFun;
    }
    
    /**
     * �������⹦�ܶ���
     * @param specialFun    ���⹦�ܶ���
     */
    public void setSpecialFun(enumSpecificFunction specialFun)
    {
        m_eSpecialFun = specialFun;
    }
    
    /**
     * �����·�������豸�ڵ��ţ�1~16��
     * @return  ��·�������豸�ڵ��ţ�1~16��
     */
    public int getRelateModuleId()
    {
        return m_iRelateModuleId;
    }
    
    /**
     * ������·�������豸�ڵ��ţ�1~16��
     * @param relateModuleId    ��·�������豸�ڵ��ţ�1~16��
     */
    public void setRelateModuleId(int relateModuleId)
    {
        m_iRelateModuleId = relateModuleId;
    }
    
    /**
     * �����·�������豸��·��
     * @return  ��·�������豸��·��
     */
    public int getRelateModuleSubRoad()
    {
        return m_iRelateModuleSubRoad;
    }
    
    /**
     * ������·�������豸��·��
     * @param relateModuleSubRoad   ��·�������豸��·��
     */
    public void setRelateModuleSubRoad(int relateModuleSubRoad)
    {
        m_iRelateModuleSubRoad = relateModuleSubRoad;
    }
    
    /**
     * ����
     * @param ucBuffer      ��ű������Ļ�����
     * @param iOffset       ��ű���������ʼ����
     * @param iEncodeBytes  �������ĳ���
     * @return  �ɹ�����true
     */
    public boolean Encode(byte[] ucBuffer, int iOffset, IntHolder iEncodeBytes)
    {
        int             iOldOffset = iOffset;
        
        
        // �豸��·�ţ�1~32��
        ucBuffer[iOffset++] = (byte)m_iSubRoadId;
        
        // ���⹦�ܶ���
        ucBuffer[iOffset++] = (byte)m_eSpecialFun.getValue();
        
        // ��·�������豸�ڵ��ţ�1~16��
        ucBuffer[iOffset++] = (byte)m_iRelateModuleId;
        
        // ��·�������豸��·��
        System.arraycopy(NumberUtil.intToByte4(m_iRelateModuleSubRoad), 0, ucBuffer, iOffset, 4);
        iOffset += 4;
        
        // ���ر�����ֽ���
        if (null != iEncodeBytes)
        {
            iEncodeBytes.value = iOffset - iOldOffset;
        }
        
        return true;
    }
    
    /**
     * ������·����
     * @param ucBuffer      �����·���ĵ�����
     * @param iOffset       ��·���ĵ���ʼ����
     * @param iParseBytes   �������ֽ���
     * @return  �����ɹ�����true
     */
    public boolean parseFrame(byte[] ucBuffer, int iOffset, IntHolder iParseBytes)
    {
        int             iOldOffset = iOffset;
        
        
        // �豸��·�ţ�1~32��
        m_iSubRoadId = ucBuffer[iOffset++];
        
        // ���⹦�ܶ���
        m_eSpecialFun = enumSpecificFunction.getEnumByVaule(ucBuffer[iOffset++]);
        
        // ��·�������豸�ڵ��ţ�1~16��
        m_iRelateModuleId = ucBuffer[iOffset++];
        
        // ��·�������豸��·��
        m_iRelateModuleSubRoad = NumberUtil.byte4ToInt(ucBuffer, iOffset);
        iOffset += 4;
        
        // ���ؽ������ֽ���
        if (null != iParseBytes)
        {
            iParseBytes.value = iOffset - iOldOffset;
        }
        
        return true;
    }
}
