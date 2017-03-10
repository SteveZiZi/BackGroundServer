package com.xtoee.devices.pad;

import java.io.InputStream;
import java.io.OutputStream;

import org.omg.CORBA.IntHolder;

import com.xtoee.devices.CAbstractDevice;
import com.xtoee.devices.CPowerSystem;
import com.xtoee.devices.enumDeviceType;
import com.xtoee.devices.dcu.CDcuDevice;
import com.xtoee.devices.dcu.enumControlCode;
import com.xtoee.devices.dcu.enumDataItem;
import com.xtoee.util.CSocketUtil;
import com.xtoee.util.NumberUtil;

public class CPadDevice extends CAbstractDevice
{
    /**
     * ���캯��
     */
    public CPadDevice()
    {
        this("00000000");
    }
    
    /**
     * ���캯��
     * @param strLogicAddress   �߼���ַ
     */
    public CPadDevice(String strLogicAddress)
    {
        super(strLogicAddress);
        m_eDeviceType = enumDeviceType.PAD;
    }

    /**
     * Pad���������������߳�ʱ��ִ�д˺���
     */
    public void run()
    {
        InputStream     in  = null;
        OutputStream    out = null;
        byte[]          ucBuffer = new byte[65536];
        byte[]          sentBuffer = new byte[65536];
        byte[]          recvBuffer = new byte[65536];
        IntHolder       sentFrameLen = new IntHolder();
        IntHolder       recvFrameLen = new IntHolder();
        
        
        try
        {
            // ���socket���ļ�������
            in = m_sockConn.getInputStream();
            out = m_sockConn.getOutputStream();

            // ѭ������Pad����
            while (true)
            {
                // ����һ��ʱ��
                Thread.sleep(100);
                
                // ����Pad�ı���
                byte[] recvBytes = CSocketUtil.readStream(in);
                if ((null == recvBytes) || (recvBytes.length == 0))
                {
                    // �������֡���
                    CPowerSystem powerSystem = CPowerSystem.GetInstance();
                    if (System.currentTimeMillis() - m_nLastTimeHeartBeat > powerSystem.getMaxHeartBeatInterval())
                    {
                        System.out.println("�ܾ�û�յ�Pad������֡�������ܵ����ˣ���Ӧ�Ĵ����̼߳����رգ�");
                        break;
                    }
                    else 
                    {
                        continue;
                    }
                }
                
                // �������յı���
                int     iTotalParsed = 0;
                IntHolder iParsedLen = new IntHolder(0);
                while (iTotalParsed < recvBytes.length)
                {
                    // ������δ�����ı��ĵ�������
                    System.arraycopy(recvBytes, iTotalParsed, ucBuffer, 0, recvBytes.length - iTotalParsed);

                    // ���������������
                    if ((recvBytes.length - iTotalParsed >= 23)
                            && (ucBuffer[0] == SYMBOL_START)
                            && (ucBuffer[7] == SYMBOL_START)
                            && (ucBuffer[8] == enumControlCode.ReadRequest.getValue())
                            && (ucBuffer[22] == SYMBOL_END))
                    {
                        // �����ѽ����ı��ĳ���
                        iTotalParsed += 23;
                        
                        // ���ָ���߼���ַ��DCU
                        String strLogicAddress = NumberUtil.BcdArrayToString(ucBuffer, 1, 4);
                        CDcuDevice dcu = (CDcuDevice)CPowerSystem.GetInstance().GetDeviceByLogicAddress(strLogicAddress);
                        if (dcu == null)
                        {
                            continue;
                        }
                        
                        // ���ָ�����������Ӧ����
                        int iDataItem = NumberUtil.byte2ToUnsignedShort(ucBuffer, 19);
                        byte[] resp = dcu.getQueryResponseByDataItem(iDataItem);
                        if ((null != resp) && (resp.length > 0))
                        {
                            // ������Ӧ����
                            out.write(resp, 0, resp.length);
                            out.flush();
                        }
                    }
                    // ���澯��־
                    else if ((recvBytes.length - iTotalParsed >= 19)
                            && (ucBuffer[0] == SYMBOL_START)
                            && (ucBuffer[7] == SYMBOL_START)
                            && (ucBuffer[8] == enumControlCode.ReadAlarmLog.getValue())
                            && (ucBuffer[18] == SYMBOL_END))
                    {
                        // �����ѽ����ı��ĳ���
                        iTotalParsed += 19;

                        // ���ָ���߼���ַ��DCU
                        String strLogicAddress = NumberUtil.BcdArrayToString(ucBuffer, 1, 4);
                        CDcuDevice dcu = (CDcuDevice)CPowerSystem.GetInstance().GetDeviceByLogicAddress(strLogicAddress);
                        if (dcu == null)
                        {
                            continue;
                        }

                        // ���ָ�����������Ӧ����
                        byte[] resp = dcu.getQueryResponseByDataItem(enumDataItem.ReadAlarmLog.getValue());
                        if ((null != resp) && (resp.length > 0))
                        {
                            // ������Ӧ����
                            out.write(resp, 0, resp.length);
                            out.flush();
                        }
                    }
                    // д�������
                    else if ((recvBytes.length - iTotalParsed >= 20)
                            && (ucBuffer[0] == SYMBOL_START)
                            && (ucBuffer[7] == SYMBOL_START)
                            && (ucBuffer[8] == enumControlCode.WriteRequest.getValue()))
                    {
                        // �����ѽ����ı��ĳ���
                        int iRequestLen = recvBytes.length - iTotalParsed;
                        iTotalParsed += iRequestLen;

                        // ���ָ���߼���ַ��DCU
                        String strLogicAddress = NumberUtil.BcdArrayToString(ucBuffer, 1, 4);
                        CDcuDevice dcu = (CDcuDevice)CPowerSystem.GetInstance().GetDeviceByLogicAddress(strLogicAddress);
                        if (dcu == null)
                        {
                            continue;
                        }
                        
                        // ��DCU���Ϳ��������������Ӧ����
                        if (dcu.Ioctl(ucBuffer, iRequestLen, recvBuffer, recvFrameLen))
                        {
                            // ��Pad������Ӧ����
                            out.write(recvBuffer, 0, recvFrameLen.value);
                            out.flush();
                        }
                    }
                    // ����DCU����֡
                    else if (parseIoctlRequest_heartBeat(ucBuffer, recvBytes.length - iTotalParsed, iParsedLen))
                    {
                        // �����ѽ����ı��ĳ���
                        iTotalParsed += iParsedLen.value;

                        // �������һ���յ��������ĵĺ�����
                        setLastTimeHeartBeat(System.currentTimeMillis());

                        // �ϳ�����֡��Ӧ����
                        if (composeIoctlResponse_heartBeat(sentBuffer, sentFrameLen))
                        {
                            // ������Ӧ����
                            out.write(sentBuffer, 0, sentFrameLen.value);
                            out.flush();
                        }
                    }
                    // ��������������ʶ�������
                    else 
                    {
                        StringBuffer strBuffer = new StringBuffer();
                        strBuffer.append("��Pad�յ��˲��ܳɹ������ı��ģ�");
                        for(int i = 0; i < recvBytes.length - iTotalParsed; i++)
                        {
                            strBuffer.append(String.format("%02x ", ucBuffer[i]));
                        }
                        
                        System.out.println(strBuffer.toString());
                        break;
                    }
                }
            }
        }
        catch(Exception e)
        {
            StringBuffer sb = new StringBuffer();
            sb.append("Pad������̳߳����쳣��");
            sb.append(e.getMessage());
            System.err.println(sb.toString());
        }
        finally
        {
            // �ر�������
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (Exception e1)
                {
                    e1.printStackTrace();
                }
            }
            
            // �ر������
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (Exception e1)
                {
                    e1.printStackTrace();
                }
            }
            
            // �ر�socket
            if (m_sockConn != null)
            {
                try
                {
                    m_sockConn.close();
                }
                catch (Exception e1)
                {
                    e1.printStackTrace();
                }
                
                m_sockConn = null;
            }
        }
    }

    /**
     * ����DCU��������֡
     * 
     * @param ucBuffer ��Ŵ��������ĵĻ�����
     * @param iFrameLen ����֡�ĳ���
     * @return �����ɹ�����true
     * @throws Exception �쳣��Ϣ
     */
    @Override
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
        if (enumControlCode.PadLoginRequest.getValue() != (ucBuffer[nIndex++] & 0xff))
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
}
