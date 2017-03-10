package com.xtoee.devices.dcu;

import java.util.LinkedList;
import java.util.List;

import com.xtoee.util.NumberUtil;

/**
 * 继电器
 * @author zgm
 *
 */
public class CRelay
{
    private int                     m_nID;                      // 继电器号
    private List<CSubLoop>          m_lstSubLoop;               // 子回路
    
    
    /**
     * 构造函数
     * @param nId   继电器号
     */
    public CRelay(int nId)
    {
        m_nID = nId;
        m_lstSubLoop = new LinkedList<CSubLoop>();
        
        // 初始化子回路
        for(int i = 0; i < 8; i++)
        {
            m_lstSubLoop.add(new CSubLoop());
        }
    }
    
    /**
     * 获得继电器号
     * @return 继电器号
     */
    public int getID()
    {
        return m_nID;
    }
    
    /**
     * 获得子回路列表
     * @return  子回路列表
     */
    public List<CSubLoop> getSubLoopList()
    {
        return m_lstSubLoop;
    }
    
    /**
     * 解析状态信息
     * @param ucBuffer  存放状态报文的数组
     * @param iStartIdx 状态报文的起始索引
     * @return  解析成功返回true
     */
    public boolean parseFrame(byte[] ucBuffer, int iStartIdx)
    {
        int             iValue;
        
        
        // 检查输入参数
        if ((null == ucBuffer) || (iStartIdx < 0) || (iStartIdx + 10 > ucBuffer.length))
        {
            return false;
        }
        
        // 检查继电器ID号
        if (m_nID != ucBuffer[iStartIdx++])
        {
            return false;
        }
        
        // 类型
        iValue = ucBuffer[iStartIdx++];
        
        // 子回路状态
        iValue = NumberUtil.byte4ToInt(ucBuffer, iStartIdx);
        for (int i = 0; i < 8; i++)
        {
            boolean bIsConn = ((iValue & (1 << (i + 13))) != 0 );
            m_lstSubLoop.get(i).SetConnect(bIsConn);
        }
        
        return true;
    }
}
