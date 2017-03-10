<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>程控</title>
<link rel="stylesheet" type="text/css" href="jquery-easyui-1.4.3/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="jquery-easyui-1.4.3/themes/icon.css">
<script type="text/javascript" src="jquery-easyui-1.4.3/jquery.min.js"></script>
<script type="text/javascript" src="jquery-easyui-1.4.3/jquery.easyui.min.js"></script>
<script type="text/javascript" src="jquery-easyui-1.4.3/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript">

    //表格刷新函数
    function Refresh()
    {
        // 刷新告警日志表
        $("#tbAlarmLogInfo").datagrid("reload");
    }
    // 定时刷新
    setInterval("Refresh()", 3000);
    
</script>
</head>
<body>
	<div id="cc" class="easyui-layout" data-options="fit:true">
	    <div data-options="region:'west',split:true,hideCollapsedContent:false" style="width:100px">
            <a href="getPage_runPage?logicAddress=${param.logicAddress}&deviceID=${param.deviceID}" class="easyui-linkbutton" style="width:100%" 
            data-options="toggle:true,group:'g_buttons',iconCls:'icon-run',size:'large',iconAlign:'top'">运行</a>
            <a href="getPage_controlPage?logicAddress=${param.logicAddress}&deviceID=${param.deviceID}" class="easyui-linkbutton" style="width:100%" 
            data-options="toggle:true,group:'g_buttons',iconCls:'icon-control',size:'large',iconAlign:'top'">程控</a>
            <a href="getPage_logPage?logicAddress=${param.logicAddress}&deviceID=${param.deviceID}" class="easyui-linkbutton" style="width:100%" 
            data-options="toggle:true,group:'g_buttons',selected:true,iconCls:'icon-log',size:'large',iconAlign:'top'">日志</a>
            <a href="getPage_managePage?logicAddress=${param.logicAddress}&deviceID=${param.deviceID}" class="easyui-linkbutton" style="width:100%" 
            data-options="toggle:true,group:'g_buttons',iconCls:'icon-manage',size:'large',iconAlign:'top'">管理</a>
            <a href="getPage_configPage?logicAddress=${param.logicAddress}&deviceID=${param.deviceID}" class="easyui-linkbutton" style="width:100%" 
            data-options="toggle:true,group:'g_buttons',iconCls:'icon-system',size:'large',iconAlign:'top'">配置</a>
            <a href="getPage_archivePage?logicAddress=${param.logicAddress}&deviceID=${param.deviceID}" class="easyui-linkbutton" style="width:100%" 
            data-options="toggle:true,group:'g_buttons',iconCls:'icon-archive',size:'large',iconAlign:'top'">设备</a>
        </div>
	    <div data-options="region:'center'">
	        <!-- 告警日志表 -->
	        <table id="tbAlarmLogInfo" title="告警日志" class="easyui-datagrid" fitColumns="true"  loadMsg="" striped=true 
                pagination="true" rownumbers="true" url="alarmLogList?logicAddress=${param.logicAddress}" pageSize="20">
                <thead>
                    <tr>
                        <th field="logDate" width="50">日志时间</th>
                        <th field="logType" width="50">日志类型</th>
                        <th field="logContent" width="50">日志内容</th>
                    </tr>
                </thead>
            </table>
	    </div>
	</div>
</body>
</html>