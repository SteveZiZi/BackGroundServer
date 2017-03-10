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

	// 表格刷新函数
	function Refresh()
	{
	    // 刷新控制任务列表
	    $("#tbcontrolTaskInfo").datagrid("reload");
	}
	// 定时刷新
	setInterval("Refresh()", 10000);

	/**
	 * “动作”单元格的格式化器
	 * value    字段值
	 * row      行记录数据
	 * index    行索引
	 */
	function formatter_action(value, row, index)
    {
	    var s = '<a href="#" onclick="showTaskDetail(this)">详细信息</a>&nbsp;&nbsp;&nbsp;&nbsp;';
	    var c = '<a href="#" onclick="deleteTask(this)">删除任务</a>';
	    return s + c;
    }
	
    /**
    * 显示任务详细信息
    *
    * target    行对象
    *
    */
    function showTaskDetail(target)
    {
    	// 重置添加任务对话框
        resetDialog_AddControlTask();
    	
        // 禁能控件
        $('#controlTask_Id').numberspinner('disable');
        
    	// 获得任务Id
        var tr = $(target).closest('tr.datagrid-row');
        var taskId = tr.children('td[field="taskId"]').text();
    	
    	// 发送添加任务请求
        $.ajax(
        {
            type:"post",
            url:"controlTaskInfo",
            data:
            {
                logicAddress:"${param.logicAddress}",
                taskId:taskId
            },
            dataType:"json",
            success:function(data, textStatus)
            {
            	// 任务使能
                $("#controlTask_Enable").prop("checked", data.isEnable);
            	// 任务Id
            	$('#controlTask_Id').numberspinner('setValue', data.id);
            	// 优先级
            	$('#controlTask_Priority').combobox('setValue', data.priority);
            	// 开始时间
            	$('#controlTask_StartTime').datetimebox('setValue', data.startTime);
            	// 结束时间
            	$('#controlTask_EndTime').datetimebox('setValue', data.endTime);
            	// 执行周期明细
            	var $iCycleScale = data.cycleScale;
            	for(var i = 0; i < 7; i++)
            	{
            		if(0 != ($iCycleScale & (1 << i)) )
            		{
            			$("#controlTask_CycleMode" + i).prop("checked", true);
            		}
            	}
            	
            	// 回路选择
            	var $iCircuit = data.selectedCircuit;
            	for(var i = 0; i < 16; i++)
                {
                    if(0 != ($iCircuit & (1 << i)) )
                    {
                        $("#controlTask_CircuitSelect" + (i + 1)).prop("checked", true);
                    }
                }
            	
            	// 方案列表
            	var $planList = data.planList;
            	$.each($planList, function(i, item)
            	{
            		// 添加一行记录
            		addRow('tbTaskItem');
            		
            		// 开始时间
            		var iRowIdx = $("#tbTaskItem tr").length - 1;
            		$("#taskItem_StartUpTime" + iRowIdx).timespinner('setValue', item.startTime);
            		
            		// 动作
            		$("#taskItem_action" + iRowIdx).combobox('setValue', item.brightness);
            	});
            	
            	// 弹出详细信息对话框
                $("#dlg_AddControlTask").dialog("open").dialog("setTitle", "详细信息");
            },
            error:function(XMLHttpRequest, textStatus, errorThrown)
            {
            	$.messager.alert('提示', '操作失败!', 'error');
            	
            	// 重新加载任务列表
                $("#tbcontrolTaskInfo").datagrid("reload");
            }
        });
    }

    // 删除任务
    function deleteTask(target)
    {
    	$.messager.confirm('提示', '确定要删除吗?', function(bFlag)
        {
    	    if (bFlag)
    	    {
    	    	// 获得任务Id
    	        var tr = $(target).closest('tr.datagrid-row');
    	        var taskId = tr.children('td[field="taskId"]').text();
    	        
    	        // 发送添加任务请求
    	        $.ajax(
    	        {
    	            type:"post",
    	            url:"deleteControlTasks",
    	            data:
    	            {
    	            	logicAddress:"${param.logicAddress}",
    	                taskIds:taskId
    	            },
    	            dataType:"text",
    	            success:function(data, textStatus)
    	            {
    	                if($.trim(data) == "success")
    	                {
    	                    // 重新加载任务列表
    	                    $("#tbcontrolTaskInfo").datagrid("reload");
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

    // 添加任务
    function openDialog_AddControlTask()
    {
    	// 检查已存在的任务数
    	var iRows = $('#tbcontrolTaskInfo').datagrid("getRows").length;
    	if(iRows >= 12)
    	{
    		$.messager.alert('提示', '最多只能有12个程控任务！', 'warning');
    		return;
    	}

    	// 重置添加任务对话框
        resetDialog_AddControlTask();
    	
    	// 使能控件
        $("#addControlTask_ok").linkbutton('enable');
    	
    	// 任务使能
    	$("#controlTask_Enable").prop("checked", true);
    	
        // 弹出添加任务对话框
    	$("#dlg_AddControlTask").dialog("open").dialog("setTitle", "添加任务");
    }
    
    // 确定按钮（添加任务）
    function submit_AddControlTask()
    {
    	// 获得任务使能checkbox的值
    	var strEnable = "false";
        $("input[name='controlTask_Enable']:checked").each(function()
        {
        	strEnable = "true"
    	});
        
        // 获得回路选择checkbox的值
        var strCircuit = "";
        $("input[name='controlTask_CircuitSelect']:checked").each(function()
        {
        	if(strCircuit.length != 0)
        	{
        		strCircuit += ",";
        	}
        	
        	strCircuit += $(this).val();
        });
        
        // 检查回路字符串
        if(strCircuit.length == 0)
        {
        	$.messager.alert('提示', '请至少选择一条回路！', 'warning');
            return;	
       	}
        
        // 获得循环方式checkbox的值
        var strCycleMode = "";
        $("input[name='controlTask_CycleMode']:checked").each(function()
        {
        	if(strCycleMode.length != 0)
            {
        		strCycleMode += ",";
            }
        	
        	strCycleMode += $(this).val();
        });
        
        // 检查循环方式字符串
        if(strCycleMode.length == 0)
        {
            $.messager.alert('提示', '请选择循环方式！', 'warning');
            return; 
        }
        
        // 获得所有的任务项的开始时间
        var strtaskItemStartupTimes = "";
        $("#tbTaskItem tr").find("td:eq(0)").each(function()
        {
        	if(strtaskItemStartupTimes.length != 0)
            {
        		strtaskItemStartupTimes += ",";
            }
        	
        	strtaskItemStartupTimes += $(this).find("span .textbox-value").val();
        });
        
        // 获得所有的任务项的动作
        var strtaskItemActions = "";
        $("#tbTaskItem tr").find("td:eq(1)").each(function()
        {
        	if(strtaskItemActions.length != 0)
            {
        		strtaskItemActions += ",";
            }
        	
        	strtaskItemActions += $(this).find("span .textbox-value").val();
        });
        
        // 检查任务动作字符串
        if((strtaskItemStartupTimes.length == 0) || (strtaskItemActions.length == 0))
        {
            $.messager.alert('提示', '请添加任务动作！', 'warning');
            return; 
        }
    	
        // 发送添加任务请求
    	$.ajax(
    	{
    	    type:"post",
    	    url:"addControlTask",
    	    data:
    	    {
    	    	logicAddress:"${param.logicAddress}",
    	    	taskEnable:strEnable,
    	        taskId:$('#controlTask_Id').val(),
    	        taskPriority:$('#controlTask_Priority').combobox('getValue'),
    	    	taskStartTime:$('#controlTask_StartTime').datetimebox('getValue'),
    	    	taskEndTime:$('#controlTask_EndTime').datetimebox('getValue'),
    	    	taskCircuitSelect:strCircuit,
    	    	taskCycleMode:strCycleMode,
    	    	taskItemStartupTimes:strtaskItemStartupTimes,
    	    	taskItemActions:strtaskItemActions
    	    },
    	    dataType:"text",
    	    beforeSend:function(XMLHttpRequest)
    	    {
    	    	// 检查任务ID是否为空
    	    	if($('#controlTask_Id').val().length == 0)
    	    	{
    	    		$.messager.alert('提示', '请输入任务ID！', 'warning');
    	    		return false;
    	    	}
    	    },
    	    success:function(data, textStatus)
    	    {
    	    	if($.trim(data) == "success")
    	    	{
    	    		// 关闭对话框
    	    		$("#dlg_AddControlTask").dialog("close");
    	    		
    	    		// 重新加载任务列表
                    $("#tbcontrolTaskInfo").datagrid("reload");
    	        }
    	        else
    	        {
    	        	$.messager.alert('提示', '添加失败!', 'error');
    	    	}
    	    }
    	});
    }
    
    /**
    * 重置添加任务对话框 
    *
    */
    function resetDialog_AddControlTask()
    {
        $(":checkbox").prop("checked", false);
        $("#tbTaskItem tr:gt(0)").remove();
        
        // 使能控件
        $('#controlTask_Id').numberspinner('enable');
    }
    
    // 取消按钮（添加任务）
    function cancel_AddControlTask()
    {
        // 关闭对话框
        $("#dlg_AddControlTask").dialog("close");
    }

    /**
    * 为table添加一行
    *
    * tbId  表格ID
    *
    */
    function addRow(tbId)
    {
    	// 获得表格对象，并检查其行数
    	var len = $("#" + tbId + " tr").length;
    	if((0 == len) || (len > 12))
    	{
    		return;
    	}
    	
    	// 合成创建行的字符串
    	var trHtml = "<tr id=" + len + ">"
    	+ "<td align='center'>"
    	+ "<input id='taskItem_StartUpTime" + len + "' style='width:80px;'>"
    	+ "</td>"
    	+ "<td align='center'>"
    	+ "<input id='taskItem_action" + len + "' style='width:80px;' editable='false'>"
    	+ "</td>"
    	+ "<td align='center'>"
    	+ "<a href=\"javascript:deleteRow('" + tbId + "', " + len + ")\">删除动作</a>"
    	+ "</td>"
    	+ "</tr>";

    	// 向表格添加一行
    	$("#" + tbId).append(trHtml);
    	
    	// 为单元格动态创建timespinner控件
    	$("#taskItem_StartUpTime" + len).timespinner({  
            value: '00:00',
            required: true
        });
    	
    	// 为单元格动态创建combobox控件
    	$("#taskItem_action" + len).combobox({
    		valueField: 'value',
            textField: 'label',
    	    data: [{label:'100%', value:'100', "selected":true}
    	    , {label:'90%', value:'90'}
    	    , {label:'80%', value:'80'}
    	    , {label:'70%', value:'70'}
    	    , {label:'60%', value:'60'}
    	    , {label:'50%', value:'50'}
    	    , {label:'40%', value:'40'}
    	    , {label:'30%', value:'30'}
    	    , {label:'20%', value:'20'}
    	    , {label:'10%', value:'10'}
    	    , {label:'0%', value:'0'}
    	    , {label:'OFF', value:'-1'}]
    	});
    }
    
    /**
     * 删除表格的指定行
     *
     * tbId  表格ID
     * rowId 行ID
     *
     */
    function deleteRow(tbId, rowId)
    {
    	// 删除指定Id的行
    	$("#" + tbId + " tr[id='" + rowId + "']").remove();
    }
    
</script>
</head>
<body>
	<div id="ct" class="easyui-layout" data-options="fit:true">
	    <div data-options="region:'west',split:true,hideCollapsedContent:false" style="width:100px">
            <a href="getPage_runPage?logicAddress=${param.logicAddress}&deviceID=${param.deviceID}" class="easyui-linkbutton" style="width:100%" 
            data-options="toggle:true,group:'g_buttons',iconCls:'icon-run',size:'large',iconAlign:'top'">运行</a>
            <a href="getPage_controlPage?logicAddress=${param.logicAddress}&deviceID=${param.deviceID}" class="easyui-linkbutton" style="width:100%" 
            data-options="toggle:true,group:'g_buttons',selected:true,iconCls:'icon-control',size:'large',iconAlign:'top'">程控</a>
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
	    
	        <!-- 任务列表 -->
	        <table id="tbcontrolTaskInfo" title="任务列表" class="easyui-datagrid" fitColumns="true" loadMsg="" striped=true 
                pagination="true" singleSelect="true" rownumbers="true" toolbar="#toolbar_ControlTask" pageSize="20"
                url="controlTaskList?logicAddress=${param.logicAddress}">
                <thead>
                    <tr>
                        <th field="taskId" width="50">任务ID</th>
                        <th field="taskPriority" width="50">优先级</th>
                        <th field="taskStartDate" width="50">开始日期</th>
                        <th field="taskEndDate" width="50">结束日期</th>
                        <th field="taskAction" width="50" formatter="formatter_action" align="center">操作</th>
                    </tr>
                </thead>
            </table>
	    </div>
	</div>
	
	<!-- 任务列表的工具栏 -->
    <div id="toolbar_ControlTask">
        <div>
            <a href="javascript:openDialog_AddControlTask()" class="easyui-linkbutton" iconCls="icon-add">添加</a>
        </div>
    </div>
    
    <!-- 添加控制任务对话框 -->
    <div id="dlg_AddControlTask" class="easyui-dialog" style="width:600px;height:430px;padding:5px;" closed="true" buttons="#dlgButtons_AddControlTask">
		<div class="easyui-layout" data-options="fit:true">
		    <div data-options="region:'west',split:true" style="width:300px;padding:5px;">
		        <table id="tbTaskParam">
	                <tr>
	                    <td>任务使能：</td>
	                    <td><input type="checkbox" id="controlTask_Enable" name="controlTask_Enable" checked="checked" value="true" />使能</td>
	                </tr>
	                <tr>
	                    <td>任&nbsp;&nbsp;务&nbsp;ID：</td>
	                    <td><input id="controlTask_Id" class="easyui-numberspinner" value="1" required="true" data-options="min:1,max:12" /></td>
	                </tr>
	                <tr>
	                    <td>优&nbsp;&nbsp;先&nbsp;级：</td>
	                    <td>
	                        <select class="easyui-combobox" id="controlTask_Priority" editable="false" style="width:148px;">
	                            <option value="0">低</option>
	                            <option value="1">中</option>
	                            <option value="2">高</option>
	                        </select>
	                    </td>
	                </tr>
	                <tr>
	                    <td>开始时间：</td>
	                    <td><input id="controlTask_StartTime" class="easyui-datetimebox" 
	                     required="true" value="2000-01-01 00:00:00" /></td>
	                </tr>
	                <tr>
	                    <td>结束时间：</td>
	                    <td><input id="controlTask_EndTime" class="easyui-datetimebox" 
	                     required="true" value="2050-12-31 23:59:59" /></td>
	                </tr>
	                <tr>
	                    <td>回路选择：</td>
	                    <td>
	                        <input type="checkbox" id="controlTask_CircuitSelect1" name="controlTask_CircuitSelect" value="1" />L#${param.deviceID >= 10? "": "0"}${param.deviceID}01
	                        &nbsp;&nbsp;&nbsp;&nbsp;
	                        <input type="checkbox" id="controlTask_CircuitSelect2" name="controlTask_CircuitSelect" value="2" />L#${param.deviceID >= 10? "": "0"}${param.deviceID}02
	                        <br/>
	                        <input type="checkbox" id="controlTask_CircuitSelect3" name="controlTask_CircuitSelect" value="3" />L#${param.deviceID >= 10? "": "0"}${param.deviceID}03
	                        &nbsp;&nbsp;&nbsp;&nbsp;
	                        <input type="checkbox" id="controlTask_CircuitSelect4" name="controlTask_CircuitSelect" value="4" />L#${param.deviceID >= 10? "": "0"}${param.deviceID}04
	                        <br/>
	                        <input type="checkbox" id="controlTask_CircuitSelect5" name="controlTask_CircuitSelect" value="5" />L#${param.deviceID >= 10? "": "0"}${param.deviceID}05
	                        &nbsp;&nbsp;&nbsp;&nbsp;
	                        <input type="checkbox" id="controlTask_CircuitSelect6" name="controlTask_CircuitSelect" value="6" />L#${param.deviceID >= 10? "": "0"}${param.deviceID}06
	                        <br/>
	                        <input type="checkbox" id="controlTask_CircuitSelect7" name="controlTask_CircuitSelect" value="7" />L#${param.deviceID >= 10? "": "0"}${param.deviceID}07
	                        &nbsp;&nbsp;&nbsp;&nbsp;
	                        <input type="checkbox" id="controlTask_CircuitSelect8" name="controlTask_CircuitSelect" value="8" />L#${param.deviceID >= 10? "": "0"}${param.deviceID}08
	                        <br/>
	                        <input type="checkbox" id="controlTask_CircuitSelect9" name="controlTask_CircuitSelect" value="9" />L#${param.deviceID >= 10? "": "0"}${param.deviceID}09
	                        &nbsp;&nbsp;&nbsp;&nbsp;
	                        <input type="checkbox" id="controlTask_CircuitSelect10" name="controlTask_CircuitSelect" value="10" />L#${param.deviceID >= 10? "": "0"}${param.deviceID}10
	                        <br/>
	                        <input type="checkbox" id="controlTask_CircuitSelect11" name="controlTask_CircuitSelect" value="11" />L#${param.deviceID >= 10? "": "0"}${param.deviceID}11
	                        &nbsp;&nbsp;&nbsp;&nbsp;
	                        <input type="checkbox" id="controlTask_CircuitSelect12" name="controlTask_CircuitSelect" value="12" />L#${param.deviceID >= 10? "": "0"}${param.deviceID}12
	                        <br/>
	                        <input type="checkbox" id="controlTask_CircuitSelect13" name="controlTask_CircuitSelect" value="13" />L#${param.deviceID >= 10? "": "0"}${param.deviceID}13
	                        &nbsp;&nbsp;&nbsp;&nbsp;
	                        <input type="checkbox" id="controlTask_CircuitSelect14" name="controlTask_CircuitSelect" value="14" />L#${param.deviceID >= 10? "": "0"}${param.deviceID}14
	                        <br/>
	                        <input type="checkbox" id="controlTask_CircuitSelect15" name="controlTask_CircuitSelect" value="15" />L#${param.deviceID >= 10? "": "0"}${param.deviceID}15
	                        &nbsp;&nbsp;&nbsp;&nbsp;
	                        <input type="checkbox" id="controlTask_CircuitSelect16" name="controlTask_CircuitSelect" value="16" />L#${param.deviceID >= 10? "": "0"}${param.deviceID}16
	                    </td>
	                </tr>
	                <tr>
	                    <td>循环方式：</td>
	                    <td>
	                        <input type="checkbox" id="controlTask_CycleMode1" name="controlTask_CycleMode" value="1" />周一
	                        <input type="checkbox" id="controlTask_CycleMode2" name="controlTask_CycleMode" value="2" />周二
	                        <input type="checkbox" id="controlTask_CycleMode3" name="controlTask_CycleMode" value="3" />周三
	                        <input type="checkbox" id="controlTask_CycleMode4" name="controlTask_CycleMode" value="4" />周四
	                        <br/>
	                        <input type="checkbox" id="controlTask_CycleMode5" name="controlTask_CycleMode" value="5" />周五
	                        <input type="checkbox" id="controlTask_CycleMode6" name="controlTask_CycleMode" value="6" />周六
	                        <input type="checkbox" id="controlTask_CycleMode0" name="controlTask_CycleMode" value="7" />周日
	                    </td>
	                </tr>
                </table>
		    </div>
		    <div data-options="region:'center'" style="padding:5px;">
		        <table id="tbTaskItem" title="控制指令" border="1" style="BORDER-COLLAPSE:collapse" bordercolor="#6DA3ED">
	                <thead>
	                    <tr>
	                        <th width="100px" align="center">时间</th>
	                        <th width="100px" align="center">动作</th>
	                        <th width="100px" align="center"><a href="javascript:addRow('tbTaskItem')">添加动作</a></th>
	                    </tr>
	                </thead>
                </table>
		    </div>
		</div>
    </div>
    
    <!-- 添加控制任务对话框的按钮 -->
    <div id="dlgButtons_AddControlTask">
        <a href="javascript:submit_AddControlTask()" id="addControlTask_ok" class="easyui-linkbutton" iconCls="icon-ok">确定</a>
        <a href="javascript:cancel_AddControlTask()" class="easyui-linkbutton" iconCls="icon-cancel">取消</a>
    </div>
</body>
</html>