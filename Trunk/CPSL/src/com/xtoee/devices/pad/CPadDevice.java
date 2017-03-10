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
     * 构造函数
     */
    public CPadDevice()
    {
        this("00000000");
    }
    
    /**
     * 构造函数
     * @param strLogicAddress   逻辑地址
     */
    public CPadDevice(String strLogicAddress)
    {
        super(strLogicAddress);
        m_eDeviceType = enumDeviceType.PAD;
    }

    /**
     * Pad请求处理函数，创建线程时会执行此函数
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
            // 获得socket的文件输入流
            in = m_sockConn.getInputStream();
            out = m_sockConn.getOutputStream();

            // 循环处理Pad请求
            while (true)
            {
                // 休眠一段时间
                Thread.sleep(100);
                
                // 接收Pad的报文
                byte[] recvBytes = CSocketUtil.readStream(in);
                if ((null == recvBytes) || (recvBytes.length == 0))
                {
                    // 检查心跳帧间隔
                    CPowerSystem powerSystem = CPowerSystem.GetInstance();
                    if (System.currentTimeMillis() - m_nLastTimeHeartBeat > powerSystem.getMaxHeartBeatInterval())
                    {
                        System.out.println("很久没收到Pad的心跳帧，它可能掉线了，对应的处理线程即将关闭！");
                        break;
                    }
                    else 
                    {
                        continue;
                    }
                }
                
                // 解析接收的报文
                int     iTotalParsed = 0;
                IntHolder iParsedLen = new IntHolder(0);
                while (iTotalParsed < recvBytes.length)
                {
                    // 拷贝尚未解析的报文到缓冲区
                    System.arraycopy(recvBytes, iTotalParsed, ucBuffer, 0, recvBytes.length - iTotalParsed);

                    // 读对象参数或数据
                    if ((recvBytes.length - iTotalParsed >= 23)
                            && (ucBuffer[0] == SYMBOL_START)
                            && (ucBuffer[7] == SYMBOL_START)
                            && (ucBuffer[8] == enumControlCode.ReadRequest.getValue())
                            && (ucBuffer[22] == SYMBOL_END))
                    {
                        // 递增已解析的报文长度
                        iTotalParsed += 23;
                        
                        // 获得指定逻辑地址的DCU
                        String strLogicAddress = NumberUtil.BcdArrayToString(ucBuffer, 1, 4);
                        CDcuDevice dcu = (CDcuDevice)CPowerSystem.GetInstance().GetDeviceByLogicAddress(strLogicAddress);
                        if (dcu == null)
                        {
                            continue;
                        }
                        
                        // 获得指定数据项的响应报文
                        int iDataItem = NumberUtil.byte2ToUnsignedShort(ucBuffer, 19);
                        byte[] resp = dcu.getQueryResponseByDataItem(iDataItem);
                        if ((null != resp) && (resp.length > 0))
                        {
                            // 发送响应报文
                            out.write(resp, 0, resp.length);
                            out.flush();
                        }
                    }
                    // 读告警日志
                    else if ((recvBytes.length - iTotalParsed >= 19)
                            && (ucBuffer[0] == SYMBOL_START)
                            && (ucBuffer[7] == SYMBOL_START)
                            && (ucBuffer[8] == enumControlCode.ReadAlarmLog.getValue())
                            && (ucBuffer[18] == SYMBOL_END))
                    {
                        // 递增已解析的报文长度
                        iTotalParsed += 19;

                        // 获得指定逻辑地址的DCU
                        String strLogicAddress = NumberUtil.BcdArrayToString(ucBuffer, 1, 4);
                        CDcuDevice dcu = (CDcuDevice)CPowerSystem.GetInstance().GetDeviceByLogicAddress(strLogicAddress);
                        if (dcu == null)
                        {
                            continue;
                        }

                        // 获得指定数据项的响应报文
                        byte[] resp = dcu.getQueryResponseByDataItem(enumDataItem.ReadAlarmLog.getValue());
                        if ((null != resp) && (resp.length > 0))
                        {
                            // 发送响应报文
                            out.write(resp, 0, resp.length);
                            out.flush();
                        }
                    }
                    // 写对象参数
                    else if ((recvBytes.length - iTotalParsed >= 20)
                            && (ucBuffer[0] == SYMBOL_START)
                            && (ucBuffer[7] == SYMBOL_START)
                            && (ucBuffer[8] == enumControlCode.WriteRequest.getValue()))
                    {
                        // 递增已解析的报文长度
                        int iRequestLen = recvBytes.length - iTotalParsed;
                        iTotalParsed += iRequestLen;

                        // 获得指定逻辑地址的DCU
                        String strLogicAddress = NumberUtil.BcdArrayToString(ucBuffer, 1, 4);
                        CDcuDevice dcu = (CDcuDevice)CPowerSystem.GetInstance().GetDeviceByLogicAddress(strLogicAddress);
                        if (dcu == null)
                        {
                            continue;
                        }
                        
                        // 向DCU发送控制命令，并接受响应报文
                        if (dcu.Ioctl(ucBuffer, iRequestLen, recvBuffer, recvFrameLen))
                        {
                            // 向Pad发送响应报文
                            out.write(recvBuffer, 0, recvFrameLen.value);
                            out.flush();
                        }
                    }
                    // 解析DCU心跳帧
                    else if (parseIoctlRequest_heartBeat(ucBuffer, recvBytes.length - iTotalParsed, iParsedLen))
                    {
                        // 递增已解析的报文长度
                        iTotalParsed += iParsedLen.value;

                        // 设置最近一次收到心跳报文的毫秒数
                        setLastTimeHeartBeat(System.currentTimeMillis());

                        // 合成心跳帧响应报文
                        if (composeIoctlResponse_heartBeat(sentBuffer, sentFrameLen))
                        {
                            // 发送响应报文
                            out.write(sentBuffer, 0, sentFrameLen.value);
                            out.flush();
                        }
                    }
                    // 出现了其他不能识别的命令
                    else 
                    {
                        StringBuffer strBuffer = new StringBuffer();
                        strBuffer.append("从Pad收到了不能成功解析的报文：");
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
            sb.append("Pad命令处理线程出现异常：");
            sb.append(e.getMessage());
            System.err.println(sb.toString());
        }
        finally
        {
            // 关闭输入流
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
            
            // 关闭输出流
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
            
            // 关闭socket
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
     * 解析DCU登入请求帧
     * 
     * @param ucBuffer 存放待解析报文的缓冲区
     * @param iFrameLen 报文帧的长度
     * @return 解析成功返回true
     * @throws Exception 异常信息
     */
    @Override
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
        if (enumControlCode.PadLoginRequest.getValue() != (ucBuffer[nIndex++] & 0xff))
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
}
