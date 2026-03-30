package com.example.movieticket.repository;

import com.example.movieticket.domain.Movie;
import java.util.*;

public class InMemoryMovieRepository implements MovieRepository {
  private final Map<String, Movie> moviesById = new HashMap<>();

  @Override
  public void upsert(Movie movie) {
    if (movie == null) {
      throw new IllegalArgumentException("movie");
    }
    moviesById.put(movie.getMovieId(), movie);
  }

  @Override
  public Movie findById(String movieId) {
    if (movieId == null || movieId.isBlank()) {
      throw new IllegalArgumentException("movieId");
    }
    return moviesById.get(movieId);
  }

  @Override
  public Collection<Movie> findAll() {
    return Collections.unmodifiableCollection(moviesById.values());
  }
}

