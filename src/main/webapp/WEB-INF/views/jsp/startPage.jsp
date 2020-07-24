<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>

<html>

<head>

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <spring:url value="/resources/favicon.ico" var="siteIcon"/>
    <link rel="icon" type="image/x-icon" href="${siteIcon}"/>

    <title>LEONI UA Telephone List</title>

    <spring:url value="/resources/css/bootstrap.min.css" var="bootstrapCss"/>
    <spring:url value="/resources/js/bootstrap.min.js" var="bootstrapJs"/>
    <spring:url value="/resources/js/jquery-3.3.1.min.js" var="jqueryJs"/>
    <spring:url value="/resources/js/mark.min.js" var="markJs"/>
    <spring:url value="/resources/logo.png" var="logo"/>
    <spring:url value="/tellist" var="pageurl"/>


    <link href="${bootstrapCss}" rel="stylesheet"/>
    <script src="${bootstrapJs}"></script>
    <script src=${jqueryJs}></script>
    <script src="${markJs}" charset="UTF-8"></script>
    <link href="${logo}" rel="stylesheet"/>

    <style>

        body {
            padding-top: 60px;
            padding-bottom: 60px;
            padding-left: 15px;
            padding-right: 15px;
        }

        mark {
            background: yellow;
            color: black;
            padding: 0;
        }

        .loading {
            position: absolute;
            top: 50%;
            left: 50%;
        }

        .loading-bar {
            display: inline-block;
            width: 4px;
            height: 18px;
            border-radius: 4px;
            animation: loading 1s ease-in-out infinite;
        }

        .loading-bar:nth-child(1) {
            background-color: #3498db;
            animation-delay: 0;
        }

        .loading-bar:nth-child(2) {
            background-color: #c0392b;
            animation-delay: 0.09s;
        }

        .loading-bar:nth-child(3) {
            background-color: #f1c40f;
            animation-delay: .18s;
        }

        .loading-bar:nth-child(4) {
            background-color: #27ae60;
            animation-delay: .27s;
        }

        @keyframes loading {
            0% {
                transform: scale(1);
            }
            20% {
                transform: scale(1, 2.2);
            }
            40% {
                transform: scale(1);
            }
        }


    </style>


    <script type="text/javascript">
        function load() {
            window.location.href = "${pageContext.request.contextPath}/tellist";
        }
    </script>

</head>

<body onload="load()">

<div class="container-fluid">

    <h2 style="text-align: center">
        LEONI UA Telephone List
    </h2>

    <!--Employees List-->
    <c:if test="${not empty message}">
        <div class="alert alert-success">
                ${message}
        </div>
    </c:if>


    <div class="loading">
        <div class="loading-bar"></div>
        <div class="loading-bar"></div>
        <div class="loading-bar"></div>
        <div class="loading-bar"></div>
    </div>


</div>

</body>
</html>