<%-- src/main/webapp/movieDetails.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> <%-- For date/time formatting (optional) --%>

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
    </c:choose>
</main>



<%@ include file="components/footer.jsp" %>

<script src="js/bootstrap.bundle.min.js"></script>
</body>
</html>