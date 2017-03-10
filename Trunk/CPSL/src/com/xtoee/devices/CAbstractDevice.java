package com.xtoee.devices;

import java.net.Socket;
import java.util.concurrent.ScheduledFuture;

import org.omg.CORBA.IntHolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.xtoee.devices.dcu.enumControlCode;
import com.xtoee.util.NumberUtil;
import com.xtoee.util.StringUtil;

/**
 * 时频设备基类
 * @author zgm
 *
 */
public abstract class CAbstractDevice implements IDevice
{
    protected final String          NODE_TYPE           = "type";
    protected final String          NODE_ID             = "id";
    protected final String          NODE_NAME           = "name";
    protected final String          NODE_IP             = "ip";
    protected final String          NODE_PORT           = "port";
    protected final String          NODE_LOGIC_ADDR     = "logicAddress";
    protected final String          NODE_MASTER_ADDR    = "masterAddress";
    
    protected static final int      SYMBOL_START    = 0x68;     // 帧起始符
    protected static final int      SYMBOL_END      = 0x16;     // 帧结束符
    
    protected enumDeviceType        m_eDeviceType;              // 设备类型
    protected int                   m_nID;                      // 设备ID
    protected String                m_strDeviceName;            // 设备名称
    protected String                m_strDeviceIP;              // 设备IP
    protected int                   m_iDevicePort;              // 设备端口
    protected String                m_strLogicAddress;          // 逻辑地址
    protected int                   m_iMasterAddress;           // 主站地址
    protected enumDeviceState       m_eDeviceState;             // 设备状态
    
    protected Socket                m_sockConn;                 // socket连接
    protected long                  m_nLastTimeHeartBeat;       // 最近一次收到心跳报文的时间（毫秒）

    protected ScheduledFuture<?>    m_sfDeviceAttrs;            // 设备状态轮询任务
    
    
    /**
     * 构造函数
     * @param strLogicAddress   逻辑地址
     */
    public CAbstractDevice(String strLogicAddress)
    {
        m_strDeviceIP = "";
        m_iDevicePort = 161;
        m_strLogicAddress = strLogicAddress;
        m_eDeviceState = enumDeviceState.OffLine;
        m_sfDeviceAttrs = null;
    }

    /**
     * 获得时频设备的类型
     * @return  设备类型
     */
    public enumDeviceType GetDeviceType()
    {
        return m_eDeviceType;
    }
    
    /**
     * 设置时频设备的类型
     * @param eType 设备类型
     */
    public void SetDeviceType(enumDeviceType eType)
    {
        m_eDeviceType = eType;
    }
    
    /**
     * 获得设备ID
     * @return  设备ID
     */
    public int getID()
    {
        return m_nID;
    }
    
    /**
     * 设置设备ID
     * @param nID 设备ID
     */
    public void setID(int nID)
    {
        if (nID > 0)
        {
            m_nID = nID;
        }
    }
    
    /**
     * 获得设备名称
     * @return  设备名称
     */
    public String getDeviceName()
    {
        return m_strDeviceName;
    }
    
    /**
     * 设置设备名称
     * @param strName   设备名称
     */
    public void SetDeviceName(String strName)
    {
        m_strDeviceName = strName;
    }
    
    /**
     * 获得设备IP地址
     * @return  设备IP地址
     */
    public String GetDeviceIP()
    {
        return m_strDeviceIP;
    }
    
    /**
     * 设置设备IP地址
     * @param strIP 设备IP地址
     */
    public void SetDeviceIP(String strIP)
    {
        m_strDeviceIP = strIP;
    }

    /**
     * 获得设备通信端口号
     * @return  设备端口号
     */
    public int getDevicePort()
    {
        return m_iDevicePort;
    }

    /**
     * 设置设备通信端口号
     * @param iDevicePort   新的设备端口号
     */
    public void setDevicePort(int iDevicePort)
    {
        this.m_iDevicePort = iDevicePort;
    }

    /**
     * 获得设备逻辑地址
     * @return  逻辑地址
     */
    public String getLogicAddress()
    {
        return m_strLogicAddress;
    }

    /**
     * 设置设备逻辑地址
     * @param strLogicAddress   逻辑地址
     */
    public void setLogicAddress(String strLogicAddress)
    {
        this.m_strLogicAddress = strLogicAddress;
    }

    /**
     * 获得设备主站地址
     * @return  主站地址
     */
    public int getMasterAddress()
    {
        return m_iMasterAddress;
    }

    /**
     * 设置设备主站地址
     * @param iMasterAddress    主站地址
     */
    public void setMasterAddress(int iMasterAddress)
    {
        this.m_iMasterAddress = iMasterAddress;
    }
    
    /**
     * 获得设备状态
     * @return  设备状态
     */
    public enumDeviceState GetDeviceState()
    {
        return m_eDeviceState;
    }
    
    /**
     * 设置设备状态
     * @param eNewState 设备状态
     */
    public void SetDeviceState(enumDeviceState eNewState)
    {
        // 检查设备状态是否已更改
        if (m_eDeviceState == eNewState)
        {
            return;
        }
        
        // 设置新的设备状态
        m_eDeviceState = eNewState;
    }

    /**
     * 获得服务器与设备通信的Socket
     * 
     * @return 与设备通信的Socket
     */
    public Socket getSockConn()
    {
        return m_sockConn;
    }

    /**
     * 设置服务器与设备通信的Socket
     * 
     * @param sockConn 与设备通信的Socket
     */
    public void setSockConn(Socket sockConn)
    {
        this.m_sockConn = sockConn;
    }

    /**
     * 获得最近一次收到心跳报文的时间
     * 
     * @return 收到心跳报文时的毫秒数
     */
    public long getLastTimeHeartBeat()
    {
        return m_nLastTimeHeartBeat;
    }

    /**
     * 设置最近一次收到心跳文本的时间
     * 
     * @param nLastTimeHeartBeat 收到心跳报文时的毫秒数
     */
    public void setLastTimeHeartBeat(long nLastTimeHeartBeat)
    {
        this.m_nLastTimeHeartBeat = nLastTimeHeartBeat;
    }
    
    /**
     * 获得设备状态轮询任务
     * @return  状态轮询任务
     */
    public ScheduledFuture<?> GetScheduledFuture()
    {
        return m_sfDeviceAttrs;
    }
    
    /**
     * 设置设备状态轮询任务
     * @param sf    状态轮询任务
     */
    public void SetScheduledFuture(ScheduledFuture<?> sf)
    {
        m_sfDeviceAttrs = sf;
    }
    
    /**
     * 加载配置
     * @param xnDevice  设备信息描述节点
     * @return  接在成功返回true
     */
    public boolean LoadConfig(Element xnDevice)
    {
        // 检查输入参数
        if (null == xnDevice)
        {
            return false;
        }
        
        // <id>
        Element xnID = (Element) xnDevice.getElementsByTagName(NODE_ID).item(0);
        if ((null == xnID) || StringUtil.IsNullOrEmpty(xnID.getTextContent()))
        {
            return false;
        }
        m_nID = Integer.parseInt(xnID.getTextContent());
        
        // <name>
        Element xnName = (Element) xnDevice.getElementsByTagName(NODE_NAME).item(0);
        if ((null == xnName) || StringUtil.IsNullOrEmpty(xnName.getTextContent()))
        {
            return false;
        }
        m_strDeviceName = xnName.getTextContent();
        
        // <ip>
        Element xnDeviceIP = (Element) xnDevice.getElementsByTagName(NODE_IP).item(0);
        if ((null == xnDeviceIP) || StringUtil.IsNullOrEmpty(xnDeviceIP.getTextContent()))
        {
            return false;
        }
        m_strDeviceIP = xnDeviceIP.getTextContent();
        
        // <port>
        Element xnDevicePort = (Element) xnDevice.getElementsByTagName(NODE_PORT).item(0);
        if ((null != xnDevicePort) && !StringUtil.IsNullOrEmpty(xnDevicePort.getTextContent()))
        {
            m_iDevicePort = Integer.parseInt(xnDevicePort.getTextContent());
        }
        
        // <logicAddress>
        Element xnLogicAddress = (Element) xnDevice.getElementsByTagName(NODE_LOGIC_ADDR).item(0);
        if ((null == xnLogicAddress) || StringUtil.IsNullOrEmpty(xnLogicAddress.getTextContent()))
        {
            return false;
        }
        m_strLogicAddress = xnLogicAddress.getTextContent();
        
        // <MasterAddress>
        Element xnMasterAddress = (Element) xnDevice.getElementsByTagName(NODE_MASTER_ADDR).item(0);
        if ((null == xnMasterAddress) || StringUtil.IsNullOrEmpty(xnMasterAddress.getTextContent()))
        {
            return false;
        }
        m_iMasterAddress = Integer.parseInt(xnMasterAddress.getTextContent());
        
        return true;
    }
    
    /**
     * 保存配置
     * @param xmlDoc    XML文档对象
     * @param xnDevice  XML设备节点对象
     * @return  保存成功返回true
     */
    public boolean SaveConfig(Document xmlDoc, Element xnDevice)
    {
        // 检查输入参数
        if ((null == xmlDoc) || (null == xnDevice))
        {
            return false;
        }
        
        // <id>
        Element xeId = xmlDoc.createElement(NODE_ID);
        xeId.setTextContent(Integer.toString(m_nID));
        xnDevice.appendChild(xeId);
        
        // <type>
        Element xeType = xmlDoc.createElement(NODE_TYPE);
        xeType.setTextContent(m_eDeviceType.toString());
        xnDevice.appendChild(xeType);
        
        // <name>
        Element xeName = xmlDoc.createElement(NODE_NAME);
        xeName.setTextContent(m_strDeviceName);
        xnDevice.appendChild(xeName);
        
        // <ip>
        Element xeDeviceIP = xmlDoc.createElement(NODE_IP);
        xeDeviceIP.setTextContent(m_strDeviceIP);
        xnDevice.appendChild(xeDeviceIP);
        
        // <port>
        Element xeDevicePort = xmlDoc.createElement(NODE_PORT);
        xeDevicePort.setTextContent(Integer.toString(m_iDevicePort));
        xnDevice.appendChild(xeDevicePort);
        
        // <logicAddress>
        Element xeLogicAddress = xmlDoc.createElement(NODE_LOGIC_ADDR);
        xeLogicAddress.setTextContent(m_strLogicAddress);
        xnDevice.appendChild(xeLogicAddress);
        
        // <masterAddress>
        Element xeMasterAddr = xmlDoc.createElement(NODE_MASTER_ADDR);
        xeMasterAddr.setTextContent(Integer.toString(m_iMasterAddress));
        xnDevice.appendChild(xeMasterAddr);
        
        return true;
    }

    /**
     * 设备状态轮询函数，设备状态轮询任务会周期性的调用此函数
     */
    public abstract void run();

    /**
     * 生成校验码
     * 
     * @param ucBuffer 数组
     * @param iStart 起始索引
     * @param iCount 数量
     * @return 校验码
     * @throws Exception 参数异常
     */
    protected byte GenCheckSum(byte[] ucBuffer, int iStart, int iCount) throws Exception
    {
        int             iSum = 0;

        // 检查输入参数
        if ((null == ucBuffer) || (iStart < 0) || (iStart + iCount > ucBuffer.length))
        {
            throw new Exception("函数GenCheckSum的输入参数不正确！");
        }

        // 计算数组中指定起始位置和数量的字节的累加和
        for (int i = 0; i < iCount; i++)
        {
            iSum += ucBuffer[iStart + i];
        }

        // 返回累加和的最低字节
        return (byte) (iSum & 0xFF);
    }

    /**
     * 解析DCU登入请求帧
     * 
     * @param ucBuffer 存放待解析报文的缓冲区
     * @param iFrameLen 报文帧的长度
     * @return 解析成功返回true
     * @throws Exception 异常信息
     */
    public boolean parseIoctlRequest_login(byte[] ucBuffer, int iFrameLen) throws Exception
    {
        int             nIndex = 0;
        

        // 检查输入参数
        if ((null == ucBuffer) || (iFrameLen < 16) || (ucBuffer.length < iFrameLen))
        {
            return false;
        }

        // 起始字符
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }
        
        // 逻辑地址
        m_strLogicAddress = NumberUtil.BcdArrayToString(ucBuffer, nIndex, 4);
        nIndex += 4;

        // 主站地址
        m_iMasterAddress = ucBuffer[nIndex++];

        // 命令序号
        int iValue = ucBuffer[nIndex++];

        // 起始字符
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // 控制码
        if ((enumControlCode.LoginResponse.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // 数据长度
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // 密码
        for (int i = 0; i < 3; i++)
        {
            // 检查密码
            if (0x11 != ucBuffer[nIndex++])
            {
                return false;
            }
        }

        // 校验和
        iValue = GenCheckSum(ucBuffer, 0, nIndex);
        if (iValue != ucBuffer[nIndex++])
        {
            return false;
        }

        // 帧尾
        if (SYMBOL_END != ucBuffer[nIndex++])
        {
            return false;
        }

        return true;
    }

    /**
     * 合成DCU登入响应帧
     * 
     * @param ucBuffer 存放响应帧的缓冲区
     * @param iFrameLen 响应帧的长度
     * @return 合成成功返回true
     * @throws Exception 异常信息
     */
    public boolean composeIoctlResponse_login(byte[] ucBuffer, IntHolder iFrameLen) throws Exception
    {
        int             nIndex = 0;
        

        // 检查输入参数
        if ((null == ucBuffer) || (ucBuffer.length < 13))
        {
            return false;
        }

        // 起始字符
        ucBuffer[nIndex++] = (byte) SYMBOL_START;

        // 逻辑地址
        System.arraycopy(NumberUtil.StringToBcdArray(m_strLogicAddress), 0, ucBuffer, nIndex, 4);
        nIndex += 4;

        // 主站地址
        ucBuffer[nIndex++] = (byte) m_iMasterAddress;

        // 命令序号
        ucBuffer[nIndex++] = 0x00;

        // 起始字符
        ucBuffer[nIndex++] = (byte) SYMBOL_START;

        // 控制码
        ucBuffer[nIndex++] = (byte) enumControlCode.LoginResponse.getValue();

        // 数据长度
        System.arraycopy(NumberUtil.ShortToByte2(0), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 校验和
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // 帧尾
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // 设置响应命令的长度
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * 解析DCU登出请求帧
     * 
     * @param ucBuffer 存放待解析报文的缓冲区
     * @param iFrameLen 报文帧的长度
     * @param iParsedLen 解析的字节数
     * @return 解析成功返回true
     * @throws Exception 异常信息
     */
    protected boolean parseIoctlRequest_logout(byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen) throws Exception
    {
        int nIndex = 0;

        // 检查输入参数
        if ((null == ucBuffer) || (iFrameLen < 13) || (ucBuffer.length < iFrameLen))
        {
            return false;
        }

        // 起始字符
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // 逻辑地址
        if (!m_strLogicAddress.equals(NumberUtil.BcdArrayToString(ucBuffer, nIndex, 4)))
        {
            return false;
        }
        nIndex += 4;

        // 主站地址
        if (m_iMasterAddress != ucBuffer[nIndex++])
        {
            return false;
        }

        // 命令序号
        int iValue = ucBuffer[nIndex++];

        // 起始字符
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // 控制码
        if ((enumControlCode.LogoutResponse.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // 数据长度
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // 校验和
        iValue = GenCheckSum(ucBuffer, 0, nIndex);
        if (iValue != ucBuffer[nIndex++])
        {
            return false;
        }

        // 帧尾
        if (SYMBOL_END != ucBuffer[nIndex++])
        {
            return false;
        }
        
        // 返回已解析的长度
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * 合成DCU登出响应帧
     * 
     * @param ucBuffer 存放响应帧的缓冲区
     * @param iFrameLen 响应帧的长度
     * @return 合成成功返回true
     * @throws Exception 异常信息
     */
    protected boolean composeIoctlResponse_logout(byte[] ucBuffer, IntHolder iFrameLen) throws Exception
    {
        int             nIndex = 0;
        

        // 检查输入参数
        if ((null == ucBuffer) || (ucBuffer.length < 13))
        {
            return false;
        }

        // 起始字符
        ucBuffer[nIndex++] = (byte) SYMBOL_START;

        // 逻辑地址
        System.arraycopy(NumberUtil.StringToBcdArray(m_strLogicAddress), 0, ucBuffer, nIndex, 4);
        nIndex += 4;

        // 主站地址
        ucBuffer[nIndex++] = (byte) m_iMasterAddress;

        // 命令序号
        ucBuffer[nIndex++] = 0x00;

        // 起始字符
        ucBuffer[nIndex++] = (byte) SYMBOL_START;

        // 控制码
        ucBuffer[nIndex++] = (byte) enumControlCode.LogoutResponse.getValue();

        // 数据长度
        System.arraycopy(NumberUtil.ShortToByte2(0), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 校验和
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // 帧尾
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // 设置响应命令的长度
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * 解析心跳帧
     * 
     * @param ucBuffer 存放待解析报文的缓冲区
     * @param iFrameLen 报文帧的长度
     * @param iParsedLen 解析的字节数
     * @return 解析成功返回true
     * @throws Exception 异常信息
     */
    protected boolean parseIoctlRequest_heartBeat(byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen) throws Exception
    {
        int nIndex = 0;

        // 检查输入参数
        if ((null == ucBuffer) || (iFrameLen < 13) || (ucBuffer.length < iFrameLen))
        {
            return false;
        }

        // 起始字符
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // 逻辑地址
        if (!m_strLogicAddress.equals(NumberUtil.BcdArrayToString(ucBuffer, nIndex, 4)))
        {
            return false;
        }
        nIndex += 4;

        // 主站地址
        if (m_iMasterAddress != ucBuffer[nIndex++])
        {
            return false;
        }

        // 命令序号
        int iValue = ucBuffer[nIndex++];

        // 起始字符
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // 控制码
        if ((enumControlCode.HeartBeatResponse.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // 数据长度
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // 校验和
        iValue = GenCheckSum(ucBuffer, 0, nIndex);
        if (iValue != ucBuffer[nIndex++])
        {
            return false;
        }

        // 帧尾
        if (SYMBOL_END != ucBuffer[nIndex++])
        {
            return false;
        }
        
        // 返回已解析的长度
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * 合成DCU心跳响应帧
     * 
     * @param ucBuffer 存放响应帧的缓冲区
     * @param iFrameLen 响应帧的长度
     * @return 合成成功返回true
     * @throws Exception 异常信息
     */
    protected boolean composeIoctlResponse_heartBeat(byte[] ucBuffer, IntHolder iFrameLen) throws Exception
    {
        int             nIndex = 0;
        

        // 检查输入参数
        if ((null == ucBuffer) || (ucBuffer.length < 13))
        {
            return false;
        }

        // 起始字符
        ucBuffer[nIndex++] = (byte) SYMBOL_START;

        // 逻辑地址
        System.arraycopy(NumberUtil.StringToBcdArray(m_strLogicAddress), 0, ucBuffer, nIndex, 4);
        nIndex += 4;

        // 主站地址
        ucBuffer[nIndex++] = (byte) m_iMasterAddress;

        // 命令序号
        ucBuffer[nIndex++] = 0x00;

        // 起始字符
        ucBuffer[nIndex++] = (byte) SYMBOL_START;

        // 控制码
        ucBuffer[nIndex++] = (byte) enumControlCode.HeartBeatResponse.getValue();

        // 数据长度
        System.arraycopy(NumberUtil.ShortToByte2(0), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 校验和
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // 帧尾
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // 设置响应命令的长度
        iFrameLen.value = nIndex;
        return true;
    }
}
