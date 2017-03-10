<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>直流集中供电照明系统主界面</title>
<link rel="stylesheet" type="text/css" href="jquery-easyui-1.4.3/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="jquery-easyui-1.4.3/themes/icon.css">
<script type="text/javascript" src="jquery-easyui-1.4.3/jquery.min.js"></script>
<script type="text/javascript" src="jquery-easyui-1.4.3/jquery.easyui.min.js"></script>
<script type="text/javascript" src="jquery-easyui-1.4.3/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript">

    $(function(){
		// 初始化树形控件
		refreshDeviceTree();
    });
    // 定时刷新
    setInterval("refreshDeviceTree()", 10000);
    
    /**
    * 刷新树形控件 
    *
    */
    function refreshDeviceTree()
    {
    	// 初始化树形控件
        $("#deviceTree").tree(
        {
            url: 'listDevice',
            method: 'GET',
            animate: true,
            lines: true,
            onClick: function(node)
            {
                if(node.url)
                {
                    openTab(node.text,node.url);
                }
            },
            onContextMenu: function(e, node)
            {
                e.preventDefault();
                $(this).tree('select', node.target);
                $('#menuTree').menu('show', {left: e.pageX, top: e.pageY});
            }
        });
    }

	// 新增Tab
	function openTab(text,url)
	{
		if($("#tabs").tabs('exists', text))
		{
			$("#tabs").tabs('select', text);
		}
		else
		{
			var content="<iframe frameborder='0' scrolling='auto' style='width:100%;height:100%' src=" + url + "></iframe>";
			$("#tabs").tabs('add',
			{
				title:text,
				closable:true,
				content:content
			});
		}
	}
	
	// 添加树形节点
	function addDevice()
	{
		/*
        var t = $('#deviceTree');
        var node = t.tree('getSelected');
        t.tree('append', 
        {
            parent: (node? node.target: null),
            data: [{text: 'new item1'}]
        });
        */
		$.messager.alert('提示', '建设中...', 'info');
    }
	
	// 删除树形节点
    function deleteDevice()
    {
		/*
        var node = $('#deviceTree').tree('getSelected');
        $('#deviceTree').tree('remove', node.target);
        */
    	$.messager.alert('提示', '建设中...', 'info');
    }
	
	// 添加项目
	function addProject()
	{
		$.messager.alert('提示', '建设中...', 'info');
	}
		
	// 展开树形节点
	function expand()
    {
        var node = $('#deviceTree').tree('getSelected');
        $('#deviceTree').tree('expand', node.target);
    }
		
	// 收缩树形节点
	function collapse()
	{
        var node = $('#deviceTree').tree('getSelected');
        $('#deviceTree').tree('collapse', node.target);
    }
	
	/**
	 * 打开修改密码对话框
	 *
	 */
	function openDialog_ChangePassword()
	{
		// 重置密码控件
		$('#password_old').textbox('setValue', "");
		$('#password_new1').textbox('setValue', "");
		$('#password_new2').textbox('setValue', "");
		
		// 弹出修改密码对话框
        $("#dlg_ChangePassword").dialog("open").dialog("setTitle", "修改密码");
	}
	
	/**
     * 确定修改密码
     *
     */
	function submit_ChangePassword()
	{
		// 检查原密码
		var strOldPassword = $('#password_old').val();
		if(strOldPassword.length == 0)
		{
			$.messager.alert('提示', '请输入原密码！', 'warning');
            return;
		}
		
		// 检查新密码
		var strNewPassword1 = $('#password_new1').val();
        if(strNewPassword1.length == 0)
        {
            $.messager.alert('提示', '请输入新密码！', 'warning');
            return;
        }
        
        // 检查重复新密码
        var strNewPassword2 = $('#password_new2').val();
        if(strNewPassword2.length == 0)
        {
            $.messager.alert('提示', '请输入重复新密码！', 'warning');
            return;
        }
        
        // 比较新密码、重复新密码是否相同
        if(strNewPassword1 != strNewPassword2)
        {
        	$.messager.alert('提示', '新密码与重复新密码不一致！', 'warning');
            return;
       	}
        
        // 比较原密码、新密码是否相同
        if(strOldPassword == strNewPassword1)
        {
        	$.messager.alert('提示', '原密码与新密码相同！', 'warning');
        	return;
        }
        
        // 发送修改密码请求
        $.ajax(
        {
            type:"post",
            url:"changePassword",
            data:
            {
                userName:"${currentUser.userName}",
                oldPassword:strOldPassword,
                newPassword:strNewPassword1
            },
            dataType:"json",
            success:function(data, textStatus)
            {
                if(data.isSuccess == "true")
                {
                    // 关闭对话框
                    $("#dlg_ChangePassword").dialog("close");
                    $.messager.alert('提示', '修改密码成功!', 'info');
                }
                else
                {
                    $.messager.alert('提示', data.errorMsg, 'error');
                }
            }
        });
	}
	
	/**
     * 取消修改密码
     *
     */
	function cancel_ChangePassword()
	{
		// 关闭对话框
        $("#dlg_ChangePassword").dialog("close");
	}

</script>
</head>
<body class="easyui-layout">
	<div region="north" style="height: 80px;background-color: #131466">
		<div align="left" style="width: 80%;float: left"><img src="images/main.jpg"></div>
		<div style="padding-top: 50px; padding-right: 5px; text-align:right;">
		    <a href="#" class="easyui-menubutton" data-options="menu:'#menuUser'" style="background:#ffffff">
                当前用户：&nbsp;${currentUser.userName }
            </a>   
		</div>
	</div>
	<div region="center">
		<div class="easyui-tabs" fit="true" border="false" id="tabs">
			<div title="首页" >
			    <img src="images/welcome.jpg" alt="欢迎使用" width="100%" height="100%"/>
			</div>
		</div>
	</div>
	<div region="west"style="width: 150px;" title="项目名称" split="true">
	   <ul id="deviceTree"></ul>
	</div>
	<!--  
	<div region="south" style="height: 25px;" align="center">版权所有 <a href="http://xtoee.com/">广州炫通电气科技有限公司</a></div>
	-->
	
	<!-- 树形控件的右键菜单 -->
	<div id="menuTree" class="easyui-menu" style="width:120px;">
	<!--  
        <div onclick="addDevice()" data-options="iconCls:'icon-add'">添加设备</div>
        <div onclick="deleteDevice()" data-options="iconCls:'icon-remove'">删除设备</div>
        <div class="menu-sep"></div>
        <div onclick="addProject()" data-options="iconCls:'icon-add'">添加项目</div>
        <div class="menu-sep"></div>
    -->
        <div onclick="expand()">展开</div>
        <div onclick="collapse()">折叠</div>
    </div>
    
    <!-- 用户名的菜单 -->
    <div id="menuUser" class="easyui-menu" style="width:120px;">
        <div onclick="openDialog_ChangePassword()">修改密码</div>
        <div onclick="javascript:location.href='logout'">退出</div>
    </div>
    
    <!-- 修改密码对话框 -->
    <div id="dlg_ChangePassword" class="easyui-dialog" style="width:300px;height:200px;padding:20px;" closed="true" buttons="#dlgButtons_ChangePassword">
        <table id="tbChangePassword">
            <tr>
                <td>原密码：</td>
                <td><input id="password_old" class="easyui-textbox" type="password" /></td>
            </tr>
            <tr>
                <td>新密码：</td>
                <td><input id="password_new1" class="easyui-textbox" type="password" /></td>
            </tr>
            <tr>
                <td>重复新密码：</td>
                <td><input id="password_new2" class="easyui-textbox" type="password" /></td>
            </tr>
        </table>
    </div>
    
    <!--修改密码对话框的按钮 -->
    <div id="dlgButtons_ChangePassword">
        <a href="javascript:submit_ChangePassword()" class="easyui-linkbutton" iconCls="icon-ok">确定</a>
        <a href="javascript:cancel_ChangePassword()" class="easyui-linkbutton" iconCls="icon-cancel">取消</a>
    </div>
</body>
</html>