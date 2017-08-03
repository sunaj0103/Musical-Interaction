<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page session="true" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script src="/web/js_css/jquery-3.1.1.min.js"></script>
<script src="/web/js_css/bootstrap.min.js"></script>
<link href="/web/js_css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
<link href="/web/js_css/boardStyle.css" rel="stylesheet" type="text/css"/>
<title>게시판</title>
<script>
function prePage(){
	history.back();
}

function close(){
	var win=window.open("","_self");
	win.close();
}
</script>
</head>
<body>
	<div class="container">
		<br>
		<h1>에러!</h1>
		<h3>${msg}</h3>
		<br><br>
		<div>
			<a class="btn btn-info btn-md" href="javascript:prePage();">이전 페이지</a>
			<a class="btn btn-warning btn-md" href="javascript:close();">닫기</a>
		</div>
	</div>
</body>
</html>