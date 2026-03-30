package com.example.movieticket.repository;

import com.example.movieticket.domain.Show;
import java.util.*;

public interface ShowRepository {
  void upsert(Show show);

  Show findById(String showId);

  List<Show> findByMovieId(String movieId);

  List<Show> findByTheaterId(String theaterId);

  Collection<Show> findAll();
}

