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
	var ok = "${ok}";
	
	if(ok == "write"){
		alert("글이 등록되었습니다.");
	}
	if(ok == "edit"){
		alert("글이 수정되었습니다.");
	}
	if(ok == "del"){
		alert("글이 삭제되었습니다.");
	}
	
	$(function(){
		$("#searchBtn").click(function(){
			var keyword = $("#keyword").val();
			var searchOption = $("select[name=searchOption]").val();
			
			if(keyword == "" || keyword == null){
				alert("검색 키워드를 입력해 주세요.");
				return false;
			}else{
				location.href="/web/board?searchOption="+searchOption+"&keyword="+keyword;
			}
		});
	});
</script>
</head>
<body>
	<div class="container">
		<div class="row">
			<h2 class="boardTitle"><a href="/web/board">게시판</a></h2>
			<div class="col-sm-offset-10 col-sm-2">
				(전체 글: ${count})
			</div>
			<br/><br/>
			<div class="col-md-12">
				<c:set var="number" value="${(pageVo.onePageCount*pageVo.currentPage)-(pageVo.onePageCount-1)}"/>
				<table class="table table-list-search table-hover">
                	<thead>
                        <tr>
                            <th>NO</th>
                            <th>제목</th>
                            <th>글쓴이</th>
                            <th>작성일</th>
                            <th>조회수</th>
                        </tr>
                    </thead>
                    
                    <tbody>
                    	<c:if test="${count==0}">
							<tr>
								<td colspan="5">&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 검색 결과가 없습니다.</td>
							</tr>
						</c:if>
                    	<c:forEach var="list" items="${list}">
	                        <tr>
	                            <td>
	                            	${number}
	                            	<c:set var="number" value="${number+1}"/>
	                            </td>
	                            <td>
									<a href="/web/boardDetail?num=${list.board_idx}">${list.subject}</a>
	                            </td>
	                            <td>${list.writer}</td>
	                            <td>${list.date_created}</td>
	                            <td>${list.hit_cnt}</td>
	                        </tr>
                        </c:forEach>
                    </tbody>
            	</table>   
			</div>
			
			<c:if test="${count!=0}">
				<div class="text-center">
					<ul class="pagination">
						<c:choose>
							<c:when test="${keyword == '' || keyword eq null}">
								<li><a href="/web/board?currentPage=${1}">&#60;&#60;</a></li>
							</c:when>
							<c:otherwise>
								<li><a href="/web/board?currentPage=${1}&searchOption=${searchOption}&keyword=${keyword}">&#60;&#60;</a></li>
							</c:otherwise>
						</c:choose>
						<c:if test="${pageVo.currentPage>1}">
							<c:choose>
								<c:when test="${keyword == '' || keyword eq null}">
									<li><a href="/web/board?currentPage=${pageVo.currentPage-1}">&#60;</a></li>
								</c:when>
								<c:otherwise>
									<li><a href="/web/board?currentPage=${pageVo.currentPage-1}&searchOption=${searchOption}&keyword=${keyword}">&#60;</a></li>
								</c:otherwise>
							</c:choose>
						</c:if>
						<c:forEach var="p" begin="${pageVo.startPageNum}" end="${pageVo.startPageNum+pageVo.onePageCount-1}">
							<c:if test="${p<=pageVo.totalPageCount}">
								<c:if test="${pageVo.currentPage==p }">
									<c:choose>
										<c:when test="${keyword == '' || keyword eq null}">
											<li class="active"><a href="/web/board?currentPage=${p}">${p}</a></li>
										</c:when>
										<c:otherwise>
											<li class="active"><a href="/web/board?currentPage=${p}&searchOption=${searchOption}&keyword=${keyword}">${p}</a></li>
										</c:otherwise>
									</c:choose>
								</c:if>
								<c:if test="${pageVo.currentPage!=p }">
									<c:choose>
										<c:when test="${keyword == '' || keyword eq null}">
											<li><a href="/web/board?currentPage=${p}">${p}</a></li>
										</c:when>
										<c:otherwise>
											<li><a href="/web/board?currentPage=${p}&searchOption=${searchOption}&keyword=${keyword}">${p}</a></li>
										</c:otherwise>
									</c:choose>
								</c:if>
							</c:if>
						</c:forEach>
						<c:if test="${pageVo.currentPage!=pageVo.totalPageCount}">
							<c:choose>
								<c:when test="${keyword == '' || keyword eq null}">
									<li><a href="/web/board?currentPage=${pageVo.currentPage+1}">&#62;</a></li>
								</c:when>
								<c:otherwise>
									<li><a href="/web/board?currentPage=${pageVo.currentPage+1}&searchOption=${searchOption}&keyword=${keyword}">&#62;</a></li>
								</c:otherwise>
							</c:choose>	
						</c:if>
						<c:choose>
							<c:when test="${keyword == '' || keyword eq null}">
								<li><a href="/web/board?currentPage=${pageVo.totalPageCount}">&#62;&#62;</a></li>
							</c:when>
							<c:otherwise>
								<li><a href="/web/board?currentPage=${pageVo.totalPageCount}&searchOption=${searchOption}&keyword=${keyword}">&#62;&#62;</a></li>
							</c:otherwise>
						</c:choose>
					</ul>
				</div>
			</c:if>
				
			<div class="col-sm-offset-7 col-sm-2">
				<select class="form-control input-sm" name="searchOption">
						<option value="subject" <c:out value="${searchOption eq 'subject'}"/>>제목</option>
						<option value="writer" <c:out value="${searchOption eq 'writer'}"/>>글쓴이</option>
						<option value="content" <c:out value="${searchOption eq 'content'}"/>>내용</option>
						<option value="all" <c:out value="${searchOption eq 'all'}"/>>제목+글쓴이+내용</option>
				</select>
			</div>
			<div class="col-sm-2">
				<input type="text" id="keyword" class="form-control input-sm" value="${keyword}"/>
			</div>
			<div class="col-sm-1">
				<input type="button" value="조회" id="searchBtn"  class="btn btn-default btn-md"/>
			</div>
			<br><br>
			<div class="col-sm-offset-1 col-sm-1">
				<a href="/web/boardWrite" class="btn btn-success btn-md">글쓰기</a>
			</div>
		</div>
	</div>
</body>
</html>