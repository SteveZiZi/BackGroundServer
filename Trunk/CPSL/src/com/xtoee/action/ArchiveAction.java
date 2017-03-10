package com.xtoee.action;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork2.ActionSupport;
import com.xtoee.devices.IDevice;
import com.xtoee.devices.dcu.CDcuDevice;
import com.xtoee.devices.dcu.deviceNode.CModuleBase;
import com.xtoee.service.ArchiveManageService;
import com.xtoee.service.DeviceManageService;
import com.xtoee.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * �������������
 * @author zgm
 *
 */
public class ArchiveAction extends ActionSupport
{
    private static final long       serialVersionUID = 1L;
    private DeviceManageService     deviceManageService;        // �豸����ӿ�
    private ArchiveManageService    archiveManageService;       // �����������
    private InputStream             responseJson;               // ��Ӧ�����Json��ʽ��
    
    private Integer                 page;                       // ҳ��
    private Integer                 rows;                       // �к�
    
    private String                  logicAddress;               // �豸�߼���ַ
    
    private Integer                 moduleId;                   // ģ��ID
    private Integer                 closedCircuit;              // ��·��
    private Integer                 functionType;               // ��������
    private Integer                 moduleType;                 // �豸����
    private Integer                 commParam;                  // ͨѶ����
    private String                  moduleAddress;              // �豸��ַ
    
    private String                  archiveIds;                 // ��ɾ���ĵ���ID

    
    /**
     * �����豸����ӿ�
     * @param deviceManageService   �豸�������
     */
    public void setDeviceManageService(DeviceManageService deviceManageService)
    {
        this.deviceManageService = deviceManageService;
    }
    
    /**
     * ���õ����������
     * @param archiveManageService  �����������
     */
    public void setArchiveManageService(ArchiveManageService archiveManageService)
    {
        this.archiveManageService = archiveManageService;
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
     * ����ģ��ID
     * @param moduleId  ģ��ID
     */
    public void setModuleId(Integer moduleId)
    {
        this.moduleId = moduleId;
    }

    /**
     * ���û�·��
     * @param closedCircuit ��·��
     */
    public void setClosedCircuit(Integer closedCircuit)
    {
        this.closedCircuit = closedCircuit;
    }

    /**
     * ���ù�������
     * @param functionType  ��������
     */
    public void setFunctionType(Integer functionType)
    {
        this.functionType = functionType;
    }

    /**
     * �����豸����
     * @param moduleType    �豸����
     */
    public void setModuleType(Integer moduleType)
    {
        this.moduleType = moduleType;
    }

    /**
     * �����豸��ַ
     * @param moduleAddress �豸��ַ
     */
    public void setModuleAddress(String moduleAddress)
    {
        this.moduleAddress = moduleAddress;
    }

    /**
     * ����ͨѶ����
     * @param commParam ͨѶ����
     */
    public void setCommParam(Integer commParam)
    {
        this.commParam = commParam;
    }
    
    /**
     * ���ô�ɾ���ĵ���Id
     * @param archiveIds    ��ɾ���ĵ���Id
     */
    public void setArchiveIds(String archiveIds)
    {
        this.archiveIds = archiveIds;
    }

    /**
     * ��õ����б�
     * @return  �ɹ�����success
     * @throws UnsupportedEncodingException
     */
    public String getArchiveList() throws UnsupportedEncodingException
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

        // ����豸�����б�
        List<CModuleBase> lstArchives = archiveManageService.getArchiveList(logicAddress);
        if (null == lstArchives)
        {
            return ERROR;
        }

        // ѭ�������豸�����б�
        int iCount = 0;
        JSONArray jsonArray = new JSONArray();
        for (CModuleBase archive : lstArchives)
        {
            // ��������
            iCount++;
            
            // ����к��Ƿ���ָ����ҳ�뷶Χ
            if ((iCount > (page - 1) * rows) && (iCount <= page * rows))
            {
                // ����豸������Ϣ
                JSONObject row = new JSONObject();
                row.put("moduleId", archive.getModuleId());
                row.put("moduleAddress", archive.getModuleAddress());
                
                StringBuffer sb = new StringBuffer();
                sb.append("L#");
                int iDeviceId = device.getID();
                sb.append(String.format("%02d", iDeviceId));
                sb.append(String.format("%02d", archive.getClosedCircuitId()));
                row.put("closedCircuit", sb.toString());
                
                row.put("functionType", CModuleBase.getFunctionTypeDesc(archive.getFunctionType()));
                row.put("moduleType", CModuleBase.getModuleTypeDesc(archive.getModuleType()));
                row.put("commParam", CModuleBase.getCommParamDesc(archive.getCommParam()));

                // ����豸������Ϣ���м�����
                jsonArray.add(row);
            }
        }

        // �ϳɷ��ؽ��
        JSONObject result = new JSONObject();
        result.put("total", lstArchives.size());
        result.put("rows", jsonArray);
        
        // ������Ӧ�����Json��ʽ��
        setResponseJson(new ByteArrayInputStream(result.toString().getBytes("UTF-8")));
        return SUCCESS;
    }
    
    /**
     * ��ӵ���
     * @return  �ɹ�����success
     * @throws UnsupportedEncodingException
     */
    public String addArchives() throws UnsupportedEncodingException
    {
        // ����������
        if (StringUtil.IsNullOrEmpty(logicAddress)
                || StringUtil.IsNullOrEmpty(moduleAddress)
                || !moduleAddress.matches("\\d{12}"))
        {
            return ERROR;
        }
        
        // �����豸�ڵ����
        CModuleBase moduleBase = new CModuleBase(moduleId, moduleAddress, closedCircuit, functionType, moduleType, commParam);
        
        // ����豸�ڵ����������
        List<CModuleBase> lstModules = new ArrayList<CModuleBase>();
        lstModules.add(moduleBase);
        
        // ��ӵ����б�
        JSONObject result = new JSONObject();
        if (archiveManageService.addArchives(logicAddress, lstModules))
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
     * ɾ������
     * @return  �ɹ�����success
     * @throws UnsupportedEncodingException 
     */
    public String deleteArchives() throws UnsupportedEncodingException
    {
        // ����������
        if (StringUtil.IsNullOrEmpty(logicAddress) 
                || StringUtil.IsNullOrEmpty(archiveIds))
        {
            return ERROR;
        }
        
        // ��ֵ���ID
        String[] strSegments = archiveIds.split(",");
        
        // �ϳɵ���ID�б�
        List<Integer> lstArchiveIds = new ArrayList<Integer>();
        for(int i = 0; i < strSegments.length; i++)
        {
            lstArchiveIds.add(Integer.parseInt(strSegments[i]));
        }
        
        // ɾ�������б�
        if (archiveManageService.deleteArchives(logicAddress, lstArchiveIds))
        {
            // ������Ӧ�����Json��ʽ��
            setResponseJson(new ByteArrayInputStream("success".toString().getBytes("UTF-8")));
        }
        else
        {
            // ������Ӧ�����Json��ʽ��
            setResponseJson(new ByteArrayInputStream("error".toString().getBytes("UTF-8")));
        }
        
        return SUCCESS;
    }
}
