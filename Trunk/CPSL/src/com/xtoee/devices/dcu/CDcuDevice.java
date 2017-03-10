package com.xtoee.devices.dcu;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.omg.CORBA.IntHolder;

import com.xtoee.devices.CAbstractDevice;
import com.xtoee.devices.CPowerSystem;
import com.xtoee.devices.enumDeviceState;
import com.xtoee.devices.enumDeviceType;
import com.xtoee.devices.dcu.alarmLog.CAlarmLogBase;
import com.xtoee.devices.dcu.alarmLog.CAlarmLogFactory;
import com.xtoee.devices.dcu.deviceNode.CModuleBase;
import com.xtoee.util.CSocketUtil;
import com.xtoee.util.NumberUtil;

/**
 * DCU�豸
 * 
 * @author zgm
 *
 */
public class CDcuDevice extends CAbstractDevice
{
    protected int                   m_iMeasurePoint;            // ������
    protected String                m_strAdminPhone;            // ����Ա�绰

    protected List<CClosedCircuit>  m_lstClosedCircuit;         // ��·�б�
    protected List<CControlTask>    m_lstControlTasks;          // ���������б�
    protected List<CAlarmLogBase>   m_lstAlarmLogs;             // �澯��־�б�
    protected List<CModuleBase>     m_lstArchives;              // �豸�����б�
    protected Map<Integer, Map<String, Integer>> m_mapCCDimVdb; // ��·��Dim-Vdbӳ���

    protected byte[]                m_ucSentBuffer4Query;       // ��ѯ����ķ��ͻ�����
    protected byte[]                m_ucRecvBuffer4Query;       // ��ѯ����Ľ��ջ�����
    protected byte[]                m_ucSentBuffer4Ioctl;       // ��������ķ��ͻ�����
    protected byte[]                m_ucRecvBuffer4Ioctl;       // ��������Ľ��ջ�����
    
    private Map<Integer, byte[]>     m_mapQueryCmd2RespFrame;    // ��ѯ���������Ӧ����Ӧ֡

    
    /**
     * ���캯��
     */
    public CDcuDevice()
    {
        this("00000000");
    }
    
    /**
     * ���캯��
     * @param strLogicAddress   �߼���ַ
     */
    public CDcuDevice(String strLogicAddress)
    {
        super(strLogicAddress);
        m_eDeviceType = enumDeviceType.DCU;
        m_lstClosedCircuit = new LinkedList<CClosedCircuit>();
        m_lstControlTasks = new LinkedList<CControlTask>();
        m_ucSentBuffer4Query = new byte[65536];
        m_ucRecvBuffer4Query = new byte[65536];
        m_ucSentBuffer4Ioctl = new byte[65536];
        m_ucRecvBuffer4Ioctl = new byte[65536];
        m_mapCCDimVdb = new HashMap<>();
        m_mapQueryCmd2RespFrame = new HashMap<Integer, byte[]>();
    }

    /**
     * ��õ�ѹֵ
     * 
     * @return ��ѹֵ
     */
    public double getVoltage()
    {
        double          dMax = Double.MIN_VALUE;
        double          dTemp = Double.MIN_VALUE;

        
        // ѭ������ÿһ����·
        for (CClosedCircuit closedCircuit : m_lstClosedCircuit)
        {
            // ������л�·�е���ߵ�ѹֵ
            dTemp = closedCircuit.getVoltage();
            if (dTemp > dMax)
            {
                dMax = dTemp;
            }
        }

        return dMax;
    }

    /**
     * ��õ���ֵ
     * 
     * @return ����ֵ
     */
    public double getCurrent()
    {
        double          dTotal = 0;

        // ѭ������ÿһ����·
        for (CClosedCircuit closedCircuit : m_lstClosedCircuit)
        {
            // �ۼӵ���ֵ
            dTotal += closedCircuit.getCurrent();
        }

        return dTotal;
    }

    /**
     * ��ù���ֵ
     * 
     * @return ����
     */
    public double getPower()
    {
        return getVoltage() * getCurrent();
    }

    /**
     * ����¶�ֵ
     * 
     * @return �¶�ֵ
     */
    public double getTemperature()
    {
        double          dMax = Double.NEGATIVE_INFINITY;
        double          dTemp = Double.NEGATIVE_INFINITY;

        
        // ѭ������ÿһ����·
        for (CClosedCircuit closedCircuit : m_lstClosedCircuit)
        {
            // ������л�·�е�����¶�ֵ
            dTemp = closedCircuit.getTemperature();
            if (dTemp > dMax)
            {
                dMax = dTemp;
            }
        }

        return dMax;
    }

    /**
     * ����豸״̬
     * 
     * @return �豸״̬
     */
    public String getStatus()
    {
        // ����豸�Ƿ�����
        if (m_eDeviceState == enumDeviceState.OffLine)
        {
            return "������";
        }

        // ѭ�����ÿһ����·��״̬
        for (CClosedCircuit closedCircuit : m_lstClosedCircuit)
        {
            String strStatus = closedCircuit.getStatus();
            if ((null != strStatus) && (0 != strStatus.compareToIgnoreCase("����")))
            {
                return "�쳣";
            }
        }

        return "����";
    }

    /**
     * ��ù���Ա�绰����
     * 
     * @return ����Ա�绰
     */
    public String getAdminPhone()
    {
        return m_strAdminPhone;
    }

    /**
     * ��û�·�б�
     * 
     * @return ��·�б�
     */
    public List<CClosedCircuit> getClosedCircuitList()
    {
        return m_lstClosedCircuit;
    }

    /**
     * ���ָ��ID�ŵĻ�·����
     * 
     * @param nId ��·ID
     * @return ��·����
     */
    public CClosedCircuit getClosedCircuitById(int nId)
    {
        // ѭ������ÿһ����·
        for (CClosedCircuit closedCircuit : m_lstClosedCircuit)
        {
            // ���ID���Ƿ�ƥ��
            if (closedCircuit.getID() == nId)
            {
                return closedCircuit;
            }
        }

        return null;
    }

    /**
     * ��ָ���Ļ�·�б��в���ָ��ID�ŵĻ�·
     * 
     * @param lstClosedCircuit ��·�б�
     * @param nId ��·ID��
     * @return ��·����
     */
    private CClosedCircuit getClosedCircuitById(List<CClosedCircuit> lstClosedCircuit, int nId)
    {
        // ����������
        if (null == lstClosedCircuit)
        {
            return null;
        }

        // ѭ������ÿһ����·
        for (CClosedCircuit closedCircuit : lstClosedCircuit)
        {
            // ���ID���Ƿ�ƥ��
            if (closedCircuit.getID() == nId)
            {
                return closedCircuit;
            }
        }

        return null;
    }

    /**
     * ��ӻ�·����������
     * 
     * @param closedCircuit ��·����
     * @return �ɹ�����true
     */
    public boolean AddClosedCircuit(CClosedCircuit closedCircuit)
    {
        // ����·�����Ƿ� �Ѿ�����
        if ((null == closedCircuit) || (null != getClosedCircuitById(closedCircuit.getID())))
        {
            return false;
        }

        // ��ӻ�·����������
        m_lstClosedCircuit.add(closedCircuit);
        return true;
    }

    /**
     * ��ָ���Ļ�·�б���ӻ�·����
     * 
     * @param lstClosedCircuit ��·�б�
     * @param closedCircuit ��·����
     * @return �ɹ�����true
     */
    private boolean AddClosedCircuit(List<CClosedCircuit> lstClosedCircuit, CClosedCircuit closedCircuit)
    {
        // ����·�����Ƿ� �Ѿ�����
        if ((null == lstClosedCircuit) || (null == closedCircuit) || (null != getClosedCircuitById(lstClosedCircuit, closedCircuit.getID())))
        {
            return false;
        }

        // ��ӻ�·����������
        lstClosedCircuit.add(closedCircuit);
        return true;
    }

    /**
     * ɾ�����еĻ�·����
     */
    public void DeleteAllClosedCircuits()
    {
        m_lstClosedCircuit.clear();
    }

    /**
     * ��ÿ��������б�
     * 
     * @return ���������б�
     */
    public List<CControlTask> getContorlTaskList()
    {
        return m_lstControlTasks;
    }

    /**
     * ���ÿ��������б�
     * 
     * @param lstTasks ���������б�
     */
    public void setControlTaskList(List<CControlTask> lstTasks)
    {
        m_lstControlTasks = lstTasks;
    }

    /**
     * ��ø澯��־�б�
     * 
     * @return �澯��־�б�
     */
    public List<CAlarmLogBase> getAlarmLogList()
    {
        return m_lstAlarmLogs;
    }

    /**
     * ���ø澯��־�б�
     * 
     * @param lstAlarmLogs �澯��־�б�
     */
    public void setAlarmLogList(List<CAlarmLogBase> lstAlarmLogs)
    {
        m_lstAlarmLogs = lstAlarmLogs;
    }

    /**
     * ����豸�����б�
     * 
     * @return �豸�����б�
     */
    public List<CModuleBase> getArchiveList()
    {
        return m_lstArchives;
    }

    /**
     * ���Ĭ�ϵ�Dim-Vdbӳ���
     * 
     * @return Dim-Vdbӳ���
     */
    private Map<String, Integer> getDefaultDimVdbMap()
    {
        Map<String, Integer> mapDimVdb = null;
        
        
        mapDimVdb = new HashMap<String, Integer>();
        mapDimVdb.put("100", 300);
        mapDimVdb.put("90", 289);
        mapDimVdb.put("80", 278);
        mapDimVdb.put("70", 267);
        mapDimVdb.put("60", 256);
        mapDimVdb.put("50", 245);
        mapDimVdb.put("40", 234);
        mapDimVdb.put("30", 222);
        mapDimVdb.put("20", 211);
        mapDimVdb.put("10", 201);
        mapDimVdb.put("0", 190);

        return mapDimVdb;
    }

    /**
     * ���ݻ�·�ţ���øû�·��Ӧ��Dim-Vdb��
     * 
     * @param iClosedCircuitId ��·��
     * @return ��·��Ӧ��Dim-Vdb��
     */
    public Map<String, Integer> getDimVdbMapByCCId(int iClosedCircuitId)
    {
        Map<String, Integer> mapDimVdb = null;
        
        
        // ���ָ����·��Dim-Vdbӳ���
        mapDimVdb = m_mapCCDimVdb.get(iClosedCircuitId);
        if ((null == mapDimVdb) && (null != getClosedCircuitById(iClosedCircuitId)))
        {
            // �����û��Ϊָ��Id�Ļ�·����Dim-Vdb����ô������
            mapDimVdb = getDefaultDimVdbMap();

            // ���½���Dim-Vdb����ӵ�������
            m_mapCCDimVdb.put(iClosedCircuitId, mapDimVdb);
        }

        return mapDimVdb;
    }

    /**
     * ���ݻ�·��ѹ����ö�Ӧ��Dimֵ
     * 
     * @param iCircuitId ��·��
     * @param iVoltage ��·��ѹ
     * @return ��·��ѹ��Ӧ��Dimֵ
     */
    public String getDimByCircuitVoltage(int iCircuitId, int iVoltage)
    {
        Map<String, Integer> mapDimVdb = null;
        
        
        // ���ָ����·��Dim-Vdbӳ���
        mapDimVdb = m_mapCCDimVdb.get(iCircuitId);
        if (null == mapDimVdb)
        {
            // �����û��Ϊָ��Id�Ļ�·����Dim-Vdb����ô������
            mapDimVdb = getDefaultDimVdbMap();
        }

        // ѭ������Map����
        for (Entry<String, Integer> entry : mapDimVdb.entrySet())
        {
            // ���ƥ��ĵ�ѹ�ڵ�
            if (entry.getValue() == iVoltage)
            {
                return entry.getKey();
            }
        }

        return "";
    }

    /**
     * �豸״̬��ѯ�������豸״̬��ѯ����������Եĵ��ô˺���
     */
    public void run()
    {
        try
        {
            // ���socket�ѹر�
            if (CSocketUtil.isSocketClosed(m_sockConn))
            {
                // ����豸Ϊ������
                m_eDeviceState = enumDeviceState.OffLine;
                
                // �������֡���
                CPowerSystem powerSystem = CPowerSystem.GetInstance();
                if (System.currentTimeMillis() - m_nLastTimeHeartBeat > powerSystem.getMaxHeartBeatInterval())
                {
                    // ֹͣ�豸״̬��ѯ�������豸״̬����Ϊ������
                    powerSystem.StopDeviceStatePoll(this);
                }
                
                return;
            }

            // ���������������̵�������Ϣ
            List<CClosedCircuit> lstClosedCircuit = new LinkedList<CClosedCircuit>();
            if (Query(enumDataItem.ReadAllRectifier.toString(), null, lstClosedCircuit)
                    && Query(enumDataItem.ReadAllRelay.toString(), null, lstClosedCircuit))
            {
                // �����µĻ�·����
                m_lstClosedCircuit = lstClosedCircuit;
                m_eDeviceState = enumDeviceState.Online;
            }

            // ����һ��ʱ�䣬���ÿ��������л����õ���
            Thread.sleep(200);

            // ��������б�
            List<CControlTask> lstControlTasks = new LinkedList<CControlTask>();
            boolean bFlag1 = Query(enumDataItem.ReadSingleControlTask_1.toString(), null, lstControlTasks);
            boolean bFlag2 = Query(enumDataItem.ReadSingleControlTask_2.toString(), null, lstControlTasks);
            boolean bFlag3 = Query(enumDataItem.ReadSingleControlTask_3.toString(), null, lstControlTasks);
            boolean bFlag4 = Query(enumDataItem.ReadSingleControlTask_4.toString(), null, lstControlTasks);
            boolean bFlag5 = Query(enumDataItem.ReadSingleControlTask_5.toString(), null, lstControlTasks);
            boolean bFlag6 = Query(enumDataItem.ReadSingleControlTask_6.toString(), null, lstControlTasks);
            boolean bFlag7 = Query(enumDataItem.ReadSingleControlTask_7.toString(), null, lstControlTasks);
            boolean bFlag8 = Query(enumDataItem.ReadSingleControlTask_8.toString(), null, lstControlTasks);
            boolean bFlag9 = Query(enumDataItem.ReadSingleControlTask_9.toString(), null, lstControlTasks);
            boolean bFlag10 = Query(enumDataItem.ReadSingleControlTask_10.toString(), null, lstControlTasks);
            boolean bFlag11 = Query(enumDataItem.ReadSingleControlTask_11.toString(), null, lstControlTasks);
            boolean bFlag12 = Query(enumDataItem.ReadSingleControlTask_12.toString(), null, lstControlTasks);
            if (bFlag1 || bFlag2 || bFlag3 || bFlag4 || bFlag5 || bFlag6 || 
                bFlag7 || bFlag8 || bFlag9 || bFlag10 || bFlag11 || bFlag12)
            {
                // ������������б�
                m_lstControlTasks = lstControlTasks;
                m_eDeviceState = enumDeviceState.Online;
            }
            
            /* 
            // ������ȡ�����б�����������ȡ�����б�ʱDCU����Ӧ���Ŀ��ܺܳ��������ݲ�ʹ������
            if (Query(enumDataItem.ReadAllControlTask.toString(), null, lstControlTasks))
            {
                // ������������б�
                m_lstControlTasks = lstControlTasks;
                m_eDeviceState = enumDeviceState.Online;
            }
            */

            // ����һ��ʱ�䣬���ÿ��������л����õ���
            Thread.sleep(200);

            // ���澯��־
            List<CAlarmLogBase> lstAlarmLogs = new LinkedList<CAlarmLogBase>();
            if (Query(enumDataItem.ReadAlarmLog.toString(), new CQueryParam_ReadAlarmLog(new Date(), 0xff), lstAlarmLogs))
            {
                // ����澯��־�б�
                m_lstAlarmLogs = lstAlarmLogs;
                m_eDeviceState = enumDeviceState.Online;
            }

            // ����һ��ʱ�䣬���ÿ��������л����õ���
            Thread.sleep(200);

            // ���豸����
            List<CModuleBase> lstArchives = new LinkedList<CModuleBase>();
            if (Query(enumDataItem.ReadAllArchives.toString(), null, lstArchives))
            {
                // �����豸�����б�
                m_lstArchives = lstArchives;
                m_eDeviceState = enumDeviceState.Online;
            }

            // ����һ��ʱ�䣬���ÿ��������л����õ���
            Thread.sleep(200);

            // ��ȡ����Ա�绰
            StringBuffer strAdminPhone = new StringBuffer();
            if (Query(enumDataItem.AdminPhone.toString(), null, strAdminPhone))
            {
                // �������Ա�绰
                m_strAdminPhone = strAdminPhone.toString();
                m_eDeviceState = enumDeviceState.Online;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * ��ѯ
     * 
     * @param strCmdType ��������
     * @param inParam �������
     * @param outParam �������
     * @return �ɹ�����true
     * @throws Exception socket����ʧ��ʱ�׳��쳣
     */
    public synchronized boolean Query(String strCmdType, Object inParam, Object outParam)
    {
        boolean         bFlag = false;
        int             iRecvFrameLen = 0;
        

        try
        {
            // ������������
            InputStream inStream = m_sockConn.getInputStream();
            OutputStream outStream = m_sockConn.getOutputStream();

            // �ȴ��������еĵǳ�֡������֡
            HandleSpecialRequest();

            // ���ָ�����͵Ĳ�ѯ����
            IntHolder iSentFrameLen = new IntHolder(0);
            if (!getQueryCommand(strCmdType, m_ucSentBuffer4Query, iSentFrameLen, inParam))
            {
                return false;
            }

            // ���socket���ջ�����
            CSocketUtil.clearRecvBuffer(m_sockConn);

            // ���豸���Ͳ�ѯ����
            outStream.write(m_ucSentBuffer4Query, 0, iSentFrameLen.value);
            outStream.flush();

            // ���ղ�ѯ��Ӧ
            iRecvFrameLen = inStream.read(m_ucRecvBuffer4Query, 0, m_ucRecvBuffer4Query.length);
            if (iRecvFrameLen == 0)
            {
                return false;
            }
            
            // �������յı���
            int     iTotalParsed = 0;
            byte[]  tmpBuffer = new byte[iRecvFrameLen];
            IntHolder iParsedLen = new IntHolder(0);
            while (iTotalParsed < iRecvFrameLen)
            {
                // ������δ�����ı��ĵ�������
                System.arraycopy(m_ucRecvBuffer4Query, iTotalParsed, tmpBuffer, 0, iRecvFrameLen - iTotalParsed);

                // ����ָ�������������Ӧ����
                if (parseQueryResponse(strCmdType, tmpBuffer, iRecvFrameLen - iTotalParsed, iParsedLen, outParam))
                {
                    // �����ѽ����ı��ĳ���
                    iTotalParsed += iParsedLen.value;
                    
                    // �����ѯ���������Ӧ����Ӧ֡
                    byte[] resp = new byte[iParsedLen.value];
                    System.arraycopy(tmpBuffer, 0, resp, 0, iParsedLen.value);
                    m_mapQueryCmd2RespFrame.put(enumDataItem.valueOf(strCmdType).getValue(), resp);
                    
                    // ���÷��ر�־
                    bFlag = true;
                }
                // ����DCU�ǳ�����֡
                else if (parseIoctlRequest_logout(tmpBuffer, iRecvFrameLen - iTotalParsed, iParsedLen))
                {
                    // �����ѽ����ı��ĳ���
                    iTotalParsed += iParsedLen.value;

                    // �޸��豸��Ϣ
                    SetDeviceState(enumDeviceState.OffLine);

                    // �ϳ�DCU�ǳ��������Ӧ����
                    if (composeIoctlResponse_logout(m_ucSentBuffer4Ioctl, iSentFrameLen))
                    {
                        // ������Ӧ����
                        outStream.write(m_ucSentBuffer4Ioctl, 0, iSentFrameLen.value);
                        outStream.flush();
                    }
                }
                // ����DCU����֡
                else if (parseIoctlRequest_heartBeat(tmpBuffer, iRecvFrameLen - iTotalParsed, iParsedLen))
                {
                    // �����ѽ����ı��ĳ���
                    iTotalParsed += iParsedLen.value;

                    // �������һ���յ��������ĵĺ�����
                    setLastTimeHeartBeat(System.currentTimeMillis());
                    if (GetDeviceState() == enumDeviceState.OffLine)
                    {
                        SetDeviceState(enumDeviceState.Online);
                    }

                    // �ϳ�����֡��Ӧ����
                    if (composeIoctlResponse_heartBeat(m_ucSentBuffer4Ioctl, iSentFrameLen))
                    {
                        // ������Ӧ����
                        outStream.write(m_ucSentBuffer4Ioctl, 0, iSentFrameLen.value);
                        outStream.flush();
                    }
                }
                // ��������������ʶ�������
                else 
                {
                    StringBuffer strBuffer = new StringBuffer();
                    strBuffer.append("����");
                    strBuffer.append(strCmdType);
                    strBuffer.append("��ѯ���ȴ�յ��˲�ƥ���������ɹ�����Ӧ���ģ�");
                    for(int i = 0; i < iRecvFrameLen; i++)
                    {
                        strBuffer.append(String.format("%02x ", m_ucRecvBuffer4Query[i]));
                    }
                    
                    System.out.println(strBuffer.toString());
                    break;
                }
            }
        }
        catch (Exception e)
        {
            StringBuffer buffer = new StringBuffer();
            
            if (iRecvFrameLen > 0)
            {
                buffer.append("���������쳣��");
                for(int i = 0; i < iRecvFrameLen; i++)
                {
                    buffer.append(String.format("%02x ", m_ucRecvBuffer4Query[i]));
                }
                
                buffer.append("��");
            }
            buffer.append("�쳣��Ϣ��");
            buffer.append(e.getMessage());
            
            System.out.println(buffer.toString());
            return false;
        }

        return bFlag;
    }

    /**
     * ���ָ�����͵Ĳ�ѯ����
     * 
     * @param strCmdType ��ѯ��������
     * @param ucBuffer ��Ų�ѯ����Ļ�����
     * @param iFrameLen ��ѯ����ĳ���
     * @param inParam �������
     * @return �ɹ�����true
     */
    protected boolean getQueryCommand(String strCmdType, byte[] ucBuffer, IntHolder iFrameLen, Object inParam) throws Exception
    {
        boolean         bRet = false;
        enumDataItem    eCmdType;
        

        // �����������ͣ�ִ����Ӧ�Ĳ�ѯ����ϳɺ���
        eCmdType = enumDataItem.valueOf(strCmdType);
        switch (eCmdType)
        {
        // ���澯��־(0x0019)
        case ReadAlarmLog:
            bRet = getQueryCommand_ReadAlarmLog(ucBuffer, iFrameLen, inParam);
            break;

        // ������Ա�绰(0x010D)
        case AdminPhone:
            bRet = getQueryCommand_ReadAdminPhone(ucBuffer, iFrameLen);
            break;

        // �������ӻ�·(0x0202)
        case ReadAllRelay:
            bRet = getQueryCommand_ReadAllRelay(ucBuffer, iFrameLen);
            break;

        // ������������(0x040F)
        case ReadAllRectifier:
            bRet = getQueryCommand_ReadAllRectifier(ucBuffer, iFrameLen);
            break;

        // ��������������0x0511 ~ 0x051C��
        case ReadSingleControlTask_1:
        case ReadSingleControlTask_2:
        case ReadSingleControlTask_3:
        case ReadSingleControlTask_4:
        case ReadSingleControlTask_5:
        case ReadSingleControlTask_6:
        case ReadSingleControlTask_7:
        case ReadSingleControlTask_8:
        case ReadSingleControlTask_9:
        case ReadSingleControlTask_10:
        case ReadSingleControlTask_11:
        case ReadSingleControlTask_12:
            bRet = getQueryCommand_ReadSingleControlTask(eCmdType, ucBuffer, iFrameLen);
            break;

        // �����еĿ�������(0x051f)
        case ReadAllControlTask:
            bRet = getQueryCommand_ReadAllControlTask(ucBuffer, iFrameLen);
            break;

        // �����豸������Ϣ(0x897F)
        case ReadAllArchives:
            bRet = getQueryCommand_ReadArchives(ucBuffer, iFrameLen, inParam);
            break;

        default:
            break;
        }

        return bRet;
    }

    /**
     * ����ָ�����Ͳ�ѯ�������Ӧ֡
     * 
     * @param strCmdType ��ѯ��������
     * @param ucBuffer ��ѯ�������Ӧ֡
     * @param iFrameLen ��ѯ������Ӧ֡�ĳ���
     * @param iParsedLen ���������ֽ���
     * @param outParam �����������Ķ���
     * @return �ɹ�����true
     */
    protected boolean parseQueryResponse(String strCmdType, byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, Object outParam)
    {
        boolean         bRet = false;
        enumDataItem    eCmdType;
        

        // �����������ͣ�ִ����Ӧ�Ĳ�ѯ����ϳɺ���
        eCmdType = enumDataItem.valueOf(strCmdType);
        switch (eCmdType)
        {
        // ���澯��־
        case ReadAlarmLog:
            bRet = parseQueryResponse_ReadAlarmLog(ucBuffer, iFrameLen, iParsedLen, outParam);
            break;

        // ������Ա�绰
        case AdminPhone:
            bRet = parseQueryResponse_ReadAdminPhone(ucBuffer, iFrameLen, iParsedLen, outParam);
            break;

        // �������ӻ�·
        case ReadAllRelay:
            bRet = parseQueryResponse_ReadAllRelay(ucBuffer, iFrameLen, iParsedLen, outParam);
            break;

        // ������������
        case ReadAllRectifier:
            bRet = parseQueryResponse_ReadAllRectifier(ucBuffer, iFrameLen, iParsedLen, outParam);
            break;
       
        // ��������������0x0511 ~ 0x051C��
        case ReadSingleControlTask_1:
        case ReadSingleControlTask_2:
        case ReadSingleControlTask_3:
        case ReadSingleControlTask_4:
        case ReadSingleControlTask_5:
        case ReadSingleControlTask_6:
        case ReadSingleControlTask_7:
        case ReadSingleControlTask_8:
        case ReadSingleControlTask_9:
        case ReadSingleControlTask_10:
        case ReadSingleControlTask_11:
        case ReadSingleControlTask_12:
            bRet = parseQueryResponse_ReadSingleControlTask(eCmdType, ucBuffer, iFrameLen, iParsedLen, outParam);
            break;

        // �����еĿ�������
        case ReadAllControlTask:
            bRet = parseQueryResponse_ReadAllControlTask(ucBuffer, iFrameLen, iParsedLen, outParam);
            break;

        // �����豸������Ϣ
        case ReadAllArchives:
            bRet = parseQueryResponse_ReadArchives(ucBuffer, iFrameLen, iParsedLen, outParam);
            break;

        default:
            break;
        }

        return bRet;
    }

    /**
     * ��ö�ȡ����Ա�绰������
     * 
     * @param ucBuffer ��Ų�ѯ����Ļ�����
     * @param iFrameLen ��ѯ����ĳ���
     * @return �ɹ�����true
     * @throws Exception �쳣��Ϣ
     */
    protected boolean getQueryCommand_ReadAdminPhone(byte[] ucBuffer, IntHolder iFrameLen) throws Exception
    {
        int             nIndex = 0;
        

        // ����������
        if ((null == ucBuffer) || ucBuffer.length < 23)
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
        ucBuffer[nIndex++] = (byte) enumControlCode.ReadRequest.getValue();

        // ���ݳ���
        System.arraycopy(NumberUtil.ShortToByte2(10), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // �������־
        System.arraycopy(NumberUtil.longToByte8(1), 0, ucBuffer, nIndex, 8);
        nIndex += 8;

        // ��������
        System.arraycopy(NumberUtil.ShortToByte2(enumDataItem.AdminPhone.getValue()), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // У���
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // ֡β
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // ���ò�ѯ����ĳ���
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * ������ȡ����Ա�绰�������Ӧ֡
     * 
     * @param ucBuffer ��ѯ�������Ӧ֡
     * @param iFrameLen ��ѯ������Ӧ֡�ĳ���
     * @param iParsedLen ���������ֽ���
     * @param outParam �������
     * @return �ɹ�����true
     */
    protected boolean parseQueryResponse_ReadAdminPhone(byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, Object outParam)
    {
        int             nIndex = 0;
        @SuppressWarnings("unused")
        long            iValue = 0;
        

        // ����������
        if ((null == ucBuffer) || (iFrameLen < 29) || (ucBuffer.length < iFrameLen) || !(outParam instanceof StringBuffer))
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
        iValue = ucBuffer[nIndex++];

        // ��ʼ�ַ�
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // ������
        if ((enumControlCode.ReadRequest.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // ���ݳ���
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // �������־
        iValue = NumberUtil.Byte8ToLong(ucBuffer, nIndex);
        nIndex += 8;

        // ��������
        if (enumDataItem.AdminPhone.getValue() != NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex))
        {
            return false;
        }
        nIndex += 2;

        // ����Ա�绰
        StringBuffer strAdminPhone = (StringBuffer) outParam;
        String strValue = NumberUtil.BcdArrayToString(ucBuffer, nIndex, 6);
        if (null != strValue)
        {
            // ע��ȥ����ͷ���ַ���a��
            strAdminPhone.append(strValue.replaceFirst("^a+", ""));
        }
        nIndex += 6;
        
        // У��
        iValue = ucBuffer[nIndex++];
        
        // ֡β
        iValue = ucBuffer[nIndex++];
        
        // �����ѽ����ĳ���
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * ��ö�ȡ�����ӻ�·��Ϣ������
     * 
     * @param ucBuffer ��Ų�ѯ����Ļ�����
     * @param iFrameLen ��ѯ����ĳ���
     * @return �ɹ�����true
     */
    protected boolean getQueryCommand_ReadAllRelay(byte[] ucBuffer, IntHolder iFrameLen) throws Exception
    {
        int             nIndex = 0;

        
        // ����������
        if ((null == ucBuffer) || ucBuffer.length < 23)
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
        ucBuffer[nIndex++] = (byte) enumControlCode.ReadRequest.getValue();

        // ���ݳ���
        System.arraycopy(NumberUtil.ShortToByte2(10), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // �������־
        System.arraycopy(NumberUtil.longToByte8(1), 0, ucBuffer, nIndex, 8);
        nIndex += 8;

        // ��������
        System.arraycopy(NumberUtil.ShortToByte2(enumDataItem.ReadAllRelay.getValue()), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // У���
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // ֡β
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // ���ò�ѯ����ĳ���
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * ������ȡ�����ӻ�·��Ϣ����Ӧ֡
     * 
     * @param ucBuffer ��ѯ�������Ӧ֡
     * @param iFrameLen ��ѯ������Ӧ֡�ĳ���
     * @param iParsedLen ���������ֽ���
     * @param outParam �������
     * @return �ɹ�����true
     */
    @SuppressWarnings("unchecked")
    protected boolean parseQueryResponse_ReadAllRelay(byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, Object outParam)
    {
        int             nIndex = 0;
        long            iValue = 0;
        int             LENGTH_HEADER = 22;
        int             LENGTH_RECTIFIER = 10;
        int             LENGTH_TAIL = 2;
        

        // ����������
        if ((null == ucBuffer) || (iFrameLen < LENGTH_HEADER) || (ucBuffer.length < iFrameLen) || !(outParam instanceof LinkedList))
        {
            return false;
        }
        LinkedList<CClosedCircuit> lstClosedCircuit = (LinkedList<CClosedCircuit>) outParam;

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
        iValue = ucBuffer[nIndex++];

        // ��ʼ�ַ�
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // ������
        if ((enumControlCode.ReadRequest.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // ���ݳ���
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // �������־
        iValue = NumberUtil.Byte8ToLong(ucBuffer, nIndex);
        nIndex += 8;

        // ��������
        if (enumDataItem.ReadAllRelay.getValue() != NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex))
        {
            return false;
        }
        nIndex += 2;

        // �̵�����
        iValue = ucBuffer[nIndex++];
        if ((iFrameLen < (LENGTH_HEADER + iValue * LENGTH_RECTIFIER + LENGTH_TAIL)) || (ucBuffer.length < iFrameLen))
        {
            return false;
        }

        // ����ÿһ���̵�����״̬
        for (int i = 0; i < iValue; i++, nIndex += LENGTH_RECTIFIER)
        {
            int iRelayId = ucBuffer[nIndex];
            int iClosedCircuitId = ucBuffer[nIndex + 6];

            // ���ָ��ID�Ļ�·����
            CClosedCircuit closedCircuit = getClosedCircuitById(lstClosedCircuit, iClosedCircuitId);
            if (null == closedCircuit)
            {
                closedCircuit = new CClosedCircuit(iClosedCircuitId);
            }

            // ���ָ��ID�ļ̵�������
            CRelay relay = closedCircuit.getRelayById(iRelayId);
            if (null == relay)
            {
                relay = new CRelay(iRelayId);
            }

            // �̵����������״̬��Ϣ
            if (!relay.parseFrame(ucBuffer, nIndex))
            {
                continue;
            }

            // ��Ӽ̵������󵽻�·��
            closedCircuit.AddRelay(relay);

            // ��ӻ�·��������
            AddClosedCircuit(lstClosedCircuit, closedCircuit);
        }
        
        // У��
        iValue = ucBuffer[nIndex++];
        
        // ֡β
        iValue = ucBuffer[nIndex++];
        
        // �����ѽ����ĳ���
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * ��ö�ȡ������������Ϣ������
     * 
     * @param ucBuffer ��Ų�ѯ����Ļ�����
     * @param iFrameLen ��ѯ����ĳ���
     * @return �ɹ�����true
     */
    protected boolean getQueryCommand_ReadAllRectifier(byte[] ucBuffer, IntHolder iFrameLen) throws Exception
    {
        int             nIndex = 0;
        

        // ����������
        if ((null == ucBuffer) || ucBuffer.length < 23)
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
        ucBuffer[nIndex++] = (byte) enumControlCode.ReadRequest.getValue();

        // ���ݳ���
        System.arraycopy(NumberUtil.ShortToByte2(10), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // �������־
        System.arraycopy(NumberUtil.longToByte8(1), 0, ucBuffer, nIndex, 8);
        nIndex += 8;

        // ��������
        System.arraycopy(NumberUtil.ShortToByte2(enumDataItem.ReadAllRectifier.getValue()), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // У���
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // ֡β
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // ���ò�ѯ����ĳ���
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * ������ȡ������������Ϣ����Ӧ֡
     * 
     * @param ucBuffer ��ѯ�������Ӧ֡
     * @param iFrameLen ��ѯ������Ӧ֡�ĳ���
     * @param iParsedLen ���������ֽ���
     * @param outParam �������
     * @return �ɹ�����true
     */
    @SuppressWarnings("unchecked")
    protected boolean parseQueryResponse_ReadAllRectifier(byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, Object outParam)
    {
        int             nIndex = 0;
        long            iValue = 0;
        int             LENGTH_HEADER = 22;
        int             LENGTH_RECTIFIER = 12;
        int             LENGTH_TAIL = 2;

        
        // ����������
        if ((null == ucBuffer) || (iFrameLen < LENGTH_HEADER) || (ucBuffer.length < iFrameLen) || !(outParam instanceof LinkedList))
        {
            return false;
        }
        LinkedList<CClosedCircuit> lstClosedCircuit = (LinkedList<CClosedCircuit>) outParam;

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
        iValue = ucBuffer[nIndex++];

        // ��ʼ�ַ�
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // ������
        if ((enumControlCode.ReadRequest.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // ���ݳ���
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // �������־
        iValue = NumberUtil.Byte8ToLong(ucBuffer, nIndex);
        nIndex += 8;

        // ��������
        if (enumDataItem.ReadAllRectifier.getValue() != NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex))
        {
            return false;
        }
        nIndex += 2;

        // ��������
        iValue = ucBuffer[nIndex++];
        if ((iFrameLen < (LENGTH_HEADER + iValue * LENGTH_RECTIFIER + LENGTH_TAIL)) || (ucBuffer.length < iFrameLen))
        {
            return false;
        }

        // ����ÿһ����������״̬
        for (int i = 0; i < iValue; i++, nIndex += LENGTH_RECTIFIER)
        {
            int iRectifierId = ucBuffer[nIndex];
            int iClosedCircuitId = ucBuffer[nIndex + 2];

            // ���ָ��ID�Ļ�·����
            CClosedCircuit closedCircuit = getClosedCircuitById(lstClosedCircuit, iClosedCircuitId);
            if (null == closedCircuit)
            {
                closedCircuit = new CClosedCircuit(iClosedCircuitId);
            }

            // ���ָ��ID������������
            CRectifier rectifier = closedCircuit.getRectifierById(iRectifierId);
            if (null == rectifier)
            {
                rectifier = new CRectifier(iRectifierId);
            }

            // �������������״̬��Ϣ
            if (!rectifier.parseFrame(ucBuffer, nIndex))
            {
                continue;
            }

            // ������������󵽻�·��
            closedCircuit.AddRetifier(rectifier);

            // ��ӻ�·��������
            AddClosedCircuit(lstClosedCircuit, closedCircuit);
        }

        // У��
        iValue = ucBuffer[nIndex++];
        
        // ֡β
        iValue = ucBuffer[nIndex++];
        
        // �����ѽ����ĳ���
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * ��������������
     * 
     * @param eDataItem �������־
     * @param ucBuffer ��Ų�ѯ����Ļ�����
     * @param iFrameLen ��ѯ����ĳ���
     * @return �ɹ�����true
     * @throws Exception �쳣��Ϣ
     */
    protected boolean getQueryCommand_ReadSingleControlTask(enumDataItem eDataItem, byte[] ucBuffer, IntHolder iFrameLen) throws Exception
    {
        int             nIndex = 0;
        

        // ����������
        if ((null == ucBuffer) 
                || (ucBuffer.length < 23)
                || (eDataItem.getValue() < enumDataItem.ReadSingleControlTask_1.getValue())
                || (eDataItem.getValue() > enumDataItem.ReadSingleControlTask_12.getValue()))
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
        ucBuffer[nIndex++] = (byte) enumControlCode.ReadRequest.getValue();

        // ���ݳ���
        System.arraycopy(NumberUtil.ShortToByte2(10), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // �������־
        System.arraycopy(NumberUtil.longToByte8(1), 0, ucBuffer, nIndex, 8);
        nIndex += 8;

        // ��������
        System.arraycopy(NumberUtil.ShortToByte2(eDataItem.getValue()), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // У���
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // ֡β
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // ���ò�ѯ����ĳ���
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * ������ȡ���п����������Ӧ֡
     * 
     * @param eDataItem �������־
     * @param ucBuffer ��ѯ�������Ӧ֡
     * @param iFrameLen ��Ӧ֡�ĳ���
     * @param iParsedLen ���������ֽ���
     * @param outParam ������п���������б�
     * @return �����ɹ�����true
     */
    protected boolean parseQueryResponse_ReadSingleControlTask(enumDataItem eDataItem, byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, Object outParam)
    {
        int             nIndex          = 0;
        @SuppressWarnings("unused")
        long            iValue          = 0;
        int             LENGTH_HEADER   = 22;
        int             LENGTH_TASK     = 18;
        int             LENGTH_TAIL     = 2;
        

        // ����������
        if ((null == ucBuffer) 
                || (iFrameLen < LENGTH_HEADER) 
                || (ucBuffer.length < iFrameLen) 
                || !(outParam instanceof LinkedList)
                || (eDataItem.getValue() < enumDataItem.ReadSingleControlTask_1.getValue())
                || (eDataItem.getValue() > enumDataItem.ReadSingleControlTask_12.getValue()))
        {
            return false;
        }
        @SuppressWarnings("unchecked")
        LinkedList<CControlTask> lstTasks = (LinkedList<CControlTask>) outParam;

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
        iValue = ucBuffer[nIndex++];

        // ��ʼ�ַ�
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // ������
        if ((enumControlCode.ReadRequest.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // ���ݳ���
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // �������־
        iValue = NumberUtil.Byte8ToLong(ucBuffer, nIndex);
        nIndex += 8;

        // ��������
        if (eDataItem.getValue() != NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex))
        {
            return false;
        }
        nIndex += 2;
        
        // ���ݱ��ĳ��ȣ��ж��Ƿ���ڿ�������
        if (iFrameLen >= (LENGTH_HEADER + LENGTH_TASK + LENGTH_TAIL))
        {
            try
            {
                // ������������
                CControlTask task = new CControlTask(this);
                IntHolder iImplPlanLen = new IntHolder();
                if (!task.Decode(ucBuffer, nIndex, iImplPlanLen))
                {
                    return false;
                }

                // ������񵽼�����
                lstTasks.add(task);
                nIndex += iImplPlanLen.value;
            }
            catch (ParseException e)
            {
                System.err.println(e.getMessage());
                return false;
            }
        }

        // У��
        iValue = ucBuffer[nIndex++];
        
        // ֡β
        iValue = ucBuffer[nIndex++];
        
        // �����ѽ����ĳ���
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * �����еĿ�������
     * 
     * @param ucBuffer ��Ų�ѯ����Ļ�����
     * @param iFrameLen ��ѯ����ĳ���
     * @return �ɹ�����true
     * @throws Exception �쳣��Ϣ
     */
    protected boolean getQueryCommand_ReadAllControlTask(byte[] ucBuffer, IntHolder iFrameLen) throws Exception
    {
        int             nIndex = 0;
        

        // ����������
        if ((null == ucBuffer) || ucBuffer.length < 23)
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
        ucBuffer[nIndex++] = (byte) enumControlCode.ReadRequest.getValue();

        // ���ݳ���
        System.arraycopy(NumberUtil.ShortToByte2(10), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // �������־
        System.arraycopy(NumberUtil.longToByte8(1), 0, ucBuffer, nIndex, 8);
        nIndex += 8;

        // ��������
        System.arraycopy(NumberUtil.ShortToByte2(enumDataItem.ReadAllControlTask.getValue()), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // У���
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // ֡β
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // ���ò�ѯ����ĳ���
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * ������ȡ���п����������Ӧ֡
     * 
     * @param ucBuffer ��ѯ�������Ӧ֡
     * @param iFrameLen ��Ӧ֡�ĳ���
     * @param iParsedLen ���������ֽ���
     * @param outParam ������п���������б�
     * @return �����ɹ�����true
     */
    @SuppressWarnings("unchecked")
    protected boolean parseQueryResponse_ReadAllControlTask(byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, Object outParam)
    {
        int             nIndex          = 0;
        long            iValue          = 0;
        int             LENGTH_HEADER   = 22;
        int             LENGTH_TASK     = 18;
        int             LENGTH_TAIL     = 2;
        

        // ����������
        if ((null == ucBuffer) || (iFrameLen < LENGTH_HEADER) || (ucBuffer.length < iFrameLen) || !(outParam instanceof LinkedList))
        {
            return false;
        }
        LinkedList<CControlTask> lstTasks = (LinkedList<CControlTask>) outParam;

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
        iValue = ucBuffer[nIndex++];

        // ��ʼ�ַ�
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // ������
        if ((enumControlCode.ReadRequest.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // ���ݳ���
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // �������־
        iValue = NumberUtil.Byte8ToLong(ucBuffer, nIndex);
        nIndex += 8;

        // ��������
        if (enumDataItem.ReadAllControlTask.getValue() != NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex))
        {
            return false;
        }
        nIndex += 2;

        // ������
        iValue = ucBuffer[nIndex++];
        if ((iFrameLen < (LENGTH_HEADER + iValue * LENGTH_TASK + LENGTH_TAIL)) || (ucBuffer.length < iFrameLen))
        {
            return false;
        }

        // ����ÿһ����������
        try
        {
            IntHolder iImplPlanLen = new IntHolder();
            for (int i = 0; i < iValue; i++)
            {
                // ������������
                CControlTask task = new CControlTask(this);
                if (!task.Decode(ucBuffer, nIndex, iImplPlanLen))
                {
                    return false;
                }

                // ������񵽼�����
                lstTasks.add(task);
                nIndex += iImplPlanLen.value;
            }
        }
        catch (ParseException e)
        {
            System.err.println(e.getMessage());
            return false;
        }
        
        // У��
        iValue = ucBuffer[nIndex++];
        
        // ֡β
        iValue = ucBuffer[nIndex++];
        
        // �����ѽ����ĳ���
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * ��ó����豸������Ϣ������
     * 
     * @param ucBuffer ��Ų�ѯ����Ļ�����
     * @param iFrameLen ��ѯ����ĳ���
     * @param inParam �����豸������Ϣ����Ĳ���
     * @return �ɹ�����true
     * @throws Exception �쳣��Ϣ
     */
    protected boolean getQueryCommand_ReadArchives(byte[] ucBuffer, IntHolder iFrameLen, Object inParam) throws Exception
    {
        int             nIndex = 0;
        

        // ����������
        if ((null == ucBuffer) || (ucBuffer.length < 23))
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
        ucBuffer[nIndex++] = (byte) enumControlCode.ReadRequest.getValue();

        // ���ݳ���
        System.arraycopy(NumberUtil.ShortToByte2(10), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // �������־
        System.arraycopy(NumberUtil.longToByte8(1), 0, ucBuffer, nIndex, 8);
        nIndex += 8;

        // ��������
        System.arraycopy(NumberUtil.ShortToByte2(enumDataItem.ReadAllArchives.getValue()), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // У���
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // ֡β
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // ���ò�ѯ����ĳ���
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * ���������豸������Ϣ����Ӧ֡
     * 
     * @param ucBuffer �����Ӧ֡�Ļ�����
     * @param iFrameLen ��Ӧ֡�ĳ���
     * @param iParsedLen ���������ֽ���
     * @param outParam ����豸������Ϣ���б�
     * @return �ɹ�����true
     */
    protected boolean parseQueryResponse_ReadArchives(byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, Object outParam)
    {
        int             nIndex = 0;
        @SuppressWarnings("unused")
        long            iValue = 0;
        

        // ����������
        if ((null == ucBuffer) || (iFrameLen < 22) || (ucBuffer.length < iFrameLen) || !(outParam instanceof List))
        {
            return false;
        }
        @SuppressWarnings("unchecked")
        List<CModuleBase> lstModules = (List<CModuleBase>) outParam;

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
        iValue = ucBuffer[nIndex++];

        // ��ʼ�ַ�
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // ������
        if ((enumControlCode.ReadRequest.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // ���ݳ���
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // �������־
        iValue = NumberUtil.Byte8ToLong(ucBuffer, nIndex);
        nIndex += 8;

        // ��������
        if (enumDataItem.ReadAllArchives.getValue() != NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex))
        {
            return false;
        }
        nIndex += 2;

        // ��������
        int iModuleNum = ucBuffer[nIndex++];

        // �豸��������
        lstModules.clear();
        for (int i = 0; i < iModuleNum; i++)
        {
            CModuleBase module = new CModuleBase();
            IntHolder iParseBytes = new IntHolder(0);

            // �����豸�ڵ㱨��
            if (!module.parseFrame(ucBuffer, nIndex, iParseBytes))
            {
                return false;
            }

            // ����豸�ڵ�
            lstModules.add(module);
            nIndex += iParseBytes.value;
        }

        // У��
        iValue = ucBuffer[nIndex++];
        
        // ֡β
        iValue = ucBuffer[nIndex++];
        
        // �����ѽ����ĳ���
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * ��ö�ȡ�澯��־������
     * 
     * @param ucBuffer ��Ų�ѯ����Ļ�����
     * @param iFrameLen ��ѯ����ĳ���
     * @param inParam �澯��־��������Ĳ���
     * @return �ɹ�����true
     * @throws Exception �쳣��Ϣ
     */
    protected boolean getQueryCommand_ReadAlarmLog(byte[] ucBuffer, IntHolder iFrameLen, Object inParam) throws Exception
    {
        int             nIndex = 0;
        CQueryParam_ReadAlarmLog    ralParam;
        

        // ����������
        if ((null == ucBuffer) || (ucBuffer.length < 19) || !(inParam instanceof CQueryParam_ReadAlarmLog))
        {
            return false;
        }
        ralParam = (CQueryParam_ReadAlarmLog) inParam;

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
        ucBuffer[nIndex++] = (byte) enumControlCode.ReadAlarmLog.getValue();

        // ���ݳ���
        System.arraycopy(NumberUtil.ShortToByte2(6), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // �澯��ʼʱ��
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
        System.arraycopy(NumberUtil.StringToBcdArray(sdf.format(ralParam.getStartTime())), 0, ucBuffer, nIndex, 5);
        nIndex += 5;

        // �澯���ݵ���
        ucBuffer[nIndex++] = (byte) ralParam.getLogCount();

        // У���
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // ֡β
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // ���ò�ѯ����ĳ���
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * �����澯��־����Ӧ֡
     * 
     * @param ucBuffer �����Ӧ֡�Ļ�����
     * @param iFrameLen ��Ӧ֡�ĳ���
     * @param iParsedLen ���������ֽ���
     * @param outParam ��Ÿ澯��־���б�
     * @return �ɹ�����true
     */
    @SuppressWarnings({ "deprecation", "unchecked" })
    protected boolean parseQueryResponse_ReadAlarmLog(byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, Object outParam)
    {
        int             nIndex = 0;
        @SuppressWarnings("unused")
        long            iValue = 0;
        

        // ����������
        if ((null == ucBuffer) || (iFrameLen < 14) || (ucBuffer.length < iFrameLen) || !(outParam instanceof LinkedList))
        {
            return false;
        }
        LinkedList<CAlarmLogBase> lstLogs = (LinkedList<CAlarmLogBase>) outParam;

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
        iValue = ucBuffer[nIndex++];

        // ��ʼ�ַ�
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // ������
        if ((enumControlCode.ReadAlarmLog.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // ���ݳ���
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // �澯����
        int iAlarmCount = ucBuffer[nIndex++];
        if (iFrameLen < (14 + iAlarmCount * 13))
        {
            return false;
        }

        // ѭ������ÿһ���澯����
        for (int i = 0; i < iAlarmCount; i++)
        {
            // �������ţ�6�ֽڣ�
            nIndex += 6;

            // ��
            int iYear = 100 + NumberUtil.BcdByteToNormalByte(ucBuffer[nIndex++]);
            // ��
            int iMonth = NumberUtil.BcdByteToNormalByte(ucBuffer[nIndex++]) - 1;
            // ��
            int iDay = NumberUtil.BcdByteToNormalByte(ucBuffer[nIndex++]);
            // ʱ
            int iHour = NumberUtil.BcdByteToNormalByte(ucBuffer[nIndex++]);
            // ��
            int iMinute = NumberUtil.BcdByteToNormalByte(ucBuffer[nIndex++]);

            // �澯����
            int iAlarmType = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
            nIndex += 2;

            // ���ݱ������룬���ɸ澯���󣬽����澯����
            IntHolder iParseBytes = new IntHolder(0);
            CAlarmLogBase alarmLog = CAlarmLogFactory.getInstance().CreateAlarmLog(iAlarmType);
            if ((null == alarmLog) || !alarmLog.parseFrame(ucBuffer, nIndex, iParseBytes))
            {
                return false;
            }
            nIndex += iParseBytes.value;

            // ���ø澯����ʱ��
            alarmLog.setAlarmDate(new Date(iYear, iMonth, iDay, iHour, iMinute));

            // ��Ӹ澯��־����������
            lstLogs.add(alarmLog);
        }
        
        // У��
        iValue = ucBuffer[nIndex++];
        
        // ֡β
        iValue = ucBuffer[nIndex++];
        
        // �����ѽ����ĳ���
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

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
     * ��DCU���Ϳ�������
     * @param ucRequestFrame    ��ſ�������Ļ�����
     * @param iRequestLen       ��������ĳ���
     * @param ucRespFrame       �����Ӧ����Ļ�����
     * @param iRespLen          ��Ӧ����ĳ���
     * @return  �շ��ɹ�����true
     */
    public synchronized boolean Ioctl(byte[] ucRequestFrame, int iRequestLen, byte[] ucRespFrame, IntHolder iRespLen)
    {
        boolean         bFlag = false;
        
        
        // ����������
        if ((null == ucRequestFrame) 
                || (ucRequestFrame.length == 0)
                || (null == ucRespFrame)
                || (null == iRespLen))
        {
            return false;
        }
        
        try
        {
            // ������������
            InputStream inStream = m_sockConn.getInputStream();
            OutputStream outStream = m_sockConn.getOutputStream();

            // �ȴ��������еĵǳ�֡������֡
            HandleSpecialRequest();
            
            // ���socket���ջ�����
            CSocketUtil.clearRecvBuffer(m_sockConn);
            
            // ���豸���Ϳ�������
            outStream.write(ucRequestFrame, 0, iRequestLen);
            outStream.flush();

            // ���տ�����Ӧ
            iRespLen.value = inStream.read(ucRespFrame, 0, ucRespFrame.length);
            if (iRespLen.value == 0)
            {
                return false;
            }

            // ���Ϊ�ɹ�
            bFlag = true;
        }
        catch(Exception e)
        {
            StringBuffer strBuffer = new StringBuffer();
            strBuffer.append("����Pad��������ʱ�����쳣��");
            strBuffer.append(e.getMessage());
            System.out.println(strBuffer.toString());
        }
        
        return bFlag;
    }

    /**
     * ����
     * 
     * @param strCmdType ������������
     * @param inParam �������
     * @param outParam �������
     * @return �ɹ�����true
     */
    public synchronized boolean Ioctl(String strCmdType, Object inParam, CIoctlResult outParam)
    {
        boolean         bFlag = false;
        int             iRecvFrameLen = 0;

        
        try
        {
            // ������������
            InputStream inStream = m_sockConn.getInputStream();
            OutputStream outStream = m_sockConn.getOutputStream();

            // �ȴ��������еĵǳ�֡������֡
            HandleSpecialRequest();

            // ���ָ�����͵Ŀ�������
            IntHolder iSentFrameLen = new IntHolder(0);
            if (!getIoctlCommand(strCmdType, m_ucSentBuffer4Ioctl, iSentFrameLen, inParam))
            {
                return false;
            }

            // ���socket���ջ�����
            CSocketUtil.clearRecvBuffer(m_sockConn);

            // ���豸���Ϳ�������
            outStream.write(m_ucSentBuffer4Ioctl, 0, iSentFrameLen.value);
            outStream.flush();

            // ���տ�����Ӧ
            iRecvFrameLen = inStream.read(m_ucRecvBuffer4Ioctl, 0, m_ucRecvBuffer4Ioctl.length);
            if (iRecvFrameLen == 0)
            {
                return false;
            }
            
            // �������յı���
            int     iTotalParsed = 0;
            byte[]  tmpBuffer = new byte[iRecvFrameLen];
            IntHolder iParsedLen = new IntHolder(0);
            while (iTotalParsed < iRecvFrameLen)
            {
                // ������δ�����ı��ĵ�������
                System.arraycopy(m_ucRecvBuffer4Ioctl, iTotalParsed, tmpBuffer, 0, iRecvFrameLen - iTotalParsed);

                // ����ָ�������������Ӧ����
                if (parseIoctlResponse(strCmdType, tmpBuffer, iRecvFrameLen - iTotalParsed, iParsedLen, outParam))
                {
                    // �����ѽ����ı��ĳ���
                    iTotalParsed += iParsedLen.value;
                    
                    // ���÷��ر�־
                    bFlag = true;
                }
                // ����DCU�ǳ�����֡
                else if (parseIoctlRequest_logout(tmpBuffer, iRecvFrameLen - iTotalParsed, iParsedLen))
                {
                    // �����ѽ����ı��ĳ���
                    iTotalParsed += iParsedLen.value;

                    // �޸��豸��Ϣ
                    SetDeviceState(enumDeviceState.OffLine);

                    // �ϳ�DCU�ǳ��������Ӧ����
                    if (composeIoctlResponse_logout(m_ucSentBuffer4Ioctl, iSentFrameLen))
                    {
                        // ������Ӧ����
                        outStream.write(m_ucSentBuffer4Ioctl, 0, iSentFrameLen.value);
                        outStream.flush();
                    }
                }
                // ����DCU����֡
                else if (parseIoctlRequest_heartBeat(tmpBuffer, iRecvFrameLen - iTotalParsed, iParsedLen))
                {
                    // �����ѽ����ı��ĳ���
                    iTotalParsed += iParsedLen.value;

                    // �������һ���յ��������ĵĺ�����
                    setLastTimeHeartBeat(System.currentTimeMillis());
                    if (GetDeviceState() == enumDeviceState.OffLine)
                    {
                        SetDeviceState(enumDeviceState.Online);
                    }

                    // �ϳ�����֡��Ӧ����
                    if (composeIoctlResponse_heartBeat(m_ucSentBuffer4Ioctl, iSentFrameLen))
                    {
                        // ������Ӧ����
                        outStream.write(m_ucSentBuffer4Ioctl, 0, iSentFrameLen.value);
                        outStream.flush();
                    }
                }
                // ��������������ʶ�������
                else 
                {
                    StringBuffer strBuffer = new StringBuffer();
                    strBuffer.append("����");
                    strBuffer.append(strCmdType);
                    strBuffer.append("�������ȴ�յ��˲�ƥ���������ɹ�����Ӧ���ģ�");
                    for(int i = 0; i < iRecvFrameLen; i++)
                    {
                        strBuffer.append(String.format("%02x ", m_ucRecvBuffer4Ioctl[i]));
                    }
                    
                    System.out.println(strBuffer.toString());
                    break;
                }
            }
        }
        catch (Exception e)
        {
            StringBuffer strBuffer = new StringBuffer();
            
            if (iRecvFrameLen > 0)
            {
                strBuffer.append("���������쳣��");
                for(int i = 0; i < iRecvFrameLen; i++)
                {
                    strBuffer.append(String.format("%02x ", m_ucRecvBuffer4Ioctl[i]));
                }
                
                strBuffer.append("��");
            }
            strBuffer.append("�쳣��Ϣ��");
            strBuffer.append(e.getMessage());
            
            System.out.println(strBuffer.toString());
            return false;
        }

        return bFlag;
    }

    /**
     * ��ÿ�������
     * 
     * @param strCmdType ������������
     * @param ucBuffer ��ſ�������Ļ�����
     * @param iFrameLen ���������
     * @param inParam �������
     * @return �ɹ�����true
     * @throws Exception �쳣��Ϣ
     */
    protected boolean getIoctlCommand(String strCmdType, byte[] ucBuffer, IntHolder iFrameLen, Object inParam) throws Exception
    {
        boolean         bRet = false;
        enumDataItem    eCmdType;
        

        // �����������ͣ�ִ����Ӧ�Ŀ�������ϳɺ���
        eCmdType = enumDataItem.valueOf(strCmdType);
        switch (eCmdType)
        {
        // ���ù���Ա�绰
        case AdminPhone:
            bRet = getIoctlCommand_SetAdminPhone(ucBuffer, iFrameLen, inParam);
            break;

        // Զ�̿���
        case RemoteControl:
            bRet = getIoctlCommand_RemoteControl(ucBuffer, iFrameLen, inParam);
            break;

        // �̿�����
        case WriteControlTask:
            bRet = getIoctlCommand_WriteControlTask(ucBuffer, iFrameLen, inParam);
            break;

        // ����ɾ���̿�����
        case DeleteControlTasks:
            bRet = getIoctlCommand_DeleteControlTasks(ucBuffer, iFrameLen, inParam);
            break;

        // Уʱ
        case Timing:
            bRet = getIoctlCommand_Timing(ucBuffer, iFrameLen, inParam);
            break;

        // ���������豸����
        case AddArchives:
            bRet = getIoctlCommand_AddArchives(ucBuffer, iFrameLen, inParam);
            break;

        // ����ɾ���豸����
        case DeleteArchives:
            bRet = getIoctlCommand_DeleteArchives(ucBuffer, iFrameLen, inParam);
            break;

        default:
            break;
        }

        return bRet;
    }

    /**
     * ��������������Ӧ
     * 
     * @param strCmdType ������������
     * @param ucBuffer ����������Ӧ֡
     * @param iFrameLen ����������Ӧ֡����
     * @param iParsedLen �������ֽ���
     * @param outParam �������
     * @return �ɹ�����true
     */
    protected boolean parseIoctlResponse(String strCmdType, byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, CIoctlResult outParam)
    {
        boolean         bRet = false;
        enumDataItem    eCmdType;
        

        // �����������ͣ�ִ����Ӧ�Ŀ�������ϳɺ���
        eCmdType = enumDataItem.valueOf(strCmdType);
        switch (eCmdType)
        {
        // ���ù���Ա�绰
        case AdminPhone:
            bRet = parseIoctlResponse_SetAdminPhone(ucBuffer, iFrameLen, iParsedLen, outParam);
            break;

        // Զ�̿���
        case RemoteControl:
            bRet = parseIoctlResponse_RemoteControl(ucBuffer, iFrameLen, iParsedLen, outParam);
            break;

        // �̿�����
        case WriteControlTask:
            bRet = parseIoctlResponse_WriteControlTask(ucBuffer, iFrameLen, iParsedLen, outParam);
            break;

        // ����ɾ���̿�����
        case DeleteControlTasks:
            bRet = parseIoctlResponse_DeleteControlTasks(ucBuffer, iFrameLen, iParsedLen, outParam);
            break;

        // Уʱ
        case Timing:
            bRet = parseIoctlResponse_Timing(ucBuffer, iFrameLen, iParsedLen, outParam);
            break;

        // ���������豸����
        case AddArchives:
            bRet = parseIoctlResponse_AddArchives(ucBuffer, iFrameLen, iParsedLen, outParam);
            break;

        // ����ɾ���豸����
        case DeleteArchives:
            bRet = parseIoctlResponse_DeleteArchives(ucBuffer, iFrameLen, iParsedLen, outParam);
            break;

        default:
            break;
        }

        return bRet;
    }

    /**
     * ������ù���Ա�绰����Ŀ�������
     * 
     * @param ucBuffer ��ſ�������Ļ�����
     * @param iFrameLen �ϳɵĿ��������
     * @param inParam �������(String����)
     * @return �ɹ�����true
     * @throws Exception �쳣��Ϣ
     */
    protected boolean getIoctlCommand_SetAdminPhone(byte[] ucBuffer, IntHolder iFrameLen, Object inParam) throws Exception
    {
        int             nIndex = 0;
        String          strPhone = null;
        

        // ����������
        if ((null == ucBuffer) || (ucBuffer.length < 27) || !(inParam instanceof String))
        {
            return false;
        }

        // ����Ա�绰����
        strPhone = (String) inParam;
        if (strPhone.length() == 0)
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
        ucBuffer[nIndex++] = (byte) enumControlCode.WriteRequest.getValue();

        // ���ݳ���
        System.arraycopy(NumberUtil.ShortToByte2(14), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // �������
        System.arraycopy(NumberUtil.ShortToByte2(0), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // Ȩ�޵ȼ�
        ucBuffer[nIndex++] = 0x11;

        // ����
        ucBuffer[nIndex++] = 0x11;
        ucBuffer[nIndex++] = 0x11;
        ucBuffer[nIndex++] = 0x11;

        // ��������
        System.arraycopy(NumberUtil.ShortToByte2(enumDataItem.AdminPhone.getValue()), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // ���ֻ��ŵ�ǰ�渽���ַ���A����ȷ����Ӻ���ַ�������Ϊ12
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < (12 - strPhone.length()); i++)
        {
            sb.append("A");
        }
        sb.append(strPhone);

        // ����ֻ��ŵ�������
        String strData = sb.toString();
        for (int i = 0; i < 6; i++)
        {
            ucBuffer[nIndex++] = (byte) Integer.parseInt(strData.substring(i * 2, (i + 1) * 2), 16);
        }

        // У���
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // ֡β
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // ���ò�ѯ����ĳ���
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * �������ù���Ա�绰�������Ӧ����
     * 
     * @param ucBuffer ��Ӧ����
     * @param iFrameLen ��Ӧ���ĳ���
     * @param iParsedLen �������ֽ���
     * @param outParam �������
     * @return �ɹ�����true
     */
    protected boolean parseIoctlResponse_SetAdminPhone(byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, CIoctlResult outParam)
    {
        int             nIndex = 0;
        @SuppressWarnings("unused")
        long            iValue = 0;
        

        // ����������
        if ((null == ucBuffer) || (iFrameLen < 18) || (ucBuffer.length < iFrameLen))
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
        iValue = ucBuffer[nIndex++];

        // ��ʼ�ַ�
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // ������
        if ((enumControlCode.WriteRequest.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // ���ݳ���
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // �������
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // ��������
        if (enumDataItem.AdminPhone.getValue() != NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex))
        {
            return false;
        }
        nIndex += 2;

        // ���ý��
        enumErrorCode eErrorCode = enumErrorCode.getErrorByInt(ucBuffer[nIndex++]);
        if (null != outParam)
        {
            outParam.setErrorCode(eErrorCode);
        }

        // У��
        iValue = ucBuffer[nIndex++];
        
        // ֡β
        iValue = ucBuffer[nIndex++];
        
        // �����ѽ����ĳ���
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * ���Զ�̿�������ı���
     * 
     * @param ucBuffer ��ſ�������Ļ�����
     * @param iFrameLen �ϳɵĿ��������
     * @param inParam ���������List<CIoctlParam_RemoteControl>���ͣ�
     * @return �ɹ�����true
     * @throws Exception �쳣��Ϣ
     */
    @SuppressWarnings("unchecked")
    protected boolean getIoctlCommand_RemoteControl(byte[] ucBuffer, IntHolder iFrameLen, Object inParam) throws Exception
    {
        int             nIndex = 0;
        List<CIoctlParam_RemoteControl>     m_lstParams;
        

        // ����������
        if ((null == ucBuffer) || !(inParam instanceof List))
        {
            return false;
        }

        // ��黺��������
        m_lstParams = (List<CIoctlParam_RemoteControl>) inParam;
        if (ucBuffer.length < (22 + (m_lstParams.size() * 7)))
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
        ucBuffer[nIndex++] = (byte) enumControlCode.WriteRequest.getValue();

        // ���ݳ���
        System.arraycopy(NumberUtil.ShortToByte2(9 + m_lstParams.size() * 7), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // �������
        System.arraycopy(NumberUtil.ShortToByte2(0), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // Ȩ�޵ȼ�
        ucBuffer[nIndex++] = 0x11;

        // ����
        ucBuffer[nIndex++] = 0x11;
        ucBuffer[nIndex++] = 0x11;
        ucBuffer[nIndex++] = 0x11;

        // ��������
        System.arraycopy(NumberUtil.ShortToByte2(enumDataItem.RemoteControl.getValue()), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // ң��·��
        ucBuffer[nIndex++] = (byte) m_lstParams.size();

        // ѭ������ÿһ��������·
        for (CIoctlParam_RemoteControl ctlParam : m_lstParams)
        {
            // �豸�ţ���·�š��̵����ţ�
            ucBuffer[nIndex++] = (byte) ctlParam.getDeviceId();

            // �ӻ�·��
            ucBuffer[nIndex++] = (byte) ctlParam.getSubLoopId();

            // ���⹦����
            ucBuffer[nIndex++] = (byte) ctlParam.getSpecificFunc().getValue();

            // ���������ݣ���ѹֵ��
            System.arraycopy(NumberUtil.ShortToBcdByte2(ctlParam.getDataItem() * 10), 0, ucBuffer, nIndex, 2);
            nIndex += 2;

            // ���������ݣ�����ֵ��
            System.arraycopy(NumberUtil.ShortToBcdByte2(0), 0, ucBuffer, nIndex, 2);
            nIndex += 2;
        }

        // У���
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // ֡β
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // ���ò�ѯ����ĳ���
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * ����Զ�̿����������Ӧ����
     * 
     * @param ucBuffer ��Ӧ����
     * @param iFrameLen ��Ӧ���ĳ���
     * @param iParsedLen �������ֽ���
     * @param outParam �������
     * @return �ɹ�����true
     */
    protected boolean parseIoctlResponse_RemoteControl(byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, CIoctlResult outParam)
    {
        int             nIndex = 0;
        @SuppressWarnings("unused")
        long            iValue = 0;
        

        // ����������
        if ((null == ucBuffer) || (iFrameLen < 18) || (ucBuffer.length < iFrameLen))
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
        iValue = ucBuffer[nIndex++];

        // ��ʼ�ַ�
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // ������
        if ((enumControlCode.WriteRequest.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // ���ݳ���
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // �������
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // ��������
        if (enumDataItem.RemoteControl.getValue() != NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex))
        {
            return false;
        }
        nIndex += 2;

        // ���ý��
        enumErrorCode eErrorCode = enumErrorCode.getErrorByInt(ucBuffer[nIndex++]);
        if (null != outParam)
        {
            outParam.setErrorCode(eErrorCode);
        }

        // У��
        iValue = ucBuffer[nIndex++];
        
        // ֡β
        iValue = ucBuffer[nIndex++];
        
        // �����ѽ����ĳ���
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * ������ó̿�����ı���
     * 
     * @param ucBuffer ��ſ�������Ļ�����
     * @param iFrameLen �ϳɵĿ��������
     * @param inParam ���������CIoctlParam_WriteControlTask���ͣ�
     * @return �ɹ�����true
     * @throws Exception �쳣��Ϣ
     */
    protected boolean getIoctlCommand_WriteControlTask(byte[] ucBuffer, IntHolder iFrameLen, Object inParam) throws Exception
    {
        int             nIndex      = 0;
        CControlTask    m_ctlTask;
        

        // ����������
        if ((null == ucBuffer) || !(inParam instanceof CControlTask))
        {
            return false;
        }

        // �����������
        byte[] ucDataField = new byte[2048];
        IntHolder iDataFieldLen = new IntHolder();
        m_ctlTask = (CControlTask) inParam;
        if (!m_ctlTask.Encode(ucDataField, 0, iDataFieldLen) || (ucBuffer.length < (21 + iDataFieldLen.value)))
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
        ucBuffer[nIndex++] = (byte) enumControlCode.WriteRequest.getValue();

        // ���ݳ���
        System.arraycopy(NumberUtil.ShortToByte2(8 + iDataFieldLen.value), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // �������
        System.arraycopy(NumberUtil.ShortToByte2(0), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // Ȩ�޵ȼ�
        ucBuffer[nIndex++] = 0x11;

        // ����
        ucBuffer[nIndex++] = 0x11;
        ucBuffer[nIndex++] = 0x11;
        ucBuffer[nIndex++] = 0x11;

        // ��������
        System.arraycopy(NumberUtil.ShortToByte2(enumDataItem.WriteControlTask.getValue()), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // ����������
        System.arraycopy(ucDataField, 0, ucBuffer, nIndex, iDataFieldLen.value);
        nIndex += iDataFieldLen.value;

        // У���
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // ֡β
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // ���ò�ѯ����ĳ���
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * �������ó̿������������Ӧ����
     * 
     * @param ucBuffer ��Ӧ����
     * @param iFrameLen ��Ӧ���ĳ���
     * @param iParsedLen �������ֽ���
     * @param outParam �������
     * @return �ɹ�����true
     */
    protected boolean parseIoctlResponse_WriteControlTask(byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, CIoctlResult outParam)
    {
        int             nIndex = 0;
        @SuppressWarnings("unused")
        long            iValue = 0;
        

        // ����������
        if ((null == ucBuffer) || (iFrameLen < 18) || (ucBuffer.length < iFrameLen))
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
        iValue = ucBuffer[nIndex++];

        // ��ʼ�ַ�
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // ������
        if ((enumControlCode.WriteRequest.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // ���ݳ���
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // �������
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // ��������
        if (enumDataItem.WriteControlTask.getValue() != NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex))
        {
            return false;
        }
        nIndex += 2;

        // ���ý��
        enumErrorCode eErrorCode = enumErrorCode.getErrorByInt(ucBuffer[nIndex++]);
        if (null != outParam)
        {
            outParam.setErrorCode(eErrorCode);
        }

        // У��
        iValue = ucBuffer[nIndex++];
        
        // ֡β
        iValue = ucBuffer[nIndex++];
        
        // �����ѽ����ĳ���
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * ����ɾ���̿�����
     * 
     * @param ucBuffer ��ſ�������Ļ�����
     * @param iFrameLen �ϳɵĿ��������
     * @param inParam ���������List<int>���ͣ�
     * @return �ɹ�����true
     * @throws Exception �쳣��Ϣ
     */
    protected boolean getIoctlCommand_DeleteControlTasks(byte[] ucBuffer, IntHolder iFrameLen, Object inParam) throws Exception
    {
        int             nIndex      = 0;
        

        // ����������
        if ((null == ucBuffer) || !(inParam instanceof List<?>))
        {
            return false;
        }
        @SuppressWarnings("unchecked")
        List<Integer> lstTaskIds = (List<Integer>) inParam;

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
        ucBuffer[nIndex++] = (byte) enumControlCode.WriteRequest.getValue();

        // ���ݳ���
        System.arraycopy(NumberUtil.ShortToByte2(8 + 1 + 2 * lstTaskIds.size()), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // �������
        System.arraycopy(NumberUtil.ShortToByte2(0), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // Ȩ�޵ȼ�
        ucBuffer[nIndex++] = 0x11;

        // ����
        ucBuffer[nIndex++] = 0x11;
        ucBuffer[nIndex++] = 0x11;
        ucBuffer[nIndex++] = 0x11;

        // ��������
        System.arraycopy(NumberUtil.ShortToByte2(enumDataItem.DeleteControlTasks.getValue()), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // ������
        ucBuffer[nIndex++] = (byte) lstTaskIds.size();

        // �������ݿ�
        for (int i = 0; i < lstTaskIds.size(); i++)
        {
            ucBuffer[nIndex++] = 0x00;
            ucBuffer[nIndex++] = (byte) lstTaskIds.get(i).intValue();
        }

        // У���
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // ֡β
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // ���ò�ѯ����ĳ���
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * ��������ɾ���̿������������Ӧ����
     * 
     * @param ucBuffer ��Ӧ����
     * @param iFrameLen ��Ӧ���ĳ���
     * @param iParsedLen �������ֽ���
     * @param outParam �������
     * @return �ɹ�����true
     */
    protected boolean parseIoctlResponse_DeleteControlTasks(byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, CIoctlResult outParam)
    {
        int             nIndex = 0;
        @SuppressWarnings("unused")
        long            iValue = 0;
        

        // ����������
        if ((null == ucBuffer) || (iFrameLen < 18) || (ucBuffer.length < iFrameLen))
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
        iValue = ucBuffer[nIndex++];

        // ��ʼ�ַ�
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // ������
        if ((enumControlCode.WriteRequest.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // ���ݳ���
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // �������
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // ��������
        if (enumDataItem.DeleteControlTasks.getValue() != NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex))
        {
            return false;
        }
        nIndex += 2;

        // ���ý��
        enumErrorCode eErrorCode = enumErrorCode.getErrorByInt(ucBuffer[nIndex++]);
        if (null != outParam)
        {
            outParam.setErrorCode(eErrorCode);
        }

        // У��
        iValue = ucBuffer[nIndex++];
        
        // ֡β
        iValue = ucBuffer[nIndex++];
        
        // �����ѽ����ĳ���
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * ���Уʱ����
     * 
     * @param ucBuffer ��ſ�������Ļ�����
     * @param iFrameLen �ϳɵĿ��������
     * @param inParam ���������Date���ͣ�
     * @return �ɹ�����true
     * @throws Exception �쳣��Ϣ
     */
    protected boolean getIoctlCommand_Timing(byte[] ucBuffer, IntHolder iFrameLen, Object inParam) throws Exception
    {
        int             nIndex  = 0;
        Date            dt      = null;
        

        // ����������
        if ((null == ucBuffer) || (ucBuffer.length < 27) || !(inParam instanceof Date))
        {
            return false;
        }
        dt = (Date) inParam;

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
        ucBuffer[nIndex++] = (byte) enumControlCode.WriteRequest.getValue();

        // ���ݳ���
        System.arraycopy(NumberUtil.ShortToByte2(14), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // �������
        System.arraycopy(NumberUtil.ShortToByte2(0), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // Ȩ�޵ȼ�
        ucBuffer[nIndex++] = 0x11;

        // ����
        ucBuffer[nIndex++] = 0x11;
        ucBuffer[nIndex++] = 0x11;
        ucBuffer[nIndex++] = 0x11;

        // ��������
        System.arraycopy(NumberUtil.ShortToByte2(enumDataItem.Timing.getValue()), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // ʱ��
        SimpleDateFormat sdf = new SimpleDateFormat("ssmmHHddMMyy");
        System.arraycopy(NumberUtil.StringToBcdArray(sdf.format(dt)), 0, ucBuffer, nIndex, 6);
        nIndex += 6;

        // У���
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // ֡β
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // ���ò�ѯ����ĳ���
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * ����Уʱ�������Ӧ����
     * 
     * @param ucBuffer ��Ӧ����
     * @param iFrameLen ��Ӧ���ĳ���
     * @param iParsedLen �������ֽ���
     * @param outParam �������
     * @return �ɹ�����true
     */
    protected boolean parseIoctlResponse_Timing(byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, CIoctlResult outParam)
    {
        int             nIndex = 0;
        @SuppressWarnings("unused")
        long            iValue = 0;
        

        // ����������
        if ((null == ucBuffer) || (iFrameLen < 18) || (ucBuffer.length < iFrameLen))
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
        iValue = ucBuffer[nIndex++];

        // ��ʼ�ַ�
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // ������
        if ((enumControlCode.WriteRequest.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // ���ݳ���
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // �������
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // ��������
        if (enumDataItem.Timing.getValue() != NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex))
        {
            return false;
        }
        nIndex += 2;

        // ���ý��
        enumErrorCode eErrorCode = enumErrorCode.getErrorByInt(ucBuffer[nIndex++]);
        if (null != outParam)
        {
            outParam.setErrorCode(eErrorCode);
        }

        // У��
        iValue = ucBuffer[nIndex++];
        
        // ֡β
        iValue = ucBuffer[nIndex++];
        
        // �����ѽ����ĳ���
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * �����������豸�����ı���
     * 
     * @param ucBuffer ��ſ�������Ļ�����
     * @param iFrameLen �ϳɵĿ��������
     * @param inParam ���������List<CModuleBase>���ͣ�
     * @return �ɹ�����true
     * @throws Exception �쳣��Ϣ
     */
    protected boolean getIoctlCommand_AddArchives(byte[] ucBuffer, IntHolder iFrameLen, Object inParam) throws Exception
    {
        int             nIndex      = 0;
        

        // ����������
        if ((null == ucBuffer) || !(inParam instanceof List))
        {
            return false;
        }
        @SuppressWarnings("unchecked")
        List<CModuleBase> lstModules = (List<CModuleBase>) inParam;

        // �����������
        byte[] ucDataField = new byte[2048];
        int iDataFieldLen = 0;
        for (int i = 0; i < lstModules.size(); i++)
        {
            CModuleBase moduleBase = lstModules.get(i);
            IntHolder iMbEncodeBytes = new IntHolder();

            // �����豸�ڵ�
            if (!moduleBase.Encode(ucDataField, 0, iMbEncodeBytes))
            {
                return false;
            }

            // �ۼ��ѱ�����ֽ���
            iDataFieldLen += iMbEncodeBytes.value;
        }

        // ������鳤��
        if (ucBuffer.length < (22 + iDataFieldLen))
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
        ucBuffer[nIndex++] = (byte) enumControlCode.WriteRequest.getValue();

        // ���ݳ���
        System.arraycopy(NumberUtil.ShortToByte2(9 + iDataFieldLen), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // �������
        System.arraycopy(NumberUtil.ShortToByte2(0), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // Ȩ�޵ȼ�
        ucBuffer[nIndex++] = 0x11;

        // ����
        ucBuffer[nIndex++] = 0x11;
        ucBuffer[nIndex++] = 0x11;
        ucBuffer[nIndex++] = 0x11;

        // ��������
        System.arraycopy(NumberUtil.ShortToByte2(enumDataItem.AddArchives.getValue()), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // �����豸��
        ucBuffer[nIndex++] = (byte) lstModules.size();

        // �豸��������
        System.arraycopy(ucDataField, 0, ucBuffer, nIndex, iDataFieldLen);
        nIndex += iDataFieldLen;

        // У���
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // ֡β
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // ���ò�ѯ����ĳ���
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * ������������豸��������Ӧ����
     * 
     * @param ucBuffer ��Ӧ����
     * @param iFrameLen ��Ӧ���ĳ���
     * @param iParsedLen �������ֽ���
     * @param outParam �������
     * @return �ɹ�����true
     */
    protected boolean parseIoctlResponse_AddArchives(byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, CIoctlResult outParam)
    {
        int             nIndex = 0;
        @SuppressWarnings("unused")
        long            iValue = 0;
        

        // ����������
        if ((null == ucBuffer) || (iFrameLen < 18) || (ucBuffer.length < iFrameLen))
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
        iValue = ucBuffer[nIndex++];

        // ��ʼ�ַ�
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // ������
        if ((enumControlCode.WriteRequest.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // ���ݳ���
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // �������
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // ��������
        if (enumDataItem.AddArchives.getValue() != NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex))
        {
            return false;
        }
        nIndex += 2;

        // ���ý��
        enumErrorCode eErrorCode = enumErrorCode.getErrorByInt(ucBuffer[nIndex++]);
        if (null != outParam)
        {
            outParam.setErrorCode(eErrorCode);
        }

        // У��
        iValue = ucBuffer[nIndex++];
        
        // ֡β
        iValue = ucBuffer[nIndex++];
        
        // �����ѽ����ĳ���
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * �������ɾ���豸�����ı���
     * 
     * @param ucBuffer ��ſ�������Ļ�����
     * @param iFrameLen �ϳɵĿ��������
     * @param inParam ���������CIoctlParam_DeleteArchives���ͣ�
     * @return �ɹ�����true
     * @throws Exception �쳣��Ϣ
     */
    protected boolean getIoctlCommand_DeleteArchives(byte[] ucBuffer, IntHolder iFrameLen, Object inParam) throws Exception
    {
        int             nIndex      = 0;
        

        // ����������
        if ((null == ucBuffer) || !(inParam instanceof CIoctlParam_DeleteArchives))
        {
            return false;
        }
        CIoctlParam_DeleteArchives lstModules = (CIoctlParam_DeleteArchives) inParam;

        // �����������
        byte[] ucDataField = new byte[2048];
        int iDataFieldLen = 0;
        if (CIoctlParam_DeleteArchives.DT_DeleteAll == lstModules.getDeleteType())
        {
            // ȫ��ɾ��
            ucDataField[iDataFieldLen++] = 0x00;
        }
        else
        {
            List<Integer> lstDeviceIds = lstModules.getDeviceIdList();

            // ��ɾ���ĵ�������
            ucDataField[iDataFieldLen++] = (byte) lstDeviceIds.size();
            for (int i = 0; i < lstDeviceIds.size(); i++)
            {
                // �豸��
                ucDataField[iDataFieldLen++] = (byte) lstDeviceIds.get(i).intValue();
                ucDataField[iDataFieldLen++] = 0x00;
            }
        }

        // ������鳤��
        if (ucBuffer.length < (21 + iDataFieldLen))
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
        ucBuffer[nIndex++] = (byte) enumControlCode.WriteRequest.getValue();

        // ���ݳ���
        System.arraycopy(NumberUtil.ShortToByte2(8 + iDataFieldLen), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // �������
        System.arraycopy(NumberUtil.ShortToByte2(0), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // Ȩ�޵ȼ�
        ucBuffer[nIndex++] = 0x11;

        // ����
        ucBuffer[nIndex++] = 0x11;
        ucBuffer[nIndex++] = 0x11;
        ucBuffer[nIndex++] = 0x11;

        // ��������
        System.arraycopy(NumberUtil.ShortToByte2(enumDataItem.DeleteArchives.getValue()), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // �豸��������
        System.arraycopy(ucDataField, 0, ucBuffer, nIndex, iDataFieldLen);
        nIndex += iDataFieldLen;

        // У���
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // ֡β
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // ���ò�ѯ����ĳ���
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * ��������ɾ���豸��������Ӧ����
     * 
     * @param ucBuffer ��Ӧ����
     * @param iFrameLen ��Ӧ���ĳ���
     * @param iParsedLen �������ֽ���
     * @param outParam �������
     * @return �ɹ�����true
     */
    protected boolean parseIoctlResponse_DeleteArchives(byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, CIoctlResult outParam)
    {
        int             nIndex = 0;
        @SuppressWarnings("unused")
        long            iValue = 0;
        

        // ����������
        if ((null == ucBuffer) || (iFrameLen < 18) || (ucBuffer.length < iFrameLen))
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
        iValue = ucBuffer[nIndex++];

        // ��ʼ�ַ�
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // ������
        if ((enumControlCode.WriteRequest.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // ���ݳ���
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // �������
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // ��������
        if (enumDataItem.DeleteArchives.getValue() != NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex))
        {
            return false;
        }
        nIndex += 2;

        // ���ý��
        enumErrorCode eErrorCode = enumErrorCode.getErrorByInt(ucBuffer[nIndex++]);
        if (null != outParam)
        {
            outParam.setErrorCode(eErrorCode);
        }

        // У��
        iValue = ucBuffer[nIndex++];
        
        // ֡β
        iValue = ucBuffer[nIndex++];
        
        // �����ѽ����ĳ���
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * ������������󣨵ǳ�������֡��
     */
    protected void HandleSpecialRequest()
    {
        byte[]          sentBuffer = new byte[1024];
        IntHolder       iSentFrameLen = new IntHolder(0);


        try
        {
            // ���տͻ��˵ı���
            InputStream inStream = m_sockConn.getInputStream();
            OutputStream outStream = m_sockConn.getOutputStream();
            byte[] recvBuffer = CSocketUtil.readStream(inStream);
            if ((null == recvBuffer) || (recvBuffer.length == 0))
            {
                return;
            }

            // �������յı���
            int         iTotalParsed = 0;
            byte[]      tmpBuffer = new byte[recvBuffer.length];
            IntHolder   iParsedLen = new IntHolder(0);
            while (iTotalParsed < recvBuffer.length)
            {
                // ������δ�����ı��ĵ�������
                System.arraycopy(recvBuffer, iTotalParsed, tmpBuffer, 0, recvBuffer.length - iTotalParsed);

                // ����DCU�ǳ�����֡
                if (parseIoctlRequest_logout(tmpBuffer, recvBuffer.length - iTotalParsed, iParsedLen))
                {
                    // �����ѽ����ı��ĳ���
                    iTotalParsed += iParsedLen.value;

                    // �޸��豸��Ϣ
                    SetDeviceState(enumDeviceState.OffLine);

                    // �ϳ�DCU�ǳ��������Ӧ����
                    if (composeIoctlResponse_logout(sentBuffer, iSentFrameLen))
                    {
                        // ������Ӧ����
                        outStream.write(sentBuffer, 0, iSentFrameLen.value);
                        outStream.flush();
                    }
                }
                // ����DCU����֡
                else if (parseIoctlRequest_heartBeat(tmpBuffer, recvBuffer.length - iTotalParsed, iParsedLen))
                {
                    // �����ѽ����ı��ĳ���
                    iTotalParsed += iParsedLen.value;

                    // �������һ���յ��������ĵĺ�����
                    setLastTimeHeartBeat(System.currentTimeMillis());
                    if (GetDeviceState() == enumDeviceState.OffLine)
                    {
                        SetDeviceState(enumDeviceState.Online);
                    }

                    // �ϳ�����֡��Ӧ����
                    if (composeIoctlResponse_heartBeat(sentBuffer, iSentFrameLen))
                    {
                        // ������Ӧ����
                        outStream.write(sentBuffer, 0, iSentFrameLen.value);
                        outStream.flush();
                    }
                }
                // ��������������ʶ�������
                else 
                {
                    StringBuffer strBuffer = new StringBuffer();
                    
                    strBuffer.append("û�з�������ȴ�յ��˱��ģ�");
                    for(int i = 0; i < recvBuffer.length; i++)
                    {
                        strBuffer.append(String.format("%02x ", recvBuffer[i]));
                    }

                    System.out.println(strBuffer.toString());
                    break;
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    /**
     * ���ָ�����������Ӧ����
     * @param iDataItem ��������
     * @return  ��Ӧ����
     */
    public byte[] getQueryResponseByDataItem(int iDataItem)
    {
        return m_mapQueryCmd2RespFrame.get(iDataItem);
    }
}
