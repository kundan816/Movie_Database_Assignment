package org.example;

import models.Movie;
import models.Actor;
import models.Director;
import utils.CSVReader;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    // In-memory storage for Movies, Actors, and Directors
    static Map<String, Movie> movies;
    static Map<String, Actor> actors;
    static Map<String, Director> directors;

    public static void main(String[] args) {
        // Load data from CSV files into memory
        movies = CSVReader.loadMovies("movies_large.csv");
        actors = CSVReader.loadActors("actors_large.csv");
        directors = CSVReader.loadDirectors("directors_large.csv");

        Scanner scanner = new Scanner(System.in);
        // Menu-driven system for user interactions
        while (true) {
            System.out.println("\nMovie Management System:");
            System.out.println("1. Get Movie Information");
            System.out.println("2. Get Top 10 Rated Movies");
            System.out.println("3. Get Movies by Genre");
            System.out.println("4. Get Movies by Director");
            System.out.println("5. Get Movies by Release Year");
            System.out.println("6. Get Movies by Release Year Range");
            System.out.println("7. Add a New Movie");
            System.out.println("8. Update Movie Rating");
            System.out.println("9. Delete a Movie");
            System.out.println("10. Sort and Return 15 Movies by Release Year");
            System.out.println("11. Get Directors with the Most Movies");
            System.out.println("12. Get Actors Who Have Worked in Multiple Movies");
            System.out.println("13. Get Movies of the Youngest Actor (as of 2025-02-10)");
            System.out.println("14. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter Movie ID or Title: ");
                    getMovieInfo(scanner.nextLine());
                    break;
                case 2:
                    getTopRatedMovies();
                    break;
                case 3:
                    System.out.print("Enter Genre: ");
                    getMoviesByGenre(scanner.nextLine());
                    break;
                case 4:
                    System.out.print("Enter Director Name: ");
                    getMoviesByDirector(scanner.nextLine());
                    break;
                case 5:
                    System.out.print("Enter Release Year: ");
                    getMoviesByYear(scanner.nextInt());
                    break;
                case 6:
                    System.out.print("Enter Year Range (YYYY-YYYY): ");
                    String[] range = scanner.next().split("-");
                    getMoviesByYearRange(Integer.parseInt(range[0]), Integer.parseInt(range[1]));
                    break;
                case 7:
                    addNewMovie(scanner);
                    break;
                case 8:
                    System.out.print("Enter Movie ID and New Rating: ");
                    updateMovieRating(scanner.next(), scanner.nextDouble());
                    break;
                case 9:
                    System.out.print("Enter Movie ID to Delete: ");
                    deleteMovie(scanner.next());
                    break;
                case 10:
                    getSortedMoviesByYear();
                    break;
                case 11:
                    getTopDirectors();
                    break;
                case 12:
                    getTopActors();
                    break;
                case 13:
                    getMoviesOfYoungestActor();
                    break;
                case 14:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option! Try again.");
            }
        }
    }

//    Retrieves and displays movie details based on user input.
    public static void getMovieInfo(String input) {
        for (Movie movie : movies.values()) {
            if (movie.getMovieId().equals(input) || movie.getTitle().equalsIgnoreCase(input)) {
                String directorName = directors.get(movie.getDirectorId()).getName();
                List<String> actorNames = movie.getActorIds().stream()
                        .map(actorId -> actors.containsKey(actorId) ? actors.get(actorId).getName() : "Unknown")
                        .collect(Collectors.toList());

                System.out.println("\nMovie: " + movie.getTitle());
                System.out.println("Year: " + movie.getReleaseYear());
                System.out.println("Genre: " + movie.getGenre());
                System.out.println("Rating: " + movie.getRating());
                System.out.println("Director: " + directorName);
                System.out.println("Actors: " + String.join(", ", actorNames));
                return;
            }
        }
        System.out.println("Movie not found.");
    }

//    Displays the top 10 highest-rated movies.
    public static void getTopRatedMovies() {
        movies.values().stream()
                .sorted(Comparator.comparingDouble(Movie::getRating).reversed())
                .limit(10)
                .forEach(m -> System.out.println(m.getTitle() + " - " + m.getRating()));
    }
//Displays movies belonging to a specified genre.
    public static void getMoviesByGenre(String genre) {
        movies.values().stream()
                .filter(m -> m.getGenre().equalsIgnoreCase(genre))
                .forEach(m -> System.out.println(m.getTitle()));
    }
//Displays movies directed by a specified director.
    public static void getMoviesByDirector(String directorName) {
        String directorId = directors.entrySet().stream()
                .filter(entry -> entry.getValue().getName().equalsIgnoreCase(directorName))
                .map(Map.Entry::getKey)
                .findFirst().orElse(null);

        if (directorId != null) {
            movies.values().stream()
                    .filter(m -> m.getDirectorId().equals(directorId))
                    .forEach(m -> System.out.println(m.getTitle()));
        } else {
            System.out.println("Director not found.");
        }
    }
//Displays movies released in a specified year.
    public static void getMoviesByYear(int year) {
        movies.values().stream()
                .filter(m -> m.getReleaseYear() == year)
                .forEach(m -> System.out.println(m.getTitle()));
    }
//Displays movies released within a given year range.
    public static void getMoviesByYearRange(int start, int end) {
        movies.values().stream()
                .filter(m -> m.getReleaseYear() >= start && m.getReleaseYear() <= end)
                .forEach(m -> System.out.println(m.getTitle()));
    }
//Adds a new movie to the database.
    public static void addNewMovie(Scanner scanner) {
        System.out.print("Enter Movie ID, Title, Director ID, Year, Rating, Genre, Actor IDs (comma-separated): ");
        String movieId = scanner.next();
        String title = scanner.next();
        String directorId = scanner.next();
        int year = scanner.nextInt();
        double rating = scanner.nextDouble();
        String genre = scanner.next();
        List<String> actorIds = Arrays.asList(scanner.next().split(","));

        movies.put(movieId, new Movie(movieId, title, directorId, year, rating, genre, actorIds));
        System.out.println("Movie added successfully.");
    }
  //Updates the rating of an existing movie.
    public static void updateMovieRating(String movieId, double newRating) {
        if (movies.containsKey(movieId)) {
            movies.get(movieId).setRating(newRating);
            System.out.println("Rating updated.");
        } else {
            System.out.println("Movie not found.");
        }
    }
   //Deletes a movie from the database.
    public static void deleteMovie(String movieId) {
        if (movies.remove(movieId) != null) {
            System.out.println("Movie deleted.");
        } else {
            System.out.println("Movie not found.");
        }
    }
   //Sorts and displays 15 movies based on release year.
    public static void getSortedMoviesByYear() {
        movies.values().stream()
                .sorted(Comparator.comparingInt(Movie::getReleaseYear))
                .limit(15)
                .forEach(m -> System.out.println(m.getTitle() + " (" + m.getReleaseYear() + ")"));
    }
/// Retrieves and displays the top 5 directors who have directed the most movies.
    public static void getTopDirectors() {
        Map<String, Long> directorCounts = movies.values().stream()
                .collect(Collectors.groupingBy(Movie::getDirectorId, Collectors.counting()));

        directorCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> System.out.println(directors.get(entry.getKey()).getName() + " - " + entry.getValue() + " movies"));
    }
//Retrieves and displays the top 5 actors who have worked in the most movies.
    public static void getTopActors() {
        Map<String, Long> actorCounts = movies.values().stream()
                .flatMap(m -> m.getActorIds().stream())
                .collect(Collectors.groupingBy(a -> a, Collectors.counting()));

        actorCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> System.out.println(actors.get(entry.getKey()).getName() + " - " + entry.getValue() + " movies"));
    }
//Finds and displays movies in which the youngest actor (as of 2025-02-10) has worked.
    public static void getMoviesOfYoungestActor() {
//        Find the youngest actor based on birth year
        Actor youngest = Collections.min(actors.values(), Comparator.comparingInt(Actor::getBirthYear));
        // Display all movies in which the youngest actor has acted
        movies.values().stream()
                .filter(m -> m.getActorIds().contains(youngest.getActorId()))
                .forEach(m -> System.out.println(m.getTitle() + " - Age: " + (2025 - youngest.getBirthYear())));
    }
}
