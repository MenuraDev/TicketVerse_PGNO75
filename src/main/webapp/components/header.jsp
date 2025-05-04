<!-- header.jsp -->
<header>
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
        <div class="container-fluid">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/home">TicketVerse</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNavDropdown"
                    aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNavDropdown">
                <ul class="navbar-nav">
                    <li class="nav-item">
                        <a class="nav-link active" aria-current="page" href="${pageContext.request.contextPath}/home">Home</a>
                    </li>
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="navbarDropdownMenuLink" role="button"
                           data-bs-toggle="dropdown" aria-expanded="false">
                            Movies
                        </a>
                        <ul class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
                            <li><a class="dropdown-item" href="movies">All Movies</a></li>
                            <li><a class="dropdown-item" href="movies?status=nowshowing">Now Showing</a></li>
                            <li><a class="dropdown-item" href="movies?status=comingsoon">Coming Soon</a></li>
                        </ul>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#">Showtimes</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="index.jsp#about">About Us</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="index.jsp#contact">Contact</a>
                    </li>
                </ul>

                <ul class="navbar-nav ms-auto">
                    <%-- <c:out value="${sessionScope.user}" />--%>
                    <c:if test="${not empty sessionScope.user}">
                        <li class="navbar-text me-3">
                            <h6>Welcome Back, ${sessionScope.user.username}!</h6>
                        </li>
                    </c:if>
                    <c:choose>
                        <c:when test="${not empty sessionScope.user}">
                            <li class="nav-item">
                                <a href="dashboard" class="btn btn-info me-3">Dashboard</a>
                            </li>
                            <li class="nav-item">
                                <a href="logout" class="btn btn-danger me-3">Logout</a>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <li class="nav-item">
                                <a href="login.jsp" class="btn btn-primary me-3">Login</a>
                            </li>
                            <li class="nav-item">
                                <a href="register.jsp" class="btn btn-primary me-3">Register</a>
                            </li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>
        </div>
    </nav>
</header>
