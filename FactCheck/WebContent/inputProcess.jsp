<%@ page import="org.json.simple.JSONObject"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="team.proreplyer.*, team.proreplyer.DataProcessor"%>

<%
	request.setCharacterEncoding("UTF-8");

	String input = request.getParameter("txt_input");
	Result result = new DataProcessor().processInputData(input);
	String result_probability = result.result;
	String[] result_related_sentence = new String[result.relatedData_array.size()];
	String[] result_related_link = new String[result.relatedData_array.size()];
	for(int i=0; i<result.relatedData_array.size(); i++) {
		result_related_sentence[i] = ((JSONObject) result.relatedData_array.get(i)).get("sentence").toString();
		result_related_link[i] = ((JSONObject) result.relatedData_array.get(i)).get("link").toString();
	}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>input processing...</title>
	</head>
	<body>
		<form id="formToResult" action="result.jsp" method="post">
			<input type="hidden" name="input" value="<%=input %>">
			<input type="hidden" name="result" value="<%=result_probability %>">
			<%
			if(result_related_link.length == 0) {
			%>
			<input type="hidden" name="related_sentence" value="">
			<input type="hidden" name="related_link" value="">
			<%
			} else {
				for(int i=0; i<result_related_link.length; i++) {
				%>
				<input type="hidden" name="related_sentence" value="<%=result_related_sentence[i] %>">
				<input type="hidden" name="related_link" value="<%=result_related_link[i] %>">
				<%
				}
			}
			%>
		</form>

<%
	out.println("<script>document.getElementById('formToResult').submit();</script>");
%>
	</body>
</html>
