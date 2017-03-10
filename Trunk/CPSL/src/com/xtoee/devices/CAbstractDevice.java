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
 * ʱƵ�豸����
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
    
    protected static final int      SYMBOL_START    = 0x68;     // ֡��ʼ��
    protected static final int      SYMBOL_END      = 0x16;     // ֡������
    
    protected enumDeviceType        m_eDeviceType;              // �豸����
    protected int                   m_nID;                      // �豸ID
    protected String                m_strDeviceName;            // �豸����
    protected String                m_strDeviceIP;              // �豸IP
    protected int                   m_iDevicePort;              // �豸�˿�
    protected String                m_strLogicAddress;          // �߼���ַ
    protected int                   m_iMasterAddress;           // ��վ��ַ
    protected enumDeviceState       m_eDeviceState;             // �豸״̬
    
    protected Socket                m_sockConn;                 // socket����
    protected long                  m_nLastTimeHeartBeat;       // ���һ���յ��������ĵ�ʱ�䣨���룩

    protected ScheduledFuture<?>    m_sfDeviceAttrs;            // �豸״̬��ѯ����
    
    
    /**
     * ���캯��
     * @param strLogicAddress   �߼���ַ
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
     * ���ʱƵ�豸������
     * @return  �豸����
     */
    public enumDeviceType GetDeviceType()
    {
        return m_eDeviceType;
    }
    
    /**
     * ����ʱƵ�豸������
     * @param eType �豸����
     */
    public void SetDeviceType(enumDeviceType eType)
    {
        m_eDeviceType = eType;
    }
    
    /**
     * ����豸ID
     * @return  �豸ID
     */
    public int getID()
    {
        return m_nID;
    }
    
    /**
     * �����豸ID
     * @param nID �豸ID
     */
    public void setID(int nID)
    {
        if (nID > 0)
        {
            m_nID = nID;
        }
    }
    
    /**
     * ����豸����
     * @return  �豸����
     */
    public String getDeviceName()
    {
        return m_strDeviceName;
    }
    
    /**
     * �����豸����
     * @param strName   �豸����
     */
    public void SetDeviceName(String strName)
    {
        m_strDeviceName = strName;
    }
    
    /**
     * ����豸IP��ַ
     * @return  �豸IP��ַ
     */
    public String GetDeviceIP()
    {
        return m_strDeviceIP;
    }
    
    /**
     * �����豸IP��ַ
     * @param strIP �豸IP��ַ
     */
    public void SetDeviceIP(String strIP)
    {
        m_strDeviceIP = strIP;
    }

    /**
     * ����豸ͨ�Ŷ˿ں�
     * @return  �豸�˿ں�
     */
    public int getDevicePort()
    {
        return m_iDevicePort;
    }

    /**
     * �����豸ͨ�Ŷ˿ں�
     * @param iDevicePort   �µ��豸�˿ں�
     */
    public void setDevicePort(int iDevicePort)
    {
        this.m_iDevicePort = iDevicePort;
    }

    /**
     * ����豸�߼���ַ
     * @return  �߼���ַ
     */
    public String getLogicAddress()
    {
        return m_strLogicAddress;
    }

    /**
     * �����豸�߼���ַ
     * @param strLogicAddress   �߼���ַ
     */
    public void setLogicAddress(String strLogicAddress)
    {
        this.m_strLogicAddress = strLogicAddress;
    }

    /**
     * ����豸��վ��ַ
     * @return  ��վ��ַ
     */
    public int getMasterAddress()
    {
        return m_iMasterAddress;
    }

    /**
     * �����豸��վ��ַ
     * @param iMasterAddress    ��վ��ַ
     */
    public void setMasterAddress(int iMasterAddress)
    {
        this.m_iMasterAddress = iMasterAddress;
    }
    
    /**
     * ����豸״̬
     * @return  �豸״̬
     */
    public enumDeviceState GetDeviceState()
    {
        return m_eDeviceState;
    }
    
    /**
     * �����豸״̬
     * @param eNewState �豸״̬
     */
    public void SetDeviceState(enumDeviceState eNewState)
    {
        // ����豸״̬�Ƿ��Ѹ���
        if (m_eDeviceState == eNewState)
        {
            return;
        }
        
        // �����µ��豸״̬
        m_eDeviceState = eNewState;
    }

    /**
     * ��÷��������豸ͨ�ŵ�Socket
     * 
     * @return ���豸ͨ�ŵ�Socket
     */
    public Socket getSockConn()
    {
        return m_sockConn;
    }

    /**
     * ���÷��������豸ͨ�ŵ�Socket
     * 
     * @param sockConn ���豸ͨ�ŵ�Socket
     */
    public void setSockConn(Socket sockConn)
    {
        this.m_sockConn = sockConn;
    }

    /**
     * ������һ���յ��������ĵ�ʱ��
     * 
     * @return �յ���������ʱ�ĺ�����
     */
    public long getLastTimeHeartBeat()
    {
        return m_nLastTimeHeartBeat;
    }

    /**
     * �������һ���յ������ı���ʱ��
     * 
     * @param nLastTimeHeartBeat �յ���������ʱ�ĺ�����
     */
    public void setLastTimeHeartBeat(long nLastTimeHeartBeat)
    {
        this.m_nLastTimeHeartBeat = nLastTimeHeartBeat;
    }
    
    /**
     * ����豸״̬��ѯ����
     * @return  ״̬��ѯ����
     */
    public ScheduledFuture<?> GetScheduledFuture()
    {
        return m_sfDeviceAttrs;
    }
    
    /**
     * �����豸״̬��ѯ����
     * @param sf    ״̬��ѯ����
     */
    public void SetScheduledFuture(ScheduledFuture<?> sf)
    {
        m_sfDeviceAttrs = sf;
    }
    
    /**
     * ��������
     * @param xnDevice  �豸��Ϣ�����ڵ�
     * @return  ���ڳɹ�����true
     */
    public boolean LoadConfig(Element xnDevice)
    {
        // ����������
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
     * ��������
     * @param xmlDoc    XML�ĵ�����
     * @param xnDevice  XML�豸�ڵ����
     * @return  ����ɹ�����true
     */
    public boolean SaveConfig(Document xmlDoc, Element xnDevice)
    {
        // ����������
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
     * �豸״̬��ѯ�������豸״̬��ѯ����������Եĵ��ô˺���
     */
    public abstract void run();

    /**
     * ����У����
     * 
     * @param ucBuffer ����
     * @param iStart ��ʼ����
     * @param iCount ����
     * @return У����
     * @throws Exception �����쳣
     */
    protected byte GenCheckSum(byte[] ucBuffer, int iStart, int iCount) throws Exception
    {
        int             iSum = 0;

        // ����������
        if ((null == ucBuffer) || (iStart < 0) || (iStart + iCount > ucBuffer.length))
        {
            throw new Exception("����GenCheckSum�������������ȷ��");
        }

        // ����������ָ����ʼλ�ú��������ֽڵ��ۼӺ�
        for (int i = 0; i < iCount; i++)
        {
            iSum += ucBuffer[iStart + i];
        }

        // �����ۼӺ͵�����ֽ�
        return (byte) (iSum & 0xFF);
    }

    /**
     * ����DCU��������֡
     * 
     * @param ucBuffer ��Ŵ��������ĵĻ�����
     * @param iFrameLen ����֡�ĳ���
     * @return �����ɹ�����true
     * @throws Exception �쳣��Ϣ
     */
    public boolean parseIoctlRequest_login(byte[] ucBuffer, int iFrameLen) throws Exception
    {
        int             nIndex = 0;
        

        // ����������
        if ((null == ucBuffer) || (iFrameLen < 16) || (ucBuffer.length < iFrameLen))
        {
            return false;
        }

        // ��ʼ�ַ�
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }
        
        // �߼���ַ
        m_strLogicAddress = NumberUtil.BcdArrayToString(ucBuffer, nIndex, 4);
        nIndex += 4;

        // ��վ��ַ
        m_iMasterAddress = ucBuffer[nIndex++];

        // �������
        int iValue = ucBuffer[nIndex++];

        // ��ʼ�ַ�
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // ������
        if ((enumControlCode.LoginResponse.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // ���ݳ���
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // ����
        for (int i = 0; i < 3; i++)
        {
            // �������
            if (0x11 != ucBuffer[nIndex++])
            {
                return false;
            }
        }

        // У���
        iValue = GenCheckSum(ucBuffer, 0, nIndex);
        if (iValue != ucBuffer[nIndex++])
        {
            return false;
        }

        // ֡β
        if (SYMBOL_END != ucBuffer[nIndex++])
        {
            return false;
        }

        return true;
    }

    /**
     * �ϳ�DCU������Ӧ֡
     * 
     * @param ucBuffer �����Ӧ֡�Ļ�����
     * @param iFrameLen ��Ӧ֡�ĳ���
     * @return �ϳɳɹ�����true
     * @throws Exception �쳣��Ϣ
     */
    public boolean composeIoctlResponse_login(byte[] ucBuffer, IntHolder iFrameLen) throws Exception
    {
        int             nIndex = 0;
        

        // ����������
        if ((null == ucBuffer) || (ucBuffer.length < 13))
        {
            return false;
        }

        // ��ʼ�ַ�
        ucBuffer[nIndex++] = (byte) SYMBOL_START;

        // �߼���ַ
        System.arraycopy(NumberUtil.StringToBcdArray(m_strLogicAddress), 0, ucBuffer, nIndex, 4);
        nIndex += 4;

        // ��վ��ַ
        ucBuffer[nIndex++] = (byte) m_iMasterAddress;

        // �������
        ucBuffer[nIndex++] = 0x00;

        // ��ʼ�ַ�
        ucBuffer[nIndex++] = (byte) SYMBOL_START;

        // ������
        ucBuffer[nIndex++] = (byte) enumControlCode.LoginResponse.getValue();

        // ���ݳ���
        System.arraycopy(NumberUtil.ShortToByte2(0), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // У���
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // ֡β
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // ������Ӧ����ĳ���
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * ����DCU�ǳ�����֡
     * 
     * @param ucBuffer ��Ŵ��������ĵĻ�����
     * @param iFrameLen ����֡�ĳ���
     * @param iParsedLen �������ֽ���
     * @return �����ɹ�����true
     * @throws Exception �쳣��Ϣ
     */
    protected boolean parseIoctlRequest_logout(byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen) throws Exception
    {
        int nIndex = 0;

        // ����������
        if ((null == ucBuffer) || (iFrameLen < 13) || (ucBuffer.length < iFrameLen))
        {
            return false;
        }

        // ��ʼ�ַ�
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // �߼���ַ
        if (!m_strLogicAddress.equals(NumberUtil.BcdArrayToString(ucBuffer, nIndex, 4)))
        {
            return false;
        }
        nIndex += 4;

        // ��վ��ַ
        if (m_iMasterAddress != ucBuffer[nIndex++])
        {
            return false;
        }

        // �������
        int iValue = ucBuffer[nIndex++];

        // ��ʼ�ַ�
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // ������
        if ((enumControlCode.LogoutResponse.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // ���ݳ���
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // У���
        iValue = GenCheckSum(ucBuffer, 0, nIndex);
        if (iValue != ucBuffer[nIndex++])
        {
            return false;
        }

        // ֡β
        if (SYMBOL_END != ucBuffer[nIndex++])
        {
            return false;
        }
        
        // �����ѽ����ĳ���
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * �ϳ�DCU�ǳ���Ӧ֡
     * 
     * @param ucBuffer �����Ӧ֡�Ļ�����
     * @param iFrameLen ��Ӧ֡�ĳ���
     * @return �ϳɳɹ�����true
     * @throws Exception �쳣��Ϣ
     */
    protected boolean composeIoctlResponse_logout(byte[] ucBuffer, IntHolder iFrameLen) throws Exception
    {
        int             nIndex = 0;
        

        // ����������
        if ((null == ucBuffer) || (ucBuffer.length < 13))
        {
            return false;
        }

        // ��ʼ�ַ�
        ucBuffer[nIndex++] = (byte) SYMBOL_START;

        // �߼���ַ
        System.arraycopy(NumberUtil.StringToBcdArray(m_strLogicAddress), 0, ucBuffer, nIndex, 4);
        nIndex += 4;

        // ��վ��ַ
        ucBuffer[nIndex++] = (byte) m_iMasterAddress;

        // �������
        ucBuffer[nIndex++] = 0x00;

        // ��ʼ�ַ�
        ucBuffer[nIndex++] = (byte) SYMBOL_START;

        // ������
        ucBuffer[nIndex++] = (byte) enumControlCode.LogoutResponse.getValue();

        // ���ݳ���
        System.arraycopy(NumberUtil.ShortToByte2(0), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // У���
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // ֡β
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // ������Ӧ����ĳ���
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * ��������֡
     * 
     * @param ucBuffer ��Ŵ��������ĵĻ�����
     * @param iFrameLen ����֡�ĳ���
     * @param iParsedLen �������ֽ���
     * @return �����ɹ�����true
     * @throws Exception �쳣��Ϣ
     */
    protected boolean parseIoctlRequest_heartBeat(byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen) throws Exception
    {
        int nIndex = 0;

        // ����������
        if ((null == ucBuffer) || (iFrameLen < 13) || (ucBuffer.length < iFrameLen))
        {
            return false;
        }

        // ��ʼ�ַ�
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // �߼���ַ
        if (!m_strLogicAddress.equals(NumberUtil.BcdArrayToString(ucBuffer, nIndex, 4)))
        {
            return false;
        }
        nIndex += 4;

        // ��վ��ַ
        if (m_iMasterAddress != ucBuffer[nIndex++])
        {
            return false;
        }

        // �������
        int iValue = ucBuffer[nIndex++];

        // ��ʼ�ַ�
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // ������
        if ((enumControlCode.HeartBeatResponse.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // ���ݳ���
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // У���
        iValue = GenCheckSum(ucBuffer, 0, nIndex);
        if (iValue != ucBuffer[nIndex++])
        {
            return false;
        }

        // ֡β
        if (SYMBOL_END != ucBuffer[nIndex++])
        {
            return false;
        }
        
        // �����ѽ����ĳ���
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * �ϳ�DCU������Ӧ֡
     * 
     * @param ucBuffer �����Ӧ֡�Ļ�����
     * @param iFrameLen ��Ӧ֡�ĳ���
     * @return �ϳɳɹ�����true
     * @throws Exception �쳣��Ϣ
     */
    protected boolean composeIoctlResponse_heartBeat(byte[] ucBuffer, IntHolder iFrameLen) throws Exception
    {
        int             nIndex = 0;
        

        // ����������
        if ((null == ucBuffer) || (ucBuffer.length < 13))
        {
            return false;
        }

        // ��ʼ�ַ�
        ucBuffer[nIndex++] = (byte) SYMBOL_START;

        // �߼���ַ
        System.arraycopy(NumberUtil.StringToBcdArray(m_strLogicAddress), 0, ucBuffer, nIndex, 4);
        nIndex += 4;

        // ��վ��ַ
        ucBuffer[nIndex++] = (byte) m_iMasterAddress;

        // �������
        ucBuffer[nIndex++] = 0x00;

        // ��ʼ�ַ�
        ucBuffer[nIndex++] = (byte) SYMBOL_START;

        // ������
        ucBuffer[nIndex++] = (byte) enumControlCode.HeartBeatResponse.getValue();

        // ���ݳ���
        System.arraycopy(NumberUtil.ShortToByte2(0), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // У���
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // ֡β
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // ������Ӧ����ĳ���
        iFrameLen.value = nIndex;
        return true;
    }
}
