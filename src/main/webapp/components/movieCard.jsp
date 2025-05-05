<div class="col-md-6 col-lg-4">
    <div class="movie-card card border-0 shadow-lg h-100 bg-dark text-white">
        <div class="position-relative overflow-hidden">
            <img
                    src="${movie.poster}"
                    class="card-img-top rounded-top img-fluid"
                    alt="${movie.title} Poster"
            />
            <div class="poster-overlay"></div>

            <div class="position-absolute top-0 start-0 m-3">
                                <span class="badge bg-info movie-badge">
                                    ${movie.movieStatus}
                                </span>
            </div>

            <div class="position-absolute bottom-0 end-0 m-3">
                                <span class="badge bg-danger movie-badge">
                                    ${movie.genre}
                                </span>
            </div>

            <div class="position-absolute bottom-0 start-0 m-3">
                                <span class="badge bg-warning movie-badge">
                                    <i class="fas fa-star text-white"></i> ${movie.rating}
                                </span>
            </div>
        </div>

        <div class="card-body d-flex flex-column">
            <h5 class="card-title fw-bold mb-2 text-truncate">
                ${movie.title}
            </h5>

            <p class="card-text text-muted small mb-3">
                Directed by ${movie.director}
            </p>

            <div class="mt-auto">
                <c:choose>
                    <c:when test="${movie.movieStatus == 'Now Showing'}">
                        <a
                                href="movie-details?id=${movie.id}"
                                class="btn btn-primary ticket-btn w-100 d-flex align-items-center justify-content-center"
                        >
                            <i class="fas fa-ticket-alt me-2"></i>
                            Book Tickets
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a
                                href="movie-details?id=${movie.id}"
                                class="btn btn-outline-light ticket-btn w-100 d-flex align-items-center justify-content-center"
                        >
                            <i class="fas fa-info-circle me-2"></i>
                            View Details
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>
