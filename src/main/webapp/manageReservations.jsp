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

    <%-- Circular Queue Status Section --%>
    <div class="card mb-4">
        <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
            <h5 class="mb-0"><i class="fas fa-circle-notch me-2"></i>Reservation Queue Status</h5>
            <form action="admin" method="post" onsubmit="return confirm('Are you sure you want to approve all reservations? This will clear the queue.');">
                <input type="hidden" name="action" value="approveAllReservations">
                <button type="submit" class="btn btn-success btn-sm">
                    <i class="fas fa-check-double me-1"></i>Approve All Reservations
                </button>
            </form>
        </div>
        <div class="card-body">
            <div class="row">
                <div class="col-md-6">
                    <p><strong>Queue Size:</strong> ${queueSize}</p>
                    <p><strong>Queue Status:</strong> 
                        <c:choose>
                            <c:when test="${queueSize == 0}">
                                <span class="badge bg-secondary">Empty</span>
                            </c:when>
                            <c:when test="${queueSize == 100}">
                                <span class="badge bg-danger">Full</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge bg-success">Active</span>
                            </c:otherwise>
                        </c:choose>
                    </p>
                </div>
                <div class="col-md-6 text-end">
                    <c:if test="${queueSize > 0}">
                        <form action="admin" method="post" class="d-inline">
                            <input type="hidden" name="action" value="removeFromQueue">
                            <button type="submit" class="btn btn-warning">
                                <i class="fas fa-user-check me-1"></i>Approve This Reservation
                            </button>
                        </form>
                    </c:if>
                </div>
            </div>

            <%-- Display Current Reservation Details --%>
            <div class="mt-3">
                <h6>Current Reservation to Approve:</h6>
                <c:choose>
                    <c:when test="${not empty currentQueueReservation}">
                        <div class="alert alert-info">
                            <strong>Res. ID:</strong> ${currentQueueReservation.reservationId}<br>
                            <strong>User:</strong> ${currentQueueReservation.username} (ID: ${currentQueueReservation.userId})<br>
                            <strong>Movie:</strong> ${currentQueueReservation.movieTitle}<br>
                            <strong>Showtime:</strong> 
                            <fmt:parseDate value="${currentQueueReservation.showtime}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedShowtimeQ" />
                            <fmt:formatDate value="${parsedShowtimeQ}" pattern="MMM d, yyyy 'at' h:mm a" /><br>
                            <strong>Seats:</strong> ${currentQueueReservation.seats}<br>
                            <strong>Reservation Date:</strong> <fmt:formatDate value="${currentQueueReservation.reservationTime}" pattern="MMM d, yyyy, h:mm:ss a" />
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-success mb-0">All reservations have been approved! No pending reservations in the queue.</div>
                    </c:otherwise>
                </c:choose>
            </div>

            <%-- Display Queue Contents --%>
            <div class="mt-3">
                <h6>Current Queue Contents (Oldest to Newest):</h6>
                <div class="queue-display">
                    <c:choose>
                        <c:when test="${not empty queueTickets}">
                            <div class="d-flex flex-wrap gap-2">
                                <c:forEach items="${queueTickets}" var="ticketId">
                                    <span class="badge bg-info">Ticket #${ticketId}</span>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <p class="text-muted">No tickets in queue</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>

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
                        <th>Status</th>
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