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
 * ����ϵͳ
 * 
 * @author zgm
 *
 */
public class CPowerSystem
{
    /**
     * XML�ڵ㶨��
     */
    protected final String          NODE_ROOT    = "root";
    protected final String          NODE_SERVER_PORT = "serverPort";
    protected final String          NODE_MAX_HEART_BEAT_INTERVAL = "maxHeartBeatInterval";
    protected final String          NODE_STATE_POLL_INTERVAL = "statePollInterval";
    protected final String          NODE_READ_TIMEOUT = "readTimeOut";
    protected final String          NODE_DEVICES = "devices";
    protected final String          NODE_DEVICE  = "device";
    protected final String          NODE_TYPE    = "type";

    private static CPowerSystem     s_PowerSystem;              // ��������
    private String                  m_strCfgFile;               // �����ļ�·��

    private ArrayList<IDevice>      m_deviceList;               // �豸����

    private ScheduledThreadPoolExecutor m_tpDeviceStatePoll;    // �豸״̬��ѯ�̳߳أ����������ԵĲ�ѯ�����豸��״̬
    
    private int                     m_iServerPort;              // �����������˿�
    private boolean                 m_bServerFlag;              // �������Ƿ�����
    private long                    m_nMaxHeartBeatInterval;    // �豸�����������������룩
    private long                    m_nStatePollInterval;       // �豸״̬��ѯ��������룩
    private long                    m_nReadTimeout;             // �������ȴ��ͻ�����Ӧ�ĳ�ʱ�����룩

    
    /**
     * ���캯��
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
     * ��������ϵͳ��������
     * 
     * @return ����ϵͳ��������
     */
    public static CPowerSystem GetInstance()
    {
        // �������δ��������ô������
        if (null == s_PowerSystem)
        {
            s_PowerSystem = new CPowerSystem();
        }

        return s_PowerSystem;
    }

    /**
     * �����豸����
     * 
     * @param eDeviceType �豸����
     * @param strDeviceIP �豸IP
     * @return ���ɵ��豸����
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
     * ����豸��������
     * 
     * @param tfDevice �豸����
     * @return ��ӳɹ�����true
     */
    public synchronized boolean AddDevice(IDevice tfDevice)
    {
        // ����豸�Ƿ��Ѿ�����
        if ((null == tfDevice) || (GetDeviceByLogicAddress(tfDevice.getLogicAddress()) != null))
        {
            return false;
        }

        // ����豸��������
        m_deviceList.add(tfDevice);
        return true;
    }

    /**
     * ����豸�б�
     * @return  �豸�б�
     */
    public List<IDevice> getDeviceList()
    {
        return m_deviceList;
    }

    /**
     * ���ָ�������Ķ���
     * 
     * @param nIndex ��������
     * @return �豸����
     */
    public synchronized IDevice GetDeviceByIdx(int nIndex)
    {
        // ����������
        if ((nIndex < 0) || (nIndex >= m_deviceList.size()))
        {
            return null;
        }

        return m_deviceList.get(nIndex);
    }
    
    /**
     * ���ָ���߼���ַ���豸
     * 
     * @param strLogicAddress �߼���ַ
     * @return �豸����
     */
    public synchronized IDevice GetDeviceByLogicAddress(String strLogicAddress)
    {
        // ����������
        if (null == strLogicAddress)
        {
            return null;
        }

        // ���豸�б��в���ָ���߼���ַ���豸
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
     * ����豸����
     * 
     * @return �豸����
     */
    public synchronized int GetCount()
    {
        return m_deviceList.size();
    }

    /**
     * ����XML
     * 
     * @param strFile XML�ļ�·��
     * @return ���سɹ�����true
     */
    public synchronized boolean LoadConfig(String strFile)
    {
        IDevice tfDevice;
        boolean bRet = false;

        // ����������
        if ((null == strFile) || (strFile.length() == 0))
        {
            return false;
        }
        m_strCfgFile = strFile;

        // ����XML
        try
        {
            // ����XML�ĵ�������
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // ����XML�ĵ�
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

                // �����豸����
                tfDevice = CreateDevice(enumDeviceType.valueOf(xnType.getTextContent()), "");
                if (null == tfDevice)
                {
                    continue;
                }

                // �����豸����
                if (!tfDevice.LoadConfig(xnDevice))
                {
                    continue;
                }

                // ����豸��������
                AddDevice(tfDevice);
            }

            // ���Ϊ�����ɹ�
            bRet = true;
        }
        catch (Exception e)
        {
            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append(e.toString());
            strBuilder.append("\r\n����");
            strBuilder.append(m_strCfgFile);
            strBuilder.append("ʧ�ܣ�������ļ��Ƿ�����ҺϷ���");

            System.out.println(strBuilder.toString());
        }

        return bRet;
    }

    /**
     * ����XML
     * 
     * @param strFile ����·��
     * @return �ɹ�����true
     */
    public synchronized boolean SaveConfig(String strFile)
    {
        boolean bRet = false;

        // ����ļ�����·��
        if ((null != strFile) && (strFile.length() != 0))
        {
            m_strCfgFile = strFile;
        }

        // ����XML
        try
        {
            // ����XML�ĵ�������
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // ����ĵ�����
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

            // �����豸����
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

            // ���Ϊ����ɹ�
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
     * ��ʼ��ѯָ���豸��״̬
     * 
     * @param tfDevice �豸����
     */
    public boolean StartDeviceStatePoll(IDevice tfDevice)
    {
        // ����������
        if (null == tfDevice)
        {
            return false;
        }

        // �����ǰ�Ѿ������豸״̬��ѯ������ô����ʧ��
        if (null != tfDevice.GetScheduledFuture())
        {
            return false;
        }

        // �����豸״̬��ѯ����
        ScheduledFuture<?> sf = m_tpDeviceStatePoll.scheduleWithFixedDelay(tfDevice, 0, m_nStatePollInterval, TimeUnit.MILLISECONDS);
        if (null == sf)
        {
            return false;
        }

        // �����豸״̬��ѯ����ľ��
        tfDevice.SetScheduledFuture(sf);
        return true;
    }

    /**
     * ֹͣ��ѯָ���豸��״̬
     * 
     * @param tfDevice �豸����
     */
    public boolean StopDeviceStatePoll(IDevice tfDevice)
    {
        // ����������
        if (null == tfDevice)
        {
            return false;
        }

        // ����豸״̬��ѯ����
        ScheduledFuture<?> sf = tfDevice.GetScheduledFuture();
        if (null == sf)
        {
            return false;
        }

        // ֹͣ�豸״̬��ѯ����
        if (!sf.cancel(true))
        {
            return false;
        }

        // ���豸״̬��ѯ�����ÿ�
        tfDevice.SetScheduledFuture(null);

        // ���豸״̬����Ϊ������
        tfDevice.SetDeviceState(enumDeviceState.OffLine);
        return true;
    }

    /**
     * �Ƿ����з����������߳�
     * 
     * @return ����true��ʾ�������з����������߳�
     */
    public boolean isRunServer()
    {
        return m_bServerFlag;
    }

    /**
     * �����Ƿ����з����������߳�
     * 
     * @param bRunFlag false��ʾֹͣ
     */
    public void setRunServer(boolean bRunFlag)
    {
        m_bServerFlag = bRunFlag;
    }

    /**
     * ��ʼ���տͻ��˵�����
     */
    public void StartAcceptConn()
    {
        // ����������socket
        try (ServerSocket sockServer = new ServerSocket(getServerPort());)
        {
            // ����������з����������߳�
            while (isRunServer())
            {
                // ���տͻ�������
                Socket sockConn = sockServer.accept();

                // ����Socket����
                sockConn.setSoTimeout((int) m_nReadTimeout);
                sockConn.setKeepAlive(true);

                // ����ͨ���̣߳����ڴ���������Ϳͻ��˵�ͨ�ű���
                new CLoginHandleThread(this, sockConn).start();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * ֹͣ���տͻ��˵�����
     */
    public void StopAcceptConn()
    {
        setRunServer(false);
    }

    /**
     * ��÷����������˿�
     * @return  �����������˿�
     */
    public int getServerPort()
    {
        return m_iServerPort;
    }

    /**
     * ���÷����������˿�
     * @param iServerPort �����������˿�
     */
    public void setServerPort(int iServerPort)
    {
        if (iServerPort >= 0)
        {
            this.m_iServerPort = iServerPort;
        }
    }

    /**
     * ����豸�����������������룩
     * 
     * @return ����������
     */
    public long getMaxHeartBeatInterval()
    {
        return m_nMaxHeartBeatInterval;
    }

    /**
     * �����豸�����������������룩
     * 
     * @param nMaxHeartBeatInterval ���������������룩
     */
    public void setMaxHeartBeatInterval(long nMaxHeartBeatInterval)
    {
        // ����������
        if (nMaxHeartBeatInterval >= 0)
        {
            m_nMaxHeartBeatInterval = nMaxHeartBeatInterval;
        }
    }

    /**
     * ��÷������ȴ��豸��Ӧ�ĳ�ʱ�����룩
     * 
     * @return �������ȴ��豸��Ӧ�ĳ�ʱ�����룩
     */
    public long getReadTimeout()
    {
        return m_nReadTimeout;
    }

    /**
     * ���÷������ȴ��豸��Ӧ�ĳ�ʱ�����룩
     * 
     * @param nReadTimeout �������ȴ��豸��Ӧ�ĳ�ʱ�����룩
     */
    public void setReadTimeout(long nReadTimeout)
    {
        if (nReadTimeout >= 0)
        {
            this.m_nReadTimeout = nReadTimeout;
        }
    }

    /**
     * ����豸״̬��ѯ��������룩
     * @return  ��ѯ��������룩
     */
    public long geStatePollInterval()
    {
        return m_nStatePollInterval;
    }

    /**
     * �����豸״̬��ѯ��������룩
     * @param nStatePollInterval    �豸״̬��ѯ��������룩
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
 * �ͻ��˵�½������
 * 
 * @author zgm
 *
 */
class CLoginHandleThread extends Thread
{
    private CPowerSystem            powerSystem;                // ����ϵͳ
    private Socket                  sockConn;                   // �������Ϳͻ��˵�����

    
    /**
     * ���캯��
     * 
     * @param powerSystem ����ϵͳ
     * @param sockConn �������Ϳͻ��˵�����
     */
    public CLoginHandleThread(CPowerSystem powerSystem, Socket sockConn)
    {
        this.powerSystem = powerSystem;
        this.sockConn = sockConn;
    }

    /**
     * �߳�ִ�к���
     */
    public void run()
    {
        CDcuDevice tmpDcuDevice = new CDcuDevice();
        CPadDevice tmpPadDevice = new CPadDevice();
        byte[] sentBuffer = new byte[1024];
        IntHolder iSentFrameLen = new IntHolder(0);

        try
        {
            // ���socket���ļ�������
            InputStream inStream = sockConn.getInputStream();
            OutputStream outStream = sockConn.getOutputStream();
            
            // ����һ��ʱ�䣬�ȴ��ͻ��˷��͵�½����
            Thread.sleep(1000);

            // ���տͻ��˵ı���
            byte[] recvBytes = CSocketUtil.readStream(inStream);
            if ((null == recvBytes) || (recvBytes.length == 0))
            {
                return;
            }

            // ����DCU��������֡
            if (tmpDcuDevice.parseIoctlRequest_login(recvBytes, recvBytes.length))
            {
                // ���ָ���߼���ַ���豸�Ƿ��Ѵ���
                String strLogicAddress = tmpDcuDevice.getLogicAddress();
                CDcuDevice device = (CDcuDevice) powerSystem.GetDeviceByLogicAddress(strLogicAddress);
                if (device == null)
                {
                    // �����豸���󣬲���ӵ�ϵͳ��������
                    device = new CDcuDevice(strLogicAddress);
                    powerSystem.AddDevice(device);
                }

                // �����豸��Ϣ
                device.setSockConn(sockConn);
                device.setID(Integer.parseInt(strLogicAddress.substring(6)));
                device.setDevicePort(sockConn.getPort());
                device.SetDeviceIP(sockConn.getInetAddress().getHostAddress());
                device.SetDeviceName(strLogicAddress);
                device.setMasterAddress(tmpDcuDevice.getMasterAddress());
                device.setLastTimeHeartBeat(System.currentTimeMillis());

                // �豸����״̬
                if (device.GetDeviceState() == enumDeviceState.OffLine)
                {
                    device.SetDeviceState(enumDeviceState.Online);
                }

                // �ϳ�DCU������Ӧ֡
                if (device.composeIoctlResponse_login(sentBuffer, iSentFrameLen))
                {
                    // ������Ӧ����
                    outStream.write(sentBuffer, 0, iSentFrameLen.value);
                    outStream.flush();
                }

                // ��ʼ��ѯ�豸״̬
                powerSystem.StartDeviceStatePoll(device);
            }
            // ����Pad��������֡
            else if (tmpPadDevice.parseIoctlRequest_login(recvBytes, recvBytes.length)) 
            {
                // ����Pad����
                CPadDevice pad = new CPadDevice(tmpPadDevice.getLogicAddress());
                pad.setSockConn(sockConn);
                pad.setMasterAddress(tmpPadDevice.getMasterAddress());
                pad.setLastTimeHeartBeat(System.currentTimeMillis());
                
                // �ϳ�DCU������Ӧ֡
                if (pad.composeIoctlResponse_login(sentBuffer, iSentFrameLen))
                {
                    // ������Ӧ����
                    outStream.write(sentBuffer, 0, iSentFrameLen.value);
                    outStream.flush();
                }
                
                // �����̣߳�����Pad����
                new Thread(pad).start();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
