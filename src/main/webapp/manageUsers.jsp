<%-- src/main/webapp/manageUsers.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> <%-- Import JSTL format library --%>

<html>
<head>
    <title>Manage Users</title>
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <%-- Add Font Awesome --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>

<%-- Include Header --%>
<%@ include file="components/header.jsp" %>

<%-- Main Content Container --%>
<div class="container mt-4">
    <h1 class="mb-3"><i class="fas fa-users-cog me-2"></i>Manage Users</h1>

    <%-- Feedback Messages Placeholders --%>
    <c:if test="${not empty sessionScope.adminUserSuccess}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${sessionScope.adminUserSuccess}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="adminUserSuccess" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.adminUserError}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${sessionScope.adminUserError}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="adminUserError" scope="session"/>
    </c:if>

    <%-- Add New User Button --%>
    <a href="admin?action=addUser" class="btn btn-success mb-3">
        <i class="fas fa-user-plus me-1"></i> Add New User
    </a>

    <c:choose>
        <c:when test="${not empty users}">
            <%-- Responsive Table Wrapper --%>
            <div class="table-responsive">
                    <%-- Styled Table - Changed from table-dark for consistency --%>
                <table class="table table-bordered table-striped table-hover align-middle">
                        <%-- Styled Table Header --%>
                    <thead class="table-primary">
                    <tr>
                        <th>ID</th>
                        <th>Username</th>
                        <th>Email</th>
                        <th>First Name</th>
                        <th>Last Name</th>
                        <th>Role</th>
                        <th>Created At</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${users}" var="user">
                        <tr>
                            <td>${user.id}</td>
                            <td>${user.username}</td>
                            <td>${user.email}</td>
                            <td>${user.firstName}</td>
                            <td>${user.lastName}</td>
                            <td>
                                    <%-- Add badges for roles --%>
                                <c:choose>
                                    <c:when test="${user.role == 'admin'}">
                                        <span class="badge bg-danger"><i class="fas fa-user-shield me-1"></i> ${user.role}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-secondary"><i class="fas fa-user me-1"></i> ${user.role}</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                    <%-- Format the LocalDateTime createdAt --%>
                                    <%-- Need to convert LocalDateTime to Date for fmt:formatDate --%>
                                    <%-- Alternatively, handle formatting in servlet or use a different taglib if available --%>
                                    <%-- Simple display for now if conversion is complex here --%>
                                <c:catch var="formatEx">
                                    <jsp:useBean id="createdAtDate" class="java.util.Date"/>
                                    <jsp:setProperty name="createdAtDate" property="time"
                                                     value="${user.createdAt.atZone(pageContext.request.servletContext.getAttribute('javax.servlet.jsp.jstl.fmt.timeZone') != null ? pageContext.request.servletContext.getAttribute('javax.servlet.jsp.jstl.fmt.timeZone') : java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()}"/>
                                    <fmt:formatDate value="${createdAtDate}" pattern="MMM d, yyyy, h:mm a" />
                                </c:catch>
                                <c:if test="${not empty formatEx}">
                                    ${user.createdAt} <%-- Fallback to default toString() --%>
                                </c:if>

                            </td>
                            <td>
                                <a href="admin?action=editUser&id=${user.id}" class="btn btn-primary btn-sm me-1" title="Edit User">
                                    <i class="fas fa-user-edit"></i>
                                </a>
                                <a href="admin?action=deleteUser&id=${user.id}" class="btn btn-danger btn-sm" title="Delete User"
                                   onclick="return confirm('Are you sure you want to delete user \'${user.username}\'?');"> <%-- Improved confirmation --%>
                                    <i class="fas fa-user-times"></i>
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
                No users found in the system. You can add one using the button above.
            </div>
        </c:otherwise>
    </c:choose>

    <%-- Styled Back Button --%>
    <a href="admin?action=adminDashboard" class="btn btn-secondary mt-3">
        <i class="fas fa-arrow-left me-1"></i> Back to Admin Dashboard
    </a>
</div> <%-- End Container --%>

<%-- Include Footer --%>
<%@ include file="components/footer.jsp" %>

<script src="js/bootstrap.bundle.min.js"></script>
</body>
</html>