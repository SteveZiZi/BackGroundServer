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
 * DCU设备
 * 
 * @author zgm
 *
 */
public class CDcuDevice extends CAbstractDevice
{
    protected int                   m_iMeasurePoint;            // 测量点
    protected String                m_strAdminPhone;            // 管理员电话

    protected List<CClosedCircuit>  m_lstClosedCircuit;         // 回路列表
    protected List<CControlTask>    m_lstControlTasks;          // 控制任务列表
    protected List<CAlarmLogBase>   m_lstAlarmLogs;             // 告警日志列表
    protected List<CModuleBase>     m_lstArchives;              // 设备档案列表
    protected Map<Integer, Map<String, Integer>> m_mapCCDimVdb; // 回路的Dim-Vdb映射表

    protected byte[]                m_ucSentBuffer4Query;       // 查询命令的发送缓冲区
    protected byte[]                m_ucRecvBuffer4Query;       // 查询命令的接收缓冲区
    protected byte[]                m_ucSentBuffer4Ioctl;       // 控制命令的发送缓冲区
    protected byte[]                m_ucRecvBuffer4Ioctl;       // 控制命令的接收缓冲区
    
    private Map<Integer, byte[]>     m_mapQueryCmd2RespFrame;    // 查询命令和它对应的响应帧

    
    /**
     * 构造函数
     */
    public CDcuDevice()
    {
        this("00000000");
    }
    
    /**
     * 构造函数
     * @param strLogicAddress   逻辑地址
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
     * 获得电压值
     * 
     * @return 电压值
     */
    public double getVoltage()
    {
        double          dMax = Double.MIN_VALUE;
        double          dTemp = Double.MIN_VALUE;

        
        // 循环遍历每一个回路
        for (CClosedCircuit closedCircuit : m_lstClosedCircuit)
        {
            // 获得所有回路中的最高电压值
            dTemp = closedCircuit.getVoltage();
            if (dTemp > dMax)
            {
                dMax = dTemp;
            }
        }

        return dMax;
    }

    /**
     * 获得电流值
     * 
     * @return 电流值
     */
    public double getCurrent()
    {
        double          dTotal = 0;

        // 循环遍历每一个回路
        for (CClosedCircuit closedCircuit : m_lstClosedCircuit)
        {
            // 累加电流值
            dTotal += closedCircuit.getCurrent();
        }

        return dTotal;
    }

    /**
     * 获得功率值
     * 
     * @return 功率
     */
    public double getPower()
    {
        return getVoltage() * getCurrent();
    }

    /**
     * 获得温度值
     * 
     * @return 温度值
     */
    public double getTemperature()
    {
        double          dMax = Double.NEGATIVE_INFINITY;
        double          dTemp = Double.NEGATIVE_INFINITY;

        
        // 循环遍历每一个回路
        for (CClosedCircuit closedCircuit : m_lstClosedCircuit)
        {
            // 获得所有回路中的最高温度值
            dTemp = closedCircuit.getTemperature();
            if (dTemp > dMax)
            {
                dMax = dTemp;
            }
        }

        return dMax;
    }

    /**
     * 获得设备状态
     * 
     * @return 设备状态
     */
    public String getStatus()
    {
        // 检查设备是否在线
        if (m_eDeviceState == enumDeviceState.OffLine)
        {
            return "不在线";
        }

        // 循环检查每一个回路的状态
        for (CClosedCircuit closedCircuit : m_lstClosedCircuit)
        {
            String strStatus = closedCircuit.getStatus();
            if ((null != strStatus) && (0 != strStatus.compareToIgnoreCase("正常")))
            {
                return "异常";
            }
        }

        return "正常";
    }

    /**
     * 获得管理员电话号码
     * 
     * @return 管理员电话
     */
    public String getAdminPhone()
    {
        return m_strAdminPhone;
    }

    /**
     * 获得回路列表
     * 
     * @return 回路列表
     */
    public List<CClosedCircuit> getClosedCircuitList()
    {
        return m_lstClosedCircuit;
    }

    /**
     * 获得指定ID号的回路对象
     * 
     * @param nId 回路ID
     * @return 回路对象
     */
    public CClosedCircuit getClosedCircuitById(int nId)
    {
        // 循环遍历每一个回路
        for (CClosedCircuit closedCircuit : m_lstClosedCircuit)
        {
            // 检查ID号是否匹配
            if (closedCircuit.getID() == nId)
            {
                return closedCircuit;
            }
        }

        return null;
    }

    /**
     * 在指定的回路列表中查找指定ID号的回路
     * 
     * @param lstClosedCircuit 回路列表
     * @param nId 回路ID号
     * @return 回路对象
     */
    private CClosedCircuit getClosedCircuitById(List<CClosedCircuit> lstClosedCircuit, int nId)
    {
        // 检查输入参数
        if (null == lstClosedCircuit)
        {
            return null;
        }

        // 循环遍历每一个回路
        for (CClosedCircuit closedCircuit : lstClosedCircuit)
        {
            // 检查ID号是否匹配
            if (closedCircuit.getID() == nId)
            {
                return closedCircuit;
            }
        }

        return null;
    }

    /**
     * 添加回路对象到容器中
     * 
     * @param closedCircuit 回路对象
     * @return 成功返回true
     */
    public boolean AddClosedCircuit(CClosedCircuit closedCircuit)
    {
        // 检查回路对象是否 已经存在
        if ((null == closedCircuit) || (null != getClosedCircuitById(closedCircuit.getID())))
        {
            return false;
        }

        // 添加回路对象到容器中
        m_lstClosedCircuit.add(closedCircuit);
        return true;
    }

    /**
     * 向指定的回路列表添加回路对象
     * 
     * @param lstClosedCircuit 回路列表
     * @param closedCircuit 回路对象
     * @return 成功返回true
     */
    private boolean AddClosedCircuit(List<CClosedCircuit> lstClosedCircuit, CClosedCircuit closedCircuit)
    {
        // 检查回路对象是否 已经存在
        if ((null == lstClosedCircuit) || (null == closedCircuit) || (null != getClosedCircuitById(lstClosedCircuit, closedCircuit.getID())))
        {
            return false;
        }

        // 添加回路对象到容器中
        lstClosedCircuit.add(closedCircuit);
        return true;
    }

    /**
     * 删除所有的回路对象
     */
    public void DeleteAllClosedCircuits()
    {
        m_lstClosedCircuit.clear();
    }

    /**
     * 获得控制任务列表
     * 
     * @return 控制任务列表
     */
    public List<CControlTask> getContorlTaskList()
    {
        return m_lstControlTasks;
    }

    /**
     * 设置控制任务列表
     * 
     * @param lstTasks 控制任务列表
     */
    public void setControlTaskList(List<CControlTask> lstTasks)
    {
        m_lstControlTasks = lstTasks;
    }

    /**
     * 获得告警日志列表
     * 
     * @return 告警日志列表
     */
    public List<CAlarmLogBase> getAlarmLogList()
    {
        return m_lstAlarmLogs;
    }

    /**
     * 设置告警日志列表
     * 
     * @param lstAlarmLogs 告警日志列表
     */
    public void setAlarmLogList(List<CAlarmLogBase> lstAlarmLogs)
    {
        m_lstAlarmLogs = lstAlarmLogs;
    }

    /**
     * 获得设备档案列表
     * 
     * @return 设备档案列表
     */
    public List<CModuleBase> getArchiveList()
    {
        return m_lstArchives;
    }

    /**
     * 获得默认的Dim-Vdb映射表
     * 
     * @return Dim-Vdb映射表
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
     * 根据回路号，获得该回路对应的Dim-Vdb表
     * 
     * @param iClosedCircuitId 回路号
     * @return 回路对应的Dim-Vdb表
     */
    public Map<String, Integer> getDimVdbMapByCCId(int iClosedCircuitId)
    {
        Map<String, Integer> mapDimVdb = null;
        
        
        // 获得指定回路的Dim-Vdb映射表
        mapDimVdb = m_mapCCDimVdb.get(iClosedCircuitId);
        if ((null == mapDimVdb) && (null != getClosedCircuitById(iClosedCircuitId)))
        {
            // 如果还没有为指定Id的回路创建Dim-Vdb表，那么创建它
            mapDimVdb = getDefaultDimVdbMap();

            // 将新建的Dim-Vdb表添加到集合中
            m_mapCCDimVdb.put(iClosedCircuitId, mapDimVdb);
        }

        return mapDimVdb;
    }

    /**
     * 根据回路电压，获得对应的Dim值
     * 
     * @param iCircuitId 回路号
     * @param iVoltage 回路电压
     * @return 回路电压对应的Dim值
     */
    public String getDimByCircuitVoltage(int iCircuitId, int iVoltage)
    {
        Map<String, Integer> mapDimVdb = null;
        
        
        // 获得指定回路的Dim-Vdb映射表
        mapDimVdb = m_mapCCDimVdb.get(iCircuitId);
        if (null == mapDimVdb)
        {
            // 如果还没有为指定Id的回路创建Dim-Vdb表，那么创建它
            mapDimVdb = getDefaultDimVdbMap();
        }

        // 循环遍历Map容器
        for (Entry<String, Integer> entry : mapDimVdb.entrySet())
        {
            // 检查匹配的电压节点
            if (entry.getValue() == iVoltage)
            {
                return entry.getKey();
            }
        }

        return "";
    }

    /**
     * 设备状态轮询函数，设备状态轮询任务会周期性的调用此函数
     */
    public void run()
    {
        try
        {
            // 如果socket已关闭
            if (CSocketUtil.isSocketClosed(m_sockConn))
            {
                // 标记设备为不在线
                m_eDeviceState = enumDeviceState.OffLine;
                
                // 检查心跳帧间隔
                CPowerSystem powerSystem = CPowerSystem.GetInstance();
                if (System.currentTimeMillis() - m_nLastTimeHeartBeat > powerSystem.getMaxHeartBeatInterval())
                {
                    // 停止设备状态轮询，并将设备状态设置为不在线
                    powerSystem.StopDeviceStatePoll(this);
                }
                
                return;
            }

            // 读所有整流器、继电器的信息
            List<CClosedCircuit> lstClosedCircuit = new LinkedList<CClosedCircuit>();
            if (Query(enumDataItem.ReadAllRectifier.toString(), null, lstClosedCircuit)
                    && Query(enumDataItem.ReadAllRelay.toString(), null, lstClosedCircuit))
            {
                // 设置新的回路集合
                m_lstClosedCircuit = lstClosedCircuit;
                m_eDeviceState = enumDeviceState.Online;
            }

            // 休眠一段时间，以让控制命令有机会拿到锁
            Thread.sleep(200);

            // 获得任务列表
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
                // 保存控制任务列表
                m_lstControlTasks = lstControlTasks;
                m_eDeviceState = enumDeviceState.Online;
            }
            
            /* 
            // 批量读取任务列表（由于批量读取任务列表时DCU的响应报文可能很长，所以暂不使用它）
            if (Query(enumDataItem.ReadAllControlTask.toString(), null, lstControlTasks))
            {
                // 保存控制任务列表
                m_lstControlTasks = lstControlTasks;
                m_eDeviceState = enumDeviceState.Online;
            }
            */

            // 休眠一段时间，以让控制命令有机会拿到锁
            Thread.sleep(200);

            // 读告警日志
            List<CAlarmLogBase> lstAlarmLogs = new LinkedList<CAlarmLogBase>();
            if (Query(enumDataItem.ReadAlarmLog.toString(), new CQueryParam_ReadAlarmLog(new Date(), 0xff), lstAlarmLogs))
            {
                // 保存告警日志列表
                m_lstAlarmLogs = lstAlarmLogs;
                m_eDeviceState = enumDeviceState.Online;
            }

            // 休眠一段时间，以让控制命令有机会拿到锁
            Thread.sleep(200);

            // 读设备档案
            List<CModuleBase> lstArchives = new LinkedList<CModuleBase>();
            if (Query(enumDataItem.ReadAllArchives.toString(), null, lstArchives))
            {
                // 保存设备档案列表
                m_lstArchives = lstArchives;
                m_eDeviceState = enumDeviceState.Online;
            }

            // 休眠一段时间，以让控制命令有机会拿到锁
            Thread.sleep(200);

            // 读取管理员电话
            StringBuffer strAdminPhone = new StringBuffer();
            if (Query(enumDataItem.AdminPhone.toString(), null, strAdminPhone))
            {
                // 保存管理员电话
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
     * 查询
     * 
     * @param strCmdType 命令类型
     * @param inParam 输入参数
     * @param outParam 输出参数
     * @return 成功返回true
     * @throws Exception socket操作失败时抛出异常
     */
    public synchronized boolean Query(String strCmdType, Object inParam, Object outParam)
    {
        boolean         bFlag = false;
        int             iRecvFrameLen = 0;
        

        try
        {
            // 获得输入输出流
            InputStream inStream = m_sockConn.getInputStream();
            OutputStream outStream = m_sockConn.getOutputStream();

            // 先处理缓冲区中的登出帧、心跳帧
            HandleSpecialRequest();

            // 获得指定类型的查询命令
            IntHolder iSentFrameLen = new IntHolder(0);
            if (!getQueryCommand(strCmdType, m_ucSentBuffer4Query, iSentFrameLen, inParam))
            {
                return false;
            }

            // 清空socket接收缓冲区
            CSocketUtil.clearRecvBuffer(m_sockConn);

            // 向设备发送查询命令
            outStream.write(m_ucSentBuffer4Query, 0, iSentFrameLen.value);
            outStream.flush();

            // 接收查询响应
            iRecvFrameLen = inStream.read(m_ucRecvBuffer4Query, 0, m_ucRecvBuffer4Query.length);
            if (iRecvFrameLen == 0)
            {
                return false;
            }
            
            // 解析接收的报文
            int     iTotalParsed = 0;
            byte[]  tmpBuffer = new byte[iRecvFrameLen];
            IntHolder iParsedLen = new IntHolder(0);
            while (iTotalParsed < iRecvFrameLen)
            {
                // 拷贝尚未解析的报文到缓冲区
                System.arraycopy(m_ucRecvBuffer4Query, iTotalParsed, tmpBuffer, 0, iRecvFrameLen - iTotalParsed);

                // 解析指定类型命令的响应报文
                if (parseQueryResponse(strCmdType, tmpBuffer, iRecvFrameLen - iTotalParsed, iParsedLen, outParam))
                {
                    // 递增已解析的报文长度
                    iTotalParsed += iParsedLen.value;
                    
                    // 保存查询命令和它对应的响应帧
                    byte[] resp = new byte[iParsedLen.value];
                    System.arraycopy(tmpBuffer, 0, resp, 0, iParsedLen.value);
                    m_mapQueryCmd2RespFrame.put(enumDataItem.valueOf(strCmdType).getValue(), resp);
                    
                    // 设置返回标志
                    bFlag = true;
                }
                // 解析DCU登出请求帧
                else if (parseIoctlRequest_logout(tmpBuffer, iRecvFrameLen - iTotalParsed, iParsedLen))
                {
                    // 递增已解析的报文长度
                    iTotalParsed += iParsedLen.value;

                    // 修改设备信息
                    SetDeviceState(enumDeviceState.OffLine);

                    // 合成DCU登出请求的响应报文
                    if (composeIoctlResponse_logout(m_ucSentBuffer4Ioctl, iSentFrameLen))
                    {
                        // 发送响应报文
                        outStream.write(m_ucSentBuffer4Ioctl, 0, iSentFrameLen.value);
                        outStream.flush();
                    }
                }
                // 解析DCU心跳帧
                else if (parseIoctlRequest_heartBeat(tmpBuffer, iRecvFrameLen - iTotalParsed, iParsedLen))
                {
                    // 递增已解析的报文长度
                    iTotalParsed += iParsedLen.value;

                    // 设置最近一次收到心跳报文的毫秒数
                    setLastTimeHeartBeat(System.currentTimeMillis());
                    if (GetDeviceState() == enumDeviceState.OffLine)
                    {
                        SetDeviceState(enumDeviceState.Online);
                    }

                    // 合成心跳帧响应报文
                    if (composeIoctlResponse_heartBeat(m_ucSentBuffer4Ioctl, iSentFrameLen))
                    {
                        // 发送响应报文
                        outStream.write(m_ucSentBuffer4Ioctl, 0, iSentFrameLen.value);
                        outStream.flush();
                    }
                }
                // 出现了其他不能识别的命令
                else 
                {
                    StringBuffer strBuffer = new StringBuffer();
                    strBuffer.append("发送");
                    strBuffer.append(strCmdType);
                    strBuffer.append("查询命令，却收到了不匹配或解析不成功的响应报文：");
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
                buffer.append("解析报文异常：");
                for(int i = 0; i < iRecvFrameLen; i++)
                {
                    buffer.append(String.format("%02x ", m_ucRecvBuffer4Query[i]));
                }
                
                buffer.append("，");
            }
            buffer.append("异常信息：");
            buffer.append(e.getMessage());
            
            System.out.println(buffer.toString());
            return false;
        }

        return bFlag;
    }

    /**
     * 获得指定类型的查询命令
     * 
     * @param strCmdType 查询命令类型
     * @param ucBuffer 存放查询命令的缓冲区
     * @param iFrameLen 查询命令的长度
     * @param inParam 输入参数
     * @return 成功返回true
     */
    protected boolean getQueryCommand(String strCmdType, byte[] ucBuffer, IntHolder iFrameLen, Object inParam) throws Exception
    {
        boolean         bRet = false;
        enumDataItem    eCmdType;
        

        // 根据命令类型，执行相应的查询命令合成函数
        eCmdType = enumDataItem.valueOf(strCmdType);
        switch (eCmdType)
        {
        // 读告警日志(0x0019)
        case ReadAlarmLog:
            bRet = getQueryCommand_ReadAlarmLog(ucBuffer, iFrameLen, inParam);
            break;

        // 读管理员电话(0x010D)
        case AdminPhone:
            bRet = getQueryCommand_ReadAdminPhone(ucBuffer, iFrameLen);
            break;

        // 读所有子回路(0x0202)
        case ReadAllRelay:
            bRet = getQueryCommand_ReadAllRelay(ucBuffer, iFrameLen);
            break;

        // 读所有整流器(0x040F)
        case ReadAllRectifier:
            bRet = getQueryCommand_ReadAllRectifier(ucBuffer, iFrameLen);
            break;

        // 读单个控制任务（0x0511 ~ 0x051C）
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

        // 读所有的控制任务(0x051f)
        case ReadAllControlTask:
            bRet = getQueryCommand_ReadAllControlTask(ucBuffer, iFrameLen);
            break;

        // 抄收设备档案信息(0x897F)
        case ReadAllArchives:
            bRet = getQueryCommand_ReadArchives(ucBuffer, iFrameLen, inParam);
            break;

        default:
            break;
        }

        return bRet;
    }

    /**
     * 解析指定类型查询命令的响应帧
     * 
     * @param strCmdType 查询命令类型
     * @param ucBuffer 查询命令的响应帧
     * @param iFrameLen 查询命令响应帧的长度
     * @param iParsedLen 被解析的字节数
     * @param outParam 保存解析结果的对象
     * @return 成功返回true
     */
    protected boolean parseQueryResponse(String strCmdType, byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, Object outParam)
    {
        boolean         bRet = false;
        enumDataItem    eCmdType;
        

        // 根据命令类型，执行相应的查询命令合成函数
        eCmdType = enumDataItem.valueOf(strCmdType);
        switch (eCmdType)
        {
        // 读告警日志
        case ReadAlarmLog:
            bRet = parseQueryResponse_ReadAlarmLog(ucBuffer, iFrameLen, iParsedLen, outParam);
            break;

        // 读管理员电话
        case AdminPhone:
            bRet = parseQueryResponse_ReadAdminPhone(ucBuffer, iFrameLen, iParsedLen, outParam);
            break;

        // 读所有子回路
        case ReadAllRelay:
            bRet = parseQueryResponse_ReadAllRelay(ucBuffer, iFrameLen, iParsedLen, outParam);
            break;

        // 读所有整流器
        case ReadAllRectifier:
            bRet = parseQueryResponse_ReadAllRectifier(ucBuffer, iFrameLen, iParsedLen, outParam);
            break;
       
        // 读单个控制任务（0x0511 ~ 0x051C）
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

        // 读所有的控制任务
        case ReadAllControlTask:
            bRet = parseQueryResponse_ReadAllControlTask(ucBuffer, iFrameLen, iParsedLen, outParam);
            break;

        // 抄收设备档案信息
        case ReadAllArchives:
            bRet = parseQueryResponse_ReadArchives(ucBuffer, iFrameLen, iParsedLen, outParam);
            break;

        default:
            break;
        }

        return bRet;
    }

    /**
     * 获得读取管理员电话的命令
     * 
     * @param ucBuffer 存放查询命令的缓冲区
     * @param iFrameLen 查询命令的长度
     * @return 成功返回true
     * @throws Exception 异常信息
     */
    protected boolean getQueryCommand_ReadAdminPhone(byte[] ucBuffer, IntHolder iFrameLen) throws Exception
    {
        int             nIndex = 0;
        

        // 检查输入参数
        if ((null == ucBuffer) || ucBuffer.length < 23)
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
        ucBuffer[nIndex++] = (byte) enumControlCode.ReadRequest.getValue();

        // 数据长度
        System.arraycopy(NumberUtil.ShortToByte2(10), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 测量点标志
        System.arraycopy(NumberUtil.longToByte8(1), 0, ucBuffer, nIndex, 8);
        nIndex += 8;

        // 数据项编号
        System.arraycopy(NumberUtil.ShortToByte2(enumDataItem.AdminPhone.getValue()), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 校验和
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // 帧尾
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // 设置查询命令的长度
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * 解析读取管理员电话命令的响应帧
     * 
     * @param ucBuffer 查询命令的响应帧
     * @param iFrameLen 查询命令响应帧的长度
     * @param iParsedLen 被解析的字节数
     * @param outParam 输出参数
     * @return 成功返回true
     */
    protected boolean parseQueryResponse_ReadAdminPhone(byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, Object outParam)
    {
        int             nIndex = 0;
        @SuppressWarnings("unused")
        long            iValue = 0;
        

        // 检查输入参数
        if ((null == ucBuffer) || (iFrameLen < 29) || (ucBuffer.length < iFrameLen) || !(outParam instanceof StringBuffer))
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
        iValue = ucBuffer[nIndex++];

        // 起始字符
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // 控制码
        if ((enumControlCode.ReadRequest.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // 数据长度
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // 测量点标志
        iValue = NumberUtil.Byte8ToLong(ucBuffer, nIndex);
        nIndex += 8;

        // 数据项编号
        if (enumDataItem.AdminPhone.getValue() != NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex))
        {
            return false;
        }
        nIndex += 2;

        // 管理员电话
        StringBuffer strAdminPhone = (StringBuffer) outParam;
        String strValue = NumberUtil.BcdArrayToString(ucBuffer, nIndex, 6);
        if (null != strValue)
        {
            // 注意去掉开头的字符‘a’
            strAdminPhone.append(strValue.replaceFirst("^a+", ""));
        }
        nIndex += 6;
        
        // 校验
        iValue = ucBuffer[nIndex++];
        
        // 帧尾
        iValue = ucBuffer[nIndex++];
        
        // 返回已解析的长度
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * 获得读取所有子回路信息的命令
     * 
     * @param ucBuffer 存放查询命令的缓冲区
     * @param iFrameLen 查询命令的长度
     * @return 成功返回true
     */
    protected boolean getQueryCommand_ReadAllRelay(byte[] ucBuffer, IntHolder iFrameLen) throws Exception
    {
        int             nIndex = 0;

        
        // 检查输入参数
        if ((null == ucBuffer) || ucBuffer.length < 23)
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
        ucBuffer[nIndex++] = (byte) enumControlCode.ReadRequest.getValue();

        // 数据长度
        System.arraycopy(NumberUtil.ShortToByte2(10), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 测量点标志
        System.arraycopy(NumberUtil.longToByte8(1), 0, ucBuffer, nIndex, 8);
        nIndex += 8;

        // 数据项编号
        System.arraycopy(NumberUtil.ShortToByte2(enumDataItem.ReadAllRelay.getValue()), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 校验和
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // 帧尾
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // 设置查询命令的长度
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * 解析读取所有子回路信息的响应帧
     * 
     * @param ucBuffer 查询命令的响应帧
     * @param iFrameLen 查询命令响应帧的长度
     * @param iParsedLen 被解析的字节数
     * @param outParam 输出参数
     * @return 成功返回true
     */
    @SuppressWarnings("unchecked")
    protected boolean parseQueryResponse_ReadAllRelay(byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, Object outParam)
    {
        int             nIndex = 0;
        long            iValue = 0;
        int             LENGTH_HEADER = 22;
        int             LENGTH_RECTIFIER = 10;
        int             LENGTH_TAIL = 2;
        

        // 检查输入参数
        if ((null == ucBuffer) || (iFrameLen < LENGTH_HEADER) || (ucBuffer.length < iFrameLen) || !(outParam instanceof LinkedList))
        {
            return false;
        }
        LinkedList<CClosedCircuit> lstClosedCircuit = (LinkedList<CClosedCircuit>) outParam;

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
        iValue = ucBuffer[nIndex++];

        // 起始字符
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // 控制码
        if ((enumControlCode.ReadRequest.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // 数据长度
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // 测量点标志
        iValue = NumberUtil.Byte8ToLong(ucBuffer, nIndex);
        nIndex += 8;

        // 数据项编号
        if (enumDataItem.ReadAllRelay.getValue() != NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex))
        {
            return false;
        }
        nIndex += 2;

        // 继电器数
        iValue = ucBuffer[nIndex++];
        if ((iFrameLen < (LENGTH_HEADER + iValue * LENGTH_RECTIFIER + LENGTH_TAIL)) || (ucBuffer.length < iFrameLen))
        {
            return false;
        }

        // 解析每一个继电器的状态
        for (int i = 0; i < iValue; i++, nIndex += LENGTH_RECTIFIER)
        {
            int iRelayId = ucBuffer[nIndex];
            int iClosedCircuitId = ucBuffer[nIndex + 6];

            // 获得指定ID的回路对象
            CClosedCircuit closedCircuit = getClosedCircuitById(lstClosedCircuit, iClosedCircuitId);
            if (null == closedCircuit)
            {
                closedCircuit = new CClosedCircuit(iClosedCircuitId);
            }

            // 获得指定ID的继电器对象
            CRelay relay = closedCircuit.getRelayById(iRelayId);
            if (null == relay)
            {
                relay = new CRelay(iRelayId);
            }

            // 继电器对象解析状态信息
            if (!relay.parseFrame(ucBuffer, nIndex))
            {
                continue;
            }

            // 添加继电器对象到回路中
            closedCircuit.AddRelay(relay);

            // 添加回路到集合中
            AddClosedCircuit(lstClosedCircuit, closedCircuit);
        }
        
        // 校验
        iValue = ucBuffer[nIndex++];
        
        // 帧尾
        iValue = ucBuffer[nIndex++];
        
        // 返回已解析的长度
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * 获得读取所有整流器信息的命令
     * 
     * @param ucBuffer 存放查询命令的缓冲区
     * @param iFrameLen 查询命令的长度
     * @return 成功返回true
     */
    protected boolean getQueryCommand_ReadAllRectifier(byte[] ucBuffer, IntHolder iFrameLen) throws Exception
    {
        int             nIndex = 0;
        

        // 检查输入参数
        if ((null == ucBuffer) || ucBuffer.length < 23)
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
        ucBuffer[nIndex++] = (byte) enumControlCode.ReadRequest.getValue();

        // 数据长度
        System.arraycopy(NumberUtil.ShortToByte2(10), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 测量点标志
        System.arraycopy(NumberUtil.longToByte8(1), 0, ucBuffer, nIndex, 8);
        nIndex += 8;

        // 数据项编号
        System.arraycopy(NumberUtil.ShortToByte2(enumDataItem.ReadAllRectifier.getValue()), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 校验和
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // 帧尾
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // 设置查询命令的长度
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * 解析读取所有整流器信息的响应帧
     * 
     * @param ucBuffer 查询命令的响应帧
     * @param iFrameLen 查询命令响应帧的长度
     * @param iParsedLen 被解析的字节数
     * @param outParam 输出参数
     * @return 成功返回true
     */
    @SuppressWarnings("unchecked")
    protected boolean parseQueryResponse_ReadAllRectifier(byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, Object outParam)
    {
        int             nIndex = 0;
        long            iValue = 0;
        int             LENGTH_HEADER = 22;
        int             LENGTH_RECTIFIER = 12;
        int             LENGTH_TAIL = 2;

        
        // 检查输入参数
        if ((null == ucBuffer) || (iFrameLen < LENGTH_HEADER) || (ucBuffer.length < iFrameLen) || !(outParam instanceof LinkedList))
        {
            return false;
        }
        LinkedList<CClosedCircuit> lstClosedCircuit = (LinkedList<CClosedCircuit>) outParam;

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
        iValue = ucBuffer[nIndex++];

        // 起始字符
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // 控制码
        if ((enumControlCode.ReadRequest.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // 数据长度
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // 测量点标志
        iValue = NumberUtil.Byte8ToLong(ucBuffer, nIndex);
        nIndex += 8;

        // 数据项编号
        if (enumDataItem.ReadAllRectifier.getValue() != NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex))
        {
            return false;
        }
        nIndex += 2;

        // 整流器数
        iValue = ucBuffer[nIndex++];
        if ((iFrameLen < (LENGTH_HEADER + iValue * LENGTH_RECTIFIER + LENGTH_TAIL)) || (ucBuffer.length < iFrameLen))
        {
            return false;
        }

        // 解析每一个整流器的状态
        for (int i = 0; i < iValue; i++, nIndex += LENGTH_RECTIFIER)
        {
            int iRectifierId = ucBuffer[nIndex];
            int iClosedCircuitId = ucBuffer[nIndex + 2];

            // 获得指定ID的回路对象
            CClosedCircuit closedCircuit = getClosedCircuitById(lstClosedCircuit, iClosedCircuitId);
            if (null == closedCircuit)
            {
                closedCircuit = new CClosedCircuit(iClosedCircuitId);
            }

            // 获得指定ID的整流器对象
            CRectifier rectifier = closedCircuit.getRectifierById(iRectifierId);
            if (null == rectifier)
            {
                rectifier = new CRectifier(iRectifierId);
            }

            // 整流器对象解析状态信息
            if (!rectifier.parseFrame(ucBuffer, nIndex))
            {
                continue;
            }

            // 添加整流器对象到回路中
            closedCircuit.AddRetifier(rectifier);

            // 添加回路到集合中
            AddClosedCircuit(lstClosedCircuit, closedCircuit);
        }

        // 校验
        iValue = ucBuffer[nIndex++];
        
        // 帧尾
        iValue = ucBuffer[nIndex++];
        
        // 返回已解析的长度
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * 读单个控制任务
     * 
     * @param eDataItem 数据项标志
     * @param ucBuffer 存放查询命令的缓冲区
     * @param iFrameLen 查询命令的长度
     * @return 成功返回true
     * @throws Exception 异常信息
     */
    protected boolean getQueryCommand_ReadSingleControlTask(enumDataItem eDataItem, byte[] ucBuffer, IntHolder iFrameLen) throws Exception
    {
        int             nIndex = 0;
        

        // 检查输入参数
        if ((null == ucBuffer) 
                || (ucBuffer.length < 23)
                || (eDataItem.getValue() < enumDataItem.ReadSingleControlTask_1.getValue())
                || (eDataItem.getValue() > enumDataItem.ReadSingleControlTask_12.getValue()))
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
        ucBuffer[nIndex++] = (byte) enumControlCode.ReadRequest.getValue();

        // 数据长度
        System.arraycopy(NumberUtil.ShortToByte2(10), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 测量点标志
        System.arraycopy(NumberUtil.longToByte8(1), 0, ucBuffer, nIndex, 8);
        nIndex += 8;

        // 数据项编号
        System.arraycopy(NumberUtil.ShortToByte2(eDataItem.getValue()), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 校验和
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // 帧尾
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // 设置查询命令的长度
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * 解析读取所有控制任务的响应帧
     * 
     * @param eDataItem 数据项标志
     * @param ucBuffer 查询命令的响应帧
     * @param iFrameLen 响应帧的长度
     * @param iParsedLen 被解析的字节数
     * @param outParam 存放所有控制任务的列表
     * @return 解析成功返回true
     */
    protected boolean parseQueryResponse_ReadSingleControlTask(enumDataItem eDataItem, byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, Object outParam)
    {
        int             nIndex          = 0;
        @SuppressWarnings("unused")
        long            iValue          = 0;
        int             LENGTH_HEADER   = 22;
        int             LENGTH_TASK     = 18;
        int             LENGTH_TAIL     = 2;
        

        // 检查输入参数
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
        iValue = ucBuffer[nIndex++];

        // 起始字符
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // 控制码
        if ((enumControlCode.ReadRequest.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // 数据长度
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // 测量点标志
        iValue = NumberUtil.Byte8ToLong(ucBuffer, nIndex);
        nIndex += 8;

        // 数据项编号
        if (eDataItem.getValue() != NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex))
        {
            return false;
        }
        nIndex += 2;
        
        // 根据报文长度，判断是否存在控制任务
        if (iFrameLen >= (LENGTH_HEADER + LENGTH_TASK + LENGTH_TAIL))
        {
            try
            {
                // 解析控制任务
                CControlTask task = new CControlTask(this);
                IntHolder iImplPlanLen = new IntHolder();
                if (!task.Decode(ucBuffer, nIndex, iImplPlanLen))
                {
                    return false;
                }

                // 添加任务到集合中
                lstTasks.add(task);
                nIndex += iImplPlanLen.value;
            }
            catch (ParseException e)
            {
                System.err.println(e.getMessage());
                return false;
            }
        }

        // 校验
        iValue = ucBuffer[nIndex++];
        
        // 帧尾
        iValue = ucBuffer[nIndex++];
        
        // 返回已解析的长度
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * 读所有的控制任务
     * 
     * @param ucBuffer 存放查询命令的缓冲区
     * @param iFrameLen 查询命令的长度
     * @return 成功返回true
     * @throws Exception 异常信息
     */
    protected boolean getQueryCommand_ReadAllControlTask(byte[] ucBuffer, IntHolder iFrameLen) throws Exception
    {
        int             nIndex = 0;
        

        // 检查输入参数
        if ((null == ucBuffer) || ucBuffer.length < 23)
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
        ucBuffer[nIndex++] = (byte) enumControlCode.ReadRequest.getValue();

        // 数据长度
        System.arraycopy(NumberUtil.ShortToByte2(10), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 测量点标志
        System.arraycopy(NumberUtil.longToByte8(1), 0, ucBuffer, nIndex, 8);
        nIndex += 8;

        // 数据项编号
        System.arraycopy(NumberUtil.ShortToByte2(enumDataItem.ReadAllControlTask.getValue()), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 校验和
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // 帧尾
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // 设置查询命令的长度
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * 解析读取所有控制任务的响应帧
     * 
     * @param ucBuffer 查询命令的响应帧
     * @param iFrameLen 响应帧的长度
     * @param iParsedLen 被解析的字节数
     * @param outParam 存放所有控制任务的列表
     * @return 解析成功返回true
     */
    @SuppressWarnings("unchecked")
    protected boolean parseQueryResponse_ReadAllControlTask(byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, Object outParam)
    {
        int             nIndex          = 0;
        long            iValue          = 0;
        int             LENGTH_HEADER   = 22;
        int             LENGTH_TASK     = 18;
        int             LENGTH_TAIL     = 2;
        

        // 检查输入参数
        if ((null == ucBuffer) || (iFrameLen < LENGTH_HEADER) || (ucBuffer.length < iFrameLen) || !(outParam instanceof LinkedList))
        {
            return false;
        }
        LinkedList<CControlTask> lstTasks = (LinkedList<CControlTask>) outParam;

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
        iValue = ucBuffer[nIndex++];

        // 起始字符
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // 控制码
        if ((enumControlCode.ReadRequest.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // 数据长度
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // 测量点标志
        iValue = NumberUtil.Byte8ToLong(ucBuffer, nIndex);
        nIndex += 8;

        // 数据项编号
        if (enumDataItem.ReadAllControlTask.getValue() != NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex))
        {
            return false;
        }
        nIndex += 2;

        // 任务数
        iValue = ucBuffer[nIndex++];
        if ((iFrameLen < (LENGTH_HEADER + iValue * LENGTH_TASK + LENGTH_TAIL)) || (ucBuffer.length < iFrameLen))
        {
            return false;
        }

        // 解析每一个控制任务
        try
        {
            IntHolder iImplPlanLen = new IntHolder();
            for (int i = 0; i < iValue; i++)
            {
                // 解析控制任务
                CControlTask task = new CControlTask(this);
                if (!task.Decode(ucBuffer, nIndex, iImplPlanLen))
                {
                    return false;
                }

                // 添加任务到集合中
                lstTasks.add(task);
                nIndex += iImplPlanLen.value;
            }
        }
        catch (ParseException e)
        {
            System.err.println(e.getMessage());
            return false;
        }
        
        // 校验
        iValue = ucBuffer[nIndex++];
        
        // 帧尾
        iValue = ucBuffer[nIndex++];
        
        // 返回已解析的长度
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * 获得抄收设备档案信息的命令
     * 
     * @param ucBuffer 存放查询命令的缓冲区
     * @param iFrameLen 查询命令的长度
     * @param inParam 抄收设备档案信息命令的参数
     * @return 成功返回true
     * @throws Exception 异常信息
     */
    protected boolean getQueryCommand_ReadArchives(byte[] ucBuffer, IntHolder iFrameLen, Object inParam) throws Exception
    {
        int             nIndex = 0;
        

        // 检查输入参数
        if ((null == ucBuffer) || (ucBuffer.length < 23))
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
        ucBuffer[nIndex++] = (byte) enumControlCode.ReadRequest.getValue();

        // 数据长度
        System.arraycopy(NumberUtil.ShortToByte2(10), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 测量点标志
        System.arraycopy(NumberUtil.longToByte8(1), 0, ucBuffer, nIndex, 8);
        nIndex += 8;

        // 数据项编号
        System.arraycopy(NumberUtil.ShortToByte2(enumDataItem.ReadAllArchives.getValue()), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 校验和
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // 帧尾
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // 设置查询命令的长度
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * 解析抄收设备档案信息的响应帧
     * 
     * @param ucBuffer 存放响应帧的缓冲区
     * @param iFrameLen 响应帧的长度
     * @param iParsedLen 被解析的字节数
     * @param outParam 存放设备档案信息的列表
     * @return 成功返回true
     */
    protected boolean parseQueryResponse_ReadArchives(byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, Object outParam)
    {
        int             nIndex = 0;
        @SuppressWarnings("unused")
        long            iValue = 0;
        

        // 检查输入参数
        if ((null == ucBuffer) || (iFrameLen < 22) || (ucBuffer.length < iFrameLen) || !(outParam instanceof List))
        {
            return false;
        }
        @SuppressWarnings("unchecked")
        List<CModuleBase> lstModules = (List<CModuleBase>) outParam;

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
        iValue = ucBuffer[nIndex++];

        // 起始字符
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // 控制码
        if ((enumControlCode.ReadRequest.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // 数据长度
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // 测量点标志
        iValue = NumberUtil.Byte8ToLong(ucBuffer, nIndex);
        nIndex += 8;

        // 数据项编号
        if (enumDataItem.ReadAllArchives.getValue() != NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex))
        {
            return false;
        }
        nIndex += 2;

        // 档案个数
        int iModuleNum = ucBuffer[nIndex++];

        // 设备档案内容
        lstModules.clear();
        for (int i = 0; i < iModuleNum; i++)
        {
            CModuleBase module = new CModuleBase();
            IntHolder iParseBytes = new IntHolder(0);

            // 解析设备节点报文
            if (!module.parseFrame(ucBuffer, nIndex, iParseBytes))
            {
                return false;
            }

            // 添加设备节点
            lstModules.add(module);
            nIndex += iParseBytes.value;
        }

        // 校验
        iValue = ucBuffer[nIndex++];
        
        // 帧尾
        iValue = ucBuffer[nIndex++];
        
        // 返回已解析的长度
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * 获得读取告警日志的命令
     * 
     * @param ucBuffer 存放查询命令的缓冲区
     * @param iFrameLen 查询命令的长度
     * @param inParam 告警日志抄读命令的参数
     * @return 成功返回true
     * @throws Exception 异常信息
     */
    protected boolean getQueryCommand_ReadAlarmLog(byte[] ucBuffer, IntHolder iFrameLen, Object inParam) throws Exception
    {
        int             nIndex = 0;
        CQueryParam_ReadAlarmLog    ralParam;
        

        // 检查输入参数
        if ((null == ucBuffer) || (ucBuffer.length < 19) || !(inParam instanceof CQueryParam_ReadAlarmLog))
        {
            return false;
        }
        ralParam = (CQueryParam_ReadAlarmLog) inParam;

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
        ucBuffer[nIndex++] = (byte) enumControlCode.ReadAlarmLog.getValue();

        // 数据长度
        System.arraycopy(NumberUtil.ShortToByte2(6), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 告警起始时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
        System.arraycopy(NumberUtil.StringToBcdArray(sdf.format(ralParam.getStartTime())), 0, ucBuffer, nIndex, 5);
        nIndex += 5;

        // 告警数据点数
        ucBuffer[nIndex++] = (byte) ralParam.getLogCount();

        // 校验和
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // 帧尾
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // 设置查询命令的长度
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * 解析告警日志的响应帧
     * 
     * @param ucBuffer 存放响应帧的缓冲区
     * @param iFrameLen 响应帧的长度
     * @param iParsedLen 被解析的字节数
     * @param outParam 存放告警日志的列表
     * @return 成功返回true
     */
    @SuppressWarnings({ "deprecation", "unchecked" })
    protected boolean parseQueryResponse_ReadAlarmLog(byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, Object outParam)
    {
        int             nIndex = 0;
        @SuppressWarnings("unused")
        long            iValue = 0;
        

        // 检查输入参数
        if ((null == ucBuffer) || (iFrameLen < 14) || (ucBuffer.length < iFrameLen) || !(outParam instanceof LinkedList))
        {
            return false;
        }
        LinkedList<CAlarmLogBase> lstLogs = (LinkedList<CAlarmLogBase>) outParam;

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
        iValue = ucBuffer[nIndex++];

        // 起始字符
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // 控制码
        if ((enumControlCode.ReadAlarmLog.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // 数据长度
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // 告警数量
        int iAlarmCount = ucBuffer[nIndex++];
        if (iFrameLen < (14 + iAlarmCount * 13))
        {
            return false;
        }

        // 循环解析每一个告警数据
        for (int i = 0; i < iAlarmCount; i++)
        {
            // 测量点表号（6字节）
            nIndex += 6;

            // 年
            int iYear = 100 + NumberUtil.BcdByteToNormalByte(ucBuffer[nIndex++]);
            // 月
            int iMonth = NumberUtil.BcdByteToNormalByte(ucBuffer[nIndex++]) - 1;
            // 日
            int iDay = NumberUtil.BcdByteToNormalByte(ucBuffer[nIndex++]);
            // 时
            int iHour = NumberUtil.BcdByteToNormalByte(ucBuffer[nIndex++]);
            // 分
            int iMinute = NumberUtil.BcdByteToNormalByte(ucBuffer[nIndex++]);

            // 告警编码
            int iAlarmType = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
            nIndex += 2;

            // 根据报警编码，生成告警对象，解析告警参数
            IntHolder iParseBytes = new IntHolder(0);
            CAlarmLogBase alarmLog = CAlarmLogFactory.getInstance().CreateAlarmLog(iAlarmType);
            if ((null == alarmLog) || !alarmLog.parseFrame(ucBuffer, nIndex, iParseBytes))
            {
                return false;
            }
            nIndex += iParseBytes.value;

            // 设置告警发生时间
            alarmLog.setAlarmDate(new Date(iYear, iMonth, iDay, iHour, iMinute));

            // 添加告警日志对象到容器中
            lstLogs.add(alarmLog);
        }
        
        // 校验
        iValue = ucBuffer[nIndex++];
        
        // 帧尾
        iValue = ucBuffer[nIndex++];
        
        // 返回已解析的长度
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * 生产校验码
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
     * 向DCU发送控制命令
     * @param ucRequestFrame    存放控制命令的缓冲区
     * @param iRequestLen       控制命令的长度
     * @param ucRespFrame       存放响应命令的缓冲区
     * @param iRespLen          响应命令的长度
     * @return  收发成功返回true
     */
    public synchronized boolean Ioctl(byte[] ucRequestFrame, int iRequestLen, byte[] ucRespFrame, IntHolder iRespLen)
    {
        boolean         bFlag = false;
        
        
        // 检查输入参数
        if ((null == ucRequestFrame) 
                || (ucRequestFrame.length == 0)
                || (null == ucRespFrame)
                || (null == iRespLen))
        {
            return false;
        }
        
        try
        {
            // 获得输入输出流
            InputStream inStream = m_sockConn.getInputStream();
            OutputStream outStream = m_sockConn.getOutputStream();

            // 先处理缓冲区中的登出帧、心跳帧
            HandleSpecialRequest();
            
            // 清空socket接收缓冲区
            CSocketUtil.clearRecvBuffer(m_sockConn);
            
            // 向设备发送控制命令
            outStream.write(ucRequestFrame, 0, iRequestLen);
            outStream.flush();

            // 接收控制响应
            iRespLen.value = inStream.read(ucRespFrame, 0, ucRespFrame.length);
            if (iRespLen.value == 0)
            {
                return false;
            }

            // 标记为成功
            bFlag = true;
        }
        catch(Exception e)
        {
            StringBuffer strBuffer = new StringBuffer();
            strBuffer.append("处理Pad控制命令时出现异常：");
            strBuffer.append(e.getMessage());
            System.out.println(strBuffer.toString());
        }
        
        return bFlag;
    }

    /**
     * 设置
     * 
     * @param strCmdType 控制命令类型
     * @param inParam 输入参数
     * @param outParam 输出参数
     * @return 成功返回true
     */
    public synchronized boolean Ioctl(String strCmdType, Object inParam, CIoctlResult outParam)
    {
        boolean         bFlag = false;
        int             iRecvFrameLen = 0;

        
        try
        {
            // 获得输入输出流
            InputStream inStream = m_sockConn.getInputStream();
            OutputStream outStream = m_sockConn.getOutputStream();

            // 先处理缓冲区中的登出帧、心跳帧
            HandleSpecialRequest();

            // 获得指定类型的控制命令
            IntHolder iSentFrameLen = new IntHolder(0);
            if (!getIoctlCommand(strCmdType, m_ucSentBuffer4Ioctl, iSentFrameLen, inParam))
            {
                return false;
            }

            // 清空socket接收缓冲区
            CSocketUtil.clearRecvBuffer(m_sockConn);

            // 向设备发送控制命令
            outStream.write(m_ucSentBuffer4Ioctl, 0, iSentFrameLen.value);
            outStream.flush();

            // 接收控制响应
            iRecvFrameLen = inStream.read(m_ucRecvBuffer4Ioctl, 0, m_ucRecvBuffer4Ioctl.length);
            if (iRecvFrameLen == 0)
            {
                return false;
            }
            
            // 解析接收的报文
            int     iTotalParsed = 0;
            byte[]  tmpBuffer = new byte[iRecvFrameLen];
            IntHolder iParsedLen = new IntHolder(0);
            while (iTotalParsed < iRecvFrameLen)
            {
                // 拷贝尚未解析的报文到缓冲区
                System.arraycopy(m_ucRecvBuffer4Ioctl, iTotalParsed, tmpBuffer, 0, iRecvFrameLen - iTotalParsed);

                // 解析指定类型命令的响应报文
                if (parseIoctlResponse(strCmdType, tmpBuffer, iRecvFrameLen - iTotalParsed, iParsedLen, outParam))
                {
                    // 递增已解析的报文长度
                    iTotalParsed += iParsedLen.value;
                    
                    // 设置返回标志
                    bFlag = true;
                }
                // 解析DCU登出请求帧
                else if (parseIoctlRequest_logout(tmpBuffer, iRecvFrameLen - iTotalParsed, iParsedLen))
                {
                    // 递增已解析的报文长度
                    iTotalParsed += iParsedLen.value;

                    // 修改设备信息
                    SetDeviceState(enumDeviceState.OffLine);

                    // 合成DCU登出请求的响应报文
                    if (composeIoctlResponse_logout(m_ucSentBuffer4Ioctl, iSentFrameLen))
                    {
                        // 发送响应报文
                        outStream.write(m_ucSentBuffer4Ioctl, 0, iSentFrameLen.value);
                        outStream.flush();
                    }
                }
                // 解析DCU心跳帧
                else if (parseIoctlRequest_heartBeat(tmpBuffer, iRecvFrameLen - iTotalParsed, iParsedLen))
                {
                    // 递增已解析的报文长度
                    iTotalParsed += iParsedLen.value;

                    // 设置最近一次收到心跳报文的毫秒数
                    setLastTimeHeartBeat(System.currentTimeMillis());
                    if (GetDeviceState() == enumDeviceState.OffLine)
                    {
                        SetDeviceState(enumDeviceState.Online);
                    }

                    // 合成心跳帧响应报文
                    if (composeIoctlResponse_heartBeat(m_ucSentBuffer4Ioctl, iSentFrameLen))
                    {
                        // 发送响应报文
                        outStream.write(m_ucSentBuffer4Ioctl, 0, iSentFrameLen.value);
                        outStream.flush();
                    }
                }
                // 出现了其他不能识别的命令
                else 
                {
                    StringBuffer strBuffer = new StringBuffer();
                    strBuffer.append("发送");
                    strBuffer.append(strCmdType);
                    strBuffer.append("控制命令，却收到了不匹配或解析不成功的响应报文：");
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
                strBuffer.append("解析报文异常：");
                for(int i = 0; i < iRecvFrameLen; i++)
                {
                    strBuffer.append(String.format("%02x ", m_ucRecvBuffer4Ioctl[i]));
                }
                
                strBuffer.append("，");
            }
            strBuffer.append("异常信息：");
            strBuffer.append(e.getMessage());
            
            System.out.println(strBuffer.toString());
            return false;
        }

        return bFlag;
    }

    /**
     * 获得控制命令
     * 
     * @param strCmdType 控制命令类型
     * @param ucBuffer 存放控制命令的缓冲区
     * @param iFrameLen 控制命令长度
     * @param inParam 输入参数
     * @return 成功返回true
     * @throws Exception 异常信息
     */
    protected boolean getIoctlCommand(String strCmdType, byte[] ucBuffer, IntHolder iFrameLen, Object inParam) throws Exception
    {
        boolean         bRet = false;
        enumDataItem    eCmdType;
        

        // 根据命令类型，执行相应的控制命令合成函数
        eCmdType = enumDataItem.valueOf(strCmdType);
        switch (eCmdType)
        {
        // 设置管理员电话
        case AdminPhone:
            bRet = getIoctlCommand_SetAdminPhone(ucBuffer, iFrameLen, inParam);
            break;

        // 远程控制
        case RemoteControl:
            bRet = getIoctlCommand_RemoteControl(ucBuffer, iFrameLen, inParam);
            break;

        // 程控任务
        case WriteControlTask:
            bRet = getIoctlCommand_WriteControlTask(ucBuffer, iFrameLen, inParam);
            break;

        // 批量删除程控任务
        case DeleteControlTasks:
            bRet = getIoctlCommand_DeleteControlTasks(ucBuffer, iFrameLen, inParam);
            break;

        // 校时
        case Timing:
            bRet = getIoctlCommand_Timing(ucBuffer, iFrameLen, inParam);
            break;

        // 批量增加设备档案
        case AddArchives:
            bRet = getIoctlCommand_AddArchives(ucBuffer, iFrameLen, inParam);
            break;

        // 批量删除设备档案
        case DeleteArchives:
            bRet = getIoctlCommand_DeleteArchives(ucBuffer, iFrameLen, inParam);
            break;

        default:
            break;
        }

        return bRet;
    }

    /**
     * 解析控制命令响应
     * 
     * @param strCmdType 控制命令类型
     * @param ucBuffer 控制命令响应帧
     * @param iFrameLen 控制命令响应帧长度
     * @param iParsedLen 解析的字节数
     * @param outParam 输出参数
     * @return 成功返回true
     */
    protected boolean parseIoctlResponse(String strCmdType, byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, CIoctlResult outParam)
    {
        boolean         bRet = false;
        enumDataItem    eCmdType;
        

        // 根据命令类型，执行相应的控制命令合成函数
        eCmdType = enumDataItem.valueOf(strCmdType);
        switch (eCmdType)
        {
        // 设置管理员电话
        case AdminPhone:
            bRet = parseIoctlResponse_SetAdminPhone(ucBuffer, iFrameLen, iParsedLen, outParam);
            break;

        // 远程控制
        case RemoteControl:
            bRet = parseIoctlResponse_RemoteControl(ucBuffer, iFrameLen, iParsedLen, outParam);
            break;

        // 程控任务
        case WriteControlTask:
            bRet = parseIoctlResponse_WriteControlTask(ucBuffer, iFrameLen, iParsedLen, outParam);
            break;

        // 批量删除程控任务
        case DeleteControlTasks:
            bRet = parseIoctlResponse_DeleteControlTasks(ucBuffer, iFrameLen, iParsedLen, outParam);
            break;

        // 校时
        case Timing:
            bRet = parseIoctlResponse_Timing(ucBuffer, iFrameLen, iParsedLen, outParam);
            break;

        // 批量增加设备档案
        case AddArchives:
            bRet = parseIoctlResponse_AddArchives(ucBuffer, iFrameLen, iParsedLen, outParam);
            break;

        // 批量删除设备档案
        case DeleteArchives:
            bRet = parseIoctlResponse_DeleteArchives(ucBuffer, iFrameLen, iParsedLen, outParam);
            break;

        default:
            break;
        }

        return bRet;
    }

    /**
     * 获得设置管理员电话号码的控制命令
     * 
     * @param ucBuffer 存放控制命令的缓冲区
     * @param iFrameLen 合成的控制命令长度
     * @param inParam 输入参数(String类型)
     * @return 成功返回true
     * @throws Exception 异常信息
     */
    protected boolean getIoctlCommand_SetAdminPhone(byte[] ucBuffer, IntHolder iFrameLen, Object inParam) throws Exception
    {
        int             nIndex = 0;
        String          strPhone = null;
        

        // 检查输入参数
        if ((null == ucBuffer) || (ucBuffer.length < 27) || !(inParam instanceof String))
        {
            return false;
        }

        // 管理员电话号码
        strPhone = (String) inParam;
        if (strPhone.length() == 0)
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
        ucBuffer[nIndex++] = (byte) enumControlCode.WriteRequest.getValue();

        // 数据长度
        System.arraycopy(NumberUtil.ShortToByte2(14), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 测量点号
        System.arraycopy(NumberUtil.ShortToByte2(0), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 权限等级
        ucBuffer[nIndex++] = 0x11;

        // 密码
        ucBuffer[nIndex++] = 0x11;
        ucBuffer[nIndex++] = 0x11;
        ucBuffer[nIndex++] = 0x11;

        // 数据项编号
        System.arraycopy(NumberUtil.ShortToByte2(enumDataItem.AdminPhone.getValue()), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 在手机号的前面附加字符‘A’，确保添加后的字符串长度为12
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < (12 - strPhone.length()); i++)
        {
            sb.append("A");
        }
        sb.append(strPhone);

        // 填充手机号到报文中
        String strData = sb.toString();
        for (int i = 0; i < 6; i++)
        {
            ucBuffer[nIndex++] = (byte) Integer.parseInt(strData.substring(i * 2, (i + 1) * 2), 16);
        }

        // 校验和
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // 帧尾
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // 设置查询命令的长度
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * 解析设置管理员电话号码的响应命令
     * 
     * @param ucBuffer 响应报文
     * @param iFrameLen 响应报文长度
     * @param iParsedLen 解析的字节数
     * @param outParam 输出参数
     * @return 成功返回true
     */
    protected boolean parseIoctlResponse_SetAdminPhone(byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, CIoctlResult outParam)
    {
        int             nIndex = 0;
        @SuppressWarnings("unused")
        long            iValue = 0;
        

        // 检查输入参数
        if ((null == ucBuffer) || (iFrameLen < 18) || (ucBuffer.length < iFrameLen))
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
        iValue = ucBuffer[nIndex++];

        // 起始字符
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // 控制码
        if ((enumControlCode.WriteRequest.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // 数据长度
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // 测量点号
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // 数据项编号
        if (enumDataItem.AdminPhone.getValue() != NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex))
        {
            return false;
        }
        nIndex += 2;

        // 设置结果
        enumErrorCode eErrorCode = enumErrorCode.getErrorByInt(ucBuffer[nIndex++]);
        if (null != outParam)
        {
            outParam.setErrorCode(eErrorCode);
        }

        // 校验
        iValue = ucBuffer[nIndex++];
        
        // 帧尾
        iValue = ucBuffer[nIndex++];
        
        // 返回已解析的长度
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * 获得远程控制命令的报文
     * 
     * @param ucBuffer 存放控制命令的缓冲区
     * @param iFrameLen 合成的控制命令长度
     * @param inParam 输入参数（List<CIoctlParam_RemoteControl>类型）
     * @return 成功返回true
     * @throws Exception 异常信息
     */
    @SuppressWarnings("unchecked")
    protected boolean getIoctlCommand_RemoteControl(byte[] ucBuffer, IntHolder iFrameLen, Object inParam) throws Exception
    {
        int             nIndex = 0;
        List<CIoctlParam_RemoteControl>     m_lstParams;
        

        // 检查输入参数
        if ((null == ucBuffer) || !(inParam instanceof List))
        {
            return false;
        }

        // 检查缓冲区长度
        m_lstParams = (List<CIoctlParam_RemoteControl>) inParam;
        if (ucBuffer.length < (22 + (m_lstParams.size() * 7)))
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
        ucBuffer[nIndex++] = (byte) enumControlCode.WriteRequest.getValue();

        // 数据长度
        System.arraycopy(NumberUtil.ShortToByte2(9 + m_lstParams.size() * 7), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 测量点号
        System.arraycopy(NumberUtil.ShortToByte2(0), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 权限等级
        ucBuffer[nIndex++] = 0x11;

        // 密码
        ucBuffer[nIndex++] = 0x11;
        ucBuffer[nIndex++] = 0x11;
        ucBuffer[nIndex++] = 0x11;

        // 数据项编号
        System.arraycopy(NumberUtil.ShortToByte2(enumDataItem.RemoteControl.getValue()), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 遥控路数
        ucBuffer[nIndex++] = (byte) m_lstParams.size();

        // 循环遍历每一个控制子路
        for (CIoctlParam_RemoteControl ctlParam : m_lstParams)
        {
            // 设备号（回路号、继电器号）
            ucBuffer[nIndex++] = (byte) ctlParam.getDeviceId();

            // 子回路号
            ucBuffer[nIndex++] = (byte) ctlParam.getSubLoopId();

            // 特殊功能字
            ucBuffer[nIndex++] = (byte) ctlParam.getSpecificFunc().getValue();

            // 数据项内容（电压值）
            System.arraycopy(NumberUtil.ShortToBcdByte2(ctlParam.getDataItem() * 10), 0, ucBuffer, nIndex, 2);
            nIndex += 2;

            // 数据项内容（电流值）
            System.arraycopy(NumberUtil.ShortToBcdByte2(0), 0, ucBuffer, nIndex, 2);
            nIndex += 2;
        }

        // 校验和
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // 帧尾
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // 设置查询命令的长度
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * 解析远程控制命令的响应命令
     * 
     * @param ucBuffer 响应报文
     * @param iFrameLen 响应报文长度
     * @param iParsedLen 解析的字节数
     * @param outParam 输出参数
     * @return 成功返回true
     */
    protected boolean parseIoctlResponse_RemoteControl(byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, CIoctlResult outParam)
    {
        int             nIndex = 0;
        @SuppressWarnings("unused")
        long            iValue = 0;
        

        // 检查输入参数
        if ((null == ucBuffer) || (iFrameLen < 18) || (ucBuffer.length < iFrameLen))
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
        iValue = ucBuffer[nIndex++];

        // 起始字符
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // 控制码
        if ((enumControlCode.WriteRequest.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // 数据长度
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // 测量点号
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // 数据项编号
        if (enumDataItem.RemoteControl.getValue() != NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex))
        {
            return false;
        }
        nIndex += 2;

        // 设置结果
        enumErrorCode eErrorCode = enumErrorCode.getErrorByInt(ucBuffer[nIndex++]);
        if (null != outParam)
        {
            outParam.setErrorCode(eErrorCode);
        }

        // 校验
        iValue = ucBuffer[nIndex++];
        
        // 帧尾
        iValue = ucBuffer[nIndex++];
        
        // 返回已解析的长度
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * 获得设置程控任务的报文
     * 
     * @param ucBuffer 存放控制命令的缓冲区
     * @param iFrameLen 合成的控制命令长度
     * @param inParam 输入参数（CIoctlParam_WriteControlTask类型）
     * @return 成功返回true
     * @throws Exception 异常信息
     */
    protected boolean getIoctlCommand_WriteControlTask(byte[] ucBuffer, IntHolder iFrameLen, Object inParam) throws Exception
    {
        int             nIndex      = 0;
        CControlTask    m_ctlTask;
        

        // 检查输入参数
        if ((null == ucBuffer) || !(inParam instanceof CControlTask))
        {
            return false;
        }

        // 编码输入参数
        byte[] ucDataField = new byte[2048];
        IntHolder iDataFieldLen = new IntHolder();
        m_ctlTask = (CControlTask) inParam;
        if (!m_ctlTask.Encode(ucDataField, 0, iDataFieldLen) || (ucBuffer.length < (21 + iDataFieldLen.value)))
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
        ucBuffer[nIndex++] = (byte) enumControlCode.WriteRequest.getValue();

        // 数据长度
        System.arraycopy(NumberUtil.ShortToByte2(8 + iDataFieldLen.value), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 测量点号
        System.arraycopy(NumberUtil.ShortToByte2(0), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 权限等级
        ucBuffer[nIndex++] = 0x11;

        // 密码
        ucBuffer[nIndex++] = 0x11;
        ucBuffer[nIndex++] = 0x11;
        ucBuffer[nIndex++] = 0x11;

        // 数据项编号
        System.arraycopy(NumberUtil.ShortToByte2(enumDataItem.WriteControlTask.getValue()), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 数据项内容
        System.arraycopy(ucDataField, 0, ucBuffer, nIndex, iDataFieldLen.value);
        nIndex += iDataFieldLen.value;

        // 校验和
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // 帧尾
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // 设置查询命令的长度
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * 解析设置程控任务命令的响应报文
     * 
     * @param ucBuffer 响应报文
     * @param iFrameLen 响应报文长度
     * @param iParsedLen 解析的字节数
     * @param outParam 输出参数
     * @return 成功返回true
     */
    protected boolean parseIoctlResponse_WriteControlTask(byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, CIoctlResult outParam)
    {
        int             nIndex = 0;
        @SuppressWarnings("unused")
        long            iValue = 0;
        

        // 检查输入参数
        if ((null == ucBuffer) || (iFrameLen < 18) || (ucBuffer.length < iFrameLen))
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
        iValue = ucBuffer[nIndex++];

        // 起始字符
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // 控制码
        if ((enumControlCode.WriteRequest.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // 数据长度
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // 测量点号
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // 数据项编号
        if (enumDataItem.WriteControlTask.getValue() != NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex))
        {
            return false;
        }
        nIndex += 2;

        // 设置结果
        enumErrorCode eErrorCode = enumErrorCode.getErrorByInt(ucBuffer[nIndex++]);
        if (null != outParam)
        {
            outParam.setErrorCode(eErrorCode);
        }

        // 校验
        iValue = ucBuffer[nIndex++];
        
        // 帧尾
        iValue = ucBuffer[nIndex++];
        
        // 返回已解析的长度
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * 批量删除程控任务
     * 
     * @param ucBuffer 存放控制命令的缓冲区
     * @param iFrameLen 合成的控制命令长度
     * @param inParam 输入参数（List<int>类型）
     * @return 成功返回true
     * @throws Exception 异常信息
     */
    protected boolean getIoctlCommand_DeleteControlTasks(byte[] ucBuffer, IntHolder iFrameLen, Object inParam) throws Exception
    {
        int             nIndex      = 0;
        

        // 检查输入参数
        if ((null == ucBuffer) || !(inParam instanceof List<?>))
        {
            return false;
        }
        @SuppressWarnings("unchecked")
        List<Integer> lstTaskIds = (List<Integer>) inParam;

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
        ucBuffer[nIndex++] = (byte) enumControlCode.WriteRequest.getValue();

        // 数据长度
        System.arraycopy(NumberUtil.ShortToByte2(8 + 1 + 2 * lstTaskIds.size()), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 测量点号
        System.arraycopy(NumberUtil.ShortToByte2(0), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 权限等级
        ucBuffer[nIndex++] = 0x11;

        // 密码
        ucBuffer[nIndex++] = 0x11;
        ucBuffer[nIndex++] = 0x11;
        ucBuffer[nIndex++] = 0x11;

        // 数据项编号
        System.arraycopy(NumberUtil.ShortToByte2(enumDataItem.DeleteControlTasks.getValue()), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 任务数
        ucBuffer[nIndex++] = (byte) lstTaskIds.size();

        // 任务数据块
        for (int i = 0; i < lstTaskIds.size(); i++)
        {
            ucBuffer[nIndex++] = 0x00;
            ucBuffer[nIndex++] = (byte) lstTaskIds.get(i).intValue();
        }

        // 校验和
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // 帧尾
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // 设置查询命令的长度
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * 解析批量删除程控任务命令的响应报文
     * 
     * @param ucBuffer 响应报文
     * @param iFrameLen 响应报文长度
     * @param iParsedLen 解析的字节数
     * @param outParam 输出参数
     * @return 成功返回true
     */
    protected boolean parseIoctlResponse_DeleteControlTasks(byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, CIoctlResult outParam)
    {
        int             nIndex = 0;
        @SuppressWarnings("unused")
        long            iValue = 0;
        

        // 检查输入参数
        if ((null == ucBuffer) || (iFrameLen < 18) || (ucBuffer.length < iFrameLen))
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
        iValue = ucBuffer[nIndex++];

        // 起始字符
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // 控制码
        if ((enumControlCode.WriteRequest.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // 数据长度
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // 测量点号
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // 数据项编号
        if (enumDataItem.DeleteControlTasks.getValue() != NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex))
        {
            return false;
        }
        nIndex += 2;

        // 设置结果
        enumErrorCode eErrorCode = enumErrorCode.getErrorByInt(ucBuffer[nIndex++]);
        if (null != outParam)
        {
            outParam.setErrorCode(eErrorCode);
        }

        // 校验
        iValue = ucBuffer[nIndex++];
        
        // 帧尾
        iValue = ucBuffer[nIndex++];
        
        // 返回已解析的长度
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * 获得校时命令
     * 
     * @param ucBuffer 存放控制命令的缓冲区
     * @param iFrameLen 合成的控制命令长度
     * @param inParam 输入参数（Date类型）
     * @return 成功返回true
     * @throws Exception 异常信息
     */
    protected boolean getIoctlCommand_Timing(byte[] ucBuffer, IntHolder iFrameLen, Object inParam) throws Exception
    {
        int             nIndex  = 0;
        Date            dt      = null;
        

        // 检查输入参数
        if ((null == ucBuffer) || (ucBuffer.length < 27) || !(inParam instanceof Date))
        {
            return false;
        }
        dt = (Date) inParam;

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
        ucBuffer[nIndex++] = (byte) enumControlCode.WriteRequest.getValue();

        // 数据长度
        System.arraycopy(NumberUtil.ShortToByte2(14), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 测量点号
        System.arraycopy(NumberUtil.ShortToByte2(0), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 权限等级
        ucBuffer[nIndex++] = 0x11;

        // 密码
        ucBuffer[nIndex++] = 0x11;
        ucBuffer[nIndex++] = 0x11;
        ucBuffer[nIndex++] = 0x11;

        // 数据项编号
        System.arraycopy(NumberUtil.ShortToByte2(enumDataItem.Timing.getValue()), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 时间
        SimpleDateFormat sdf = new SimpleDateFormat("ssmmHHddMMyy");
        System.arraycopy(NumberUtil.StringToBcdArray(sdf.format(dt)), 0, ucBuffer, nIndex, 6);
        nIndex += 6;

        // 校验和
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // 帧尾
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // 设置查询命令的长度
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * 解析校时命令的响应报文
     * 
     * @param ucBuffer 响应报文
     * @param iFrameLen 响应报文长度
     * @param iParsedLen 解析的字节数
     * @param outParam 输出参数
     * @return 成功返回true
     */
    protected boolean parseIoctlResponse_Timing(byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, CIoctlResult outParam)
    {
        int             nIndex = 0;
        @SuppressWarnings("unused")
        long            iValue = 0;
        

        // 检查输入参数
        if ((null == ucBuffer) || (iFrameLen < 18) || (ucBuffer.length < iFrameLen))
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
        iValue = ucBuffer[nIndex++];

        // 起始字符
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // 控制码
        if ((enumControlCode.WriteRequest.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // 数据长度
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // 测量点号
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // 数据项编号
        if (enumDataItem.Timing.getValue() != NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex))
        {
            return false;
        }
        nIndex += 2;

        // 设置结果
        enumErrorCode eErrorCode = enumErrorCode.getErrorByInt(ucBuffer[nIndex++]);
        if (null != outParam)
        {
            outParam.setErrorCode(eErrorCode);
        }

        // 校验
        iValue = ucBuffer[nIndex++];
        
        // 帧尾
        iValue = ucBuffer[nIndex++];
        
        // 返回已解析的长度
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * 获得批量添加设备档案的报文
     * 
     * @param ucBuffer 存放控制命令的缓冲区
     * @param iFrameLen 合成的控制命令长度
     * @param inParam 输入参数（List<CModuleBase>类型）
     * @return 成功返回true
     * @throws Exception 异常信息
     */
    protected boolean getIoctlCommand_AddArchives(byte[] ucBuffer, IntHolder iFrameLen, Object inParam) throws Exception
    {
        int             nIndex      = 0;
        

        // 检查输入参数
        if ((null == ucBuffer) || !(inParam instanceof List))
        {
            return false;
        }
        @SuppressWarnings("unchecked")
        List<CModuleBase> lstModules = (List<CModuleBase>) inParam;

        // 编码输入参数
        byte[] ucDataField = new byte[2048];
        int iDataFieldLen = 0;
        for (int i = 0; i < lstModules.size(); i++)
        {
            CModuleBase moduleBase = lstModules.get(i);
            IntHolder iMbEncodeBytes = new IntHolder();

            // 编码设备节点
            if (!moduleBase.Encode(ucDataField, 0, iMbEncodeBytes))
            {
                return false;
            }

            // 累加已编码的字节数
            iDataFieldLen += iMbEncodeBytes.value;
        }

        // 检查数组长度
        if (ucBuffer.length < (22 + iDataFieldLen))
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
        ucBuffer[nIndex++] = (byte) enumControlCode.WriteRequest.getValue();

        // 数据长度
        System.arraycopy(NumberUtil.ShortToByte2(9 + iDataFieldLen), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 测量点号
        System.arraycopy(NumberUtil.ShortToByte2(0), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 权限等级
        ucBuffer[nIndex++] = 0x11;

        // 密码
        ucBuffer[nIndex++] = 0x11;
        ucBuffer[nIndex++] = 0x11;
        ucBuffer[nIndex++] = 0x11;

        // 数据项编号
        System.arraycopy(NumberUtil.ShortToByte2(enumDataItem.AddArchives.getValue()), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 新增设备数
        ucBuffer[nIndex++] = (byte) lstModules.size();

        // 设备档案内容
        System.arraycopy(ucDataField, 0, ucBuffer, nIndex, iDataFieldLen);
        nIndex += iDataFieldLen;

        // 校验和
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // 帧尾
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // 设置查询命令的长度
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * 解析批量添加设备档案的响应报文
     * 
     * @param ucBuffer 响应报文
     * @param iFrameLen 响应报文长度
     * @param iParsedLen 解析的字节数
     * @param outParam 输出参数
     * @return 成功返回true
     */
    protected boolean parseIoctlResponse_AddArchives(byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, CIoctlResult outParam)
    {
        int             nIndex = 0;
        @SuppressWarnings("unused")
        long            iValue = 0;
        

        // 检查输入参数
        if ((null == ucBuffer) || (iFrameLen < 18) || (ucBuffer.length < iFrameLen))
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
        iValue = ucBuffer[nIndex++];

        // 起始字符
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // 控制码
        if ((enumControlCode.WriteRequest.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // 数据长度
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // 测量点号
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // 数据项编号
        if (enumDataItem.AddArchives.getValue() != NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex))
        {
            return false;
        }
        nIndex += 2;

        // 设置结果
        enumErrorCode eErrorCode = enumErrorCode.getErrorByInt(ucBuffer[nIndex++]);
        if (null != outParam)
        {
            outParam.setErrorCode(eErrorCode);
        }

        // 校验
        iValue = ucBuffer[nIndex++];
        
        // 帧尾
        iValue = ucBuffer[nIndex++];
        
        // 返回已解析的长度
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * 获得批量删除设备档案的报文
     * 
     * @param ucBuffer 存放控制命令的缓冲区
     * @param iFrameLen 合成的控制命令长度
     * @param inParam 输入参数（CIoctlParam_DeleteArchives类型）
     * @return 成功返回true
     * @throws Exception 异常信息
     */
    protected boolean getIoctlCommand_DeleteArchives(byte[] ucBuffer, IntHolder iFrameLen, Object inParam) throws Exception
    {
        int             nIndex      = 0;
        

        // 检查输入参数
        if ((null == ucBuffer) || !(inParam instanceof CIoctlParam_DeleteArchives))
        {
            return false;
        }
        CIoctlParam_DeleteArchives lstModules = (CIoctlParam_DeleteArchives) inParam;

        // 编码输入参数
        byte[] ucDataField = new byte[2048];
        int iDataFieldLen = 0;
        if (CIoctlParam_DeleteArchives.DT_DeleteAll == lstModules.getDeleteType())
        {
            // 全部删除
            ucDataField[iDataFieldLen++] = 0x00;
        }
        else
        {
            List<Integer> lstDeviceIds = lstModules.getDeviceIdList();

            // 待删除的档案个数
            ucDataField[iDataFieldLen++] = (byte) lstDeviceIds.size();
            for (int i = 0; i < lstDeviceIds.size(); i++)
            {
                // 设备号
                ucDataField[iDataFieldLen++] = (byte) lstDeviceIds.get(i).intValue();
                ucDataField[iDataFieldLen++] = 0x00;
            }
        }

        // 检查数组长度
        if (ucBuffer.length < (21 + iDataFieldLen))
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
        ucBuffer[nIndex++] = (byte) enumControlCode.WriteRequest.getValue();

        // 数据长度
        System.arraycopy(NumberUtil.ShortToByte2(8 + iDataFieldLen), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 测量点号
        System.arraycopy(NumberUtil.ShortToByte2(0), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 权限等级
        ucBuffer[nIndex++] = 0x11;

        // 密码
        ucBuffer[nIndex++] = 0x11;
        ucBuffer[nIndex++] = 0x11;
        ucBuffer[nIndex++] = 0x11;

        // 数据项编号
        System.arraycopy(NumberUtil.ShortToByte2(enumDataItem.DeleteArchives.getValue()), 0, ucBuffer, nIndex, 2);
        nIndex += 2;

        // 设备档案内容
        System.arraycopy(ucDataField, 0, ucBuffer, nIndex, iDataFieldLen);
        nIndex += iDataFieldLen;

        // 校验和
        ucBuffer[nIndex] = GenCheckSum(ucBuffer, 0, nIndex);
        nIndex += 1;

        // 帧尾
        ucBuffer[nIndex++] = (byte) SYMBOL_END;

        // 设置查询命令的长度
        iFrameLen.value = nIndex;
        return true;
    }

    /**
     * 解析批量删除设备档案的响应报文
     * 
     * @param ucBuffer 响应报文
     * @param iFrameLen 响应报文长度
     * @param iParsedLen 解析的字节数
     * @param outParam 输出参数
     * @return 成功返回true
     */
    protected boolean parseIoctlResponse_DeleteArchives(byte[] ucBuffer, int iFrameLen, IntHolder iParsedLen, CIoctlResult outParam)
    {
        int             nIndex = 0;
        @SuppressWarnings("unused")
        long            iValue = 0;
        

        // 检查输入参数
        if ((null == ucBuffer) || (iFrameLen < 18) || (ucBuffer.length < iFrameLen))
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
        iValue = ucBuffer[nIndex++];

        // 起始字符
        if (SYMBOL_START != ucBuffer[nIndex++])
        {
            return false;
        }

        // 控制码
        if ((enumControlCode.WriteRequest.getValue() + 0x80) != (ucBuffer[nIndex++] & 0xff))
        {
            return false;
        }

        // 数据长度
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // 测量点号
        iValue = NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex);
        nIndex += 2;

        // 数据项编号
        if (enumDataItem.DeleteArchives.getValue() != NumberUtil.byte2ToUnsignedShort(ucBuffer, nIndex))
        {
            return false;
        }
        nIndex += 2;

        // 设置结果
        enumErrorCode eErrorCode = enumErrorCode.getErrorByInt(ucBuffer[nIndex++]);
        if (null != outParam)
        {
            outParam.setErrorCode(eErrorCode);
        }

        // 校验
        iValue = ucBuffer[nIndex++];
        
        // 帧尾
        iValue = ucBuffer[nIndex++];
        
        // 返回已解析的长度
        if (iParsedLen != null)
        {
            iParsedLen.value = nIndex;
        }

        return true;
    }

    /**
     * 处理特殊的请求（登出、心跳帧）
     */
    protected void HandleSpecialRequest()
    {
        byte[]          sentBuffer = new byte[1024];
        IntHolder       iSentFrameLen = new IntHolder(0);


        try
        {
            // 接收客户端的报文
            InputStream inStream = m_sockConn.getInputStream();
            OutputStream outStream = m_sockConn.getOutputStream();
            byte[] recvBuffer = CSocketUtil.readStream(inStream);
            if ((null == recvBuffer) || (recvBuffer.length == 0))
            {
                return;
            }

            // 解析接收的报文
            int         iTotalParsed = 0;
            byte[]      tmpBuffer = new byte[recvBuffer.length];
            IntHolder   iParsedLen = new IntHolder(0);
            while (iTotalParsed < recvBuffer.length)
            {
                // 拷贝尚未解析的报文到缓冲区
                System.arraycopy(recvBuffer, iTotalParsed, tmpBuffer, 0, recvBuffer.length - iTotalParsed);

                // 解析DCU登出请求帧
                if (parseIoctlRequest_logout(tmpBuffer, recvBuffer.length - iTotalParsed, iParsedLen))
                {
                    // 递增已解析的报文长度
                    iTotalParsed += iParsedLen.value;

                    // 修改设备信息
                    SetDeviceState(enumDeviceState.OffLine);

                    // 合成DCU登出请求的响应报文
                    if (composeIoctlResponse_logout(sentBuffer, iSentFrameLen))
                    {
                        // 发送响应报文
                        outStream.write(sentBuffer, 0, iSentFrameLen.value);
                        outStream.flush();
                    }
                }
                // 解析DCU心跳帧
                else if (parseIoctlRequest_heartBeat(tmpBuffer, recvBuffer.length - iTotalParsed, iParsedLen))
                {
                    // 递增已解析的报文长度
                    iTotalParsed += iParsedLen.value;

                    // 设置最近一次收到心跳报文的毫秒数
                    setLastTimeHeartBeat(System.currentTimeMillis());
                    if (GetDeviceState() == enumDeviceState.OffLine)
                    {
                        SetDeviceState(enumDeviceState.Online);
                    }

                    // 合成心跳帧响应报文
                    if (composeIoctlResponse_heartBeat(sentBuffer, iSentFrameLen))
                    {
                        // 发送响应报文
                        outStream.write(sentBuffer, 0, iSentFrameLen.value);
                        outStream.flush();
                    }
                }
                // 出现了其他不能识别的命令
                else 
                {
                    StringBuffer strBuffer = new StringBuffer();
                    
                    strBuffer.append("没有发送请求，却收到了报文：");
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
     * 获得指定数据项的响应报文
     * @param iDataItem 数据项编号
     * @return  响应报文
     */
    public byte[] getQueryResponseByDataItem(int iDataItem)
    {
        return m_mapQueryCmd2RespFrame.get(iDataItem);
    }
}
