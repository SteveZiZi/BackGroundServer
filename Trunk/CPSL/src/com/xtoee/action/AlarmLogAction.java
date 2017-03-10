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
 * �澯��־������
 * @author zgm
 *
 */
public class AlarmLogAction extends ActionSupport
{
    private static final long serialVersionUID = 1L;
    private DeviceManageService     deviceManageService;        // �豸����ӿ�
    private LogManageService        logManageService;           // ��־�������
    private InputStream             responseJson;               // ��Ӧ�����Json��ʽ��
    
    private Integer                 page;                       // ҳ��
    private Integer                 rows;                       // �к�
    
    private String                  logicAddress;               // �豸�߼���ַ
    
    
    /**
     * �����豸����ӿ�
     * @param deviceManageService   �豸�������
     */
    public void setDeviceManageService(DeviceManageService deviceManageService)
    {
        this.deviceManageService = deviceManageService;
    }
    
    /**
     * ������־�������
     * @param logManageService  ��־�������
     */
    public void setLogManageService(LogManageService logManageService)
    {
        this.logManageService = logManageService;
    }

    /**
     * ����ҳ��
     * @param page  ҳ��
     */
    public void setPage(Integer page)
    {
        this.page = page;
    }

    /**
     * �����к�
     * @param rows �к�
     */
    public void setRows(Integer rows)
    {
        this.rows = rows;
    }
    
    /**
     * �����豸�߼���ַ
     * @param logicAddress  �豸�߼���ַ
     */
    public void setLogicAddress(String logicAddress)
    {
        this.logicAddress = logicAddress;
    }

    /**
     * �����Ӧ�����Json��ʽ��
     * @return  ��Ӧ�����Json��ʽ��
     */
    public InputStream getResponseJson()
    {
        return responseJson;
    }

    /**
     * ������Ӧ�����Json��ʽ��
     * @param responseJson  ��Ӧ�����Json��ʽ��
     */
    public void setResponseJson(InputStream responseJson)
    {
        this.responseJson = responseJson;
    }
    
    /**
     * ��ø澯��־�б�
     * @return  �ɹ�����success
     * @throws UnsupportedEncodingException
     */
    public String getAlarmLogList() throws UnsupportedEncodingException
    {
        // ����������
        if ((page <= 0) 
                || (rows <= 0)
                || StringUtil.IsNullOrEmpty(logicAddress))
        {
            return ERROR;
        }
        
        // �����豸�߼���ַ������豸����
        IDevice device = deviceManageService.GetDeviceByLogicAddress(logicAddress);
        if ((null == device) || !(device instanceof CDcuDevice))
        {
            return ERROR;
        }
        
        // ��ø澯��־�б�
        List<CAlarmLogBase> lstAlarmLogs = logManageService.getAlarmLogList(logicAddress);
        if (null == lstAlarmLogs)
        {
            return ERROR;
        }

        // ѭ�������澯��־�б�
        int iCount = 0;
        JSONArray logs = new JSONArray();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (CAlarmLogBase alarmLog : lstAlarmLogs)
        {
            // ��������
            iCount++;
            
            // ����к��Ƿ���ָ����ҳ�뷶Χ
            if ((iCount > (page - 1) * rows) && (iCount <= page * rows))
            {
                // ��ø澯��־��Ϣ
                JSONObject row = new JSONObject();
                row.put("logDate", sdf.format(alarmLog.getAlarmDate()));
                row.put("logType", alarmLog.getLogType());
                row.put("logContent", alarmLog.getLogContent(device));

                // ��Ӹ澯��־��Ϣ���м�����
                logs.add(row);
            }
        }

        // �ϳɷ��ؽ��
        JSONObject result = new JSONObject();
        result.put("total", lstAlarmLogs.size());
        result.put("rows", logs);
        
        // ������Ӧ�����Json��ʽ��
        setResponseJson(new ByteArrayInputStream(result.toString().getBytes("UTF-8")));
        return SUCCESS;
    }
}
