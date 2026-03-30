package com.example.movieticket.repository;

import com.example.movieticket.domain.Theater;
import java.util.*;

public interface TheaterRepository {
  void upsert(Theater theater);

  Theater findById(String theaterId);

  List<Theater> findByCity(String city);

  Collection<Theater> findAll();
}

