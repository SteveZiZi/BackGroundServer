package com.xtoee.devices;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.omg.CORBA.IntHolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.xtoee.devices.dcu.CDcuDevice;
import com.xtoee.devices.pad.CPadDevice;
import com.xtoee.util.CSocketUtil;
import com.xtoee.util.StringUtil;

/**
 * 供电系统
 * 
 * @author zgm
 *
 */
public class CPowerSystem
{
    /**
     * XML节点定义
     */
    protected final String          NODE_ROOT    = "root";
    protected final String          NODE_SERVER_PORT = "serverPort";
    protected final String          NODE_MAX_HEART_BEAT_INTERVAL = "maxHeartBeatInterval";
    protected final String          NODE_STATE_POLL_INTERVAL = "statePollInterval";
    protected final String          NODE_READ_TIMEOUT = "readTimeOut";
    protected final String          NODE_DEVICES = "devices";
    protected final String          NODE_DEVICE  = "device";
    protected final String          NODE_TYPE    = "type";

    private static CPowerSystem     s_PowerSystem;              // 单例对象
    private String                  m_strCfgFile;               // 配置文件路径

    private ArrayList<IDevice>      m_deviceList;               // 设备容器

    private ScheduledThreadPoolExecutor m_tpDeviceStatePoll;    // 设备状态轮询线程池，用于周期性的查询所有设备的状态
    
    private int                     m_iServerPort;              // 服务器监听端口
    private boolean                 m_bServerFlag;              // 服务器是否运行
    private long                    m_nMaxHeartBeatInterval;    // 设备的最大心跳间隔（毫秒）
    private long                    m_nStatePollInterval;       // 设备状态轮询间隔（毫秒）
    private long                    m_nReadTimeout;             // 服务器等待客户端响应的超时（毫秒）

    
    /**
     * 构造函数
     */
    private CPowerSystem()
    {
        m_deviceList = new ArrayList<IDevice>();
        m_tpDeviceStatePoll = new ScheduledThreadPoolExecutor(20);
        m_tpDeviceStatePoll.setRemoveOnCancelPolicy(true);
        m_iServerPort = 8888;
        m_bServerFlag = true;
        m_nMaxHeartBeatInterval = 300 * 1000;
        m_nStatePollInterval = 3000;
        m_nReadTimeout = 5000;
    }

    /**
     * 创建供电系统单例对象
     * 
     * @return 供电系统单例对象
     */
    public static CPowerSystem GetInstance()
    {
        // 如果对象未创建，那么创建它
        if (null == s_PowerSystem)
        {
            s_PowerSystem = new CPowerSystem();
        }

        return s_PowerSystem;
    }

    /**
     * 创建设备对象
     * 
     * @param eDeviceType 设备类型
     * @param strDeviceIP 设备IP
     * @return 生成的设备对象
     */
    public IDevice CreateDevice(enumDeviceType eDeviceType, String strDeviceIP)
    {
        IDevice tfDevice = null;

        switch (eDeviceType)
        {
        case DCU:
            tfDevice = new CDcuDevice(strDeviceIP);
            break;

        default:
            break;
        }

        return tfDevice;
    }

    /**
     * 添加设备到容器中
     * 
     * @param tfDevice 设备对象
     * @return 添加成功返回true
     */
    public synchronized boolean AddDevice(IDevice tfDevice)
    {
        // 检查设备是否已经存在
        if ((null == tfDevice) || (GetDeviceByLogicAddress(tfDevice.getLogicAddress()) != null))
        {
            return false;
        }

        // 添加设备到容器中
        m_deviceList.add(tfDevice);
        return true;
    }

    /**
     * 获得设备列表
     * @return  设备列表
     */
    public List<IDevice> getDeviceList()
    {
        return m_deviceList;
    }

    /**
     * 获得指定索引的对象
     * 
     * @param nIndex 对象索引
     * @return 设备对象
     */
    public synchronized IDevice GetDeviceByIdx(int nIndex)
    {
        // 检查输入参数
        if ((nIndex < 0) || (nIndex >= m_deviceList.size()))
        {
            return null;
        }

        return m_deviceList.get(nIndex);
    }
    
    /**
     * 获得指定逻辑地址的设备
     * 
     * @param strLogicAddress 逻辑地址
     * @return 设备对象
     */
    public synchronized IDevice GetDeviceByLogicAddress(String strLogicAddress)
    {
        // 检查输入参数
        if (null == strLogicAddress)
        {
            return null;
        }

        // 在设备列表中查找指定逻辑地址的设备
        for (IDevice tfDevice : m_deviceList)
        {
            if ((null != tfDevice) && strLogicAddress.equals(tfDevice.getLogicAddress()))
            {
                return tfDevice;
            }
        }

        return null;
    }

    /**
     * 获得设备数量
     * 
     * @return 设备数量
     */
    public synchronized int GetCount()
    {
        return m_deviceList.size();
    }

    /**
     * 加载XML
     * 
     * @param strFile XML文件路径
     * @return 加载成功返回true
     */
    public synchronized boolean LoadConfig(String strFile)
    {
        IDevice tfDevice;
        boolean bRet = false;

        // 检查输入参数
        if ((null == strFile) || (strFile.length() == 0))
        {
            return false;
        }
        m_strCfgFile = strFile;

        // 解析XML
        try
        {
            // 创建XML文档解析器
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // 加载XML文档
            InputStream iStream = new FileInputStream(m_strCfgFile);
            Document xmlDoc = builder.parse(iStream);

            // <root>
            Element xnRoot = xmlDoc.getDocumentElement();
            if ((null == xnRoot) || !(xnRoot.hasChildNodes()))
            {
                return false;
            }

            // <serverPort>
            Element xnServerPort = (Element) xnRoot.getElementsByTagName(NODE_SERVER_PORT).item(0);
            if ((null != xnServerPort) && !StringUtil.IsNullOrEmpty(xnServerPort.getTextContent()))
            {
                setServerPort(Integer.parseInt(xnServerPort.getTextContent()));
            }

            // <maxHeartBeatInterval>
            Element xnMaxHeartBeatInterval = (Element) xnRoot.getElementsByTagName(NODE_MAX_HEART_BEAT_INTERVAL).item(0);
            if ((null != xnMaxHeartBeatInterval) && !StringUtil.IsNullOrEmpty(xnMaxHeartBeatInterval.getTextContent()))
            {
                setMaxHeartBeatInterval(Integer.parseInt(xnMaxHeartBeatInterval.getTextContent()));
            }
            
            // <statePollInterval>
            Element xnStatePollInterval = (Element) xnRoot.getElementsByTagName(NODE_STATE_POLL_INTERVAL).item(0);
            if ((null != xnStatePollInterval) && !StringUtil.IsNullOrEmpty(xnStatePollInterval.getTextContent()))
            {
                setStatePollInterval(Integer.parseInt(xnStatePollInterval.getTextContent()));
            }

            // <readTimeOut>
            Element xnReadTimeOut = (Element) xnRoot.getElementsByTagName(NODE_READ_TIMEOUT).item(0);
            if ((null != xnReadTimeOut) && !StringUtil.IsNullOrEmpty(xnReadTimeOut.getTextContent()))
            {
                setReadTimeout(Integer.parseInt(xnReadTimeOut.getTextContent()));
            }

            // <devices>
            Element xnDevices = (Element) xnRoot.getElementsByTagName(NODE_DEVICES).item(0);
            if (xnDevices == null)
            {
                return true;
            }

            // <device>
            NodeList lstDevice = xnDevices.getElementsByTagName(NODE_DEVICE);
            for (int i = 0; i < lstDevice.getLength(); i++)
            {
                Element xnDevice = (Element) lstDevice.item(i);
                if ((null == xnDevice) || !xnDevice.hasChildNodes())
                {
                    continue;
                }

                // <type>
                Element xnType = (Element) xnDevice.getElementsByTagName(NODE_TYPE).item(0);
                if ((xnType == null) || StringUtil.IsNullOrEmpty(xnType.getTextContent()))
                {
                    continue;
                }

                // 创建设备对象
                tfDevice = CreateDevice(enumDeviceType.valueOf(xnType.getTextContent()), "");
                if (null == tfDevice)
                {
                    continue;
                }

                // 加载设备配置
                if (!tfDevice.LoadConfig(xnDevice))
                {
                    continue;
                }

                // 添加设备到容器中
                AddDevice(tfDevice);
            }

            // 标记为解析成功
            bRet = true;
        }
        catch (Exception e)
        {
            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append(e.toString());
            strBuilder.append("\r\n加载");
            strBuilder.append(m_strCfgFile);
            strBuilder.append("失败，请检查该文件是否存在且合法！");

            System.out.println(strBuilder.toString());
        }

        return bRet;
    }

    /**
     * 保存XML
     * 
     * @param strFile 保存路径
     * @return 成功返回true
     */
    public synchronized boolean SaveConfig(String strFile)
    {
        boolean bRet = false;

        // 检查文件保存路径
        if ((null != strFile) && (strFile.length() != 0))
        {
            m_strCfgFile = strFile;
        }

        // 保存XML
        try
        {
            // 创建XML文档解析器
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // 获得文档对象
            Document xmlDoc = builder.newDocument();
            if (xmlDoc == null)
            {
                return false;
            }

            // <root>
            Element xnRoot = xmlDoc.createElement(NODE_ROOT);
            xmlDoc.appendChild(xnRoot);

            // <serverPort>
            Element xnServerPort = xmlDoc.createElement(NODE_SERVER_PORT);
            xnServerPort.setTextContent(Integer.toString(m_iServerPort));
            xnRoot.appendChild(xnServerPort);

            // <maxHeartBeatInterval>
            Element xnMaxHeartBeatInterval = xmlDoc.createElement(NODE_MAX_HEART_BEAT_INTERVAL);
            xnMaxHeartBeatInterval.setTextContent(Long.toString(m_nMaxHeartBeatInterval));
            xnRoot.appendChild(xnMaxHeartBeatInterval);
            
            // <statePollInterval>
            Element xnStatePollInterval = xmlDoc.createElement(NODE_STATE_POLL_INTERVAL);
            xnStatePollInterval.setTextContent(Long.toString(m_nStatePollInterval));
            xnRoot.appendChild(xnStatePollInterval);

            // <readTimeOut>
            Element xnReadTimeOut = xmlDoc.createElement(NODE_READ_TIMEOUT);
            xnReadTimeOut.setTextContent(Long.toString(m_nReadTimeout));
            xnRoot.appendChild(xnReadTimeOut);

            // <devices>
            Element xnDevices = xmlDoc.createElement(NODE_DEVICES);
            xnRoot.appendChild(xnDevices);

            // 遍历设备链表
            for (IDevice tfDevice : m_deviceList)
            {
                if (null == tfDevice)
                {
                    continue;
                }

                // <device>
                Element xnDevice = xmlDoc.createElement(NODE_DEVICE);
                if (!tfDevice.SaveConfig(xmlDoc, xnDevice))
                {
                    return false;
                }

                xnDevices.appendChild(xnDevice);
            }

            // 标记为保存成功
            Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            tf.transform(new DOMSource(xmlDoc), new StreamResult(new FileOutputStream(m_strCfgFile)));
            bRet = true;
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        }

        return bRet;
    }

    /**
     * 开始轮询指定设备的状态
     * 
     * @param tfDevice 设备对象
     */
    public boolean StartDeviceStatePoll(IDevice tfDevice)
    {
        // 检查输入参数
        if (null == tfDevice)
        {
            return false;
        }

        // 如果当前已经存在设备状态轮询任务，那么返回失败
        if (null != tfDevice.GetScheduledFuture())
        {
            return false;
        }

        // 创建设备状态轮询任务
        ScheduledFuture<?> sf = m_tpDeviceStatePoll.scheduleWithFixedDelay(tfDevice, 0, m_nStatePollInterval, TimeUnit.MILLISECONDS);
        if (null == sf)
        {
            return false;
        }

        // 保存设备状态轮询任务的句柄
        tfDevice.SetScheduledFuture(sf);
        return true;
    }

    /**
     * 停止轮询指定设备的状态
     * 
     * @param tfDevice 设备对象
     */
    public boolean StopDeviceStatePoll(IDevice tfDevice)
    {
        // 检查输入参数
        if (null == tfDevice)
        {
            return false;
        }

        // 获得设备状态轮询任务
        ScheduledFuture<?> sf = tfDevice.GetScheduledFuture();
        if (null == sf)
        {
            return false;
        }

        // 停止设备状态轮询任务
        if (!sf.cancel(true))
        {
            return false;
        }

        // 将设备状态轮询任务置空
        tfDevice.SetScheduledFuture(null);

        // 将设备状态设置为不在线
        tfDevice.SetDeviceState(enumDeviceState.OffLine);
        return true;
    }

    /**
     * 是否运行服务器监听线程
     * 
     * @return 返回true表示继续运行服务器监听线程
     */
    public boolean isRunServer()
    {
        return m_bServerFlag;
    }

    /**
     * 设置是否运行服务器监听线程
     * 
     * @param bRunFlag false表示停止
     */
    public void setRunServer(boolean bRunFlag)
    {
        m_bServerFlag = bRunFlag;
    }

    /**
     * 开始接收客户端的连接
     */
    public void StartAcceptConn()
    {
        // 创建服务器socket
        try (ServerSocket sockServer = new ServerSocket(getServerPort());)
        {
            // 如果继续运行服务器监听线程
            while (isRunServer())
            {
                // 接收客户端连接
                Socket sockConn = sockServer.accept();

                // 设置Socket属性
                sockConn.setSoTimeout((int) m_nReadTimeout);
                sockConn.setKeepAlive(true);

                // 创建通信线程，用于处理服务器和客户端的通信报文
                new CLoginHandleThread(this, sockConn).start();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 停止接收客户端的连接
     */
    public void StopAcceptConn()
    {
        setRunServer(false);
    }

    /**
     * 获得服务器监听端口
     * @return  服务器监听端口
     */
    public int getServerPort()
    {
        return m_iServerPort;
    }

    /**
     * 设置服务器监听端口
     * @param iServerPort 服务器监听端口
     */
    public void setServerPort(int iServerPort)
    {
        if (iServerPort >= 0)
        {
            this.m_iServerPort = iServerPort;
        }
    }

    /**
     * 获得设备的最大心跳间隔（毫秒）
     * 
     * @return 最大心跳间隔
     */
    public long getMaxHeartBeatInterval()
    {
        return m_nMaxHeartBeatInterval;
    }

    /**
     * 设置设备的最大心跳间隔（毫秒）
     * 
     * @param nMaxHeartBeatInterval 最大心跳间隔（毫秒）
     */
    public void setMaxHeartBeatInterval(long nMaxHeartBeatInterval)
    {
        // 检查输入参数
        if (nMaxHeartBeatInterval >= 0)
        {
            m_nMaxHeartBeatInterval = nMaxHeartBeatInterval;
        }
    }

    /**
     * 获得服务器等待设备响应的超时（毫秒）
     * 
     * @return 服务器等待设备响应的超时（毫秒）
     */
    public long getReadTimeout()
    {
        return m_nReadTimeout;
    }

    /**
     * 设置服务器等待设备响应的超时（毫秒）
     * 
     * @param nReadTimeout 服务器等待设备响应的超时（毫秒）
     */
    public void setReadTimeout(long nReadTimeout)
    {
        if (nReadTimeout >= 0)
        {
            this.m_nReadTimeout = nReadTimeout;
        }
    }

    /**
     * 获得设备状态轮询间隔（毫秒）
     * @return  轮询间隔（毫秒）
     */
    public long geStatePollInterval()
    {
        return m_nStatePollInterval;
    }

    /**
     * 设置设备状态轮询间隔（毫秒）
     * @param nStatePollInterval    设备状态轮询间隔（毫秒）
     */
    public void setStatePollInterval(long nStatePollInterval)
    {
        if (nStatePollInterval >= 0)
        {
            this.m_nStatePollInterval = nStatePollInterval;
        }
    }
}

/**
 * 客户端登陆处理类
 * 
 * @author zgm
 *
 */
class CLoginHandleThread extends Thread
{
    private CPowerSystem            powerSystem;                // 供电系统
    private Socket                  sockConn;                   // 服务器和客户端的连接

    
    /**
     * 构造函数
     * 
     * @param powerSystem 供电系统
     * @param sockConn 服务器和客户端的连接
     */
    public CLoginHandleThread(CPowerSystem powerSystem, Socket sockConn)
    {
        this.powerSystem = powerSystem;
        this.sockConn = sockConn;
    }

    /**
     * 线程执行函数
     */
    public void run()
    {
        CDcuDevice tmpDcuDevice = new CDcuDevice();
        CPadDevice tmpPadDevice = new CPadDevice();
        byte[] sentBuffer = new byte[1024];
        IntHolder iSentFrameLen = new IntHolder(0);

        try
        {
            // 获得socket的文件输入流
            InputStream inStream = sockConn.getInputStream();
            OutputStream outStream = sockConn.getOutputStream();
            
            // 休眠一段时间，等待客户端发送登陆报文
            Thread.sleep(1000);

            // 接收客户端的报文
            byte[] recvBytes = CSocketUtil.readStream(inStream);
            if ((null == recvBytes) || (recvBytes.length == 0))
            {
                return;
            }

            // 解析DCU登入请求帧
            if (tmpDcuDevice.parseIoctlRequest_login(recvBytes, recvBytes.length))
            {
                // 检查指定逻辑地址的设备是否已存在
                String strLogicAddress = tmpDcuDevice.getLogicAddress();
                CDcuDevice device = (CDcuDevice) powerSystem.GetDeviceByLogicAddress(strLogicAddress);
                if (device == null)
                {
                    // 创建设备对象，并添加到系统管理器中
                    device = new CDcuDevice(strLogicAddress);
                    powerSystem.AddDevice(device);
                }

                // 更新设备信息
                device.setSockConn(sockConn);
                device.setID(Integer.parseInt(strLogicAddress.substring(6)));
                device.setDevicePort(sockConn.getPort());
                device.SetDeviceIP(sockConn.getInetAddress().getHostAddress());
                device.SetDeviceName(strLogicAddress);
                device.setMasterAddress(tmpDcuDevice.getMasterAddress());
                device.setLastTimeHeartBeat(System.currentTimeMillis());

                // 设备在线状态
                if (device.GetDeviceState() == enumDeviceState.OffLine)
                {
                    device.SetDeviceState(enumDeviceState.Online);
                }

                // 合成DCU登入响应帧
                if (device.composeIoctlResponse_login(sentBuffer, iSentFrameLen))
                {
                    // 发送响应报文
                    outStream.write(sentBuffer, 0, iSentFrameLen.value);
                    outStream.flush();
                }

                // 开始轮询设备状态
                powerSystem.StartDeviceStatePoll(device);
            }
            // 解析Pad登入请求帧
            else if (tmpPadDevice.parseIoctlRequest_login(recvBytes, recvBytes.length)) 
            {
                // 创建Pad对象
                CPadDevice pad = new CPadDevice(tmpPadDevice.getLogicAddress());
                pad.setSockConn(sockConn);
                pad.setMasterAddress(tmpPadDevice.getMasterAddress());
                pad.setLastTimeHeartBeat(System.currentTimeMillis());
                
                // 合成DCU登入响应帧
                if (pad.composeIoctlResponse_login(sentBuffer, iSentFrameLen))
                {
                    // 发送响应报文
                    outStream.write(sentBuffer, 0, iSentFrameLen.value);
                    outStream.flush();
                }
                
                // 开启线程，处理Pad请求
                new Thread(pad).start();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
