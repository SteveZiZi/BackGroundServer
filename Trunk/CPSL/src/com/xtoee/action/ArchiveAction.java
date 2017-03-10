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
 * 档案管理控制器
 * @author zgm
 *
 */
public class ArchiveAction extends ActionSupport
{
    private static final long       serialVersionUID = 1L;
    private DeviceManageService     deviceManageService;        // 设备管理接口
    private ArchiveManageService    archiveManageService;       // 档案管理对象
    private InputStream             responseJson;               // 响应结果（Json格式）
    
    private Integer                 page;                       // 页号
    private Integer                 rows;                       // 行号
    
    private String                  logicAddress;               // 设备逻辑地址
    
    private Integer                 moduleId;                   // 模块ID
    private Integer                 closedCircuit;              // 回路号
    private Integer                 functionType;               // 功能类型
    private Integer                 moduleType;                 // 设备类型
    private Integer                 commParam;                  // 通讯参数
    private String                  moduleAddress;              // 设备地址
    
    private String                  archiveIds;                 // 待删除的档案ID

    
    /**
     * 设置设备管理接口
     * @param deviceManageService   设备管理对象
     */
    public void setDeviceManageService(DeviceManageService deviceManageService)
    {
        this.deviceManageService = deviceManageService;
    }
    
    /**
     * 设置档案管理对象
     * @param archiveManageService  档案管理对象
     */
    public void setArchiveManageService(ArchiveManageService archiveManageService)
    {
        this.archiveManageService = archiveManageService;
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
     * 设置模块ID
     * @param moduleId  模块ID
     */
    public void setModuleId(Integer moduleId)
    {
        this.moduleId = moduleId;
    }

    /**
     * 设置回路号
     * @param closedCircuit 回路号
     */
    public void setClosedCircuit(Integer closedCircuit)
    {
        this.closedCircuit = closedCircuit;
    }

    /**
     * 设置功能类型
     * @param functionType  功能类型
     */
    public void setFunctionType(Integer functionType)
    {
        this.functionType = functionType;
    }

    /**
     * 设置设备类型
     * @param moduleType    设备类型
     */
    public void setModuleType(Integer moduleType)
    {
        this.moduleType = moduleType;
    }

    /**
     * 设置设备地址
     * @param moduleAddress 设备地址
     */
    public void setModuleAddress(String moduleAddress)
    {
        this.moduleAddress = moduleAddress;
    }

    /**
     * 设置通讯参数
     * @param commParam 通讯参数
     */
    public void setCommParam(Integer commParam)
    {
        this.commParam = commParam;
    }
    
    /**
     * 设置待删除的档案Id
     * @param archiveIds    待删除的档案Id
     */
    public void setArchiveIds(String archiveIds)
    {
        this.archiveIds = archiveIds;
    }

    /**
     * 获得档案列表
     * @return  成功返回success
     * @throws UnsupportedEncodingException
     */
    public String getArchiveList() throws UnsupportedEncodingException
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

        // 获得设备档案列表
        List<CModuleBase> lstArchives = archiveManageService.getArchiveList(logicAddress);
        if (null == lstArchives)
        {
            return ERROR;
        }

        // 循环遍历设备档案列表
        int iCount = 0;
        JSONArray jsonArray = new JSONArray();
        for (CModuleBase archive : lstArchives)
        {
            // 递增行数
            iCount++;
            
            // 检查行号是否在指定的页码范围
            if ((iCount > (page - 1) * rows) && (iCount <= page * rows))
            {
                // 获得设备档案信息
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

                // 添加设备档案信息到行集合中
                jsonArray.add(row);
            }
        }

        // 合成返回结果
        JSONObject result = new JSONObject();
        result.put("total", lstArchives.size());
        result.put("rows", jsonArray);
        
        // 设置响应结果（Json格式）
        setResponseJson(new ByteArrayInputStream(result.toString().getBytes("UTF-8")));
        return SUCCESS;
    }
    
    /**
     * 添加档案
     * @return  成功返回success
     * @throws UnsupportedEncodingException
     */
    public String addArchives() throws UnsupportedEncodingException
    {
        // 检查输入参数
        if (StringUtil.IsNullOrEmpty(logicAddress)
                || StringUtil.IsNullOrEmpty(moduleAddress)
                || !moduleAddress.matches("\\d{12}"))
        {
            return ERROR;
        }
        
        // 创建设备节点对象
        CModuleBase moduleBase = new CModuleBase(moduleId, moduleAddress, closedCircuit, functionType, moduleType, commParam);
        
        // 添加设备节点对象到容器中
        List<CModuleBase> lstModules = new ArrayList<CModuleBase>();
        lstModules.add(moduleBase);
        
        // 添加档案列表
        JSONObject result = new JSONObject();
        if (archiveManageService.addArchives(logicAddress, lstModules))
        {
            result.put("success", "true");
        }
        else
        {
            result.put("success", "false");
            result.put("errorMsg", "设置失败！");
        }
        
        // 设置响应结果（Json格式）
        setResponseJson(new ByteArrayInputStream(result.toString().getBytes("UTF-8")));
        return SUCCESS;
    }
    
    /**
     * 删除档案
     * @return  成功返回success
     * @throws UnsupportedEncodingException 
     */
    public String deleteArchives() throws UnsupportedEncodingException
    {
        // 检查输入参数
        if (StringUtil.IsNullOrEmpty(logicAddress) 
                || StringUtil.IsNullOrEmpty(archiveIds))
        {
            return ERROR;
        }
        
        // 拆分档案ID
        String[] strSegments = archiveIds.split(",");
        
        // 合成档案ID列表
        List<Integer> lstArchiveIds = new ArrayList<Integer>();
        for(int i = 0; i < strSegments.length; i++)
        {
            lstArchiveIds.add(Integer.parseInt(strSegments[i]));
        }
        
        // 删除档案列表
        if (archiveManageService.deleteArchives(logicAddress, lstArchiveIds))
        {
            // 设置响应结果（Json格式）
            setResponseJson(new ByteArrayInputStream("success".toString().getBytes("UTF-8")));
        }
        else
        {
            // 设置响应结果（Json格式）
            setResponseJson(new ByteArrayInputStream("error".toString().getBytes("UTF-8")));
        }
        
        return SUCCESS;
    }
}
