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
    <spring:url value="/resources/js/jquery-3.5.1.min.js" var="jqueryJs"/>
    <spring:url value="/resources/js/mark.min.js" var="markJs"/>
    <spring:url value="/resources/logo.png" var="logo"/>
    <spring:url value="/list" var="homePageUrl"/>

    <link href="${bootstrapCss}" rel="stylesheet"/>
    <script src=${jqueryJs}></script>
    <script src="${bootstrapJs}"></script>
    <script src="${markJs}" charset="UTF-8"></script>
    <link href="${logo}" rel="stylesheet"/>

    <style>

        mark {
            background: yellow;
            color: black;
            padding: 0;
        }

        @page {
            margin: 0;
        }

        @media screen {
            /*--This is for Screen--*/
            body {
                padding-bottom: 60px;
                padding-top: 60px;
            }

            .only-print {
                display: none;
            }

            .not-visible {
                display: none;
            }

            .box {
                display: none;
                width: 100%;
            }

            a:hover + .box, .box:hover {
                display: inline-block;
                position: fixed;
                top: 20%;
                left: 30%;
            }

            .boxTop {
                display: none;
                width: 100%;
            }

            a:hover + .boxTop, .boxTop:hover {
                display: inline-block;
                position: fixed;
                top: 5%;
            }
        }

        @media print /*--This is for Printing--*/ {

            body {
                padding-bottom: 0px;
                padding-top: 0px;
            }

            nav {
                display: none;
            }

            .showed-print {
                font-weight: bold;
                text-align: center;
                font-size: 180%;
            }

            .only-print {
                font-weight: bold;
                text-align: center;
                font-size: 180%;
            }

            .hidden-print {
                display: none;
            }
        }


    </style>

</head>


<nav class="navbar navbar-default navbar-fixed-top">

    <div class="container">

        <div class="navbar-header">
            <a class="navbar-brand" href="${pageContext.request.contextPath}">
                <span class="glyphicon glyphicon-list" aria-hidden="true"></span>
                <b>Main page</b>
            </a>
        </div>

        <ul class="nav navbar-nav">

            <li>
                <form class="navbar-form" action="${homePageUrl}" method="get"
                      id="seachEmployeeForm" role="form">

                    <div class="input-group">
                        <input type="text" style="width:520px" name="searchAction" class="form-control" required="true"
                               placeholder="Enter name, last name, number, login, cost center or department of employee"
                               value="${searchString}"
                        >

                        <div class="input-group-btn">
                            <button class="btn btn-default" type="submit">
                                <i class="glyphicon glyphicon-search"></i>
                                Search
                            </button>
                        </div>
                    </div>

                    <a><span class="label label-default"><bold>?</bold></span></a>
                    <div class="boxTop">
                        <img src="data:image/jpeg;base64,${regexPhotoLink}"/>
                    </div>

                </form>
            </li>

        </ul>

        <img src="${logo}" width="12%" height="auto" style="padding-top:7px; float:right">

        <c:if test="${not empty currentSessionUserName}">
            <span class="label label-info"
                  style="float:right; position: relative; top: 7px; right: 7px;">${currentSessionUserName}</span>
        </c:if>

    </div>
</nav>

<body>

<div class="container">

    <h2 style="text-align: center" class="hidden-print">
        LEONI UA Telephone List
    </h2>

    <br>

    <div class="jumbotron">

        <c:choose>
            <c:when test="${not isPermissionIsGranted}">
                <div class="alert alert-danger">
                    <strong>Access to this page is forbidden for you.</strong>
                </div>
                <h3>Go to <a href="${pageContext.request.contextPath}">main page</a></h3>
            </c:when>

            <c:otherwise>

                <div class="media">

                    <div class="media-left">
                        <img src="data:image/jpeg;base64,${currentEmployee.photoLink}" class="media-object"
                             style="width:350px; border: 1px solid #ddd; padding: 5px;">
                    </div>


                    <div class="media-body">

                        <h2 class="media-heading">${currentEmployee.name}</h2>

                        <br>

                        <ul class="list-group">

                            <li class="list-group-item"><strong>Personal ID</strong>: ${currentEmployee.persNumber}</li>

                            <li class="list-group-item"><strong>Department</strong>: ${currentEmployee.department}</li>

                            <li class="list-group-item"><strong>Position</strong>: ${currentEmployee.position} </li>

                            <li class="list-group-item"><strong>CC</strong>: ${currentEmployee.costCenter} </li>

                            <li class="list-group-item"><strong>Login</strong>: ${currentEmployee.login} </li>

                            <li class="list-group-item"><strong>E-mail</strong>: ${currentEmployee.email} </li>

                            <li class="list-group-item"><strong>Location</strong>: ${currentEmployee.plantName} </li>

                            <li class="list-group-item"><strong>Hired Date</strong>: ${currentEmployee.hiredDate} </li>

                            <li class="list-group-item"><strong>Date Of Birth</strong>: ${currentEmployee.dateOfBirth}
                            </li>

                            <li class="list-group-item"><strong>Private phone
                                number</strong>: ${currentEmployee.privatePhoneNum}</li>

                        </ul>

                        <c:if test="${not empty currentEmployee.phoneNumbersWithDescription}">
                            <div class="panel panel-info">
                                <div class="panel-body">
                                    <strong>Phone number</strong>:
                                </div>
                                <c:forEach var="oneEntry" items="${currentEmployee.phoneNumbersWithDescription}">
                                    <div class="panel-footer">${oneEntry}</div>
                                </c:forEach>
                            </div>
                        </c:if>

                        <c:if test="${not empty currentEmployee.allMobileTelNumbers}">
                            <div class="panel panel-info">
                                <div class="panel-body">
                                    <strong>Mobile number</strong>:
                                </div>
                                <c:forEach var="oneEntry" items="${currentEmployee.allMobileTelNumbers}">
                                    <div class="panel-footer">${oneEntry}</div>
                                </c:forEach>
                            </div>
                        </c:if>

                    </div>


                </div>

            </c:otherwise>
        </c:choose>
    </div>

</div>

</body>
</html>