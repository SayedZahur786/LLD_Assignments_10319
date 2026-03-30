package com.example.movieticket.repository;

import com.example.movieticket.domain.Movie;
import java.util.*;

public interface MovieRepository {
  void upsert(Movie movie);

  Movie findById(String movieId);

  Collection<Movie> findAll();
}

