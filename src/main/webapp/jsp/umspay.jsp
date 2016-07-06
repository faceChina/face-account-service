<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport"  content="width=device-width,initial-scale=1.0,maximum-scale=1.0,minimum-scale=1.0,user-scalable=no">
<meta content="yes" name="apple-mobile-web-app-capable" />
<meta content="black" name="apple-mobile-web-app-status-bar-style" />
<meta content="telephone=no" name="format-detection" />
<title>支付等待</title>
<script type="text/javascript">
	function check(){
		document.getElementById("myform").submit();    
	}
</script>
</head>
<body onload="javascript:check();">
<!-- <body> -->
	<form action="http://116.228.21.162:9127/umsFrontWebQmjf/umspay" method="post" id="myform" style="display: none;">
		<table>
			<tr>
				<td>签名： <input id="merSign" name="merSign" type="text" value="${rsp.merSign }"></td>
			</tr>
			<tr>
				<td>特征码： <input id="chrCode" name="chrCode" type="text" value="${rsp.chrCode }"></td>
			</tr>
			<tr>
				<td>订单号： <input id="tranId" name="tranId" type="text" value="${rsp.tranId }"></td>
			</tr>
			<tr>
				<td> 商 户 地 址 ： <input id="url" name="url" type="text" value="${rsp.url }"></td>
			</tr>
			<tr>
				<td> 商 户 用 户 号 ： <input id="mchantUserCode" name="mchantUserCode" type="text" value="${rsp.mchantUserCode }"></td>
			</tr>
			<tr>
				<td><s:property value="error"/></td>
			</tr>
			<tr>
				<td><input id="sub" type="button" value="提交" onclick="check()" > </td>
			</tr>
		</table>
	</form>
	<h6>请求中……</h6>
</body>
</html>
