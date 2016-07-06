<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>${sessionScope.shop.name}-支付等待</title>
<script type="text/javascript">
	function toPay(){
		document.getElementById("myform").submit();    
	}
</script>
</head>
<body onload="javascript:toPay();">
	<form id="myform" method=post action="https://yintong.com.cn/llpayh5/payment.htm" enctype="application/x-www-form-urlencoded">
		<input type="hidden" name='req_data' value='${req_data}'>
<!-- 		<input type=submit value="网银在线支付">
 -->	</form>
</body>
</html>
