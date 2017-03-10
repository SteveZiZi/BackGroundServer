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
 * 设备控制器
 * @author zgm
 *
 */
public class DeviceAction extends ActionSupport
{
    private static final long       serialVersionUID = 1L;
    private DeviceManageService     deviceManageService;        // 设备管理接口
    private InputStream             responseJson;               // 响应结果（Json格式）
    
    private Integer                 page;                       // 页号
    private Integer                 rows;                       // 行号
    
    private String                  logicAddress;               // 设备逻辑地址
    
    private String                  closedCircuitName;          // 回路名称（例如L#0102，表示1号设备的2号回路）
    private String                  closedCircuitLight;         // 回路亮度
    private String                  closedCircuitSwitch;        // 回路开关

    
    /**
     * 设置设备管理接口
     * @param deviceManageService   设备管理对象
     */
    public void setDeviceManageService(DeviceManageService deviceManageService)
    {
        this.deviceManageService = deviceManageService;
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
     * 设置回路名称
     * @param closedCircuitName     回路名称
     */
    public void setClosedCircuitName(String closedCircuitName)
    {
        this.closedCircuitName = closedCircuitName;
    }
    
    /**
     * 设置回路亮度
     * @param closedCircuitLight    回路亮度
     */
    public void setClosedCircuitLight(String closedCircuitLight)
    {
        this.closedCircuitLight = closedCircuitLight;
    }
    
    /**
     * 设置回路开关
     * @param closedCircuitSwitch   回路开关
     */
    public void setClosedCircuitSwitch(String closedCircuitSwitch)
    {
        this.closedCircuitSwitch = closedCircuitSwitch;
    }
    
    /**
     * 获得设备列表
     * @return  成功返回success
     * @throws Exception
     */
    public String getDeviceList() throws Exception
    {
        // 获得供电系统管理的设备列表
        List<IDevice> lstDevices = deviceManageService.getDeviceList();
        if (null == lstDevices)
        {
            return ERROR;
        }

        // 创建设备节点
        JSONArray childs = new JSONArray();
        for (IDevice device : lstDevices)
        {
            // 检查设备类型
            if (!(device instanceof CDcuDevice))
            {
                continue;
            }
            
            // 忽略不在线的设备
            CDcuDevice dcuDevice = (CDcuDevice) device;
            if (dcuDevice.GetDeviceState() == enumDeviceState.OffLine)
            {
                continue;
            }
           
            // 创建Json对象
            JSONObject node = new JSONObject();
            node.put("id", dcuDevice.getID());
            node.put("text", dcuDevice.getDeviceName());
            node.put("iconCls", "icon-tree-device");
            node.put("url", "getPage_runPage?logicAddress=" + dcuDevice.getLogicAddress() + "&deviceID=" + dcuDevice.getID());

            // 添加Json对象到
            childs.add(node);
        }

        // 创建根节点
        JSONObject root = new JSONObject();
        root.put("text", "Project1");
        root.put("iconCls", "icon-tree-project");
        root.put("children", childs);

        // 创建树节点
        JSONArray tree = new JSONArray();
        tree.add(root);
        
        // 设置响应结果（Json格式）
        setResponseJson(new ByteArrayInputStream(tree.toString().getBytes("UTF-8")));
        return SUCCESS;
    }
    
    /**
     * 获得设备状态信息
     * @return  成功返回success
     * @throws UnsupportedEncodingException 
     */
    public String getDeviceStatusInfo() throws UnsupportedEncodingException
    {
        // 检查输入参数
        if (StringUtil.IsNullOrEmpty(logicAddress))
        {
            return ERROR;
        }
        
        // 根据设备逻辑地址，获得设备对象
        IDevice device = deviceManageService.GetDeviceByLogicAddress(logicAddress);
        if ((null == device) || !(device instanceof CDcuDevice))
        {
            return ERROR;
        }
        CDcuDevice dcuDevice = (CDcuDevice)device;

        // 获得设备状态信息
        JSONObject statusInfo = new JSONObject();
        statusInfo.put("deviceName", dcuDevice.getDeviceName());
        statusInfo.put("deviceVoltage", String.format("%.1f", dcuDevice.getVoltage()));
        statusInfo.put("deviceCurrent", String.format("%.1f", dcuDevice.getCurrent()));
        statusInfo.put("devicePower", String.format("%.2f", dcuDevice.getPower()));
        statusInfo.put("deviceTemperature", String.format("%.1f", dcuDevice.getTemperature()));
        statusInfo.put("deviceStatus", dcuDevice.getStatus());

        // 添加设备信息到行集合中
        JSONArray rows = new JSONArray();
        rows.add(statusInfo);
        
        // 合成返回结果
        JSONObject result = new JSONObject();
        result.put("total", 1);
        result.put("rows", rows);
        
        // 设置响应结果（Json格式）
        setResponseJson(new ByteArrayInputStream(result.toString().getBytes("UTF-8")));
        return SUCCESS;
    }
    
    /**
     * 获得设备回路的信息
     * @return  成功返回success
     * @throws UnsupportedEncodingException 
     */
    public String getClosedCircuitInfo() throws UnsupportedEncodingException
    {
        // 检查输入参数
        if (StringUtil.IsNullOrEmpty(logicAddress)
                || (page <= 0) 
                || (rows <= 0))
        {
            return ERROR;
        }
        
        // 根据设备逻辑地址，获得设备对象
        IDevice device = deviceManageService.GetDeviceByLogicAddress(logicAddress);
        if ((null == device) || !(device instanceof CDcuDevice))
        {
            return ERROR;
        }
        CDcuDevice dcuDevice = (CDcuDevice)device;

        // 循环遍历所有回路
        int iCount = 0;
        JSONArray jsonArray = new JSONArray();
        List<CClosedCircuit> lstClosedCircuit = dcuDevice.getClosedCircuitList();
        for (CClosedCircuit closedCircuit : lstClosedCircuit)
        {
            // 递增行数
            iCount++;
            
            // 检查行号是否在指定的页码范围
            if ((iCount > (page - 1) * rows) && (iCount <= page * rows))
            {
                // 获得回路状态信息
                JSONObject closedCircuitInfo = new JSONObject();
                closedCircuitInfo.put("closedCircuitName", String.format("L#%02d%02d", dcuDevice.getID(), closedCircuit.getID()));
                closedCircuitInfo.put("closedCircuitVoltage", String.format("%.1f", closedCircuit.getVoltage()));
                closedCircuitInfo.put("closedCircuitCurrent", String.format("%.1f", closedCircuit.getCurrent()));
                closedCircuitInfo.put("closedCircuitPower", String.format("%.2f", closedCircuit.getPower()));
                closedCircuitInfo.put("closedCircuitTemperature", String.format("%.1f", closedCircuit.getTemperature()));
                closedCircuitInfo.put("closedCircuitStatus", closedCircuit.getStatus());
              
                // 添加回路状态信息到行集合中
                jsonArray.add(closedCircuitInfo);
            }
        }

        // 合成返回结果
        JSONObject result = new JSONObject();
        result.put("total", lstClosedCircuit.size());
        result.put("rows", jsonArray);
        
        // 设置响应结果（Json格式）
        setResponseJson(new ByteArrayInputStream(result.toString().getBytes("UTF-8")));
        return SUCCESS;
    }

    /**
     * 获得设备整流器信息
     * @return 成功返回success
     * @throws UnsupportedEncodingException 
     */
    public String getRectifierInfo() throws UnsupportedEncodingException
    {
        // 检查输入参数
        if (StringUtil.IsNullOrEmpty(logicAddress)
                || (page <= 0) 
                || (rows <= 0))
        {
            return ERROR;
        }
        
        // 根据设备逻辑地址，获得设备对象
        IDevice device = deviceManageService.GetDeviceByLogicAddress(logicAddress);
        if ((null == device) || !(device instanceof CDcuDevice))
        {
            return ERROR;
        }
        CDcuDevice dcuDevice = (CDcuDevice) device;

        // 循环遍历所有回路
        int iCount = 0;
        JSONArray jsonArray = new JSONArray();
        List<CClosedCircuit> lstClosedCircuit = dcuDevice.getClosedCircuitList();
        for (CClosedCircuit closedCircuit : lstClosedCircuit)
        {
            List<CRectifier> lstRectifier = closedCircuit.getRectifierList();
            for (CRectifier rectifier : lstRectifier)
            {
                // 递增行数
                iCount++;
                
                // 检查行号是否在指定的页码范围
                if ((iCount > (page - 1) * rows) && (iCount <= page * rows))
                {
                    // 获得整流器状态信息
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

                    // 添加整流器状态信息到行集合中
                    jsonArray.add(rectifierInfo);
                }
            }
        }

        // 合成返回结果
        JSONObject result = new JSONObject();
        result.put("total", iCount);
        result.put("rows", jsonArray);
        
        // 设置响应结果（Json格式）
        setResponseJson(new ByteArrayInputStream(result.toString().getBytes("UTF-8")));
        return SUCCESS;
    }
    
    /**
     * 设置回路亮度
     * @return  成功返回success
     * @throws UnsupportedEncodingException
     */
    public String changeClosedCircuitLight() throws UnsupportedEncodingException
    {
        // 检查输入参数
        if (StringUtil.IsNullOrEmpty(logicAddress)
                || StringUtil.IsNullOrEmpty(closedCircuitName)
                || StringUtil.IsNullOrEmpty(closedCircuitLight))
        {
            return ERROR;
        }
        
        // 修改设备指定回路的亮度
        JSONObject result = new JSONObject();
        if (deviceManageService.changeClosedCircuitLight(logicAddress, closedCircuitName, closedCircuitLight))
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
     * 回路开关
     * @return  成功返回success
     * @throws UnsupportedEncodingException
     */
    public String closedCircuitSwitch() throws UnsupportedEncodingException
    {
        // 检查输入参数
        if (StringUtil.IsNullOrEmpty(logicAddress)
                || StringUtil.IsNullOrEmpty(closedCircuitName)
                || StringUtil.IsNullOrEmpty(closedCircuitSwitch))
        {
            return ERROR;
        }
        
        // 回路开关
        JSONObject result = new JSONObject();
        if (deviceManageService.closedCircuitSwitch(logicAddress, closedCircuitName, closedCircuitSwitch))
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
}
