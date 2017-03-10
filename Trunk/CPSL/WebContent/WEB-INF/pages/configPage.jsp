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

	/**
	* “动作”单元格的格式化器
	* value    字段值
	* row      行记录数据
	* index    行索引
	*/
	function formatter_action(value, row, index)
	{
        if (row.editing)
        {
            var s = '<a href="#" onclick="saveRow(this)">保存</a> ';
            var c = '<a href="#" onclick="cancelRow(this)">取消</a>';
            return s + c;
        } 
        else 
        {
            var e = '<a href="#" onclick="editRow(this)">编辑</a>';
            return e;
        }
    }
	
	/**
	* 在一行进入编辑模式的时候触发
	* index：编辑行的索引，索引从0开始。
	* row：对应于编辑行的记录。
	*/
	function onBeforeEdit(index, row)
	{
		$("#tbDimVdbInfo").datagrid("selectRow", index);
        row.editing = true;
        updateActions(index);
    }
	
	/**
	* 在用户完成编辑一行的时候触发
	* index：编辑行的索引，索引从0开始。
	* row：对应于编辑行的记录。
	*/
    function onAfterEdit(index, row)
    {
        row.editing = false;
        updateActions(index);
    }
    
    /**
     * 在用户取消编辑一行的时候触发
     * index：编辑行的索引，索引从0开始。
     * row：对应于编辑行的记录。
     */
    function onCancelEdit(index, row)
    {
        row.editing = false;
        updateActions(index);
    }
    
    /**
     * 更新指定行
     * index：执行更新操作行的索引，索引从0开始。
     * row：对应于编辑行的记录。
     */
    function updateActions(index)
    {
        $('#tbDimVdbInfo').datagrid('updateRow',
        {
            index: index,
            row:{}
        });
    }
    
    /**
     * 获得指定行的索引号
     * target：行对象。
     */
    function getRowIndex(target)
    {
        var tr = $(target).closest('tr.datagrid-row');
        return parseInt(tr.attr('datagrid-row-index'));
    }
    
    /**
     * 编辑指定的行
     * target：行对象。
     */
    function editRow(target)
    {
        $('#tbDimVdbInfo').datagrid('beginEdit', getRowIndex(target));
    }
    
    /**
     * 删除指定的行
     * target：行对象。
     */
    function deleteRow(target)
    {
        $.messager.confirm('Confirm','Are you sure?',function(r)
        {
            if (r)
            {
                $('#tbDimVdbInfo').datagrid('deleteRow', getRowIndex(target));
            }
        });
    }
    
    /**
     * 保存指定的行
     * target：行对象。
     */
    function saveRow(target)
    {
    	var        ed;
    	
    	
    	// 获得回路名
    	var tr = $(target).closest('tr.datagrid-row');
    	var ccName = tr.children('td[field="closedCircuitName"]').text();
    	
    	// 获得100%对应的电压值
    	ed = $('#tbDimVdbInfo').datagrid('getEditor', {index:getRowIndex(target), field:'percent100'});
        var percent100 = $(ed.target).numberbox('getText');
        
        // 获得90%对应的电压值
        ed = $('#tbDimVdbInfo').datagrid('getEditor', {index:getRowIndex(target), field:'percent90'});
        var percent90 = $(ed.target).numberbox('getText');
        
        // 获得80%对应的电压值
        ed = $('#tbDimVdbInfo').datagrid('getEditor', {index:getRowIndex(target), field:'percent80'});
        var percent80 = $(ed.target).numberbox('getText');
        
        // 获得70%对应的电压值
        ed = $('#tbDimVdbInfo').datagrid('getEditor', {index:getRowIndex(target), field:'percent70'});
        var percent70 = $(ed.target).numberbox('getText');
        
        // 获得60%对应的电压值
        ed = $('#tbDimVdbInfo').datagrid('getEditor', {index:getRowIndex(target), field:'percent60'});
        var percent60 = $(ed.target).numberbox('getText');
        
        // 获得50%对应的电压值
        ed = $('#tbDimVdbInfo').datagrid('getEditor', {index:getRowIndex(target), field:'percent50'});
        var percent50 = $(ed.target).numberbox('getText');
        
        // 获得40%对应的电压值
        ed = $('#tbDimVdbInfo').datagrid('getEditor', {index:getRowIndex(target), field:'percent40'});
        var percent40 = $(ed.target).numberbox('getText');
        
        // 获得30%对应的电压值
        ed = $('#tbDimVdbInfo').datagrid('getEditor', {index:getRowIndex(target), field:'percent30'});
        var percent30 = $(ed.target).numberbox('getText');
        
        // 获得20%对应的电压值
        ed = $('#tbDimVdbInfo').datagrid('getEditor', {index:getRowIndex(target), field:'percent20'});
        var percent20 = $(ed.target).numberbox('getText');
        
        // 获得10%对应的电压值
        ed = $('#tbDimVdbInfo').datagrid('getEditor', {index:getRowIndex(target), field:'percent10'});
        var percent10 = $(ed.target).numberbox('getText');
        
        // 获得0%对应的电压值
        ed = $('#tbDimVdbInfo').datagrid('getEditor', {index:getRowIndex(target), field:'percent0'});
        var percent0 = $(ed.target).numberbox('getText');
        
        // 保存回路Dim-Vdb配置
        $.ajax(
        {
        	type:"post",
        	url:"setClosedCircuitDimVdb",
        	data:
        	{
        		logicAddress:"${param.logicAddress}",
        		closedCircuitName:ccName,
        		percent100:percent100,
        		percent90:percent90,
        		percent80:percent80,
        		percent70:percent70,
        		percent60:percent60,
        		percent50:percent50,
        		percent40:percent40,
        		percent30:percent30,
        		percent20:percent20,
        		percent10:percent10,
        		percent0:percent0
        	},
        	dataType:"text",
            success:function(data, textStatus)
            {
            	if($.trim(data) == "success")
                {
            		$('#tbDimVdbInfo').datagrid('endEdit', getRowIndex(target));
                }
                else
                {
                	$.messager.alert('提示', '保存失败!', 'error');
                }
            }
        });
    }
    
    /**
     * 取消编辑指定的行
     * target：行对象。
     */
    function cancelRow(target)
    {
        $('#tbDimVdbInfo').datagrid('cancelEdit', getRowIndex(target));
    }
	
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
            data-options="toggle:true,group:'g_buttons',selected:true,iconCls:'icon-system',size:'large',iconAlign:'top'">配置</a>
            <a href="getPage_archivePage?logicAddress=${param.logicAddress}&deviceID=${param.deviceID}" class="easyui-linkbutton" style="width:100%" 
            data-options="toggle:true,group:'g_buttons',iconCls:'icon-archive',size:'large',iconAlign:'top'">设备</a>
        </div>
	    <div data-options="region:'center'">
	    
	        <!-- 告警日志表 -->
            <table id="tbDimVdbInfo" title="Dim-Vdb" class="easyui-datagrid" fitColumns="true" pagination="true" pageSize="20"
            rownumbers="true" data-options="iconCls:'icon-edit', singleSelect:true, onBeforeEdit:onBeforeEdit
            , onAfterEdit:onAfterEdit, onCancelEdit:onCancelEdit, url:'dimVdbList?logicAddress=${param.logicAddress}'">
                <thead>
                    <tr>
                        <th field="closedCircuitName" width="50">L-V</th>
                        <th field="percent100" width="50" data-options="editor:{type:'numberbox', options:{required:true, min:0, max:999}}">100%</th>
                        <th field="percent90" width="50" data-options="editor:{type:'numberbox', options:{required:true, min:0, max:999}}">90%</th>
                        <th field="percent80" width="50" data-options="editor:{type:'numberbox', options:{required:true, min:0, max:999}}">80%</th>
                        <th field="percent70" width="50" data-options="editor:{type:'numberbox', options:{required:true, min:0, max:999}}">70%</th>
                        <th field="percent60" width="50" data-options="editor:{type:'numberbox', options:{required:true, min:0, max:999}}">60%</th>
                        <th field="percent50" width="50" data-options="editor:{type:'numberbox', options:{required:true, min:0, max:999}}">50%</th>
                        <th field="percent40" width="50" data-options="editor:{type:'numberbox', options:{required:true, min:0, max:999}}">40%</th>
                        <th field="percent30" width="50" data-options="editor:{type:'numberbox', options:{required:true, min:0, max:999}}">30%</th>
                        <th field="percent20" width="50" data-options="editor:{type:'numberbox', options:{required:true, min:0, max:999}}">20%</th>
                        <th field="percent10" width="50" data-options="editor:{type:'numberbox', options:{required:true, min:0, max:999}}">10%</th>
                        <th field="percent0" width="50" data-options="editor:{type:'numberbox', options:{required:true, min:0, max:999}}">0%</th>
                        <th field="action" width="50" formatter="formatter_action" align="center">操作</th>
                    </tr>
                </thead>
            </table>
	    </div>
	</div>
</body>
</html>