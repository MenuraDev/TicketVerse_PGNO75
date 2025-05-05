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


    <c:choose>
        <c:when test="${not empty allReservationDetails}">
            <div class="table-responsive">
                <table class="table table-bordered table-striped table-hover">
                    <thead class="table-primary">
                    <tr>
                        <th>Res. ID</th>
                        <th>User</th>
                        <th>Movie Title</th>
                        <th>Showtime</th>
                        <th>Seats</th>
                        <th>Reservation Date</th>
                            <%-- Optional: Add Admin Actions --%>
                            <%-- <th>Actions</th> --%>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${allReservationDetails}" var="detail">
                        <tr>
                            <td>${detail.reservationId}</td>
                            <td>${detail.username} (ID: ${detail.userId})</td>
                            <td>${detail.movieTitle}</td>
                            <td>
                                <fmt:parseDate value="${detail.showtime}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedShowtime" />
                                <fmt:formatDate value="${parsedShowtime}" pattern="MMM d, yyyy 'at' h:mm a" />
                            </td>
                            <td style="max-width: 200px; word-wrap: break-word;">${detail.seats}</td> <%-- Allow wrapping for many seats --%>
                            <td>
                                <fmt:formatDate value="${detail.reservationTime}" pattern="MMM d, yyyy, h:mm:ss a" />
                            </td>
                                <%-- <td>
                                    <%-- Example Admin Delete Action Form --%>
                                <%--
                                <form action="admin" method="post" style="display: inline;">
                                    <input type="hidden" name="action" value="adminDeleteReservation">
                                    <input type="hidden" name="reservationId" value="${detail.reservationId}">
                                    <button type="submit" class="btn btn-sm btn-outline-danger"
                                            onclick="return confirm('ADMIN ACTION: Permanently delete reservation ${detail.reservationId}?');">
                                        <i class="fas fa-trash-alt"></i>
                                    </button>
                                </form>
                                --%>
                                <%-- </td> --%>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:when>
        <c:otherwise>
            <div class="alert alert-info" role="alert">
                No reservations have been made by any user yet.
            </div>
        </c:otherwise>
    </c:choose>

    <a href="admin" class="btn btn-secondary mt-3"><i class="fas fa-arrow-left me-1"></i> Back to Admin Dashboard</a>

</div>

<%@ include file="components/footer.jsp" %>

<script src="js/bootstrap.bundle.min.js"></script>
</body>
</html>