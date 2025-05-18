<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - TicketVerse</title>
    <link href="css/bootstrap.min.css" rel="stylesheet">  <%--Use Context Path for css --%>
</head>
<body class="bg-light">
    <!-- Server-side success alert for registration confirmation -->
    <c:if test="${sessionScope.registerSuccess == true}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <strong>Registration Successful!</strong>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="registerSuccess" scope="session"/>
    </c:if>
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <h1 class="text-center mt-5">Login</h1>

                <!-- Server-side error message display -->
                <% if (request.getAttribute("errorMessage") != null) { %>
                <div class="alert alert-danger" role="alert">
                    <strong><%= request.getAttribute("errorMessage") %></strong>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
                <% } %>

                <!-- Login form with POST action to 'login' endpoint -->
                <form action="login" method="post" class="mt-4">
                    <div class="mb-3">
                        <label for="username" class="form-label">Username</label>
                        <input type="text" class="form-control" id="username" name="username" required>
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">Password</label>
                        <input type="password" class="form-control" id="password" name="password" required>
                    </div>
                    <div class="mb-3 form-check">
                        <input type="checkbox" class="form-check-input" id="rememberMe" name="rememberMe">
                        <label class="form-check-label" for="rememberMe">Remember Me</label>
                    </div>
                    <button type="submit" class="btn btn-primary w-100">Login</button>
                    <div class="mt-3">
                        Don't have an account? <a href="register.jsp">Register</a>
                    </div>
                </form>
            </div>
        </div>
    </div>

<script src="js/bootstrap.bundle.min.js"></script>

</body>
</html>