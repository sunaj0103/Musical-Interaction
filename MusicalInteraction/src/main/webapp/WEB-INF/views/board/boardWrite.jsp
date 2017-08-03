<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isErrorPage="true" %>
<%@ page session="true" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script src="/web/js_css/jquery-3.1.1.min.js"></script>
<script src="/web/js_css/bootstrap.min.js"></script>
<script src="/web/js_css/ckeditor/ckeditor.js"></script>
<link href="/web/js_css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
<link href="/web/js_css/ckeditor/samples/css/samples.css" rel="stylesheet"/>
<link href="/web/js_css/boardStyle.css" rel="stylesheet" type="text/css"/>
<title>게시판</title>
<script>
var ok = "${ok}";

if(ok == "write"){
	alert("등록되었습니다.");
}

var cnt = 1;

$(function(){
	$("#boardBtn").click(function(){
		if($("#writer").val() == ""){
			alert("이름을 입력해 주세요.");
			return false;
		}
		
		if($("#subject").val() == ""){
			alert("제목을 입력해 주세요.");
			return false;
		}
		
		var content = CKEDITOR.instances['content'].getData();
		
		if(content == ""){
			alert("내용을 입력해 주세요.");
			return false;
		}
				
		$("#boardWriteFrm").submit();
    });
	
	$("#resetBtn").click(function(){
		$("#writer").val("");
		$("#subject").val("");
		CKEDITOR.instances['content'].setData('');
	});
	
	$(".delBtn").click(function(){
		//console.log("del");
		//$(this).parent().siblings().remove();
		$(this).parent().parent().remove();
	});
	
	$("#addFile").click(function(){
		var str = '<div><div class="col-sm-offset-3 col-sm-4">';
		   str += '<input type="file" name="file_'+(cnt++)+'"/></div>';
		   str += '<div class="col-sm-1">';
		   str += '<input type="button" value="삭제" class="delBtn btn btn-default btn-sm"/></div></div>';
		   
        $("#fileDiv").append(str);
        
        $(".delBtn").click(function(){
    		console.log("del");
    		//$(this).parent().siblings().remove();
    		$(this).parent().parent().remove();
    	});
	});
	
});

</script>
</head>
<body>
	<div class="container">
		<div class="row">
			<h2 class="boardTitle">게시판</h2>
			<div class="col-md-12">
				<form class="form-horizontal" id="boardWriteFrm" name="boardWriteFrm" method="post" enctype="multipart/form-data" action="/web/boardWriteOk">
					<div class="form-group">
						<label for="name" class="control-label col-sm-2">작성자 </label>
						<div class="col-sm-10">
							<input type="text" class="form-control" name="writer" id="writer"/>
						</div>
					</div>
					<div class="form-group">
						<label for="subject" class="control-label col-sm-2">제목 </label>
						<div class="col-sm-10">
							<input type="text" class="form-control" name="subject" id="subject"/>
						</div>
					</div>
					<div class="form-group">
						<label for="content" class="control-label col-sm-2">내용 </label>
						<div class="col-sm-10">
							<textarea class="ckeditor" cols="1" name="content" id="content" rows="15"></textarea>
						</div>
					</div>
					<div class="form-group">
						<label for="file" class="control-label col-sm-2">첨부파일 </label>
						<div class="col-sm-10"></div>
					</div>
					<div class="form-group" id="fileDiv">
						<div>
							<div class="col-sm-offset-3 col-sm-4">
								<input type="file" name="file_0">
							</div>
							<div class="col-sm-1">
								<input type="button" value="삭제" class="delBtn btn btn-default btn-sm"/>
							</div>
						</div>
					</div>
					<div class="form-group">
						<div class="col-sm-offset-3 col-sm-9">
							<input type="button" value="파일 추가" class="btn btn-default btn-sm" id="addFile"/>
						</div>
					</div>
					<div class="form-group"> 
			    		<div class="col-sm-offset-6 col-sm-6">
			    			<input type="button" id="resetBtn" value="다시쓰기" class="btn btn-warning btn-md"/>
							<input type="button" id="boardBtn" value="등록" class="btn btn-success btn-md"/>
						</div>
					</div>
					<div class="form-group"> 
						<div class="col-sm-offset-10 col-sm-1">
			    			<a href="/web/board" class="btn btn-info btn-md">목록</a>
			    		</div>
					</div>
				</form>
			</div>
		</div>
	</div>
</body>
</html>