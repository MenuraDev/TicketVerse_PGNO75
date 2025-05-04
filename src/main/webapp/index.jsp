<%@ page contentType="text/html; charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="css/bootstrap.min.css">
  <%-- Font Awesome if movieCard.jsp uses it --%>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
  <title>TicketVerse</title>
  <style>
    .hero {
      /* background-image: url('YOUR_HERO_IMAGE_URL'); */ /* Add your hero image */
      background-color: #333; /* Placeholder background */
      background-size: cover;
      height: 400px; /* Adjusted height */
      background-position: center;
    }
    /* Add styles from movieCard if needed directly here or ensure CSS is linked */
    .movie-card img {
      height: 450px; /* Example fixed height */
      object-fit: cover; /* Cover the area, might crop */
    }
    .movie-card .card-body { min-height: 150px; } /* Ensure consistent card body height */
  </style>
</head>
<body>
<!-- Login Success Alert -->
<c:if test="${sessionScope.loginSuccess == true}">
  <div class="alert alert-success alert-dismissible fade show" role="alert">
    <strong>Welcome Back, ${sessionScope.user.username}!</strong> You have successfully logged in!
    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
  </div>
  <c:remove var="loginSuccess" scope="session"/>
</c:if>

<%-- Display potential error from IndexServlet --%>
<c:if test="${not empty requestScope.indexPageError}">
  <div class="alert alert-warning" role="alert">
      ${requestScope.indexPageError}
  </div>
</c:if>


<%@ include file="components/header.jsp" %>

<main>

  <!-- Hero Section -->
  <div class="hero">
    <div class="h-100 d-flex align-items-center justify-content-center bg-dark bg-opacity-50">
      <div class="text-center text-white p-3"> <%-- Added padding --%>
        <h1 class="display-4 fw-bold">Welcome to TicketVerse</h1> <%-- Adjusted size --%>
        <p class="lead">Experience the magic of cinema</p>
        <a href="movies" class="btn btn-primary btn-lg mt-3">Browse All Movies</a> <%-- Changed text --%>
      </div>
    </div>
  </div>

  <!-- Featured Movies Section -->
  <section class="container my-5">

    <!-- Now Showing Section -->
    <div class="mb-5">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="text-primary fw-bold mb-0">Now Showing</h2> <%-- Removed margin bottom --%>
        <a href="movies?status=nowshowing" class="btn btn-outline-primary btn-sm">
          View All <i class="fas fa-chevron-right ms-1"></i> <%-- Adjusted icon margin --%>
        </a>
      </div>

      <%-- Check if the list is empty --%>
      <c:choose>
        <c:when test="${not empty latestNowShowing}">
          <div class="row g-4">
              <%-- Iterate over the LATEST Now Showing movies from IndexServlet --%>
            <c:forEach items="${latestNowShowing}" var="movie">
              <%@ include file="components/movieCard.jsp" %>
            </c:forEach>
          </div>
        </c:when>
        <c:otherwise>
          <div class="alert alert-info">No movies currently showing.</div>
        </c:otherwise>
      </c:choose>
    </div>

    <!-- Coming Soon Section -->
    <div class="mb-5">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="text-primary fw-bold mb-0">Coming Soon</h2>
        <a href="movies?status=comingsoon" class="btn btn-outline-primary btn-sm">
          View All <i class="fas fa-chevron-right ms-1"></i>
        </a>
      </div>

      <%-- Check if the list is empty --%>
      <c:choose>
        <c:when test="${not empty latestComingSoon}">
          <div class="row g-4">
              <%-- Iterate over the LATEST Coming Soon movies from IndexServlet --%>
            <c:forEach items="${latestComingSoon}" var="movie">
              <%@ include file="components/movieCard.jsp" %>
            </c:forEach>
          </div>
        </c:when>
        <c:otherwise>
          <div class="alert alert-info">No movies announced as coming soon yet.</div>
        </c:otherwise>
      </c:choose>
    </div>

  </section>

  <!-- About Us Section -->
  <section id="about" class="py-5 bg-light">
    <%-- ... existing About Us content ... --%>
    <div class="container">
      <div class="row align-items-center">
        <div class="col-lg-6 mb-4 mb-lg-0">
          <img src="images/AboutUs.png"
               class="img-fluid rounded shadow-lg"
               alt="Modern cinema hall with comfortable seats">
        </div>
        <div class="col-lg-6 ps-lg-5">
          <h2 class="display-5 fw-bold mb-4 text-primary">About TicketVerse</h2>
          <p class="lead text-muted">
            Welcome to TicketVerse, where cinema comes alive. We're redefining the movie-watching experience with cutting-edge technology and unparalleled comfort.
          </p>
          <%-- ... rest of about section ... --%>
          <a href="movies" class="btn btn-primary btn-lg mt-4">Book Your Experience</a>
        </div>
      </div>
    </div>
  </section>

  <!-- Contact Section -->
  <section id="contact" class="py-5">
    <%-- ... existing Contact content ... --%>
    <div class="container">
      <h2 class="display-5 fw-bold text-center mb-5 text-primary">Contact Us</h2>
      <%-- ... rest of contact section ... --%>
    </div>
  </section>
</main>

<%@ include file="components/footer.jsp" %>

<script src="js/bootstrap.bundle.min.js"></script>
</body>
</html>