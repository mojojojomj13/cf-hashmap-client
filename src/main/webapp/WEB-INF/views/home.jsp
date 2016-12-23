<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>HashMap Client Home</title>
<script type="text/javascript">
	window.onload = function() {
		prettyPrint1();
		prettyPrint2();

	}

	function prettyPrint1() {

		<c:if test="${not empty allServices}">
		var obj = ${allServices};
		//var obj = JSON.parse(ugly);
		var pretty = JSON.stringify(obj, undefined, 4);
		document.getElementById('allServices').innerHTML = '<pre>' + pretty
				+ '</pre>';
		</c:if>
	}

	function prettyPrint2() {
		<c:if test="${not empty aService}">
		var obj = ${aService};
		//var obj = JSON.parse(ugly);
		var pretty = JSON.stringify(obj, undefined, 4);
		document.getElementById('aService').innerHTML = '<pre>' + pretty
				+ '</pre>';
		</c:if>
	}
</script>
</head>
<body>
	<h1>
		<u>HashMap Client Home</u>
	</h1>

	<fieldset>
		<legend>Get All Services</legend>
		<form action="getAllServices">
			<input type="submit" value="GET ALL SERVICES" />
			<div
				style="wdith: 90%; height: 200px; overflow-y: scroll; border: solid 1px green; border-radius: 10px;"
				id="allServices">${allServices}</div>

		</form>
		<form action="createServiceInstance">
			<input type="submit" value="CREATE A SERVICE INSTANCE" />
		</form>

		<form action="getServiceInfo">
			<select name="selectedSid">
				<c:forEach items="${sId}" var="id" varStatus="count">
					<option label="Service ${count.index+1}" value="${id}"></option>
				</c:forEach>
			</select> <input name="action" type="submit" value="GET SERVICE INFO" />
			<input name="action" type="submit" value="BIND A SERVICE">
			<div
				style="wdith: 90%; height: 200px; overflow-y: scroll; border: solid 1px green; border-radius: 10px;"
				id="aService">${aService}</div>
		</form>
		${sId}

	</fieldset>
</body>
</html>