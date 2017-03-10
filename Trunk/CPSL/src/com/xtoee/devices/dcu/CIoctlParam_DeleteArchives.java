package com.xtoee.devices.dcu;

import java.util.ArrayList;
import java.util.List;

/**
 * 删除设备档案命令的参数
 * @author zgm
 *
 */
public class CIoctlParam_DeleteArchives
{
    public static final int         DT_DeleteAll    = 0;        // 删除所有
    public static final int         DT_DeletePart   = 1;        // 删除部分
    
    private int                     m_iDeleteType;              // 删除操作类型（删除所有、删除部分）
    private List<Integer>           m_lstDeviceIds;             // 待删除的设备号列表
    
    
    /**
     * 构造函数
     */
    public CIoctlParam_DeleteArchives()
    {
        m_iDeleteType   = DT_DeletePart;
        m_lstDeviceIds  = new ArrayList<Integer>();
    }
    
    /**
     * 构造函数
     * @param m_iDeleteType 删除操作类型
     */
    public CIoctlParam_DeleteArchives(int iDeleteType)
    {
        m_iDeleteType = iDeleteType;
    }

    /**
     * 获得删除操作类型
     * @return  删除操作类型（删除所有、删除部分）
     */
    public int getDeleteType()
    {
        return m_iDeleteType;
    }
    
    /**
     * 设置删除操作类型（删除所有、删除部分）
     * @param deleteType    删除操作类型（删除所有、删除部分）
     */
    public void setDeleteType(int deleteType)
    {
        m_iDeleteType = deleteType;
    }
    
    /**
     * 获得待删除的设备号列表
     * @return  待删除的设备号列表
     */
    public List<Integer> getDeviceIdList()
    {
        return m_lstDeviceIds;
    }
    
    /**
     * 设置待删除的设备号列表
     * @param deviceIdList  待删除的设备号列表
     */
    public void setDeviceIdList(List<Integer> deviceIdList)
    {
        m_lstDeviceIds = deviceIdList;
    }
}
