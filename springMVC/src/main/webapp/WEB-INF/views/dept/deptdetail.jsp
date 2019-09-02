<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="path" value="${pageContext.request.contextPath }"></c:set>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<form action="deptdetail" method="post">
		<input type="text" name="department_id" value="${dept.department_id }"><br>
		<input type="text" name="department_name" value="${dept.department_name }"><br>
		<img alt="이미지" src="${path }/resources/${dept.fileName }"><br>
		<a href="deptdownload?folder=resources&file=${dept.fileName }">이미지 다운로드</a>
		<input type="submit" value="수정하기"/>
	</form>
</body>
</html>