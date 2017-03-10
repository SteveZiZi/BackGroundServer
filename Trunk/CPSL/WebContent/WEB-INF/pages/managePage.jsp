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

    // 文档初始化完毕，设置控件值
	$(function(){
		
		// 设置当前时间
        var curr_time = new Date();
        var strDate = curr_time.getFullYear()+"-";
        strDate += curr_time.getMonth()+1+"-";
        strDate += curr_time.getDate()+"-";
        strDate += curr_time.getHours()+":";
        strDate += curr_time.getMinutes()+":";
        strDate += curr_time.getSeconds();
        $('#newDatetime').datetimebox('setValue', strDate);
        
		// 读取管理员电话号码
        $.ajax(
        {
            type:"post",
            url:"getAdminPhone",
            data:
            {
            	logicAddress:"${param.logicAddress}"
            },
            dataType:"json",
            success:function(data, textStatus)
            {
                // 管理员电话
                $('#newAdminPhone').textbox('setValue', data.adminPhone);
            }
        });
	});

    // 手机号验证
    $.extend($.fn.validatebox.defaults.rules, {
    	// 验证手机号
        checkPhone:{
            validator: function(value)
            {
                var rex = /^1[3-8]+\d{9}$/;
                return rex.test(value);
            },
            message: '请输入11位手机号码'
        },
        // 验证IP地址
        checkIp : {
            validator : function(value) 
            {  
                var reg = /^((1?\d?\d|(2([0-4]\d|5[0-5])))\.){3}(1?\d?\d|(2([0-4]\d|5[0-5])))$/ ;  
                return reg.test(value);  
            },  
            message : 'IP地址不正确'  
        }
    });
    
    // 校时
    function timing()
    {
    	$.ajax(
    	{
    		type:"post",
    		url:"setDeviceTime",
    		data:
    		{
    			logicAddress:"${param.logicAddress}",
                newDatetime:$('#newDatetime').datetimebox('getValue')
            },
    		dataType:"text",
    		beforeSend:function(XMLHttpRequest)
    		{
    			if($('#newDatetime').datetimebox('getValue').length == 0)
    			{
    				$.messager.alert('提示', '请输入时间！', 'warning');
    				return false;
    			}
    		},
    		success:function(data, textStatus)
    		{
    			if($.trim(data) == "success")
    			{
                    $.messager.alert('提示', '校时成功!', 'info');
                }
                else
                {
                    $.messager.alert('提示', '校时失败!', 'error');
                }
    		}
    	});
    }
    
    // 设置手机号
    function setAdminPhone()
    {
        $.ajax(
        {
            type:"post",
            url:"modifyAdminPhone",
            data:
            {
            	logicAddress:"${param.logicAddress}",
                adminPhone:$('#newAdminPhone').val()
            },
            dataType:"text",
            beforeSend:function(XMLHttpRequest)
            {
                var rex = /^1[3-8]+\d{9}$/;
                if(!rex.test($('#newAdminPhone').val()))
                {
                	$.messager.alert('提示', '请输入正确的手机号码！', 'warning');
                    return false;
                }
            },
            success:function(data, textStatus)
            {
                if($.trim(data) == "success")
                {
                	$.messager.alert('提示', '设置成功!', 'info');
                }
                else
                {
                	$.messager.alert('提示', '设置失败!', 'error');
                }
            }
        });
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
            data-options="toggle:true,group:'g_buttons',selected:true,iconCls:'icon-manage',size:'large',iconAlign:'top'">管理</a>
            <a href="getPage_configPage?logicAddress=${param.logicAddress}&deviceID=${param.deviceID}" class="easyui-linkbutton" style="width:100%" 
            data-options="toggle:true,group:'g_buttons',iconCls:'icon-system',size:'large',iconAlign:'top'">配置</a>
            <a href="getPage_archivePage?logicAddress=${param.logicAddress}&deviceID=${param.deviceID}" class="easyui-linkbutton" style="width:100%" 
            data-options="toggle:true,group:'g_buttons',iconCls:'icon-archive',size:'large',iconAlign:'top'">设备</a>
        </div>
	    <div data-options="region:'center'" style="background:#EEEFF4">
	        <div style="height:100%; background:url(images/managePage.jpg) no-repeat">
		        <table>
			        <tr height="260">
		                <td colspan="4"></td>
		            </tr>
	                <tr height="30">
	                    <td width="550"></td>
	                    <td>校&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;时：</td>
	                    <td><input id="newDatetime" class="easyui-datetimebox" /></td>
	                    <td><a href="javascript:timing()" class="easyui-linkbutton" style="width:80px">校时</a></td>
	                </tr>
	                <tr height="30">
	                    <td></td>
	                    <td>管理员电话：</td>
	                    <td><input id="newAdminPhone" class="easyui-textbox" required="true" data-options="validType:'checkPhone'" /></td>
	                    <td><a href="javascript:setAdminPhone()" class="easyui-linkbutton" style="width:80px">设置</a></td>
	                </tr>
	                <tr height="30">
	                    <td></td>
	                    <td>终端逻辑地址：</td>
	                    <td>${param.logicAddress}</td>
	                    <td></td>
	                </tr>
	                <tr height="30">
	                    <td></td>
	                    <td>软&nbsp;件&nbsp;版&nbsp;本：</td>
	                    <td>V1.1</td>
	                    <td></td>
	                </tr>
	            </table>
            </div>
	    </div>
	</div>
</body>
</html>