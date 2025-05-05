<%-- src/main/webapp/manageReservations.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <title>Manage Reservations</title>
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>

<%@ include file="components/header.jsp" %> <%-- Assuming admin sees same header or you have admin-specific one --%>

<div class="container mt-4">
    <h1 class="mb-3"><i class="fas fa-clipboard-list me-2"></i>All User Reservations</h1>

    <%-- Display potential feedback messages from POST actions (if implemented) --%>
    <c:if test="${not empty sessionScope.adminReservationSuccess}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${sessionScope.adminReservationSuccess}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="adminReservationSuccess" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.adminReservationError}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${sessionScope.adminReservationError}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="adminReservationError" scope="session"/>
    </c:if>




    <a href="admin" class="btn btn-secondary mt-3"><i class="fas fa-arrow-left me-1"></i> Back to Admin Dashboard</a>

</div>

<%@ include file="components/footer.jsp" %>

<script src="js/bootstrap.bundle.min.js"></script>
</body>
</html>