package com.example.movieticket.repository;

import com.example.movieticket.domain.Show;
import java.util.*;

public class InMemoryShowRepository implements ShowRepository {
  private final Map<String, Show> showsById = new HashMap<>();

  @Override
  public void upsert(Show show) {
    if (show == null) {
      throw new IllegalArgumentException("show");
    }
    showsById.put(show.getShowId(), show);
  }

  @Override
  public Show findById(String showId) {
    if (showId == null || showId.isBlank()) {
      throw new IllegalArgumentException("showId");
    }
    return showsById.get(showId);
  }

  @Override
  public List<Show> findByMovieId(String movieId) {
    if (movieId == null || movieId.isBlank()) {
      throw new IllegalArgumentException("movieId");
    }
    List<Show> result = new ArrayList<>();
    for (Show show : showsById.values()) {
      if (movieId.equals(show.getMovieId())) {
        result.add(show);
      }
    }
    return result;
  }

  @Override
  public List<Show> findByTheaterId(String theaterId) {
    if (theaterId == null || theaterId.isBlank()) {
      throw new IllegalArgumentException("theaterId");
    }
    List<Show> result = new ArrayList<>();
    for (Show show : showsById.values()) {
      if (theaterId.equals(show.getTheaterId())) {
        result.add(show);
      }
    }
    return result;
  }

  @Override
  public Collection<Show> findAll() {
    return Collections.unmodifiableCollection(showsById.values());
  }
}

