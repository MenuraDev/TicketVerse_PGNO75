<%-- src/main/webapp/manageMovies.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> <%-- Add JSTL format library --%>

<html>
<head>
  <title>Manage Movies</title>
  <link rel="stylesheet" href="css/bootstrap.min.css">
  <%-- Add Font Awesome --%>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
  <style>
    /* Optional: Limit width of showtime cell if needed */
    .showtime-cell {
      max-width: 300px;
      white-space: normal; /* Allow wrapping */
    }
    .showtime-cell ul {
      padding-left: 1.2em; /* Indent list */
      margin-bottom: 0; /* Remove default bottom margin */
    }
  </style>
</head>
<body>

<%-- Include Header --%>
<%@ include file="components/header.jsp" %>

<%-- Main Content Container --%>
<div class="container mt-4">
  <h1 class="mb-3"><i class="fas fa-film me-2"></i>Manage Movies</h1>

  <%-- Feedback Messages Placeholders (Adapt session attribute names if needed) --%>
  <c:if test="${not empty sessionScope.adminMovieSuccess}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        ${sessionScope.adminMovieSuccess}
      <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <c:remove var="adminMovieSuccess" scope="session"/>
  </c:if>
  <c:if test="${not empty sessionScope.adminMovieError}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        ${sessionScope.adminMovieError}
      <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <c:remove var="adminMovieError" scope="session"/>
  </c:if>

  <%-- Add New Movie Button --%>
  <a href="movie?action=addMovie" class="btn btn-success mb-3">
    <i class="fas fa-plus me-1"></i> Add New Movie
  </a>

  <c:choose>
    <c:when test="${not empty movies}">
      <%-- Responsive Table Wrapper --%>
      <div class="table-responsive">
          <%-- Styled Table --%>
        <table class="table table-bordered table-striped table-hover align-middle">
            <%-- Styled Table Header --%>
          <thead class="table-primary">
          <tr>
            <th>ID</th>
            <th>Title</th>
            <th>Genre</th>
            <th>Duration</th>
            <th>Rating</th>
            <th>Status</th>
            <th>Showtimes</th>
            <th>Actions</th>
          </tr>
          </thead>
          <tbody>
          <c:forEach items="${movies}" var="movie">
            <tr>
              <td>${movie.id}</td>
              <td>${movie.title}</td>
              <td>${movie.genre}</td>
              <td>${movie.duration} min</td> <%-- Shortened 'minutes' --%>
              <td>${movie.rating}</td>
              <td>
                  <%-- Add badges for status for better visibility --%>
                <c:choose>
                  <c:when test="${movie.movieStatus == 'Now Showing'}">
                    <span class="badge bg-success">${movie.movieStatus}</span>
                  </c:when>
                  <c:when test="${movie.movieStatus == 'Coming Soon'}">
                    <span class="badge bg-info text-dark">${movie.movieStatus}</span>
                  </c:when>
                  <c:otherwise>
                    <span class="badge bg-secondary">${movie.movieStatus}</span>
                  </c:otherwise>
                </c:choose>
              </td>
              <td class="showtime-cell"> <%-- Apply class for potential styling --%>
                <ul class="list-unstyled mb-0"> <%-- Use list-unstyled for less default styling --%>
                  <c:forEach items="${movie.showtimes}" var="showtime">
                    <li>
                        <%-- Attempt to format the showtime --%>
                      <c:catch var="parseEx"> <%-- Catch parsing errors gracefully --%>
                        <fmt:parseDate value="${showtime}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedShowtime" />
                        <fmt:formatDate value="${parsedShowtime}" pattern="MMM d, h:mm a" />
                      </c:catch>
                      <c:if test="${not empty parseEx}"> <%-- If parsing failed, show original --%>
                        ${showtime} <%-- Fallback to original string --%>
                      </c:if>
                    </li>
                  </c:forEach>
                </ul>
              </td>
              <td>
                <a href="movie?action=editMovie&id=${movie.id}" class="btn btn-primary btn-sm me-1" title="Edit Movie"> <%-- Added title --%>
                  <i class="fas fa-edit"></i>
                </a>
                <a href="movie?action=deleteMovie&id=${movie.id}" class="btn btn-danger btn-sm" title="Delete Movie" <%-- Added title --%>
                   onclick="return confirm('Are you sure you want to delete the movie \'${movie.title}\'?');"> <%-- Improved confirmation --%>
                  <i class="fas fa-trash-alt"></i>
                </a>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </div>
    </c:when>
    <c:otherwise>
      <div class="alert alert-info" role="alert">
        No movies found in the system. You can add one using the button above.
      </div>
    </c:otherwise>
  </c:choose>

  <%-- Styled Back Button --%>
  <a href="admin" class="btn btn-secondary mt-3">
    <i class="fas fa-arrow-left me-1"></i> Back to Admin Dashboard
  </a>
</div> <%-- End Container --%>

<%-- Include Footer --%>
<%@ include file="components/footer.jsp" %>

<script src="js/bootstrap.bundle.min.js"></script>
</body>
</html>