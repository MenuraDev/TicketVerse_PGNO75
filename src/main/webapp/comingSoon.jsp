<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Coming Soon</title>
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<%@ include file="components/header.jsp" %>

<main>

    <h2 class="text-primary fw-bold">Coming Soon</h2>

    <section class="container my-5">
        <div class="row g-4">
            <c:forEach items="${movies}" var="movie">
                <%@ include file="components/movieCard.jsp" %>
            </c:forEach>
        </div>
    </section>

</main>

<%@ include file="components/footer.jsp" %>

<script src="js/bootstrap.bundle.min.js"></script>
<script src="js/script.js"></script>
</body>
</html>