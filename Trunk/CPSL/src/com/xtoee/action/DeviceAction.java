package com.xtoee.action;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.opensymphony.xwork2.ActionSupport;
import com.xtoee.devices.IDevice;
import com.xtoee.devices.enumDeviceState;
import com.xtoee.devices.dcu.CClosedCircuit;
import com.xtoee.devices.dcu.CDcuDevice;
import com.xtoee.devices.dcu.CRectifier;
import com.xtoee.service.DeviceManageService;
import com.xtoee.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * �豸������
 * @author zgm
 *
 */
public class DeviceAction extends ActionSupport
{
    private static final long       serialVersionUID = 1L;
    private DeviceManageService     deviceManageService;        // �豸����ӿ�
    private InputStream             responseJson;               // ��Ӧ�����Json��ʽ��
    
    private Integer                 page;                       // ҳ��
    private Integer                 rows;                       // �к�
    
    private String                  logicAddress;               // �豸�߼���ַ
    
    private String                  closedCircuitName;          // ��·���ƣ�����L#0102����ʾ1���豸��2�Ż�·��
    private String                  closedCircuitLight;         // ��·����
    private String                  closedCircuitSwitch;        // ��·����

    
    /**
     * �����豸����ӿ�
     * @param deviceManageService   �豸�������
     */
    public void setDeviceManageService(DeviceManageService deviceManageService)
    {
        this.deviceManageService = deviceManageService;
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
     * ���û�·����
     * @param closedCircuitName     ��·����
     */
    public void setClosedCircuitName(String closedCircuitName)
    {
        this.closedCircuitName = closedCircuitName;
    }
    
    /**
     * ���û�·����
     * @param closedCircuitLight    ��·����
     */
    public void setClosedCircuitLight(String closedCircuitLight)
    {
        this.closedCircuitLight = closedCircuitLight;
    }
    
    /**
     * ���û�·����
     * @param closedCircuitSwitch   ��·����
     */
    public void setClosedCircuitSwitch(String closedCircuitSwitch)
    {
        this.closedCircuitSwitch = closedCircuitSwitch;
    }
    
    /**
     * ����豸�б�
     * @return  �ɹ�����success
     * @throws Exception
     */
    public String getDeviceList() throws Exception
    {
        // ��ù���ϵͳ������豸�б�
        List<IDevice> lstDevices = deviceManageService.getDeviceList();
        if (null == lstDevices)
        {
            return ERROR;
        }

        // �����豸�ڵ�
        JSONArray childs = new JSONArray();
        for (IDevice device : lstDevices)
        {
            // ����豸����
            if (!(device instanceof CDcuDevice))
            {
                continue;
            }
            
            // ���Բ����ߵ��豸
            CDcuDevice dcuDevice = (CDcuDevice) device;
            if (dcuDevice.GetDeviceState() == enumDeviceState.OffLine)
            {
                continue;
            }
           
            // ����Json����
            JSONObject node = new JSONObject();
            node.put("id", dcuDevice.getID());
            node.put("text", dcuDevice.getDeviceName());
            node.put("iconCls", "icon-tree-device");
            node.put("url", "getPage_runPage?logicAddress=" + dcuDevice.getLogicAddress() + "&deviceID=" + dcuDevice.getID());

            // ���Json����
            childs.add(node);
        }

        // �������ڵ�
        JSONObject root = new JSONObject();
        root.put("text", "Project1");
        root.put("iconCls", "icon-tree-project");
        root.put("children", childs);

        // �������ڵ�
        JSONArray tree = new JSONArray();
        tree.add(root);
        
        // ������Ӧ�����Json��ʽ��
        setResponseJson(new ByteArrayInputStream(tree.toString().getBytes("UTF-8")));
        return SUCCESS;
    }
    
    /**
     * ����豸״̬��Ϣ
     * @return  �ɹ�����success
     * @throws UnsupportedEncodingException 
     */
    public String getDeviceStatusInfo() throws UnsupportedEncodingException
    {
        // ����������
        if (StringUtil.IsNullOrEmpty(logicAddress))
        {
            return ERROR;
        }
        
        // �����豸�߼���ַ������豸����
        IDevice device = deviceManageService.GetDeviceByLogicAddress(logicAddress);
        if ((null == device) || !(device instanceof CDcuDevice))
        {
            return ERROR;
        }
        CDcuDevice dcuDevice = (CDcuDevice)device;

        // ����豸״̬��Ϣ
        JSONObject statusInfo = new JSONObject();
        statusInfo.put("deviceName", dcuDevice.getDeviceName());
        statusInfo.put("deviceVoltage", String.format("%.1f", dcuDevice.getVoltage()));
        statusInfo.put("deviceCurrent", String.format("%.1f", dcuDevice.getCurrent()));
        statusInfo.put("devicePower", String.format("%.2f", dcuDevice.getPower()));
        statusInfo.put("deviceTemperature", String.format("%.1f", dcuDevice.getTemperature()));
        statusInfo.put("deviceStatus", dcuDevice.getStatus());

        // ����豸��Ϣ���м�����
        JSONArray rows = new JSONArray();
        rows.add(statusInfo);
        
        // �ϳɷ��ؽ��
        JSONObject result = new JSONObject();
        result.put("total", 1);
        result.put("rows", rows);
        
        // ������Ӧ�����Json��ʽ��
        setResponseJson(new ByteArrayInputStream(result.toString().getBytes("UTF-8")));
        return SUCCESS;
    }
    
    /**
     * ����豸��·����Ϣ
     * @return  �ɹ�����success
     * @throws UnsupportedEncodingException 
     */
    public String getClosedCircuitInfo() throws UnsupportedEncodingException
    {
        // ����������
        if (StringUtil.IsNullOrEmpty(logicAddress)
                || (page <= 0) 
                || (rows <= 0))
        {
            return ERROR;
        }
        
        // �����豸�߼���ַ������豸����
        IDevice device = deviceManageService.GetDeviceByLogicAddress(logicAddress);
        if ((null == device) || !(device instanceof CDcuDevice))
        {
            return ERROR;
        }
        CDcuDevice dcuDevice = (CDcuDevice)device;

        // ѭ���������л�·
        int iCount = 0;
        JSONArray jsonArray = new JSONArray();
        List<CClosedCircuit> lstClosedCircuit = dcuDevice.getClosedCircuitList();
        for (CClosedCircuit closedCircuit : lstClosedCircuit)
        {
            // ��������
            iCount++;
            
            // ����к��Ƿ���ָ����ҳ�뷶Χ
            if ((iCount > (page - 1) * rows) && (iCount <= page * rows))
            {
                // ��û�·״̬��Ϣ
                JSONObject closedCircuitInfo = new JSONObject();
                closedCircuitInfo.put("closedCircuitName", String.format("L#%02d%02d", dcuDevice.getID(), closedCircuit.getID()));
                closedCircuitInfo.put("closedCircuitVoltage", String.format("%.1f", closedCircuit.getVoltage()));
                closedCircuitInfo.put("closedCircuitCurrent", String.format("%.1f", closedCircuit.getCurrent()));
                closedCircuitInfo.put("closedCircuitPower", String.format("%.2f", closedCircuit.getPower()));
                closedCircuitInfo.put("closedCircuitTemperature", String.format("%.1f", closedCircuit.getTemperature()));
                closedCircuitInfo.put("closedCircuitStatus", closedCircuit.getStatus());
              
                // ��ӻ�·״̬��Ϣ���м�����
                jsonArray.add(closedCircuitInfo);
            }
        }

        // �ϳɷ��ؽ��
        JSONObject result = new JSONObject();
        result.put("total", lstClosedCircuit.size());
        result.put("rows", jsonArray);
        
        // ������Ӧ�����Json��ʽ��
        setResponseJson(new ByteArrayInputStream(result.toString().getBytes("UTF-8")));
        return SUCCESS;
    }

    /**
     * ����豸��������Ϣ
     * @return �ɹ�����success
     * @throws UnsupportedEncodingException 
     */
    public String getRectifierInfo() throws UnsupportedEncodingException
    {
        // ����������
        if (StringUtil.IsNullOrEmpty(logicAddress)
                || (page <= 0) 
                || (rows <= 0))
        {
            return ERROR;
        }
        
        // �����豸�߼���ַ������豸����
        IDevice device = deviceManageService.GetDeviceByLogicAddress(logicAddress);
        if ((null == device) || !(device instanceof CDcuDevice))
        {
            return ERROR;
        }
        CDcuDevice dcuDevice = (CDcuDevice) device;

        // ѭ���������л�·
        int iCount = 0;
        JSONArray jsonArray = new JSONArray();
        List<CClosedCircuit> lstClosedCircuit = dcuDevice.getClosedCircuitList();
        for (CClosedCircuit closedCircuit : lstClosedCircuit)
        {
            List<CRectifier> lstRectifier = closedCircuit.getRectifierList();
            for (CRectifier rectifier : lstRectifier)
            {
                // ��������
                iCount++;
                
                // ����к��Ƿ���ָ����ҳ�뷶Χ
                if ((iCount > (page - 1) * rows) && (iCount <= page * rows))
                {
                    // ���������״̬��Ϣ
                    JSONObject rectifierInfo = new JSONObject();
                    rectifierInfo.put("recifierName", String.format("R#%02d%02d%02d"
                            , dcuDevice.getID()
                            , closedCircuit.getID()
                            , rectifier.getID()));
                    rectifierInfo.put("recifierVoltage", String.format("%.1f", rectifier.getVoltage()));
                    rectifierInfo.put("recifierCurrent", String.format("%.1f", rectifier.getCurrent()));
                    rectifierInfo.put("recifierPower", String.format("%.2f", rectifier.getPower()));
                    rectifierInfo.put("recifierTemperature", String.format("%.1f", rectifier.getTemperature()));
                    rectifierInfo.put("recifierStatus", rectifier.getStatus());

                    // ���������״̬��Ϣ���м�����
                    jsonArray.add(rectifierInfo);
                }
            }
        }

        // �ϳɷ��ؽ��
        JSONObject result = new JSONObject();
        result.put("total", iCount);
        result.put("rows", jsonArray);
        
        // ������Ӧ�����Json��ʽ��
        setResponseJson(new ByteArrayInputStream(result.toString().getBytes("UTF-8")));
        return SUCCESS;
    }
    
    /**
     * ���û�·����
     * @return  �ɹ�����success
     * @throws UnsupportedEncodingException
     */
    public String changeClosedCircuitLight() throws UnsupportedEncodingException
    {
        // ����������
        if (StringUtil.IsNullOrEmpty(logicAddress)
                || StringUtil.IsNullOrEmpty(closedCircuitName)
                || StringUtil.IsNullOrEmpty(closedCircuitLight))
        {
            return ERROR;
        }
        
        // �޸��豸ָ����·������
        JSONObject result = new JSONObject();
        if (deviceManageService.changeClosedCircuitLight(logicAddress, closedCircuitName, closedCircuitLight))
        {
            result.put("success", "true");
        }
        else
        {
            result.put("success", "false");
            result.put("errorMsg", "����ʧ�ܣ�");
        }

        // ������Ӧ�����Json��ʽ��
        setResponseJson(new ByteArrayInputStream(result.toString().getBytes("UTF-8")));
        return SUCCESS;
    }
    
    /**
     * ��·����
     * @return  �ɹ�����success
     * @throws UnsupportedEncodingException
     */
    public String closedCircuitSwitch() throws UnsupportedEncodingException
    {
        // ����������
        if (StringUtil.IsNullOrEmpty(logicAddress)
                || StringUtil.IsNullOrEmpty(closedCircuitName)
                || StringUtil.IsNullOrEmpty(closedCircuitSwitch))
        {
            return ERROR;
        }
        
        // ��·����
        JSONObject result = new JSONObject();
        if (deviceManageService.closedCircuitSwitch(logicAddress, closedCircuitName, closedCircuitSwitch))
        {
            result.put("success", "true");
        }
        else
        {
            result.put("success", "false");
            result.put("errorMsg", "����ʧ�ܣ�");
        }

        // ������Ӧ�����Json��ʽ��
        setResponseJson(new ByteArrayInputStream(result.toString().getBytes("UTF-8")));
        return SUCCESS;
    }
}
