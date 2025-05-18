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

    <%-- Display Booking Success Message --%>
    <c:if test="${not empty sessionScope.bookingSuccess}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle me-2"></i> ${sessionScope.bookingSuccess}
            <c:if test="${not empty sessionScope.lastBookingSeatsFormatted}">
                <hr>
                <p class="mb-1"><strong>Showtime:</strong>
                    <fmt:parseDate value="${sessionScope.lastBookingShowtime}" pattern="yyyy-MM-dd'T'HH:mm" var="bst" />
                    <fmt:formatDate value="${bst}" pattern="EEEE, MMMM d 'at' h:mm a" />
                </p>
                <p class="mb-1"><strong>Seats:</strong> ${sessionScope.lastBookingSeatsFormatted}</p>
                <p class="mb-0"><strong>Reservation Time:</strong>
                    <fmt:formatDate value="${sessionScope.lastBookingTimestamp}" pattern="MMMM d, yyyy 'at' h:mm:ss a" />
                </p>
            </c:if>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="bookingSuccess" scope="session"/>
        <c:remove var="lastBookingMovieId" scope="session"/>
        <c:remove var="lastBookingShowtime" scope="session"/>
        <c:remove var="lastBookingSeatsFormatted" scope="session"/>
        <c:remove var="lastBookingTimestamp" scope="session"/>
    </c:if>

    <%-- Display Cancellation Success Message --%>
    <c:if test="${not empty sessionScope.cancelSuccess}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle me-2"></i> ${sessionScope.cancelSuccess}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="cancelSuccess" scope="session"/>
    </c:if>

    <%-- Display Cancellation Error Message --%>
    <c:if test="${not empty sessionScope.cancelError}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-triangle me-2"></i> ${sessionScope.cancelError}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="cancelError" scope="session"/>
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

    <c:choose>
        <c:when test="${not empty reservationDetails}">
            <div class="table-responsive">
                <table class="table table-striped table-hover">
                    <thead class="table-dark">
                    <tr>
                        <th>Movie Title</th>
                        <th>Showtime</th>
                        <th>Seats</th>
                        <th>Date Reservation Made</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${reservationDetails}" var="detail">
                        <tr>
                            <td>${detail.movieTitle}</td>
                            <td>
                                    <%-- Parse the showtime string and format it --%>
                                <fmt:parseDate value="${detail.showtime}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedShowtime" />
                                <fmt:formatDate value="${parsedShowtime}" pattern="MMM d, yyyy 'at' h:mm a" />
                            </td>
                            <td>${detail.seats}</td>
                            <td>
                                    <%-- Format the java.util.Date reservation time --%>
                                <fmt:formatDate value="${detail.reservationTime}" pattern="MMM d, yyyy, h:mm a" />
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${detail.reservationStatus}">
                                        <span class="badge bg-success">Approved</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-warning text-dark">Pending</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                    <%-- Form for the Cancel Button --%>
                                <form action="dashboard" method="post" style="display: inline;"> <%-- Use POST --%>
                                    <input type="hidden" name="action" value="cancelReservation">
                                    <input type="hidden" name="reservationId" value="${detail.id}"> <%-- Use the reservation ID --%>
                                    <button type="submit" class="btn btn-sm btn-danger"
                                            onclick="return confirm('Are you sure you want to cancel this reservation?');">
                                        <i class="fas fa-times me-1"></i> Cancel
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:when>
        <c:otherwise>
            <c:if test="${empty dashboardError}"> <%-- Only show this if there wasn't a loading error --%>
                <div class="alert alert-info" role="alert">
                    <i class="fas fa-info-circle me-2"></i> You have no reservations yet.
                </div>
            </c:if>
        </c:otherwise>
    </c:choose>


    <div class="mt-4">
        <a href="${pageContext.request.contextPath}/home" class="btn btn-secondary"><i class="fas fa-home me-1"></i> Back to Home</a>
        <a href="movies" class="btn btn-primary"><i class="fas fa-film me-1"></i> Book Another Movie</a>
    </div>
    <c:forEach items="${reservationDetails}" var="detail">
        <tr>
            <td>${detail.movieTitle}</td>
            <td>
                    <%-- Parse the showtime string and format it --%>
                <fmt:parseDate value="${detail.showtime}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedShowtime" />
                <fmt:formatDate value="${parsedShowtime}" pattern="MMM d, yyyy 'at' h:mm a" />
            </td>
            <td>${detail.seats}</td>
            <td>
                    <%-- Format the java.util.Date reservation time --%>
                <fmt:formatDate value="${detail.reservationTime}" pattern="MMM d, yyyy, h:mm a" />
            </td>
            <td>
                <c:choose>
                    <c:when test="${detail.reservationStatus}">
                        <span class="badge bg-success">Approved</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge bg-warning text-dark">Pending</span>
                    </c:otherwise>
                </c:choose>
            </td>
            <td>
                    <%-- Form for the Cancel Button --%>

            </td>
        </tr>
    </c:forEach>

</div>

<%@ include file="components/footer.jsp" %>

<script src="js/bootstrap.bundle.min.js"></script>
</body>
</html>