<%@ page session="false" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>


<head>

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <title>LEONI UA Telephone List</title>

    <spring:url value="/resources/css/bootstrap.min.css" var="bootstrapCss"/>
    <spring:url value="/resources/js/bootstrap.min.js" var="bootstrapJs"/>
    <spring:url value="/resources/logo.png" var="logo"/>
    <spring:url value="/" var="urlHome"/>

    <script src="${bootstrapJs}"></script>
    <link href="${bootstrapCss}" rel="stylesheet"/>
    <link href="${logo}" rel="stylesheet"/>

    <style>

        body {
            padding-top: 60px;
        }

    </style>

</head>



<body>

<div class="container">


    <div class="alert alert-danger">
        <strong>Something went wrong... Or current page isn't available.</strong>
    </div>

    <h2>Go to <a href="${urlHome}">main page</a></h2>

    <br>
    <p>${myMessage}</p>

    <c:forEach items="${exception.stackTrace}" var="stackTrace">
        ${stackTrace}
    </c:forEach>

</div>

</body>
</html>