package com.xtoee.action;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.opensymphony.xwork2.ActionSupport;
import com.xtoee.devices.IDevice;
import com.xtoee.devices.dcu.CDcuDevice;
import com.xtoee.devices.dcu.alarmLog.CAlarmLogBase;
import com.xtoee.service.DeviceManageService;
import com.xtoee.service.LogManageService;
import com.xtoee.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 告警日志控制器
 * @author zgm
 *
 */
public class AlarmLogAction extends ActionSupport
{
    private static final long serialVersionUID = 1L;
    private DeviceManageService     deviceManageService;        // 设备管理接口
    private LogManageService        logManageService;           // 日志管理对象
    private InputStream             responseJson;               // 响应结果（Json格式）
    
    private Integer                 page;                       // 页号
    private Integer                 rows;                       // 行号
    
    private String                  logicAddress;               // 设备逻辑地址
    
    
    /**
     * 设置设备管理接口
     * @param deviceManageService   设备管理对象
     */
    public void setDeviceManageService(DeviceManageService deviceManageService)
    {
        this.deviceManageService = deviceManageService;
    }
    
    /**
     * 设置日志管理对象
     * @param logManageService  日志管理对象
     */
    public void setLogManageService(LogManageService logManageService)
    {
        this.logManageService = logManageService;
    }

    /**
     * 设置页号
     * @param page  页号
     */
    public void setPage(Integer page)
    {
        this.page = page;
    }

    /**
     * 设置行号
     * @param rows 行号
     */
    public void setRows(Integer rows)
    {
        this.rows = rows;
    }
    
    /**
     * 设置设备逻辑地址
     * @param logicAddress  设备逻辑地址
     */
    public void setLogicAddress(String logicAddress)
    {
        this.logicAddress = logicAddress;
    }

    /**
     * 获得响应结果（Json格式）
     * @return  响应结果（Json格式）
     */
    public InputStream getResponseJson()
    {
        return responseJson;
    }

    /**
     * 设置响应结果（Json格式）
     * @param responseJson  响应结果（Json格式）
     */
    public void setResponseJson(InputStream responseJson)
    {
        this.responseJson = responseJson;
    }
    
    /**
     * 获得告警日志列表
     * @return  成功返回success
     * @throws UnsupportedEncodingException
     */
    public String getAlarmLogList() throws UnsupportedEncodingException
    {
        // 检查输入参数
        if ((page <= 0) 
                || (rows <= 0)
                || StringUtil.IsNullOrEmpty(logicAddress))
        {
            return ERROR;
        }
        
        // 根据设备逻辑地址，获得设备对象
        IDevice device = deviceManageService.GetDeviceByLogicAddress(logicAddress);
        if ((null == device) || !(device instanceof CDcuDevice))
        {
            return ERROR;
        }
        
        // 获得告警日志列表
        List<CAlarmLogBase> lstAlarmLogs = logManageService.getAlarmLogList(logicAddress);
        if (null == lstAlarmLogs)
        {
            return ERROR;
        }

        // 循环遍历告警日志列表
        int iCount = 0;
        JSONArray logs = new JSONArray();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (CAlarmLogBase alarmLog : lstAlarmLogs)
        {
            // 递增行数
            iCount++;
            
            // 检查行号是否在指定的页码范围
            if ((iCount > (page - 1) * rows) && (iCount <= page * rows))
            {
                // 获得告警日志信息
                JSONObject row = new JSONObject();
                row.put("logDate", sdf.format(alarmLog.getAlarmDate()));
                row.put("logType", alarmLog.getLogType());
                row.put("logContent", alarmLog.getLogContent(device));

                // 添加告警日志信息到行集合中
                logs.add(row);
            }
        }

        // 合成返回结果
        JSONObject result = new JSONObject();
        result.put("total", lstAlarmLogs.size());
        result.put("rows", logs);
        
        // 设置响应结果（Json格式）
        setResponseJson(new ByteArrayInputStream(result.toString().getBytes("UTF-8")));
        return SUCCESS;
    }
}
