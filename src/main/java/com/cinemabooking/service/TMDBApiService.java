package com.cinemabooking.service;

import com.cinemabooking.dao.MovieDAO;
import java.util.List;
import com.cinemabooking.model.Movie;
import com.cinemabooking.utils.ConfigLoader;
import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Date;


public class TMDBApiService {

    private static final String API_KEY = ConfigLoader.get("tmdb.api.key");
    private static final String BASE_URL = ConfigLoader.get("tmdb.base.url");
    private static final String IMAGE_BASE_URL = ConfigLoader.get("tmdb.image.base.url");

    private final MovieDAO movieDAO;
    private final HttpClient httpClient;
    private final Gson gson;

    public TMDBApiService() {
        this.movieDAO = new MovieDAO();
        this.httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
        this.gson = new Gson();
    }


    public void fetchAndSaveNowPlayingMovies() throws Exception {
        String url = BASE_URL + "?api_key=" + API_KEY + "&language=vi-VN&page=1";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            TMDBResponse tmdbResponse = gson.fromJson(response.body(), TMDBResponse.class);

            int countAdded = 0;

            for (TMDBMovie item : tmdbResponse.results) {
                if (!movieDAO.isMovieExist(item.id)) {
                    Movie movie = new Movie();
                    movie.setTmdbId(item.id);
                    movie.setTitle(item.title);
                    movie.setDescription(item.overview);

                    if (item.release_date != null && !item.release_date.isEmpty()) {
                        movie.setReleaseDate(Date.valueOf(item.release_date));
                    }

                    if (item.poster_path != null) {
                        movie.setPosterUrl(IMAGE_BASE_URL + item.poster_path);
                    }

                    movie.setRating(item.vote_average);

                    movie.setDuration(120);
                    movie.setGenre("Hành động / Viễn tưởng");

                    // Lưu vào DB
                    movieDAO.insertMovie(movie);
                    countAdded++;
                }
            }
            System.out.println("Đã kéo và cập nhật thành công " + countAdded + " phim mới từ TMDB!");
        } else {
            throw new Exception("Lỗi gọi API TMDB! HTTP Status: " + response.statusCode());
        }
    }

    private static class TMDBResponse {
        List<TMDBMovie> results;
    }

    private static class TMDBMovie {
        int id;
        String title;
        String overview;
        String release_date;
        String poster_path;
        double vote_average;
    }
}
