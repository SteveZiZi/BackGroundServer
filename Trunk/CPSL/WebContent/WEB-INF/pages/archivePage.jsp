<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>设备</title>
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
        $("#tbArchiveInfo").datagrid("reload");
    }
    // 定时刷新
    setInterval("Refresh()", 3000);
    
    /**
     * “动作”单元格的格式化器
     * value    字段值
     * row      行记录数据
     * index    行索引
     */
    function formatter_action(value, row, index)
    {
        var c = '<a href="#" onclick="deleteArchive(this)">删除档案</a>';
        return c;
    }
    
    // 删除档案
    function deleteArchive(target)
    {
    	$.messager.confirm('提示', '确定要删除吗?', function(bFlag)
    	{
            if (bFlag)
            {
            	// 获得任务Id
                var tr = $(target).closest('tr.datagrid-row');
                var archiveId = tr.children('td[field="moduleId"]').text();
                
                // 发送添加任务请求
                $.ajax(
                {
                    type:"post",
                    url:"deleteArchives",
                    data:
                    {
                    	logicAddress:"${param.logicAddress}",
                        archiveIds:archiveId
                    },
                    dataType:"text",
                    success:function(data, textStatus)
                    {
                        if($.trim(data) == "success")
                        {
                            // 重新加载设备档案列表
                            $("#tbArchiveInfo").datagrid("reload");
                        }
                        else
                        {
                            $.messager.alert('提示', '删除失败!', 'error');
                        }
                    }
                });
            }
        });
    }
    
    // 添加档案
    function openDialog_AddArchive()
    {
        // 重置添加档案对话框
        resetDialog_AddArchive();

        // 弹出添加档案对话框
        $("#dlg_AddArchive").dialog("open").dialog("setTitle", "添加档案");
    }

    /**
    * 重置添加档案对话框 
    *
    */
    function resetDialog_AddArchive()
    {
        
    }
    
    // 添加档案（确定按钮）
    function submit_AddArchive()
    {
        $("#fm_AddArchive").form("submit", {
            url:"addArchives",
            onSubmit:function()
            {
                return $(this).form("validate");
            },
            success:function(result)
            {
                // 关闭对话框
                $("#dlg_AddArchive").dialog("close");
                
                var result = eval('('+result+')');
                if(result.errorMsg)
                {
                    // 弹出提示信息
                    $.messager.alert("提示", result.errorMsg, 'error');
                }
                else
                {
                    // 重新加载设备档案列表
                    $("#tbArchiveInfo").datagrid("reload");
                }
            }
        });
    }
    
    // 添加档案（取消按钮）
    function cancel_AddArchive()
    {
        $("#dlg_AddArchive").dialog("close");
    }
    
    // 扩展验证器
    $.extend($.fn.validatebox.defaults.rules, {
        // 验证设备地址
        checkModuleAddress:{
            validator: function(value)
            {
                var rex = /^\d{12}$/;
                return rex.test(value);
            },
            message: '设备地址必须为12位数字'
        },
    });
    
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
            data-options="toggle:true,group:'g_buttons',iconCls:'icon-log',size:'large',iconAlign:'top'">日志</a>
            <a href="getPage_managePage?logicAddress=${param.logicAddress}&deviceID=${param.deviceID}" class="easyui-linkbutton" style="width:100%" 
            data-options="toggle:true,group:'g_buttons',iconCls:'icon-manage',size:'large',iconAlign:'top'">管理</a>
            <a href="getPage_configPage?logicAddress=${param.logicAddress}&deviceID=${param.deviceID}" class="easyui-linkbutton" style="width:100%" 
            data-options="toggle:true,group:'g_buttons',iconCls:'icon-system',size:'large',iconAlign:'top'">配置</a>
            <a href="getPage_archivePage?logicAddress=${param.logicAddress}&deviceID=${param.deviceID}" class="easyui-linkbutton" style="width:100%" 
            data-options="toggle:true,group:'g_buttons',selected:true,iconCls:'icon-archive',size:'large',iconAlign:'top'">设备</a>
        </div>
	    <div data-options="region:'center'">
	        <!-- 设备档案表 -->
	        <table id="tbArchiveInfo" title="设备档案" class="easyui-datagrid" fitColumns="true"  loadMsg="" striped=true 
                pagination="true" rownumbers="true" toolbar="#toolbar_AddArchive" url="archiveList?logicAddress=${param.logicAddress}" pageSize="20">
                <thead>
                    <tr>
                        <th field="moduleId" width="50">设备序号</th>
                        <th field="moduleAddress" width="50">设备地址</th>
                        <th field="closedCircuit" width="50">回路号</th>
                        <th field="functionType" width="50">功能类型</th>
                        <th field="moduleType" width="50">设备类型</th>
                        <th field="commParam" width="50">通讯参数</th>
                        <th field="archiveAction" width="50" formatter="formatter_action" align="center">操作</th>
                    </tr>
                </thead>
            </table>
	    </div>
	</div>
	
	<!-- 任务列表的工具栏 -->
    <div id="toolbar_AddArchive">
        <div>
            <a href="javascript:openDialog_AddArchive()" class="easyui-linkbutton" iconCls="icon-add">添加档案</a>
        </div>
    </div>
    
    <!-- 添加控制任务对话框 -->
    <div id="dlg_AddArchive" class="easyui-dialog" style="padding: 20px 20px" closed="true" buttons="#dlgButtons_AddArchive">
        <form id="fm_AddArchive" method="post">
            <input type=hidden name="logicAddress" value="${param.logicAddress}" />
            <table>
                <tr>
                    <td>设备序号：</td>
                    <td><input name="moduleId" id="moduleId" class="easyui-numberspinner" data-options="min:1,max:160" value="1" required="true" /></td>
                </tr>
                <tr>
                    <td>设备节点地址：</td>
                    <td><input name="moduleAddress" id="moduleAddress" class="easyui-textbox" 
                    value="000000000001" required="true" data-options="validType:'checkModuleAddress'" /></td>
                </tr>
                <tr>
                    <td>回路号：</td>
                    <td>
                        <select name="closedCircuit" id="closedCircuit" class="easyui-combobox" editable="false" style="width:148px;">
                            <option value ="1">L#${param.deviceID >= 10? "": "0"}${param.deviceID}01</option>
                            <option value ="2">L#${param.deviceID >= 10? "": "0"}${param.deviceID}02</option>
                            <option value ="3">L#${param.deviceID >= 10? "": "0"}${param.deviceID}03</option>
                            <option value ="4">L#${param.deviceID >= 10? "": "0"}${param.deviceID}04</option>
                            <option value ="5">L#${param.deviceID >= 10? "": "0"}${param.deviceID}05</option>
                            <option value ="6">L#${param.deviceID >= 10? "": "0"}${param.deviceID}06</option>
                            <option value ="7">L#${param.deviceID >= 10? "": "0"}${param.deviceID}07</option>
                            <option value ="8">L#${param.deviceID >= 10? "": "0"}${param.deviceID}08</option>
                            <option value ="9">L#${param.deviceID >= 10? "": "0"}${param.deviceID}09</option>
                            <option value ="10">L#${param.deviceID >= 10? "": "0"}${param.deviceID}10</option>
                            <option value ="11">L#${param.deviceID >= 10? "": "0"}${param.deviceID}11</option>
                            <option value ="12">L#${param.deviceID >= 10? "": "0"}${param.deviceID}12</option>
                            <option value ="13">L#${param.deviceID >= 10? "": "0"}${param.deviceID}13</option>
                            <option value ="14">L#${param.deviceID >= 10? "": "0"}${param.deviceID}14</option>
                            <option value ="15">L#${param.deviceID >= 10? "": "0"}${param.deviceID}15</option>
                            <option value ="16">L#${param.deviceID >= 10? "": "0"}${param.deviceID}16</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>功能类型：</td>
                    <td>
                        <select name="functionType" id="functionType" class="easyui-combobox" editable="false" style="width:148px;">
                            <option value ="0" selected>保留</option>
                            <option value ="1">控制类型</option>
						    <option value ="2">面板显示类型</option>
						</select>
                    </td>
                </tr>
                <tr>
                    <td>设备类型：</td>
                    <td>
                        <select name="moduleType" id="moduleType" class="easyui-combobox" editable="false" style="width:148px;">
                            <option value ="0" selected>DCU主模块</option>
                            <option value ="1">空调模块</option>
                            <option value ="2">8路继电器模块</option>
                            <option value ="3">4路调光板</option>
                            <option value ="4">2路继电器模块</option>
                            <option value ="5">3路LED调光板</option>
                            <option value ="6">门牌显示模块</option>
                            <option value ="7">RFID插卡取电模块</option>
                            <option value ="8">20按键模块</option>
                            <option value ="9">整流器</option>
                            <option value ="10">光感器</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>通讯参数：</td>
                    <td>
                        <select name="commParam" id="commParam" class="easyui-combobox" editable="false" style="width:148px;">
                            <option value ="1">1200</option>
                            <option value ="2">2400</option>
                            <option value ="3">4800</option>
                            <option value ="4">9600</option>
                            <option value ="5" selected>19200</option>
                        </select>
                    </td>
                </tr>
            </table>
        </form>
    </div>
    
    <!-- 添加控制任务对话框的按钮 -->
    <div id="dlgButtons_AddArchive">
        <a href="javascript:submit_AddArchive()" id="addControlTask_ok" class="easyui-linkbutton" iconCls="icon-ok">确定</a>
        <a href="javascript:cancel_AddArchive()" class="easyui-linkbutton" iconCls="icon-cancel">取消</a>
    </div>
</body>
</html>