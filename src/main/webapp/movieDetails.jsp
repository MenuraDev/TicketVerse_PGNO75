<%-- src/main/webapp/movieDetails.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> <%-- For date/time formatting (optional) --%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${not empty movie ? movie.title : 'Movie Details'} - TicketVerse</title>
    <link rel="stylesheet" href="css/bootstrap.min.css">
</head>
<body>

<%@ include file="components/header.jsp" %>

<main class="container my-5 py-5 bg-dark-subtle rounded-3 shadow-lg">
    <c:choose>
        <c:when test="${not empty movie}">
            <div class="row g-4">
                <!-- Movie Poster Column -->
                <div class="col-12 col-md-4">
                    <div class="poster-wrapper rounded-4 overflow-hidden position-relative">
                        <img src="${movie.poster}" alt="${movie.title} Poster"
                             class="w-100 h-100 object-fit-cover transition-all hover-scale">
                        <div class="position-absolute top-0 end-0 m-3">
                            <span class="badge bg-gradient fs-6 px-3 py-2">
                                    ${movie.movieStatus}
                            </span>
                        </div>
                    </div>
                </div>

                <!-- Movie Details Column -->
                <div class="col-12 col-md-8">
                    <div class="p-4 bg-white rounded-4 shadow-sm">
                        <h1 class="display-5 fw-bold text-primary mb-4">${movie.title}</h1>

                        <div class="details-grid mb-4">
                            <div class="d-flex align-items-center mb-3">
                                <i class="fas fa-film fs-4 me-3 text-primary"></i>
                                <div>
                                    <h6 class="text-uppercase text-muted mb-1">Genre</h6>
                                    <p class="mb-0 fs-5">${movie.genre}</p>
                                </div>
                            </div>
                            <div class="d-flex align-items-center mb-3">
                                <i class="fas fa-clock fs-4 me-3 text-primary"></i>
                                <div>
                                    <h6 class="text-uppercase text-muted mb-1">Duration</h6>
                                    <p class="mb-0 fs-5">${movie.duration} min</p>
                                </div>
                            </div>
                            <div class="d-flex align-items-center mb-3">
                                <i class="fas fa-star fs-4 me-3 text-warning"></i>
                                <div>
                                    <h6 class="text-uppercase text-muted mb-1">Rating</h6>
                                    <p class="mb-0 fs-5">${movie.rating}/10</p>
                                </div>
                            </div>
                            <div class="d-flex align-items-center mb-3">
                                <i class="fas fa-calendar fs-4 me-3 text-primary"></i>
                                <div>
                                    <h6 class="text-uppercase text-muted mb-1">Release Date</h6>
                                    <p class="mb-0 fs-5">
                                        <c:choose>
                                            <c:when test="${not empty movie.releaseDate}">
                                                <c:set var="releaseDateStr" value="${movie.releaseDate.toString()}" />
                                                <fmt:parseDate value="${releaseDateStr}" pattern="yyyy-MM-dd" var="parsedDate" />
                                                <fmt:formatDate value="${parsedDate}" pattern="MMMM d, yyyy" />
                                            </c:when>
                                            <c:otherwise>
                                                Not available
                                            </c:otherwise>
                                        </c:choose>
                                    </p>
                                </div>
                            </div>
                        </div>

                        <hr class="my-5 border-primary border-opacity-50">

                        <div class="synopsis mb-3">
                            <h6 class="text-success mb-3">Director : ${movie.director}</h6>
                            <h6 class="text-success mb-3">Cast : ${movie.cast}</h6>
                        </div>

                        <hr class="my-5 border-primary border-opacity-50">

                        <div class="synopsis mb-5">
                            <h4 class="text-primary mb-3">Story Line</h4>
                            <p class="lead">${movie.synopsis}</p>
                        </div>

                        <hr class="my-5 border-primary border-opacity-50">

                        <c:if test="${movie.movieStatus == 'Now Showing'}">
                            <div class="showtimes mb-5">
                                <h4 class="text-primary mb-4">Available Showtimes</h4>
                                <div class="d-flex flex-wrap gap-3">
                                    <c:forEach items="${movie.showtimes}" var="showtime">
                                        <div class="showtime-item bg-light p-3 rounded-3 shadow-sm">
                                            <i class="fas fa-calendar-alt me-2 text-primary"></i>
                                                <%-- Format the showtime --%>
                                            <fmt:parseDate value="${showtime}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedShowtime" />
                                            <span class="fw-semibold">
                                                <fmt:formatDate value="${parsedShowtime}" pattern="EEEE, MMMM d 'at' h:mm a" />
                                            </span>
                                                <%-- Only show Book Now if user is logged in --%>
                                            <c:if test="${not empty sessionScope.user}">
                                                <%-- Encode the showtime parameter for the URL --%>
                                                <c:url var="bookUrl" value="book-tickets">
                                                    <c:param name="movieId" value="${movie.id}" />
                                                    <c:param name="showtime" value="${showtime}" />
                                                    <%-- userId will be retrieved from session in the servlet --%>
                                                </c:url>
                                                <a href="${bookUrl}" class="btn btn-sm btn-success ms-3">
                                                    <i class="fas fa-ticket-alt me-1"></i> Book Now
                                                </a>
                                            </c:if>
                                            <c:if test="${empty sessionScope.user}">
                                                <a href="login.jsp?redirect=movie-details?id=${movie.id}" class="btn btn-sm btn-outline-secondary ms-3 disabled" title="Login to Book">
                                                    <i class="fas fa-ticket-alt me-1"></i> Book Now (Login Required)
                                                </a>
                                                <%-- Alternatively, disable or hide the button --%>
                                                <%-- <button class="btn btn-sm btn-success ms-3" disabled>Book Now (Login Required)</button> --%>
                                            </c:if>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>
                        </c:if>

                        <c:if test="${not empty movie.trailerURL}">
                            <div class="trailer ratio ratio-16x9 rounded-4 overflow-hidden">
                                <iframe
                                        width="560"
                                        height="315"
                                        src="https://www.youtube.com/embed/${movie.trailerURL}"
                                        title="YouTube video player"
                                        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
                                        referrerpolicy="strict-origin-when-cross-origin"
                                        allowfullscreen>
                                </iframe>
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <h4 class="alert-heading d-flex align-items-center">
                    <i class="fas fa-exclamation-triangle me-2 fs-3"></i> Error Loading Movie
                </h4>
                <p class="mb-0">${not empty errorMessage ? errorMessage : 'Could not retrieve movie details.'}</p>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                <div class="mt-3">
                    <a href="movies" class="btn btn-outline-danger">
                        <i class="fas fa-arrow-left me-2"></i> Back to Movies
                    </a>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</main>

<style>
    /* Custom CSS Enhancements */
    .poster-wrapper {
        height: 500px;
    }

    .hover-scale:hover {
        transform: scale(1.05);
        transition: transform 0.3s ease;
    }

    .details-grid {
        display: grid;
        grid-template-columns: repeat(3, 1fr);
        gap: 2rem;
    }

    .showtime-item {
        transition: all 0.3s ease;
    }

    .showtime-item:hover {
        transform: translateY(-3px);
        box-shadow: 0 0.5rem 1rem rgba(0,0,0,0.15);
    }

    .badge-gradient {
        background: linear-gradient(135deg, #ff6b6b, #4ecdc4);
        color: white !important;
    }

    .trailer iframe {
        border: 5px solid #fff;
        box-shadow: 0 0 20px rgba(0,0,0,0.1);
    }

    @media (max-width: 768px) {
        .details-grid {
            grid-template-columns: 1fr;
        }
    }
</style>

<%@ include file="components/footer.jsp" %>

<script src="js/bootstrap.bundle.min.js"></script>
</body>
</html>