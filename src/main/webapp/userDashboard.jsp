<%-- src/main/webapp/userDashboard.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <title>User Dashboard</title>
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>

<%@ include file="components/header.jsp" %>

<div class="container my-4">
    <h1 class="mb-3">User Dashboard</h1>
    <%-- Optional Welcome Message from Servlet --%>
    <c:if test="${not empty dashboardContent}">
        <p class="lead">${dashboardContent}</p>
    </c:if>


    <%-- Display Dashboard Loading Error Message --%>
    <c:if test="${not empty dashboardError}">
        <div class="alert alert-danger" role="alert">
            <i class="fas fa-exclamation-triangle me-2"></i> ${dashboardError}
        </div>
    </c:if>


    <%-- Reservation History Section --%>
    <hr class="my-4">
    <h2><i class="fas fa-ticket-alt me-2"></i>My Reservations</h2>


    <div class="mt-4">
        <a href="${pageContext.request.contextPath}/home" class="btn btn-secondary"><i class="fas fa-home me-1"></i> Back to Home</a>
        <a href="movies" class="btn btn-primary"><i class="fas fa-film me-1"></i> Book Another Movie</a>
    </div>

</div>

<%@ include file="components/footer.jsp" %>

<script src="js/bootstrap.bundle.min.js"></script>
</body>
</html>