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
 * 系统配置控制器
 * @author zgm
 *
 */
public class SysConfigAction extends ActionSupport
{
    private static final long       serialVersionUID = 1L;
    private DeviceManageService     deviceManageService;        // 设备管理接口
    private ConfigManageService     configManageService;        // 配置管理对象
    private InputStream             responseJson;               // 响应结果（Json格式）
    
    private Integer                 page;                       // 页号
    private Integer                 rows;                       // 行号
    
    private String                  logicAddress;               // 设备逻辑地址
    private Date                    newDatetime;                // 系统时间
    private String                  adminPhone;                 // 管理员手机号
    
    private String                  closedCircuitName;          // 回路名称(L#0102，表示1号设备的2号回路)
    private Integer                 percent0;                   // 亮度0%对应的电压值
    private Integer                 percent10;                  // 亮度10%对应的电压值
    private Integer                 percent20;                  // 亮度20%对应的电压值
    private Integer                 percent30;                  // 亮度30%对应的电压值
    private Integer                 percent40;                  // 亮度40%对应的电压值
    private Integer                 percent50;                  // 亮度50%对应的电压值
    private Integer                 percent60;                  // 亮度60%对应的电压值
    private Integer                 percent70;                  // 亮度70%对应的电压值
    private Integer                 percent80;                  // 亮度80%对应的电压值
    private Integer                 percent90;                  // 亮度90%对应的电压值
    private Integer                 percent100;                 // 亮度100%对应的电压值
    

    /**
     * 设置设备管理接口
     * @param deviceManageService   设备管理对象
     */
    public void setDeviceManageService(DeviceManageService deviceManageService)
    {
        this.deviceManageService = deviceManageService;
    }
    
    /**
     * 设置配置管理对象
     * @param configManageService   配置管理对象
     */
    public void setConfigManageService(ConfigManageService configManageService)
    {
        this.configManageService = configManageService;
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
     * 设置系统时间
     * @param newDatetime   系统时间
     */
    public void setNewDatetime(Date newDatetime)
    {
        this.newDatetime = newDatetime;
    }
    
    /**
     * 设置管理员手机号
     * @param adminPhone    管理员手机号
     */
    public void setAdminPhone(String adminPhone)
    {
        this.adminPhone = adminPhone;
    }

    /**
     * 设置回路名称(L#0102，表示1号设备的2号回路)
     * @param closedCircuitName 回路名称
     */
    public void setClosedCircuitName(String closedCircuitName)
    {
        this.closedCircuitName = closedCircuitName;
    }

    /**
     * 设置亮度0%对应的电压值
     * @param percent0  电压值
     */
    public void setPercent0(Integer percent0)
    {
        this.percent0 = percent0;
    }

    /**
     * 设置亮度10%对应的电压值
     * @param percent10  电压值
     */
    public void setPercent10(Integer percent10)
    {
        this.percent10 = percent10;
    }

    /**
     * 设置亮度20%对应的电压值
     * @param percent20  电压值
     */
    public void setPercent20(Integer percent20)
    {
        this.percent20 = percent20;
    }

    /**
     * 设置亮度30%对应的电压值
     * @param percent30  电压值
     */
    public void setPercent30(Integer percent30)
    {
        this.percent30 = percent30;
    }

    /**
     * 设置亮度40%对应的电压值
     * @param percent40  电压值
     */
    public void setPercent40(Integer percent40)
    {
        this.percent40 = percent40;
    }

    /**
     * 设置亮度50%对应的电压值
     * @param percent50  电压值
     */
    public void setPercent50(Integer percent50)
    {
        this.percent50 = percent50;
    }

    /**
     * 设置亮度60%对应的电压值
     * @param percent60  电压值
     */
    public void setPercent60(Integer percent60)
    {
        this.percent60 = percent60;
    }

    /**
     * 设置亮度70%对应的电压值
     * @param percent70  电压值
     */
    public void setPercent70(Integer percent70)
    {
        this.percent70 = percent70;
    }

    /**
     * 设置亮度80%对应的电压值
     * @param percent80  电压值
     */
    public void setPercent80(Integer percent80)
    {
        this.percent80 = percent80;
    }

    /**
     * 设置亮度90%对应的电压值
     * @param percent90  电压值
     */
    public void setPercent90(Integer percent90)
    {
        this.percent90 = percent90;
    }

    /**
     * 设置亮度100%对应的电压值
     * @param percent100  电压值
     */
    public void setPercent100(Integer percent100)
    {
        this.percent100 = percent100;
    }
    
    /**
     * 获得管理员手机号
     * @return 成功返回success
     * @throws UnsupportedEncodingException 
     */
    public String getAdminPhone() throws UnsupportedEncodingException
    {
        // 检查输入参数
        if (StringUtil.IsNullOrEmpty(logicAddress))
        {
            return ERROR;
        }
        
        // 合成返回结果
        JSONObject result = new JSONObject();
        result.put("adminPhone", configManageService.getAdminPhone(logicAddress));
        
        // 设置响应结果（Json格式）
        setResponseJson(new ByteArrayInputStream(result.toString().getBytes("UTF-8")));
        return SUCCESS;
    }

    /**
     * 设置管理员手机号
     * @return  成功返回success
     * @throws UnsupportedEncodingException
     */
    public String modifyAdminPhone() throws UnsupportedEncodingException
    {
        // 检查输入参数
        if (StringUtil.IsNullOrEmpty(logicAddress)
                || StringUtil.IsNullOrEmpty(adminPhone))
        {
            return ERROR;
        }
        
        // 设置管理员电话
        if (configManageService.setAdminPhone(logicAddress, adminPhone))
        {
            // 设置响应结果（Json格式）
            setResponseJson(new ByteArrayInputStream("success".getBytes("UTF-8")));
        }
        else 
        {
            // 设置响应结果（Json格式）
            setResponseJson(new ByteArrayInputStream("error".getBytes("UTF-8")));
        }
        
        return SUCCESS;
    }
    
    /**
     * 设置设备时间
     * @return  成功返回success
     * @throws UnsupportedEncodingException
     */
    public String setDeviceTime() throws UnsupportedEncodingException
    {
        // 检查输入参数
        if (StringUtil.IsNullOrEmpty(logicAddress)
                || (null == newDatetime))
        {
            return ERROR;
        }
        
        // 设置设备时间
        if (configManageService.setDeviceTime(logicAddress, newDatetime))
        {
            // 设置响应结果（Json格式）
            setResponseJson(new ByteArrayInputStream("success".getBytes("UTF-8")));
        }
        else 
        {
            // 设置响应结果（Json格式）
            setResponseJson(new ByteArrayInputStream("error".getBytes("UTF-8")));
        }
        
        return SUCCESS;
    }
    
    /**
     * 获得Dim-Vdb列表
     * @return  成功返回success
     * @throws UnsupportedEncodingException
     */
    public String getDimVdbList() throws UnsupportedEncodingException
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
                // 获得回路的Dim-Vdb表
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
     * 设置回路Dim-Vdb值
     * @return  成功返回success
     * @throws UnsupportedEncodingException 
     */
    public String setClosedCircuitDimVdb() throws UnsupportedEncodingException
    {
        // 检查输入参数
        if (StringUtil.IsNullOrEmpty(logicAddress)
                || StringUtil.IsNullOrEmpty(closedCircuitName))
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
        
        // 获得指定回路的Dim-Vdb表
        int iClosedCircuitId = Integer.parseInt(closedCircuitName.substring(4, 6));
        Map<String, Integer> mapDimVdb = dcuDevice.getDimVdbMapByCCId(iClosedCircuitId);
        if (null == mapDimVdb)
        {
            return ERROR;
        }
        
        // 修改回路的Dim-Vdb值
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
        
        // 设置响应结果（Json格式）
        setResponseJson(new ByteArrayInputStream("success".getBytes("UTF-8")));
        return SUCCESS;
    }

}
