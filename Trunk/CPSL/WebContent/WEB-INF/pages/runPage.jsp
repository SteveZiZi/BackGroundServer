<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>运行</title>
<link rel="stylesheet" type="text/css" href="jquery-easyui-1.4.3/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="jquery-easyui-1.4.3/themes/icon.css">
<script type="text/javascript" src="jquery-easyui-1.4.3/jquery.min.js"></script>
<script type="text/javascript" src="jquery-easyui-1.4.3/jquery.easyui.min.js"></script>
<script type="text/javascript" src="jquery-easyui-1.4.3/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript">
    var     selRowIdx;

    
    // 表格刷新函数
    function Refresh()
    {
    	// 刷新设备状态表
    	$("#tbDeviceStatusInfo").datagrid("reload");
    	
    	// 获得回路信息表
    	var tbCC = $("#tbClosedCircuit");
    	// 获得当前选中行
    	selRowIdx = tbCC.datagrid('getRowIndex', tbCC.datagrid('getSelected'));
    	// 刷新回路信息表
    	tbCC.datagrid("reload");
    	
    	// 刷新整流器列表
    	$("#tbRectifier").datagrid("reload");
    }
    // 定时刷新
    setInterval("Refresh()", 3000);

    // 回路信息表加载数据成功后回调的函数
    function onLoadClosedCircuitSuccess(data)
    {
    	// 设置选中行
    	$("#tbClosedCircuit").datagrid('selectRow', selRowIdx);
    }
    
    /**
     * “动作”单元格的格式化器
     * value    字段值
     * row      行记录数据
     * index    行索引
     */
    function formatter_action(value, row, index)
    {
        var s = '<a href="#" onclick="openDialog_ClosedCircuitSwitch(this)">开关</a>&nbsp;&nbsp;';
        var c = '<a href="#" onclick="openDialog_ClosedCircuitLight(this)">亮度</a>';
        return s + c;
    }

    // 打开回路开关设置对话框
    function openDialog_ClosedCircuitSwitch(target)
    {
    	// 获得回路号
    	var tr = $(target).closest('tr.datagrid-row');
        var circuitId = tr.children('td[field="closedCircuitName"]').text();
        
        $("#fm_ClosedCircuitSwitch #closedCircuitName").attr("value", circuitId);
        $("#dlg_ClosedCircuitSwitch").dialog("open").dialog("setTitle", "回路开关设置");
    }
    
    // 回路开关设置（确定按钮）
    function submit_ClosedCircuitSwitch()
    {
    	$("#fm_ClosedCircuitSwitch").form("submit", {
            url:"closedCircuitSwitch",
            onSubmit:function()
            {
                return $(this).form("validate");
            },
            success:function(result)
            {
            	var result = eval('('+result+')');
                if(result.errorMsg)
                {
                    $.messager.alert("提示", result.errorMsg, 'error');
                }
                else
                {
                    $.messager.alert("提示", "设置成功！", 'info');
                }
                
                $("#dlg_ClosedCircuitSwitch").dialog("close");
            }
        });
    }
    
    // 回路开关设置（取消按钮）
    function cancel_ClosedCircuitSwitch()
    {
    	$("#dlg_ClosedCircuitSwitch").dialog("close");
    }
    
    // 打开回路亮度设置对话框
    function openDialog_ClosedCircuitLight(target)
    {
    	// 获得回路号
        var tr = $(target).closest('tr.datagrid-row');
        var circuitId = tr.children('td[field="closedCircuitName"]').text();
        
        $("#fm_ClosedCircuitLight #closedCircuitName").attr("value", circuitId);
        $("#dlg_ClosedCircuitLight").dialog("open").dialog("setTitle", "回路亮度设置");
    }
    
    // 回路亮度设置（确定按钮）
    function submit_ClosedCircuitLight()
    {
        $("#fm_ClosedCircuitLight").form("submit", {
            url:"closedCircuitLight",
            onSubmit:function()
            {
                return $(this).form("validate");
            },
            success:function(result)
            {
                var result = eval('('+result+')');
                if(result.errorMsg)
                {
                    $.messager.alert("提示", result.errorMsg, 'error');
                }
                else
                {
                    $.messager.alert("提示", "设置成功！", 'info');
                }
                
                $("#dlg_ClosedCircuitLight").dialog("close");
            }
        });
    }
    
    // 回路开关设置（取消按钮）
    function cancel_ClosedCircuitLight()
    {
        $("#dlg_ClosedCircuitLight").dialog("close");
    }
    
    // 数据表格的单元格样式
    function cellStyler(value, row, index)
    {
        if (value != "正常")
        {
            return 'color:red;';
        }
    }
    
</script>
</head>
<body>
	<div id="cc" class="easyui-layout" data-options="fit:true">
	    <div data-options="region:'west',split:true,hideCollapsedContent:false" style="width:100px">
	        <a href="getPage_runPage?logicAddress=${param.logicAddress}&deviceID=${param.deviceID}" class="easyui-linkbutton" style="width:100%" 
	        data-options="toggle:true,group:'g_buttons',selected:true,iconCls:'icon-run',size:'large',iconAlign:'top'">运行</a>
	        <a href="getPage_controlPage?logicAddress=${param.logicAddress}&deviceID=${param.deviceID}" class="easyui-linkbutton" style="width:100%" 
	        data-options="toggle:true,group:'g_buttons',iconCls:'icon-control',size:'large',iconAlign:'top'">程控</a>
	        <a href="getPage_logPage?logicAddress=${param.logicAddress}&deviceID=${param.deviceID}" class="easyui-linkbutton" style="width:100%" 
	        data-options="toggle:true,group:'g_buttons',iconCls:'icon-log',size:'large',iconAlign:'top'">日志</a>
	        <a href="getPage_managePage?logicAddress=${param.logicAddress}&deviceID=${param.deviceID}" class="easyui-linkbutton" style="width:100%" 
	        data-options="toggle:true,group:'g_buttons',iconCls:'icon-manage',size:'large',iconAlign:'top'">管理</a>
	        <a href="getPage_configPage?logicAddress=${param.logicAddress}&deviceID=${param.deviceID}" class="easyui-linkbutton" style="width:100%" 
	        data-options="toggle:true,group:'g_buttons',iconCls:'icon-system',size:'large',iconAlign:'top'">配置</a>
	        <a href="getPage_archivePage?logicAddress=${param.logicAddress}&deviceID=${param.deviceID}" class="easyui-linkbutton" style="width:100%" 
            data-options="toggle:true,group:'g_buttons',iconCls:'icon-archive',size:'large',iconAlign:'top'">设备</a>
	    </div>
	    <div data-options="region:'center'">
	        <!-- DCU设备表 -->
	        <br/>
            <table id="tbDeviceStatusInfo" title="DCU" class="easyui-datagrid" fitColumns="true" loadMsg="" striped=true 
                rownumbers="true" url="deviceStatusInfo?logicAddress=${param.logicAddress}">
                <thead>
                    <tr>
		                <th field="deviceName" width="15">名称</th>
		                <th field="deviceVoltage" width="15" align="center">电压（V）</th>
		                <th field="deviceCurrent" width="15" align="center">电流（A）</th>
		                <th field="devicePower" width="15" align="center">功率（W）</th>
		                <th field="deviceTemperature" width="15" align="center">温度（℃）</th>
		                <th field="deviceStatus" width="25" align="center" data-options="styler:cellStyler">状态</th>
		            </tr>
		        </thead>
		    </table>
		    
		    <!-- 回路表 -->
		    <br/>
	        <table id="tbClosedCircuit" title="回路" class="easyui-datagrid" fitColumns="true"  loadMsg="" striped=true 
                pagination="true" singleSelect="true" rownumbers="true" 
                data-options="method:'get', url:'closedCircuitInfo?logicAddress=${param.logicAddress}'">
                <thead>
                    <tr>
                        <th field="closedCircuitName" width="15">C-L</th>
                        <th field="closedCircuitVoltage" width="15" align="center">电压（V）</th>
                        <th field="closedCircuitCurrent" width="15" align="center">电流（A）</th>
                        <th field="closedCircuitPower" width="15" align="center">功率（W）</th>
                        <th field="closedCircuitTemperature" width="15" align="center">温度（℃）</th>
                        <th field="closedCircuitStatus" width="12" align="center" data-options="styler:cellStyler">状态</th>
                        <th field="closedCircuitAction" width="13" align="center" formatter="formatter_action">操作</th>
                    </tr>
                </thead>
            </table>
            
            <!-- 整流器表 -->
            <br/>
            <table id="tbRectifier" title="整流器" class="easyui-datagrid" fitColumns="true" loadMsg="" striped=true 
                pagination="true" singleSelect="true" rownumbers="true" url="rectifierInfo?logicAddress=${param.logicAddress}">
                <thead>
                    <tr>
                        <th field="recifierName" width="15">L-R</th>
                        <th field="recifierVoltage" width="15" align="center">电压（V）</th>
                        <th field="recifierCurrent" width="15" align="center">电流（A）</th>
                        <th field="recifierPower" width="15" align="center">功率（W）</th>
                        <th field="recifierTemperature" width="15" align="center">温度（℃）</th>
                        <th field="recifierStatus" width="25" align="center" data-options="styler:cellStyler">状态</th>
                    </tr>
                </thead>
            </table>
	    </div>
	</div>
	
	<!-- 回路表的工具栏 -->
	<div id="toolbar_ClosedCircuit">
        <div>
            <a href="javascript:openDialog_ClosedCircuitSwitch()" class="easyui-linkbutton" iconCls="icon-edit">开关</a>
            <a href="javascript:openDialog_ClosedCircuitLight()" class="easyui-linkbutton" iconCls="icon-edit">亮度</a>
        </div>
    </div>
    
    <!-- 回路开关设置对话框 -->
    <div id="dlg_ClosedCircuitSwitch" class="easyui-dialog" style="padding: 40px 40px"
        closed="true" buttons="#dlgButtons_ClosedCircuitSwitch">
        <form id="fm_ClosedCircuitSwitch" method="post">
            <input type=hidden name="logicAddress" value="${param.logicAddress}" />
            <table>
                <tr>
                    <td>回路名称：</td>
                    <td><input type="text" name="closedCircuitName" id="closedCircuitName" readonly/></td>
                </tr>
                <tr>
                    <td>开关控制：</td>
                    <td><input name="closedCircuitSwitch" id="closedCircuitSwitch" class="easyui-switchbutton" checked /></td>
                </tr>
            </table>
        </form>
    </div>
    
    <!-- 回路开关设置对话框的按钮 -->
    <div id="dlgButtons_ClosedCircuitSwitch">
        <a href="javascript:submit_ClosedCircuitSwitch()" class="easyui-linkbutton" iconCls="icon-ok">确定</a>
        <a href="javascript:cancel_ClosedCircuitSwitch()" class="easyui-linkbutton" iconCls="icon-cancel">取消</a>
    </div>
    
    <!-- 回路亮度设置对话框 -->
    <div id="dlg_ClosedCircuitLight" class="easyui-dialog" style="padding: 40px 40px"
        closed="true" buttons="#dlgButtons_ClosedCircuitLight">
        <form id="fm_ClosedCircuitLight" method="post">
            <input type=hidden name="logicAddress" value="${param.logicAddress}" />
            <table>
                <tr>
                    <td>回路名称：</td>
                    <td><input type="text" name="closedCircuitName" id="closedCircuitName" readonly/></td>
                </tr>
                <tr>
                    <td>亮&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;度：</td>
                    <td>
                        <select name="closedCircuitLight" id="closedCircuitLight" class="easyui-combobox" editable="false"  style="width:148px;">
                            <option value="100" selected="true">100%</option>
                            <option value="90">90%</option>
                            <option value="80">80%</option>
                            <option value="70">70%</option>
                            <option value="60">60%</option>
                            <option value="50">50%</option>
                            <option value="40">40%</option>
                            <option value="30">30%</option>
                            <option value="20">20%</option>
                            <option value="10">10%</option>
                            <option value="0">0%</option>
                        </select>
                    </td>
                </tr>
            </table>
        </form>
    </div>
    
    <!-- 回路亮度对话框的按钮 -->
    <div id="dlgButtons_ClosedCircuitLight">
        <a href="javascript:submit_ClosedCircuitLight()" class="easyui-linkbutton" iconCls="icon-ok">确定</a>
        <a href="javascript:cancel_ClosedCircuitLight()" class="easyui-linkbutton" iconCls="icon-cancel">取消</a>
    </div>
</body>
</html>