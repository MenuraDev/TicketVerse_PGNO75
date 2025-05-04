<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Add New Movie</title>
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <style>
        .showtime-group {
            margin-bottom: 10px;
        }
    </style>
</head>
<body>
<h1>Add New Movie</h1>
<% if (request.getAttribute("errorMessage") != null) { %>
<div class="alert alert-danger" role="alert">
    <strong><%= request.getAttribute("errorMessage") %></strong>
    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
</div>
<% } %>
<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <form action="movie?action=saveMovie" method="post">
                <div class="mb-3">
                    <label for="title" class="form-label">Title:</label>
                    <input type="text" class="form-control" id="title" name="title" required>
                </div>
                <div class="mb-3">
                    <label for="poster" class="form-label">Poster URL:</label>
                    <input type="text" class="form-control" id="poster" name="poster" required>
                </div>
                <div class="mb-3">
                    <label for="synopsis" class="form-label">Synopsis:</label>
                    <textarea class="form-control" id="synopsis" name="synopsis" rows="3" required></textarea>
                </div>
                <div class="mb-3">
                    <label for="genre" class="form-label">Genre:</label>
                    <input type="text" class="form-control" id="genre" name="genre" required>
                </div>
                <div class="mb-3">
                    <label for="duration" class="form-label">Duration (minutes):</label>
                    <input type="number" class="form-control" id="duration" name="duration" required>
                </div>
                <div class="mb-3">
                    <label for="rating" class="form-label">Rating:</label>
                    <input type="text" class="form-control" id="rating" name="rating" required>
                </div>
                <div class="mb-3">
                    <label for="director" class="form-label">Director:</label>
                    <input type="text" class="form-control" id="director" name="director" required>
                </div>
                <div class="mb-3">
                    <label for="cast" class="form-label">Cast:</label>
                    <input type="text" class="form-control" id="cast" name="cast" required>
                </div>
                <div class="mb-3">
                    <label for="trailerURL" class="form-label">Trailer URL (optional):</label>
                    <input type="text" class="form-control" id="trailerURL" name="trailerURL">
                </div>

                <div class="mb-3">
                    <label for="movieStatus" class="form-label">Movie Status:</label>
                    <select class="form-select" id="movieStatus" name="movieStatus" required>
                        <option value="Coming Soon">Coming Soon</option>
                        <option value="Now Showing">Now Showing</option>
                    </select>
                </div>

                <div class="mb-3">
                    <label class="form-label">Show Dates and Times:</label>
                    <div id="showtimes-container">
                        <div class="showtime-group">
                            <input type="datetime-local" class="form-control" name="showtimes">
                        </div>
                    </div>
                    <button type="button" class="btn btn-secondary mt-2" onclick="addShowtimeField()">Add Show Time</button>
                </div>

                <button type="submit" class="btn btn-primary">Add Movie</button>
                <a href="admin" class="btn btn-secondary">Cancel</a>
            </form>
        </div>
    </div>
</div>

<script>
    function addShowtimeField() {
        const container = document.getElementById('showtimes-container');
        const newShowtimeGroup = document.createElement('div');
        newShowtimeGroup.classList.add('showtime-group');

        const newInput = document.createElement('input');
        newInput.type = 'datetime-local';
        newInput.classList.add('form-control');
        newInput.name = 'showtimes';

        newShowtimeGroup.appendChild(newInput);
        container.appendChild(newShowtimeGroup);
    }
</script>
<script src="js/bootstrap.bundle.min.js"></script>
</body>
</html>