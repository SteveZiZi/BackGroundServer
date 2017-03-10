<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>直流集中供电照明系统登录</title>
<link rel="stylesheet" type="text/css" href="jquery-easyui-1.4.3/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="jquery-easyui-1.4.3/themes/icon.css">
<script type="text/javascript" src="jquery-easyui-1.4.3/jquery.min.js"></script>
<script type="text/javascript" src="jquery-easyui-1.4.3/jquery.easyui.min.js"></script>
<script type="text/javascript" src="jquery-easyui-1.4.3/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript">

	function resetValue()
	{
		$("#userName").textbox('setValue', "");
		$("#password").textbox('setValue', "");
	}
	
</script>
</head>
<body style="background-image:url(images/login.jpg);background-position:center top;background-repeat:no-repeat">
	<div align="center" style="padding-top:237px;">
		<form action="login" method="post">
			<table width="500" height="400" >
				<tr height="80">
					<td colspan="3"></td>
				</tr>
				<tr height="10">
					<td width="10%"></td>
					<td><input name="user.userName" id="userName" value="${user.userName}" 
						class="easyui-textbox" style="width:100%;height:50px;border:0" 
						data-options="prompt:'请输入您的用户名',iconCls:'icon-man',iconWidth:38">
					</td>
					<td width="10%"></td>
				</tr>
				<tr height="10">
					<td></td>
					<td><input name="user.password" id="password" 
						class="easyui-textbox" type="password" style="width:100%;height:50px" 
						data-options="prompt:'请输入密码',iconCls:'icon-lock',iconWidth:38">
					</td>
					<td></td>
				</tr>
				<tr height="10">
					<td></td>
					<td colspan="2">
						<font color="red">${error }</font>
					</td>
				</tr>
				<tr height="10">
                    <td></td>
                    <td><input type="submit" value="登录" style="width:100%;height:50px"/></td>
                    <td></td>
                </tr>
				<tr height="10">
                    <td></td>
                    <td><input type="button" value="重置" style="width:100%;height:50px;background:#6B7DE9;border:none" onclick="resetValue()"/></td>
                    <td></td>
                </tr>
                <tr height="10">
                    <td></td>
                </tr>
			</table>
		</form>
	</div>
</body>
</html>