package utils;

import model.Movie;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MovieSorter {
    
    public static void sortByReleaseDate(List<Movie> movies) {
        if (movies == null || movies.size() <= 1) {
            return;
        }

        LocalDate currentDate = LocalDate.now();

        for (int i = 1; i < movies.size(); i++) {
            Movie currentMovie = movies.get(i);
            LocalDate movieDate = currentMovie.getReleaseDate();
            int j = i - 1;

            while (j >= 0) {
                LocalDate compareDate = movies.get(j).getReleaseDate();
                
                // Handle null dates by placing them at the end
                if (compareDate == null) {
                    if (movieDate != null) {
                        movies.set(j + 1, movies.get(j));
                        j--;
                    } else {
                        break;
                    }
                } else if (movieDate == null) {
                    break;
                } else {
                    // Both dates are non-null, compare them
                    boolean isCurrentMoviePast = movieDate.isBefore(currentDate) || movieDate.isEqual(currentDate);
                    boolean isCompareMoviePast = compareDate.isBefore(currentDate) || compareDate.isEqual(currentDate);

                    if (isCurrentMoviePast && !isCompareMoviePast) {
                        // Current movie is past/current, compare movie is future - move compare movie down
                        movies.set(j + 1, movies.get(j));
                        j--;
                    } else if (!isCurrentMoviePast && isCompareMoviePast) {
                        // Current movie is future, compare movie is past/current - keep current position
                        break;
                    } else {
                        // Both movies are in the same category (both past/current or both future)
                        if (isCurrentMoviePast) {
                            // Both are past/current - sort by date (newest first)
                            if (compareDate.isBefore(movieDate)) {
                                movies.set(j + 1, movies.get(j));
                                j--;
                            } else {
                                break;
                            }
                        } else {
                            // Both are future - sort by date (earliest first)
                            if (compareDate.isAfter(movieDate)) {
                                movies.set(j + 1, movies.get(j));
                                j--;
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
            movies.set(j + 1, currentMovie);
        }
    }
} 