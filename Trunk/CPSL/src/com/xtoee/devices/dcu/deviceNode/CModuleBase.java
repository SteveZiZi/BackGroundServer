package com.xtoee.devices.dcu.deviceNode;

import java.util.ArrayList;
import java.util.List;

import org.omg.CORBA.IntHolder;

import com.xtoee.util.NumberUtil;

/**
 * 抽象设备节点
 * @author zgm
 *
 */
public class CModuleBase
{
    /**
     * 功能类型
     */
    public static final int         FT_RESERVE      = 0;        // 保留
    public static final int         FT_CONTROL      = 1;        // 控制类型
    public static final int         FT_DISPLAY      = 2;        // 面板显示类型
    
    /**
     * 设备类型字
     */
    public static final int         MT_DCU          = 0;        // DCU主模块
    public static final int         MT_Aircon       = 1;        // 空调模块
    public static final int         MT_8RoadRelay   = 2;        // 8路继电器模块
    public static final int         MT_4RoadLight   = 3;        // 4路调光板
    public static final int         MT_2RoadRelay   = 4;        // 2路继电器模块
    public static final int         MT_3RoadLed     = 5;        // 3路LED调光版
    public static final int         MT_DoorPlate    = 6;        // 门牌显示模块
    public static final int         MT_RFID         = 7;        // RFID插卡取电模块
    public static final int         MT_20Keys       = 8;        // 20按键模块
    public static final int         MT_Rectifier    = 9;        // 整流器
    public static final int         MT_LightSensor  = 10;       // 光感器
    
    /**
     * 通讯参数
     */
    public static final int         CP_1200         = 1;        // 1200
    public static final int         CP_2400         = 2;        // 2400
    public static final int         CP_4800         = 3;        // 4800
    public static final int         CP_9600         = 4;        // 9600
    public static final int         CP_19200        = 5;        // 19200
    
    private int                     m_iModuleId;                // 设备节点编号
    private String                  m_strModuleAddress;         // 设备节点地址
    private int                     m_iClosedCircuitId;         // 回路号
    private int                     m_iFunctionType;            // 功能类型
    private int                     m_iModuleType;              // 设备类型字
    private int                     m_iCommParam;               // 通讯参数
    private List<CSubRoad>          m_lstRelateDevices;         // 关联设备
    
    
    /**
     * 构造函数
     */
    public CModuleBase()
    {
        super();
        m_lstRelateDevices = new ArrayList<CSubRoad>();
    }

    /**
     * 构造函数
     * @param moduleId          设备节点编号
     * @param moduleAddress     设备节点地址
     * @param closedCircuitId   回路号
     * @param functionType      功能类型
     * @param moduleType        设备类型字
     * @param commParam         通讯参数
     */
    public CModuleBase(int moduleId, String moduleAddress,
            int closedCircuitId, int functionType, int moduleType, int commParam)
    {
        super();
        m_iModuleId = moduleId;
        m_strModuleAddress = moduleAddress;
        m_iClosedCircuitId = closedCircuitId;
        m_iFunctionType = functionType;
        m_iModuleType = moduleType;
        m_iCommParam = commParam;
        m_lstRelateDevices = new ArrayList<CSubRoad>();
    }

    /**
     * 获得设备节点编号
     * @return  设备节点编号
     */
    public int getModuleId()
    {
        return m_iModuleId;
    }
    
    /**
     * 设置设备节点编号
     * @param moduleId  设备节点编号
     */
    public void setModuleId(int moduleId)
    {
        m_iModuleId = moduleId;
    }
    
    /**
     * 获得设备节点地址
     * @return  设备节点地址
     */
    public String getModuleAddress()
    {
        return m_strModuleAddress;
    }
    
    /**
     * 设置设备节点地址
     * @param moduleAddress 设备节点地址
     */
    public void setModuleAddress(String moduleAddress)
    {
        m_strModuleAddress = moduleAddress;
    }
    
    /**
     * 获得回路号
     * @return  回路号
     */
    public int getClosedCircuitId()
    {
        return m_iClosedCircuitId;
    }
    
    /**
     * 设置回路号
     * @param closedCircuitId   回路号
     */
    public void setClosedCircuitId(int closedCircuitId)
    {
        m_iClosedCircuitId = closedCircuitId;
    }
    
    /**
     * 获得功能类型
     * @return  功能类型
     */
    public int getFunctionType()
    {
        return m_iFunctionType;
    }
    
    /**
     * 设置功能类型
     * @param functionType  功能类型
     */
    public void setFunctionType(int functionType)
    {
        m_iFunctionType = functionType;
    }
    
    /**
     * 获得功能类型码对应的描述字符串
     * @param iFuncType 功能类型码
     * @return  功能类型码对应的描述字符串
     */
    public static String getFunctionTypeDesc(int iFuncType)
    {
        String          strRet = "未知";
        
        
        switch (iFuncType)
        {
        case FT_RESERVE:
            strRet = "保留";
            break;
            
        case FT_CONTROL:
            strRet = "控制类型";
            break;
            
        case FT_DISPLAY:
            strRet = "面板显示类型";
            break;

        default:
            break;
        }
        
        return strRet;
    }
    
    /**
     * 获得设备类型字
     * @return  设备类型字
     */
    public int getModuleType()
    {
        return m_iModuleType;
    }
    
    /**
     * 设置设备类型字
     * @param moduleType    设备类型字
     */
    public void setModuleType(int moduleType)
    {
        m_iModuleType = moduleType;
    }
    
    /**
     * 获得设备类型字对应的描述字符串
     * @param iModuleType   设备类型字
     * @return  设备类型字对应的描述字符串
     */
    public static String getModuleTypeDesc(int iModuleType)
    {
        String          strRet = "未知";
        
        
        switch (iModuleType)
        {
        case MT_DCU:
            strRet = "DCU主模块";
            break;
            
        case MT_Aircon:
            strRet = "空调模块";
            break;
            
        case MT_8RoadRelay:
            strRet = "8路继电器模块";
            break;
            
        case MT_4RoadLight:
            strRet = "4路调光板";
            break;
            
        case MT_2RoadRelay:
            strRet = "2路继电器模块";
            break;
            
        case MT_3RoadLed:
            strRet = "3路LED调光版";
            break;
            
        case MT_DoorPlate:
            strRet = "门牌显示模块";
            break;
            
        case MT_RFID:
            strRet = "RFID插卡取电模块";
            break;
            
        case MT_20Keys:
            strRet = "20按键模块";
            break;
            
        case MT_Rectifier:
            strRet = "整流器";
            break;
            
        case MT_LightSensor:
            strRet = "光感器";
            break;

        default:
            break;
        }
        
        return strRet;
    }
    
    /**
     * 获得通讯参数
     * @return  通讯参数
     */
    public int getCommParam()
    {
        return m_iCommParam;
    }
    
    /**
     * 设置通讯参数
     * @param commParam 通讯参数
     */
    public void setCommParam(int commParam)
    {
        m_iCommParam = commParam;
    }
    
    /**
     * 获得通讯参数对应的描述字符串
     * @param iCommParam    通讯参数
     * @return  通讯参数对应的描述字符串
     */
    public static String getCommParamDesc(int iCommParam)
    {
        String          strRet = "未知";
        
        
        switch (iCommParam)
        {
        case CP_1200:
            strRet = "1200";
            break;
            
        case CP_2400:
            strRet = "2400";
            break;
            
        case CP_4800:
            strRet = "4800";
            break;
            
        case CP_9600:
            strRet = "9600";
            break;
            
        case CP_19200:
            strRet = "19200";
            break;

        default:
            break;
        }
        
        return strRet;
    }
    
    /**
     * 获得关联设备
     * @return  关联设备
     */
    public List<CSubRoad> getRelateDevices()
    {
        return m_lstRelateDevices;
    }
    
    /**
     * 设置关联设备
     * @param relateDevices 关联设备
     */
    public void setRelateDevices(List<CSubRoad> relateDevices)
    {
        m_lstRelateDevices = relateDevices;
    }
    
    /**
     * 编码
     * @param ucBuffer      存放编码结果的缓冲区
     * @param iOffset       存放编码结果的起始索引
     * @param iEncodeBytes  编码结果的长度
     * @return  成功返回true
     */
    public boolean Encode(byte[] ucBuffer, int iOffset, IntHolder iEncodeBytes)
    {
        int             iOldOffset = iOffset;
        
        
        // 设备节点编号
        ucBuffer[iOffset++] = (byte)m_iModuleId;
        
        // 保留
        ucBuffer[iOffset++] = 0x00;
        
        // 设备节点地址
        byte[] maBytes = NumberUtil.StringToBcdArray(m_strModuleAddress);
        for (int i = maBytes.length - 1; i >= 0; i--)
        {
            ucBuffer[iOffset++] = maBytes[i];
        }
        
        // 回路号
        ucBuffer[iOffset++] = (byte)m_iClosedCircuitId;
        
        // 功能类型
        ucBuffer[iOffset++] = (byte)m_iFunctionType;
        
        // 设备类型字
        ucBuffer[iOffset++] = (byte)m_iModuleType;
        
        // 通讯参数
        ucBuffer[iOffset++] = (byte)m_iCommParam;
        
        // 关联设备数
        ucBuffer[iOffset++] = (byte)m_lstRelateDevices.size();
        
        // 关联设备
        for (int i = 0; i < m_lstRelateDevices.size(); i++)
        {
            CSubRoad subRoad = m_lstRelateDevices.get(i);
            IntHolder iSubRoadEncodeBytes = new IntHolder(0);
            
            // 编码子路
            if (!subRoad.Encode(ucBuffer, iOffset, iSubRoadEncodeBytes))
            {
                return false;
            }

            // 递增已编码的字节数
            iOffset += iSubRoadEncodeBytes.value;
        }
        
        // 返回编码的字节数
        if (null != iEncodeBytes)
        {
            iEncodeBytes.value = iOffset - iOldOffset;
        }
        
        return true;
    }
    
    /**
     * 解析设备节点报文
     * @param ucBuffer      存放设备节点报文的数组
     * @param iOffset       设备节点报文的起始索引
     * @param iParseBytes   解析的字节数
     * @return  解析成功返回true
     */
    public boolean parseFrame(byte[] ucBuffer, int iOffset, IntHolder iParseBytes)
    {
        int             iOldOffset = 0;
        
        
        // 保存解析的起始位置
        iOldOffset = iOffset;
        
        // 设备节点编号
        m_iModuleId = (ucBuffer[iOffset++] & 0xff);
        
        // 保留
        @SuppressWarnings("unused")
        int iValue = ucBuffer[iOffset++];
        
        // 设备节点地址
        m_strModuleAddress = "";
        for (int i = 0; i < 6; i++)
        {
            m_strModuleAddress = NumberUtil.BcdArrayToString(ucBuffer, iOffset++, 1) + m_strModuleAddress;
        }
        
        // 回路号
        m_iClosedCircuitId = ucBuffer[iOffset++];
        
        // 功能类型
        m_iFunctionType = ucBuffer[iOffset++];
        
        // 设备类型字
        m_iModuleType = ucBuffer[iOffset++];
        
        // 通讯参数
        m_iCommParam = ucBuffer[iOffset++];
        
        // 关联设备数
        int iRelateDeviceNum = ucBuffer[iOffset++];
        
        // 关联设备
        m_lstRelateDevices.clear();
        for (int i = 0; i < iRelateDeviceNum; i++)
        {
            CSubRoad subRoad = new CSubRoad();
            IntHolder iSubRoadParseBytes = new IntHolder(0);
            
            // 解析子路报文
            if (!subRoad.parseFrame(ucBuffer, iOffset, iSubRoadParseBytes))
            {
                return false;
            }
            
            // 保存子路对象
            m_lstRelateDevices.add(subRoad);
            
            // 递增已解析的字节数
            iOffset += iSubRoadParseBytes.value;
        }
        
        // 返回解析的字节数
        if (null != iParseBytes)
        {
            iParseBytes.value = iOffset - iOldOffset;
        }
        
        return true;
    }
}
