<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
	request.setCharacterEncoding("UTF-8");

	String input = request.getParameter("input");
	String result = request.getParameter("result");
	String[] related_sentence = request.getParameterValues("related_sentence");
	String[] related_link = request.getParameterValues("related_link");
%>


    
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>사실 확인을 해보아요</title>
	<link rel="stylesheet" type="text/css" href="Main.css?ver=1"/>
</head>
<body>
	<p id="p_title"><span>C</span>heck <span>T</span>he <span>F</span>acts!</p>
	
	<div id="div_left">
		<div id="div_input">
			<p id="p_input">문장 입력</p>
			<div id="div_inputArea">
				<form action="inputProcess.jsp" method="post">
				<table>
					<tr>
						<td><textarea id="txt_input" name="txt_input"><%=input%></textarea></td>
					</tr>
					<tr>
						<td><input type="submit" id="btn_input" value="입력"></td>
					</tr>
				</table>
				</form>
			</div>
		</div>
		<div id="div_result">
			<%=result %>
		</div>
	</div>
	<div id="div_right">
		<p id="p_relatedInfo">관련된 정보</p>
		<div id="div_relatedArea">
			<table>
			<%
				for(int i=0; i<related_sentence.length; i++) {
					if(related_sentence[i].equals("")) {
						break;
					}
			%>
				<tr>
					<td class="relatedDatas"><span class="txt_related"><%=related_sentence[i] %></span><span class="span_link"><a href="<%=related_link[i] %>" target="_blank">바로가기</a></span></td>
				</tr>
			<%
				}
			%>
			</table>
		</div>
	</div>
</body>
</html>