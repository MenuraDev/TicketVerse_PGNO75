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
    <style>
        .seat {
            display: inline-block;
            width: 40px;
            height: 35px;
            margin: 3px;
            background-color: #444451; /* Available */
            border-top-left-radius: 10px;
            border-top-right-radius: 10px;
            position: relative; /* For checkbox positioning */
            cursor: pointer;
            transition: background-color 0.3s ease;
        }
        .seat.selected {
            background-color: #6feaf6; /* Selected */
        }
        .seat.occupied {
            background-color: #ff0000; /* Occupied */
            cursor: not-allowed;
        }
        .seat input[type="checkbox"] {
            position: absolute;
            opacity: 0; /* Hide the actual checkbox */
            width: 100%;
            height: 100%;
            cursor: pointer;
        }
        .seat.occupied input[type="checkbox"] {
            cursor: not-allowed;
        }

        .seat-label {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            color: white;
            font-size: 0.8em;
            font-weight: bold;
            pointer-events: none; /* Label doesn't block checkbox click */
        }

        .screen {
            background-color: #fff;
            height: 70px;
            width: 80%;
            margin: 15px auto; /* Center the screen */
            transform: rotateX(-45deg);
            box-shadow: 0 3px 10px rgba(255, 255, 255, 0.7);
            text-align: center;
            line-height: 70px; /* Vertically center text */
            font-weight: bold;
        }

        .cinema-layout {
            perspective: 1000px; /* For 3D effect on screen */
            margin-bottom: 30px;
            text-align: center; /* Center the rows */
        }
        .seat-row {
            margin-bottom: 5px;
        }
        .legend {
            list-style: none;
            padding: 0;
            display: flex;
            justify-content: center;
            margin-top: 20px;
            gap: 20px; /* Space between legend items */
        }
        .legend li {
            display: flex;
            align-items: center;
        }
        .legend .seat { /* Use seat styles for legend items */
            width: 20px;
            height: 18px;
            margin: 0 5px 0 0; /* Adjust margin */
            cursor: default; /* Legend seats not clickable */
            border-radius: 5px; /* Smaller radius for legend */
        }
        .legend .seat.occupied { background-color: #ff0000; }
        .legend .seat.selected { background-color: #6feaf6; }
        .legend .seat.available { background-color: #444451; } /* Explicit available color */

        .info-section p { margin-bottom: 5px; }
        .info-section strong { color: #0d6efd; } /* Bootstrap primary color */

    </style>
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
        <c:otherwise>
            <div class="alert alert-warning" role="alert">
                <h4 class="alert-heading"><i class="fas fa-exclamation-triangle me-2"></i> Missing Information</h4>
                <p>Could not load booking details. Please select a movie and showtime first.</p>
                <hr>
                <p class="mb-0">
                    <a href="movies" class="btn btn-primary"><i class="fas fa-film me-1"></i> Browse Movies</a>
                </p>
            </div>
        </c:otherwise>
    </c:choose>
</main>

<%@ include file="components/footer.jsp" %>

<script src="js/bootstrap.bundle.min.js"></script>
<script>
    const seatPrice = 10.00; // Example price per seat
    const selectedSeatsDisplay = document.getElementById('selected-seats-display');
    const totalPriceDisplay = document.getElementById('total-price-display');
    let currentSelectedSeats = []; // Keep track of selected seat IDs

    function toggleSeatSelection(checkbox) {
        const seatDiv = checkbox.parentElement;
        const seatId = checkbox.value;

        if (checkbox.checked) {
            seatDiv.classList.add('selected');
            if (!currentSelectedSeats.includes(seatId)) {
                currentSelectedSeats.push(seatId);
            }
        } else {
            seatDiv.classList.remove('selected');
            currentSelectedSeats = currentSelectedSeats.filter(id => id !== seatId);
        }
        updateSummary();
    }

    function updateSummary() {
        // Sort seats for consistent display, e.g., A1, A10, B2
        currentSelectedSeats.sort((a, b) => {
            const rowA = a.charAt(0);
            const numA = parseInt(a.substring(1));
            const rowB = b.charAt(0);
            const numB = parseInt(b.substring(1));

            if (rowA < rowB) return -1;
            if (rowA > rowB) return 1;
            return numA - numB;
        });


        if (currentSelectedSeats.length === 0) {
            selectedSeatsDisplay.textContent = 'None';
            totalPriceDisplay.textContent = '0.00';
        } else {
            selectedSeatsDisplay.textContent = currentSelectedSeats.join(', ');
            totalPriceDisplay.textContent = (currentSelectedSeats.length * seatPrice).toFixed(2);
        }
    }

    // Initialize summary on page load (in case of errors preserving selections - basic example)
    document.addEventListener('DOMContentLoaded', () => {
        const form = document.getElementById('bookingForm');
        if(form) {
            const checkboxes = form.querySelectorAll('input[name="selectedSeats"]:checked');
            checkboxes.forEach(cb => {
                if (!cb.disabled) { // Only count initially checked if it wasn't occupied
                    toggleSeatSelection(cb); // Visually mark and update summary
                }
            });
        }
    });

</script>
</body>
</html>