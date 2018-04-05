<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
</head>
<body>
	<form action="http://sdkba.qbao.com/test/dotrustpay.html" method="post">
	<div>交易类型:<input type="text" name="transType" value="2020"></div>
	<div>应用编码:<input type="text" name="appCode" ></div>
	<div>业务流水号:<input type="text" name="orderNo" ></div>
	<div>用户Id:<input type="text" name="userId" ></div>
	<div>交易金额:<input type="text" name="transAmount" ></div>
	<div>应用渠道:<select name="appSource">
		<option value="0">IOS</option>
		<option value="1">WEB</option>
		<option value="2">Android</option>
	</select></div>
	<div>交易简介:<input type="text" name="transIntro"></div>
	<div>支付方式:<select name="payType">
		<option value="0">组合支付</option>
		<option value="1">宝币支付</option>
	</select></div>
	<div>异步通知url:<input type="text" name="payNotifyUrl"></div>
	<input type="submit" value="提交">
	</form>
</body>

</html> 