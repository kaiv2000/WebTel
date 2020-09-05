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
    <spring:url value="/resources/catload.gif" var="loadPic"/>
    <spring:url value="/list" var="homePageUrl"/>

    <script src=${jqueryJs}></script>
    <script src="${bootstrapJs}"></script>
    <link href="${bootstrapCss}" rel="stylesheet"/>
    <script src="${markJs}" charset="UTF-8"></script>
    <link href="${logo}"/>

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

        .highLight {
            background-color: yellow;
        }

        img.center {
            display: block;
            margin: 0 auto;
        }

    </style>

    <meta http-equiv="X-UA-Compatible" content="IE=EDGE"/>

</head>

<body onload="loadData()">

<nav class="navbar navbar-default navbar-fixed-top">

    <div class="container">

        <div class="navbar-header">
            <a class="navbar-brand" href="${pageContext.request.contextPath}">
                <span class="glyphicon glyphicon-list" aria-hidden="true"></span>
                <b id="mainPageButton">Main page</b>
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

<h2 style="text-align: center">
    LEONI UA Telephone List
</h2>

<c:set var="sessionNotCreated" value="${empty employeeList}"/>

<script type="text/javascript">
    function loadData() {

        var pageNotLoaded = ${sessionNotCreated};

        if (pageNotLoaded == true) {

            document.getElementById("waitData").style.display = 'block';
            document.getElementById("mainData").style.display = 'none';

            const request = new XMLHttpRequest();
            request.onreadystatechange = function () {
                if (this.readyState == 4 && this.status == 200) {
                    $.ajaxSetup({cache: false});
                    $("#mainData").load(" #mainData > *", function () {
                        document.getElementById("waitData").style.display = 'none';
                        document.getElementById("mainData").style.display = 'block';
                    });
                }
            }
            request.open("GET", "${homePageUrl}", true);
            request.send(null);
        }
    }

</script>

<script type="text/javascript">
    function refreshPage() {

    };
</script>


<div id="waitData" style="display: none;">

    <img class="center" src="${loadPic}"/>

</div>

<div class="container-fluid" id="mainData">

    <c:choose>
        <c:when test="${not empty employeeList.pageList}">

            <c:set var="pageListHolder" value="${employeeList}" scope="session"/>
            <c:set var="allPagesCount" value="${allPagesCount}"/>
            <c:set var="currentPage" value="${currentPage}"/>

            <table class="table table-striped">
                <thead>

                <tr>
                    <th><b>Phone No</b></th>
                    <th><b>Phone Description</b></th>
                    <th><b>Personal ID</b></th>
                    <th><b>Name</b></th>
                    <th><b>Department</b></th>
                    <th><b>Position</b></th>
                    <th><b>CC</b></th>
                    <th><b>Login</b></th>
                    <th><b>E-mail</b></th>
                    <th><b>Mobile</b></th>
                    <th><b>Location</b></th>
                </tr>
                </thead>

                <tbody>

                <c:forEach var="employee" items="${pageListHolder.pageList}">
                    <tr class="${classSucess}">

                        <c:choose>
                            <c:when test="${not empty searchString}">

                                <c:set var="telNumber"
                                       value="${fn:replace(employee.telNumber, '<span class=highLight>', '')}"/>
                                <c:set var="telNumber" value="${fn:replace(telNumber, '</span>', '')}"/>
                                <td style="color:#305ace"><a href="tel:${telNumber}">
                                        ${employee.telNumber}
                                </td>
                                <td>
                                        ${employee.description}
                                </td>

                                <c:choose>
                                    <c:when test="${not isPermissionIsGranted}">
                                        <td>
                                                ${employee.persNumber}
                                        </td>
                                    </c:when>

                                    <c:otherwise>
                                        <td style="color:#305ace">
                                            <c:set var="persNumber"
                                                   value="${fn:replace(employee.persNumber, '<span class=highLight>', '')}"/>
                                            <c:set var="persNumber"
                                                   value="${fn:replace(persNumber, '</span>', '')}"/>
                                            <a href="${pageContext.request.contextPath}/details-${persNumber}">
                                                    ${employee.persNumber}
                                            </a>
                                            <div class="box">
                                                <img src="data:image/jpeg;base64,${employee.photoLink}"
                                                     style="width:250px; border: 1px solid #ddd; padding: 5px;">
                                                </img>
                                            </div>
                                        </td>
                                    </c:otherwise>
                                </c:choose>

                                <td>
                                        ${employee.name}
                                </td>
                                <td>
                                        ${employee.department}
                                </td>
                                <td>
                                        ${employee.position}
                                </td>
                                <td>
                                        ${employee.costCenter}
                                </td>
                                <td>
                                        ${employee.login}
                                </td>
                                <td>
                                    <c:set var="email"
                                           value="${fn:replace(employee.email, '<span class=highLight>', '')}"/>
                                    <c:set var="email"
                                           value="${fn:replace(email, '</span>', '')}"/>
                                    <a href="mailto:${email}">
                                            ${employee.email}
                                    </a>
                                </td>
                                <td>
                                    <c:if test="${not empty employee.allMobileTelNumbers}">
                                        <c:forEach items="${employee.allMobileTelNumbers}" var="entry"
                                                   varStatus="loop">
                                            <c:choose>
                                                <c:when test="${loop.index=='0'}">
                                                    <div>
                                                            ${entry}
                                                    </div>
                                                </c:when>
                                                <c:otherwise>
                                                    <div><h6>
                                                        <c:set var="currentField" value="${entry}"/>
                                                            ${entry}
                                                    </h6></div>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:forEach>
                                    </c:if>
                                </td>
                                <td>
                                        ${employee.plantName}
                                </td>

                            </c:when>

                            <c:otherwise>

                                <td style="color:#305ace"><a
                                        href="tel:${employee.telNumber}">${employee.telNumber}</a></td>
                                <td>${employee.description}</td>

                                <c:choose>
                                    <c:when test="${not isPermissionIsGranted}">
                                        <td>${employee.persNumber}</td>
                                    </c:when>

                                    <c:otherwise>
                                        <td style="color:#305ace">
                                            <a href="${pageContext.request.contextPath}/details-${employee.persNumber}">${employee.persNumber}</a>
                                            <div class="box">
                                                <img src="data:image/jpeg;base64,${employee.photoLink}"
                                                     style="width:250px; border: 1px solid #ddd; padding: 5px;">
                                                </img>
                                            </div>
                                        </td>
                                    </c:otherwise>
                                </c:choose>

                                <td>${employee.name}</td>
                                <td>${employee.department}</td>
                                <td>${employee.position}</td>
                                <td>${employee.costCenter}</td>
                                <td>${employee.login}</td>
                                <td>
                                    <a href="mailto:${employee.email}">${employee.email}</a>
                                </td>
                                <td>
                                    <c:if test="${not empty employee.allMobileTelNumbers}">
                                        <c:forEach items="${employee.allMobileTelNumbers}" var="entry"
                                                   varStatus="loop">
                                            <c:choose>
                                                <c:when test="${loop.index=='0'}">
                                                    <div>${entry}</div>
                                                </c:when>
                                                <c:otherwise>
                                                    <div><h6>${entry}</h6></div>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:forEach>
                                    </c:if>
                                </td>
                                <td>${employee.plantName}</td>

                            </c:otherwise>

                        </c:choose>

                    </tr>

                </c:forEach>

                </tbody>

            </table>


            <nav class="navbar navbar-default navbar-fixed-bottom">

                <div>
                    <ul class="pager">

                        <c:choose>

                            <c:when test="${pageListHolder.firstPage}">

                                <li class="page-item disabled"><a>Previous page</a></li>

                            </c:when>

                            <c:otherwise>
                                <li class="page-item">
                                    <a class="page-link" href="${homePageUrl}?show=prevPage">Previous page</a>
                                </li>
                            </c:otherwise>

                        </c:choose>

                        <span class="badge">Page ${currentPage} of ${allPagesCount} (total ${foundedObjectsCount} records) </span>

                        <c:choose>

                            <c:when test="${pageListHolder.lastPage}">

                                <li class="page-item disabled">
                                    <a class="page-link">Next page</a>
                                </li>

                            </c:when>

                            <c:otherwise>

                                <li class="page-item">
                                    <a class="page-link" href="${homePageUrl}?show=nextPage">Next page</a>
                                </li>

                            </c:otherwise>

                        </c:choose>


                    </ul>
                </div>
            </nav>

        </c:when>

        <c:otherwise>
            <br>
            <div class="alert alert-info">
                According to the given criteria, nothing was not found
            </div>
        </c:otherwise>

    </c:choose>

</div>

</body>
</html>