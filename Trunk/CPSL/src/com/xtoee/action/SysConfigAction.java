package com.xtoee.action;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionSupport;
import com.xtoee.devices.IDevice;
import com.xtoee.devices.dcu.CClosedCircuit;
import com.xtoee.devices.dcu.CDcuDevice;
import com.xtoee.service.ConfigManageService;
import com.xtoee.service.DeviceManageService;
import com.xtoee.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * ϵͳ���ÿ�����
 * @author zgm
 *
 */
public class SysConfigAction extends ActionSupport
{
    private static final long       serialVersionUID = 1L;
    private DeviceManageService     deviceManageService;        // �豸����ӿ�
    private ConfigManageService     configManageService;        // ���ù������
    private InputStream             responseJson;               // ��Ӧ�����Json��ʽ��
    
    private Integer                 page;                       // ҳ��
    private Integer                 rows;                       // �к�
    
    private String                  logicAddress;               // �豸�߼���ַ
    private Date                    newDatetime;                // ϵͳʱ��
    private String                  adminPhone;                 // ����Ա�ֻ���
    
    private String                  closedCircuitName;          // ��·����(L#0102����ʾ1���豸��2�Ż�·)
    private Integer                 percent0;                   // ����0%��Ӧ�ĵ�ѹֵ
    private Integer                 percent10;                  // ����10%��Ӧ�ĵ�ѹֵ
    private Integer                 percent20;                  // ����20%��Ӧ�ĵ�ѹֵ
    private Integer                 percent30;                  // ����30%��Ӧ�ĵ�ѹֵ
    private Integer                 percent40;                  // ����40%��Ӧ�ĵ�ѹֵ
    private Integer                 percent50;                  // ����50%��Ӧ�ĵ�ѹֵ
    private Integer                 percent60;                  // ����60%��Ӧ�ĵ�ѹֵ
    private Integer                 percent70;                  // ����70%��Ӧ�ĵ�ѹֵ
    private Integer                 percent80;                  // ����80%��Ӧ�ĵ�ѹֵ
    private Integer                 percent90;                  // ����90%��Ӧ�ĵ�ѹֵ
    private Integer                 percent100;                 // ����100%��Ӧ�ĵ�ѹֵ
    

    /**
     * �����豸����ӿ�
     * @param deviceManageService   �豸�������
     */
    public void setDeviceManageService(DeviceManageService deviceManageService)
    {
        this.deviceManageService = deviceManageService;
    }
    
    /**
     * �������ù������
     * @param configManageService   ���ù������
     */
    public void setConfigManageService(ConfigManageService configManageService)
    {
        this.configManageService = configManageService;
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
     * ����ϵͳʱ��
     * @param newDatetime   ϵͳʱ��
     */
    public void setNewDatetime(Date newDatetime)
    {
        this.newDatetime = newDatetime;
    }
    
    /**
     * ���ù���Ա�ֻ���
     * @param adminPhone    ����Ա�ֻ���
     */
    public void setAdminPhone(String adminPhone)
    {
        this.adminPhone = adminPhone;
    }

    /**
     * ���û�·����(L#0102����ʾ1���豸��2�Ż�·)
     * @param closedCircuitName ��·����
     */
    public void setClosedCircuitName(String closedCircuitName)
    {
        this.closedCircuitName = closedCircuitName;
    }

    /**
     * ��������0%��Ӧ�ĵ�ѹֵ
     * @param percent0  ��ѹֵ
     */
    public void setPercent0(Integer percent0)
    {
        this.percent0 = percent0;
    }

    /**
     * ��������10%��Ӧ�ĵ�ѹֵ
     * @param percent10  ��ѹֵ
     */
    public void setPercent10(Integer percent10)
    {
        this.percent10 = percent10;
    }

    /**
     * ��������20%��Ӧ�ĵ�ѹֵ
     * @param percent20  ��ѹֵ
     */
    public void setPercent20(Integer percent20)
    {
        this.percent20 = percent20;
    }

    /**
     * ��������30%��Ӧ�ĵ�ѹֵ
     * @param percent30  ��ѹֵ
     */
    public void setPercent30(Integer percent30)
    {
        this.percent30 = percent30;
    }

    /**
     * ��������40%��Ӧ�ĵ�ѹֵ
     * @param percent40  ��ѹֵ
     */
    public void setPercent40(Integer percent40)
    {
        this.percent40 = percent40;
    }

    /**
     * ��������50%��Ӧ�ĵ�ѹֵ
     * @param percent50  ��ѹֵ
     */
    public void setPercent50(Integer percent50)
    {
        this.percent50 = percent50;
    }

    /**
     * ��������60%��Ӧ�ĵ�ѹֵ
     * @param percent60  ��ѹֵ
     */
    public void setPercent60(Integer percent60)
    {
        this.percent60 = percent60;
    }

    /**
     * ��������70%��Ӧ�ĵ�ѹֵ
     * @param percent70  ��ѹֵ
     */
    public void setPercent70(Integer percent70)
    {
        this.percent70 = percent70;
    }

    /**
     * ��������80%��Ӧ�ĵ�ѹֵ
     * @param percent80  ��ѹֵ
     */
    public void setPercent80(Integer percent80)
    {
        this.percent80 = percent80;
    }

    /**
     * ��������90%��Ӧ�ĵ�ѹֵ
     * @param percent90  ��ѹֵ
     */
    public void setPercent90(Integer percent90)
    {
        this.percent90 = percent90;
    }

    /**
     * ��������100%��Ӧ�ĵ�ѹֵ
     * @param percent100  ��ѹֵ
     */
    public void setPercent100(Integer percent100)
    {
        this.percent100 = percent100;
    }
    
    /**
     * ��ù���Ա�ֻ���
     * @return �ɹ�����success
     * @throws UnsupportedEncodingException 
     */
    public String getAdminPhone() throws UnsupportedEncodingException
    {
        // ����������
        if (StringUtil.IsNullOrEmpty(logicAddress))
        {
            return ERROR;
        }
        
        // �ϳɷ��ؽ��
        JSONObject result = new JSONObject();
        result.put("adminPhone", configManageService.getAdminPhone(logicAddress));
        
        // ������Ӧ�����Json��ʽ��
        setResponseJson(new ByteArrayInputStream(result.toString().getBytes("UTF-8")));
        return SUCCESS;
    }

    /**
     * ���ù���Ա�ֻ���
     * @return  �ɹ�����success
     * @throws UnsupportedEncodingException
     */
    public String modifyAdminPhone() throws UnsupportedEncodingException
    {
        // ����������
        if (StringUtil.IsNullOrEmpty(logicAddress)
                || StringUtil.IsNullOrEmpty(adminPhone))
        {
            return ERROR;
        }
        
        // ���ù���Ա�绰
        if (configManageService.setAdminPhone(logicAddress, adminPhone))
        {
            // ������Ӧ�����Json��ʽ��
            setResponseJson(new ByteArrayInputStream("success".getBytes("UTF-8")));
        }
        else 
        {
            // ������Ӧ�����Json��ʽ��
            setResponseJson(new ByteArrayInputStream("error".getBytes("UTF-8")));
        }
        
        return SUCCESS;
    }
    
    /**
     * �����豸ʱ��
     * @return  �ɹ�����success
     * @throws UnsupportedEncodingException
     */
    public String setDeviceTime() throws UnsupportedEncodingException
    {
        // ����������
        if (StringUtil.IsNullOrEmpty(logicAddress)
                || (null == newDatetime))
        {
            return ERROR;
        }
        
        // �����豸ʱ��
        if (configManageService.setDeviceTime(logicAddress, newDatetime))
        {
            // ������Ӧ�����Json��ʽ��
            setResponseJson(new ByteArrayInputStream("success".getBytes("UTF-8")));
        }
        else 
        {
            // ������Ӧ�����Json��ʽ��
            setResponseJson(new ByteArrayInputStream("error".getBytes("UTF-8")));
        }
        
        return SUCCESS;
    }
    
    /**
     * ���Dim-Vdb�б�
     * @return  �ɹ�����success
     * @throws UnsupportedEncodingException
     */
    public String getDimVdbList() throws UnsupportedEncodingException
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
                // ��û�·��Dim-Vdb��
                int iClosedCircuitId = closedCircuit.getID();
                Map<String, Integer> mapDimVdb = dcuDevice.getDimVdbMapByCCId(iClosedCircuitId);
                
                JSONObject closedCircuitInfo = new JSONObject();
                closedCircuitInfo.put("closedCircuitName", String.format("L#%02d%02d", dcuDevice.getID(), iClosedCircuitId));
                closedCircuitInfo.put("percent100", mapDimVdb.get("100"));
                closedCircuitInfo.put("percent90", mapDimVdb.get("90"));
                closedCircuitInfo.put("percent80", mapDimVdb.get("80"));
                closedCircuitInfo.put("percent70", mapDimVdb.get("70"));
                closedCircuitInfo.put("percent60", mapDimVdb.get("60"));
                closedCircuitInfo.put("percent50", mapDimVdb.get("50"));
                closedCircuitInfo.put("percent40", mapDimVdb.get("40"));
                closedCircuitInfo.put("percent30", mapDimVdb.get("30"));
                closedCircuitInfo.put("percent20", mapDimVdb.get("20"));
                closedCircuitInfo.put("percent10", mapDimVdb.get("10"));
                closedCircuitInfo.put("percent0", mapDimVdb.get("0"));
              
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
     * ���û�·Dim-Vdbֵ
     * @return  �ɹ�����success
     * @throws UnsupportedEncodingException 
     */
    public String setClosedCircuitDimVdb() throws UnsupportedEncodingException
    {
        // ����������
        if (StringUtil.IsNullOrEmpty(logicAddress)
                || StringUtil.IsNullOrEmpty(closedCircuitName))
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
        
        // ���ָ����·��Dim-Vdb��
        int iClosedCircuitId = Integer.parseInt(closedCircuitName.substring(4, 6));
        Map<String, Integer> mapDimVdb = dcuDevice.getDimVdbMapByCCId(iClosedCircuitId);
        if (null == mapDimVdb)
        {
            return ERROR;
        }
        
        // �޸Ļ�·��Dim-Vdbֵ
        mapDimVdb.put("100", percent100);
        mapDimVdb.put("90", percent90);
        mapDimVdb.put("80", percent80);
        mapDimVdb.put("70", percent70);
        mapDimVdb.put("60", percent60);
        mapDimVdb.put("50", percent50);
        mapDimVdb.put("40", percent40);
        mapDimVdb.put("30", percent30);
        mapDimVdb.put("20", percent20);
        mapDimVdb.put("10", percent10);
        mapDimVdb.put("0", percent0);
        
        // ������Ӧ�����Json��ʽ��
        setResponseJson(new ByteArrayInputStream("success".getBytes("UTF-8")));
        return SUCCESS;
    }

}
