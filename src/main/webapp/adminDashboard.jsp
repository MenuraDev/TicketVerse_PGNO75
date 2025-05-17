<%-- src/main/webapp/adminDashboard.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css"> <%-- Include Font Awesome --%>
</head>
<body>

<%@ include file="components/header.jsp" %>

<div class="container my-4">
    <h1 class="mb-4"><i class="fas fa-user-shield me-2"></i>Admin Dashboard</h1>
    <c:if test="${not empty dashboardContent}"><p>${dashboardContent}</p></c:if>

    <div class="list-group">
        <a href="admin?action=manageUsers" class="list-group-item list-group-item-action">
            <i class="fas fa-users me-2"></i> Manage Users
        </a>
        <a href="movie?action=manageMovies" class="list-group-item list-group-item-action">
            <i class="fas fa-film me-2"></i> Manage Movies
        </a>
        <%-- New Button/Link for Reservations --%>
        <a href="admin?action=manageReservations" class="list-group-item list-group-item-action">
            <i class="fas fa-clipboard-list me-2"></i> View All Reservations
        </a>
    </div>

    <a href="${pageContext.request.contextPath}/home" class="btn btn-secondary mt-4"><i class="fas fa-home me-1"></i> Back to Home</a>
</div>

<script src="js/bootstrap.bundle.min.js"></script>

</body>
</html>