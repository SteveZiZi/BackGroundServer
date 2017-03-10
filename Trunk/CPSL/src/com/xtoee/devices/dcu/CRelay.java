package com.xtoee.devices.dcu;

import java.util.LinkedList;
import java.util.List;

import com.xtoee.util.NumberUtil;

/**
 * �̵���
 * @author zgm
 *
 */
public class CRelay
{
    private int                     m_nID;                      // �̵�����
    private List<CSubLoop>          m_lstSubLoop;               // �ӻ�·
    
    
    /**
     * ���캯��
     * @param nId   �̵�����
     */
    public CRelay(int nId)
    {
        m_nID = nId;
        m_lstSubLoop = new LinkedList<CSubLoop>();
        
        // ��ʼ���ӻ�·
        for(int i = 0; i < 8; i++)
        {
            m_lstSubLoop.add(new CSubLoop());
        }
    }
    
    /**
     * ��ü̵�����
     * @return �̵�����
     */
    public int getID()
    {
        return m_nID;
    }
    
    /**
     * ����ӻ�·�б�
     * @return  �ӻ�·�б�
     */
    public List<CSubLoop> getSubLoopList()
    {
        return m_lstSubLoop;
    }
    
    /**
     * ����״̬��Ϣ
     * @param ucBuffer  ���״̬���ĵ�����
     * @param iStartIdx ״̬���ĵ���ʼ����
     * @return  �����ɹ�����true
     */
    public boolean parseFrame(byte[] ucBuffer, int iStartIdx)
    {
        int             iValue;
        
        
        // ����������
        if ((null == ucBuffer) || (iStartIdx < 0) || (iStartIdx + 10 > ucBuffer.length))
        {
            return false;
        }
        
        // ���̵���ID��
        if (m_nID != ucBuffer[iStartIdx++])
        {
            return false;
        }
        
        // ����
        iValue = ucBuffer[iStartIdx++];
        
        // �ӻ�·״̬
        iValue = NumberUtil.byte4ToInt(ucBuffer, iStartIdx);
        for (int i = 0; i < 8; i++)
        {
            boolean bIsConn = ((iValue & (1 << (i + 13))) != 0 );
            m_lstSubLoop.get(i).SetConnect(bIsConn);
        }
        
        return true;
    }
}
