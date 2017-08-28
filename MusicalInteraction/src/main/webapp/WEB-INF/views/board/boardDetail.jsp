<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page session="true" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script src="/web/js_css/jquery-3.1.1.min.js"></script>
<script src="/web/js_css/bootstrap.min.js"></script>
<link href="/web/js_css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
<link href="/web/js_css/boardStyle.css" rel="stylesheet" type="text/css"/>
<title>게시판</title>
<script type="text/javascript">
var stat = "${stat}";

if(stat == "del"){
	alert("글이 삭제되었습니다.");
	location.href='/web/board';
}

function downloadFile(oriFile, fileName){
	location.href="/web/downloadFile?oriFile=" + oriFile + "&fileName=" + fileName;
}

function delChk(){
	if(confirm("글을 삭제하시겠습니까?")){
		location.href="/web/boardDel?num=${vo.board_idx}";
	}
}
	
function editChk(){
	location.href="/web/boardEdit?num=${vo.board_idx}";
}
</script>
</head>
<body>
	<div class="container">
		<div class="row">
			<div class="col-sm-12">
				<h2 class="boardTitle">게시판</h2>
			</div>
			<div>
				<div class="col-sm-12 text-center brdTop brdBtm">${vo.subject}</div>
				<div class="col-sm-4 text-center brdBtm">글쓴이: ${vo.writer}</div>
				<div class="col-sm-4 text-center brdBtm">작성일: ${vo.date_created}</div>
				<div class="col-sm-4 text-center brdBtm">조회수: ${vo.hit_cnt}</div>
				<div class="col-sm-12"><br></div>
				<div class="col-sm-offset-2 col-sm-8 content">
					<c:set var="content1" value="${fn:replace(vo.content, '&lt;', '<')}"/>
      				<c:set var="content2" value="${fn:replace(content1, '&gt;', '>')}" />
					<c:set var="content" value="${fn:replace(content2, '&quot;', '\"')}"/>
					${content}
				</div>
				<div class="col-sm-12"></div>
				<div class="col-sm-12 brdTop brdBtm">
					<div class="col-sm-4 text-center">첨부파일</div>
					<div class="col-sm-8">
						<c:if test="${fVo == '[]' || fVo eq null}">
							첨부 파일이 없습니다.
						</c:if>
						<c:if test="${fVo != '[]' || fVo ne null}">
							<c:forEach var="fVo" items="${fVo}">
								<input type="hidden" id="file_idx" value="${fVo.file_idx}">
	                        	<a href="javascript:downloadFile('${fVo.ori_file_name}', '${fVo.stored_file_name}')">${fVo.ori_file_name}</a>&nbsp;
	                        	<%-- <fmt:formatNumber value="${fVo.file_size}" type="number"/> --%>
	                        	(<script>
	                        		var bytes = Number("${fVo.file_size}");
	                            	var s = ['bytes', 'KB', 'MB', 'GB', 'TB', 'PB'];
	                            	var e = Math.floor(Math.log(bytes)/Math.log(1024));
	                           
	                            	if(e == "-Infinity") document.write("0 "+s[0]); 
	                            	else  document.write( (bytes/Math.pow(1024, Math.floor(e))).toFixed(2)+" "+s[e]);
	                        	</script>)
	                        	<c:set var="fileLower" value="${fn:toLowerCase(fVo.ori_file_name)}" />
								<c:forTokens var="ext" items="${fileLower}" delims="." varStatus="status">
								    <!-- 파일명중간에 "." 이 존재할수도 있으니 항상 status.last(마지막번째)를 실행해주어야 한다 -->
								    <c:if test="${status.last}">
								        <c:choose>
								            <c:when test="${ext eq 'jpg' || ext eq 'png' || ext eq 'gif'}">
								            	<img src="${pageContext.request.contextPath}/upload/${fVo.stored_file_name}"/>
								            </c:when>
								            <c:when test="${ext eq 'pdf'}">
								                &nbsp;
								                <a href="/web/sheetMusic?stored_file_name=${fVo.stored_file_name}" class="btn btn-default btn-sm">보기</a>
								            </c:when>
								        </c:choose>
								    </c:if>
								</c:forTokens>
								<br>
							</c:forEach>
						</c:if>
					</div>
				</div>
				<div class="col-sm-12"><br></div>
				<div class="col-sm-offset-5 col-sm-2">
				    <a href="javascript:editChk()" class="btn btn-success btn-md">수정</a>
			        <a href="javascript:delChk()" class="btn btn-warning btn-md">삭제</a>
			    </div>
			    <div class="col-sm-12"></div>
				<div class="col-sm-offset-10 col-sm-1">
					<a href="/web/board" class="btn btn-info btn-md">목록</a>
				</div>
			</div>	
		</div>
	</div>
</body>
</html>