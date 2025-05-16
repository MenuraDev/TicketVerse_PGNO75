<%-- src/main/webapp/bookTickets.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Book Tickets - ${not empty movie ? movie.title : 'Select Seats'}</title>
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css"> <%-- Font Awesome for icons --%>
</head>
<body>

<%@ include file="components/header.jsp" %>

<main class="container my-4">

    <c:choose>
        <c:when test="${not empty movie and not empty showtime}">
            <h1 class="mb-3 text-center">Book Tickets</h1>

            <div class="row justify-content-center mb-4">
                <div class="col-md-8 info-section bg-light p-3 rounded shadow-sm text-center">
                    <h4>${movie.title}</h4>
                    <fmt:parseDate value="${showtime}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedShowtime" />
                    <p><i class="fas fa-calendar-alt me-1 text-primary"></i> Showtime: <strong><fmt:formatDate value="${parsedShowtime}" pattern="EEEE, MMMM d, yyyy 'at' h:mm a" /></strong></p>
                    <p><i class="fas fa-film me-1 text-primary"></i> Genre: <strong>${movie.genre}</strong> | <i class="fas fa-clock me-1 text-primary"></i> Duration: <strong>${movie.duration} min</strong></p>
                </div>
            </div>


            <%-- Error Message Display --%>
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="fas fa-exclamation-triangle me-2"></i> ${errorMessage}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <%-- Seat Selection Form --%>
            <form id="bookingForm" action="book-tickets" method="post">
                <input type="hidden" name="movieId" value="${movie.id}">
                <input type="hidden" name="showtime" value="${showtime}">

                <div class="cinema-layout bg-dark p-4 rounded shadow">
                    <div class="screen">SCREEN</div>

                        <%-- Define seat layout (Example: 5 rows, 8 seats per row) --%>
                        <%-- Adjust rows and seats as needed --%>
                    <c:set var="rows" value="${['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H']}" />
                    <c:set var="seatsPerRow" value="${10}" />

                    <c:forEach items="${rows}" var="row">
                        <div class="seat-row">
                            <c:forEach begin="1" end="${seatsPerRow}" var="seatNum">
                                <c:set var="seatId" value="${row}${seatNum}" />
                                <%-- Check if the seat is in the bookedSeats set passed from the servlet --%>
                                <c:set var="isOccupied" value="${false}" />
                                <c:forEach items="${bookedSeats}" var="bookedSeat">
                                    <c:if test="${bookedSeat == seatId}">
                                        <c:set var="isOccupied" value="${true}" />
                                    </c:if>
                                </c:forEach>

                                <div class="seat ${isOccupied ? 'occupied' : ''}">
                                    <input type="checkbox"
                                           name="selectedSeats" <%-- Crucial: Use same name for array --%>
                                           value="${seatId}"
                                           id="seat-${seatId}"
                                        ${isOccupied ? 'disabled' : ''}
                                           onchange="toggleSeatSelection(this)">
                                    <span class="seat-label">${seatId}</span>
                                </div>
                            </c:forEach>
                        </div>
                    </c:forEach>
                </div>

                    <%-- Legend --%>
                <ul class="legend">
                    <li><div class="seat available"></div> Available</li>
                    <li><div class="seat selected"></div> Selected</li>
                    <li><div class="seat occupied"></div> Occupied</li>
                </ul>

                <hr> <%-- Separator --%>

                    <%-- Summary Area (Optional, can be updated with JS) --%>
                <div class="summary-section text-center my-3">
                    <p>Selected Seats: <span id="selected-seats-display">None</span></p>
                    <p>Total Price: $<span id="total-price-display">0.00</span></p> <%-- Add pricing logic if needed --%>
                </div>


                <div class="text-center mt-4">
                    <button type="submit" class="btn btn-lg btn-success">
                        <i class="fas fa-check-circle me-2"></i> Confirm Booking
                    </button>
                    <a href="movie-details?id=${movie.id}" class="btn btn-lg btn-secondary">
                        <i class="fas fa-arrow-left me-2"></i> Cancel
                    </a>
                </div>
            </form>

        </c:when>
    </c:choose>
</main>

<%@ include file="components/footer.jsp" %>

<script src="js/bootstrap.bundle.min.js"></script>

</body>
</html>