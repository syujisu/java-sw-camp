<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%
        session.invalidate(); // 모든세션정보 삭제
        response.sendRedirect("/Login.jsp"); // 로그인 화면으로 다시 돌아간다.
    %>